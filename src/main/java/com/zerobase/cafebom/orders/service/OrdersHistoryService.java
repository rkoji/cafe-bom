package com.zerobase.cafebom.orders.service;


import com.zerobase.cafebom.exception.CustomException;
import com.zerobase.cafebom.exception.ErrorCode;
import com.zerobase.cafebom.member.domain.entity.Member;
import com.zerobase.cafebom.member.repository.MemberRepository;
import com.zerobase.cafebom.orders.domain.entity.Orders;
import com.zerobase.cafebom.orders.repository.OrdersRepository;
import com.zerobase.cafebom.orders.service.dto.OrdersHisDto;
import com.zerobase.cafebom.orders.service.dto.OrdersProductDto;
import com.zerobase.cafebom.ordersproduct.domain.entity.OrdersProduct;
import com.zerobase.cafebom.ordersproduct.repository.OrdersProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.zerobase.cafebom.exception.ErrorCode.MEMBER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdersHistoryService {

    private final OrdersRepository ordersRepository;

    private final OrdersProductRepository ordersProductRepository;

    private final MemberRepository memberRepository;

    //youngseon 23.08.28
    public List<OrdersHisDto> findOrderHistoryFor3Months(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_EXISTS));

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Orders> orders = ordersRepository.findByMemberAndCreatedDateAfter(member, threeMonthsAgo);
        return from(orders);
    }

    // youngseon 23.08.28
    public List<OrdersHisDto> findAllOrderHistory(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_EXISTS));

        List<Orders> orders = ordersRepository.findByMember(member); // findByMember 에서 MemberId로 변경
        return from(orders);
    }

    // youngseon 23.08.28
    public List<OrdersHisDto> findOrderHistoryByPeriod(Long memberId, LocalDate startDate, LocalDate endDate) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_EXISTS));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<Orders> orders = ordersRepository.findByMemberAndCreatedDateBetween(member, startDateTime, endDateTime);
        return from(orders);
    }

    //youngseon 23.08.28
    public List<OrdersHisDto> from(List<Orders> orders) {

        List<OrdersHisDto> ordersHisDtoList = new ArrayList<>();

        for (Orders order : orders) {

            OrdersHisDto ordersHisDto = new OrdersHisDto(order);

            List<OrdersProduct> ordersProductList = ordersProductRepository.findByOrdersId(order.getId());

            for (OrdersProduct ordersProduct : ordersProductList) {

                OrdersProductDto ordersProductDto = new OrdersProductDto(ordersProduct);

                ordersHisDto.addOrderProductDto(ordersProductDto);
            }

            ordersHisDtoList.add(ordersHisDto);
        }
        return ordersHisDtoList;

    }
}