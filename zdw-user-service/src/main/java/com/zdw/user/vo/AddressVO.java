package com.zdw.user.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 通常用来封装业务对象的属性，也可以用于表示数据库表中的一条记录或多个表之间的查询结果集。VO对象通常包含了业务对象的属性和一些辅助属性，它主要用于在业务逻辑层和展现层之间传递数据。
 */

@Data
public class AddressVO {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 是否默认收货地址：0->否；1->是
     */
    @JsonProperty("default_status")
    private Integer defaultStatus;

    /**
     * 收发货人姓名
     */
    @JsonProperty("receive_name")
    private String receiveName;

    /**
     * 收货人电话
     */
    private String phone;

    /**
     * 省/直辖市
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String region;

    /**
     * 详细地址
     */

    @JsonProperty("detail_address")
    private String detailAddress;


}
