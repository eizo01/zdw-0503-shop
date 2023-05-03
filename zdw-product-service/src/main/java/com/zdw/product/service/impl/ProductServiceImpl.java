package com.zdw.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.product.mapper.ProductMapper;
import com.zdw.product.model.ProductDO;
import com.zdw.product.service.ProductService;
import com.zdw.product.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 商品分页
     * @param page
     * @param size
     * @return
     */
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
        pageMap.put("current_data",productDOPage.getRecords().stream().map(obj ->beanProcess(obj)).collect(Collectors.toList()));

        return pageMap;
    }

    /**
     * 根据id找商品详情
     * @param productId
     * @return
     */
    @Override
    public ProductVO findDetailById(long productId) {

        ProductDO productDO = productMapper.selectById(productId);

        return beanProcess(productDO);

    }

    /**
     * 批量查询
     * @param productIdList
     * @return
     */
    @Override
    public List<ProductVO> findProductsByIdBatch(List<Long> productIdList) {

        List<ProductDO> productDOList =  productMapper.selectList(new QueryWrapper<ProductDO>().in("id",productIdList));

        List<ProductVO> productVOList = productDOList.stream().map(obj->beanProcess(obj)).collect(Collectors.toList());

        return productVOList;
    }

    private ProductVO beanProcess(ProductDO obj){
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(obj,productVO);
        // 剩余库存
        productVO.setStock(obj.getStock() - obj.getLockStock());

        return productVO;
    }
}
