package com.zdw.coupon.config;

import com.zdw.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginInterceptor())
                //拦截的路径
                .addPathPatterns("/api/coupon/*/**","/api/coupon_record/*/**")

                //排查不拦截的路径
                .excludePathPatterns("/api/coupon/*/page_coupon","/api/coupon/*/new_user_coupon");

    }



}
