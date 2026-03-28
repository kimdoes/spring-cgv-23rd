package com.ceos23.spring_cgv_23rd.FoodOrder.Domain;

import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    private OrderItem(TheaterMenu menu, int quantity){
        this.menu = menu;
        this.quantity = quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private TheaterMenu menu;

    private int quantity;

    public long getPrice(){
        return (long) menu.getPrice() * quantity;
    }

    public void addOrder(Order or){
        this.order = or;
        or.getOrderItems().add(this);
    }

    public static OrderItem create(Order order, TheaterMenu menu, int quantity){
        OrderItem oi = new OrderItem(
                menu, quantity
        );

        oi.addOrder(order);
        order.addPrice(oi.getPrice());

        return oi;
    }
}
