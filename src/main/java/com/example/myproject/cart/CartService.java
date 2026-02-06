package com.example.myproject.cart;

import com.example.myproject.cart.dto.CartItemCreateRequest;
import com.example.myproject.cart.dto.CartItemUpdateRequest;
import com.example.myproject.product.Product;
import com.example.myproject.product.ProductRepository;
import com.example.myproject.user.User;
import com.example.myproject.user.UserRepository;
import com.example.myproject.user.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(
        CartRepository cartRepository,
        CartItemRepository cartItemRepository,
        UserRepository userRepository,
        ProductRepository productRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Cart getOrCreateCart(Long buyerId) {
        return cartRepository.findByBuyerId(buyerId)
            .orElseGet(() -> {
                User buyer = userRepository.findById(buyerId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                if (buyer.getRole() != UserRole.BUYER) {
                    throw new IllegalArgumentException("구매자 계정만 장바구니를 사용할 수 있습니다.");
                }
                return cartRepository.save(new Cart(buyer));
            });
    }

    @Transactional(readOnly = true)
    public List<CartItem> listItems(Long cartId) {
        return cartItemRepository.findByCartIdWithProduct(cartId);
    }

    @Transactional
    public CartItem addItem(Long buyerId, CartItemCreateRequest request) {
        Cart cart = getOrCreateCart(buyerId);
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return cartItemRepository.findByCartIdAndProductIdWithProduct(cart.getId(), product.getId())
            .map(existing -> {
                int nextQuantity = existing.getQuantity() + request.getQuantity();
                if (nextQuantity > product.getStockQuantity()) {
                    throw new IllegalArgumentException("재고 수량을 초과할 수 없습니다.");
                }
                existing.setQuantity(nextQuantity);
                return existing;
            })
            .orElseGet(() -> {
                if (request.getQuantity() > product.getStockQuantity()) {
                    throw new IllegalArgumentException("재고 수량을 초과할 수 없습니다.");
                }
                return cartItemRepository.save(new CartItem(cart, product, request.getQuantity()));
            });
    }

    @Transactional
    public CartItem updateQuantity(Long buyerId, Long itemId, CartItemUpdateRequest request) {
        Cart cart = getOrCreateCart(buyerId);
        CartItem item = cartItemRepository.findByIdWithProduct(itemId)
            .orElseThrow(() -> new IllegalArgumentException("장바구니 아이템을 찾을 수 없습니다."));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("장바구니 아이템이 일치하지 않습니다.");
        }
        if (request.getQuantity() > item.getProduct().getStockQuantity()) {
            throw new IllegalArgumentException("재고 수량을 초과할 수 없습니다.");
        }
        item.setQuantity(request.getQuantity());
        return item;
    }

    @Transactional
    public void removeItem(Long buyerId, Long itemId) {
        Cart cart = getOrCreateCart(buyerId);
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("장바구니 아이템을 찾을 수 없습니다."));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("장바구니 아이템이 일치하지 않습니다.");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long buyerId) {
        Cart cart = getOrCreateCart(buyerId);
        cartItemRepository.deleteByCartId(cart.getId());
    }
}
