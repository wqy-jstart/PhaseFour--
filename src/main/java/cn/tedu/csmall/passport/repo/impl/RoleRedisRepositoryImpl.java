package cn.tedu.csmall.passport.repo.impl;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import cn.tedu.csmall.passport.pojo.vo.RoleStandardVO;
import cn.tedu.csmall.passport.repo.IRoleRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 向Redis中缓存角色数据的实现类
 *
 * @Author java@Wqy
 * @Version 0.0.1
 */
@Slf4j
@Repository
public class RoleRedisRepositoryImpl implements IRoleRedisRepository {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public RoleRedisRepositoryImpl() {
        log.debug("创建处理缓存的数据访问实现类对象:RoleRedisRepositoryImpl");
    }

    // 实现向Redis中写入数据的业务
    @Override
    public void save(RoleStandardVO roleStandardVO) {
        String key = ROLE_ITEM_KEY_PREFIX + roleStandardVO.getId();// 这样存储便于在Redis中归类呈现
        // 向Redis的品牌中的role:item-keys里,添加该次添加的角色key值,为了删除时直接遍历里面item的key值作删除
        log.debug("向Set集合中存入此次查找的Key值");
        redisTemplate.opsForSet().add(ROLE_ITEM_KEYS_KEY, key);
        redisTemplate.opsForValue().set(key, roleStandardVO);
        log.debug("向缓存中存入品牌详情成功!");
    }

    // 实现向Redis中写入多条品牌数据的业务
    @Override
    public void save(List<RoleListItemVO> roles) {
        String key = ROLE_LIST_KEY;// 用来存放角色列表的key
        ListOperations<String, Serializable> ops = redisTemplate.opsForList();
        for (RoleListItemVO role : roles) {
            ops.rightPush(key, role);// 调用rightPush()方法向Redis中存入角色列表
        }

    }

    // 实现删除Redis中所有数据的业务(集合.item,keys)
    @Override
    public Long deleteAll() {
        // 获取到role:item-keys中所有的item的key
        Set<Serializable> members = redisTemplate.opsForSet().members(ROLE_ITEM_KEYS_KEY);
        Set<String> keys = new HashSet<>();// 创建一个Set集合
        for (Serializable member : members) {
            keys.add((String) member); // 将遍历的所有item的key放到Set集合中,例如role:item1
        }
        // 将List集合和保存Key的Set的Key也添加到集合中
        keys.add(ROLE_LIST_KEY);// role:list
        keys.add(ROLE_ITEM_KEYS_KEY);// role:item-keys
        return redisTemplate.delete(keys);
    }

    // 实现根据key向Redis中获取一条角色数据
    @Override
    public RoleStandardVO get(Long id) {
        Serializable serializable = redisTemplate.opsForValue().get(ROLE_ITEM_KEY_PREFIX + id);
        RoleStandardVO roleStandardVO = null; // 预先声明一个角色详情的引用
        if (serializable != null) { // 判断根据id返回的角色数据是否为null
            if (serializable instanceof RoleStandardVO) { // 判断返回的数据类型与RoleStandardVO是否存在可转换的关系
                roleStandardVO = (RoleStandardVO) serializable;// 强制转换
            }
        }
        return roleStandardVO;
    }

    // 实现向Redis中查询所有角色列表的业务
    @Override
    public List<RoleListItemVO> list() {
        long start = 0;
        long end = -1;
        return list(start, end);
    }

    // 实现根据下标向Redis中获取角色数据的方法
    @Override
    public List<RoleListItemVO> list(long start, long end) {
        String key = ROLE_LIST_KEY;// 拿到角色列表的key值
        ListOperations<String, Serializable> ops = redisTemplate.opsForList();// 创建一个ListOperation
        List<Serializable> list = ops.range(key, start, end);// 调用range()方法,传入key和下标,返回list集合
        List<RoleListItemVO> roles = new ArrayList<>();
        for (Serializable item : list) {
            roles.add((RoleListItemVO) item);
        }
        return roles;// 装满后作出返回
    }
}
