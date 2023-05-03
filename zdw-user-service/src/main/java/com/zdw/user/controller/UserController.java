package com.zdw.user.controller;


import com.zdw.enums.BizCodeEnum;
import com.zdw.user.request.UserLoginRequest;
import com.zdw.user.request.UserRegisterRequest;
import com.zdw.user.service.FileService;
import com.zdw.user.service.UserService;
import com.zdw.user.vo.UserVO;
import com.zdw.util.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zdw
 * @since 2023-04-28
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/api/user/v1")
public class UserController {
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;


    /**
     * 上传用户头像
     *
     * 默认最大是1M,超过则报错
     *
     * @param file 文件
     * @return
     */
    @ApiOperation("用户头像上传")
    @PostMapping(value = "upload")
    public JsonData uploadUserImg(
            @ApiParam(value = "文件上传",required = true)
            @RequestPart("file") MultipartFile file){

        String result = fileService.uploadImage(file);


        return result!=null? JsonData.buildSuccess(result):JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }



    /**
     *  用户注册
     * @param registerRequest
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public JsonData register(@ApiParam("用户注册对象") @RequestBody UserRegisterRequest registerRequest){

        JsonData jsonData = userService.register(registerRequest);
        return jsonData;
    }

    /**
     * 用户登录
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public JsonData login(@ApiParam("用户登录对象") @RequestBody UserLoginRequest userLoginRequest){


        JsonData jsonData = userService.login(userLoginRequest);

        return jsonData;
    }

    /**
     * 用户个人信息查询
     * @return
     */
    @ApiOperation("个人信息查询")
    @GetMapping("/detail")
    public JsonData detail(){

        UserVO userVO = userService.findUserDetail();

        return JsonData.buildSuccess(userVO);
    }


}

