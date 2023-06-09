package com.zdw.product.controller;

import com.zdw.product.request.CartItemRequest;
import com.zdw.product.service.CartService;
import com.zdw.product.vo.CartItemVO;
import com.zdw.product.vo.CartVO;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "购物车")
@RestController
@RequestMapping("/api/cart/v1")
public class CartController {


    @Autowired
    private CartService cartService;


    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public JsonData addToCart(@ApiParam("购物项") @RequestBody CartItemRequest cartItemRequest){

        cartService.addToCart(cartItemRequest);

        return JsonData.buildSuccess();
    }



    @ApiOperation("修改购物车数量")
    @PostMapping("/change")
    public JsonData changeItemNum( @ApiParam("购物项") @RequestBody  CartItemRequest cartItemRequest){

        cartService.changeItemNum(cartItemRequest);

        return JsonData.buildSuccess();
    }




    @ApiOperation("清空购物车")
    @DeleteMapping("/clear")
    public JsonData cleanMyCart(){

        cartService.clear();
        return JsonData.buildSuccess();
    }



    @ApiOperation("查看我的购物车")
    @GetMapping("/mycart")
    public JsonData findMyCart(){

        CartVO cartVO = cartService.getMyCart();

        return JsonData.buildSuccess(cartVO);
    }



    @ApiOperation("删除购物项")
    @DeleteMapping("/delete/{product_id}")
    public JsonData deleteItem( @ApiParam(value = "商品id",required = true)@PathVariable("product_id")long productId ){

        cartService.deleteItem(productId);
        return JsonData.buildSuccess();
    }


    /**
     * 用于订单服务，确认订单，获取对应的商品项详情信息
     *
     * 会清空购物车的商品数据
     * @param productIdList
     * @return
     */
    @ApiOperation("获取对应订单的商品信息")
    @PostMapping("confirm_order_cart_items")
    public JsonData confirmOrderCartItems(@ApiParam("商品id列表") @RequestBody List<Long> productIdList){
        // 购物项
        List<CartItemVO> cartItemVOList = cartService.confirmOrderCartItems(productIdList);

        return JsonData.buildSuccess(cartItemVOList);

    }




}
