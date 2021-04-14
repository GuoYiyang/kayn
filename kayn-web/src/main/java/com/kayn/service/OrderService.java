package com.kayn.service;

import com.kayn.dto.OrderDto;
import com.kayn.pojo.order.MyOrder;
import com.kayn.pojo.order.OrderGood;
import com.kayn.pojo.order.OrderResult;
import com.kayn.result.Result;

import java.util.ArrayList;

public interface OrderService {

    Result<OrderResult> addOrder(OrderDto orderDto);

    Result<OrderResult> getOrderList(String username, Integer currentPage, Integer pageSize);

    Result<MyOrder> getOrderDetail(Integer orderId);

    Result<MyOrder> cancelOrder(Integer orderId);

    Result<MyOrder> delOrder(Integer orderId);

    Result<MyOrder> payOrder(Integer orderId);
}
