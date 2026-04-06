package com.ventalize.shop.board.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryRead {
    private Integer id;
    private Integer memberId;
    private String memberName;
    private String category;
    private String title;
    private String content;
    private String answerContent;
    private Boolean isAnswered;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
}
