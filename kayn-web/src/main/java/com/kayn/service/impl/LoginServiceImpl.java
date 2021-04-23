package com.kayn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kayn.mapper.user.UserInfoMapper;
import com.kayn.mapper.user.UserMapper;
import com.kayn.mapper.user.UserRoleMapper;
import com.kayn.pojo.user.User;
import com.kayn.pojo.user.UserInfo;
import com.kayn.pojo.user.UserRole;
import com.kayn.result.Result;
import com.kayn.service.LoginService;
import com.kayn.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public Result<UserInfo> checkLogin(String tokenHeader) {
        Result<UserInfo> result = new Result<>();
        String token = tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        Claims claims = JwtTokenUtil.checkJWT(token);
        if (claims != null) {
            UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("username", JwtTokenUtil.getUsername(token)));
            result.setSuccess(true)
                    .setCode(200)
                    .setMessage("success")
                    .setTimestamp(new Date().getTime())
                    .setResult(userInfo);
        } else {
            result.setSuccess(false)
                    .setCode(500)
                    .setMessage("fail")
                    .setTimestamp(new Date().getTime())
                    .setResult(null);
        }
        return result;
    }

    @Override
    public Result<UserInfo> register(String username, String password) {
        Result<UserInfo> result = new Result<>();
        try {
            userMapper.insert(new User().setUsername(username).setPassword(password));
            userInfoMapper.insert(new UserInfo().setUsername(username));
            userRoleMapper.insert(new UserRole().setUsername(username).setRole("ROLE_USER"));
            result.setCode(200)
                    .setSuccess(true)
                    .setTimestamp(new Date().getTime())
                    .setMessage("注册成功")
                    .setResult(null);
        } catch (Exception e) {
            result.setCode(500)
                    .setSuccess(false)
                    .setTimestamp(new Date().getTime())
                    .setMessage("注册失败")
                    .setResult(null);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result<UserInfo> loginOut() {
        return new Result<UserInfo>()
                .setSuccess(true)
                .setCode(200)
                .setResult(null);
    }
}
