package cn.tedu.csmall.passport.service.impl;

import cn.tedu.csmall.passport.mapper.RoleMapper;
import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import cn.tedu.csmall.passport.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    RoleMapper roleMapper;

    public RoleServiceImpl() {
        log.debug("创建业务对象：RoleServiceImpl");
    }

    /**
     * 实现Service接口中获取角色列表的功能
     * @return 返回List集合
     */
    @Override
    public List<RoleListItemVO> list() {
        log.debug("开始处理查询角色列表的业务!");
        List<RoleListItemVO> list = roleMapper.list();// 获取List列表集合
        Iterator<RoleListItemVO> iterator = list.iterator();// 创建一个迭代器
        while (iterator.hasNext()){ // 判断是否存在下一个元素
            RoleListItemVO item = iterator.next(); // 获取下一个对象元素
            if (item.getId()==1){ // 若该对象的id为1
                iterator.remove(); // 从集合中使用迭代器移除该对象
                break;// 跳出分支
            }
        }
        return list;
    }
}
