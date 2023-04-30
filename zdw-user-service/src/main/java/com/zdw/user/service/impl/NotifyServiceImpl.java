package com.zdw.user.service.impl;

import com.zdw.constant.CacheKey;
import com.zdw.enums.BizCodeEnum;
import com.zdw.enums.SendCodeEnum;
import com.zdw.user.service.MailService;
import com.zdw.user.service.NotifyService;
import com.zdw.util.CheckUtil;
import com.zdw.util.CommonUtil;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
    private static final String CONTENT = "您的验证码是%s，有效时间是120秒，请保管好验证码，请勿让别人获取";
    /**
     * 验证码10分钟有效 生产用60s
     */
    private static final int CODE_EXPIRED = 60 * 1000 * 10;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 前置判断：是否重复发送
     * 1、存储验证码到缓存
     * 2、发送邮箱验证码
     * 后置存储发送记录
     *
     * @param sendCodeEnum
     * @param to
     * @return
     */
    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        // 如果不为空 判断是否是60s重复发送
        if (StringUtils.isNoneBlank(cacheValue)){
           // 拿到时间戳 毫秒为单位
            long time = Long.parseLong(cacheValue.split("_")[1]);
            if (CommonUtil.getCurrentTimestamp() - time < 1000 * 60){
                log.info("重复发送验证码,时间间隔:{} 秒",(CommonUtil.getCurrentTimestamp()-time)/1000);
            }
        }
        // 拼接验证码 222333_9990211210
        String code = CommonUtil.getRandomCode(6);
        String value = code + "_" + CommonUtil.getCurrentTimestamp();// 验证码和时间戳拼接成为value
        redisTemplate.opsForValue().set(cacheKey,value,CODE_EXPIRED, TimeUnit.MILLISECONDS);


        // 判断格式是否是邮箱
        if(CheckUtil.isEmail(to)){

            //邮箱验证码
            mailService.sendSimpleMail(to,SUBJECT,String.format(CONTENT,code));
            return JsonData.buildSuccess();

        }else if(CheckUtil.isPhone(to)){
            //短信验证码
        }

        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }

    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {
        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY,sendCodeEnum.name(),to);

        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isNotBlank(cacheValue)){
            // 拿到前面一位
            String cacheCode = cacheValue.split("_")[0];
            if(cacheCode.equals(code)){
                //删除验证码
                redisTemplate.delete(cacheKey);
                return true;
            }

        }
        return false;

    }
}
