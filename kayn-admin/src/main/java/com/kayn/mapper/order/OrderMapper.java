package com.kayn.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kayn.pojo.order.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
