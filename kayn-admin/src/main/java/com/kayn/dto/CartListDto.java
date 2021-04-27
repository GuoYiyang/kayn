package com.kayn.dto;

import com.kayn.pojo.cart.Cart;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CartListDto {
    private List<Cart> cartList;
    private Integer totalCnt;
}
