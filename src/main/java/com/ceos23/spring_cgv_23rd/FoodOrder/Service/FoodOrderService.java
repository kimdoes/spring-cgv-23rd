package com.ceos23.spring_cgv_23rd.FoodOrder.Service;

import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodMenuAndQuantityDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodOrderRequestDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.OrderResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.Order;
import com.ceos23.spring_cgv_23rd.FoodOrder.Domain.OrderItem;
import com.ceos23.spring_cgv_23rd.FoodOrder.Repository.FoodOrderRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterMenuRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FoodOrderService {

    /**
     * TODO: 같은 영화관 강제하기
     */
    private final TheaterMenuRepository theaterMenuRepository;
    private final UserRepository userRepository;
    private final FoodOrderRepository foodOrderRepository;

    public FoodOrderService(TheaterMenuRepository theaterMenuRepository, UserRepository userRepository, FoodOrderRepository foodOrderRepository) {
        this.theaterMenuRepository = theaterMenuRepository;
        this.userRepository = userRepository;
        this.foodOrderRepository = foodOrderRepository;
    }

    @Transactional
    public OrderResponseDTO orderMenu(String loginId, FoodOrderRequestDTO foodOrderRequestDTO){

        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저가 없습니다.")
        );
        Order order = Order.create(user);

        for(FoodMenuAndQuantityDTO menuReqInfos : foodOrderRequestDTO.reqs()){
            orderMenu(order, menuReqInfos.menuId(), menuReqInfos.quantity());
        }

        return OrderResponseDTO.create(foodOrderRepository.save(order));
    }

    private OrderItem orderMenu(Order order, long menuId, int quantity){
        TheaterMenu menu = theaterMenuRepository.findById(menuId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "메뉴가 없습니다.")
        );

        OrderItem oi = OrderItem.create(order, menu, quantity);
        menu.buy(quantity);

        return oi;
    }


}
