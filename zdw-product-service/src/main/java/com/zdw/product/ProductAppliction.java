package com.zdw.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: 曾德威
 * @Date: 2023/5/3
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.zdw.product.mapper")
@SpringBootApplication
public class ProductAppliction {
    public static void main(String[] args) {
        SpringApplication.run(ProductAppliction.class,args);
    }
}
