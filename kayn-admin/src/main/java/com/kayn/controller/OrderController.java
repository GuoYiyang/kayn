package com.kayn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kayn.dto.OrderListDto;
import com.kayn.dto.R;
import com.kayn.mapper.order.OrderAddressMapper;
import com.kayn.mapper.order.OrderGoodMapper;
import com.kayn.mapper.order.OrderMapper;
import com.kayn.pojo.order.Order;
import com.kayn.pojo.order.OrderAddress;
import com.kayn.pojo.order.OrderGood;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/order")
public class OrderController {

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderGoodMapper orderGoodMapper;

    @Resource
    OrderAddressMapper orderAddressMapper;

    @GetMapping("/getOrderList")
    public R getOrderList(@RequestParam(value = "username", required = false) String username,
                          @RequestParam Integer pageIndex,
                          @RequestParam Integer pageSize) {
        R r = new R();
        try {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>().orderByDesc("order_id");
            Page<Order> page = new Page<>(pageIndex, pageSize);
            if (!username.equals("")) {
                queryWrapper.eq("username", username);
            }
            List<Order> orderList = orderMapper.selectPage(page, queryWrapper).getRecords();
            Integer count = orderMapper.selectCount(queryWrapper);
            r.setCode(200).setData(new OrderListDto().setOrderList(orderList).setTotalCnt(count));
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }

    @GetMapping("/getOrderGoodList")
    public R getOrderGoodList(@RequestParam Integer orderID) {
        R r = new R();
        try {
            QueryWrapper<OrderGood> queryWrapper = new QueryWrapper<OrderGood>().eq("order_id", orderID);
            List<OrderGood> orderGoodList = orderGoodMapper.selectList(queryWrapper);
            r.setCode(200).setData(orderGoodList);
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }

    @GetMapping("/getOrderAddress")
    public R getOrderAddress(@RequestParam Integer addressID) {
        R r = new R();
        try {
            QueryWrapper<OrderAddress> queryWrapper = new QueryWrapper<OrderAddress>().eq("address_id", addressID);
            List<OrderAddress> orderAddressList = orderAddressMapper.selectList(queryWrapper);
            r.setCode(200).setData(orderAddressList);
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }
    @DeleteMapping("/deleteOrder")
    public R deleteOrder(@RequestParam Integer orderID) {
        R r = new R();
        try {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>().eq("order_id", orderID);
            int delete = orderMapper.delete(queryWrapper);
            r.setCode(200).setData(delete);
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }


}
