package com.example.myproject.product.dto;

import com.example.myproject.product.ProductStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private int price;
    private int stockQuantity;
    private ProductStatus status;
    private LocalDateTime createdAt;
}
