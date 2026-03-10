package com.flprocha.ecommerce_api.repository;

import com.flprocha.ecommerce_api.entity.Order;
import com.flprocha.ecommerce_api.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerEmail(String customerEmail, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);
}