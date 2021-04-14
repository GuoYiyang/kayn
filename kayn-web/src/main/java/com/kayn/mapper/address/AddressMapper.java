package com.kayn.mapper.address;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kayn.pojo.address.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
