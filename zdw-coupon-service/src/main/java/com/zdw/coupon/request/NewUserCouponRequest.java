package com.zdw.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 可以做更全的模板 ： 加类型 时间期限
 */
@ApiModel
@Data
public class NewUserCouponRequest {


    @ApiModelProperty(value = "用户Id",example = "19")
    @JsonProperty("user_id")
    private long userId;


    @ApiModelProperty(value = "名称",example = "曾德威")
    @JsonProperty("name")
    private String name;



}
