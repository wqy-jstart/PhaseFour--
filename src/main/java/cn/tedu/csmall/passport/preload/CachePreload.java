package cn.tedu.csmall.passport.preload;

import cn.tedu.csmall.passport.mapper.RoleMapper;
import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import cn.tedu.csmall.passport.pojo.vo.RoleStandardVO;
import cn.tedu.csmall.passport.repo.IRoleRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 该组件用于在项目启动之前加载Redis中的缓存数据
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
@Slf4j
@Component
public class CachePreload implements ApplicationRunner {

    // 注入角色的Mapper层接口
    @Autowired
    private RoleMapper roleMapper;

    // 注入角色的Redis缓存接口
    @Autowired
    private IRoleRedisRepository roleRedisRepository;

    public CachePreload(){
        log.debug("创建开机自动执行的组件对象:CachePreload");
    }

    // ApplicationRunner中的run()方法会在项目启动成功之后自动执行
    @Override
    public void run(ApplicationArguments args) throws Exception {
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
