package com.zerobase.cafebom.orders.service.dto;

import com.zerobase.cafebom.orders.domain.entity.Orders;
import com.zerobase.cafebom.orders.domain.type.OrdersStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class OrdersHisDto {

    public OrdersHisDto(Orders order)
    {
        this.orderId = order.getId();

        this.orderDate = order.getCreatedDate();

        this.ordersStatus = order.getStatus();
    }

    private Long orderId;

    private LocalDateTime orderDate;

    private OrdersStatus ordersStatus;

    private List<OrdersProductDto> orderedProductsList = new ArrayList<>();

    public void addOrderProductDto(OrdersProductDto ordersProductDto)
    {

        orderedProductsList.add(ordersProductDto);
    }


}
