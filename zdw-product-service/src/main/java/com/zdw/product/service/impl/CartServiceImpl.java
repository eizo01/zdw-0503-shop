package com.zdw.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.zdw.constant.CacheKey;
import com.zdw.enums.BizCodeEnum;
import com.zdw.exception.BizException;
import com.zdw.interceptor.LoginInterceptor;
import com.zdw.model.LoginUser;
import com.zdw.product.request.CartItemRequest;
import com.zdw.product.service.CartService;
import com.zdw.product.service.ProductService;
import com.zdw.product.vo.CartItemVO;
import com.zdw.product.vo.CartVO;
import com.zdw.product.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @Author: 曾德威
 * @Date: 2023/5/3
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductService productService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addToCart(CartItemRequest cartItemRequest) {
        long productId = cartItemRequest.getProductId();
        int buyNum = cartItemRequest.getBuyNum();

        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        Object cacheObject = myCartOps.get(productId);
        String result = "";
        if (cacheObject != null){
            result = (String) cacheObject;
        }
        if (StringUtils.isBlank(result)){
            // 不存在新建购物项
            CartItemVO cartItemVO = new CartItemVO();
            ProductVO product = productService.findDetailById(productId);
            if (product == null){
                throw new BizException(BizCodeEnum.CART_FAIL);
            }

            cartItemVO.setAmount(product.getAmount());
            cartItemVO.setBuyNum(buyNum);
            cartItemVO.setProductId(productId);
            cartItemVO.setProductImg(product.getCoverImg());
            cartItemVO.setProductTitle(product.getTitle());
            myCartOps.put(productId, JSON.toJSONString(cartItemVO));

        }else{
            //存在商品，修改数量
            CartItemVO cartItem = JSON.parseObject(result,CartItemVO.class);
            cartItem.setBuyNum(cartItem.getBuyNum()+buyNum);
            myCartOps.put(productId,JSON.toJSONString(cartItem));
        }

    }
    // 清空购物车
    @Override
    public void clear() {
        String cartKey = getCartKey();
        redisTemplate.delete(cartKey);

    }
    @Override
    public void deleteItem(long productId) {
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        myCartOps.delete(productId);
    }


    @Override
    public CartVO getMyCart() {
        List<CartItemVO> cartItemVOList = buildCartItem(false);
        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);
        // 自己会根据属性计算
        return cartVO;
    }




    /**
     * 获取最新的购物项
     * @param latesPrice
     * @return
     */
    private List<CartItemVO> buildCartItem(boolean latesPrice) {
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();

        List<Object> itemList = myCartOps.values();

        List<CartItemVO> cartItemVOList = new ArrayList<>();
        List<Long> productIdList = new ArrayList<>();

        for (Object item : itemList){
            // 转化对象
            CartItemVO cartItemVO = JSON.parseObject((String)item, CartItemVO.class);
            cartItemVOList.add(cartItemVO);

            productIdList.add(cartItemVO.getProductId());
        }
        // false的话就不需要设置最新价格
        if (latesPrice){
            setProductLatesPrice(cartItemVOList,productIdList);
        }
        return cartItemVOList;

    }

    /**
     * 设置商品最新的价格
     * @param cartItemVOList
     * @param productIdList
     */
    private void setProductLatesPrice(List<CartItemVO> cartItemVOList, List<Long> productIdList) {
        List<ProductVO> productsByIdBatch = productService.findProductsByIdBatch(productIdList);
        // 根据id分组
        Map<Long, ProductVO> collect = productsByIdBatch.stream()
                .collect(Collectors.toMap(ProductVO::getId, Function.identity()));
        // 设置商品的最新的信息 价格 标头 图片连接
        cartItemVOList.stream().forEach(item ->{
            ProductVO productVO = collect.get(item.getProductId());
            item.setProductTitle(productVO.getTitle());
            item.setProductImg(productVO.getCoverImg());
            item.setAmount(productVO.getAmount());

        });

    }


    @Override
    public void changeItemNum(CartItemRequest cartItemRequest) {
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        Object cacheObj = myCartOps.get(cartItemRequest.getProductId());

        if(cacheObj==null){throw new BizException(BizCodeEnum.CART_FAIL);}
        String obj = (String)cacheObj;
        CartItemVO cartItemVO = JSON.parseObject(obj, CartItemVO.class);
        cartItemVO.setBuyNum(cartItemRequest.getBuyNum());
        myCartOps.put(cartItemRequest.getProductId(), JSON.toJSONString(cartItemVO));
    }

    /**
     * 确认购物车商品信息
     * @param productIdList
     * @return 全部的要购买的购物项
     */
    @Override
    public List<CartItemVO> confirmOrderCartItems(List<Long> productIdList) {
        // 获得全部的购物车的 购物项 并且更新商品的更新价格
        List<CartItemVO> cartItemVOList = buildCartItem(true);
        // 根据需要的商品id进行过滤 并清空对应的购物项
        List<CartItemVO> resultList = cartItemVOList.stream().filter(obj -> {
            if (productIdList.contains(obj.getProductId())) {
                deleteItem(obj.getProductId());
                return true;
            }
            return false;
        }).collect(Collectors.toList());




        return resultList;
    }

    /**
     * 得到我的购物车对象，输入key，得到空购物车
     * @return
     */
    private BoundHashOperations<String,Object,Object> getMyCartOps(){
        String cartKey = getCartKey();

        return redisTemplate.boundHashOps(cartKey);


    }
    /**
     * 购物车的key cart:1
     * @return
     */
    private String getCartKey() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String cacheKey = String.format(CacheKey.CART_KEY, loginUser.getId());

        return cacheKey;
    }


}
