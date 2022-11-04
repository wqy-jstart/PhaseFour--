package cn.tedu.csmall.passport.service.impl;

import cn.tedu.csmall.passport.ex.ServiceException;
import cn.tedu.csmall.passport.mapper.AdminMapper;
import cn.tedu.csmall.passport.pojo.dto.AdminAddNewDTO;
import cn.tedu.csmall.passport.pojo.entity.Admin;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.pojo.vo.AdminStandardVO;
import cn.tedu.csmall.passport.service.IAdminService;
import cn.tedu.csmall.passport.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {
    @Autowired
    private AdminMapper adminMapper;

    /**
     * 添加管理员的业务
     *
     * @param adminAddNewDTO 接收客户端的DTO对象
     */
    @Override
    public void adNew(AdminAddNewDTO adminAddNewDTO) {
        log.debug("开始处理添加[添加管理员]的业务!");
        int selectByUsername = adminMapper.countByUsername(adminAddNewDTO.getUsername());
        if (selectByUsername > 0) {
            String message = "添加管理员失败,用户名已经被占用!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERROR_CONFLICT, message);
        }
        int selectByPhone = adminMapper.countByPhone(adminAddNewDTO.getPhone());
        if (selectByPhone > 0) {
            String message = "添加管理员失败,手机号已经被占用!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERROR_CONFLICT, message);
        }
        int selectByEmail = adminMapper.countByEmail(adminAddNewDTO.getEmail());
        if (selectByEmail > 0) {
            String message = "添加管理员失败,电子邮箱已经被占用!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERROR_CONFLICT, message);
        }
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminAddNewDTO, admin);
        admin.setLoginCount(0);// 设置累计登录次数
        int rows = adminMapper.insert(admin);
        if (rows != 1) {
            String message = "服务器忙!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERR_INSERT, message);
        }
    }

    /**
     * 删除管理员的功能
     *
     * @param id 要删除的管理员id
     */
    @Override
    public void delete(Long id) {
        log.debug("开始处理[删除管理员]的业务,参数{}", id);
        // id值为1的管理不允许被删除
        if (id == 1) {
            String message = "删除失败,管理员数据不存在!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERR_DELETE, message);
        }
        // 判断该id是否存在
        AdminStandardVO admin = adminMapper.getStandardById(id);
        if (admin == null) {
            String message = "删除失败，该管理员id不存在！";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERR_NOT_FOUND, message);
        }

        int rows = adminMapper.deleteById(id);
        if (rows != 1) {
            String message = "服务器忙";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERR_DELETE, message);
        }
    }

    /**
     * 实现查询管理员列表的方法
     *
     * @return List
     */
    @Override
    public List<AdminListItemVO> list() {
        log.debug("开始处理[查询管理员列表]的业务!");
        List<AdminListItemVO> list = adminMapper.list();
        Iterator<AdminListItemVO> iterator = list.iterator();
        while (iterator.hasNext()) { // 判断是否含有下一个元素
            AdminListItemVO item = iterator.next();// 获取下一个元素
            if (item.getId() == 1) { // 判断该元素对象的id是否为1
                iterator.remove(); // 如果是,则移除该对象
                break; // 跳出分支
            }
        }
        return list;
    }

    /**
     * 处理启用管理员的业务
     *
     * @param id 启用的管理员id
     */
    @Override
    public void setEnable(Long id) {
        updateEnableById(id, 1);
    }

    /**
     * 处理禁用管理员的业务
     *
     * @param id 禁用的管理员id
     */
    @Override
    public void setDisable(Long id) {
        updateEnableById(id, 0);
    }

    /**
     * 该方法用来处理启用与禁用的逻辑
     *
     * @param id     id
     * @param enable 是否启用或禁用
     */
    private void updateEnableById(Long id, Integer enable) {
        String[] s = {"禁用", "启用"};
        // 判断id是否为1(系统管理员)
        if (id == 1) {
            String message = s[enable] + "管理员失败";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERR_NOT_FOUND, message);
        }
        // 根据id查询管理员详情
        AdminStandardVO adminStandardVO = adminMapper.getStandardById(id);
        if (adminStandardVO == null) {
            String message = s[enable] + "管理员失败,该id不存在!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERR_NOT_FOUND, message);
        }
        // 判断查询结果中的enable与方法参数enable是否相同
        if (enable.equals(adminStandardVO.getEnable())) {
            String message = s[enable] + "管理员失败,数据发生冲突!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERROR_CONFLICT, message);
        }

        // 创建admin对象,并封装id和enable这2个属性的值
        Admin admin = new Admin();
        admin.setId(id);
        admin.setEnable(enable);
        int rows = adminMapper.updateById(admin);
        if (rows != 1) {
            String message = s[enable] + "管理员失败,服务器忙!";
            throw new ServiceException(ServiceCode.ERR_UPDATE, message);
        }
        log.debug("设置成功!");
    }
}
