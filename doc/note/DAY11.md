# 69. 关于Spring Security框架

Spring Security框架主要解决了**认证**与**授权**相关的问题。

# 70. 添加Spring Boot Security依赖

在`csmall-passport`项目中添加依赖项：

```xml
<!-- Spring Boot Security的依赖项，用于处理认证与授权 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

当添加了以上依赖项后，当前项目会：

- 此依赖项中包含`BCryptPasswordEncoder`类，可以用于处理密码加密
- 所有请求都是必须通过认证的，在没有通过认证之前，任何请求都会被重定向到Spring Security内置的登录页面
  - 可以使用`user`作为用户名，使用启动项目时随机生成的UUID密码来登录
  - 当登录成功后，会自动重定向到此前尝试访问的页面
  - 当登录成功后，所有GET的异步请求允许访问，但POST的异步请求不允许访问（403错误）

当添加依赖后，在浏览器中尝试访问时还可能出现以下错误：

```
org.springframework.security.web.firewall.RequestRejectedException: The request was rejected because the header value "Idea-c968a669=03021799-4633-4321-9d0d-11b7ee08f588; username=é»æ±å; JSESSIONID=120F9329E0CE7AF9E052A302EFE494F2" is not allowed.
```

此错误是浏览器的问题导致的，更换浏览器即可。

# 71. 关于BCryptPasswordEncoder

BCrypt算法是用于对密码进行加密处理的，在`spring-boot-starter-security`中包含了`BCryptPasswordEncoder`，可以实现编码、验证：

```java
public class BCryptTests {

    @Test
    public void encode() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "123456";
        System.out.println("原文：" + rawPassword);

        for (int i = 0; i < 50; i++) {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            System.out.println("密文：" + encodedPassword);
        }
//        密文：$2a$10$H7neseWrkpdCQiW6R4bJyeXaU.nowsFZZz.iO4HCLzFScz.FdpDSG
//        密文：$2a$10$DoQQSh9eAxDRVKADzQ.Q8Oa4QqcpMUR9UmKyptop3i0mwsdfS.wyC
//        密文：$2a$10$tZCa3YIYehg5B9VESrDOWeoBAX3aX4f.Ioc4awtiY/vwihGmD.xQG
//        密文：$2a$10$9qx53wQEF0XjSjKattwEw.mFayMvjxLnZmPnRO5V1DnZvKuCLrVQG
//        密文：$2a$10$dmGQK7iwTd9Mbwa/mxzABeBHezbqyGpqwmxUobwelQDlRuW4oHS9e
    }

    @Test
    public void matches() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "123456";
        String encodedPassword = "$2a$10$H7neseWrkpdCQiW6R4bJyeXaU.nowsFZZz.iO4HCLzFScz.FdpDSG";

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("原文：" + rawPassword);
        System.out.println("密文：" + encodedPassword);
        System.out.println("验证：" + matches);
    }

}
```

BCrypt算法默认使用了随机盐值，所以，即使使用相同的原文，每次编码产生的密文都是不同的！

BCrypt算法被刻意设计为慢速的，所以，可以非常有限的避免穷举式的暴力破解！

# 72. 关于Spring Security的配置类

在Spring Boot项目中，在根包下创建`config.SecurityConfiguration`类，作为Spring Security的配置类，需要继承自`WebSecurityConfigurerAdapter`类，并重写其中的方法进行配置：

```java
package cn.tedu.csmall.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
/**
 * Spring Security配置类
 *
 * @author java@tedu.cn
 * @version 0.0.1
 */
@Slf4j
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public SecurityConfiguration() {
        log.debug("创建配置类对象：SecurityConfiguration");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 如果不调用父类方法，默认所有请求都不需要通过认证，可以直接访问
        // super.configure(http);

        // 白名单
        String[] urls = {
                "/favicon.ico",
                "/doc.html",
                "/**/*.js",
                "/**/*.css",
                "/swagger-resources",
                "/v2/api-docs"
        };

        // 将防止伪造跨域攻击的机制禁用
        http.csrf().disable();

        // 提示：关于请求路径的配置，如果同一路径对应多项配置规则，以第1次配置的为准
        http.authorizeRequests() // 管理请求授权
                .mvcMatchers(urls) // 匹配某些路径
                .permitAll() // 直接许可，即可不需要通过认证即可访问
                .anyRequest() // 除了以上配置过的以外的其它所有请求
                .authenticated(); // 要求是“已经通过认证的”

        // 启用登录表单
        // 当未认证时：
        // -- 如果启用了表单，会自动重定向到登录表单
        // -- 如果未启用表单，则会提示403错误
        http.formLogin();
    }

}
```

# 73. 关于伪造的跨域攻击

伪造的跨域攻击（CSRF）主要是基于服务器端对浏览器的信任，在多选项卡的浏览器中，如果在X选项卡中登录，在Y选项卡中的访问也会被视为“已登录”。

在Spring Security框架中，默认开启了“防止伪造的跨域攻击”的机制，其基本做法就是在POST请求中，要求客户端提交其随机生成的一个UUID值，例如，（在没有禁用防止伪造跨域攻击时）在Spring Security的登录页面中有：

```html
<input name="_csrf" type="hidden" value="b6dc65f8-e0cf-4907-bdaf-a5f19b759f93" />
```

以上代码中的`value`值就是一个UUID值，是前次GET请求时由服务器端响应的，服务器端会要求客户端携带此UUID来访问，否则，就会将请求视为伪造的跨域攻击行为！

# 74. 关于登录账号

默认情况下，Spring Security框架提供了默认的用户名`user`和启动时随机生成UUID密码，如果需要自定义登录账号，可以自定义类，实现`UserDetailsService`接口，重写接口中的如下方法：

```java
UserDetails loadUserByUsername(String username);
```

Spring Security框架在处理认证时，会自动根据提交的用户名（用户在登录表单中输入的用户名）来调用以上方法，以上方法应该返回匹配的用户详情（`UserDetails`类型的对象），接下来，Spring Security会自动根据用户详情（`UserDetails`对象）来完成认证过程，例如判断密码是否正确等。

可以在根包下创建`security.UserDetailsServiceImpl`类，在类上添加`@Service`注解，实现`UserDetailsService`接口，重写接口中定义的抽象方法：

```java
package cn.tedu.csmall.passport.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.debug("Spring Security框架自动调用UserDetailsServiceImpl中的loadUserByUsername方法，参数：{}", s);
        // 假设正确的用户名 / 密码分别是 root / 1234
        if ("root".equals(s)) {
            UserDetails userDetails = User.builder()
                    .username("root")
                    .password("1234")
                    .disabled(false)
                    .accountLocked(false) // 此项目未设计“账号锁定”的机制，固定为false
                    .accountExpired(false) // 此项目未设计“账号过期”的机制，固定为false
                    .credentialsExpired(false) // 此项目未设计“凭证锁定”的机制，固定为false
                    .authorities("暂时给出的假的权限标识") // 权限
                    .build();
            return userDetails;
        }
        return null;
    }

}
```

完成后，重启项目，首先，可以在启动日志中看到，Spring Security框架不再生成随机的UUID密码。

在Spring Security处理认证时，还会自动装配Spring容器中的密码编码器（`PasswordEncoder`），如果Spring容器中并没有密码编码器，则无法验证密码是否正确，当使用了正确的用户名尝试登录时，服务器端将报告错误：

```
java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
```

可以在`SecurityConfiguration`中添加`@Bean`方法，来配置所需的密码编码器：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
}
```

提示：以上使用的密码编码器是无操作的密码编码器（**No** **Op**eration），不会对密码进行加密处理，是不推荐的，所以，此类被声明为已过期，在IntelliJ IDEA中，此类的名称会有删除线。

当添加了密码编码器后，再次启用项目，尝试登录：

- 当用户名错误时，会提示`UserDetailsService`返回了是`null`
- 当用户名正确，但密码错误时，会提示登录失败
- 当用户名、密码均正确时，将成功登录

也可以将以上`NoOpPasswordEncoder`换成`BCryptPasswordEncoder`，例如：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    // return NoOpPasswordEncoder.getInstance();
    return new BCryptPasswordEncoder();
}
```

如果修改，则`UserDetails`对象中封装的密码也必须是与此密码编码器符合的，即必须是BCrypt算法加密的结果，例如：

```java
@Override
public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    log.debug("Spring Security框架自动调用UserDetailsServiceImpl中的loadUserByUsername方法，参数：{}", s);
    // 假设正确的用户名 / 密码分别是 root / 1234
    if ("root".equals(s)) {
        UserDetails userDetails = User.builder()
                .username("root")
                .password("$2a$10$DoQQSh9eAxDRVKADzQ.Q8Oa4QqcpMUR9UmKyptop3i0mwsdfS.wyC")
                
            	// 后续代码没有调整……
```

# 75. 使用数据库中的管理员账号登录

只需要保证在`UserDetailsServiceImpl`类中，返回的是数据库中对应的管理员信息即可！

所以，需要在Mapper层实现“根据用户名查询用户的登录信息”的功能，需要执行的SQL语句大致是：

```mysql
SELECT id, username, password, enable FROM ams_admin WHERE username=?
```

在根包下创建`pojo.vo.AdminLoginInfoVO`类：

```java

```

在`AdminMapper.java`接口中添加抽象方法：

```java
AdminLoginInfoVO getLoginInfoByUsername(String username);
```

在`AdminMapper.xml`中配置：

```xml

```

在`AdminMapperTests`中测试：

```java

```

完成后，调整`UserDetailsServiceImpl`中的实现：

- 如果数据库中没有匹配的管理员信息，可以返回`null`（或抛出异常等）
- 如果数据库中存在匹配的管理员信息，则用于封装`UserDetails`对象，并返回此对象

具体代码：

```java
package cn.tedu.csmall.passport.security;

import cn.tedu.csmall.passport.mapper.AdminMapper;
import cn.tedu.csmall.passport.pojo.vo.AdminLoginInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.debug("Spring Security框架自动调用UserDetailsServiceImpl中的loadUserByUsername方法，参数：{}", s);
        AdminLoginInfoVO admin = adminMapper.getLoginInfoByUsername(s);
        log.debug("从数据库中根据用户名【{}】查询管理员信息，结果：{}", s, admin);
        if (admin == null) {
            log.debug("没有与用户名【{}】匹配的管理员信息，即将抛出BadCredentialsException", s);
            String message = "登录失败，用户名不存在！";
            throw new BadCredentialsException(message);
        }

        UserDetails userDetails = User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .disabled(admin.getEnable() == 0)
                .accountLocked(false) // 此项目未设计“账号锁定”的机制，固定为false
                .accountExpired(false) // 此项目未设计“账号过期”的机制，固定为false
                .credentialsExpired(false) // 此项目未设计“凭证锁定”的机制，固定为false
                .authorities("暂时给出的假的权限标识") // 权限
                .build();
        log.debug("即将向Spring Security框架返回UserDetails对象：{}", userDetails);
        return userDetails;
    }

}
```

# 76. 使用前后端分离的登录模式

目前的登录是由Spring Security提供了登录表单，然后由自定义的`UserDetailsServiceImpl`获取对应的用户信息，并由Spring Security完后后续的认证过程，以此来实现的，这**不是**前后端分离的开发模式，因为依赖于Spring Security提供的登录表单，例如`csmall-web-client`或其它客户端根本没有办法像服务器端发送登录请求！

要实现前后端分离的登录模式，需要：

- 使用控制器接收来自客户端的登录请求
  - 创建`AdminLoginDTO`封装客户端提交的用户名、密码
  - 所设计的登录请求的URL必须添加到“白名单”
- 使用Service处理登录认证
  - 调用`AuthenticationManager`的`authenticate()`方法处理认证
    - 可以通过重写配置类中的`authenticationManagerBean()`方法，并添加`@Bean`注解来得到

**【AdminLoginDTO】**

```java
package cn.tedu.csmall.passport.pojo.dto;

@Data
public class AdminLoginDTO implements Serializable {

    private String username;
    private String password;

}
```

**【SecurityConfiguration】**

```java
@Bean
@Override
public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
}
```

**【IAdminService】**

```java
void login(AdminLoginDTO adminLoginDTO);
```

**【AdminServiceImpl】**

```java
@Autowired
private AuthenticationManager authenticationManager;

@Override
public void login(AdminLoginDTO adminLoginDTO) {
    log.debug("开始处理【管理员登录】的业务，参数：{}", adminLoginDTO);
    Authentication authentication
            = new UsernamePasswordAuthenticationToken(
                    adminLoginDTO.getUsername(), adminLoginDTO.getPassword());
    authenticationManager.authenticate(authentication);
}
```

**【AdminController】**

```java
// http://localhost:9081/admins/login
@ApiOperation("管理员登录")
@ApiOperationSupport(order = 50)
@PostMapping("/login")
public JsonResult<Void> login(AdminLoginDTO adminLoginDTO) {
    log.debug("开始处理【管理员登录】的请求，参数：{}", adminLoginDTO);
    adminService.login(adminLoginDTO);
    return JsonResult.ok();
}
```

**注意：强烈建议禁用Spring Security的登录表单！**

完成后，重启项目，可以通过API文档向 http://localhost:9081/admins/login 提交请求，如果用户名或密码错误，都会导致403错误，如果用户名和密码均正确，则会响应`state`为`20000`的JSON结果。

最后，在全局异常处理器中，补充对相关异常的处理：

```java
@ExceptionHandler({
        InternalAuthenticationServiceException.class, // AuthenticationServiceException >>> AuthenticationException
        BadCredentialsException.class // AuthenticationException
})
public JsonResult<Void> handleAuthenticationException(AuthenticationException e) {
    log.debug("捕获到AuthenticationException");
    log.debug("异常类型：{}", e.getClass().getName());
    log.debug("异常消息：{}", e.getMessage());
    String message = "登录失败，用户名或密码错！";
    return JsonResult.fail(ServiceCode.ERR_UNAUTHORIZED, message);
}

@ExceptionHandler
public JsonResult<Void> handleDisabledException(DisabledException e) {
    log.debug("捕获到DisabledException");
    String message = "登录失败，此账号已经被禁用！";
    return JsonResult.fail(ServiceCode.ERR_UNAUTHORIZED_DISABLED, message);
}

@ExceptionHandler
public String handleThrowable(Throwable e) {
    String message = "你有异常没有处理，请根据服务器端控制台的信息，补充对此类异常的处理！！！";
    e.printStackTrace();
    return message;
}
```



























# 77. 简历技术描述参考

- 【了解/掌握/熟练掌握】开发工具的使用，包括：Eclipse、IntelliJ IDEA、Git、Maven；
- 【了解/掌握/熟练掌握】Java语法，【理解/深刻理解】面向对象编程思想，【了解/掌握/熟练掌握】Java SE API，包括：String、日期、IO、反射、线程、网络编程、集合、异常等；
- 【了解/掌握/熟练掌握】HTML、CSS、JavaScript前端技术，并【了解/掌握/熟练掌握】前端相关框架技术及常用工具组件，包括：jQuery、Bootstrap、Vue脚手架、Element UI、axios、qs、富文本编辑器等；
- 【了解/掌握/熟练掌握】MySQL的应用，【了解/掌握/熟练掌握】DDL、DML的规范使用；
- 【了解/掌握/熟练掌握】数据库编程技术，包括：JDBC、数据库连接池（commons-dbcp、commons-dbcp2、Hikari、druid），及相关框架技术，例如：Mybatis Plus等；
- 【了解/掌握/熟练掌握】主流框架技术的规范使用，例如：SSM（Spring，Spring MVC， Mybatis）、Spring Boot、Spring Validation、Spring Security等；
- 【理解/深刻理解】Java开发规范（参考阿里巴巴的Java开发手册）；
- 【了解/掌握/熟练掌握】基于RESTful的Web应用程序开发；
- 【了解/掌握/熟练掌握】基于Spring Security与JWT实现单点登录；







