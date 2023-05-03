package com.zdw.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: 曾德威
 * @Date: 2023/5/3
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@MapperScan("com.zdw.product.mapper")
@SpringBootApplication
public class ProductAppliction {
    public static void main(String[] args) {
        SpringApplication.run(ProductAppliction.class,args);
    }
}
