package com.zdw.order.service;

import com.zdw.order.model.ProductOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.order.request.ConfirmOrderRequest;
import com.zdw.util.JsonData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
public interface ProductOrderService extends IService<ProductOrderDO> {

    JsonData comfirmOrder(ConfirmOrderRequest confirmOrderRequest);
}
