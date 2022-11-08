# 51. 关于csmall-passport项目

此项目主要实现管理员管理，例如：添加管理员、显示管理员列表、删除管理员、启用管理员、禁用管理员等。

还将实现管理员登录功能，此登录将可以影响到其它项目，例如`csmall-product`项目，只要在当前`csmall-passport`项目中登录，在`csmall-product`项目中也能识别管理员的身份，并检查权限等。

# 52. 关于mall_ams数据库

此数据库的核心数据表包括：

- `ams_admin`：管理员表
- `ams_permission`：权限表
- `ams_role`：角色表
- `ams_admin_role`：管理员与角色的关联表
- `ams_role_permission`：角色与权限的关联表

以上表的设计是基于**RBAC（Role-Based Access Control）模型**的！

# 53. 关于创建新项目

- 创建新的项目`jsd2207-csmall-passport`
- 调整`pom.xml`，主要：
  - 设置父级的Spring Boot项目版本：`2.5.9`
  - 添加相关依赖项：从前序项目中复制
- 从前序项目中复制配置文件
  - `application.yml`系列文件，主要修改：连接到的数据库的名称、服务端口号
- 从前序项目中复制配置类
  - 可以将原有所有配置类全部复制到当前项目，需要修改：Mybatis配置中扫描的包名、Knife4j配置中控制器类的包名，及Knife4j配置中其它用于显示的字样
- 创建数据库，创建数据表

# 54. 添加管理员--Mapper层

当添加管理员时，本质上是执行“插入管理员数据”的操作，需要执行的SQL语句大致是：

```mysql
INSERT INTO ams_admin (除了id和2个时间以外的字段列表) VALUES (匹配的值列表)
```

当添加管理员时，还应该执行相关的检查，例如：

- 用户名必须唯一（在任何系统中，登录时使用的凭证必须唯一）
- 手机号码必须唯一（暂时用不上，但合理）
- 电子邮箱必须唯一（暂时用不上，但合理）

以上检查需要执行的SQL语句大致是：

```mysql
SELECT count(*) FROM ams_admin WHERE username=?
```

```mysql
SELECT count(*) FROM ams_admin WHERE phone=?
```

```mysql
SELECT count(*) FROM ams_admin WHERE email=?
```

首先，在根包下创建`pojo.entity.Admin`实体类：

```java

```

在根包下创建`mapper.AdminMapper`接口，并在接口中添加抽象方法：

```java
@Repository
public interface AdminMapper {
    int insert(Admin admin);
    int countByUsername(String username);
    int countByPhone(String phone);
    int countByEmail(String email);
}
```

在`src/main/resources`下创建`mapper`文件夹，并在此文件夹下通过复制粘贴得到`AdminMapper.xml`文件，在此文件中配置以上4个抽象方法对应的SQL语句：

```xml

```

在`src/test/java`下的根包下创建`mapper.AdminMapperTests`测试类，对以上开发的4个功能进行测试：

```java

```

# 55. 添加管理员--Service层

从前序项目（`csmall-product`）中复制`ServiceCode`到当前项目对应的包中。

从前序项目（`csmall-product`）中复制`ServiceException`到当前项目对应的包中。

在根包下创建`pojo.dto.AdminAddNewDTO`类，在类中声明必要属性：

```java
@Data
public class AdminAddNewDTO implements Serializable {
    // 用户名 / 密码 / 昵称 / 头像 / 手机号码 / 电子邮箱 / 简介 / 是否启用
}
```

在根包下创建`service.IAdminService`接口，并在接口中添加抽象方法：

```java
@Transactional
public interface IAdminService {
    void addNew(AdminAddNewDTO adminAddNewDTO);
}
```

在根包下创建`service.impl.AdminServiceImpl`类，实现以上接口，并在类上添加`@Service`注解，在类中自动装配`AdminMapper`对象，并重写接口中定义的抽象方法：

```java
@Service
public class AdminServiceImpl implements IAdminService {
    
    @Autowired 
    private AdminMapper adminMapper;
    
    @Override 
    public void addNew(AdminAddNewDTO adminAddNewDTO) {
        // 调用adminMapper的int countByUsername(String username)执行统计查询
        // 判断统计结果是否大于0
        // 是：抛出异常（ERR_CONFLICT：用户名被占用）
        
        // 调用adminMapper的int countByPhone(String phone)执行统计查询
        // 判断统计结果是否大于0
        // 是：抛出异常（ERR_CONFLICT：手机号码被占用）
        
        // 调用adminMapper的int countByEmail(String email)执行统计查询
        // 判断统计结果是否大于0
        // 是：抛出异常（ERR_CONFLICT：电子邮箱被占用）
        
        // 创建Admin对象
        // 通过BeanUtils.copyProperties()将参数对象中的各属性复制到Admin对象中
        // 补全Admin对象的属性值：loginCount >>> 0
        // TODO 从参数对象中取出密码，进行加密，再存入到Admin对象中
        // 调用adminMapper的int insert(Admin admin)方法，执行插入管理员数据，获取返回值
        // 判断以上返回的受影响行数是否不等于1
        // 是：抛出异常（ERR_INSERT：服务器忙）
    }
}
```

在`src/test/java`下的根包下创建`service.AdminServiceTests`测试类，编写并执行测试：

```java

```

# 56. 添加管理员--Controller层

从前序项目（`csmall-product`）中复制`JsonResult`到当前项目对应的包中。

从前序项目（`csmall-product`）中复制`GlobalExceptionHandler`到当前项目对应的包中。

在项目的根包下创建`controller.AdminController`类，在类上添加`@RestController`和`@RequestMapping("/admins")`注解，并在类中自动装配`IAdminService`对象，然后，在类中添加处理请求的方法：

```java
@RestController
@RequestMapping("/admins")
public class AdminController {
    
    @Autowired
    private IAdminService adminService;
    
    @PostMapping("/add-new")
    public JsonResult<Void> addNew(AdminAddNewDTO adminAddNewDTO) {
        adminService.addNew(adminAddNewDTO);
        return JsonResult.ok();
    }
    
}
```

完成后，启动项目，通过 http://localhost:9081/doc.html 打开API文档，可进行调试。

当调试无误后，应该在`AdminController`添加API文档相关的注解。

# 57. 显示管理员列表--Mapper层

在根包下创建`pojo.vo.AdminListItemVO`类，此类中应该包含显示列表时所需的属性：

```java
@Data
public class AdminListItemVO implements Serializable {
    // ID / 用户名 / 昵称 / 头像 / 手机号码 / 电子邮箱 / 是否启用 / 登录次数 / 最后登录IP地址 / 最后登录时间
}
```

在`AdminMapper`接口中添加抽象方法：

```java
List<AdminListItemVO> list();
```

在`AdminMapper.xml`中配置SQL：

```xml
<!-- List<AdminListItemVO> list(); -->
<select id="list" resultMap="ListResultMap">
    SELECT
    	<include refid="ListQueryFields" />
    FROM
    	ams_admin
    ORDER BY
    	id
</select>

<sql id="ListQueryFields">
	<if test="true">
    	与VO中的属性一致的字段列表
    </if>
</sql>

<resultMap id="ListResultMap" type="cn.tedu.csmall.passport.pojo.vo.AdminListItemVO">
    <!-- 使用id标签配置主键，使用result标签配置其它字段与属性的映射 -->
</resultMap>
```

在`AdminMapperTests`中编写并执行测试：

```java

```

# 58. 显示管理员列表--Service层

在`IAdminService`中添加抽象方法（可以与Mapper接口中相同）：

```java

```

在`AdminServiceImpl`中实现以上抽象方法：

```java

```

在`AdminServiceTests`中编写并执行测试：

```java

```

# 59. 显示管理员列表--Controller层

在`AdminController`中添加处理请求的方法：

```java
@GetMapping("")
public JsonResult<List<AdminListItemVO>> list() {
    List<AdminListItemVO> list = adminService.list();
    return JsonResult.ok(list);
}
```

完成后，自行补充API文档的描述。









