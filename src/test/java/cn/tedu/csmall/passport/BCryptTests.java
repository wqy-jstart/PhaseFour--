package cn.tedu.csmall.passport;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class BCryptTests {

    // spring-boot-starter-security
    // 对原文进行BCrypt加密处理
    @Test
    public void encode(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "123456";
        System.out.println("原文:"+rawPassword);
        for (int i = 0; i < 50; i++) {
            String encodePassword = passwordEncoder.encode(rawPassword);
            System.out.println("密文:"+encodePassword);
        }
//        密文:$2a$10$55Ov/NaAt7ZAivHTd./Wse/9hD8oxLK4AQ8rSLOlCI08nnZiFSzSy
//        密文:$2a$10$E3qfyfcFd0ebIfAVK7t6kuxhIvPrZqmmhSg7smOOpXmuoZTnNhQX6
//        密文:$2a$10$gdomQWHp.p5z89YC2eff/ulLuuBZ8QKAXcAkQxPWRGH6xahoxWmoC
//        密文:$2a$10$41V6BJDjxE5atwUTUp1mPuH3r3kAa6Jki5Q37MmL8yy8ET/CdDxiG
//        密文:$2a$10$i4S5o/cWHeZhOo1JMnfNsOZdhJIX1obMEP1tblMn3.HAyYzEiPdd.
//        密文:$2a$10$L4c/Pd3OsWgIlwIPfs5oCuriTlS1jzk6GGYhjPtJkpcZSSFuTiDO6
//        密文:$2a$10$3AMhnERUXo/Pz9/7jWqe2OvUhEOL3w8n/Qtnlnuzeck8GkfQ0FqPe
    }

    // 判断原文与密文是否匹配
    @Test
    public void matches(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "123456";
        String encodedPassword = "$2a$10$55Ov/NaAt7ZAivHTd./Wse/9hD8oxLK4AQ8rSLOlCI08nnZiFSzSy";

        boolean matches = passwordEncoder.matches(rawPassword,encodedPassword);
        System.out.println("原文：" + rawPassword);
        System.out.println("密文：" + encodedPassword);
        System.out.println("验证：" + matches);
    }
}
