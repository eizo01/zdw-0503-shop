package com.zdw.user.controller;

import com.google.code.kaptcha.Producer;
import com.zdw.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 曾德威
 * @Date: 2023/4/29
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */

@Api(tags = "通知模块")
@RequestMapping("/api/user/v1")
@RestController
@Slf4j
public class NotifyController {
    @Autowired
    private Producer captchaProducer;
    @Autowired
    private StringRedisTemplate redisTemplate;

//    @Autowired
//    private NotifyService notifyService;
    /**
     * 图形验证码有效期10分钟
     */
    private static final long CAPTCHA_CODE_EXPIRED = 60 * 1000 * 10;
    /**
     * 获取图形验证码
     * @param request
     * @param response
     * @deprecated 同一个浏览器会覆盖，不同浏览器不会覆盖redis
     */
    @ApiOperation("获取图形验证码")
    @GetMapping("getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){

        String text = captchaProducer.createText();
        log.info("图像验证码{}",text);

        redisTemplate.opsForValue().set(getCaptchaKey(request),text,CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);
        BufferedImage bufferedImage = captchaProducer.createImage(text);
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage,"jpg",outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.info("获取图像验证码异常");
        }

    }


    /**
     * 获得缓存的key
     */

    private String getCaptchaKey(HttpServletRequest request) {
        String ipAddr = CommonUtil.getIpAddr(request);

        String userAgent = request.getHeader("User-Agent");
        // key 前面作为名字规范，后面作为区分，以访问地址和用户代理（用浏览器访问还是客户端访问）
        String key = "User-service:captcha:" + CommonUtil.MD5(ipAddr + userAgent);
        log.info("ip={}",ipAddr);
        log.info("userAgent={}",userAgent);
        log.info("key={}",key);

        return key;

    }

}
