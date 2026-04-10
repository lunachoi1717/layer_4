package com.ventalize.shop.dto.qna;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuestionRead {
    private Integer id;
    private Integer memberId;
    private String memberName;
    private Integer itemId;
    private String itemName;
    private String title;
    private String content;
    private Boolean isSecret;
    private Boolean isAnswered;
    private String answerContent;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
}
