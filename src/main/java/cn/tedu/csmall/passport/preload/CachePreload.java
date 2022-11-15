package cn.tedu.csmall.passport.preload;

import cn.tedu.csmall.passport.mapper.RoleMapper;
import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
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
//@Component
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
        log.debug("CacheSchedule.run()");

        log.debug("准备删除Redis缓存中的品牌数据...");
        roleRedisRepository.deleteAll();// 清除缓存中的数据,防止缓存堆积过多,显示的列表数据冗余
        log.debug("删除Redis缓存中的品牌数据,完成!");

        log.debug("准备从数据库中读取品牌列表...");
        List<RoleListItemVO> list = roleMapper.list();
        log.debug("从数据库中读取品牌列表，完成！");

        log.debug("准备将品牌列表写入到Redis缓存...");
        roleRedisRepository.save(list);
        log.debug("将品牌列表写入到Redis缓存，完成！");
    }

}
