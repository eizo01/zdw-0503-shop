package com.zdw.coupon.service;

import com.zdw.coupon.model.CouponRecordDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.coupon.request.LockCouponRecordRequest;
import com.zdw.coupon.vo.CouponRecordVO;
import com.zdw.model.CouponRecordMessage;
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
public interface CouponRecordService extends IService<CouponRecordDO> {

    /**
     * 分页查询领劵记录
     *
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> page(int page, int size);

    /**
     * 根据id查询详情
     *
     * @param recordId
     * @return
     */
    CouponRecordVO findById(long recordId);

    /**
     * 锁定优惠券
     *
     * @param recordRequest
     * @return
     */
    JsonData lockCouponRecords(LockCouponRecordRequest recordRequest);


    /**
     * 释放优惠券记录
     * @param recordMessage
     * @return
     */
    boolean releaseCouponRecord(CouponRecordMessage recordMessage);
}
