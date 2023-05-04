package com.zdw.user.fegin;

import com.zdw.user.request.NewUserCouponRequest;
import com.zdw.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "zdw-coupon-service")
public interface CouponFeignService {

    /**
     * 新用户注册发放优惠券
     * @param newUserCouponRequest
     * @return
     */
    @PostMapping("/api/coupon/v1/new_user_coupon")
    JsonData addNewUserCoupon(@RequestBody NewUserCouponRequest newUserCouponRequest);
}