package com.ceos23.spring_cgv_23rd.FoodOrder.Repository;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodOrderRepository extends JpaRepository<Order, Long> {
}
