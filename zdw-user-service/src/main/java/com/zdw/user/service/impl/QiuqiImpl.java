package com.zdw.user.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Json;
import com.zdw.user.config.OSSConfig;
import com.zdw.user.service.FileService;
import com.zdw.util.CommonUtil;
import com.zdw.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.net.URLCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.html.HTMLLabelElement;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @Author: 曾德威
 * @Date: 2023/4/30
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@Service
@Slf4j
public class QiuqiImpl implements FileService {


    @Autowired
    private OSSConfig qiniuConfig;


//    // 设置好账号的ACCESS_KEY和SECRET_KEY
//    private static final String ACCESS_KEY = "b3ulp1lDY4afMJSQswneCo67lw5Z4klBk-3Rj4WH";
//    private static final String SECRET_KEY = "SRfDrmvBV3he-fFuLUQ0wogBemm3FPZbemlBTNOW";
//    // 要上传的空间名
//    private static final String BUCKET_NAME = "blog-zdw";

    /**
     * 上传图片到七牛云,动态路径
     *
     * @param inputStream 图片文件输入流
     * @param fileName    原始文件名
     * @param savePath    保存在本地的目录路径
     * @param folder      存储在七牛云的文件夹名称
     * @return 文件在七牛云的访问链接
     */
    @Override
    public  String uploadImage(InputStream inputStream, String fileName, String savePath, String folder) {
        String accessKey = qiniuConfig.getAccessKey();
        String secretKey = qiniuConfig.getSecretKey();
        String bucketName = qiniuConfig.getBucketName();
        String uploadUrl = qiniuConfig.getUploadUrl();

        // 华南2
        Configuration cfg = new Configuration(Zone.zone2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucketName);

        try {
            File localFile = new File(savePath + "/" + UUID.randomUUID().toString() + "_" + fileName);
            FileOutputStream fos = new FileOutputStream(localFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();

            String key = folder + "/" + UUID.randomUUID().toString() + "_" + fileName;
            com.qiniu.http.Response response = uploadManager.put(localFile, key, upToken);
            // 解析上传成功的结果
            if (response.isOK()) {
                BucketManager bucketManager = new BucketManager(auth, cfg);
                URLCodec codec = new URLCodec(Charsets.UTF_8.name());
                String encodedFileName = codec.encode(key);
                String publicUrl = String.format("http://%s/%s", uploadUrl, encodedFileName);
                return publicUrl;
            }
        } catch (QiniuException ex) {
            com.qiniu.http.Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                System.err.println(ex2.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "sucess";
    }

    /**
     * 上传到shop0503文件夹
     * @param file
     * @return
     */
    @Override
    public String uploadImage(MultipartFile file) {
        // 获得原生文件名字
        String originalFilename = file.getOriginalFilename();
        String foldered = "shop0503/";
        // 获取相关配置
        String bucketName = qiniuConfig.getBucketName();
        String accessKey = qiniuConfig.getAccessKey();
        String secretKey = qiniuConfig.getSecretKey();
        String uploadUrl = qiniuConfig.getUploadUrl();

        // 创建上传管理器
        Configuration cfg = new Configuration(Zone.zone2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucketName);

        // 构建路径
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String folder = dtf.format(ldt);
        String fileName = CommonUtil.generateUUID();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            String key = foldered + folder + "/" + fileName + extension;
            com.qiniu.http.Response response = uploadManager.put(inputStream, key, upToken, null, null);
            if (response.isOK()) {
                URLCodec codec = new URLCodec(Charsets.UTF_8.name());
                String encodedFileName = codec.encode(key);
                String publicUrl = String.format("%s/%s", uploadUrl, encodedFileName);
                return publicUrl;
            }
        } catch (QiniuException ex) {
            com.qiniu.http.Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                System.err.println(ex2.toString());
            }
        } catch (Exception e) {
            log.error("上传头像失败:{}", e);
        }

        return null;

    }

}
