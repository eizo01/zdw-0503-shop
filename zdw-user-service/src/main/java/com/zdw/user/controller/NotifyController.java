package com.zdw.user.controller;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

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


    /**
     * 获取图形验证码
     * @param request
     * @param response
     */
    @ApiOperation("获取图形验证码")
    @GetMapping("getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){

        String text = captchaProducer.createText();
        log.info("图像验证码{}",text);

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

}
