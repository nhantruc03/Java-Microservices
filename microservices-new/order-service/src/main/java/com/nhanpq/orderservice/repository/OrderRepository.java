package com.nhanpq.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhanpq.orderservice.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    
}
