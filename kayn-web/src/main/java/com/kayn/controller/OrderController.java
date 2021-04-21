package com.kayn.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kayn.dto.OrderDto;
import com.kayn.pojo.order.MyOrder;
import com.kayn.pojo.order.OrderGood;
import com.kayn.pojo.order.OrderResult;
import com.kayn.result.Result;
import com.kayn.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Resource
    private OrderService orderService;

    /**
     * 添加订单
     * @param jsonObject 请求体
     * @return Result<OrderResult>
     */
    @PostMapping("/addOrder")
    public Result<OrderResult> addOrder(@RequestBody JSONObject jsonObject) {
        JSONArray goodsList = jsonObject.getJSONArray("goodsList");
        ArrayList<OrderGood> orderGoodList = new ArrayList<>();
        for (Object goodObject : goodsList) {
            JSONObject good = (JSONObject) goodObject;
            OrderGood orderGood = new OrderGood();
            orderGood.setProductId(Long.parseLong(good.getString("productId")))
                    .setSalePrice(good.getDouble("salePrice"))
                    .setProductNum(good.getInteger("productNum"))
                    .setChecked(good.getInteger("checked"))
                    .setProductName(good.getString("productName"))
                    .setProductImg(good.getString("productImg"));
            orderGoodList.add(orderGood);
        }
        Double orderTotal = jsonObject.getDouble("orderTotal");
        String streetName = jsonObject.getString("streetName");
        String tel = jsonObject.getString("tel");
        String username = jsonObject.getString("username");
        OrderDto orderDto = new OrderDto()
                .setUsername(username)
                .setOrderTotal(orderTotal)
                .setStreet(streetName)
                .setTel(tel)
                .setOrderGoodList(orderGoodList);

        // 记录日志
        HashMap<String, String> info = new HashMap<>();
        info.put("username", username);
        info.put("orderTotal", orderTotal.toString());
        info.put("street", streetName);
        info.put("tel", tel);
        logger.info(JSONObject.toJSONString(info));
        return orderService.addOrder(orderDto);
    }

    /**
     * 获取所用订单
     * @param username 用户名
     * @param pageSize 页大小
     * @param currentPage 当前页
     * @return Result<OrderResult>
     */
    @GetMapping("/orderList")
    public Result<OrderResult> getOrderList(@RequestParam("username") String username,
                                            @RequestParam("size") Integer pageSize,
                                            @RequestParam("page") Integer currentPage) {
        return orderService.getOrderList(username, currentPage, pageSize);
    }

    /**
     * 获取订单详情
     * @param orderId 订单号
     * @return Result<MyOrder>
     */
    @GetMapping("/orderDetail")
    public Result<MyOrder> getOrderDetail(@RequestParam("orderId") Integer orderId) {
        return orderService.getOrderDetail(orderId);
    }

    /**
     * 取消订单
     * @param jsonObject 请求体
     * @return Result<MyOrder>
     */
    @PostMapping("/cancelOrder")
    public Result<MyOrder> cancelOrder(@RequestBody JSONObject jsonObject) {
        Integer orderId = jsonObject.getInteger("orderId");
        return orderService.cancelOrder(orderId);
    }

    /**
     * 删除订单
     * @param orderId 订单号
     * @return Result<MyOrder>
     */
    @GetMapping("/delOrder")
    public Result<MyOrder> delOrder(@RequestParam("orderId") Integer orderId) {
        return orderService.delOrder(orderId);
    }

    /**
     * 支付订单
     * @param jsonObject 请求体
     * @return Result<MyOrder>
     */
    @PostMapping("/payOrder")
    public Result<MyOrder> payOrder(@RequestBody JSONObject jsonObject) {
        Integer orderId = jsonObject.getInteger("orderId");
        Double orderTotal = jsonObject.getDouble("orderTotal");
        String username = jsonObject.getString("username");

        // 记录日志
        HashMap<String, String> info = new HashMap<>();
        info.put("username", username);
        info.put("orderTotal", orderTotal.toString());
        logger.info(JSONObject.toJSONString(info));
        return orderService.payOrder(orderId);
    }

}
