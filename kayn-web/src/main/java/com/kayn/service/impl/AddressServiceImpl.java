package com.kayn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kayn.mapper.address.AddressMapper;
import com.kayn.pojo.address.Address;
import com.kayn.result.Result;
import com.kayn.service.AddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;

    @Override
    public Result<Address> addAddress(Address address) {
        Result<Address> result = new Result<>();
        try {
            addressMapper.insert(address);
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("新增地址成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("新增地址失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<Address> updateAddress(Address address) {
        Result<Address> result = new Result<>();
        try {
            addressMapper.update(new Address().setIsDefault(false), new UpdateWrapper<Address>().eq("username", address.getUsername()));
            addressMapper.update(address, new UpdateWrapper<Address>()
                    .eq("username", address.getUsername())
                    .eq("name", address.getName())
                    .eq("tel", address.getTel())
                    .eq("street_name", address.getStreetName()));
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("修改地址成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("修改地址失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;

    }

    @Override
    public Result<Address> delAddress(Integer addressId) {
        Result<Address> result = new Result<>();
        try {
            addressMapper.delete(new QueryWrapper<Address>().eq("address_id", addressId));
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("删除地址成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("删除地址失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<List<Address>> getAddressList(String username) {
        Result<List<Address>> result = new Result<>();
        try {
            List<Address> addressList = addressMapper.selectList(new QueryWrapper<Address>().eq("username", username));
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("查询地址列表成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(addressList);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("查询地址列表失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;
    }
}
