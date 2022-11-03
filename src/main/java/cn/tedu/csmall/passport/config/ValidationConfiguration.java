package cn.tedu.csmall.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;

/**
 * 创建Validation配置类
 *
 * @Author java@.Wqy
 * @Version 0.0.1
 */
@Slf4j
@Configuration
public class ValidationConfiguration {

    public ValidationConfiguration(){
        log.debug("创建配置类对象:ValidationConfiguration");
    }

    @Bean//该注解会将创建的对象传给Spring框架,用来解决响应问题
    public javax.validation.Validator validator(){
        return Validation.byProvider(HibernateValidator.class)
                .configure() // 开始配置
                .failFast(true) // 快速失败,即检查请求参数时,一旦发现某个参数不符合规则,则视为失败并停止检查（剩余未检查的部分将不会被检查）
                .buildValidatorFactory()
                .getValidator();
    }
}
