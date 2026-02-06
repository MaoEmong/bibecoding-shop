package com.example.myproject.cart.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String name;
    private int price;
    private int quantity;
}
