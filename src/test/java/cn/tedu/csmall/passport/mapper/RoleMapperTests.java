package cn.tedu.csmall.passport.mapper;

import cn.tedu.csmall.passport.pojo.vo.RoleListItemVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class RoleMapperTests {

    @Autowired
    RoleMapper roleMapper;

    @Test
    void select(){
        List<RoleListItemVO> list = roleMapper.list();
        for (RoleListItemVO item : list) {
            log.debug("{}",item);
        }
    }
}
