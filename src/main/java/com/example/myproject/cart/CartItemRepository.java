package com.example.myproject.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("select ci from CartItem ci join fetch ci.product where ci.cart.id = :cartId")
    List<CartItem> findByCartIdWithProduct(@Param("cartId") Long cartId);

    @Query("select ci from CartItem ci join fetch ci.product where ci.id = :id")
    Optional<CartItem> findByIdWithProduct(@Param("id") Long id);

    @Query("select ci from CartItem ci join fetch ci.product where ci.cart.id = :cartId and ci.product.id = :productId")
    Optional<CartItem> findByCartIdAndProductIdWithProduct(
        @Param("cartId") Long cartId,
        @Param("productId") Long productId
    );

    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    void deleteByCartId(Long cartId);
}
