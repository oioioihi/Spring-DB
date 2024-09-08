package com.hello.jdbc.repository;

import com.hello.jdbc.service.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
