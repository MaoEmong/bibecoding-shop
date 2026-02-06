package com.example.myproject.order.dto;

import com.example.myproject.order.OrderStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private int totalPrice;
    private LocalDateTime createdAt;
}
