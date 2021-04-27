package com.kayn.dto;

import com.kayn.pojo.order.Order;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrderListDto {
    private List<Order> orderList;
    private Integer totalCnt;
}
