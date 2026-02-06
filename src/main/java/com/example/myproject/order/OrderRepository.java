package com.example.myproject.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);

    @Query("select o from Order o join fetch o.buyer")
    List<Order> findAllWithBuyer();

    @Query("select distinct o from Order o join fetch o.buyer join OrderItem oi on oi.order = o join oi.product p where p.seller.id = :sellerId")
    List<Order> findBySellerId(@Param("sellerId") Long sellerId);
}
