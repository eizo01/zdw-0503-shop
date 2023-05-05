package com.zdw.order.controller;


import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.zdw.enums.BizCodeEnum;
import com.zdw.enums.ClientType;
import com.zdw.enums.ProductOrderPayTypeEnum;
import com.zdw.order.model.ProductOrderDO;
import com.zdw.order.request.ConfirmOrderRequest;
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
    @PostMapping("comfirm")
    public JsonData confrimOrder(@ApiParam("订单对象")@RequestBody ConfirmOrderRequest orderRequest, HttpServletResponse response){
        JsonData jsonData = productOrderService.comfirmOrder(orderRequest);
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

