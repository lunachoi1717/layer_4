package com.ventalize.shop.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewRead {
    private Integer id;
    private Integer memberId;
    private String memberName;
    private Integer itemId;
    private Integer orderId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
