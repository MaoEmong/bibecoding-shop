package com.example.myproject.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProductPageController {

    private final ProductService productService;

    public ProductPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String products(Model model) {
        List<Map<String, Object>> products = productService.list().stream()
            .map(p -> Map.<String, Object>of(
                "id", p.getId(),
                "name", p.getName(),
                "price", String.format("%,d", p.getPrice()),
                "stock", p.getStockQuantity()
            ))
            .collect(Collectors.toList());

        model.addAttribute("products", products);
        return "products";
    }
}
