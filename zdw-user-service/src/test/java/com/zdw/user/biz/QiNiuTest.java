package com.zdw.user.biz;


import com.zdw.user.UserApplication;

import com.zdw.user.service.impl.QiuqiImpl;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;


import static org.junit.Assert.assertNotNull;

/**
 * @Author: 曾德威
 * @Date: 2023/4/30
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class QiNiuTest {
   @Autowired
    private QiuqiImpl qiuqi;
    @Test
    public void testUp(){

            String filePath = "D://1/xjpic.jpg";
            try (FileInputStream inputStream = new FileInputStream(filePath)) {
                String url = qiuqi.uploadImage(inputStream, "xjpic.jpg", "D://1", "shop0503");
                System.out.println(url);
            } catch (Exception e) {
                e.printStackTrace();
            }


    }
    @Test
    public void testUploadImage() throws Exception {
        // 读取本地图片文件
        String filePath = "D:/1/xjpic.jpg";
        File file = new File(filePath);
        byte[] content = FileUtils.readFileToByteArray(file);

        // 构造 MockMultipartFile 对象
        MultipartFile multipartFile = new MockMultipartFile(
                file.getName(),           // 文件名
                file.getName(),           // 原始文件名
                MediaType.IMAGE_JPEG.toString(),  // 文件类型
                content);                // 文件内容


        String publicUrl = qiuqi.uploadImage(multipartFile);

        // 验证上传结果
        assertNotNull(publicUrl);
        System.out.println(publicUrl);
    }
}
