package com.example.myproject.cart;

import com.example.myproject.common.SessionUtil;
import com.example.myproject.cart.dto.CartItemCreateRequest;
import com.example.myproject.cart.dto.CartItemUpdateRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CartPageController {

    private final CartService cartService;

    public CartPageController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long buyerId = SessionUtil.requireUserId(session);
        Cart cart = cartService.getOrCreateCart(buyerId);
        List<CartItem> items = cartService.listItems(cart.getId());

        List<Map<String, Object>> viewItems = items.stream()
            .map(i -> Map.<String, Object>of(
                "id", i.getId(),
                "name", i.getProduct().getName(),
                "price", String.format("%,d", i.getProduct().getPrice()),
                "stock", i.getProduct().getStockQuantity(),
                "quantity", i.getQuantity(),
                "subtotal", String.format("%,d", i.getProduct().getPrice() * i.getQuantity())
            ))
            .collect(Collectors.toList());

        int total = items.stream().mapToInt(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
        model.addAttribute("items", viewItems);
        model.addAttribute("total", String.format("%,d", total));
        return "cart";
    }

    @PostMapping("/cart/items")
    public String addItem(CartItemCreateRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }
        try {
            Long buyerId = SessionUtil.requireUserId(session);
            cartService.addItem(buyerId, request);
            return "redirect:/cart";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("alert", ex.getMessage());
            return "redirect:/products/" + request.getProductId();
        }
    }

    @PostMapping("/cart/items/{id}/quantity")
    public String updateQuantity(
        @PathVariable Long id,
        CartItemUpdateRequest request,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }
        try {
            Long buyerId = SessionUtil.requireUserId(session);
            cartService.updateQuantity(buyerId, id, request);
            return "redirect:/cart";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("alert", ex.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/cart/items/{id}/delete")
    public String deleteItem(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }
        try {
            Long buyerId = SessionUtil.requireUserId(session);
            cartService.removeItem(buyerId, id);
            return "redirect:/cart";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("alert", ex.getMessage());
            return "redirect:/cart";
        }
    }
}
