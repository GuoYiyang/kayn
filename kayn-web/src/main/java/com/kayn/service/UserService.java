package com.kayn.service;

import com.kayn.pojo.user.UserInfo;
import com.kayn.result.Result;

public interface UserService {

    void refreshUser(String username);

    Result<UserInfo> editUser(UserInfo userInfo);

}
