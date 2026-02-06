package com.example.myproject.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductDetailPageController {

    private final ProductService productService;

    public ProductDetailPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.get(id);
        model.addAttribute("id", product.getId());
        model.addAttribute("name", product.getName());
        model.addAttribute("description", product.getDescription());
        model.addAttribute("price", String.format("%,d", product.getPrice()));
        model.addAttribute("stock", product.getStockQuantity());
        return "product-detail";
    }
}
