package com.chinatower.stream.util;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES（AES-128-ECB/PKCS5Padding），然后   Base64URLSafe
 * @since 2022/08/11/18:35:26
 */

@Slf4j
@SuppressWarnings("all")
public class AESUtils {
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    /**
     * SecretKeySpec类是KeySpec接口的实现类,用于构建秘密密钥规范
     */
    private SecretKeySpec key;

    public AESUtils(String hexKey) {
        key = new SecretKeySpec(hexKey.getBytes(), ALGORITHM);
    }
    
    private static volatile AESUtils instance = new AESUtils("1234567812345678");

    public static AESUtils getInstance() {
        return instance;
    }

    /**
     * AES加密
     * @param data
     * @return
     * @throws Exception
     */
    public String encryptData(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return Base64.encode(cipher.doFinal(data.getBytes()));
        }
        catch (Exception e) {
            log.error("AES加密异常,", e);
        }
        return null;
    }

    /**
     * AES解密
     * @param base64Data
     * @return
     * @throws Exception
     */
    public String decryptData(String base64Data) throws Exception{
        Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decode(base64Data)));
    }

    /**
     * hex字符串 转 byte数组
     * @param s
     * @return
     */
    private static byte[] hex2byte(String s) {
        if (s.length() % 2 == 0) {
            return hex2byte (s.getBytes(), 0, s.length() >> 1);
        } else {
            return hex2byte("0"+s);
        }
    }

    private static byte[] hex2byte (byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i=0; i<len*2; i++) {
            int shift = i%2 == 1 ? 0 : 4;
            d[i>>1] |= Character.digit((char) b[offset+i], 16) << shift;
        }
        return d;
    }

}

