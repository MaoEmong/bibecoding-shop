package com.example.myproject.order.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class OrderCreateRequest {
    @NotEmpty
    private List<OrderItemRequest> items;
}
