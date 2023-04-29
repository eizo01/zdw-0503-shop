package com.zdw.user.service.impl;

import com.zdw.user.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @Author: 曾德威
 * @Date: 2023/4/29
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;


    @Value("${spring.mail.from}")
    private String from;

    /**
     * 发送邮箱
     * @param to 接收人
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //邮件发送人
        mailMessage.setFrom(from);
        //邮件接收人
        mailMessage.setTo(to);
        //邮件主题
        mailMessage.setSubject(subject);
        //邮件内容
        mailMessage.setText(content);
        //发送邮件
        mailSender.send(mailMessage);

        log.info("邮件发成功:{}",mailMessage.toString());
    }
}
