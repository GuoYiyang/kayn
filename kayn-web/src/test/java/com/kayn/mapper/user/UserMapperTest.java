package com.kayn.mapper.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kayn.pojo.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class UserMapperTest {

    @Resource
    UserMapper userMapper;

    @Test
    void selectTest() {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", "admin"));
        System.out.println(user);
    }

}
