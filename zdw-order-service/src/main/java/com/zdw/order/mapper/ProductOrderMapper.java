package com.zdw.order.mapper;

import com.zdw.order.model.ProductOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
public interface ProductOrderMapper extends BaseMapper<ProductOrderDO> {

    void updateOrderPayState(@Param("outTradNo") String outTradeNo,@Param("newState") String newState,@Param("oldState")  String oldState);
}
