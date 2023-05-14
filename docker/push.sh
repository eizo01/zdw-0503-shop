#登录阿里云镜像仓
docker login --username=aliyun1573117902 registry.cn-guangzhou.aliyuncs.com --password=qq961898


#构建整个项目，或者单独构建common项目,避免依赖未被构建上去
cd ../zdw-common
mvn install


#构建网关
cd ../zdw-gateway
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag zdw-cloud/zdw-gateway:latest registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-order-service:v1.2
docker push registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-gateway-service:v1.2
echo "网关构建推送成功"


#用户服务
cd ../xdclass-user-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag zdw-cloud/zdw-user-service:latest registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-user-service:V1
docker push registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-user-service:V1
echo "用户服务构建推送成功"


#商品服务
cd ../zdw-product-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag zdw-cloud/zdw-product-service:latest registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-product-service:V1
docker push registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-product-service:V1
echo "商品服务构建推送成功"



#订单服务
cd ../zdw-order-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag zdw-cloud/zdw-order-service:latest registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-order-service:V1
docker push registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-order-service:V1
echo "订单服务构建推送成功"


#优惠券服务
cd ../zdw-coupon-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag zdw-cloud/zdw-coupon-service:latest registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-coupon-service:v1.1
docker push registry.cn-guangzhou.aliyuncs.com/zdw-shop/zdw-coupon-service:v1.1
echo "优惠券服务构建推送成功"


echo "=======构建脚本执行完毕====="