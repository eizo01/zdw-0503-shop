package com.zdw.product.mapper;

import com.zdw.product.model.ProductDO;

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
public interface ProductMapper extends BaseMapper<ProductDO> {
    /**
     * 锁定当前商品库存
     * @param productId
     * @param buyNum
     * @return
     */
    int lockProductStock(@Param("productId") long productId,@Param("buyNum") int buyNum);

    /**
     * 解锁，并且恢复之前的库存
     * @param productId
     * @param buyNum
     * @return
     */
    int unlockProductStock(@Param("productId") long productId,@Param("buyNum") int buyNum);
}
