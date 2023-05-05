package com.zdw.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdw.coupon.config.RabbitMQConfig;
import com.zdw.coupon.mapper.CouponMapper;
import com.zdw.coupon.mapper.CouponTaskMapper;
import com.zdw.coupon.model.CouponRecordDO;
import com.zdw.coupon.mapper.CouponRecordMapper;
import com.zdw.coupon.model.CouponTaskDO;
import com.zdw.coupon.request.LockCouponRecordRequest;
import com.zdw.coupon.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.coupon.vo.CouponRecordVO;
import com.zdw.enums.BizCodeEnum;
import com.zdw.enums.CouponCategoryEnum;
import com.zdw.enums.CouponStateEnum;
import com.zdw.enums.StockTaskStateEnum;
import com.zdw.exception.BizException;
import com.zdw.interceptor.LoginInterceptor;
import com.zdw.model.CouponRecordMessage;
import com.zdw.model.LoginUser;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdw
 * @since 2023-05-01
 */
@Service
@Slf4j
public class CouponRecordServiceImpl extends ServiceImpl<CouponRecordMapper, CouponRecordDO> implements CouponRecordService {
    @Autowired
    private CouponRecordMapper couponRecordMapper;


    @Override
    public Map<String, Object> page(int page, int size) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        Page<CouponRecordDO> pageInfo =  new Page<>(page,size);
        IPage<CouponRecordDO> recordDOIPage = couponRecordMapper.selectPage(pageInfo, new QueryWrapper<CouponRecordDO>()
                .eq("user_id", loginUser.getId())
                .orderByDesc("create_time"));
        HashMap<String, Object> map = new HashMap<>(3);

        map.put("total_record",recordDOIPage.getTotal());
        map.put("total_page",recordDOIPage.getPages());
        map.put("current_data",recordDOIPage.getRecords().stream().map(obj ->beanProcess(obj)).collect(Collectors.toList()));
        return map;
    }

    @Override
    public CouponRecordVO findById(long recordId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        CouponRecordDO recordDO = couponRecordMapper.selectOne(new QueryWrapper<CouponRecordDO>().eq("id", recordId).eq("user_id", loginUser.getId()));
        if(recordDO == null){return null;}

        CouponRecordVO couponRecordVO = beanProcess(recordDO);
        return couponRecordVO;
    }
    @Autowired
    private CouponTaskMapper couponTaskMapper;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 锁定优惠券
     *
     * 1）锁定优惠券记录
     * 2）task表插入记录
     * 3）发送延迟消息
     *
     * @param recordRequest
     * @return
     */
    @Override
    public JsonData lockCouponRecords(LockCouponRecordRequest recordRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        List<Long> lockcouponRecordIds = recordRequest.getLockCouponRecordIds();
        String outTradeNo = recordRequest.getOrderOutTradeNo();

        int updateRows = couponRecordMapper.lockUseStateBatch(loginUser.getId(), CouponStateEnum.NEW.name(), lockcouponRecordIds);

        List<CouponTaskDO> couponTaskDoLists = lockcouponRecordIds.stream().map(couponRecordId -> {
            CouponTaskDO couponTaskDO = new CouponTaskDO();
            couponTaskDO.setCreateTime(new Date());
            couponTaskDO.setCouponRecordId(couponRecordId);
            couponTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
            couponTaskDO.setOutTradeNo(outTradeNo);
            return couponTaskDO;
        }).collect(Collectors.toList());
        int insertRows = couponTaskMapper.insertBatch(couponTaskDoLists);
        log.info("优惠券记录锁定updateRows={}",updateRows);
        log.info("新增优惠券记录task insertRows={}",insertRows);

        if (lockcouponRecordIds.size() == insertRows && insertRows == updateRows){
            // 发送延迟消息
            for (CouponTaskDO couponTaskDO : couponTaskDoLists) {
                CouponRecordMessage couponRecordMessage = new CouponRecordMessage();
                couponRecordMessage.setOutTradeNo(outTradeNo);
                couponRecordMessage.setTaskId(couponTaskDO.getId());

                rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),rabbitMQConfig.getCouponReleaseDelayRoutingKey(),couponRecordMessage);
                log.info("优惠券锁定消息发送成功:{}",couponRecordMessage.toString());

            }
            return JsonData.buildSuccess();
        }else{

            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    private CouponRecordVO beanProcess(CouponRecordDO obj) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(obj,couponRecordVO);

        return couponRecordVO;
    }


}
