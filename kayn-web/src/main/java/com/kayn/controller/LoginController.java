package com.kayn.controller;

import com.alibaba.fastjson.JSONObject;
import com.kayn.pojo.user.UserInfo;
import com.kayn.result.Result;
import com.kayn.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private LoginService loginService;

    /**
     * 检查当前用户是否登录
     * @param tokenHeader kayn_token
     * @return Result<User>
     */
    @GetMapping("/checkLogin")
    public Result<UserInfo> checkLogin(@RequestParam(value = "token", required = false) String tokenHeader) {
        if (tokenHeader != null) {
            return loginService.checkLogin(tokenHeader);
        } else {
            return new Result<UserInfo>().setSuccess(false).setCode(500).setResult(null);
        }

    }

    /**
     * 注册用户
     * @param jsonObject 请求体 {username: xxx, password:xxx}
     * @return Result<User>
     */
    @PostMapping("/register")
    public Result<UserInfo> register(@RequestBody JSONObject jsonObject) {

        return loginService.register(jsonObject.getString("username"), jsonObject.getString("password"));
    }

    /**
     * 注销登录，直接返回true，前端清除token
     * @return Result<UserInfo>
     */
    @GetMapping("/loginOut")
    public Result<UserInfo> loginOut() {
        return loginService.loginOut();
    }
}
