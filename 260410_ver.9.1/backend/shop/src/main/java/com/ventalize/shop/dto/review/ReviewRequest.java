package com.ventalize.shop.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private Integer itemId;
    private Integer orderId;
    private Integer rating;
    private String content;
}
