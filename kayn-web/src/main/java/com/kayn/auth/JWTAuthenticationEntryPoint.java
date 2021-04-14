package com.kayn.auth;

import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 进行匿名用户访问资源时无权限的处理
 */
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException){
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/javascript;charset=utf-8");
            response.getWriter().print(JSONObject.toJSONString("您未登录，没有访问权限"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
