package com.kayn.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kayn.mapper.user.UserInfoMapper;
import com.kayn.pojo.user.UserInfo;
import com.kayn.result.Result;
import com.kayn.service.RecommenderService;
import com.kayn.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserInfoMapper userInfoMapper;


    @Override
    public Result<UserInfo> editUser(UserInfo userInfo) {
        Result<UserInfo> result = new Result<>();
        try {
            userInfoMapper.update(userInfo, new UpdateWrapper<UserInfo>().eq("username", userInfo.getUsername()));
            result.setCode(200)
                    .setSuccess(true)
                    .setMessage("修改成功")
                    .setTimestamp(new Date().getTime())
                    .setResult(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500)
                    .setSuccess(false)
                    .setMessage("修改失败")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }

        return result;
    }

}
