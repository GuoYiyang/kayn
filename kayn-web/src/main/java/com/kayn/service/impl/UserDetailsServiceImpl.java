package com.kayn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kayn.mapper.user.UserMapper;
import com.kayn.mapper.user.UserRoleMapper;
import com.kayn.pojo.user.User;
import com.kayn.pojo.user.UserRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDetailsService的实现类 用于加载用户信息
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || "".equals(username)) {
            throw new RuntimeException("用户名不能为空");
        }
        // 调用方法查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 查询用户角色信息
        UserRole userRole= userRoleMapper.selectOne(new QueryWrapper<UserRole>().eq("username", username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole.getRole()));

        return new org.springframework.security.core.userdetails.User(user.getUsername(),"{noop}" + user.getPassword(), authorities);
    }
}
