package com.zdw.coupon.service;

import com.zdw.coupon.model.CouponRecordDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.coupon.vo.CouponRecordVO;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdw
 * @since 2023-05-01
 */
public interface CouponRecordService extends IService<CouponRecordDO> {

    Map<String,Object> page(int page,int size);

    CouponRecordVO findById(long recordId);
}
