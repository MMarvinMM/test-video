package com.chinatower.platform.project.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class AppTokenData   {

        private String appToken;

        private Integer expiresIn;

        /**
         * JSAPI 访问TOKEN
         */
        private String jsApiToken;

        /**
         * 随机字符串
         */
        private String nonceStr;

        /**
         * 生成签名的10位时间戳，int型，单位：秒
         */
        private int timestamp;

        /**
         * 应用的唯一标识
         */
        private String appId;

        /**
         * 应用的唯一标识
         */
        private String signature;


}