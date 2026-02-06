package com.example.myproject.order;

import com.example.myproject.common.SessionUtil;
import com.example.myproject.order.dto.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public OrderResponse create(@Valid @RequestBody OrderCreateRequest request, HttpSession session) {
        Long buyerId = SessionUtil.requireUserId(session);
        return toResponse(orderService.createOrder(buyerId, request));
    }

    @GetMapping("/orders")
    public List<OrderResponse> list(HttpSession session) {
        Long buyerId = SessionUtil.requireUserId(session);
        return orderService.listOrders(buyerId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/seller/orders")
    public List<OrderResponse> sellerList(HttpSession session) {
        Long sellerId = SessionUtil.requireUserId(session);
        return orderService.listSellerOrders(sellerId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @PatchMapping("/seller/orders/{id}/status")
    public OrderResponse updateStatus(
        @PathVariable Long id,
        @RequestBody OrderStatusUpdateRequest request,
        HttpSession session
    ) {
        Long sellerId = SessionUtil.requireUserId(session);
        OrderStatus status = OrderStatus.valueOf(request.getStatus());
        return toResponse(orderService.updateStatus(sellerId, id, status));
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalPrice(order.getTotalPrice());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}
