package cn.tedu.csmall.passport.controller;

import cn.tedu.csmall.passport.pojo.dto.AdminAddNewDTO;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.service.IAdminService;
import cn.tedu.csmall.passport.web.JsonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * 处理删除管理员的请求
     * @param id 要删除的管理员id
     * @return 返回JsonResult
     */
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
    @ApiOperation("管理员列表")
    @ApiOperationSupport(order = 410)//排序
    @GetMapping("")
    public JsonResult<List<AdminListItemVO>> list(){
        log.debug("开始处理[查询管理员列表]的请求,无参数");
        List<AdminListItemVO> list = adminService.list();
        return JsonResult.ok(list);
    }
}
