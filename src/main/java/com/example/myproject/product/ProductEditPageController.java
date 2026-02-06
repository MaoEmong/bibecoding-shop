package com.example.myproject.product;

import com.example.myproject.common.SessionUtil;
import com.example.myproject.product.dto.ProductCreateRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;

@Controller
public class ProductEditPageController {

    private final ProductService productService;

    public ProductEditPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/seller/products/{id}/edit")
    public String edit(@PathVariable Long id, Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }
        SessionUtil.requireUserId(session);
        Product product = productService.get(id);
        model.addAttribute("id", product.getId());
        model.addAttribute("name", product.getName());
        model.addAttribute("price", product.getPrice());
        model.addAttribute("stock", product.getStockQuantity());
        model.addAttribute("description", product.getDescription());
        return "product-edit";
    }

    @PostMapping("/seller/products/{id}/edit")
    public String update(
        @PathVariable Long id,
        @Valid ProductCreateRequest request,
        BindingResult binding,
        HttpSession session,
        Model model
    ) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }
        if (binding.hasErrors()) {
            model.addAttribute("error", "입력값을 확인해주세요.");
            return "product-edit";
        }
        try {
            Long sellerId = SessionUtil.requireUserId(session);
            productService.update(sellerId, id, request);
            return "redirect:/seller/products";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "product-edit";
        }
    }
}
