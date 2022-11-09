package cn.tedu.csmall.passport.service;

import cn.tedu.csmall.passport.ex.ServiceException;
import cn.tedu.csmall.passport.pojo.dto.AdminAddNewDTO;
import cn.tedu.csmall.passport.web.JsonResult;
import cn.tedu.csmall.passport.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class AdminServiceTests {

    @Autowired
    IAdminService adminService;

    /**
     * 测试插入由客户端传入的管理员数据
     */
    @Test
    void addNew(){
        log.debug("开始处理添加管理员的请求!");
        AdminAddNewDTO adminAddNewDTO = new AdminAddNewDTO();
        adminAddNewDTO.setUsername("武清源");
        adminAddNewDTO.setPassword("123456");
        adminAddNewDTO.setNickname("Devotion");
        adminAddNewDTO.setAvatar("342311kgrfgrepg");
        adminAddNewDTO.setPhone("15551898017");
        adminAddNewDTO.setEmail("2168149199@qq.com");
        adminAddNewDTO.setDescription("无");
        adminAddNewDTO.setEnable(1);
        try{
            adminService.adNew(adminAddNewDTO);
            log.debug("添加数据成功!");
        }catch (ServiceException e){
            log.debug(e.getMessage());
        }
    }

    @Test
    void delete(){
        Long id = 30L;
        adminService.delete(id);
        log.debug("删除成功!");
    }

    @Test
    void list(){
        List<?> list = adminService.list();
        for (Object item : list) {
            log.debug("{}",item);
        }
    }

    @Test
    void setDisable(){
        adminService.setEnable(12L);//将id为12设为启用
    }

    @Test
    void test(){

        System.out.println(JsonResult.fail(ServiceCode.ERR_DELETE,"错误"));
    }
}
