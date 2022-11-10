# DAY12（11.08）

请思考以下问题，并通过文本文档提交答案：

- Spring Security框架主要解决了什么问题？
    - Spring Security框架主要解决了认证、授权的问题
- BCrypt算法的主要特点是什么？
    - 使用了随机的盐值，所以，即使密码原文相同，每次的编码结果都不同，并且，盐值是编码结果的一部分，所以，也不影响验证密码
    - 被设计为运算非常慢，所以，可以非常有效的避免穷举式的暴力破解
- UUID的主要特点是什么？
    - 随机，唯一
- 在继承了`WebSecurityConfigurerAdapter`的配置类中，重写`void configurer(HttpSecurity http)`方法
    - `http.formLogin()`方法的作用是什么？
        - 开启表单验证！如果已开启，当请求被视为未认证时，将重定向到登录表单页面，如果未开启，当请求被视为未认证时，将响应`403`
    - 配置请求认证的过程中，调用的`mvcMatchers()`方法的作用是什么？
        - 匹配某些路径，需要配置后续的某个方法一起使用，例如`permitAll()`或`authenticated()`等
    - 配置请求认证的过程中，调用的`permitAll()`方法的作用是什么？
        - 允许访问，即不需要通过认证即可访问
    - 配置请求认证的过程中，如果某个路径被多次匹配，最终此路径的规则是什么？
        - 以第1次的配置为准
    - `http.csrf().disable()`的作用是什么？
        - 禁用防伪造的跨域攻击机制
- `UserDetailsService`的作用是什么？
    - 此接口中定义了`UserDetails loadUserByUsername(String username)`的方法，Spring Security处理认证时，会自动根据用户名调用此方法，得到返回的`UserDetails`后，会基于此返回值自动完成后续的判断，例如密码是否匹配、账号是否被禁用等，所以，`UserDetailsService`的作用就是要实现根据用户名返回匹配的用户详情，以至于Spring Security能实现认证
- 如何得到`AuthenticationManager`对象？
    - 在继承了`WebSecurityConfigurerAdapter`的配置类中，重写`authenticationManagerBean()`方法，在此方法上添加`@Bean`注解即可，后续，当需要使用此对象时，使用自动装配机制即可
- 当调用`AuthenticationManager`对象的`authenticate()`方法后，会发生什么？此方法的返回结果是什么？
    - 调用了`authenticate()`方法后，Spring Security会开始执行认证，会通过参数中的用户名来调用`UserDetailsService`接口类型的对象的`loadUserByUsername()`方法，当得到此方法的返回结果后，自动执行后续的判断，例如密码是否匹配、账号是否被禁用等
    - 如果认证通过，将返回`Authentication`接口类型的对象，此对象通常有3大组成部分，分别是Principal（当事人）、Credentials（凭证）、Authorities（权限清单），其中，Principal就是`loadUserByUsername()`方法返回的结果
- Spring Security如何判定某个请求是否已经通过认证？
    - 取决于Security上下文（`SecurityContext`）中是否存在有效的认证信息（`Authentication`）

# DAY13（11.09）

请思考以下问题，并通过文本文档提交答案：

- 相比Session机制，JWT最大的优点是什么？
    - 更适合长时间保存用户状态
- 在Spring Security的配置类的`void configurer(HttpSecurity http)`方法中，`http.cors()`的作用是什么？
    - 启用Spring Security的`CorsFilter`过滤器，此过滤器可以对复杂请求的预检放行
- 根据业内惯用的作法，客户端应该如何携带JWT数据向服务器提交请求？
    - 应该使用请求头中的`Authorization`属性表示JWT数据
- 在服务器端，为什么要使用过滤器而不是其它组件来解析JWT？
    - 过滤器是Java EE体系中最早接收到请求的组件
    - Spring Security内置了一些过滤器，用于处理认证、授权的相关问题，包含检查Security上下文中是否存在认证信息
    - 解析JWT必须执行在Spring Security的相关过滤器之前，否则没有意义
- 在服务器端，JWT过滤器的主要作用是？
    - 尝试解析JWT，如果是有效的，则应该从中获取用户信息，用于创建认证信息（`Authentication`），并将认证信息存入到Security上下文中
- 如果客户端提交的请求没有携带JWT，服务器端的JWT过滤器应该如何处理？
    - 直接放行
- 在Spring Security的配置类的`void configurer(HttpSecurity http)`方法中，为什么要通过`http.addFilter()`系列方法添加JWT过滤器？
    - 如果没有通过`http.addFilter()`系列方法添加JWT过滤器，则JWT过滤器会在Spring Security内置的过滤器链之后执行，则没有意义
- 在服务器端，控制器处理请求时，如何获取当事人信息？
    - 在处理请求的方法的参数列表中，使用`@AuthenticationPrincipal`注解，添加在当事人类型的参数上
    - 当事人的类型，就是Secuirity上下文中`Authentication`对象的当事人类型
- 在服务器端，如何配置方法级别的权限？
    - 在配置类上添加`@EnableGlobalMethodSecurity(prePostEnabled = true)`注解，这是一次性配置
    - 在方法上（通常是处理请求的方法上）使用`@PreAuthorize`注解来配置权限规则
