package com.zdw.order.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 曾德威
 * @Date: 2023/5/6
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */

public class AlipayConfig {

    /**
     * 支付宝网关地址
     */
    public static final  String PAY_GATEWAY="https://openapi.alipaydev.com/gateway.do";

    /**
     * 应用私钥
     */
    public static final  String APP_RPI_KEY ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDaPiah866E5qjmtLPStuvUMKjaWVTtwbmHL8JA/uNuLzGsFcgcpYO8veAt/f2bag1dVJ/2a6LzbHQesGhtq8IAzu/cybwQ+Zf6cj9NbJT1z/GTF6JmudTTQQFPbMGGVMPEgT2o4tkDdrJqv4STbJ0ky1EUDnTV+L27+GJ61zZu5SodXD4fGP9iywPOqk/j200I02oknd/VfOMeemMBA1Gwm358rq7VzMLRtEh5NuJfTwf+awYU6qvIZfuf8nnyb5jmSat7M2J7DWW93qq0GbDH3gGfUe5SVt5bUJCo3bpwsJMkItZWsJUUiHOwBiTnPk2swSNMJgiOyx1Q4h3mbuVBAgMBAAECggEAWUTo2n/pl9Udq1io4X0PE7lfqAi4U5RVRjXD2IAqGSzWvZQfSZBuRWrJYyascMC9fLqbv9khSz7GdLMl1A5YiaobtQ0Tj0H452mJ+Sp799w57TfSpwBGTK8fURSkSEUt2pge15EKwX7w1fdGzGK2GJHNtU2AfvGC8ntUFPko+8yhl/4yNTVPQpS7gM821eyFx+uZ7q0ttQJXaGrWxUvLwIhXUBbJ0BwySeMCN3POseN+FrTPgLfxWFgOm6wq3I9nQpjcFYbLr90jd3f7waoip/6gRtzVhhnOfgnb6ImYLTg6guIsulKDumsrMd53xPrKWf7Gq9K9WmxDKMiSxM2+QQKBgQD2SGUyiIuezwC0zha76XBfe4nbggewP+PqMKc+Z6aGzhR7VhXzZvwFqoU0exC+rfJbPli7xjFEhoefo1GwdRM8gYkKA2Z04G6botz4A8qepwU9A1EQUnmNvLJAt7cW+hvRdH9NrEC3heVJgNsC+fjP9GL3tHfL1po4rW5S32Tf+QKBgQDi2ojW3C8d5uu9z/OuNcQT3uyongfGw7td1+zDXL6tS+/D0oKYxx8tOmK2a4MuYcNal4ijZFwVJ8jOVoqDEBvzGRuYWCJ7dYpPyVyPyaoa/6/1mEwKAVxUlY/YTIv2y8oh9y3O7ZQzphU9e+KCmquc5vNGDJM6lCxLTDyvjw+RiQKBgBiKChtI47CFK6rYRgefMOUyp//JKv7yvsDc7C0ZoswXyI6pezJvdnkSDoe2I2r8ZgOVaL3893e+d9D+MuuEHtCOj5sBz7mhiyPSzHWh2xSHy8vSgsc/kIEA5jnwlDuj3BW4p96TZTdkDf6O6kOXqKT/0sSfpLgYv/EZxr5XJ/PxAoGBAMB7OnZMVvIhOE9SvelIF3ngwZ8Ej4aYF0e16V+ONv+lmefmmwb0lUBz9MPhj2nwa6hXQQmaIwOUQS7KgTZZmc13tVFbHs7wWY3/Rbl/HAVSg8N4GLS4mNXwHSAjOvMJ0RVmvIiCTyhPuF/12KiGgixcTXhXPSkMBlVRd4Bie4w5AoGAMfUQub0fQn/rhMRmyib4brSqUD1YBoTrIOys8iTyiLSkrCd4r6F/Lkrp5dTgYUnjR0BsUnnnDtXWhZBzBFhAAUlMCHeLN1vwguyjq+ep4hyMVxWaDudp5f9YtwQBcNJgwIUYo3Sm0VZYQoQJZmZCTmtp7YBsDqIykH7RRuhChms=";
    /**
     * 应用公钥
     */
    public static final  String ZFB_RUB_KEY ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv/YtKEHKeNiZin5I8CKbM/HbJkibTWBDbrWSY90IXQTj2xHAUPK6Auey5mP9oG2bL8hUYINi14czm3IeRihP+modGx4rIUvL+IXNEPPEbWIEeyMRSg3kVKkudUDJcleF9GnLPhuzDR03kgnYr9oCh11PXSVLbWiRfD9V5K4WA6xQei0NWk4XhckgsQq0xTn3rzaRg2cHyWVS30yRSOiAkOSYAwuB3fl+r87ZjOzk5c2ltlIX5LaIMpHrX3ohBqujll1toI8P41+B+plEFJnYUOqltAbFQ8EdPWs8QkOB9gXF0NUQ5yckwQrJ8LJ3+lviqWxZ6ex8PEBr+AqNLBVwQQIDAQAB/CQP7jbi8xrBXIHKWDvL3gLf39m2oNXVSf9mui82x0HrBobavCAM7v3Mm8EPmX+nI/TWyU9c/xkxeiZrnU00EBT2zBhlTDxIE9qOLZA3ayar+Ek2ydJMtRFA501fi9u/hietc2buUqHVw+Hxj/YssDzqpP49tNCNNqJJ3f1XzjHnpjAQNRsJt+fK6u1czC0bRIeTbiX08H/msGFOqryGX7n/J58m+Y5kmrezNiew1lvd6qtBmwx94Bn1HuUlbeW1CQqN26cLCTJCLWVrCVFIhzsAYk5z5NrMEjTCYIjssdUOId5m7lQQIDAQAB";
    /**
     * 应用id
     */
    public static final  String APP_ID ="2021000121692604";


    /**
     * 签名类型
     */
    public static final  String SIGN_TYPE="RSA2";


    /**
     * 字符编码
     */
    public static final  String CHARSET="UTF-8";


    /**
     * 返回参数格式
     */
    public static final  String FORMAT="json";


    /**
     * 构造函数私有化
     */
    private AlipayConfig(){

    }

    private volatile static AlipayClient instance = null;

    // 单例模式的使用 懒加载的单例模块 双重校验
    public static AlipayClient getInstance(){
        if (instance == null){
            synchronized(AlipayConfig.class){
                if (instance == null){
                    instance = new DefaultAlipayClient(PAY_GATEWAY,APP_ID,APP_RPI_KEY,FORMAT,CHARSET,ZFB_RUB_KEY,SIGN_TYPE);
                }

            }

        }
        return instance;
    }

}
