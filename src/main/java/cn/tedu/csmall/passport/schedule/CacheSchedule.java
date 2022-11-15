package cn.tedu.csmall.passport.schedule;

import cn.tedu.csmall.passport.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 利用执行计划来完成缓存的加载,计划重建缓存的时间
 *
 * @Author java@Wqy
 * @Version 0.0.1
 */
@Slf4j
//@Component// 声明一个组件类
public class CacheSchedule {

    @Autowired
    private IRoleService roleService;

    public CacheSchedule() {
        log.debug("创建计划任务对象:CacheSchedule");
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void rebuildCache() {
        log.debug("开始执行[重建角色缓存]计划任务...");
        roleService.rebuildCache();
        log.debug("本次[重建角色缓存]计划任务执行完成!");
    }
}
