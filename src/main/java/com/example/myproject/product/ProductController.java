package com.example.myproject.product;

import com.example.myproject.product.dto.ProductCreateRequest;
import com.example.myproject.product.dto.ProductResponse;
import com.example.myproject.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<ProductResponse> list() {
        return productService.list().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/products/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return toResponse(productService.get(id));
    }

    @PostMapping("/seller/products")
    public ProductResponse create(
        @Valid @RequestBody ProductCreateRequest request,
        HttpSession session
    ) {
        Long sellerId = SessionUtil.requireUserId(session);
        return toResponse(productService.create(sellerId, request));
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSellerId(product.getSeller().getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}
