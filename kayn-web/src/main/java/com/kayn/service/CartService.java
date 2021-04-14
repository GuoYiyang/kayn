package com.kayn.service;

import com.kayn.pojo.cart.MyCart;
import com.kayn.result.Result;

import java.util.List;

public interface CartService {

    Result<MyCart> addCart(MyCart myCart);

    Result<MyCart> delCart(String username, String productId);

    Result<MyCart> delCartChecked(String username);

    Result<List<MyCart>> getCartList(String username);

    Result<MyCart> cartEdit(String username, String productId, Integer productNum, Integer checked);

    Result<MyCart> editCheckAll(String username, boolean checked);
}
