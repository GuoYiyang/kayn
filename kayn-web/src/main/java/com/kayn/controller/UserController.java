package com.kayn.controller;

import com.alibaba.fastjson.JSONObject;
import com.kayn.pojo.user.UserInfo;
import com.kayn.result.Result;
import com.kayn.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;


    /**
     * 编辑用户
     * @param jsonObject 请求体
     * @return Result<UserInfo>
     */
    @PostMapping("/edit")
    public Result<UserInfo> editUser(@RequestBody JSONObject jsonObject) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(jsonObject.getString("username"))
                .setPhone(jsonObject.getString("phone"))
                .setEmail(jsonObject.getString("email"))
                .setSex(jsonObject.getString("sex"))
                .setAddress(jsonObject.getString("address"))
                .setDescription(jsonObject.getString("description"));
        return userService.editUser(userInfo);
    }
}

