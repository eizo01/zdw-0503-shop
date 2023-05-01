package com.zdw.user.controller;


import com.zdw.enums.BizCodeEnum;
import com.zdw.user.model.AddressDO;
import com.zdw.user.request.AddressAddReqeust;
import com.zdw.user.service.AddressService;
import com.zdw.user.vo.AddressVO;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 电商-公司收发货地址表 前端控制器
 * </p>
 *
 * @author zdw
 * @since 2023-04-28
 */
@Api(tags = "收货地址模块")
@RestController
@RequestMapping("/api/address/v1/")
public class AddressController {
    @Autowired
    private AddressService addressService;


//    /**
//     * 根据id查找地址详情
//     * @param addressId
//     * @return
//     */
//    @ApiOperation("根据id查找地址详情")
//    @GetMapping("find/{address_id}")
//    public Object detail(
//            @ApiParam(value = "地址id" ,required = true)
//            @PathVariable("address_id") Long addressId){
//        AddressVO detail = addressService.detail(addressId);
//        return JsonData.buildSuccess(detail);
//    }

    /**
     * 根据id查找地址详情
     * @param addressId
     * @return
     */
    @ApiOperation("根据id查找地址详情")
    @GetMapping("/find/{address_id}")
    public JsonData detail(
            @ApiParam(value = "地址id",required = true)
            @PathVariable("address_id") long addressId){

        AddressVO addressVO = addressService.detail(addressId);

        return addressVO == null ? JsonData.buildResult(BizCodeEnum.ADDRESS_NO_EXITS):JsonData.buildSuccess(addressVO);
    }

    @ApiOperation("新增收货地址")
    @PostMapping("add")
    public JsonData add(@ApiParam("地址对象") @RequestBody AddressAddReqeust addressAddReqeust){

        addressService.add(addressAddReqeust);

        return JsonData.buildSuccess();
    }



    /**
     * 删除指定收货地址
     * @param addressId
     * @return
     */
    @ApiOperation("删除指定收货地址")
    @DeleteMapping("/del/{address_id}")
    public JsonData del(
            @ApiParam(value = "地址id",required = true)
            @PathVariable("address_id")int addressId){

        int rows = addressService.del(addressId);

        return rows == 1 ? JsonData.buildSuccess(): JsonData.buildResult(BizCodeEnum.ADDRESS_DEL_FAIL);
    }


    /**
     * 查询用户的全部收货地址
     * @return
     */
    @ApiOperation("查询用户的全部收货地址")
    @GetMapping("/list")
    public JsonData findUserAllAddress(){

        List<AddressVO> list = addressService.listUserAllAddress();

        return JsonData.buildSuccess(list);
    }



}

