# 86. 关于复杂请求的PreFlight

PreFight：预检

当客户端提交的请求自定义了请求头，且请求头中的属性不是常规属性时（例如`Authorization`就不是常规属性），这类请求会被视为**复杂请求**，就会触发预检（PreFlight）机制，浏览器会自动向对应的URL提交一个`OPTIONS`类型的请求，如果此请求被正常响应（即HTTP响应码为`200`），才可以正常提交原本的请求，否则，视为预检失败，会提示跨域错误。

需要注意：预检是基于浏览器缓存的，如果某个请求对应的URL曾经预检通过，则后续再次提交请求时不会执行预检！

在服务器端的`SecurityConfiguration`中，在重写的`void configurer(HttpSecurity http)`方法中，配置请求认证时，对所有`OPTIONS`请求直接放行，即可解决预检不通过导致的跨域错误，例如：

```java
http.authorizeRequests()

    // ↓↓↓↓↓ 对所有OPTIONS请求直接放行 ↓↓↓↓↓
    .mvcMatchers(HttpMethod.OPTIONS, "/**")
    .permitAll()

    .mvcMatchers(urls)
    .permitAll()
    .anyRequest()
    .authenticated();
```

或者，更简单一点，直接调用参数`http`的`cors()`方法，则Spring Security会自动启用一个`CorsFilter`，这是Spring Security专门用于处理跨域问题的过滤器，也会对`OPTIONS`请求放行，所以，实现的效果是完全相同的！

注意：以上解决方案并不能取代目前使用`WebMvcConfiguration`解决的跨域问题！

# 87. 使用配置文件自定义JWT参数

生成和解析JWT都需要使用到`secretKey`，并且，这2处使用到的`secretKey`值必须是完全相同的！所以，应该使用一个公共的位置来配置`secretKey`的值，由于此值应该允许被客户（软件的使用者）修改，则应该将此值定义的配置文件中（不推荐定义在某个类）。

则可以在`application-dev.yml`中添加自定义配置：

```yaml
# 当前项目中的自定义配置
csmall:
  # JWT相关配置
  jwt:
    # 生成和解析JWT时使用的secretKey
    secret-key: a9F8ujGDhjgFvfEd3SA90ukDS
    # JWT的有效时长，以分钟为单位
    duration-in-minute: 14400
```

提示：当在`.yml`或`.properties`中添加配置后（无论是否为自定义配置），当加载时，会将这些配置读取到Spring框架内置的`Environment`对象中，另外，操作系统的配置和JVM配置也会自动读取到`Environment`中，且配置文件中的配置的优先级是最低的（会被覆盖），使用`@Value`读取值，其实是从`Environment`中读取的，并不是直接从配置文件中读取的！

添加配置后，在生成JWT时使用，即在`AdminServiceImpl`中声明2个全局属性，通过`@Value`注解为这2个属性注入配置的值：

```java
@Value("${csmall.jwt.secret-key}")
private String secretKey;
@Value("${csmall.jwt.duration-in-minute}")
private long durationInMinute;
```

在生成JWT时，就可以直接使用这2个属性了！

另外，在`JwtAuthorizationFilter`也应该使用同样的做法，应用`secretKey`的配置！

# 88. 处理解析JWT时可能出现的异常

当前项目中使用过滤器解析JWT，而过滤器是JAVA EE项目中最早接收到请求的组件，此时其它组件（例如Controller）均未开始处理此请求，所以，如果过滤器在解析JWT时出现异常，Controller是无法“知晓”的，则全局异常处理器也无法处理这些异常，只能在过滤器中使用`try...catch`语法处理。

处理异常后的响应应该是JSON格式的，当前项目中一直在使用`JsonResult`表示响应的结果，但是，由于过滤器解析JWT时，Spring MVC的相关组件尚未运行，无法自动将`JsonResult`对象转换成JSON格式的字符串，所以，需要先在项目中添加依赖项，用于将对象转换成JSON格式的字符串：

```xml
<!-- fastjson：实现对象与JSON的相互转换 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.75</version>
</dependency>
```

然后，在`ServiceCode`中添加新的枚举值：

```java
public enum ServiceCode {

    // 此前已有的枚举值
    
    // ↓↓↓↓↓  新增的枚举值  ↓↓↓↓↓
    /**
     * 错误：JWT签名错误
     */
    ERR_JWT_SIGNATURE(60000),
    /**
     * 错误：JWT数据格式错误
     */
    ERR_JWT_MALFORMED(60100),
    /**
     * 错误：JWT已过期
     */
    ERR_JWT_EXPIRED(60200);
    
    // 其它代码
```

最后，在`JwtAuthorizationFilter`中，使用`try...catch`包裹解析JWT的代码，并处理相关异常：

```java
// 尝试解析JWT
log.debug("获取到的JWT被视为有效，准备解析JWT……");
response.setContentType("application/json; charset=utf-8");
Claims claims = null;
try {
    claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(jwt)
            .getBody();
} catch (SignatureException e) {
    log.debug("解析JWT时出现SignatureException");
    String message = "非法访问！";
    JsonResult<Void> jsonResult = JsonResult.fail(
        								ServiceCode.ERR_JWT_SIGNATURE, message);
    String jsonResultString = JSON.toJSONString(jsonResult);
    PrintWriter writer = response.getWriter();
    writer.println(jsonResultString);
    return;
} catch (MalformedJwtException e) {
    log.debug("解析JWT时出现MalformedJwtException");
    String message = "非法访问！";
    JsonResult<Void> jsonResult = JsonResult.fail(
        								ServiceCode.ERR_JWT_MALFORMED, message);
    String jsonResultString = JSON.toJSONString(jsonResult);
    PrintWriter writer = response.getWriter();
    writer.println(jsonResultString);
    return;
} catch (ExpiredJwtException e) {
    log.debug("解析JWT时出现ExpiredJwtException");
    String message = "登录信息已过期，请重新登录！";
    JsonResult<Void> jsonResult = JsonResult.fail(
        								ServiceCode.ERR_JWT_EXPIRED, message);
    String jsonResultString = JSON.toJSONString(jsonResult);
    PrintWriter writer = response.getWriter();
    writer.println(jsonResultString);
    return;
} catch (Throwable e) {
    log.debug("解析JWT时出现Throwable，需要开发人员在JWT过滤器补充对异常的处理");
    e.printStackTrace();
    String message = "你有异常没有处理，请根据服务器端控制台的信息，补充对此类异常的处理！！！";
    PrintWriter writer = response.getWriter();
    writer.println(message);
    return;
}
```

# 89. 将登录的管理员的id封装到认证信息中

在根包下创建`security.AdminDetails`类，继承自`User`类，并在类中扩展声明`Long id`属性：

```java
package cn.tedu.csmall.passport.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 管理员详情类，是Spring Security框架的loadUserByUsername()的返回结果
 *
 * @author java@tedu.cn
 * @version 0.0.1
 */
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode
public class AdminDetails extends User {

    private Long id;

    public AdminDetails(Long id,
                        String username,
                        String password,
                        boolean enabled,
                        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true,
                true, true, authorities);
        this.id = id;
    }

}
```

在`UserDetailsService`中，当需要返回`UserDetails`对象时，返回以上自定义的对象：

```java
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

        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("这是一个假权限");
        authorities.add(authority);

        AdminDetails adminDetails = new AdminDetails(
                admin.getId(),
                admin.getUsername(),
                admin.getPassword(),
                admin.getEnable() == 1,
                authorities);
        log.debug("即将向Spring Security框架返回UserDetails对象：{}", adminDetails);
        return adminDetails;
    }

}
```

至此，当用户成功登录后，`AuthenticationManager`的`authenticate()`返回的认证信息中的当事人（Principal）就是以上返回的`AdminDetails`，其中是包含`id`和用户名等信息的！在处理认证后，可以得到这些信息，并用于生成JWT。

在`AdminServiceImpl`的`login()`方法：

```java
// 从认证结果中获取所需的数据，将用于生成JWT
Object principal = authenticateResult.getPrincipal();
log.debug("认证结果中的当事人类型：{}", principal.getClass().getName());
AdminDetails adminDetails = (AdminDetails) principal;
String username = adminDetails.getUsername();
Long id = adminDetails.getId(); // 新增

// 生成JWT数据时，需要填充装到JWT中的数据
Map<String, Object> claims = new HashMap<>();
claims.put("id", id);  // 新增
claims.put("username", username);
```

至此，当用户成功登录后，得到的JWT中是包含了`id`和`username`的！

要将`id`和`username`同时封装到认证信息中，由于认证信息中的当事人只是1个数据，如果要将`id`和`username`这2个数据都封装进去，就需要自定义类，在类定义这2个属性，此类的对象将是最终存入到认证信息中的当事人：

```java
package cn.tedu.csmall.passport.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginPrincipal implements Serializable {

    private Long id;
    private String username;

}
```

在`JwtAuthorizationFilter`中：

```java
// 获取JWT中的管理员信息
String username = claims.get("username", String.class);
Long id = claims.get("id", Long.class); // 新增

// 处理权限信息
// 省略相关代码

// 创建Authentication对象
LoginPrincipal loginPrincipal = new LoginPrincipal(id, username);  // 新增
Authentication authentication
        = new UsernamePasswordAuthenticationToken(
            loginPrincipal, null, authorities);
//          ↑↑↑↑↑ 调整 ↑↑↑↑↑

// 将Authentication对象存入到SecurityContext
log.debug("向SecurityContext中存入认证信息：{}", authentication);
SecurityContextHolder.getContext().setAuthentication(authentication);
```

# 90. 在处理请求时识别当前登录的用户身份

在任何处理请求的方法的参数列表中，都可以添加`@AuthenticationPrincipal LoginPrincipal loginPrincipal`参数，Spring Security框架会自动从上下文（`SecurityContext`）中获取认证信息中的当事人，作为此参数的值！所以，在处理请求时，可以知晓当前登录的用户的`id`、`username`，例如：

```java
// http://localhost:9081/admins
@ApiOperation("查询管理员列表")
@ApiOperationSupport(order = 420)
@GetMapping("")
public JsonResult<List<AdminListItemVO>> list(
    	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓  新增  ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        @ApiIgnore @AuthenticationPrincipal LoginPrincipal loginPrincipal) {
    log.debug("开始处理【查询管理员列表】的请求，无参数");
    log.debug("当前登录的当事人：{}", loginPrincipal);
    List<AdminListItemVO> list = adminService.list();
    return JsonResult.ok(list);
}
```

提示：以上`@ApiIgnore`注解用于避免API文档中提示要求客户端提交`id`和`username`。

# 处理权限

需要在处理认证（登录）时，根据用户名查询管理员详情时，一并查询出此管理员的权限信息，需要执行的SQL语句大致是：

```mysql
SELECT
    ams_admin.id,
    ams_admin.username,
    ams_admin.password,
    ams_admin.enable,
    ams_permission.value
FROM ams_admin
LEFT JOIN ams_admin_role ON ams_admin.id=ams_admin_role.admin_id
LEFT JOIN ams_role_permission ON ams_admin_role.role_id=ams_role_permission.role_id
LEFT JOIN ams_permission ON ams_role_permission.permission_id=ams_permission.id
WHERE username='wangkejing';
```

要保证执行认证时能够查询到管理员的基本信息和权限，需要：

- 在`AdminLoginInfoVO`中添加属性，表示此管理员的权限
- `AdminMapper.java`接口中的抽象方法**不必调整**
- 在`AdminMapper.xml`中调整SQL语句，及如何封装查询结果

先在`AdminLoginInfoVO`中添加：

```java
/**
 * 权限列表
 */
private List<String> permissions;
```

然后在`AdminMapper.xml`中调整配置：

```xml
<!-- AdminLoginInfoVO getLoginInfoByUsername(String username); -->
<select id="getLoginInfoByUsername" resultMap="LoginResultMap">
    SELECT
        <include refid="LoginQueryFields" />
    FROM
        ams_admin
    LEFT JOIN ams_admin_role
        ON ams_admin.id=ams_admin_role.admin_id
    LEFT JOIN ams_role_permission
        ON ams_admin_role.role_id=ams_role_permission.role_id
    LEFT JOIN ams_permission
        ON ams_role_permission.permission_id=ams_permission.id
    WHERE
        username=#{username}
</select>

<sql id="LoginQueryFields">
    <if test="true">
        ams_admin.id,
        ams_admin.username,
        ams_admin.password,
        ams_admin.enable,
        ams_permission.value
    </if>
</sql>

<!-- 在1对多的查询中，List属性需要使用collection标签来配置 -->
<!-- collection标签的property属性：封装查询结果的类型中的属性名，即List的属性名 -->
<!-- collection标签的ofType属性：List的元素数据类型，取值为类型的全限定名 -->
<!-- collection标签的子级：如何将查询结果中的数据封装成ofType类型的对象 -->
<resultMap id="LoginResultMap" type="cn.tedu.csmall.passport.pojo.vo.AdminLoginInfoVO">
    <id column="id" property="id"/>
    <result column="username" property="username"/>
    <result column="password" property="password"/>
    <result column="enable" property="enable"/>
    <collection property="permissions" ofType="String">
        <constructor>
            <arg column="value"/>
        </constructor>
    </collection>
</resultMap>
```

接下来，应该将管理员的权限存入到`SecurityContext`中！需要：

- 在`UserDetailsServiceImpl`中返回的对象中需要包含真实的权限信息
- 在`AdminServiceImpl`中，认证通过后，从返回的结果中获取权限信息，并将其转换为JSON格式的字符串，存入到JWT中
- 在`JwtAuthorizationFilter`中，解析JWT成功后，获取权限信息对应的JSON字符串，并将其反序列化为`Collection<? extends GrantedAuthority>`格式，并存入到`Authentication`中，进而存入到`SecurityContext`中



以上全部完成后，就可以开始配置权限了！需要先在配置类（强烈建议`SecurityConfiguration`）上添加`@EnableGlobalMethodSecurity(prePostEnabled = true)`注解，以启用方法级别的权限检查！然后，可以选择将配置检查的配置添加在控制器中处理请求的方法上（其实也可以添加在其它组件的自定义方法上），例如：

```java
@ApiOperation("添加管理员")
@ApiOperationSupport(order = 100)
@PreAuthorize("hasAuthority('/ams/admin/add-new')") // 新增
@PostMapping("/add-new")
public JsonResult<Void> addNew(AdminAddNewDTO adminAddNewDTO) {
    log.debug("开始处理【添加管理员】的请求，参数：{}", adminAddNewDTO);
    adminService.addNew(adminAddNewDTO);
    return JsonResult.ok();
}
```







```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiYXV0aG9yaXRpZXNKc29uU3RyaW5nIjoiW3tcImF1dGhvcml0eVwiOlwiL2Ftcy9hZG1pbi9hZGQtbmV3XCJ9LHtcImF1dGhvcml0eVwiOlwiL2Ftcy9hZG1pbi9kZWxldGVcIn0se1wiYXV0aG9yaXR5XCI6XCIvYW1zL2FkbWluL3JlYWRcIn0se1wiYXV0aG9yaXR5XCI6XCIvYW1zL2FkbWluL3VwZGF0ZVwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvYWxidW0vYWRkLW5ld1wifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvYWxidW0vZGVsZXRlXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9hbGJ1bS9yZWFkXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9hbGJ1bS91cGRhdGVcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL2JyYW5kL2FkZC1uZXdcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL2JyYW5kL2RlbGV0ZVwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvYnJhbmQvcmVhZFwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvYnJhbmQvdXBkYXRlXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9jYXRlZ29yeS9hZGQtbmV3XCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9jYXRlZ29yeS9kZWxldGVcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL2NhdGVnb3J5L3JlYWRcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL2NhdGVnb3J5L3VwZGF0ZVwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvcGljdHVyZS9hZGQtbmV3XCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9waWN0dXJlL2RlbGV0ZVwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvcGljdHVyZS9yZWFkXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9waWN0dXJlL3VwZGF0ZVwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvcHJvZHVjdC9hZGQtbmV3XCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9wcm9kdWN0L2RlbGV0ZVwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvcHJvZHVjdC9yZWFkXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9wcm9kdWN0L3VwZGF0ZVwifV0iLCJleHAiOjE2Njg4NTE1MjgsInVzZXJuYW1lIjoicm9vdCJ9.PZw1dwiPP7uDgGf-GQsPdahmLmq1oLC3tPsP0M2K0bM
```





```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MiwiYXV0aG9yaXRpZXNKc29uU3RyaW5nIjoiW3tcImF1dGhvcml0eVwiOlwiL3Btcy9hbGJ1bS9hZGQtbmV3XCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9hbGJ1bS9kZWxldGVcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL2FsYnVtL3JlYWRcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL2FsYnVtL3VwZGF0ZVwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvYnJhbmQvYWRkLW5ld1wifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvYnJhbmQvZGVsZXRlXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9icmFuZC9yZWFkXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9icmFuZC91cGRhdGVcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL2NhdGVnb3J5L2FkZC1uZXdcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL2NhdGVnb3J5L2RlbGV0ZVwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvY2F0ZWdvcnkvcmVhZFwifSx7XCJhdXRob3JpdHlcIjpcIi9wbXMvY2F0ZWdvcnkvdXBkYXRlXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9waWN0dXJlL2FkZC1uZXdcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL3BpY3R1cmUvZGVsZXRlXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9waWN0dXJlL3JlYWRcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL3BpY3R1cmUvdXBkYXRlXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9wcm9kdWN0L2FkZC1uZXdcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL3Byb2R1Y3QvZGVsZXRlXCJ9LHtcImF1dGhvcml0eVwiOlwiL3Btcy9wcm9kdWN0L3JlYWRcIn0se1wiYXV0aG9yaXR5XCI6XCIvcG1zL3Byb2R1Y3QvdXBkYXRlXCJ9XSIsImV4cCI6MTY2ODg1MTYxNywidXNlcm5hbWUiOiJzdXBlcl9hZG1pbiJ9.J7NUoItrFePJpmcV8FLl4Pcb1IcAca31nczMfOdiN74
```







```

```





