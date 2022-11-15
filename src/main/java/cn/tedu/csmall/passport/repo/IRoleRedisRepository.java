package cn.tedu.csmall.passport.repo;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import cn.tedu.csmall.passport.pojo.vo.RoleStandardVO;

import java.util.List;

/**
 * 用来缓存Redis中角色数据的接口类
 *
 * @Author java@Wqy
 * @Version 0.0.1
 */
public interface IRoleRedisRepository {

    String ROLE_ITEM_KEY_PREFIX = "role:item";// 表示角色->item数据

    String ROLE_LIST_KEY = "role:list";// 用来存放角色的list列表key

    String ROLE_ITEM_KEYS_KEY = "role:item-keys";// 用来标记角色中的item中的key成员

    /**
     * 该方法用来存储一条角色数据,不做返回
     *
     * @param roleStandardVO 需要向Redis中存储的角色详情VO类
     */
    void save(RoleStandardVO roleStandardVO);

    /**
     * 该方法用来向Redis中存储多条角色数据
     *
     * @param roles 需要存储的角色列表
     */
    void save(List<RoleListItemVO> roles);

    /**
     * 删除Redis中的所有角色数据(item,list,role:list,role:item-keys)
     *
     * @return 返回删除的数量
     */
    Long deleteAll();

    /**
     * 向Redis中取出需要的item数据
     * 正常若向Redis中取数据需要对应的key值
     * 这里的key有前缀拼接,为了保证封装性,这里只让调用者传入id即可
     *
     * @param id 角色id
     * @return 返回角色详情VO类
     */
    RoleStandardVO get(Long id);

    /**
     * 该方法用来取出所有的角色列表,无参
     *
     * @return 返回角色列表的list集合
     */
    List<RoleListItemVO> list();

    /**
     * 该方法用来指定下标范围取出角色列表
     *
     * @param start 起始下标
     * @param end   末尾下标
     * @return 返回列表的list集合
     */
    List<RoleListItemVO> list(long start, long end);
}
