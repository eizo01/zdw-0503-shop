package com.zdw.coupon.controller;


import com.zdw.coupon.service.CouponRecordService;
import com.zdw.coupon.vo.CouponRecordVO;
import com.zdw.enums.BizCodeEnum;
import com.zdw.util.JsonData;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 查询优惠券记录信息
     * 水平权限攻击：也叫作访问控制攻击,Web应用程序接收到用户请求，修改某条数据时，没有判断数据的所属人，
     * 或者在判断数据所属人时从用户提交的表单参数中获取了userid。
     * 导致攻击者可以自行修改userid修改不属于自己的数据
     * @param recordId
     * @return
     */
    @ApiOperation("根据id查询优惠券记录信息")
    @GetMapping("/detail/{record_id}")
    public JsonData findUserCouponRecordById(@PathVariable("record_id")long recordId ){

        CouponRecordVO couponRecordVO = couponRecordService.findById(recordId);
        return  couponRecordVO == null? JsonData.buildResult(BizCodeEnum.COUPON_NO_EXITS):JsonData.buildSuccess(couponRecordVO);
    }

}

