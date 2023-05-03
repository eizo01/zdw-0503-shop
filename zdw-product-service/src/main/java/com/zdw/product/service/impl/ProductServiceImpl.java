package com.zdw.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.product.mapper.ProductMapper;
import com.zdw.product.model.ProductDO;
import com.zdw.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
@Service
public class ProductServiceImpl  implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Map<String, Object> pageProductList(int page, int size) {
        // 先构建pageInfo
        Page<ProductDO> pageInfo = new Page<>(page,size);
        // Ipage 根据自己的需求来指定分页查询的东西
        IPage<ProductDO> productDOPage = productMapper.selectPage(pageInfo, null);
        // 封装map数据
        Map<String,Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record",productDOPage.getTotal());
        pageMap.put("total_page",productDOPage.getPages());
        pageMap.put("current_data",productDOPage.getRecords());

        return pageMap;
    }
}
