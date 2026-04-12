package com.ventalize.shop.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class StartupSessionFilter extends OncePerRequestFilter {

    private static final String SESSION_STARTUP_KEY = "_startup_id";

    private final String startupId = UUID.randomUUID().toString();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String savedId = (String) session.getAttribute(SESSION_STARTUP_KEY);
            if (!startupId.equals(savedId)) {
                session.invalidate();
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);

        HttpSession newSession = request.getSession(false);
        if (newSession != null && newSession.getAttribute(SESSION_STARTUP_KEY) == null) {
            newSession.setAttribute(SESSION_STARTUP_KEY, startupId);
        }
    }
}
