package com.zdw.user.biz;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.zdw.user.UserApplication;
import com.zdw.user.service.impl.Qiuqi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.net.URLCodec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
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
    private Qiuqi qiuqi;
    @Test
    public void testUp(){{

            String filePath = "D://1/xjpic.jpg";
            try (FileInputStream inputStream = new FileInputStream(filePath)) {
                String url = qiuqi.uploadImage(inputStream, "xjpic.jpg", "D://1", "shop0503");
                System.out.println(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }
    }
}
