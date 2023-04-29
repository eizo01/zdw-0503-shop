package com.zdw.user.service;

import com.zdw.enums.SendCodeEnum;
import com.zdw.util.JsonData;

/**
 * @Author: 曾德威
 * @Date: 2023/4/29
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
public interface NotifyService {

    JsonData sendCode(SendCodeEnum sendCodeEnum,String to);
}
