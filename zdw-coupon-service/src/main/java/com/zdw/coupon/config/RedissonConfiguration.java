package com.zdw.coupon.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author: 曾德威
 * @Date: 2023/5/4
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@Configuration
public class RedissonConfiguration {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        // 设置redis连接主机地址和端口
        config.useSingleServer().setPassword("zdw961898").setAddress("redis://110.40.169.113:8000");
        // 创建RedissonClient实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}