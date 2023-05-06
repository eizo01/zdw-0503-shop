package com.zdw.order.mapper;

import com.zdw.order.model.ProductOrderItemDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
public interface ProductOrderItemMapper extends BaseMapper<ProductOrderItemDO> {

    /**
     * 批量插入
     * @param list
     */
    void insertBatch(@Param("orderItemList") List<ProductOrderItemDO> list);
}
