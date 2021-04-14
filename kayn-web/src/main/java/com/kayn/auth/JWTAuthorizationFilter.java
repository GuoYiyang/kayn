package com.kayn.auth;

import com.kayn.util.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 鉴权
 * JWTAuthorizationFilter 权限校验拦截器
 * 当访问需要权限校验的URL(当然 该URL也是需要经过配置的) 则会来到此拦截器 在该拦截器中对传来的Token进行校验
 * 只需告诉Spring security该用户是否已登录 并且是什么角色 拥有什么权限即可
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    /**
     * 构造函数
     * @param authenticationManager authenticationManager
     */
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * 在过滤之前和之后执行的事件
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            String tokenHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
            // 若请求头中没有Authorization信息 或是 Authorization 不以 Kayn 开头 则直接放行
            if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
                chain.doFilter(request, response);
                return;
            }
            // 若请求头中有token 则调用下面的方法进行解析 并设置认证信息
            SecurityContextHolder.getContext()
                    .setAuthentication(getAuthentication(tokenHeader));
            super.doFilterInternal(request, response, chain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从token中获取用户信息并新建一个token
     * @param tokenHeader 字符串形式的Token请求头
     * @return 带用户名和密码以及权限的Authentication
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) {
        // 去掉前缀 获取Token字符串
        String token = tokenHeader.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        // 从Token中解密获取用户名
        String username = JwtTokenUtil.getUsername(token);
        // 从Token中解密获取用户角色
        String role = JwtTokenUtil.getUserRole(token);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        if (username != null) {
            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        }
        return null;
    }
}
