package com.ceos23.spring_cgv_23rd.FoodOrder.Controller;

import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.FoodOrderRequestDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.DTO.OrderResponseDTO;
import com.ceos23.spring_cgv_23rd.FoodOrder.Service.FoodOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class FoodOrderController {

    private final FoodOrderService foodOrderService;

    public FoodOrderController(FoodOrderService foodOrderService) {
        this.foodOrderService = foodOrderService;
    }

    /**
     * 예약하기
     *
     * @param forDTO 예약하려는거 정보
     * @return 성공적인 예약을 축하하는 메시지..
     */
    @PostMapping(params = "userId")
    public ResponseEntity<OrderResponseDTO> orderController(
            @AuthenticationPrincipal User user,
            @RequestBody FoodOrderRequestDTO forDTO
            ){
        return ResponseEntity.ok(foodOrderService.orderMenu(user.getUsername(), forDTO));
    }
}

//@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @Setter
//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private Order order;
//
//    @ManyToOne
//    @JoinColumn(name = "menu_id")
//    private TheaterMenu menu;
//
//    private int quantity;
