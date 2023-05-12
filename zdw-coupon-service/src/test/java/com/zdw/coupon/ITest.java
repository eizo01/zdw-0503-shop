package com.zdw.coupon;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 曾德威
 * @Date: 2023/5/9
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouponApplication.class)
@Slf4j
public class ITest {
    @Test
    public static void main(String[] args) {

            //1、报错原因 size自增
//            List<String> list = new ArrayList<String>(10);
//            list.add(2, "1");
//            System.out.println(list.get(0));

            //2、排序
        List<String> strings = Arrays.asList("apple", "jk", "banner");
        Collections.sort(strings,(o1,o2) -> o1.compareTo(o2)); //从小到大 返回 -1 表示 a 小于 b，0 表示 a 等于 b ， 1 表示 a 大于 b。
        strings.forEach(System.out::println);

        // 3、过滤
        List<String> list = strings.stream().filter(s -> s.startsWith("a")).collect(Collectors.toList());
        list.forEach(System.out::println);
    }
}
