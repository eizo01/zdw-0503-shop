package com.zdw.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zdw.enums.BizCodeEnum;
import com.zdw.exception.BizException;
import com.zdw.interceptor.LoginInterceptor;
import com.zdw.model.LoginUser;
import com.zdw.order.feign.ProductFeignService;
import com.zdw.order.feign.UserFeignService;
import com.zdw.order.model.ProductOrderDO;
import com.zdw.order.mapper.ProductOrderMapper;
import com.zdw.order.request.ConfirmOrderRequest;
import com.zdw.order.request.LockProductRequest;
import com.zdw.order.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.order.vo.OrderItemVO;
import com.zdw.order.vo.ProductOrderAddressVO;
import com.zdw.order.vo.ProductOrderVO;
import com.zdw.util.CommonUtil;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.BindException;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
@Service
@Slf4j
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrderDO> implements ProductOrderService {
    @Autowired
    private ProductOrderMapper productOrderMapper;
    @Autowired
    private ProductFeignService productFeignService;
    /**
     * * 防重提交
     * * 用户微服务-确认收货地址
     * * 商品微服务-获取最新购物项和价格
     * * 订单验价
     *   * 优惠券微服务-获取优惠券
     *   * 验证价格
     * * 锁定优惠券
     * * 锁定商品库存
     * * 创建订单对象
     * * 创建子订单对象
     * * 发送延迟消息-用于自动关单
     * * 创建支付信息-对接三方支付
     *
     * @param orderRequest
     * @return
     */
    @Override
    public JsonData comfirmOrder(ConfirmOrderRequest orderRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String  orderOutTradeNo = CommonUtil.getStringNumRandom(32);
    // 下单之前先查询用户的地址信息
        ProductOrderAddressVO addressVO = getUserAddress(orderRequest.getAddressId());
        log.info("收货地址信息：{}",addressVO);

        // TODO 未测试 token-bug-fegin
        // 获取用户加入购物车的商品
        List<Long> productIdList = orderRequest.getProductIdList();
        JsonData cartItemDate = productFeignService.confirmOrderCartItem(productIdList);
        List<OrderItemVO> orderItemVOList = (List<OrderItemVO>) cartItemDate.getData(new TypeReference<OrderItemVO>(){});
        if (orderItemVOList == null){
            // 购物车商品不存在
            throw  new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_NOT_EXIST);
        }
        return null;
    }
    @Autowired
    private UserFeignService userFeignService;

    /**
     * 查询地址详情
     * @param addressId
     * @return
     */
    private ProductOrderAddressVO getUserAddress(long addressId) {
        JsonData detail = userFeignService.detail(addressId);
        if (detail.getCode() != 0){
            log.error("获取地址失败");
            throw  new BizException(BizCodeEnum.ADDRESS_NO_EXITS);
        }
        ProductOrderAddressVO addressVO = detail.getData(new TypeReference<ProductOrderAddressVO>(){});
        return addressVO;
    }

    /**
     *  查询订单状态
     * @param outTradeNo
     * @return
     */
    @Override
    public JsonData queryProductOrderState(String outTradeNo) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", outTradeNo));
        if (productOrderDO == null){
            return JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST);
        }else{
            return JsonData.buildSuccess(productOrderDO.getState());
        }

    }


}
