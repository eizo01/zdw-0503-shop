package com.zdw.coupon.mapper;

import com.zdw.coupon.model.CouponDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdw
 * @since 2023-05-01
 */
public interface CouponMapper extends BaseMapper<CouponDO> {
    /**
     * 扣减库存
     * @param couponId
     * @param stock
     * @return
     */
    int reduceStock(long couponId, Integer stock);
}
