package com.ventalize.shop.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 백엔드 재시작(DevTools 포함) 시 이전 세션을 즉시 무효화하는 필터.
 *
 * 작동 원리:
 *  - JVM 기동 시 고유한 startupId(UUID)를 생성한다.
 *  - 요청이 들어올 때 세션 안의 startupId와 현재 값을 비교한다.
 *  - 불일치 → 세션 무효화 + SecurityContext 초기화 (강제 로그아웃).
 *  - 로그인 성공 후 새로 생성된 세션에는 현재 startupId를 기록한다.
 */
public class StartupSessionFilter extends OncePerRequestFilter {

    private static final String SESSION_STARTUP_KEY = "_startup_id";

    /** 이 JVM 기동 때마다 새로 생성되는 고유 식별자 */
    private final String startupId = UUID.randomUUID().toString();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1) 기존 세션 검증: startupId가 다르면 이전 기동의 세션 → 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            String savedId = (String) session.getAttribute(SESSION_STARTUP_KEY);
            if (!startupId.equals(savedId)) {
                session.invalidate();
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);

        // 2) 새로 생성된 세션에 현재 startupId 기록 (로그인 직후 세션 포함)
        HttpSession newSession = request.getSession(false);
        if (newSession != null && newSession.getAttribute(SESSION_STARTUP_KEY) == null) {
            newSession.setAttribute(SESSION_STARTUP_KEY, startupId);
        }
    }
}
