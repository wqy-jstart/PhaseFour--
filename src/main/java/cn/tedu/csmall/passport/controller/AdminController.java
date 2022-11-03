package cn.tedu.csmall.passport.controller;

import cn.tedu.csmall.passport.pojo.dto.AdminAddNewDTO;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.service.IAdminService;
import cn.tedu.csmall.passport.web.JsonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "管理员管理模块")
@Slf4j
@Validated
@RequestMapping("/admins")
@RestController
public class AdminController {

    @Autowired
    IAdminService adminService;

    /**
     * 处理添加管理员的请求
     * @param adminAddNewDTO 接收传入的管理员数据
     * @return 返回JsonResult对象(包含状态码,和反馈信息)
     */
    @ApiOperation("添加管理员")
    @ApiOperationSupport(order = 100)
    @PostMapping("/add-new")
    public JsonResult<Void> addNew(@Valid AdminAddNewDTO adminAddNewDTO){
        adminService.adNew(adminAddNewDTO);
        return JsonResult.ok();
    }

    /**
     * 处理查询管理员列表的请求
     * @return JsonResult
     */
    @ApiOperation("管理员列表")
    @ApiOperationSupport(order = 200)
    @GetMapping("")
    public JsonResult<List<AdminListItemVO>> list(){
        log.debug("开始处理[查询管理员列表]的请求");
        List<AdminListItemVO> list = adminService.list();
        return JsonResult.ok(list);
    }
}
