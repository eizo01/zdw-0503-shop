package com.zdw.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class OrderMessage implements Serializable {

    /**
     * 消息id
     */
    private Long messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

}
