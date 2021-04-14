package com.kayn.controller;

import com.alibaba.fastjson.JSONObject;
import com.kayn.pojo.address.Address;
import com.kayn.result.Result;
import com.kayn.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Resource
    private AddressService addressService;

    /**
     * 添加收货地址
     * @param jsonObject 请求体
     * @return Result<Address>
     */
    @PostMapping("/addAddress")
    public Result<Address> addAddress(@RequestBody JSONObject jsonObject) {
        Address address = new Address();
        address.setUsername(jsonObject.getString("username"))
                .setName(jsonObject.getString("name"))
                .setTel(jsonObject.getString("tel"))
                .setStreetName(jsonObject.getString("streetName"))
                .setIsDefault(jsonObject.getBoolean("isDefault"));

        return addressService.addAddress(address);
    }

    /**
     * 更新地址
     * @param jsonObject 请求体
     * @return Result<Address>
     */
    @PostMapping("/updateAddress")
    public Result<Address> updateAddress(@RequestBody JSONObject jsonObject) {
        Address address = new Address();
        address.setUsername(jsonObject.getString("username"))
                .setName(jsonObject.getString("name"))
                .setTel(jsonObject.getString("tel"))
                .setStreetName(jsonObject.getString("streetName"))
                .setIsDefault(jsonObject.getBoolean("isDefault"));
        return addressService.updateAddress(address);
    }

    /**
     * 删除地址
     * @param jsonObject 请求体
     * @return Result<Address>
     */
    @PostMapping("/delAddress")
    public Result<Address> delAddress(@RequestBody JSONObject jsonObject) {
        return addressService.delAddress(jsonObject.getInteger("addressId"));
    }

    /**
     * 获取所有地址列表
     * @param jsonObject 请求体
     * @return Result<Address>
     */
    @PostMapping("/addressList")
    Result<List<Address>> getAddressList(@RequestBody JSONObject jsonObject) {
        return addressService.getAddressList(jsonObject.getString("username"));
    }
}
