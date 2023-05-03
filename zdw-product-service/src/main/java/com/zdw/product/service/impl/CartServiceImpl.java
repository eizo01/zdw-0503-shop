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
import com.zdw.product.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
    private StringRedisTemplate redisTemplate;

    @Override
    public void addToCart(CartItemRequest cartItemRequest) {
        long productId = cartItemRequest.getProductId();
        int buyNum = cartItemRequest.getBuyNum();
        String cartKey = getCartKey();
        BoundHashOperations<String, Object, Object> myCartOps = getMyCartOps();
        Object cacheObject = myCartOps.get(productId);
        String result = "";
        if (cacheObject != null){
            result = (String) cacheObject;
        }
        if (StringUtils.isNoneBlank(result)){
            // 不存在新建购物项
            CartItemVO cartItemVO = new CartItemVO();
            ProductVO product = productService.findDetailById(productId);
            if (product != null){
                throw new BizException(BizCodeEnum.CART_FAIL);
            }

            cartItemVO.setAmount(product.getAmount());
            cartItemVO.setBuyNum(buyNum);
            cartItemVO.setProductId(productId);
            cartItemVO.setProductImg(product.getCoverImg());
            cartItemVO.setProductTitle(product.getTitle());
            myCartOps.put(productId, JSON.toJSONString(cartItemVO));

        }else{
            CartItemVO cartItem = JSON.parseObject(result, CartItemVO.class);
            cartItem.setBuyNum(cartItem.getBuyNum() + buyNum);
            myCartOps.put(productId,JSON.toJSONString(cartItem));
        }


    }
    private BoundHashOperations<String,Object,Object> getMyCartOps(){
        String cartKey = getCartKey();
        // 得到我的购物车对象
        return redisTemplate.boundHashOps(cartKey);


    }
    /**
     * 购物车的key
     * @return
     */
    private String getCartKey() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String cacheKey = String.format(CacheKey.CART_KEY, loginUser.getId());

        return cacheKey;
    }
}
