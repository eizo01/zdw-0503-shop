package com.zdw.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class CouponRecordMessage implements Serializable {


    /**
     * 消息id
     */
    private String messageId;

    /**
     * 订单号
     */
    private String outTradeNo;


    /**
     * 库存锁定任务id
     */
    private Long taskId;


}
