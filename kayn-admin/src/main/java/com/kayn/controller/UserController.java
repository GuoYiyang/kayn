package com.kayn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kayn.dto.R;
import com.kayn.dto.UserListDto;
import com.kayn.mapper.idf.CatIdfMapper;
import com.kayn.mapper.idf.QueryIdfMapper;
import com.kayn.mapper.user.UserInfoMapper;
import com.kayn.mapper.user.UserMapper;
import com.kayn.mapper.user.UserRFMMapper;
import com.kayn.mapper.user.UserRoleMapper;
import com.kayn.pojo.idf.CatIdf;
import com.kayn.pojo.idf.QueryIdf;
import com.kayn.pojo.user.User;
import com.kayn.pojo.user.UserInfo;
import com.kayn.pojo.user.UserRFM;
import com.kayn.pojo.user.UserRole;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    CatIdfMapper catIdfMapper;

    @Resource
    QueryIdfMapper queryIdfMapper;

    @GetMapping("/checkRole")
    public R checkRole(@RequestParam String username,
                       @RequestParam String password){
        R r = new R();
        try {
            User user = userMapper.selectOne(new QueryWrapper<User>()
                    .eq("username", username)
                    .eq("password", password));
            if (user == null) {
                r.setCode(500);
            } else {
                UserRole userRole = userRoleMapper.selectOne(new QueryWrapper<UserRole>()
                        .eq("username", username));
                if (!"ROLE_ADMIN".equals(userRole.getRole())) {
                    r.setCode(500);
                } else {
                    r.setCode(200);
                }
            }
        } catch (Exception e) {
            r.setCode(500);
            e.printStackTrace();
        }
        return r;
    }

    @GetMapping("/getUserList")
    public R getUserList(@RequestParam(value = "username", required = false) String username,
                         @RequestParam Integer pageIndex,
                         @RequestParam Integer pageSize) {
        R r = new R();
        try {
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            Page<UserInfo> page = new Page<>(pageIndex, pageSize);
            if (!username.equals("")) {
                queryWrapper.eq("username", username);
            }
            List<UserInfo> userInfoList = userInfoMapper.selectPage(page, queryWrapper).getRecords();
            for (UserInfo userInfo : userInfoList) {
                User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", userInfo.getUsername()));
                UserRole userRole = userRoleMapper.selectOne(new QueryWrapper<UserRole>().eq("username", userInfo.getUsername()));
                UserRFM userRFM = userRFMMapper.selectOne(new QueryWrapper<UserRFM>().eq("username", userInfo.getUsername()));
                userInfo.setPassword(user.getPassword()).setRole(userRole.getRole());
                if(userRFM != null) {
                    userInfo.setRfm(userRFM.getRfm());
                }
            }
            Integer count = userInfoMapper.selectCount(queryWrapper);
            r.setCode(200).setData(new UserListDto().setUserList(userInfoList).setTotalCnt(count));
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
        }
        return r;
    }

    @PostMapping("/editUser")
    public R editUser(UserInfo userInfo) {
        R r = new R();
        try {
            String username = userInfo.getUsername();
            User user = new User().setUsername(username).setPassword(userInfo.getPassword());
            UserRole userRole = new UserRole().setUsername(username).setRole(userInfo.getRole());
            UserRFM userRFM = new UserRFM().setUsername(username).setRfm(userInfo.getRfm());
            userMapper.update(user, new UpdateWrapper<User>().eq("username", username));
            userInfoMapper.update(userInfo, new UpdateWrapper<UserInfo>().eq("username",username));
            userRoleMapper.update(userRole, new UpdateWrapper<UserRole>().eq("username", username));
            userRFMMapper.update(userRFM, new UpdateWrapper<UserRFM>().eq("username", username));
            r.setCode(200);
        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(500);
        }
        return r;
    }

    @DeleteMapping("/deleteUser")
    public R deleteUser(@RequestParam String username) {
        R r = new R();
        try {
            userMapper.delete(new QueryWrapper<User>().eq("username", username));
            userInfoMapper.delete(new QueryWrapper<UserInfo>().eq("username", username));
            userRoleMapper.delete(new QueryWrapper<UserRole>().eq("username", username));
            userRFMMapper.delete(new QueryWrapper<UserRFM>().eq("username", username));
            catIdfMapper.delete(new QueryWrapper<CatIdf>().eq("username", username));
            queryIdfMapper.delete(new QueryWrapper<QueryIdf>().eq("username", username));
            r.setCode(200);
        } catch (Exception e) {
            r.setCode(500);
        }
        return r;
    }
}
