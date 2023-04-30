# zdw-0503-shop

#### 介绍
开源项目-一个基础项目可以适配互联网大多项目，只要是有关于商品和拉新的

#### 项目备注

服务端口
--- --------
zdw-user-service --- 9001


 
 UI接口文档
user服务接口文档： http://localhost:9001/swagger-ui/index.html#/  
  
1、验证码接口测试：http://localhost:9001/api/user/v1/getCaptcha  --GET
2、根据id查询地址信息： http://localhost:9001/api/adress/v1/find/1 -- POST  
3、用户图片上传： http://localhost:9001/api/user/v1/upload --post









#### 功能介绍

1.   用户微服务注册需求介绍
功能亮点： 验证码这块 图片上传可以讲分块上传，还可以讲下我们用生成一个唯一的、具有时间戳信息的文件名，从而避免多个用户上传相同名称的文件时发生冲突。同时，使用日期作为路径可以方便地按照日期检索文件，方便管理和查找。



2.  xxxx
3.  xxxx

#### 服务器 110.40.169.113
部署redis ：密码是123456 接口8000 为了防止别人挖矿



#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
