# zdw-0503-shop

#### 介绍
开源项目-一个基础项目可以适配互联网大多项目，只要是有关于商品和拉新的活动项目模块

#### 项目备注

服务端口
--- --------
zdw-user-service --- 9001
zdw-coupon-service --- 9002

 
 UI接口文档
user服务接口文档： http://localhost:9001/swagger-ui/index.html#/  
  测试账号：2399492494@qq.com           密码：12345
1、验证码接口测试：http://localhost:9001/api/user/v1/getCaptcha  --GET
2、根据id查询地址信息： http://localhost:9001/api/adress/v1/find/1 -- POST  
3、用户图片上传： http://localhost:9001/api/user/v1/upload --post
4、用户注册： /api/user/v1/register --post
5、查询个人信息：Http://localhost:9001/api/user/v1/detail
6、收货地址: 

coupon服务接口文档： http://localhost:9002/swagger-ui/index.html#/  





#### 功能介绍

1.   用户微服务注册需求介绍
功能亮点： 验证码这块 图片上传可以讲分块上传，还可以讲下我们用生成一个唯一的、具有时间戳信息的文件名，从而避免多个用户上传相同名称的文件时发生冲突。同时，使用日期作为路径可以方便地按照日期检索文件，方便管理和查找。



怎么拿到user对应的信息？首先jwt存储了一份，通过解析jwt就可以，其次内部使用ThreadLocal，controller层使用LoginUser loginUser = LoginInterceptor.threadLocal.get();

越权攻击：
* 防范水平越权  
例如我是用户1创建的收货地址，那么我居然可以查询删除用户2的地址，这在软件上是不行的
```
建立用户和可操作资源的绑定关系，用户对任何资源进行操作时，通过该绑定关系确保该资源是属于该用户所有的
```

* 防范垂直越权
例子：普通管理员登录，拼接浏览器地址，直接访问高级管理员的页面
```
基于RBAC角色访问控制机制来防止纵向越权攻击，定义不同的权限角色，为每个角色分配不同的权限，当用户执行某个动作或产生某种行为时，通过用户所在的角色判定该动作或者行为是否允许。
```
项目是面向c端的，所以我们要解决水平越权的就可以了

2.  xxxx
3.  xxxx

#### 服务器 110.40.169.113
部署redis ：密码是123456 接口8000 为了防止别人挖矿



#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
