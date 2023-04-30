package com.zdw.user.config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: 曾德威
 * @Date: 2023/4/30
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@Configuration
@ConfigurationProperties(prefix = "qiniu.oss")
@Data
public class OSSConfig {

    private String accessKey;


    private String secretKey;


    private String bucketName;


    private String uploadUrl;

}
