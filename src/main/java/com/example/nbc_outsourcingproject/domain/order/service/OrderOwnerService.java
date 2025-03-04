package com.example.nbc_outsourcingproject.domain.order.service;

import com.example.nbc_outsourcingproject.domain.common.dto.AuthUser;
import com.example.nbc_outsourcingproject.domain.menu.service.MenuOwnerService;
import com.example.nbc_outsourcingproject.domain.order.dto.OrderResponse;
import com.example.nbc_outsourcingproject.domain.order.dto.OrderSaveRequest;
import com.example.nbc_outsourcingproject.domain.order.dto.OrderSaveResponse;
import com.example.nbc_outsourcingproject.domain.order.entity.Order;
import com.example.nbc_outsourcingproject.domain.order.entity.OrderMenu;
import com.example.nbc_outsourcingproject.domain.order.enums.OrderStatus;
import com.example.nbc_outsourcingproject.domain.order.repository.OrderMenuRepository;
import com.example.nbc_outsourcingproject.domain.order.repository.OrderRepository;
import com.example.nbc_outsourcingproject.domain.store.entity.Store;
import com.example.nbc_outsourcingproject.domain.store.repository.StoreRepository;
import com.example.nbc_outsourcingproject.domain.store.service.StoreService;
import com.example.nbc_outsourcingproject.domain.user.entity.User;
import com.example.nbc_outsourcingproject.domain.user.repository.UserRepository;
import com.example.nbc_outsourcingproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderOwnerService {

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuOwnerService menuOwnerService;

    @Transactional
    public OrderSaveResponse createOrder(AuthUser authUser, Long storeId, List<OrderSaveRequest> orders) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(
                () -> new IllegalStateException("User가 없습니다.")
        );

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalStateException("store가 없습니다.")
        );

        if (orders == null) {
            throw new IllegalStateException("주문 내역이 없습니다.");
        }

        for (OrderSaveRequest o : orders) {
            Long menuId = o.getMenuId();
            int quantity = o.getQuantity();

            Menu menu = menuService.getMenu(menuId);
            OrderMenu orderMenu = new OrderMenu(savedOrder, menu.getName(),menu.getPrice(),quantity);
            totalAmount += menu.getPrice() * quantity;
            orderMenuRepository.save(orderMenu);
        }

        //최소금액
        if (totalAmount < store.getMinOrderAmount();){
            savedOrder.update(totalAmount,OrderStatus.CANCELED);
            throw new IllegalStateException("최소주문금액보다 작습니다.");
        }

        int hour = savedOrder.getCreatedAt().getHour();
        int minute = savedOrder.getCreatedAt().getMinute();
        LocalTime localTime = savedOrder.getCreatedAt().toLocalTime();
        System.out.println("localTime = " + localTime);

//        if (hour < 10 || hour > 19){
//            savedOrder.update(totalAmount,OrderStatus.CANCELED);
//            orderRepository.save(order);
//            throw new IllegalStateException("영업시간이 아닙니다.");
//        }


        Order order = new Order();
        Order savedOrder = orderRepository.save(order);
        int totalAmount = 0;



//        for (Map.Entry<Long, Integer> entry : dto.getOrders().entrySet()) {
//            Long menuId = entry.getKey();
//            Integer quantity = entry.getValue();
//
//            Menu menu = menuService.getMenu(menuId);
//            OrderMenu orderMenu = new OrderMenu(savedOrder, menu.getName(),menu.getPrice(),quantity);
//            totalAmount += menu.getPrice() * quantity;
//            orderMenuRepository.save(orderMenu);
//        }



        savedOrder.update(totalAmount,OrderStatus.PENDING);
        orderRepository.save(order);
        return new OrderSaveResponse(order.getId());
    }

    public Page<OrderResponse> getOrders(int page, int size) {

        // 클라이언트에서 1부터 전달된 페이지 번호를 0 기반으로 조정
        int adjustedPage = (page > 0) ? page - 1 : 0;
        // pageable 객체 생성, 수정일 기준 내림차순 정렬
        PageRequest pageable = PageRequest.of(adjustedPage, size, Sort.by("createdAt").descending());
        // Page 조회
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(
                order -> new OrderResponse(
                        order.getId()
                )
        );
    }

//    @Transactional
//    public OrderSaveResponse updateOrderAccept(OrderAcceptRequest dto, Long orderId) {
//        Order order = orderRepository.findById(orderId).orElseThrow(
//                () -> new IllegalStateException("주문 없음")
//        );
//
//        order.updateStatus();
//    }
}
