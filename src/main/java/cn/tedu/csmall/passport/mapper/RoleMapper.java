package cn.tedu.csmall.passport.mapper;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {

    /**
     * 查询角色列表功能
     * @return 返回List集合
     */
    List<RoleListItemVO> list();
}
