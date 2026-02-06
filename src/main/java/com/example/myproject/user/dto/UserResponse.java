package com.example.myproject.user.dto;

import com.example.myproject.user.UserRole;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private UserRole role;
    private LocalDateTime createdAt;
}
