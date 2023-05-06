# zdw-0503-shop

#### 一、介绍
开源项目-一个基础项目可以适配互联网大多项目，只要是有关于商品和拉新的活动项目模块

#### 二、项目备注

服务端口
--- --------
zdw-user-service --- 9001
zdw-coupon-service --- 9002
zdw-product-service --- 9003
zdw-product-service --- 9004

DO 实体类 VO 传给前端的类 request协议 服务之间的通讯实体 前端传过来的
#### 三、 UI接口文档  
user服务接口文档： http://localhost:9001/swagger-ui/index.html#/  
  测试账号：2399492494@qq.com            密码：12345
1、验证码接口测试：http://localhost:9001/api/user/v1/getCaptcha  --GET  
2、根据id查询地址信息： http://localhost:9001/api/adress/v1/find/1 -- POST    
3、用户图片上传： http://localhost:9001/api/user/v1/upload --post  
4、用户注册： /api/user/v1/register --post  
5、查询个人信息：Http://localhost:9001/api/user/v1/detail  
6、收货地址: 

coupon服务接口文档： http://localhost:9002/swagger-ui/index.html#/  
1、优惠卷分页：http://localhost:9002/api/coupon/v1/page_coupon
2、领取优惠卷：http://localhost:9002/api/coupon/v1/add/promotion/{coupon_id}
3、发放新人优惠卷：http://localhost:9002/api/coupon/v1/new_user_coupon

Todo：管理员添加优惠卷三种类型

prouct服务接口文档：http://localhost:9003/swagger-ui/index.html#/  
#### 四、功能介绍

1. 用户微服务注册需求介绍  
功能亮点： 验证码这块 图片上传可以讲分块上传，还可以讲下我们用生成一个唯一的、具有时间戳信息的文件名，从而避免多个用户上传相同名称的文件时发生冲突。同时，使用日期作为路径可以方便地按照日期检索文件，方便管理和查找。  



怎么拿到user对应的信息？  
首先jwt存储了一份，通过解析jwt就可以，其次内部使用ThreadLocal，controller层使用LoginUser loginUser = LoginInterceptor.threadLocal.get();

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

2.  Jmeter压测扣超发优惠券问题暴露    
   * 扣减存储为负数，超发优惠券   
   * 造成资损  
   原sql  
   ```sql
update coupon set stock = stock - 1 where id = #{couponId} 
   ```

```sql
update coupon set stock = stock - 1 where id = #{couponId} and stock > 0
```

解决超发问题
优惠卷业务只需要扣一条  
* 如果数据库不止扣损一条的话，用这个解决
直接数据库更新扣减 （重点）
```sql
update coupon set stock=stock - #{num} where id = #{couponId} and stock>0
//测试如果num大于已有库存，则会变负数
update coupon set stock=stock - #{num} where id = #{couponId} and （stock - #{num})>=0
或者
update coupon set stock=stock - #{num} where id = #{couponId} and stock >= #{num} 
//修复了负数问题

```
 2. coupon微服务注册需求介绍  
添加和发送优惠卷
添加优惠卷解决超发和超领取问题

3.商品购物车微服务介绍
* 类目
  * 一个树状结构的系统，根据业务可以分成4-5级。如手机->智能手机->国产手机 类目，在这里面，手机是一级类目，国产手机是三级类目，也是叶子类目
* SPU
  * Standard Product Unit：标准化产品单元。是商品信息聚合的最小单位，是一组可复用、易检索的标准化信息的集合，该集合描述了一个产品的特性。通俗点讲，属性值、特性相同的商品就可以称为一个SPU
  * 比如 Iphone100 就是一个SPU
* SKU
  * 一般指库存保有单位。库存保有单位即库存进出计量的单位， 可以是以件、盒、托盘等为单位。*SKU*是物理上不可分割的最小存货单元，在服装、鞋类商品中使用最多最普遍，买家购买、商家进货、供应商备货、工厂生产都是依据SKU进行的


分布式事务 ： 采用rockermq  
4、订单服务 
```java
/**
     * * 防重提交
     * * 用户微服务-确认收货地址
     * * 商品微服务-获取最新购物项和价格
     * * 订单验价
     * * 优惠券微服务-获取优惠券
     * * 验证价格
     * * 锁定优惠券
     * * 锁定商品库存
     * * 创建订单对象
     * * 创建子订单对象
     * * 发送延迟消息-用于自动关单
     * * 创建支付信息-对接三方支付
     * @param orderRequest
     * @return
     */
```
     
下单功能亮点 延迟队列mq  
流程： 下单-优惠券记录锁定和释放功能设计 
      下单-商品锁定库存功能和释放库存功能
      思考： * 如何保证消息不会重复消费-幂等处理
            * 多个消息并发情况下是否需要加锁
      测试
解决token 传递问题  fegin    
       创建订单  
      
#### 五、服务器 
地址110.40.169.113
部署redis ：密码是123456 接口8000 为了防止别人挖矿

压测介绍
讲解jmeter解压文件里面的各个目录，文件等
- 目录

  ```
  bin:核心可执行文件，包含配置
          jmeter.bat: windows启动文件(window系统一定要配置显示文件拓展名)
          jmeter: mac或者linux启动文件
          jmeter-server：mac或者Liunx分布式压测使用的启动文件
          jmeter-server.bat：window分布式压测使用的启动文件
          jmeter.properties: 核心配置文件   
  extras：插件拓展的包
  
  lib:核心的依赖包
  ```

- Jmeter语言版本中英文切换

  - 控制台修改 menu -> options -> choose language

- 配置文件修改

  - bin目录 -> jmeter.properties
  - 默认 #language=en
  - 改为 language=zh_CN

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
