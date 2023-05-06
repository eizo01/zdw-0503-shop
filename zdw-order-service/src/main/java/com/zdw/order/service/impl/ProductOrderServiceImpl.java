package com.zdw.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zdw.constant.TimeConstant;
import com.zdw.enums.*;
import com.zdw.exception.BizException;
import com.zdw.interceptor.LoginInterceptor;
import com.zdw.model.LoginUser;
import com.zdw.model.OrderMessage;
import com.zdw.order.component.PayFactory;
import com.zdw.order.config.RabbitMQConfig;
import com.zdw.order.feign.CouponFeignSerivce;
import com.zdw.order.feign.ProductFeignService;
import com.zdw.order.feign.UserFeignService;
import com.zdw.order.mapper.ProductOrderItemMapper;
import com.zdw.order.model.ProductOrderDO;
import com.zdw.order.mapper.ProductOrderMapper;
import com.zdw.order.model.ProductOrderItemDO;
import com.zdw.order.request.ConfirmOrderRequest;
import com.zdw.order.request.LockCouponRecordRequest;
import com.zdw.order.request.LockProductRequest;
import com.zdw.order.request.OrderItemRequest;
import com.zdw.order.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.order.vo.*;
import com.zdw.util.CommonUtil;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.internal.requests.OrderingRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.BindException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Autowired
    private CouponFeignSerivce couponFeignSerivce;
    @Autowired
    private ProductOrderItemMapper productOrderItemMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    @Autowired
    private PayFactory payFactory;
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
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String  orderOutTradeNo = CommonUtil.getStringNumRandom(32);
    // 下单之前先查询用户的地址信息
        ProductOrderAddressVO addressVO = getUserAddress(orderRequest.getAddressId());
        log.info("收货地址信息：{}",addressVO);

        // TODO 未测试 token-bug-fegin
        // 获取用户加入购物车的商品信息 可以传一个订单号 解决清空购物车问题
        List<Long> productIdList = orderRequest.getProductIdList();
        JsonData cartItemDate = productFeignService.confirmOrderCartItem(productIdList);
        //  得到每个商品信息 封装成了map
        List<OrderItemVO> orderItemVOList = (List<OrderItemVO>) cartItemDate.getData(new TypeReference<OrderItemVO>(){});
        if (orderItemVOList == null){
            // 购物车商品不存在
            throw  new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_NOT_EXIST);
        }


        // 验证价格 减去优惠卷后的真正支持的价格
        this.checkPrice(orderItemVOList,orderRequest);

        //锁定优惠券
        this.lockCouponRecords(orderRequest ,orderOutTradeNo );

        //锁定库存
        this.lockProductStocks(orderItemVOList,orderOutTradeNo);

        // 创建订单 把地址 订单号 商品信息 传给订单服务
        ProductOrderDO productOrderDO = saveProductOrder(orderRequest, loginUser, orderOutTradeNo, addressVO);


        // 创建订单项
        saveProductOrderImtes(orderOutTradeNo,productOrderDO.getId(),orderItemVOList);

        // 发送延迟消息 用于自己关单
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOutTradeNo(orderOutTradeNo);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),rabbitMQConfig.getOrderCloseDelayRoutingKey(),orderMessage);
        // TODO 支付
        PayInfoVO payInfoVO = new PayInfoVO(orderOutTradeNo,productOrderDO.getPayAmount(),
                orderRequest.getPayType(),orderRequest.getClientType(),"orderOutTradeNo","这是一个订单号", TimeConstant.ORDER_PAY_TIMEOUT_MILLS);

        String payResult = payFactory.pay(payInfoVO);
        if(StringUtils.isNotBlank(payResult)){
            log.info("创建支付订单成功:payInfoVO={},payResult={}",payInfoVO,payResult);
            return JsonData.buildSuccess(payResult);
        }else {
            log.error("创建支付订单失败:payInfoVO={},payResult={}",payInfoVO,payResult);
            return JsonData.buildResult(BizCodeEnum.PAY_ORDER_FAIL);
        }

    }

    /**
     * 创建订单子项
     * @param orderOutTradeNo
     * @param orderId
     * @param orderItemList
     */
    private void saveProductOrderImtes(String orderOutTradeNo, Long orderId, List<OrderItemVO> orderItemList) {

        List<ProductOrderItemDO> list = orderItemList.stream().map(
                obj->{
                    ProductOrderItemDO itemDO = new ProductOrderItemDO();
                    itemDO.setBuyNum(obj.getBuyNum());
                    itemDO.setProductId(obj.getProductId());
                    itemDO.setProductImg(obj.getProductImg());
                    itemDO.setProductName(obj.getProductTitle());

                    itemDO.setOutTradeNo(orderOutTradeNo);
                    itemDO.setCreateTime(new Date());

                    //单价
                    itemDO.setAmount(obj.getAmount());
                    //总价
                    itemDO.setTotalAmount(obj.getTotalAmount());
                    itemDO.setProductOrderId(orderId);
                    return itemDO;
                }
        ).collect(Collectors.toList());


        productOrderItemMapper.insertBatch(list);
    }

    /**
     * 创建订单 把地址 订单号 商品信息 传给订单服务
     * @param orderRequest
     * @param loginUser
     * @param orderOutTradeNo
     * @param addressVO
     */
    private ProductOrderDO saveProductOrder(ConfirmOrderRequest orderRequest, LoginUser loginUser, String orderOutTradeNo, ProductOrderAddressVO addressVO) {
        ProductOrderDO productOrderDO = new ProductOrderDO();
        productOrderDO.setUserId(loginUser.getId());
        productOrderDO.setHeadImg(loginUser.getHeadImg());
        productOrderDO.setNickname(loginUser.getName());

        productOrderDO.setOutTradeNo(orderOutTradeNo);
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setDel(0);
        productOrderDO.setOrderType(ProductOrderTypeEnum.DAILY.name());
        // 实际支付的价格
        productOrderDO.setPayAmount(orderRequest.getRealPayAmount());
        // 总价 未使用优惠卷
        productOrderDO.setTotalAmount(orderRequest.getTotalAmount());
        productOrderDO.setPayType(ProductOrderPayTypeEnum.valueOf(orderRequest.getPayType()).name());
        productOrderDO.setState(ProductOrderStateEnum.NEW.name());

        productOrderDO.setReceiverAddress(JSON.toJSONString(addressVO));

        productOrderMapper.insert(productOrderDO);
        return productOrderDO;
    }

    /**
     * 锁定商品库存
     * @param orderItemVOList
     * @param orderOutTradeNo
     */
    private void lockProductStocks(List<OrderItemVO> orderItemVOList, String orderOutTradeNo) {
        List<OrderItemRequest> orderItemRequestList = orderItemVOList.stream().map(obj -> {
            OrderItemRequest orderItemRequest = new OrderItemRequest();
            orderItemRequest.setBuyNum(obj.getBuyNum());
            orderItemRequest.setProductId(obj.getProductId());
            return orderItemRequest;
        }).collect(Collectors.toList());

        LockProductRequest lockProductRequest = new LockProductRequest();
        lockProductRequest.setOrderOutTradeNo(orderOutTradeNo);
        lockProductRequest.setOrderItemList(orderItemRequestList);
        JsonData jsonData = productFeignService.lockProductStock(lockProductRequest);
        if(jsonData.getCode()!=0){
            log.error("锁定商品库存失败：{}",lockProductRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
        }
    }
    /**
     * 锁定优惠券
     * @param orderRequest
     * @param orderOutTradeNo
     */
    private void lockCouponRecords(ConfirmOrderRequest orderRequest, String orderOutTradeNo) {
        List<Long> lockCouponRecordIds = new ArrayList<>();

        if (orderRequest.getCouponRecordId() > 0){
            lockCouponRecordIds.add(orderRequest.getCouponRecordId());
            LockCouponRecordRequest lockCouponRecordRequest = new LockCouponRecordRequest();
            lockCouponRecordRequest.setLockCouponRecordIds(lockCouponRecordIds);
            lockCouponRecordRequest.setOrderOutTradeNo(orderOutTradeNo);

            JsonData jsonData = couponFeignSerivce.lockCouponRecords(lockCouponRecordRequest);
            if(jsonData.getCode()!=0){
                throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
            }
        }

    }

    /**
 * 验证价格
 * 1）统计全部商品的价格
 * 2) 获取优惠券(判断是否满足优惠券的条件)，总价再减去优惠券的价格 就是 最终的价格
 *
 * @param orderItemVOList
 * @param orderRequest
 */
    private void checkPrice(List<OrderItemVO> orderItemVOList, ConfirmOrderRequest orderRequest) {
        // 计算购物车 商品的全部总价
        BigDecimal realPayAmount = new BigDecimal("0");
        if (orderItemVOList != null){
            for (OrderItemVO orderItemVO : orderItemVOList){
                BigDecimal itemRealPayAmount = orderItemVO.getTotalAmount();
                realPayAmount = realPayAmount.add(itemRealPayAmount);
            }
        }
        //获取优惠券，判断是否可以使用 只有一张优惠卷 这里可以改成多张
        CouponRecordVO couponRecordVO = getCartCouponRecord(orderRequest.getCouponRecordId());
        // 计算购物车价格， 是否满足优惠卷满减条件
        if (couponRecordVO != null){
            // 计算是否满足满减
            if (realPayAmount.compareTo(couponRecordVO.getConditionPrice()) < 0){
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
            }
            // 优惠卷价格 比 商品价格大 【无门槛优惠卷】
            if (couponRecordVO.getPrice().compareTo(realPayAmount) > 0){
                realPayAmount = BigDecimal.ZERO;
            }else{
                realPayAmount = realPayAmount.subtract(couponRecordVO.getPrice());
            }
            // 验证价格 要与前端传来的价格一样
            if(realPayAmount.compareTo(orderRequest.getRealPayAmount()) !=0 ){
                log.error("订单验价失败：{}",orderRequest);
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
            }

        }
    }
    /**
     * 获取优惠券
     * @param couponRecordId
     * @return
     */
    private CouponRecordVO getCartCouponRecord(Long couponRecordId) {

        if(couponRecordId ==null || couponRecordId < 0){
            return null;
        }

        JsonData couponData = couponFeignSerivce.findUserCouponRecordById(couponRecordId);
        if(couponData.getCode()!=0){
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }

        if(couponData.getCode()==0){

            CouponRecordVO couponRecordVO = couponData.getData(new TypeReference<CouponRecordVO>(){});

            if(!couponAvailable(couponRecordVO)){
                log.error("优惠券使用失败");
                throw new BizException(BizCodeEnum.COUPON_UNAVAILABLE);
            }
            return couponRecordVO;
        }
        return null;
    }

    private boolean couponAvailable(CouponRecordVO couponRecordVO) {
        if (couponRecordVO.getUseState().equalsIgnoreCase(CouponStateEnum.NEW.name())){
            long currentTimestamp = CommonUtil.getCurrentTimestamp();// 拿到当前时间
            long end = couponRecordVO.getEndTime().getTime();;
            long start = couponRecordVO.getStartTime().getTime();
            if (currentTimestamp >= start && currentTimestamp <= end){
                return true;
            }
        }
        return false;

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

    @Override
    public boolean closePeoductOrder(OrderMessage orderMessage) {


        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no",orderMessage.getOutTradeNo()));

        if(productOrderDO == null){
            //1、订单不存在
            log.warn("直接确认消息，订单不存在:{}",orderMessage);
            return true;
        }
        // 消息重复投递
        if(productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())){
            //2、已经支付
            log.info("直接确认消息,订单已经支付:{}",orderMessage);
            return true;
        }
        // 订单取消
        //向第三方支付查询订单是否真的未支付
//        PayInfoVO payInfoVO = new PayInfoVO();
//        payInfoVO.setPayType(productOrderDO.getPayType());
//        payInfoVO.setOutTradeNo(orderMessage.getOutTradeNo());
//        = payFactory.queryPaySuccess(payInfoVO);
        String payResult = "";
        //结果为空，则未支付成功，本地取消订单
        if(StringUtils.isBlank(payResult)){
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(),ProductOrderStateEnum.CANCEL.name(),ProductOrderStateEnum.NEW.name());
            log.info("结果为空，则未支付成功，本地取消订单:{}",orderMessage);
            return true;
        }else {
            //不为空 - 支付成功，主动的把订单状态改成UI就支付，造成该原因的情况可能是支付通道回调有问题
            log.warn("支付成功，主动的把订单状态改成UI就支付，造成该原因的情况可能是支付通道回调有问题:{}",orderMessage);
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(),ProductOrderStateEnum.PAY.name(),ProductOrderStateEnum.NEW.name());
            return true;
        }




    }

    /**
     * 支付通知结果更新订单状态
     * @param payType
     * @param paramsMap
     * @return
     */
    @Override
    public JsonData handlerOrderCallbackMsg(ProductOrderPayTypeEnum payType, Map<String, String> paramsMap) {
        if (payType.name().equalsIgnoreCase(ProductOrderPayTypeEnum.ALIPAY.name())){

            //支付宝支付
            //获取商户订单号
            String outTradeNo = paramsMap.get("out_trade_no");
            //交易的状态
            String tradeStatus = paramsMap.get("trade_status");
            if("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)){
                //更新订单状态 加了乐观锁
                productOrderMapper.updateOrderPayState(outTradeNo,ProductOrderStateEnum.PAY.name(),ProductOrderStateEnum.NEW.name());
                return JsonData.buildSuccess();
            }
        }
        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_CALLBACK_NOT_SUCCESS);
    }
}
