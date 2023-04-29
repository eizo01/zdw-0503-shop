package com.zdw.user.service.impl;

import com.zdw.enums.BizCodeEnum;
import com.zdw.enums.SendCodeEnum;
import com.zdw.user.service.MailService;
import com.zdw.user.service.NotifyService;
import com.zdw.util.CheckUtil;
import com.zdw.util.CommonUtil;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 曾德威
 * @Date: 2023/4/29
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private MailService mailService;
    /**
     * 标题
     */
    private static final String SUBJECT = "0503电商拉新平台验证码";
    /**
     * 内容
     */
    private static final String CONTENT = "您的验证码是%s，有效时间是60秒，请保管好验证码，请勿让别人获取";
    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
        if(CheckUtil.isEmail(to)){
            //拼接验证码 2322_324243232424324
            String code = CommonUtil.getRandomCode(6);

            //邮箱验证码
            mailService.sendSimpleMail(to,SUBJECT,String.format(CONTENT,code));
            return JsonData.buildSuccess();

        }else if(CheckUtil.isPhone(to)){
            //短信验证码
        }

        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }
}
