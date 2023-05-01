package com.zdw.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdw.coupon.model.CouponDO;
import com.zdw.coupon.mapper.CouponMapper;
import com.zdw.coupon.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.coupon.vo.vo.CouponVO;
import com.zdw.enums.CouponCategoryEnum;
import com.zdw.enums.CouponPublishEnum;
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
public class CouponServiceImpl extends ServiceImpl<CouponMapper, CouponDO> implements CouponService {
    @Autowired
    private CouponMapper couponMapper;


    @Override
    public Map<String, Object> pageCouponActivity(int page, int size) {

        Page<CouponDO> pageInfo = new Page<>(page,size);

        IPage<CouponDO> couponDOIPage =  couponMapper.selectPage(pageInfo, new QueryWrapper<CouponDO>()
                .eq("publish",CouponPublishEnum.PUBLISH)
                .eq("category", CouponCategoryEnum.PROMOTION)
                .orderByDesc("create_time"));


        Map<String,Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", couponDOIPage.getTotal());
        //总页数
        pageMap.put("total_page",couponDOIPage.getPages());

        pageMap.put("current_data",couponDOIPage.getRecords().stream().map(obj->beanProcess(obj)).collect(Collectors.toList()));


        return pageMap;
    }

    /**
     * 把优惠卷do 转化为 vo
     * @return
     */
    private CouponVO beanProcess(CouponDO couponDO) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO,couponVO);
        return couponVO;
    }
}
