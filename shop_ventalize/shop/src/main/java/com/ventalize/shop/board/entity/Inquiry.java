package com.ventalize.shop.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 1:1 문의 (관리자 답변 포함)
 */
@Entity
@Getter
@Setter
@Table(name = "inquiries")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer memberId;

    /** 카테고리: 주문/배송, 환불/교환, 회원, 상품, 기타 */
    @Column(nullable = false, length = 30)
    @Builder.Default
    private String category = "기타";

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String inquiryPw;

    @Column(columnDefinition = "TEXT")
    private String answerContent;

    private Integer answerMemberId;

    private LocalDateTime answeredAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAnswered = false;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
