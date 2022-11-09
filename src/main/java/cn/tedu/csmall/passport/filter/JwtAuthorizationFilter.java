package cn.tedu.csmall.passport.filter;

import cn.tedu.csmall.passport.web.JsonResult;
import cn.tedu.csmall.passport.web.ServiceCode;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.PrintWriter;
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

    @Value("${csmall.jwt.secret-key}")
    private String secretKey;

    public static final int JWT_MIN_LENGTH = 113;// 当前项目中JWT最短的值

    public JwtAuthorizationFilter() {
        log.debug("创建过滤器对象:JwtAuthorizationFilter");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtAuthorizationFilter开始执行过滤...");

        // 获取请求头'Authorization'中的JWT内容
        String jwt = request.getHeader("Authorization");// Authorization授权,批准
        log.debug("获取客户端携带的JWT:{}", jwt);

        // 检查是否获取到了基本有效的JWT,StringUtils工具类中的hasText(String str)判断不为null,不为空串,且包含文本
        if (!StringUtils.hasText(jwt) || jwt.length() < JWT_MIN_LENGTH) {
            // 对于无效的JWT,直接放行,交由后续的组件进行处理
            log.debug("获取到的JWT被视为无效,当前过滤器将放行....");
            filterChain.doFilter(request, response);
            return;
        }

        // 尝试解析JWT(在解析时应当手动捕获相应的异常,否则无法通过过滤器,业务也不会执行)
        log.debug("获取到的JWT被视为有效,准备解析JWT...");
        response.setContentType("application/json; charset=utf-8");// 利用response设置响应类型(这里是UTF-8的json类型)
        Claims claims;
        try {
            claims = Jwts.parser() // 调用解析器,传入签名和jwt
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt)
                    .getBody();
        }catch (SignatureException e){
            log.debug("解析JWT时出现SignatureException---JWT签名不匹配!");
            String message = "非法访问!";
            // new一个JsonResult用来向客户端返回Json数据
            JsonResult<Void> jsonResult = JsonResult.fail(ServiceCode.ERR_JWT_SIGNATURE,message);
            // 引入FastJson框架依赖,调用方法将结果转换为JSON格式数据并输出给客户端
            String jsonResultString = JSON.toJSONString(jsonResult);
            PrintWriter writer = response.getWriter();// 利用response获取输出字符流
            writer.println(jsonResultString);// 向浏览器输出message
            return;
        }catch (MalformedJwtException e){
            log.debug("解析JWT时出现MalformedJwtException---JWT数据有误!");
            String message = "非法访问!";
            JsonResult<Void> jsonResult = JsonResult.fail(ServiceCode.ERR_JWT_MALFORMED,message);
            String jsonResultString = JSON.toJSONString(jsonResult);
            PrintWriter writer = response.getWriter();// 利用response获取输出字符流
            writer.println(jsonResultString);// 向浏览器输出message
            return;
        }catch (ExpiredJwtException e){
            log.debug("解析JWT时出现ExpiredJwtException---JWT已过期!");
            String message = "登录信息已过期，请重新登录！";
            JsonResult<Void> jsonResult = JsonResult.fail(ServiceCode.ERR_JWT_EXPIRED,message);
            String jsonResultString = JSON.toJSONString(jsonResult);
            PrintWriter writer = response.getWriter();// 利用response获取输出字符流
            writer.println(jsonResultString);// 向浏览器输出message
            return;
        }catch (Throwable e){
            log.debug("解析JWT时出现Throwable，需要开发人员在JWT过滤器补充对异常的处理");
            e.printStackTrace();// 打印相关异常信息
            String message = "你有异常没有处理，请根据服务器端控制台的信息，补充对此类异常的处理！！！";
            PrintWriter writer = response.getWriter();// 利用response获取输出字符流
            writer.println(message);// 向浏览器输出message
            return;
        }

        // 获取解析JWT后获取的用户名
        String username = claims.get("username",String.class);

        // 添加权限信息
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("这是一个假权限");
        authorities.add(authority);

        // 创建一个UsernamePasswordAuthenticationToken,传入用户名和权限信息,返回Authentication认证器对象
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(
                        username,null,authorities);

        // 将Authentication对象的引用存入到SecurityContext上下文中(Spring规定)
        log.debug("向SecurityContext中存入认证信息:{}",authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);// 将认证信息放到Security Context上下文中

        // 过滤器链继续向后传递,即:放行
        filterChain.doFilter(request, response);
    }
}
