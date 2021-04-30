package com.kayn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kayn.dto.R;
import com.kayn.mapper.order.OrderMapper;
import com.kayn.mapper.user.UserMapper;
import com.kayn.pojo.order.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/home")
public class HomeController {

    @Resource
    UserMapper userMapper;

    @Resource
    OrderMapper orderMapper;

    @GetMapping("/getUserCnt")
    public R getUserCnt() {
        R r = new R();
        try {
            Integer count = userMapper.selectCount(new QueryWrapper<>());
            r.setCode(200).setData(count);
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
        }
        return r;
    }

    @GetMapping("/getOrderCnt")
    public R getOrderCnt() {
        R r = new R();
        try {
            Integer count = orderMapper.selectCount(new QueryWrapper<>());
            r.setCode(200).setData(count);
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
        }
        return r;
    }
    @GetMapping("/getOrderTotal")
    public R getOrderTotal() {
        R r = new R();
        try {
            Double total = 0D;
            List<Order> orderList = orderMapper.selectList(new QueryWrapper<>());
            for (Order order: orderList) {
                total += order.getOrderTotal();
            }
            r.setCode(200).setData(total);
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
        }
        return r;
    }
}
