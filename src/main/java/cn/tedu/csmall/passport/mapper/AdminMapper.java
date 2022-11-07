package cn.tedu.csmall.passport.mapper;

import cn.tedu.csmall.passport.pojo.entity.Admin;
import cn.tedu.csmall.passport.pojo.vo.AdminListItemVO;
import cn.tedu.csmall.passport.pojo.vo.AdminLoginInfoVO;
import cn.tedu.csmall.passport.pojo.vo.AdminStandardVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMapper {

    /**
     * 添加管理员
     * @param admin 插入的Admin对象
     * @return 返回影响的条数
     */
    int insert(Admin admin);

    /**
     * 根据用户名查询数据
     * @param username 用户名
     * @return 返回影响的条数
     */
    int countByUsername(String username);

    /**
     * 根据手机号查询数据
     * @param phone 电话号码
     * @return 返回影响的条数
     */
    int countByPhone(String phone);

    /**
     * 根据电子邮箱查询数据
     * @param email 电子邮箱
     * @return 返回影响的条数
     */
    int countByEmail(String email);

    /**
     * 批量插入数据
     * @param admin 要插入的Admin对象List集合
     * @return 返回插入影响的数量
     */
    int insertBatch(List<Admin> admin);

    /**
     * 根据id删除数据
     * @param id 要删除的管理员id
     * @return 返回删除影响的数量
     */
    int deleteById(Long id);

    /**
     * 批量删除管理员
     * @param ids 要删除管理员的id数组
     * @return 返回删除影响的数量
     */
    int deleteByIds(Long[] ids);

    /**
     * 根据id修改数据
     * @param admin 修改的Admin对象
     * @return 返回修改后影响的数据数量
     */
    int updateById(Admin admin);

    /**
     * 查询数据的条数
     * @return 返回查询的数量
     */
    int count();

    /**
     * 根据id查询管理员的信息
     * @param id 要查询的管理员id
     * @return 返回查询到的管理员信息
     */
    AdminStandardVO getStandardById(Long id);

    /**
     * 查询管理员列表的数据
     * @return List集合
     */
    List<AdminListItemVO> list();

    /**
     * 根据用户名查询登录的信息
     * @param username 用户名
     * @return 返回登录的VO类信息
     */
    AdminLoginInfoVO getLoginInfoByUsername(String username);

}
