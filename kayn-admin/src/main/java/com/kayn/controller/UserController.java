package com.kayn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kayn.dto.R;
import com.kayn.dto.UserListDto;
import com.kayn.mapper.user.UserInfoMapper;
import com.kayn.mapper.user.UserMapper;
import com.kayn.mapper.user.UserRFMMapper;
import com.kayn.mapper.user.UserRoleMapper;
import com.kayn.pojo.user.UserInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Resource
    UserMapper userMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    UserRoleMapper userRoleMapper;

    @Resource
    UserRFMMapper userRFMMapper;

    @GetMapping("/getUserList")
    public R getUserList(@RequestParam Integer pageIndex,
                         @RequestParam Integer pageSize) {
        R r = new R();
        try {
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            Page<UserInfo> page = new Page<>(pageIndex, pageSize);
            List<UserInfo> userInfoList = userInfoMapper.selectPage(page, queryWrapper).getRecords();
            Integer count = userInfoMapper.selectCount(queryWrapper);
            r.setCode(200).setData(new UserListDto().setUserList(userInfoList).setTotalCnt(count));
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }


}
