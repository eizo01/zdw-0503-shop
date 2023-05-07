package com.zdw.order.service;

import com.zdw.enums.ProductOrderPayTypeEnum;
import com.zdw.model.OrderMessage;
import com.zdw.order.model.ProductOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.order.request.ConfirmOrderRequest;
import com.zdw.order.request.LockProductRequest;
import com.zdw.order.request.RepayOrderRequest;
import com.zdw.util.JsonData;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
public interface ProductOrderService extends IService<ProductOrderDO> {

    /**
     * 创建订单
     * @param orderRequest
     * @return
     */
    JsonData confirmOrder(ConfirmOrderRequest orderRequest);

    /**
     * 查询订单状态
     * @param outTradeNo
     * @return
     */
    JsonData queryProductOrderState(String outTradeNo);


    /**
     * 队列监听 定时关单
     * @param orderMessage
     * @return
     */
    boolean closePeoductOrder(OrderMessage orderMessage);
          /**
         * 支付结果回调通知
         * @param alipay
         * @param paramsMap
         * @return
         */
        JsonData handlerOrderCallbackMsg(ProductOrderPayTypeEnum alipay, Map<String, String> paramsMap);




        /**
         * 分页查询我的订单列表
         * @param page
         * @param size
         * @param state
         * @return
         */
        Map<String,Object> page(int page, int size, String state);


        /**
         * 订单二次支付
         * @param repayOrderRequest
         * @return
         */
        JsonData repay(RepayOrderRequest repayOrderRequest);
}
