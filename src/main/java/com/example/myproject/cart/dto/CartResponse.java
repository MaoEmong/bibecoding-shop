package com.example.myproject.cart.dto;

import java.util.List;
import lombok.Data;

@Data
public class CartResponse {
    private List<CartItemResponse> items;
    private int totalPrice;
}
