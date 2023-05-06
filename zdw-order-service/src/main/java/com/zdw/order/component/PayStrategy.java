package com.zdw.order.component;

import com.zdw.order.vo.PayInfoVO;


/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

public interface PayStrategy {


    /**
     * 下单
     * @return
     */
    String unifiedorder(PayInfoVO payInfoVO);


    /**
     *  退款
     * @param payInfoVO
     * @return
     */
    default String refund(PayInfoVO payInfoVO){return "";}


    /**
     * 查询支付是否成功
     * @param payInfoVO
     * @return
     */
    default String queryPaySuccess(PayInfoVO payInfoVO){return "";}


}
