package com.example.myproject.order;

import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SellerOrderPageController {

    private final OrderService orderService;

    public SellerOrderPageController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/seller/orders")
    public String sellerOrders(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long sellerId = SessionUtil.requireUserId(session);
        List<Order> orders = orderService.listSellerOrders(sellerId);

        List<Map<String, Object>> viewOrders = orders.stream()
            .map(o -> Map.<String, Object>of(
                "id", o.getId(),
                "orderNo", "ORD-" + o.getId(),
                "buyer", o.getBuyer().getEmail(),
                "status", o.getStatus().name(),
                "total", String.format("%,d", o.getTotalPrice())
            ))
            .collect(Collectors.toList());

        model.addAttribute("orders", viewOrders);
        return "seller-orders";
    }

    @PostMapping("/seller/orders/{id}/status")
    public String updateStatus(
        @PathVariable Long id,
        @RequestParam String status,
        HttpSession session
    ) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long sellerId = SessionUtil.requireUserId(session);
        OrderStatus newStatus = OrderStatus.valueOf(status);
        orderService.updateStatus(sellerId, id, newStatus);
        return "redirect:/seller/orders";
    }
}
