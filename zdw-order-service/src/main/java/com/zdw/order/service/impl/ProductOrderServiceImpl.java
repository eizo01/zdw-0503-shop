package com.zdw.order.service.impl;

import com.zdw.order.model.ProductOrderDO;
import com.zdw.order.mapper.ProductOrderMapper;
import com.zdw.order.request.ConfirmOrderRequest;
import com.zdw.order.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.util.JsonData;
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
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrderDO> implements ProductOrderService {

    @Override
    public JsonData comfirmOrder(ConfirmOrderRequest confirmOrderRequest) {
        return null;
    }
}
