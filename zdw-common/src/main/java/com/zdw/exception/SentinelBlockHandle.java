package com.zdw.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.zdw.enums.BizCodeEnum;
import com.zdw.util.CommonUtil;
import com.zdw.util.JsonData;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 曾德威
 * @Date: 2023/5/7
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */

@Component
public class SentinelBlockHandle implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        JsonData jsonData = null;
        if (e instanceof FlowException){
             jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_FLOW);

        }else if (e instanceof DegradeException){
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_DEGRADE);
        }else if (e instanceof AuthorityException){
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_AUTH);
        }
        httpServletResponse.setStatus(200);

        CommonUtil.sendJsonMessage(httpServletResponse,jsonData);
    }
}
