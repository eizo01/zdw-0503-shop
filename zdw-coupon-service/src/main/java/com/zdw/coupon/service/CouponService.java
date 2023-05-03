package com.zdw.coupon.service;

import com.zdw.coupon.model.CouponDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.coupon.request.NewUserCouponRequest;
import com.zdw.enums.CouponCategoryEnum;
import com.zdw.util.JsonData;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdw
 * @since 2023-05-01
 */
public interface CouponService extends IService<CouponDO> {
    /**
     * 促销展示分页
     * @param page
     * @param size
     * @return
     */
    Map<String,Object> pageCouponActivity(int page,int size);

    JsonData addCoupon(long couponId, CouponCategoryEnum category);

    JsonData initNewUserCoupon(NewUserCouponRequest newUserCouponRequest);
}
