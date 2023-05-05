package com.zdw.product.service;

import com.zdw.model.ProductMessage;
import com.zdw.product.model.ProductDO;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.product.request.LockProductRequest;
import com.zdw.product.vo.ProductVO;
import com.zdw.util.JsonData;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
public interface ProductService {
    /**
     * 分页查询商品列表
     * @param page
     * @param size
     * @return
     */
    Map<String,Object> pageProductList(int page, int size);

    /**
     * 根据id找商品详情
     * @param productId
     * @return
     */
    ProductVO findDetailById(long productId);

    /**
     * 根据id批量查询商品
     * @param productIdList
     * @return
     */
    List<ProductVO> findProductsByIdBatch(List<Long> productIdList);

    /**
     * 锁定商品库存
     * @param lockProductRequest
     * @return
     */
    JsonData lockProductStock(LockProductRequest lockProductRequest);

    boolean releaseProductStock(ProductMessage productMessage);

//    /**
//     * 释放商品库存
//     * @param productMessage
//     * @return
//     */
//    boolean releaseProductStock(ProductMessage productMessage);
}
