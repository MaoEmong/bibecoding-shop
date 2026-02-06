package com.example.myproject;

import com.example.myproject.cart.CartItem;
import com.example.myproject.cart.CartService;
import com.example.myproject.cart.dto.CartItemCreateRequest;
import com.example.myproject.order.Order;
import com.example.myproject.order.OrderService;
import com.example.myproject.order.OrderStatus;
import com.example.myproject.order.dto.OrderCreateRequest;
import com.example.myproject.order.dto.OrderItemRequest;
import com.example.myproject.product.Product;
import com.example.myproject.product.ProductService;
import com.example.myproject.product.dto.ProductCreateRequest;
import com.example.myproject.user.User;
import com.example.myproject.user.UserRole;
import com.example.myproject.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApiIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Test
    void mvpFlowServices() {
        User seller = userService.register("seller@test.com", "password123", "판매자", UserRole.SELLER);
        User buyer = userService.register("buyer@test.com", "password123", "구매자", UserRole.BUYER);

        ProductCreateRequest productRequest = new ProductCreateRequest();
        productRequest.setName("테스트 상품");
        productRequest.setDescription("설명");
        productRequest.setPrice(1000);
        productRequest.setStockQuantity(5);

        Product product = productService.create(seller.getId(), productRequest);
        assertThat(product.getId()).isNotNull();

        CartItemCreateRequest cartRequest = new CartItemCreateRequest();
        cartRequest.setProductId(product.getId());
        cartRequest.setQuantity(2);

        CartItem cartItem = cartService.addItem(buyer.getId(), cartRequest);
        assertThat(cartItem.getQuantity()).isEqualTo(2);

        List<CartItem> cartItems = cartService.listItems(cartItem.getCart().getId());
        assertThat(cartItems).hasSize(1);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setQuantity(2);

        OrderCreateRequest orderRequest = new OrderCreateRequest();
        orderRequest.setItems(List.of(itemRequest));

        Order order = orderService.createOrder(buyer.getId(), orderRequest);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(order.getTotalPrice()).isEqualTo(2000);

        List<Order> buyerOrders = orderService.listOrders(buyer.getId());
        assertThat(buyerOrders).hasSize(1);

        Order updated = orderService.updateStatus(seller.getId(), order.getId(), OrderStatus.SHIPPING);
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }
}
