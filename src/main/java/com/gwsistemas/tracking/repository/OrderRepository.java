package com.gwsistemas.tracking.repository;

import com.gwsistemas.tracking.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTrackingCode(String trackingCode);
}
