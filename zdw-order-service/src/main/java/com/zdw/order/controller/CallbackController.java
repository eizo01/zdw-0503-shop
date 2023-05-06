package com.zdw.order.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zdw.enums.ProductOrderPayTypeEnum;
import com.zdw.order.config.AlipayConfig;
import com.zdw.order.service.ProductOrderService;
import com.zdw.util.CommonUtil;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: 曾德威
 * @Date: 2023/5/6
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@Api("订单回调通知模块")
@Controller
@RequestMapping("/api/callback/order/v1")
@Slf4j
public class CallbackController {

    @Autowired
    private ProductOrderService productOrderService;


    /**
     * 支付回调通知 post方式 去更新订单状态
     * @param request
     * @param response
     * @return
     */
    @PostMapping("alipay")
    public String alipayCallback(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> paramsMap = CommonUtil.convertRequestParamsToMap(request);

        log.info("支付回调通知结果:{}",paramsMap);
        try {
            boolean rsaCertCheckV1 = AlipaySignature.rsaCertCheckV1(paramsMap, AlipayConfig.ZFB_RUB_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGN_TYPE);
            if (rsaCertCheckV1){
                //通知结果确认成功，不然会一直通知，八次都没返回success就认为交易失败
                JsonData jsonData = productOrderService.handlerOrderCallbackMsg(ProductOrderPayTypeEnum.ALIPAY, paramsMap);
                if (jsonData.getCode() == 0){
                    return "success";
                }
            }
        } catch (AlipayApiException e) {
            log.info("支付宝回调验证签名失败:异常：{}，参数:{}",e,paramsMap);
        }
        return "failure";
    }








}

