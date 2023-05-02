package com.zdw.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdw.coupon.mapper.CouponMapper;
import com.zdw.coupon.model.CouponRecordDO;
import com.zdw.coupon.mapper.CouponRecordMapper;
import com.zdw.coupon.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.coupon.vo.CouponRecordVO;
import com.zdw.interceptor.LoginInterceptor;
import com.zdw.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    private CouponRecordVO beanProcess(CouponRecordDO obj) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(obj,couponRecordVO);

        return couponRecordVO;
    }


}
