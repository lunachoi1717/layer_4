package com.ventalize.shop.dto.board;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FaqRead {
    private Integer id;
    private String category;
    private String question;
    private String answer;
    private Boolean isPublished;
    private LocalDateTime createdAt;
}
