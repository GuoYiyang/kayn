package com.kayn.controller;

import com.alibaba.fastjson.JSONObject;
import com.kayn.pojo.cart.MyCart;
import com.kayn.result.Result;
import com.kayn.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Resource
    private CartService cartService;

    /**
     * 添加购物车
     * @param jsonObject 请求体
     * @return Result<MyCart>
     */
    @PostMapping("/addCart")
    public Result<MyCart> addCart(@RequestBody JSONObject jsonObject) {
        MyCart myCart = new MyCart();
        String username = jsonObject.getString("username");
        Double salePrice = jsonObject.getDouble("salePrice");
        String productName = jsonObject.getString("productName");
        Integer productNum = jsonObject.getInteger("productNum");
        myCart.setProductId(jsonObject.getString("productId"))
                .setUsername(username)
                .setSalePrice(salePrice)
                .setProductName(productName)
                .setProductImg(jsonObject.getString("productImg"))
                .setProductNum(productNum)
                .setChecked(1);

        HashMap<String, String> info = new HashMap<>();
        info.put("username", username);
        info.put("productName", productName);
        info.put("salePrice", salePrice.toString());
        info.put("productNum", productNum.toString());

        // 记录日志
        logger.info(JSONObject.toJSONString(info));
        return cartService.addCart(myCart);
    }

    /**
     * 删除购物车
     * @param jsonObject 请求体
     * @return Result<MyCart>
     */
    @PostMapping("/cartDel")
    public Result<MyCart> delCart(@RequestBody JSONObject jsonObject) {
        return cartService.delCart(jsonObject.getString("username"), jsonObject.getString("productId"));
    }

    /**
     * 删除选中的商品
     * @param jsonObject 请求体
     * @return Result<MyCart>
     */
    @PostMapping("/delCartChecked")
    public Result<MyCart> delCartChecked(@RequestBody JSONObject jsonObject) {
        return cartService.delCartChecked(jsonObject.getString("username"));

    }

    /**
     * 获取购物车列表
     * @param jsonObject 请求体
     * @return Result<List<MyCart>>
     */
    @PostMapping("/cartList")
    public Result<List<MyCart>> getCartList(@RequestBody JSONObject jsonObject) {
        return cartService.getCartList(jsonObject.getString("username"));
    }

    /**
     * 编辑购物车（选中状态：checked）
     * @param jsonObject 请求体
     * @return Result<MyCart>
     */
    @PostMapping("/cartEdit")
    public Result<MyCart> cartEdit(@RequestBody JSONObject jsonObject) {
        return cartService.cartEdit(jsonObject.getString("username"),
                jsonObject.getString("productId"),
                jsonObject.getInteger("productNum"),
                jsonObject.getInteger("checked"));
    }

    /**
     * 全选更改状态
     * @param jsonObject 请求体
     * @return Result<MyCart>
     */
    @PostMapping("/editCheckAll")
    public Result<MyCart> editCheckAll(@RequestBody JSONObject jsonObject) {
        return cartService.editCheckAll(jsonObject.getString("username"), jsonObject.getBoolean("checked"));
    }
}
