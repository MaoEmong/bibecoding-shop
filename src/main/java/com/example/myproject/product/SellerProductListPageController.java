package com.example.myproject.product;

import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SellerProductListPageController {

    private final ProductService productService;

    public SellerProductListPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/seller/products")
    public String sellerProducts(Model model, HttpSession session) {
        if (session == null || session.getAttribute(SessionUtil.SESSION_USER_ID) == null) {
            return "redirect:/login";
        }

        Long sellerId = SessionUtil.requireUserId(session);
        List<Product> products = productService.listBySeller(sellerId);

        List<Map<String, Object>> viewProducts = products.stream()
            .map(p -> Map.<String, Object>of(
                "id", p.getId(),
                "name", p.getName(),
                "price", String.format("%,d", p.getPrice()),
                "stock", p.getStockQuantity(),
                "status", p.getStatus().name()
            ))
            .collect(Collectors.toList());

        model.addAttribute("products", viewProducts);
        return "seller-products";
    }
}