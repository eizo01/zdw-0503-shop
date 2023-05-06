package com.zdw.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class ProductMessage implements Serializable {


    /**
     * 消息队列id
     */
    private long messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 库存锁定taskId
     */
    private long taskId;
}
