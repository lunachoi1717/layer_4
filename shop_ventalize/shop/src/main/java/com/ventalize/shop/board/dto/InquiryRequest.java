package com.ventalize.shop.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryRequest {
    private String category;
    private String title;
    private String content;
    private String inquiryPw;
}
