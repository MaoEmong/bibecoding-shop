package com.example.myproject.common;

import com.example.myproject.user.User;
import com.example.myproject.user.UserRepository;
import com.example.myproject.user.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.web.servlet.HandlerInterceptor;

public class RoleInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final UserRole requiredRole;
    private final boolean api;

    public RoleInterceptor(UserRepository userRepository, UserRole requiredRole, boolean api) {
        this.userRepository = userRepository;
        this.requiredRole = requiredRole;
        this.api = api;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return handleUnauthorized(response);
        }

        Long userId = (Long) session.getAttribute(SessionUtil.SESSION_USER_ID);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() != requiredRole) {
            return handleForbidden(response);
        }

        return true;
    }

    private boolean handleUnauthorized(HttpServletResponse response) throws IOException {
        if (api) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다.");
            return false;
        }
        response.sendRedirect("/login");
        return false;
    }

    private boolean handleForbidden(HttpServletResponse response) throws IOException {
        if (api) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "권한이 없습니다.");
            return false;
        }
        response.sendRedirect("/");
        return false;
    }

    private void writeJson(HttpServletResponse response, int status, String code, String message)
        throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String body = String.format(
            "{\"success\":false,\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
            code,
            message
        );
        response.getWriter().write(body);
    }
}
