package com.ventalize.shop.qna.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "questions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer memberId;

    private Integer itemId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isSecret = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAnswered = false;

    @Column(columnDefinition = "TEXT")
    private String answerContent;

    private Integer answerMemberId;

    private LocalDateTime answeredAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
