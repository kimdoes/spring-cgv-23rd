package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;

import java.util.List;

public record OrderResponseDTO(
        long orderId,
        long userId,
        List<OrderItemWrapperDTO> items,
        long price
) {
    public static OrderResponseDTO create(Order order){
        return new OrderResponseDTO(order.getId(),
                order.getUser().getId(),
                OrderItemWrapperDTO.create(order.getOrderItems()),
                order.getPrice());
    }
}