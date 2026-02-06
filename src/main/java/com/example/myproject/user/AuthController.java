package com.example.myproject.user;

import com.example.myproject.user.dto.LoginRequest;
import com.example.myproject.user.dto.LoginResponse;
import com.example.myproject.user.dto.SignUpRequest;
import com.example.myproject.user.dto.UserResponse;
import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserResponse signUp(@Valid @RequestBody SignUpRequest request) {
        User user = userService.register(
            request.getEmail(),
            request.getPassword(),
            request.getName(),
            request.getRole()
        );
        return toResponse(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = userService.authenticate(request.getEmail(), request.getPassword());
        session.setAttribute(SessionUtil.SESSION_USER_ID, user.getId());
        return new LoginResponse("ok", user.getRole());
    }

    @PostMapping("/logout")
    public LoginResponse logout(HttpSession session) {
        session.invalidate();
        return new LoginResponse("ok", null);
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
