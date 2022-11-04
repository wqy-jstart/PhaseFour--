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
     * 根据id删除管理员
     * @param id 要删除的管理员id
     */
    void  delete(Long id);

    /**
     * 查询管理员列表的方法
     * @return List
     */
    List<AdminListItemVO> list();

    /**
     * 启用管理员
     * @param id 启用的管理员id
     */
    void setEnable(Long id);

    /**
     * 禁用管理员
     * @param id 禁用的管理员id
     */
    void setDisable(Long id);
}
