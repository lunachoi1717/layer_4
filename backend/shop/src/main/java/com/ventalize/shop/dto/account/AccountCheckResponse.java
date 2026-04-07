package com.ventalize.shop.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountCheckResponse {
    private boolean loggedIn;
    private String loginId;
    private String name;
    private String role;
    private String grade;
}
