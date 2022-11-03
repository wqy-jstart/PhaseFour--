package cn.tedu.csmall.passport.service.impl;

import cn.tedu.csmall.passport.ex.ServiceException;
import cn.tedu.csmall.passport.mapper.AdminMapper;
import cn.tedu.csmall.passport.pojo.dto.AdminAddNewDTO;
import cn.tedu.csmall.passport.pojo.entity.Admin;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.service.IAdminService;
import cn.tedu.csmall.passport.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public void adNew(AdminAddNewDTO adminAddNewDTO) {
        log.debug("开始处理添加[添加管理员]的业务!");
        int selectByUsername = adminMapper.countByUsername(adminAddNewDTO.getUsername());
        if (selectByUsername>0){
            String message = "添加管理员失败,用户名已经被占用!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERROR_CONFLICT,message);
        }
        int selectByPhone = adminMapper.countByPhone(adminAddNewDTO.getPhone());
        if (selectByPhone>0){
            String message = "添加管理员失败,手机号已经被占用!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERROR_CONFLICT,message);
        }
        int selectByEmail = adminMapper.countByEmail(adminAddNewDTO.getEmail());
        if (selectByEmail>0){
            String message = "添加管理员失败,电子邮箱已经被占用!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERROR_CONFLICT,message);
        }
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminAddNewDTO,admin);
        admin.setLoginCount(0);// 设置累计登录次数
        int rows = adminMapper.insert(admin);
        if (rows!=1){
            String message = "服务器忙!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERR_INSERT,message);
        }
    }

    /**
     * 实现查询管理员列表的方法
     * @return List
     */
    @Override
    public List<AdminListItemVO> list() {
        log.debug("开始处理[查询管理员列表]的业务!");
        return adminMapper.list();
    }
}
