package cn.tedu.csmall.passport.service;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 处理角色数据的业务接口
 *
 * @author java.@wqy
 * @version 0.0.1
 */
@Transactional
public interface IRoleService {

    /**
     * 该方法用来查询角色列表
     * @return List
     */
    List<RoleListItemVO> list();
}
