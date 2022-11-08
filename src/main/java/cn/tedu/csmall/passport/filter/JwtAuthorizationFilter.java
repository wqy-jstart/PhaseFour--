package cn.tedu.csmall.passport.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ★JWT过滤器---该过滤器仅对客户端再次登录携带的JWT做检查,后续不管
 *
 * <p>JWT过滤器</p>
 *
 * <p>此JWT的主要作用：</p>
 * <ul>
 *     <li>获取客户端携带的JWT，惯用做法是：客户端应该通过请求头中的Authorization属性来携带JWT</li>
 *     <li>解析客户端携带的JWT，并创建出Authentication对象，存入到SecurityContext中</li>
 * </ul>
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
@Slf4j
@Component // 声明是一个组件,便于Spring管理
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final int JWT_MIN_LENGTH = 113;// 当前项目中JWT最短的值

    public JwtAuthorizationFilter() {
        log.debug("创建过滤器对象:JwtAuthorizationFilter");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtAuthorizationFilter开始执行过滤...");

        // 获取参数名称为'Authorization'请求头中的JWT
        String jwt = request.getHeader("Authorization");// Authorization授权,批准
        log.debug("获取客户端携带的JWT:{}", jwt);

        // 检查是否获取到了基本有效的JWT,StringUtils工具类中的hasText(String str)判断不为null,不为空串,且包含文本
        if (!StringUtils.hasText(jwt) || jwt.length() < JWT_MIN_LENGTH) {
            // 对于无效的JWT,直接放行,交由后续的组件进行处理
            log.debug("获取到的JWT被视为无效,当前过滤器将放行....");
            filterChain.doFilter(request, response);
            return;
        }

        // 尝试解析JWT
        log.debug("获取到的JWT被视为有效,准备解析JWT...");
        String secretKey = "a9F8ujGDhjgFvfEd3SA90ukDS";// 类似于盐值
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();

        // 获取JWT中的管理员信息
        String username = claims.get("username",String.class);

        // 处理权限信息
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("这是一个假权限");
        authorities.add(authority);

        // new一个用户名密码认证Token,传入用户名,返回Authentication认证器对象
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(username,null,authorities);

        // 将Authentication对象存入到SecurityContext中(规定)
        log.debug("向SecurityContext中存入认证信息:{}",authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 过滤器链继续向后传递,即:放行
        filterChain.doFilter(request, response);
    }
}
