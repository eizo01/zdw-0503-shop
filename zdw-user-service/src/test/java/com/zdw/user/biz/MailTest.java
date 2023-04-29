package com.zdw.user.biz;

import com.zdw.user.UserApplication;
import com.zdw.user.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: 曾德威
 * @Date: 2023/4/29
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class MailTest {


    @Autowired
    private MailService mailService;
    @Test
    public void testMail(){
        mailService.sendSimpleMail("2399492494@qq.com","欢迎加入电商0503","但是我还没上线，您别急");
    }
}
