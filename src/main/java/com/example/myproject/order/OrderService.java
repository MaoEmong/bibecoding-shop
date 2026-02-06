package com.example.myproject.order;

import com.example.myproject.order.dto.OrderCreateRequest;
import com.example.myproject.order.dto.OrderItemRequest;
import com.example.myproject.product.Product;
import com.example.myproject.product.ProductRepository;
import com.example.myproject.user.User;
import com.example.myproject.user.UserRepository;
import com.example.myproject.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        UserRepository userRepository,
        ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Long buyerId, OrderCreateRequest request) {
        User buyer = userRepository.findById(buyerId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (buyer.getRole() != UserRole.BUYER) {
            throw new IllegalArgumentException("구매자 계정만 주문할 수 있습니다.");
        }

        int total = 0;
        Order order = new Order(buyer, 0);
        order = orderRepository.save(order);

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new IllegalArgumentException("재고가 부족합니다.");
            }

            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            int line = product.getPrice() * itemReq.getQuantity();
            total += line;
            orderItemRepository.save(new OrderItem(order, product, itemReq.getQuantity(), product.getPrice()));
        }

        order.setTotalPrice(total);
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> listOrders(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    @Transactional(readOnly = true)
    public Order getBuyerOrder(Long buyerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        if (!order.getBuyer().getId().equals(buyerId)) {
            throw new IllegalArgumentException("본인 주문만 조회할 수 있습니다.");
        }
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> listSellerOrders(Long sellerId) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));
        if (seller.getRole() != UserRole.SELLER) {
            throw new IllegalArgumentException("판매자 계정만 조회할 수 있습니다.");
        }

        return orderRepository.findBySellerId(sellerId);
    }

    @Transactional
    public Order updateStatus(Long sellerId, Long orderId, OrderStatus newStatus) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));
        if (seller.getRole() != UserRole.SELLER) {
            throw new IllegalArgumentException("판매자 계정만 변경할 수 있습니다.");
        }

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.setStatus(newStatus);
        return order;
    }
}
