package cn.tedu.csmall.passport.service;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;

import java.util.List;

public interface IRoleService {

    /**
     * 该方法用来查询角色列表
     * @return List
     */
    List<RoleListItemVO> list();
}
