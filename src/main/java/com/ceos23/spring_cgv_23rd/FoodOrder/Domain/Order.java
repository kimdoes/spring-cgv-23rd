package com.ceos23.spring_cgv_23rd.FoodOrder.Domain;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    private Order(User user){
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private long price = 0;

    public void addPrice(long price){
        this.price += price;
    }

    public void addOrderItem(OrderItem oi){
        orderItems.add(oi);
        oi.setOrder(this);
    }

    public static Order create(User user){
        return new Order(user);
    }
}
