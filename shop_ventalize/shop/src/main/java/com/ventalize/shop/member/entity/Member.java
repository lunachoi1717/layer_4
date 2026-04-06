package com.ventalize.shop.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "members")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String loginId;

    @Column(nullable = false, length = 200)
    private String loginPw;

    /** ROLE_USER / ROLE_ADMIN */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "ROLE_USER";

    /** SAPPHIRE / RUBY / EMERALD / GOLD / DIAMOND */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String grade = "SAPPHIRE";

    /** ACTIVE / SUSPENDED */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
