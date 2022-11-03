package cn.tedu.csmall.passport.service;

import cn.tedu.csmall.passport.pojo.dto.AdminAddNewDTO;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IAdminService {

    /**
     * 添加管理员的方法
     * @param adminAddNewDTO
     */
    void adNew(AdminAddNewDTO adminAddNewDTO);

    /**
     * 查询管理员列表的方法
     * @return List
     */
    List<AdminListItemVO> list();
}
