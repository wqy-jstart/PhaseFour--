package cn.tedu.csmall.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;

/**
 * 这是Redis的配置类
 *
 * @Author java@Wqy
 * @Version 0.0.1
 */
@Slf4j
@Configuration
public class RedisConfiguration {

    public RedisConfiguration(){
        log.debug("创建配置类对象:RedisConfiguration");
    }

    // 添加@Bean注解,向Spring容器中创建该对象,交由Spring管理,使用时可以进行自动装配
    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(
            RedisConnectionFactory redisConnectionFactory
    ){ // 配置Redis连接工厂
        RedisTemplate<String,Serializable> redisTemplate = new RedisTemplate<>();// 1.创建一个Redis模板
        redisTemplate.setConnectionFactory(redisConnectionFactory);// 2.设置Redis连接工厂

        redisTemplate.setKeySerializer(RedisSerializer.string());// 3.创建String序列化器,序列化key
        redisTemplate.setValueSerializer(RedisSerializer.json());// 4.创建JSON序列化器,序列化value值部分
        return redisTemplate;// 5.返回配置后的Redis模板
    }
}
