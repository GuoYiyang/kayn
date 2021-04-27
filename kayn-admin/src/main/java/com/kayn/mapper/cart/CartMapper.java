package com.kayn.mapper.cart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kayn.pojo.cart.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
