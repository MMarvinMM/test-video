/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chinatower.product.api.modules.util;

import com.chinatower.product.api.modules.dto.MultipleSignContextData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * SignUtils.
 *
 * @author: fangjun
 */
@Slf4j
@SuppressWarnings("all")
public final class SignUtils {

    private static final SignUtils SIGN_UTILS = new SignUtils();

    /**
     * 用于建立十六进制字符的输出的小写字符数组.
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private SignUtils() {
    }

    /**
     * getInstance.
     *
     * @return {@linkplain SignUtils}
     */
    public static SignUtils getInstance() {
        return SIGN_UTILS;
    }

    /**
     * 〈运管AK/SK鉴权〉.
     * HmacSHA1
     *
     * @param secret              秘钥
     * @param multipleSignContext 鉴权参数
     * @return 签名值
     */
    public static String getGenerateSign(final String secret, final MultipleSignContextData multipleSignContext) {
        return signByHmacSHA1(secret, getSignContext(multipleSignContext));
    }

    /**
     * HmacSHA1签名算法.
     *
     * @param secret      secret
     * @param signContext 原文
     * @return String
     */
    public static String signByHmacSHA1(final String secret, final String signContext) {
        String sign = null;
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA1");
            byte[] keyBytes = secret.getBytes();
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA1"));
            sign = encodeHexStr(hmacSha256.doFinal(signContext.getBytes()));
        } catch (Exception e) {
            log.warn("signByHmacSHA1 string error: ", e);
        }
        return sign;
    }

    /**
     * 签名字符串生成规则.
     *
     * @param multipleSignContext 鉴权参数
     * @return 待签名字符串
     */
    private static String getSignContext(final MultipleSignContextData multipleSignContext) {
        StringBuilder append = new StringBuilder(multipleSignContext.getPath())
                .append(multipleSignContext.getAppKey())
                .append(multipleSignContext.getMsgId())
                .append(multipleSignContext.getRequestTime());
        if (StringUtils.isNotEmpty(multipleSignContext.getCustomerCode())) {
            append.append(multipleSignContext.getCustomerCode());
        }
        if (StringUtils.isNotEmpty(multipleSignContext.getUserCode())) {
            append.append(multipleSignContext.getUserCode());
        }
        if (StringUtils.isNotEmpty(multipleSignContext.getIndustryCode())) {
            append.append(multipleSignContext.getIndustryCode());
        }
        return append.toString().toUpperCase();
    }

    /**
     * 将字节数组转换为十六进制字符数组.
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制char[]
     */
    private static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int len = data.length;
        final char[] out = new char[len << 1];
        // two characters from the hex value.
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 将字节数组转换为十六进制字符串.
     *
     * @param data byte[]
     * @return 十六进制String
     */
    private static String encodeHexStr(final byte[] data) {
        return new String(encodeHex(data, DIGITS_LOWER));
    }

}
