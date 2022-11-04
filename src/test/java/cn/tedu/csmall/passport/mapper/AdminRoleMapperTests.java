package cn.tedu.csmall.passport.mapper;

import cn.tedu.csmall.passport.pojo.entity.AdminRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AdminRoleMapperTests {

    @Autowired
    AdminRoleMapper mapper;

    @Test
    void insertBatch(){
        AdminRole[] adminRoles = new AdminRole[3];//定义一个引用类型数组,指定长度为3
        for (int i = 0; i < adminRoles.length; i++) {
            //遍历的过程创建一个AdminRole对象,并设置值,不断放到数组中
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(100L);
            adminRole.setRoleId(i+0L);
            adminRoles[i] = adminRole;
        }

        //调用方法插入到角色管理员关联的数据库中(一个用户id对应多个角色)
        int rows = mapper.insertBatch(adminRoles);
        log.debug("批量插入成功,影响数据条数为:{}",rows);
    }
}
