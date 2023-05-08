package com.zdw.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zdw.coupon.mapper.CouponRecordMapper;
import com.zdw.coupon.model.CouponDO;
import com.zdw.coupon.mapper.CouponMapper;
import com.zdw.coupon.model.CouponRecordDO;
import com.zdw.coupon.request.NewUserCouponRequest;
import com.zdw.coupon.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.coupon.vo.CouponVO;
import com.zdw.enums.BizCodeEnum;
import com.zdw.enums.CouponCategoryEnum;
import com.zdw.enums.CouponPublishEnum;
import com.zdw.enums.CouponStateEnum;
import com.zdw.exception.BizException;
import com.zdw.interceptor.LoginInterceptor;
import com.zdw.model.LoginUser;
import com.zdw.util.CommonUtil;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.*;

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
    @Autowired
    private RedissonClient redissonClient;




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
     * 领卷接口
     * 1、获取优惠价是否存在
     * 2、检验优惠卷是否领取
     * 3、扣减库存
     * 4、保存领卷记录
     * @param couponId
     * @param category
     * @return
     */
    @Transactional(rollbackFor=Exception.class,propagation= Propagation.REQUIRED)
    @Override
    public JsonData addCoupon(long couponId, CouponCategoryEnum category) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String uuid = CommonUtil.generateUUID();
        //"  lock:coupon:"+couponId+userid，锁粒度更细化
        String lockKey = "lock:coupon:" + couponId+":"+loginUser.getId();
        RLock lock = redissonClient.getLock(lockKey);
        // 默认30s过期，有watch dog 有自动续期
        lock.lock();



        try {
            CouponDO couponDO = couponMapper.selectOne(new QueryWrapper<CouponDO>()
                    .eq("id",couponId)
                    .eq("category",category.name()));


            //优惠券是否可以领取
            this.checkCoupon(couponDO,loginUser.getId());


            //构建领劵记录
            CouponRecordDO couponRecordDO = new CouponRecordDO();
            BeanUtils.copyProperties(couponDO,couponRecordDO);
            couponRecordDO.setCreateTime(new Date());
            couponRecordDO.setUseState(CouponStateEnum.NEW.name());
            couponRecordDO.setUserId(loginUser.getId());
            couponRecordDO.setUserName(loginUser.getName());
            couponRecordDO.setCouponId(couponId);
            couponRecordDO.setId(null);


            //扣减库存  TODO
            int rows = couponMapper.reduceStock(couponId);
            // int glag = 1 / 0; 本地事务会出现的问题
            if(rows==1){
                //库存扣减成功才保存记录
                couponRecordMapper.insert(couponRecordDO);

            }else {
                log.warn("发放优惠券失败:{},用户:{}",couponDO,loginUser);

                throw  new BizException(BizCodeEnum.COUPON_NO_STOCK);
            }
        }      finally {
                lock.unlock();
                log.info("解锁成功");
            }


        return JsonData.buildSuccess();
    }


    /**
     * 微服务调用的时候没有传token
     * 本地直接调用发放优惠卷方法，需要构造一个登录用户存储在threadlocal
     * 新用户 每人领取一个
     * @param newUserCouponRequest
     * @return
     */
    @Transactional(rollbackFor=Exception.class,propagation=Propagation.REQUIRED)
    @Override
    public JsonData initNewUserCoupon(NewUserCouponRequest newUserCouponRequest) {
        // 先封装用户信息
        LoginUser loginUser = new LoginUser();
        loginUser.setId(newUserCouponRequest.getUserId());
        loginUser.setName(newUserCouponRequest.getName());
        LoginInterceptor.threadLocal.set(loginUser);
        // 查询新用户有哪些优惠卷
        List<CouponDO> couponDOList = couponMapper.selectList(new QueryWrapper<CouponDO>().eq(
                "category", CouponCategoryEnum.NEW_USER.name())
         .eq("publish", CouponPublishEnum.PUBLISH.name()));

        // 遍历新注册的优惠卷 但需要注意幂等操作，把优惠卷添加到用户（账户）记录里
        for (CouponDO couponDO : couponDOList){
            this.addCoupon(couponDO.getId(),CouponCategoryEnum.NEW_USER);
        }

        return JsonData.buildSuccess();
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
    @Resource
    private CouponRecordMapper couponRecordMapper;

    /**
     * 优惠券检查
     * @param couponDO
     * @param userId
     */
    private void checkCoupon(CouponDO couponDO,long userId){

        if(couponDO==null){
            throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
        }

        //库存是否足够
        if(couponDO.getStock()<=0){
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }

        //判断是否是否发布状态
        if(!couponDO.getPublish().equals(CouponPublishEnum.PUBLISH.name())){
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }

        //是否在领取时间范围
        long time = CommonUtil.getCurrentTimestamp();
        long start = couponDO.getStartTime().getTime();
        long end = couponDO.getEndTime().getTime();
        if(time<start || time>end){
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }

        //用户是否超过限制
        int recordNum =  couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
                .eq("coupon_id",couponDO.getId())
                .eq("user_id",userId));

        if(recordNum >= couponDO.getUserLimit()){
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }

    }
}
