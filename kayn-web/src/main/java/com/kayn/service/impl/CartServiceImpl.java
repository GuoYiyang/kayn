package com.kayn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kayn.mapper.cart.MyCartMapper;
import com.kayn.pojo.cart.MyCart;
import com.kayn.result.Result;
import com.kayn.service.CartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private MyCartMapper myCartMapper;

    @Override
    public Result<MyCart> addCart(MyCart myCart) {
        Result<MyCart> result = new Result<>();
        try {
            myCartMapper.insert(myCart);
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("添加商品成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(myCart);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("添加商品失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(myCart);
        }
        return result;
    }

    @Override
    public Result<MyCart> delCart(String username, String productId) {
        Result<MyCart> result = new Result<>();
        try {
            myCartMapper.delete(new QueryWrapper<MyCart>().eq("username", username).eq("product_id", productId));
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("删除商品成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("删除商品失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<MyCart> delCartChecked(String username) {
        Result<MyCart> result = new Result<>();
        try {
            myCartMapper.delete(new QueryWrapper<MyCart>().eq("username", username).eq("checked", 1));
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("删除商品成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("删除商品失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<List<MyCart>> getCartList(String username) {
        Result<List<MyCart>> result = new Result<>();
        try {
            List<MyCart> myCartList = myCartMapper.selectList(new QueryWrapper<MyCart>().eq("username", username));
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("获取购物车列表成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(myCartList);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("获取购物车列表失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<MyCart> cartEdit(String username, String productId, Integer productNum, Integer checked) {
        Result<MyCart> result = new Result<>();
        try {
            myCartMapper.update(new MyCart().setUsername(username)
                                .setProductId(productId)
                                .setProductNum(productNum)
                                .setChecked(checked),
                    new UpdateWrapper<MyCart>().eq("username", username)
                                            .eq("product_id", productId));
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("更新成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("更新失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }

        return result;
    }

    @Override
    public Result<MyCart> editCheckAll(String username, boolean checked) {
        Result<MyCart> result = new Result<>();
        try {
            if (checked) {
                myCartMapper.update(new MyCart().setUsername(username).setChecked(1),
                        new UpdateWrapper<MyCart>().eq("username", username));
            } else {
                myCartMapper.update(new MyCart().setUsername(username).setChecked(0),
                        new UpdateWrapper<MyCart>().eq("username", username));
            }
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("更新成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("更新失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;
    }
}
