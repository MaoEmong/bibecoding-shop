package com.example.myproject.product;

import com.example.myproject.common.SessionUtil;
import com.example.myproject.product.dto.ProductCreateRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductManagePageController {

    private final ProductService productService;

    public ProductManagePageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/seller/products/new")
    public String newProduct(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new ProductCreateRequest());
        }
        return "product-create";
    }

    @PostMapping("/seller/products/new")
    public String createProduct(
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
            return "product-create";
        }
        try {
            Long sellerId = SessionUtil.requireUserId(session);
            productService.create(sellerId, request);
            return "redirect:/seller/products";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "product-create";
        }
    }

    @PostMapping("/seller/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }
        try {
            Long sellerId = SessionUtil.requireUserId(session);
            productService.delete(sellerId, id);
            return "redirect:/seller/products";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/seller/products";
        }
    }
}
