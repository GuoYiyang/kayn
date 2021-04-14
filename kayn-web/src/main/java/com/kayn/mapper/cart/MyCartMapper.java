package com.kayn.mapper.cart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kayn.pojo.cart.MyCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyCartMapper extends BaseMapper<MyCart> {
}
