package com.example.myproject.order;

import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderCompletePageController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    public OrderCompletePageController(OrderService orderService, OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/order/complete")
    public String complete(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long buyerId = SessionUtil.requireUserId(session);
        Long orderId = (Long) session.getAttribute(SessionUtil.SESSION_LAST_ORDER_ID);
        if (orderId == null) {
            return "redirect:/orders";
        }

        Order order = orderService.getBuyerOrder(buyerId, orderId);
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
        model.addAttribute("total", String.format("%,d", order.getTotalPrice()));
        model.addAttribute("items", items);
        model.addAttribute("itemCount", items.size());
        model.addAttribute("totalQuantity", totalQuantity);
        return "order-complete";
    }
}
