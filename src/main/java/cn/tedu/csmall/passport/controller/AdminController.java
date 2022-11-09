package cn.tedu.csmall.passport.controller;

import cn.tedu.csmall.passport.pojo.dto.AdminAddNewDTO;
import cn.tedu.csmall.passport.pojo.dto.AdminLoginDTO;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.security.LoginPrincipal;
import cn.tedu.csmall.passport.service.IAdminService;
import cn.tedu.csmall.passport.web.JsonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "管理员管理模块")
@Slf4j
@Validated
@RequestMapping("/admins")
@RestController
public class AdminController {

    // 注入AdminService实现类
    @Autowired
    IAdminService adminService;

    public AdminController() {
        log.info("创建控制器对象：AdminController");
    }

    /**
     * 处理管理员登录的请求
     * @param adminLoginDTO 接收传入的管理员数据
     * @return 返回JsonResult对象(包含状态码,和反馈信息)
     */
    // http://localhost:9081/admins/login
    @ApiOperation("管理员登录")
    @ApiOperationSupport(order = 50)
    @PostMapping("/login")
    public JsonResult<String> login(AdminLoginDTO adminLoginDTO){
        log.debug("开始处理[管理员登录]的请求,参数:{}",adminLoginDTO);
        String jwt = adminService.login(adminLoginDTO);
        return JsonResult.ok(jwt);
    }

    /**
     * 处理添加管理员的请求
     * @param adminAddNewDTO 接收传入的管理员数据
     * @return 返回JsonResult对象(包含状态码,和反馈信息)
     */
    // http://localhost:9081/admins/add-new
    @ApiOperation("添加管理员")
    @ApiOperationSupport(order = 100)
    @PostMapping("/add-new")
    public JsonResult<Void> addNew(@Valid AdminAddNewDTO adminAddNewDTO){
        adminService.adNew(adminAddNewDTO);
        return JsonResult.ok();
    }

    /**
     * 处理删除管理员的请求
     * @param id 要删除的管理员id
     * @return 返回JsonResult
     */
    // http://localhost:9081/adminsid/delete
    @ApiOperation("根据id删除管理员")
    @ApiOperationSupport(order = 200)
    @ApiImplicitParam(name = "id",value = "管理员id",required = true,dataType = "long")
    @PostMapping("/{id:[0-9]+}/delete")
    public JsonResult<Void> delete(@Range(min = 1,message = "删除管理员失败,尝试删除的管理员id无效") @PathVariable Long id){
        log.debug("开始处理[删除管理员]的请求,管理员id为{}",id);
        adminService.delete(id);
        return JsonResult.ok();
    }

    /**
     * 处理查询管理员列表的请求
     * @return JsonResult
     */
    // http://localhost:9081/admins
    @ApiOperation("管理员列表")
    @ApiOperationSupport(order = 210)//排序
    @GetMapping("")
    public JsonResult<List<AdminListItemVO>> list(
            @ApiIgnore @AuthenticationPrincipal LoginPrincipal loginPrincipal){// 添加@ApiIgnore注解告诉Api文档忽略当前的输入框
        log.debug("开始处理[查询管理员列表]的请求,无参数");
        log.debug("当前登录的当事人:{}",loginPrincipal);
        List<AdminListItemVO> list = adminService.list();
        return JsonResult.ok(list);
    }

    /**
     * 处理启用管理员的业务
     * @param id 要启用的管理员id
     * @return 返回JsonResult
     */
    // http://localhost:9081/admins/id/enable
    @ApiOperation("启用管理员")
    @ApiOperationSupport(order = 310)
    @ApiImplicitParam(name = "id",value = "启用的管理员id",required = true,dataType = "long")
    @PostMapping("/{id:[0-9]+}/enable")
    public JsonResult<Void> setEnable(@Range(min = 1,message = "启用管理员失败,尝试启用的id无效!")
                                          @PathVariable Long id){
        log.debug("开始将id为{}的管理员设置为启用状态",id);
        adminService.setEnable(id);
        return JsonResult.ok();
    }

    /**
     * 处理禁用管理员的业务
     * @param id 要禁用的管理员id
     * @return 返回JsonResult
     */
    // http://localhost:9081/admins/id/disable
    @ApiOperation("禁用管理员")
    @ApiOperationSupport(order = 311)
    @ApiImplicitParam(name = "id",value = "禁用的管理员id",required = true,dataType = "long")
    @PostMapping("/{id:[0-9]+}/disable")
    public JsonResult<Void> setDisable(@Range(min = 1,message = "禁用管理员失败,尝试启用的id无效!")
                                           @PathVariable Long id){
        log.debug("开始将id为{}的管理员设置为禁用状态",id);
        adminService.setDisable(id);
        return JsonResult.ok();
    }
}
