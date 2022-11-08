# 60. 删除管理员--Mapper层

删除管理员，需要Mapper实现：

- 根据id删除管理员数据
- 根据id查询管理员数据（用于检查尝试删除的数据是否存在）

# 61. 删除管理员--Service层

在`IAdminService`中添加抽象方法：

```java
void delete(Long id);
```

在`AdminServiceImpl`中实现方法：

```java
public void delete(Long id) {
    // 调用adminMapper的AdminStandardVO getStandardById(Long id)方法执行查询
    // 判断查询结果是否为null
    // 是：抛出异常（ERR_NOT_FOUND）
    
    // 调用adminMapper的int deleteById(Long id)执行删除，并获取返回的行数
    // 判断返回的行数是否不为1
    // 是：抛出异常（ERR_DELETE：服务器忙）
}
```

在`AdminServiceTests`中编写并执行测试：

```java

```

# 62. 启用或禁用管理员--Mapper层

直接使用现有的通用`update()`即可。

# 63. 启用或禁用管理员--Service层

在`IAdminService`中添加抽象方法：

```java
void setEnable(Long id); // enable=1
void setDisable(Long id); // enable=0
```

在`AdminServiceImpl`中实现方法：

```java
public void setEnable(Long id) {
    updateEnableById(id, 1);
}

public void setDisable(Long id) {
    updateEnableById(id, 0)
}

private void updateEnableById(Long id, Integer enable) {
    String[] s = {"禁用", "启用"};

    // 判断id是否为1
    // 是：抛出异常（ERR_NOT_FOUND：s[enable] 管理员失败）
    
    // 根据id查询管理员详情
    // 判断查询结果是否为null
    // 是：抛出异常（ERR_NOT_FOUND）
    
    // 判断查询结果中的enable与方法参数enable是否相同
    // 是：抛出异常（ERR_CONFLICT）
    
    // 创建Admin对象，并封装id和enable这2个属性的值
    // 调用update()方法执行更新，并获取返回的行数
    // 判断返回的行数是否不为1
    // 是：抛出异常（ERR_UPDATE）
}
```

在`AdminServiceTests`中编写并执行测试：

```java

```

# 64. 启用或禁用管理员--Controller层

在`AdminController`中也是添加2个处理请求的方法，配置的请求路径可以是：

- `/admins/9527/enable`
- `/admins/9527/disable`

编写代码时，可以从原有的`delete()`方法复制再调整。

# 65. 添加管理员时确定此管理员的角色

当某个软件系统设计了“权限”的概念时，添加用户时必须确定此用户的权限，在基于RBAC的设计中，添加用户时必须确定此用户的角色，进而可以关联到某些权限，否则，如果没有确定此用户的角色，会导致新增的用户没有权限，可能无法进行相关操作！

要补充“添加管理员时确定此管理员的角色”，需要：

- 在“添加管理员”的界面中，补充显示“角色列表”的下拉菜单，以显示出当前系统中的所有角色
- 在“添加管理员”的客户端代码中，提交请求时，需要提交所选择的“角色”
- 在服务器端的业务实现类中，添加管理员时，当插入管理员数据之后，还需要向“管理员与角色的关联表”中插入数据
- 在服务器端的业务实现类中，删除管理员时，还需要将“管理员与角色的关联表”中的相关数据一并删除

对于服务器端而言，具体需要实现的有：

- 新增`RoleMapper`接口，需要实现“查询角色列表”功能，并且，还需要开发至控制器层
- 新增`AdminRoleMapper`接口，需要实现“批量插入”功能，“根据管理员id删除数据”功能

# 66. 显示角色列表--Mapper层

在根包下创建`pojo.vo.RoleListItemVO`类：

```java

```

在根包下创建`mapper.RoleMapper`接口，添加抽象方法：

```java
@Repository
public interface RoleMapper {
    List<RoleListItemVO> list();
}
```

在`src/main/resources`下的`mapper`文件夹中，通过复制粘贴得到`RoleMapper.xml`文件，配置以上查询功能映射的SQL语句：

```xml

```

在`src/test/java`下的根包下创建`mapper.RoleMapperTests`测试类，编写并执行测试：

```java

```

# 67. 显示角色列表--Service层

在根包下创建`service.IRoleService`接口，添加抽象方法：

```java
List<RoleListItemVO> list();
```

在根包下创建`service.impl.RoleServiceImpl`类，在类上添加`@Service`注解，实现以上接口，在类中自动装配`RoleMapper`对象，然后重写抽象方法：

```java
// 注意：将Mapper查询到的列表中id为1的数据移除
```

在`src/test/java`下的根包下创建`service.RoleServiceTests`测试类，编写并执行测试：

```java

```

# 68. 显示角色列表--Controller层

在根包下创建`controller.RoleController`类，在类上添加`@RestController`和`@RequestMapping("/roles")`注解，在类中自动装配`IRoleService`对象，然后添加处理请求的方法：

```java

```



```
indecies: roleIds%5B0%5D=2&roleIds%5B1%5D=4&roleIds%5B2%5D=6

brackets: roleIds%5B%5D=2&roleIds%5B%5D=4&roleIds%5B%5D=6

repeat: roleIds=2&roleIds=4&roleIds=6
```

> `%5B`：`[`
>
> `%5D`：`]`

