package com.kayn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kayn.dto.OrderDto;
import com.kayn.mapper.address.AddressMapper;
import com.kayn.mapper.cart.MyCartMapper;
import com.kayn.mapper.order.MyOrderMapper;
import com.kayn.mapper.order.OrderGoodMapper;
import com.kayn.pojo.address.Address;
import com.kayn.pojo.cart.MyCart;
import com.kayn.pojo.order.MyOrder;
import com.kayn.pojo.order.OrderGood;
import com.kayn.pojo.order.OrderResult;
import com.kayn.result.Result;
import com.kayn.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private MyOrderMapper myOrderMapper;

    @Resource
    private OrderGoodMapper orderGoodMapper;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private MyCartMapper myCartMapper;


    @Override
    public Result<OrderResult> addOrder(OrderDto orderDto) {
        Result<OrderResult> result = new Result<>();
        try {
            // 创建订单
            MyOrder myOrder = new MyOrder();
            Address address = addressMapper.selectOne(new QueryWrapper<Address>()
                    .eq("username", orderDto.getUsername())
                    .eq("tel", orderDto.getTel())
                    .eq("street_name", orderDto.getStreet()));

            Date createDate = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(createDate);
            calendar.add(Calendar.DATE,7);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
            Date endDate = calendar.getTime();

            myOrder.setAddressId(address.getAddressId())
                    .setUsername(orderDto.getUsername())
                    .setOrderTotal(orderDto.getOrderTotal())
                    .setOrderStatus(0)
                    .setCreateDate(createDate)
                    .setEndDate(endDate);

            myOrderMapper.insert(myOrder);

            // 从购物车中删除,并且添加到订单历史中
            for(OrderGood good : orderDto.getOrderGoodList()) {
                myCartMapper.delete(new QueryWrapper<MyCart>()
                        .eq("username", orderDto.getUsername())
                        .eq("product_id", good.getProductId()));
                MyOrder order = myOrderMapper.selectOne(new QueryWrapper<MyOrder>()
                        .eq("address_id", address.getAddressId())
                        .eq("username", orderDto.getUsername())
                        .eq("create_date", createDate));
                good.setOrderId(order.getOrderId());
                orderGoodMapper.insert(good);
            }

            result.setCode(200)
                    .setSuccess(true)
                    .setTimestamp(new Date().getTime())
                    .setMessage("创建订单成功")
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setTimestamp(new Date().getTime())
                    .setMessage("创建订单失败")
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<OrderResult> getOrderList(String username, Integer currentPage, Integer pageSize) {
        Result<OrderResult> result = new Result<>();
        OrderResult orderResult = new OrderResult();
        try {
            List<MyOrder> myOrderList = myOrderMapper.selectPage(new Page<>(currentPage, pageSize),
                    new QueryWrapper<MyOrder>().eq("username", username)).getRecords();
            for(MyOrder order : myOrderList) {
                Long orderId = order.getOrderId();
                List<OrderGood> orderGoodList = orderGoodMapper.selectList(new QueryWrapper<OrderGood>().eq("order_id", orderId));
                order.setGoodsList(orderGoodList);
                Long addressId = order.getAddressId();
                Address address = addressMapper.selectOne(new QueryWrapper<Address>().eq("address_id", addressId));
                order.setAddressInfo(address);
            }
            orderResult.setData(myOrderList);
            orderResult.setTotal(myOrderMapper.selectCount(new QueryWrapper<MyOrder>().eq("username", username)));
            result.setCode(200)
                    .setSuccess(true)
                    .setTimestamp(new Date().getTime())
                    .setMessage("获取订单列表成功")
                    .setResult(orderResult);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setTimestamp(new Date().getTime())
                    .setMessage("获取订单列表失败")
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<MyOrder> getOrderDetail(Integer orderId) {
        Result<MyOrder> result = new Result<>();
        try {
            MyOrder myOrder = myOrderMapper.selectOne(new QueryWrapper<MyOrder>().eq("order_id", orderId));
            Address address = addressMapper.selectOne(new QueryWrapper<Address>().eq("address_id", myOrder.getAddressId()));
            List<OrderGood> orderGoodList = orderGoodMapper.selectList(new QueryWrapper<OrderGood>().eq("order_id", orderId));
            myOrder.setAddressInfo(address).setGoodsList(orderGoodList);
            result.setCode(200)
                    .setSuccess(true)
                    .setTimestamp(new Date().getTime())
                    .setMessage("获取订单详细信息成功")
                    .setResult(myOrder);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setTimestamp(new Date().getTime())
                    .setMessage("获取订单详细信息失败")
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<MyOrder> cancelOrder(Integer orderId) {
        Result<MyOrder> result = new Result<>();
        try {
            myOrderMapper.update(new MyOrder().setOrderStatus(-1).setFinishDate(new Date()), new QueryWrapper<MyOrder>().eq("order_id", orderId));
            result.setCode(200)
                    .setSuccess(true)
                    .setTimestamp(new Date().getTime())
                    .setMessage("取消订单成功")
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setTimestamp(new Date().getTime())
                    .setMessage("取消订单失败")
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<MyOrder> delOrder(Integer orderId) {
        Result<MyOrder> result = new Result<>();
        try {
            orderGoodMapper.delete(new QueryWrapper<OrderGood>().eq("order_id", orderId));
            myOrderMapper.delete(new QueryWrapper<MyOrder>().eq("order_id", orderId));

            result.setCode(200)
                    .setSuccess(true)
                    .setTimestamp(new Date().getTime())
                    .setMessage("删除订单成功")
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setTimestamp(new Date().getTime())
                    .setMessage("删除订单失败")
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<MyOrder> payOrder(Integer orderId) {
        Result<MyOrder> result = new Result<>();
        try {
            myOrderMapper.update(new MyOrder().setPayDate(new Date()).setOrderStatus(1), new QueryWrapper<MyOrder>().eq("order_id", orderId));
            result.setCode(200)
                    .setSuccess(true)
                    .setTimestamp(new Date().getTime())
                    .setMessage("订单支付成功")
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setTimestamp(new Date().getTime())
                    .setMessage("订单支付失败")
                    .setResult(null);
        }
        return result;
    }
}
