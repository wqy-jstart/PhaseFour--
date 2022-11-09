package cn.tedu.csmall.passport;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

/**
 * 测试Spring中Environment接口中的getProperty(String key)
 * 传入自定义配置路径也可得到对应配置的值
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
@Slf4j
@SpringBootTest
public class PropertyTests {
    @Value("${csmall.jwt.secret-key}")
    String data;

    @Autowired
    Environment env;

    @Test
    void test(){
        log.debug("从配置文件中读取到的配置值：{}", data);
        log.debug("从Environment中读取到的配置值：{}", env.getProperty("csmall.jwt.secret-key"));
    }
}
