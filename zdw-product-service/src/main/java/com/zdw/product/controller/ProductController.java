package com.zdw.product.controller;


import com.zdw.product.service.ProductService;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
@RestController
@Api(tags = "商品模块")
@RequestMapping("/api/product/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("商品列表分页查询")
    @GetMapping("page")
    public JsonData pageProductList(
            @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "1") int page,
            @ApiParam(value = "每页显示多少条") @RequestParam(value = "size",defaultValue = "10")int size){



        Map<String,Object> map = productService.pageProductList(page,size);
        return JsonData.buildSuccess(map);
    }

}

