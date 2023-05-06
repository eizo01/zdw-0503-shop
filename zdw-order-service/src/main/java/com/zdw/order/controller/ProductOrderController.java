package com.zdw.order.controller;


import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.zdw.enums.BizCodeEnum;
import com.zdw.enums.ClientType;
import com.zdw.enums.ProductOrderPayTypeEnum;
import com.zdw.order.config.AlipayConfig;
import com.zdw.order.config.PayUrlConfig;
import com.zdw.order.model.ProductOrderDO;
import com.zdw.order.request.ConfirmOrderRequest;
import com.zdw.order.request.LockProductRequest;
import com.zdw.order.service.ProductOrderService;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.IIOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
@Api("订单模块")
@RestController
@Slf4j
@RequestMapping("/api/order/v1")
public class ProductOrderController {
    @Autowired
    private ProductOrderService productOrderService;

    @ApiOperation("提交订单")
    @PostMapping("/confirm")
    public JsonData confrimOrder(@ApiParam("订单对象")@RequestBody ConfirmOrderRequest orderRequest, HttpServletResponse response){
        JsonData jsonData = productOrderService.confirmOrder(orderRequest);
        if (jsonData.getCode() == 0){
            String client = orderRequest.getClientType();
            String payType = orderRequest.getPayType();

            //如果是支付宝支付，跳转页面
            if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.ALIPAY.name())){
                log.info("创建订单成功:{}",orderRequest.toString());
                if(client.equalsIgnoreCase(ClientType.H5.name())){
                    writeData(response,jsonData);

                }else if(client.equalsIgnoreCase(ClientType.APP.name())){
                    //APP SDK支付  TODO
                }

            }else if(payType.equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT.name())){
                    // todo 微信支付
            }

        }else {
            log.error("创建订单失败",jsonData.toString());
        }
        return jsonData;
    }



    @ApiOperation("查询订单状态")
    @GetMapping("/query_state")
    public JsonData queryProductOrderState(@RequestParam("out_trade_no")String outTradeNo){
         return productOrderService.queryProductOrderState(outTradeNo);

    }

    @Autowired
    private PayUrlConfig payUrlConfig;

    /**
     * 测试支付方法
     */
    @GetMapping("test_pay")
    public void testAlipay(HttpServletResponse response) throws AlipayApiException, IOException {

        HashMap<String,String> content = new HashMap<>();
        //商户订单号,64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        String no = UUID.randomUUID().toString();

        log.info("订单号:{}",no);
        content.put("out_trade_no", no);

        content.put("product_code", "FAST_INSTANT_TRADE_PAY");

        //订单总金额，单位为元，精确到小数点后两位
        content.put("total_amount", String.valueOf("111.99"));

        //商品标题/交易标题/订单标题/订单关键字等。 注意：不可使用特殊字符，如 /，=，&amp; 等。
        content.put("subject", "杯子");

        //商品描述，可空
        content.put("body", "好的杯子");

        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        content.put("timeout_express", "5m");


        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizContent(JSON.toJSONString(content));
        request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
        request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());

        AlipayTradeWapPayResponse alipayResponse  = AlipayConfig.getInstance().pageExecute(request);

        if(alipayResponse.isSuccess()){
            System.out.println("调用成功");

            String form = alipayResponse.getBody();

            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();

        } else {
            System.out.println("调用失败");
        }
    }


    /**
     * 把数据写到网页上
     * @param response
     * @param jsonData
     */
    private void writeData(HttpServletResponse response, JsonData jsonData) {

        try {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(jsonData.getData().toString());
            response.getWriter().flush();
            response.getWriter().close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

