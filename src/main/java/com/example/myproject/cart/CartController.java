package com.example.myproject.cart;

import com.example.myproject.cart.dto.*;
import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(HttpSession session) {
        Long buyerId = SessionUtil.requireUserId(session);
        Cart cart = cartService.getOrCreateCart(buyerId);
        List<CartItemResponse> items = cartService.listItems(cart.getId())
            .stream()
            .map(this::toItemResponse)
            .collect(Collectors.toList());

        int total = items.stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();
        CartResponse response = new CartResponse();
        response.setItems(items);
        response.setTotalPrice(total);
        return response;
    }

    @PostMapping("/items")
    public CartItemResponse addItem(@Valid @RequestBody CartItemCreateRequest request, HttpSession session) {
        Long buyerId = SessionUtil.requireUserId(session);
        return toItemResponse(cartService.addItem(buyerId, request));
    }

    @PatchMapping("/items/{id}")
    public CartItemResponse updateItem(
        @PathVariable Long id,
        @Valid @RequestBody CartItemUpdateRequest request,
        HttpSession session
    ) {
        Long buyerId = SessionUtil.requireUserId(session);
        return toItemResponse(cartService.updateQuantity(buyerId, id, request));
    }

    @DeleteMapping("/items/{id}")
    public void removeItem(@PathVariable Long id, HttpSession session) {
        Long buyerId = SessionUtil.requireUserId(session);
        cartService.removeItem(buyerId, id);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setName(item.getProduct().getName());
        response.setPrice(item.getProduct().getPrice());
        response.setQuantity(item.getQuantity());
        return response;
    }
}
