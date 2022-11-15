package cn.tedu.csmall.passport.mapper;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import cn.tedu.csmall.passport.pojo.vo.RoleStandardVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {

    /**
     * 查询角色列表功能
     * @return 返回List集合
     */
    List<RoleListItemVO> list();

    /**
     * 根据id查询角色详情
     * @param id 角色id
     * @return 返回角色详情VO类
     */
    RoleStandardVO standardById(Long id);
}
