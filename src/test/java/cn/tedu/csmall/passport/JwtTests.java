package cn.tedu.csmall.passport;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试JWT的生成和解析
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
@SpringBootTest
public class JwtTests {

    @Value("${csmall.jwt.secret-key}")
    private String secretKey;

    // 生成JWT
    @Test
    public void generate() {
        // 该值应当是保密值,不能外泄
        Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 60);// 设置有效时间,直接new表示当前时间

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 9527);
        claims.put("username", "fanchuanqi");

        String jwt = Jwts.builder() // 构建者模式(通常用于设置多种参数)
                // 头部Header--固定值
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                // 载荷PayLoad
                .setClaims(claims) // 传入要放的数据
                // 签名Signature
                .setExpiration(date) // 有效时间
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        System.out.println(jwt);// 生成Token----相当于有了火车票
        // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OTUyNywiZXhwIjoxNjY3ODc5MDU5LCJ1c2VybmFtZSI6ImZhbmNodWFucWkifQ.ZcMcg9XtUF0Q1-gOOjP3mAr4hMh2fFYE8r6cB20t50Q
    }

    // 解析JWT
    @Test
    public void parse() {
        // 解析前需准备之前生成的JWT,和签名
        String jwt = "eyJhbGciOiJIUzI1NiIsInRycCI6IkpXVCJ9.eyJleHAiOjE2NjgzMjE1NjIsInVzZXJuYW1lIjoicm9vdCJ9._bSGd2e7fZ8XtWYzVJrxqSJ0dHaNqF4J0tPmSs6EXZk";
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 设置需要解析的JWT签名
                .parseClaimsJws(jwt) // 拿到需要解析的JWT代码
                .getBody();

        // <T> T get(String var1, Class<T> var2)该重载可返回自定义的数据类型
        Long id = claims.get("id",Long.class);
        String username = claims.get("username",String.class);
        System.out.println("id=" + id + ";用户名=" + username);
    }
}
