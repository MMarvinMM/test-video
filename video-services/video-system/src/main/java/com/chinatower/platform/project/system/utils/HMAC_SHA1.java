package com.chinatower.platform.project.system.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HMAC_SHA1 Sign生成器.
 * <p>
 * 需要apache.commons.codec包
 */
@Slf4j
public class HMAC_SHA1 {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static final String SHA_512 = "SHA-512";

    /**
     * 使用 HMAC-SHA1 签名方法对data进行签名
     *
     * @param data 被签名的字符串
     * @param key  密钥
     * @return 加密后的字符串
     */
    @SuppressWarnings("all")
    public static String genHMAC(String data, String key) {
        byte[] result = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = Base64.encodeBase64(rawHmac);

        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println(e.getMessage());
        }
        if (null != result) {
            return new String(result);
        } else {
            return null;
        }
    }

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
    @SuppressWarnings("all")
    public static String signByHmacSHA1(final String secret, final String signContext) {
        String sign = null;
        try {
            Mac hmacSha256 = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            byte[] keyBytes = secret.getBytes();
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, HMAC_SHA1_ALGORITHM));
            sign = encodeHexStr(hmacSha256.doFinal(signContext.getBytes()));
        } catch (Exception e) {
            log.warn("signByHmacSHA1 string error: ", e);
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

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) {
        log.info("========="+signByHmacSHA1("A9178500B87C11EBB6D80242AC110002","111111111"));
    }


    /**
     * SHA1加密
     */
    @SuppressWarnings("all")
    public static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(input.getBytes());
        return bytesToHex(hash);
    }

    /**
     * 字节数组转十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
