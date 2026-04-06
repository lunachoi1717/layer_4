package com.ventalize.shop.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequest {
    private String name;
    private String phone;
    private String address;
    private String currentPw;
    private String newPw;
}
