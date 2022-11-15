package cn.tedu.csmall.passport.service.impl;

import cn.tedu.csmall.passport.ex.ServiceException;
import cn.tedu.csmall.passport.mapper.RoleMapper;
import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import cn.tedu.csmall.passport.pojo.vo.RoleStandardVO;
import cn.tedu.csmall.passport.repo.IRoleRedisRepository;
import cn.tedu.csmall.passport.service.IRoleService;
import cn.tedu.csmall.passport.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired// 调用Redis的实现类接口
    private IRoleRedisRepository roleRedisRepository;

    public RoleServiceImpl() {
        log.debug("创建业务对象：RoleServiceImpl");
    }

    /**
     * 实现Service接口中获取角色列表的功能(实现过程中移除Id为1的角色----系统管理员)
     * @return 返回List集合
     */
    @Override
    public List<RoleListItemVO> list() {
        log.debug("开始处理查询角色列表的业务!");
        List<RoleListItemVO> list = roleRedisRepository.list();
        Iterator<RoleListItemVO> iterator = list.iterator();
        while (iterator.hasNext()){ // 判断是否含有下一个元素
            RoleListItemVO item = iterator.next(); // 获取该元素
            if (item.getId()==1){ // 如果该角色的id为1
                iterator.remove();// 移除该元素
                break;// 跳出循环
            }
        }
        return list;// 作出返回
    }

    /**
     * 执行根据id查询角色的业务
     *
     * @param id 角色id
     * @return 返回角色业务的Vo类
     */
    @Override
    public RoleStandardVO standardById(Long id) {
        log.debug("开始根据角色id:{}来查询角色详情的业务", id);
        // 根据id从缓存中获取数据
        log.debug("将从Redis中获取相关数据");
        RoleStandardVO role = roleRedisRepository.get(id);
        // 判断获取到的结果是否不为null
        if (role != null) {
            // 是:直接返回
            log.debug("命中缓存,即将返回:{}", role);
            return role;
        }
        // 无缓存数据,从数据库中查找数据
        log.debug("未命中缓存,即将向数据库中查询数据");
        role = roleMapper.standardById(id);
        // 判断查询到的结果是否为null
        if (role == null){
            // 抛出异常
            String message = "查询失败,尝试访问的数据不存在!";
            log.debug(message);
            throw new ServiceException(ServiceCode.ERR_NOT_FOUND,message);
        }
        // 将查询结果写入到缓存,并返回
        log.debug("从数据库中查询到有效结果,将查询结果存入到Redis中:{}", role);
        roleRedisRepository.save(role);
        log.debug("开始返回结果!");
        return role;
    }

    /**
     * 重建缓存
     */
    @Override
    public void rebuildCache() {
        log.debug("准备删除Redis缓存中的角色数据...");
        roleRedisRepository.deleteAll();// 清除缓存中的数据,防止缓存堆积过多,显示的列表数据冗余
        log.debug("删除Redis缓存中的品牌数据,完成!");

        log.debug("准备从数据库中读取品牌列表...");
        List<RoleListItemVO> list = roleMapper.list(); // 利用Mapper查询列表放到List中
        log.debug("从数据库中读取品牌列表，完成！");

        log.debug("准备将品牌列表写入到Redis缓存...");
        roleRedisRepository.save(list);// 利用角色的Redis接口调用save转入要保存的列表,加载到缓存中
        log.debug("将品牌列表写入到Redis缓存，完成！");

        log.debug("准备将各角色详情写入Redis缓存...");
        for (RoleListItemVO roleListVO : list) {// 遍历拿到的角色列表
            Long id = roleListVO.getId();// 获取遍历的每个角色列表的id
            RoleStandardVO roleStandardVO = roleMapper.standardById(id);// 利用拿到的id来查询对应的角色详情
            roleRedisRepository.save(roleStandardVO);// 将每一个角色详情放到Redis缓存中
        }
        log.debug("将各品牌详情写入到Redis缓存中,完成!");
    }


}
