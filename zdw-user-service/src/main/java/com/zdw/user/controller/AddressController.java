package com.zdw.user.controller;


import com.zdw.user.model.AddressDO;
import com.zdw.user.service.AddressService;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 电商-公司收发货地址表 前端控制器
 * </p>
 *
 * @author zdw
 * @since 2023-04-28
 */
@Api(tags = "收获地址模块")
@RestController
@RequestMapping("/api/adress/v1/")
public class AddressController {
    @Autowired
    private AddressService addressService;


    /**
     * 根据id查找地址详情
     * @param addressId
     * @return
     */
    @ApiOperation("根据id查找地址详情")
    @GetMapping("find/{address_id}")
    public Object detail(
            @ApiParam(value = "地址id" ,required = true)
            @PathVariable("address_id") Long addressId){
        AddressDO detail = addressService.detail(addressId);
        return JsonData.buildSuccess(detail);
    }

}

