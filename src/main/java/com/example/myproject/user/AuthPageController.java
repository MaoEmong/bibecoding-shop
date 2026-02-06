package com.example.myproject.user;

import com.example.myproject.common.SessionUtil;
import com.example.myproject.user.dto.LoginRequest;
import com.example.myproject.user.dto.SignUpRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthPageController {

    private final UserService userService;

    public AuthPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
        @Valid @org.springframework.web.bind.annotation.ModelAttribute("loginRequest") LoginRequest request,
        BindingResult binding,
        HttpSession session,
        Model model
    ) {
        if (binding.hasErrors()) {
            model.addAttribute("error", "입력값을 확인해주세요.");
            model.addAttribute("errors", binding.getAllErrors());
            return "login";
        }
        try {
            User user = userService.authenticate(request.getEmail(), request.getPassword());
            session.setAttribute(SessionUtil.SESSION_USER_ID, user.getId());
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("errors", binding.getAllErrors());
            return "login";
        }
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        if (!model.containsAttribute("signUpRequest")) {
            model.addAttribute("signUpRequest", new SignUpRequest());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
        @Valid @org.springframework.web.bind.annotation.ModelAttribute("signUpRequest") SignUpRequest request,
        BindingResult binding,
        HttpSession session,
        Model model
    ) {
        if (binding.hasErrors()) {
            model.addAttribute("error", "입력값을 확인해주세요.");
            model.addAttribute("errors", binding.getAllErrors());
            return "signup";
        }
        try {
            User user = userService.register(
                request.getEmail(), request.getPassword(), request.getName(), request.getRole()
            );
            session.setAttribute(SessionUtil.SESSION_USER_ID, user.getId());
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("errors", binding.getAllErrors());
            return "signup";
        }
    }
}