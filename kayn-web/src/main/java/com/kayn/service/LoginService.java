package com.kayn.service;

import com.kayn.pojo.user.UserInfo;
import com.kayn.result.Result;

public interface LoginService {

    Result<UserInfo> checkLogin(String tokenHeader);

    Result<UserInfo> register(String username, String password);

    Result<UserInfo> loginOut();
}
