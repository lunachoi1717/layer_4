package com.ventalize.shop.dto.member;

import com.ventalize.shop.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberRead {
    private Integer id;
    private String name;
    private String loginId;
    private String role;
    private String grade;
    private String status;
    private String phone;
    private String address;
    private LocalDateTime createdAt;

    public static MemberRead from(Member m) {
        return MemberRead.builder()
                .id(m.getId())
                .name(m.getName())
                .loginId(m.getLoginId())
                .role(m.getRole())
                .grade(m.getGrade())
                .status(m.getStatus())
                .phone(m.getPhone())
                .address(m.getAddress())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
