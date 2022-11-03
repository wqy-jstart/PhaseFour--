package cn.tedu.csmall.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 解决关于服务器端允许来自跨域的客户端发送请求,作出的相关配置
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
@Slf4j
@Configuration//该注解用于配置类可在项目启动时对该类进行加载
public class WebMvcConfiguration implements WebMvcConfigurer {

    public WebMvcConfiguration() {
        log.debug("创建配置类对象:WebMvcConfiguration");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
