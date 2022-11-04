package cn.tedu.csmall.passport.mapper;

import cn.tedu.csmall.passport.pojo.entity.AdminRole;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRoleMapper {

    /**
     * 批量插入管理员与角色的关联数据
     * @param adminRoleList 若干个管理员与角色的关联数据的集合
     * @return 受影响的行数
     */
    int insertBatch(AdminRole[] adminRoleList);
}
