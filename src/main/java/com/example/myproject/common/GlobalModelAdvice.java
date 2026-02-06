package com.example.myproject.common;

import com.example.myproject.user.User;
import com.example.myproject.user.UserRepository;
import com.example.myproject.user.UserRole;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAdvice {

    private final UserRepository userRepository;

    public GlobalModelAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("loggedIn")
    public boolean loggedIn(HttpSession session) {
        return session != null && session.getAttribute(SessionUtil.SESSION_USER_ID) != null;
    }

    @ModelAttribute("buyer")
    public boolean isBuyer(HttpSession session) {
        return getRole(session) == UserRole.BUYER;
    }

    @ModelAttribute("seller")
    public boolean isSeller(HttpSession session) {
        return getRole(session) == UserRole.SELLER;
    }

    private UserRole getRole(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object id = session.getAttribute(SessionUtil.SESSION_USER_ID);
        if (id == null) {
            return null;
        }
        User user = userRepository.findById((Long) id).orElse(null);
        return user != null ? user.getRole() : null;
    }
}
