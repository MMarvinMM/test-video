package com.chinatower.platform.auth.project.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * SSO密钥加解密工具
 * 算法与 com.chinatower.cloud.starter.base.util.ThreeDESUtil 完全一致：
 * DES/ECB/NoPadding 分块加密、待加密数据不足8字节倍数时以 0x80 + 0x00 补齐、
 * 密文以大写HEX编码；解密时反向去除 0x80/0x00 填充。
 * 解密固定使用内置密钥（与 ThreeDESUtil.decode 的固定密钥一致），仅依赖 JDK 自带 javax.crypto，与外部依赖脱钩。
 */
@SuppressWarnings("all")
public class SsoSecretCryptoUtil {

    private static final String ALGORITHM = "DES";

    private static final String FIXED_KEY = "6#dPz>3F";

    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    private SsoSecretCryptoUtil() {
    }

    /**
     * 加密
     *
     * @param text 明文
     * @param key  密钥（UTF-8 8字节，否则抛异常）
     * @return 大写HEX密文；入参为空返回 null
     */
    public static String encode(String text, String key) {
        if (text == null || key == null) {
            return null;
        }
        byte[] keyBytes = resolveKeyBytes(key, true);
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        int remainder = data.length % 8;
        int paddedLength = remainder == 0 ? data.length : data.length + 8 - remainder;
        byte[] padded = new byte[paddedLength];
        System.arraycopy(data, 0, padded, 0, data.length);
        if (remainder != 0) {
            padded[data.length] = (byte) 0x80;
        }
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding"); // NOSONAR 外部4A/小塔平台互通协议固定要求DES/ECB/NoPadding，不可更换
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, ALGORITHM));
            return toHex(cipher.doFinal(padded));
        } catch (Exception e) {
            throw new IllegalStateException("SSO密钥加密失败", e);
        }
    }

    /**
     * 解密（固定密钥）
     *
     * @param hex 大写/小写HEX密文
     * @return UTF-8明文；入参为空、HEX非法或非8字节倍数时返回 null
     */
    public static String decode(String hex) {
        if (hex == null) {
            return null;
        }
        byte[] keyBytes = FIXED_KEY.getBytes(StandardCharsets.UTF_8);
        byte[] hexBytes = hex.getBytes(StandardCharsets.UTF_8);
        if (hexBytes.length % 2 != 0) {
            return null;
        }
        byte[] encrypted = new byte[hexBytes.length / 2];
        for (int i = 0, j = 0; i < hexBytes.length; i += 2, j++) {
            encrypted[j] = (byte) ((hexNibble(hexBytes[i]) << 4) | hexNibble(hexBytes[i + 1]));
        }
        if (encrypted.length % 8 != 0) {
            return null;
        }
        if (encrypted.length == 0) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding"); // NOSONAR 外部4A/小塔平台互通协议固定要求DES/ECB/NoPadding，不可更换
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, ALGORITHM));
            byte[] plain = cipher.doFinal(encrypted);
            return new String(plain, 0, stripPadding(plain), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 取密钥 UTF-8 字节（DES 密钥固定 8 字节，与 ThreeDESUtil 行为一致）
     */
    private static byte[] resolveKeyBytes(String key, boolean throwOnInvalid) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 8) {
            if (throwOnInvalid) {
                throw new IllegalArgumentException("SSO密钥必须为8字节");
            }
            return null;
        }
        return keyBytes;
    }

    private static int hexNibble(byte b) {
        return b <= '9' ? b & 0x0F : (b & 0x0F) + 9;
    }

    /**
     * 去除 0x80/0x00 填充，返回明文有效长度
     */
    private static int stripPadding(byte[] plain) {
        int n = plain.length;
        while (n > 0 && plain[n - 1] == 0) {
            n--;
        }
        if (n == 0) {
            return 0;
        }
        return plain[n - 1] == (byte) 0x80 ? n - 1 : n;
    }

    private static String toHex(byte[] data) {
        char[] hex = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            hex[i * 2] = HEX_DIGITS[(data[i] >>> 4) & 0x0F];
            hex[i * 2 + 1] = HEX_DIGITS[data[i] & 0x0F];
        }
        return new String(hex);
    }
}
