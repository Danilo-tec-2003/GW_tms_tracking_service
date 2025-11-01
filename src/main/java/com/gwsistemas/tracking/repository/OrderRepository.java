package com.gwsistemas.tracking.repository;

import com.gwsistemas.tracking.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * @param trackingCode O código de rastreio a ser buscado.
     * @return um Optional contendo a Order (se encontrada) ou vazio (se não).
     */
    Optional<Order> findByTrackingCode(String trackingCode);
}
