package com.example.myproject.order;

import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderHistoryPageController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    public OrderHistoryPageController(OrderService orderService, OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/orders")
    public String orders(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long buyerId = SessionUtil.requireUserId(session);
        List<Order> orders = orderService.listOrders(buyerId);

        List<Map<String, Object>> viewOrders = orders.stream()
            .map(o -> {
                List<Map<String, Object>> items = orderItemRepository.findByOrderIdWithProduct(o.getId())
                    .stream()
                    .map(oi -> Map.<String, Object>of(
                        "productId", oi.getProduct().getId(),
                        "name", oi.getProduct().getName(),
                        "quantity", oi.getQuantity()
                    ))
                    .collect(Collectors.toList());
                return Map.<String, Object>of(
                    "id", o.getId(),
                    "orderNo", "ORD-" + o.getId(),
                    "status", o.getStatus().name(),
                    "total", String.format("%,d", o.getTotalPrice()),
                    "items", items
                );
            })
            .collect(Collectors.toList());

        model.addAttribute("orders", viewOrders);
        return "order-history";
    }
}
