package com.kayn.auth;

import com.alibaba.fastjson.JSON;
import com.kayn.pojo.user.UserInfo;
import com.kayn.result.Result;
import com.kayn.util.JwtTokenUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * 认证
 * JWTAuthenticationFilter登录拦截器
 * 该拦截器用于获取用户登录的信息
 * 至于具体的验证 只需创建一个token并调用 authenticationManager 的 authenticate()方法
 * 让 Spring security 验证即可 验证的事交给框架
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    /**
     * 构造函数
     * @param authenticationManager authenticationManager
     */
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * 验证操作 接收并解析用户凭证
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 从输入流中获取到登录的信息
        // 创建一个token并调用authenticationManager.authenticate() 让Spring security进行验证
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getParameter("username"), request.getParameter("password"));
        return authenticationManager.authenticate(token);
    }

    /**
     * 验证【成功】后调用的方法
     * 若验证成功 生成token并返回
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        try {
            User user= (User) authResult.getPrincipal();
            // 从User中获取权限信息
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            // 创建Token
            String token = JwtTokenUtil.createToken(user.getUsername(), authorities.toString());

            // 设置编码 防止乱码问题
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");

            // 在请求头里返回创建成功的token
            // 设置请求头为带有"Kayn"前缀的token字符串
            response.setHeader("token", JwtTokenUtil.TOKEN_PREFIX + token);

            // 处理编码方式 防止中文乱码
            response.setContentType("text/json;charset=utf-8");

            // 将反馈塞到HttpServletResponse中返回给前台
            response.getWriter().write(JSON.toJSONString(new Result<UserInfo>()
                    .setSuccess(true)
                    .setCode(200)
                    .setMessage("success")
                    .setTimestamp(new Date().getTime())
                    .setResult(new UserInfo()
                            .setUsername(user.getUsername())
                            .setState(1)
                            .setToken(JwtTokenUtil.TOKEN_PREFIX + token))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证【失败】调用的方法
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        try {
            String returnData;
            // 账号过期
            if (failed instanceof AccountExpiredException) {
                returnData="账号过期";
            }
            // 密码错误
            else if (failed instanceof BadCredentialsException) {
                returnData="密码错误";
            }
            // 密码过期
            else if (failed instanceof CredentialsExpiredException) {
                returnData="密码过期";
            }
            // 账号不可用
            else if (failed instanceof DisabledException) {
                returnData="账号不可用";
            }
            //账号锁定
            else if (failed instanceof LockedException) {
                returnData="账号锁定";
            }
            // 用户不存在
            else if (failed instanceof InternalAuthenticationServiceException) {
                returnData="用户不存在";
            }
            // 其他错误
            else{
                returnData="未知异常";
            }

            // 处理编码方式 防止中文乱码
            response.setContentType("text/json;charset=utf-8");
            // 将反馈塞到HttpServletResponse中返回给前台
            response.getWriter().write(JSON.toJSONString(new Result<UserInfo>()
                    .setSuccess(false)
                    .setCode(500)
                    .setMessage(returnData)
                    .setTimestamp(new Date().getTime())
                    .setResult(null)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
