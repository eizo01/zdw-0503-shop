package com.zdw.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdw.enums.BizCodeEnum;
import com.zdw.enums.StockTaskStateEnum;
import com.zdw.exception.BizException;
import com.zdw.model.ProductMessage;
import com.zdw.product.config.RabbitMQConfig;
import com.zdw.product.mapper.ProductMapper;
import com.zdw.product.mapper.ProductTaskMapper;
import com.zdw.product.model.ProductDO;
import com.zdw.product.model.ProductTaskDO;
import com.zdw.product.request.LockProductRequest;
import com.zdw.product.request.OrderItemRequest;
import com.zdw.product.service.ProductService;
import com.zdw.product.service.ProductTaskService;
import com.zdw.product.vo.ProductVO;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
@Slf4j
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
    @Autowired
    private ProductTaskMapper productTaskMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    /**
     * 锁定商品库存
     *
     *1)遍历商品，锁定每个商品购买数量
     *2)每一次锁定的时候，都要发送延迟消息
     *
     * @param lockProductRequest
     * @return
     */
    @Override
    public JsonData lockProductStock(LockProductRequest lockProductRequest) {
        String outTradeNo = lockProductRequest.getOrderOutTradeNo();
        List<OrderItemRequest> itemList  = lockProductRequest.getOrderItemList();
        // 拿到每个订单中的商品id
        List<Long> productIdList = itemList.stream().map(OrderItemRequest::getProductId).collect(Collectors.toList());
        //批量查询商品信息
        List<ProductVO> productsByIdBatch = this.findProductsByIdBatch(productIdList);
        //把商品id-key 商品信息是value
        Map<Long, ProductVO> productMapp = productsByIdBatch.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        for (OrderItemRequest item: itemList
             ) {
            int rows = productMapper.lockProductStock(item.getProductId(), item.getBuyNum());
            if(rows != 1){
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
            }else {
                //插入商品product_task
                ProductVO productVO = productMapp.get(item.getProductId());
                ProductTaskDO productTaskDO = new ProductTaskDO();
                productTaskDO.setBuyNum(item.getBuyNum());
                productTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
                productTaskDO.setProductId(item.getProductId());
                productTaskDO.setProductName(productVO.getTitle());
                productTaskDO.setOutTradeNo(outTradeNo);
                productTaskMapper.insert(productTaskDO);
                log.info("商品库存锁定-插入商品product_task成功:{}",productTaskDO);

                // 发送MQ延迟消息，介绍商品库存
                ProductMessage productMessage = new ProductMessage();

                productMessage.setOutTradeNo(outTradeNo);
                productMessage.setTaskId(productTaskDO.getId());
                rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(),rabbitMQConfig.getStockReleaseDelayRoutingKey(),productMessage);
                log.info("商品库存锁定信息延迟消息发送成功:{}",productMessage);
            }
        }



        return JsonData.buildSuccess();
    }

    private ProductVO beanProcess(ProductDO obj){
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(obj,productVO);
        // 剩余库存
        productVO.setStock(obj.getStock() - obj.getLockStock());

        return productVO;
    }
}
