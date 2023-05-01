package com.zdw.coupon.controller;


import com.zdw.coupon.service.CouponService;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
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
@Api(tags = "优惠卷模块")
@RestController
@RequestMapping("/api/conpon/v1")
public class CouponController {
    @Autowired
    private CouponService couponService;
    /**
     * 促销展示分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("分页查询优惠券")
    @GetMapping("page_coupon")
    public JsonData pageCouponList(
            @ApiParam(value = "当前页") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "每页显示多少条") @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Map<String, Object> pageMap = couponService.pageCouponActivity(page, size);
        return JsonData.buildSuccess(pageMap);
    }


}

