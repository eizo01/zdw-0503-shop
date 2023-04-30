package com.zdw.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @Author: 曾德威
 * @Date: 2023/4/30
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */


public interface FileService {

    public  String uploadImage(InputStream inputStream, String fileName, String savePath, String folder);

    public String uploadImage(MultipartFile file) ;
}
