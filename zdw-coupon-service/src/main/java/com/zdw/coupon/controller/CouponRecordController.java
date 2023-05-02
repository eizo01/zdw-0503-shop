package com.zdw.coupon.controller;


import com.zdw.coupon.service.CouponRecordService;
import com.zdw.util.JsonData;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zdw
 * @since 2023-05-01
 */
@RestController
@RequestMapping("/couponRecordDO")
public class CouponRecordController {
    @Autowired
    private CouponRecordService couponRecordService;

    @ApiOperation("分页查询个人领券记录")
    @GetMapping("/page_coupon")
    public JsonData pageCouponList(
            @ApiParam(value = "当前页") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "每页显示多少条") @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Map<String, Object> map = couponRecordService.page(page, size);

        return JsonData.buildSuccess(map);
    }
}

