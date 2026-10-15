package com.chinatower.event.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author
 * @date 2024/01/23
 */
@SuppressWarnings("all")
public class SignatureUtil {
    /**
     * 用于建立十六进制字符的输出的小写字符数组.
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * HmacSHA1签名算法.
     * @param secret secret
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
//            log.warn("signByHmacSHA1 string error: ", e);
        }
        return sign;
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
    /**
     * 将字节数组转换为十六进制字符数组.
     * @param data byte[]
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
}
