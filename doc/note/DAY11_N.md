# Mybatis的占位符中使用的名称

在使用Mybatis框架时，配置的SQL语句中的参数可以使用占位符来表示，例如：

```xml
<!-- int deleteById(Long id); -->
<delete id="deleteById">
    DELETE FROM ams_admin WHERE id=#{id}
</delete>
```

以上SQL语句中的`#{id}`就是占位符。

事实上，当抽象方法的参数只有1个时，在占位符中的名称是完全自由编写的，因为Mybatis框架会自动的查找那唯一参数值代入到SQL执行过程中！当抽象方法的参数超过1个时，在占位符中名称不可以是随意的名称，如果使用的名称错误，则可能出现类似以下错误：

```
org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException: Parameter 'passwordxxx' not found. Available parameters are [password, id, param1, param2]
```

在错误提示信息中，已经明确指出：`Available parameters are [password, id, param1, param2]`，即“可用的参数名称是`[password, id, param1, param2]`”，则配置的SQL语句可以是：

```xml
<update id="updatePasswordById">
    update ams_admin set password=#{password} where id=#{id}
</update>
```

或者：

```xml
<update id="updatePasswordById">
    update ams_admin set password=#{param2} where id=#{param1}
</update>
```

其实，以上是在较高版本的框架中的执行效果，在较低版本的框架中，可用的参数通常是`[arg0, arg1, param1, param2]`，在这种情况下，在占位符中使用`id`或`password`这样的名称也是不可被识别的！只能使用`arg`系列参数，或`param`系列参数，如果使用`arg`系列参数，则使用`arg0`表示抽象方法的第1个参数，使用`arg1`表示抽象方法的第2个参数，使用`arg2`表示抽象方法的第3个参数，以此类推，即使在错误提示信息中并没有`arg2`、`arg3`等，也可以正常使用！如果使用`param`系列参数，与使用`arg`系列的方式基本相同，区别在于`param`系列的是使用`param1`表示第1个参数的。

之所以较低版本的参数没有名字，是因为Java语言在编译时，会丢失局部的量的名称，包括局部变量、方法的参数，所以，尽管设计抽象方法时明确的指定了方法的参数名称，但是编译时会丢失，所以，在最终运行时将无法根据SQL语句中配置的例如`#{id}`、`#{password}`这种名称的占位符找到参数！在较高版本的框架中，加入了编译期的干预，使得抽象方法的参数名称被保留了下来，所以，可以直接使用例如`#{id}`、`#{password}`这种名称的占位符！

在较低版本的框架中，为了保证能够使用`#{id}`、`#{password}`这种名称的占位符，需要在抽象方法的各参数前添加`@Param`注解，以配置参数的名称，例如：

```java
int updatePasswordById(@Param("id") Long id, @Param("password") String password);
```

则配置的SQL语句中的占位符中必须使用注解中配置的名称！

目前，由于新老版本的框架都有人使用，为了避免出现问题，建议无论使用什么版本，只要方法的参数超过1个，都使用`@Param`注解来指定参数名称！

以上规则也适用于动态SQL中的`<foreach>`标签的`collection`属性，关于此属性的值，如果抽象方法的参数只有1个，当参数类型是数组或可变参数时，取值为`array`，如果参数类型是`List`，取值为`list`，如果抽象方法的参数超过1个，使用`@Param`配置参数名，则`collection`属性的值就是`@Param`注解配置的参数名！

# Mybatis的占位符的格式

在Mybatis中，配置SQL语句时，参数可以使用`#{}`格式的占位符表示，也可以使用`${}`格式的占位符来表示！

对于以下查询：

```xml
<select id="getStandardById" resultMap="StandardResultMap">
    SELECT
        <include refid="StandardQueryFields" />
    FROM
        ams_admin
    WHERE
        id=#{id}
</select>
```

无论是使用`#{}`还是使用`${}`格式的占位符，执行效果是相同的！

对于以下查询：

```xml
<select id="getLoginInfoByUsername" resultMap="LoginResultMap">
    SELECT
        <include refid="LoginQueryFields" />
    FROM
        ams_admin
    WHERE
        username=#{username}
</select>
```

使用`#{}`格式的占位符时，是可以正常使用的，使用`${}`格式的占位符，会出现如下错误：

```
Caused by: java.sql.SQLSyntaxErrorException: Unknown column 'root' in 'where clause'
```

其实，以上问题，可以通过调用方法时，在传入的参数值两端添加单引号来解决，例如：

```java
@Test
void getLoginInfoByUsername() {
    String username = "'liucangsong'";
    Object queryResult = mapper.getLoginInfoByUsername(username);
    log.debug("根据用户名【{}】查询数据详情完成，查询结果：{}", username, queryResult);
}
```

本质上，使用`#{}`占位符时，Mybatis在通过JDBC底层实现时，使用了预编译的处理，所以，占位符的位置只可能是个值，不可能是字段名或别的，所以不需要使用一对单引号框住；使用`${}`占位符时，并没有使用预编译，而只是将参数值拼接到SQL语句中执行，所以，对于非数值型的参数值，需要使用一对单引号框住！

由于使用`#{}`占位符时，是预编译的，所以，完全不存在SQL注入的风险，而`${}`占位符是先拼接SQL再编译并执行的，拼接的参数值有可能改变语义，所以，存在SQL注入的风险！

当然，`${}`格式的占位符虽然有不少缺点，但是，它可以表示SQL语句中的任何片段，而`#{}`只能表示某个值！而且，关于SQL注入，其实要想实现SQL注入，传入的值也是需要满足许多特征性条件的，只要在执行SQL语句之前使用正则表达式进行验证，就可以避免出现SQL注入！













