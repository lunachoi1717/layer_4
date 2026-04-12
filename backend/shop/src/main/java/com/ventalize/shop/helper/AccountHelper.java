package com.ventalize.shop.helper;

import com.ventalize.shop.dto.account.AccountJoinRequest;
import com.ventalize.shop.dto.account.AccountLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AccountHelper {

    void join(AccountJoinRequest joinReq);

    String login(AccountLoginRequest loginReq, HttpServletRequest req, HttpServletResponse res);

    Integer getMemberId(HttpServletRequest req);

    boolean isLoggedId(HttpServletRequest req);

    void logout(HttpServletRequest req, HttpServletResponse res);
}
