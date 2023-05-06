package com.zdw.gateway;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * @Author: 曾德威
 * @Date: 2023/4/28
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */

@EnableDiscoveryClient
@SpringBootApplication

public class GateWayApplication {
    public static void main(String[] args) {


        SpringApplication. run(GateWayApplication.class,args);
    }
}
