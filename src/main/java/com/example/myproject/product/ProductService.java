package com.example.myproject.product;

import com.example.myproject.product.dto.ProductCreateRequest;
import com.example.myproject.order.OrderItemRepository;
import com.example.myproject.user.User;
import com.example.myproject.user.UserRepository;
import com.example.myproject.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(
        ProductRepository productRepository,
        UserRepository userRepository,
        OrderItemRepository orderItemRepository
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Product create(Long sellerId, ProductCreateRequest request) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        if (seller.getRole() != UserRole.SELLER) {
            throw new IllegalArgumentException("판매자 계정만 상품을 등록할 수 있습니다.");
        }

        Product product = new Product(
            seller,
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getStockQuantity()
        );

        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long sellerId, Long productId, ProductCreateRequest request) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));
        if (seller.getRole() != UserRole.SELLER) {
            throw new IllegalArgumentException("판매자 계정만 상품을 수정할 수 있습니다.");
        }

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("본인 상품만 수정할 수 있습니다.");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        return product;
    }

    @Transactional
    public void delete(Long sellerId, Long productId) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));
        if (seller.getRole() != UserRole.SELLER) {
            throw new IllegalArgumentException("판매자 계정만 상품을 삭제할 수 있습니다.");
        }

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("본인 상품만 삭제할 수 있습니다.");
        }

        if (orderItemRepository.existsByProductId(productId)) {
            throw new IllegalArgumentException("주문 내역이 있는 상품은 삭제할 수 없습니다.");
        }

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<Product> list() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> listBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public Product get(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }
}
