package com.kayn.dto;

import com.kayn.pojo.order.OrderGood;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrderDto {
    private Long userId;
    private String username;
    private String street;
    private String tel;
    private Double orderTotal;
    private List<OrderGood> orderGoodList;
}
