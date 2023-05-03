package com.zdw.product.service;

import com.zdw.product.request.CartItemRequest;
import com.zdw.product.vo.CartVO;

/**
 * @Author: 曾德威
 * @Date: 2023/5/3
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */

public interface CartService {


    void addToCart(CartItemRequest cartItemRequest);

    void changeItemNum(CartItemRequest cartItemRequest);

    void clear();

    CartVO getMyCart();

    void deleteItem(long productId);
}
