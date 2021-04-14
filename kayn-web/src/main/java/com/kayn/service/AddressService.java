package com.kayn.service;

import com.kayn.pojo.address.Address;
import com.kayn.result.Result;

import java.util.List;

public interface AddressService {

    Result<Address> addAddress(Address address);

    Result<Address> updateAddress(Address address);

    Result<Address> delAddress(Integer addressId);

    Result<List<Address>> getAddressList(String username);
}
