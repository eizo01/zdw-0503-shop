package com.zdw.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zdw.enums.BizCodeEnum;
import com.zdw.order.model.ProductOrderDO;
import com.zdw.order.mapper.ProductOrderMapper;
import com.zdw.order.request.ConfirmOrderRequest;
import com.zdw.order.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return null;
    }


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
