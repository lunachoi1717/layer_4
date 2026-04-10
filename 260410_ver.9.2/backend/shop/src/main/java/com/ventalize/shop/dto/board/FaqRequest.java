package com.ventalize.shop.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FaqRequest {
    private String category;
    private String question;
    private String answer;
    private Boolean isPublished;
}
