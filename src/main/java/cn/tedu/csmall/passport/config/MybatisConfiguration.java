package cn.tedu.csmall.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
@Slf4j
@Configuration
@MapperScan("cn.tedu.csmall.passport.mapper")
public class MybatisConfiguration {
    //测试配置是否加载成功,输出对应日志内容
    public MybatisConfiguration(){
        log.info("创建配置类对象:MybatisConfiguration");
    }
}
