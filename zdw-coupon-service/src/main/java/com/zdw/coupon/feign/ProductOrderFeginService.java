package com.zdw.coupon.feign;

import com.zdw.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: 曾德威
 * @Date: 2023/5/5
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@FeignClient(name = "zdw-order-service")
public interface ProductOrderFeginService {
    @GetMapping("/api/order/v1/query_state")
    JsonData queryProductOrderState(@RequestParam("out_trade_no")String outTradeNo);
}
