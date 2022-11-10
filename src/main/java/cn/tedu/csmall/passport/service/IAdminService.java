package cn.tedu.csmall.passport.service;

import cn.tedu.csmall.passport.pojo.dto.AdminAddNewDTO;
import cn.tedu.csmall.passport.pojo.dto.AdminLoginDTO;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 处理管理员数据的业务接口
 *
 * @Author java@Wqy
 * @Version 0.0.1
 */
@Transactional
public interface IAdminService {

    /**
     * 管理员登录
     *
     * @param adminLoginDTO 封装了管理员的用户名和密码
     * @return 登录成功生成匹配的JWT
     */
    String login(AdminLoginDTO adminLoginDTO);

    /**
     * 添加管理员的方法
     * @param adminAddNewDTO 添加管理员的DTO类
     */
    void adNew(AdminAddNewDTO adminAddNewDTO);

    /**
     * 根据id删除管理员
     * @param id 要删除的管理员id
     */
    void delete(Long id);

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
