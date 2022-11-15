package cn.tedu.csmall.passport.controller;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import cn.tedu.csmall.passport.pojo.vo.RoleStandardVO;
import cn.tedu.csmall.passport.service.IRoleService;
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

import java.util.List;

@Api(tags = "02.角色管理模块")
@Slf4j
@Validated
@RequestMapping("/roles")
@RestController
public class RoleController {

    @Autowired
    IRoleService iRoleService;

    public RoleController() {
        log.info("创建控制器对象：RoleController");
    }

    /**
     * 执行获取角色列表的请求
     * 用于前端的角色下拉框
     * @return 返回JsonResult
     */
    // http://localhost:9081/roles/
    @ApiOperation("查询角色列表")
    @ApiOperationSupport(order = 100)
    @GetMapping("")
    public JsonResult<List<RoleListItemVO>> list(){
        log.debug("开始处理[查询角色列表]的请求");
        List<RoleListItemVO> list = iRoleService.list();
        return JsonResult.ok(list);
    }

    // http://localhost:9081/roles/cache/rebuild
    @ApiOperation("重建角色缓存")
    @ApiOperationSupport(order = 600)
    @PostMapping("/cache/rebuild")
    public JsonResult<Void> rebuildCache(){
        log.debug("开始处理[重建缓存]的请求,无参数");
        iRoleService.rebuildCache();
        return JsonResult.ok();
    }

    // http://localhost:9081/roles/id/select
    @ApiOperation("根据id查询角色详情")
    @ApiOperationSupport(order = 302)
    @ApiImplicitParam(name = "id",value = "角色id",required = true,dataType = "long")
    @GetMapping("/{id:[0-9]+}/select")
    public JsonResult<RoleStandardVO> standardById(@Range(min = 2,message = "查询失败,该id无效!")@PathVariable Long id){
        log.debug("开始处理[根据id{}查询品牌详情]的请求",id);
        RoleStandardVO roleStandardVO = iRoleService.standardById(id);
        return JsonResult.ok(roleStandardVO);
    }
}
