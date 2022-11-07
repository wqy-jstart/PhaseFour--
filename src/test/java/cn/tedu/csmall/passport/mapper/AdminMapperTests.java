package cn.tedu.csmall.passport.mapper;

import cn.tedu.csmall.passport.pojo.entity.Admin;
import cn.tedu.csmall.passport.pojo.vo.AdminLoginInfoVO;
import cn.tedu.csmall.passport.pojo.vo.AdminStandardVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员Mapper的测试类
 *
 * @java.@Wqy
 * @Version 0.0.1
 */
@Slf4j
@SpringBootTest
public class AdminMapperTests {

    // 自动注入Admin的DAO层mapper接口类
    @Autowired
    AdminMapper adminMapper;

    /**
     * 测试直接向数据库中添加数据
     */
    @Test
    void insert(){
        Admin admin = new Admin();
        admin.setUsername("武清源");
        admin.setPassword("123456");
        admin.setNickname("Devotion");
        admin.setAvatar("342311kgrfgrepg");
        admin.setPhone("15551898017");
        admin.setEmail("2168149199@qq.com");
        admin.setDescription("无");
        admin.setEnable(1);
        admin.setLastLoginIp("宁波");
        admin.setLoginCount(2);
        int rows = adminMapper.insert(admin);
        log.debug("插入成功,影响的条数为:{}",rows);
    }

    /**
     * 测试根据用户名查询数量
     */
    @Test
    void countByUsername(){
        String username = "武清源";
        int rows = adminMapper.countByUsername(username);
        log.debug("查询成功,查到的数据条数为:{}",rows);
    }

    /**
     * 测试根据手机号查询数量
     */
    @Test
    void countByPhone(){
        String phone = "15551898017";
        int rows = adminMapper.countByPhone(phone);
        log.debug("查询成功,查到的数据条数为:{}",rows);
    }

    /**
     * 测试根据电子邮箱查询数量
     */
    @Test
    void countByEmail(){
        String email = "2168149199@qq.com";
        int rows = adminMapper.countByEmail(email);
        log.debug("查询成功,查到的数据条数为:{}",rows);
    }

    /**
     * 测试批量插入管理员
     */
    @Test
    void insertBatch(){
        List<Admin> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Admin admin = new Admin();
            admin.setUsername("武清源"+i);
            admin.setPassword("123456");
            admin.setNickname("Devotion");
            admin.setAvatar("https://img2.baidu.com/it/u=4244269751,4000533845&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500");
            admin.setPhone("15551898017"+i);
            admin.setEmail("2168149199@qq.com"+i);
            admin.setDescription("无");
            admin.setEnable(1);
            admin.setLastLoginIp("宁波");
            admin.setLoginCount(2);
            list.add(admin);
        }
        int rows = adminMapper.insertBatch(list);
        log.debug("批量插入完成,受影响的行数,{}",rows);
    }

    /**
     * 测试根据id删除管理员
     */
    @Test
    void deleteById(){
        Long id = 6L;
        int rows = adminMapper.deleteById(id);
        log.debug("删除完成,受影响的行数为,{}",rows);
    }

    /**
     * 测试根据id批量删除管理员
     */
    @Test
    void deleteByIds(){
        Long[] ids = {7L,8L,9L};
        int rows = adminMapper.deleteByIds(ids);
        log.debug("删除完成,受影响的行数为,{}",rows);
    }

    /**
     * 测试根据id修改管理员信息
     */
    @Test
    void updateById(){
        Long id = 10L;
        Admin admin = new Admin();
        admin.setId(id);
        admin.setUsername("武清源");
        admin.setPassword("123456");
        admin.setNickname("Devotion");
        admin.setAvatar("342311kgrfgrepg");
        admin.setPhone("15551898017");
        admin.setEmail("2168149199@qq.com");
        admin.setDescription("无");
        admin.setEnable(1);
        admin.setLastLoginIp("宁波");
        admin.setLoginCount(2);
        int rows = adminMapper.updateById(admin);
        log.debug("修改完成,受影响的行数为,{}",rows);
    }

    /**
     * 测试直接查询管理员数量
     */
    @Test
    void count(){
        int rows = adminMapper.count();
        log.debug("查询成功,数据数量为:{}",rows);
    }

    /**
     * 测试根据id查询单个管理员的信息
     */
    @Test
    void getStandardById(){
        Long id = 5L;
        AdminStandardVO adminStandardVO = adminMapper.getStandardById(id);
        log.debug("查询到的管理员信息为:{}",adminStandardVO.toString());
    }

    /**
     * 测试根据用户名查询登录所需要的管理员信息
     */
    @Test
    void getLoginInfoByUsername(){
        String username = "fanchuanqi";
        AdminLoginInfoVO adminLoginInfoVO = adminMapper.getLoginInfoByUsername(username);
        log.debug("查询成功,参数{}",adminLoginInfoVO.toString());
    }
}
