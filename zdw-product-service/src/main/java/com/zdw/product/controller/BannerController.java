package com.zdw.product.controller;


import com.zdw.product.service.BannerService;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zdw
 * @since 2023-05-03
 */
@Api(tags = "品牌模块")
@RestController
@RequestMapping("/api/banner/v1")
public class BannerController {
    @Autowired
    private BannerService bannerService;


    @ApiOperation("轮播图")
    @GetMapping("/list")
    public JsonData list(){
        return JsonData.buildSuccess(bannerService.list());
    }
}

