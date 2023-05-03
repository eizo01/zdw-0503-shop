package com.zdw.product.service.impl;

import com.zdw.user.model.ProductDO;
import com.zdw.user.mapper.ProductMapper;
import com.zdw.user.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductDO> implements ProductService {

}
