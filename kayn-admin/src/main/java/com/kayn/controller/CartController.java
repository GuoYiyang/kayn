package com.kayn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kayn.dto.CartListDto;
import com.kayn.mapper.cart.CartMapper;
import com.kayn.pojo.cart.Cart;
import com.kayn.dto.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/cart")
public class CartController {

    @Resource
    CartMapper cartMapper;

    @GetMapping("/getCartList")
    public R getCartList(@RequestParam(value = "username", required = false) String username,
                         @RequestParam Integer pageIndex,
                         @RequestParam Integer pageSize) {
        R r = new R();
        try {
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>().orderByDesc("id");
            Page<Cart> page = new Page<>(pageIndex, pageSize);
            if (!username.equals("")) {
                queryWrapper.eq("username", username);
            }
            List<Cart> cartList = cartMapper.selectPage(page, queryWrapper).getRecords();
            Integer count = cartMapper.selectCount(queryWrapper);
            r.setCode(200).setData(new CartListDto().setCartList(cartList).setTotalCnt(count));
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }

    @PostMapping("/editCart")
    public R editCard(Cart cart) {
        R r = new R();
        try {
            int id = cartMapper.update(cart, new UpdateWrapper<Cart>().eq("id", cart.getId()));
            r.setCode(200).setData(id);
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }

    @DeleteMapping("/deleteCart")
    public R deleteCart(@RequestParam Integer id) {
        R r = new R();
        try {
            int id1 = cartMapper.delete(new QueryWrapper<Cart>().eq("id", id));
            r.setCode(200).setData(id1);
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }
}
