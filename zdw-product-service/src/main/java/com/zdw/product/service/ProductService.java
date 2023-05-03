package com.zdw.product.service;

import com.zdw.product.model.ProductDO;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zdw.product.vo.ProductVO;

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

    Map<String, Object> pageProductList(int page, int size);

    ProductVO findDetailById(long productId);

    List<ProductVO> findProductsByIdBatch(List<Long> productIdList);
}
