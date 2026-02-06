package com.example.myproject.order;

import com.example.myproject.cart.Cart;
import com.example.myproject.cart.CartItem;
import com.example.myproject.cart.CartService;
import com.example.myproject.common.SessionUtil;
import com.example.myproject.order.dto.OrderCreateRequest;
import com.example.myproject.order.dto.OrderItemRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderPageController {

    private final CartService cartService;
    private final OrderService orderService;

    public OrderPageController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/order")
    public String order(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long buyerId = SessionUtil.requireUserId(session);
        Cart cart = cartService.getOrCreateCart(buyerId);
        List<CartItem> items = cartService.listItems(cart.getId());

        List<Map<String, Object>> viewItems = items.stream()
            .map(i -> Map.<String, Object>of(
                "name", i.getProduct().getName(),
                "price", String.format("%,d", i.getProduct().getPrice()),
                "quantity", i.getQuantity(),
                "subtotal", String.format("%,d", i.getProduct().getPrice() * i.getQuantity())
            ))
            .collect(Collectors.toList());

        int total = items.stream().mapToInt(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
        model.addAttribute("items", viewItems);
        model.addAttribute("total", String.format("%,d", total));
        return "order";
    }

    @PostMapping("/order/complete")
    public String complete(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long buyerId = SessionUtil.requireUserId(session);
        Cart cart = cartService.getOrCreateCart(buyerId);
        List<CartItem> items = cartService.listItems(cart.getId());
        if (items.isEmpty()) {
            return "redirect:/cart";
        }

        OrderCreateRequest request = new OrderCreateRequest();
        List<OrderItemRequest> orderItems = items.stream()
            .map(i -> {
                OrderItemRequest item = new OrderItemRequest();
                item.setProductId(i.getProduct().getId());
                item.setQuantity(i.getQuantity());
                return item;
            })
            .collect(Collectors.toList());
        request.setItems(orderItems);

        Order order = orderService.createOrder(buyerId, request);
        cartService.clearCart(buyerId);
        session.setAttribute(SessionUtil.SESSION_LAST_ORDER_ID, order.getId());
        return "redirect:/order/complete";
    }
}
