package com.ventalize.shop.order.dto;

import com.ventalize.shop.item.dto.ItemRead;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderRead {
    private Integer id;
    private Integer memberId;
    private String memberName;
    private String name;
    private String address;
    private String payment;
    private Long amount;
    private String status;
    private String statusLabel;
    private LocalDateTime createdAt;
    private List<ItemRead> items;
}
