package com.zdw.coupon;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: 曾德威
 * @Date: 2023/4/28
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.zdw.coupon.mapper")
public class CouponApplication {
    public static void main(String[] args) {


        SpringApplication.run(CouponApplication.class,args);
    }
}
