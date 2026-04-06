package com.ventalize.shop.qna.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionCreateRequest {
    private Integer itemId;
    private String title;
    private String content;
    private Boolean isSecret;
}
