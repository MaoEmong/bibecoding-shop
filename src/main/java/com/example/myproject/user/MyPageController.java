package com.example.myproject.user;

import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class MyPageController {

    private final UserRepository userRepository;

    public MyPageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/mypage")
    public String mypage(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long userId = SessionUtil.requireUserId(session);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean seller = user.getRole() == UserRole.SELLER;
        model.addAttribute("userName", user.getName());
        model.addAttribute("role", user.getRole().name());

        if (seller) {
            model.addAttribute("menus", List.of(
                Map.of("label", "상품 관리", "href", "/seller/products"),
                Map.of("label", "상품 등록", "href", "/seller/products/new"),
                Map.of("label", "주문 관리", "href", "/seller/orders")
            ));
        } else {
            model.addAttribute("menus", List.of(
                Map.of("label", "주문 내역", "href", "/orders"),
                Map.of("label", "장바구니", "href", "/cart")
            ));
        }
        return "mypage";
    }
}