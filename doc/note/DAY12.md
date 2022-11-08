# 78. 关于“登录”的判断标准

在Spring Security框架中，对于“登录”（通过认证）的判断标准是：在`SecurityContext`（Security上下文）中是否存在`Authentication`对象（认证信息），如果存在，Spring Security框架会根据`Authentication`对象识别用户的身份、权限等，如果不存在，则视为“未登录”。

在默认情况下，Spring Security框架也是基于Session来处理（读写）用户的信息的。

# 79. 关于Session

HTTP协议本身是**无状态**协议，无法保存用户信息，即：某客户端第1次访问了服务器端，可能产生了某些数据，此客户端再次访问服务器端时，服务器端无法识别出这个客户端是此前曾经来访的客户端。

为了能够识别客户端的身份，当某客户端第1次向服务器端发起请求时，服务器端将向客户端响应一个JSESSIONID数据，其本质是一个UUID数据，在客户端后续的访问中，客户端会自动携带此JSESSIONID，以至于服务器端能够识别此客户端的身份。同时，在服务器端，还是一个`Map`结构的数据，此数据是使用JSESSIONID作为Key的，所以，每个客户端在服务器端都有一个与之对应在的在此`Map`中的`Value`，也就是Session数据！

> 提示：UUID是全球唯一的，从设计上，它能够保证在同一时空中的唯一性。

由于Session的运作机制，决定了它必然存在缺点：

- **默认不适用于集群或分布式系统**，因为Session是内存中的数据，所以，默认情况下，Session只存在于与客户端交互的那台服务器上，如果使用了集群，客户端每次请求的服务器都不是同一台服务器，则无法有效的识别客户端的身份
  - 可以通过共享Session等机制解决
- **不适合长时间保存数据**，因为Session是内存中的数据，并且，所有来访的客户端在服务器端都有对应的Session数据，就必须存在Session清除机制，如果长期不清除，随着来访的客户端越来越多，将占用越来越多的内存，服务器将无法存储这大量的数据，通常，会将Session设置为15分钟或最多30分钟清除

# 80. Token

Token：票据、令牌

由于客户端种类越来越多，目前，主流的识别用户身份的做法都是使用Token机制，Token可以理解为“票据”，例如现实生活中的“火车票”，某客户端第1次请求服务器，或执行登录请求，则可视为“购买火车票”的行为，当客户端成功登录，相当于成功购买了火车票，客户端的后续访问应该携带Token，相当于乘坐火车需要携带购票凭证，则服务器端可以识别客户端的身份，相当于火车站及工作人员可以识别携带了购买凭证的乘车人。

与Session最大的区别在于：Token是包含可识别的有效信息的！对于需要获取信息的一方而言，只需要具备读取Token信息的能力即可。

> Session机制中客户端需要携带的JSESSIONID本身上是UUID，此数据只具有唯一性，并不是有意义的数据，真正有意义的数据是服务器端内存中的Session数据。

所以，Token并不需要占用较多的内存空间，是可以长时间，甚至非常长时间保存用户信息的！

# 81. JWT

**JWT**：**J**SON **W**eb **T**oken

JWT是一种使用JSON格式来组织数据的Token。

# 82. 生成与解析JWT

需要添加依赖项：

```xml
<!-- JJWT（Java JWT） -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

关于**生成JWT**与**解析JWT**的示例代码：

```java
package cn.tedu.csmall.passport;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTests {

    String secretKey = "a9F8ujGFDhjgvfd3SA90ukEDS";

    @Test
    public void generate() {
        Date date = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 9527);
        claims.put("username", "liucangsong");

        String jwt = Jwts.builder()
                // Header
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                // Payload
                .setClaims(claims)
                // Signature
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        System.out.println(jwt);

        // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OTUyNywiZXhwIjoxNjY3ODc3ODg5LCJ1c2VybmFtZSI6ImxpdWNhbmdzb25nIn0.Txpj_kcLpkpUoEYA94pLCM3H807UnOEqN_r0c005I44
        // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OTUyNywiZXhwIjoxNjY3ODc5MDM2LCJ1c2VybmFtZSI6ImxpdWNhbmdzb25nIn0.gMlHQiSbbWnf5cIBi0p4V9bz05QHRaq3rNC8e_4yfpE
        // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OTUyNywiZXhwIjoxNjY3ODc5ODAxLCJ1c2VybmFtZSI6ImxpdWNhbmdzb25nIn0.fjPvR0ibgNKoTp6U-1fCOcMoAVMRkAQ1yr4C2fvf6YQ
    }

    @Test
    public void parse() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OTUyNywiZXhwIjoxNjY3ODc5ODAxLCJ1c2VybmFtZSI6ImxpdWNhbmdzb25nIn0.fjPvR0ibgNKoTp6U-1fCOcMoAVMRkAQ1yr4C2fvf6YQ";

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();

        Long id = claims.get("id", Long.class);
        String username = claims.get("username", String.class);

        System.out.println("id = " + id);
        System.out.println("username = " + username);
    }

}

```

当尝试解析JWT时，可能会出现以下错误：

- 如果JWT已过期，会抛出`ExpiredJwtException`，例如：

```
io.jsonwebtoken.ExpiredJwtException: JWT expired at 2022-11-08T11:24:49Z. Current time: 2022-11-08T11:38:01Z, a difference of 792152 milliseconds.  Allowed clock skew: 0 milliseconds.
```

- 如果JWT数据有误，会抛出`MalformedJwtException`，例如：

```
io.jsonwebtoken.MalformedJwtException: Unable to read JSON value: {"alg":"HS7#�$�uB'
```

- 如果JWT签名不匹配，会抛出`SignatureException`，例如：

```
io.jsonwebtoken.SignatureException: JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.
```

# 83. 登录成功时返回JWT

在处理登录时，当用户登录成功，应该**向客户端返回JWT数据**，以至于客户端下次提交请求时，可以携带JWT来访问服务器端！

首先，需要在通过认证（登录成功）后，生成JWT数据，并返回！在Spring Security框架中，`AuthenticationManager`调用`authenticate()`方法时，如果通过认证，会返回`Authentication`接口类型的对象，本质上是`UsernamePasswordAuthenticationToken`类型，此类型中的`pricipal`属性就是通过认证的用户信息，也是`UserDetailsService`中的`loadUserByUsername()`方法返回的结果，例如：

```
UsernamePasswordAuthenticationToken [
	Principal=org.springframework.security.core.userdetails.User [
		Username=root, 
		Password=[PROTECTED], 
		Enabled=true, 
		AccountNonExpired=true, 
		credentialsNonExpired=true, 
		AccountNonLocked=true, 
		Granted Authorities=[暂时给出的假的权限标识]
	], 
	Credentials=[PROTECTED], 
	Authenticated=true, 
	Details=null, 
	Granted Authorities=[暂时给出的假的权限标识]
]
```

所以，可以在处理认证的代码后再添加读取认证结果、生成JWT的代码：

```java
@Override
public void login(AdminLoginDTO adminLoginDTO) {
    log.debug("开始处理【管理员登录】的业务，参数：{}", adminLoginDTO);
    // 执行认证
    Authentication authentication
            = new UsernamePasswordAuthenticationToken(
                    adminLoginDTO.getUsername(), adminLoginDTO.getPassword());
    Authentication authenticateResult
            = authenticationManager.authenticate(authentication);
    log.debug("认证通过，认证管理器返回：{}", authenticateResult);

    // 从认证结果中获取所需的数据，将用于生成JWT
    Object principal = authenticateResult.getPrincipal();
    log.debug("认证结果中的当事人类型：{}", principal.getClass().getName());
    User user = (User) principal;
    String username = user.getUsername();

    // 生成JWT数据时，需要填充装到JWT中的数据
    Map<String, Object> claims = new HashMap<>();
    // claims.put("id", 9527);
    claims.put("username", username);
    // 以下是生成JWT的固定代码
    String secretKey = "a9F8ujGDhjgFvfEd3SA90ukDS";
    Date date = new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000L);
    String jwt = Jwts.builder()
            // Header
            .setHeaderParam("alg", "HS256")
            .setHeaderParam("typ", "JWT")
            // Payload
            .setClaims(claims)
            // Signature
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    log.debug("生成的JWT：{}", jwt);
}
```

接下来，需要将`IAdminService`接口中定义的“登录”方法的返回值类型修改为`String`：

```java
/**
 * 管理员登录
 *
 * @param adminLoginDTO 封装了管理员的用户名和密码的对象
 * @return 登录成功后生成的匹配的JWT
 */
String login(AdminLoginDTO adminLoginDTO);
```

并且修改其实现，并返回JWT。

然后，调整控制器中处理登录请求的方法：

```java
// http://localhost:9081/admins/login
@ApiOperation("管理员登录")
@ApiOperationSupport(order = 50)
@PostMapping("/login")
public JsonResult<String> login(AdminLoginDTO adminLoginDTO) {
    log.debug("开始处理【管理员登录】的请求，参数：{}", adminLoginDTO);
    String jwt = adminService.login(adminLoginDTO);
    return JsonResult.ok(jwt);
}
```

完成后，重启项目，在API文档中调试，使用正确的用户名、密码登录，响应结果中将包含对应的JWT数据，并且，此JWT数据可以在此前编写的测试方法中尝试解析（注意：务必保证生成JWT和解析JWT使用的`secretKey`是相同的）。

# 84. 识别客户端的身份

基于Spring Security框架的特征“依据`SecurityContext`中的认证信息来判定当前是否已经通过认证”，所以，客户端应该在得到JWT之后，携带JWT向服务器端提交请求，而服务器端应该尝试解析此JWT，并且从中获取用户信息，用于创建认证对象，最后，将认证对象存入到`SecurityContext`中，剩下的就可以交由框架进行处理了，例如判断是否已经通过认证等。

由于若干个不同的请求都需要识别客户端的身份（即解析JWT、创建认证对象、将认证对象存入到`SecurityContext`），所以，应该通过能够统一处理的组件来处理JWT，同时，此项任务**必须在Spring Security的过滤器之前执行**，则此项任务只能通过自定义过滤器来处理！

则在项目的根包下创建`filter.JwtAuthorizationFilter`类：

```java
package cn.tedu.csmall.passport.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>JWT过滤器</p>
 *
 * <p>此JWT的主要作用：</p>
 * <ul>
 *     <li>获取客户端携带的JWT，惯用做法是：客户端应该通过请求头中的Authorization属性来携带JWT</li>
 *     <li>解析客户端携带的JWT，并创建出Authentication对象，存入到SecurityContext中</li>
 * </ul>
 *
 * @author java@tedu.cn
 * @version 0.0.1
 */
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final int JWT_MIN_LENGTH = 113;

    public JwtAuthorizationFilter() {
        log.info("创建过滤器对象：JwtAuthorizationFilter");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtAuthorizationFilter开始执行过滤……");
        // 获取客户端携带的JWT
        String jwt = request.getHeader("Authorization");
        log.debug("获取客户端携带的JWT：{}", jwt);

        // 检查是否获取到了基本有效的JWT
        if (!StringUtils.hasText(jwt) || jwt.length() < JWT_MIN_LENGTH) {
            // 对于无效的JWT，直接放行，交由后续的组件进行处理
            log.debug("获取到的JWT被视为无效，当前过滤器将放行……");
            filterChain.doFilter(request, response);
            return;
        }

        // 尝试解析JWT
        log.debug("获取到的JWT被视为有效，准备解析JWT……");
        String secretKey = "a9F8ujGDhjgFvfEd3SA90ukDS";
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();

        // 获取JWT中的管理员信息
        String username = claims.get("username", String.class);

        // 处理权限信息
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("这是一个假权限");
        authorities.add(authority);

        // 创建Authentication对象
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);

        // 将Authentication对象存入到SecurityContext
        log.debug("向SecurityContext中存入认证信息：{}", authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 过滤器链继续向后传递，即：放行
        log.debug("JWT过滤器执行完毕，放行！");
        filterChain.doFilter(request, response);
    }

}
```

并且，为了保证此过滤器在Spring Security的过滤器之前执行，还应该在`SecurityConfiguration`中，先自动装配此过滤器对象：

```java
@Autowired
private JwtAuthorizationFilter jwtAuthorizationFilter;
```

然后，在`void configurer(HttpSecurity http)`方法中补充配置：

```java
// 将JWT过滤器添加到Spring Security框架的过滤器链中
http.addFilterBefore(jwtAuthorizationFilter, 
                     	UsernamePasswordAuthenticationFilter.class);
```

至此，简单的登录处理已经完成，客户端或API文档可以通过 `/admins/login` 登录，以获取JWT数据，并且，在后续的访问中，如果携带了JWT数据，将可以正常访问，否则，将无权访问！

目前，还存在需要解决的问题：

- 生成和解析JWT的`secretKey`不应该分别定义在2个类中
- 解析JWT可能出现异常，但尚未处理
- 认证信息中的“当事人”是使用`username`表示的，不包含此管理员的`id`，不便于实现后续的需求
- 认证信息中权限目前是假数据
- 前端还没有结合起来

# 85. 前端登录

在前端的登录页面中，当服务器端响应登录成功后，应该将服务器端响应的JWT数据保存下来，可以使用`localStorage`来保存数据，例如：

```javascript
  let url = 'http://localhost:9081/admins/login';
  console.log('url = ' + url);
  let formData = this.qs.stringify(this.ruleForm);
  console.log('formData = ' + formData);
  this.axios.post(url, formData).then((response) => {
    let responseBody = response.data;
    if (responseBody.state == 20000) {
      this.$message({
        message: '登录成功！',
        type: 'success'
      });
      let jwt = responseBody.data;
      console.log('登录成功，服务器端响应JWT：' + jwt);
      localStorage.setItem('jwt', jwt);  // 使用localStorage保存数据
      console.log('已经将JWT保存到localStorage');
    } else {
      console.log(responseBody.message);
      this.$message.error(responseBody.message);
    }
  });
```

后续，当需要此数据时，可以通过`localStorage.getItem(key)`来获取此前存入的数据。

在需要携带JWT的请求中，可以调用`axios`对象的`create()`方法来配置请求头，并使用此方法返回的`axios`对象向服务器端提交请求，例如：

```java
  loadAdminList() {
  console.log('loadAdminList');
  console.log('在localStorage中的JWT数据：' + localStorage.getItem('jwt'));
  let url = 'http://localhost:9081/admins';
  console.log('url = ' + url);
  this.axios
      // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓  以下是携带JWT提交请求的关键代码  ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
      .create({'headers': {'Authorization': localStorage.getItem('jwt')}})
      .get(url).then((response) => {
    let responseBody = response.data;
    this.tableData = responseBody.data;
  });
}
```

需要注意：当客户端的异步请求定义了请求头中的`Authorization`时，在服务器端，在`SecurityConfiguration`类的`void configurer(HttpSecurity http)`方法中，需要添加以下配置：

```java
http.cors();
```

否则客户端将出现跨域错误！













