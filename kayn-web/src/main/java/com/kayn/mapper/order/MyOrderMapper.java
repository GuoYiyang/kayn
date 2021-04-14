package com.kayn.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kayn.pojo.order.MyOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyOrderMapper extends BaseMapper<MyOrder> {
}
