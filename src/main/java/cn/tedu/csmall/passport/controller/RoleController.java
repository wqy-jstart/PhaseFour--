package cn.tedu.csmall.passport.controller;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import cn.tedu.csmall.passport.service.IRoleService;
import cn.tedu.csmall.passport.web.JsonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiOperation("查询角色列表")
    @ApiOperationSupport(order = 100)
    @GetMapping("")
    public JsonResult<List<RoleListItemVO>> list(){
        log.debug("开始处理[查询角色列表]的请求");
        List<RoleListItemVO> list = iRoleService.list();
        return JsonResult.ok(list);
    }
}
