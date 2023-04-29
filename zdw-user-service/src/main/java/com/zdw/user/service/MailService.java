package com.zdw.user.service;

import org.springframework.mail.SimpleMailMessage;

/**
 * @Author: 曾德威
 * @Date: 2023/4/29
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
public interface MailService {

    public void sendSimpleMail(String to,String subject,String content);
}
