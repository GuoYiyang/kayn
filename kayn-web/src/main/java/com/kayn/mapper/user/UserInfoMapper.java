package com.kayn.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kayn.pojo.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
