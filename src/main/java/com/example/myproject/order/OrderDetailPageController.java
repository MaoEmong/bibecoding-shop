package com.example.myproject.order;

import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderDetailPageController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    public OrderDetailPageController(OrderService orderService, OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long buyerId = SessionUtil.requireUserId(session);
        Order order = orderService.getBuyerOrder(buyerId, id);

        List<Map<String, Object>> items = orderItemRepository.findByOrderIdWithProduct(order.getId())
            .stream()
            .map(oi -> Map.<String, Object>of(
                "productId", oi.getProduct().getId(),
                "name", oi.getProduct().getName(),
                "price", String.format("%,d", oi.getUnitPrice()),
                "quantity", oi.getQuantity(),
                "subtotal", String.format("%,d", oi.getUnitPrice() * oi.getQuantity())
            ))
            .collect(Collectors.toList());

        int totalQuantity = items.stream()
            .mapToInt(i -> (int) i.get("quantity"))
            .sum();

        model.addAttribute("orderNo", "ORD-" + order.getId());
        model.addAttribute("status", order.getStatus().name());
        model.addAttribute("total", String.format("%,d", order.getTotalPrice()));
        model.addAttribute("createdAt", order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        model.addAttribute("itemCount", items.size());
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("items", items);
        return "order-detail";
    }
}
