package cn.tedu.csmall.passport;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class IntegerTests {

    @Test
    public void test(){
        // 包装类在[-128,127]之间equals()与==效果相同,且hashCode()会返回字面值
        Integer i1 = 127;
        Integer i2 = 127;
        System.out.println(i1.hashCode());// 127
        System.out.println(i2.hashCode());// 127
        System.out.println(i1==i2);// true
        System.out.println(i1.equals(i2));// true

        Integer i3 = 128;
        Integer i4 = 128;
        System.out.println(i3.hashCode());// 128
        System.out.println(i4.hashCode());// 128
        System.out.println(i3 == i4);// false
        System.out.println(i3.equals(i4));// true
    }
}
