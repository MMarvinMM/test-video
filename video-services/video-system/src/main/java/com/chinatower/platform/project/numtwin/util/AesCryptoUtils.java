package com.chinatower.platform.project.numtwin.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class AesCryptoUtils {

    /** 与数字孪生前端 CryptoJS AES-CBC 约定一致，不可改为 GCM */
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    
    private AesCryptoUtils() {
        // 工具类不允许实例化
    }
    
    // 密钥（24字节，对应JavaScript中的26字符）
    private static final String KEY = "zyxwvutsrqponmlkjihgfedc";
    private static final String IV = "abcdef0123456789";
    
    /**
     * 生成加密头部信息（对应JavaScript的encodeAesString方法）
     */
    public static EncryptionResult encodeAesString() {
        try {
            // 生成16位随机数
            String rawKey = generateCryptoJSKey();
            // 使用密钥加密随机数
            String encryptedKey = encodeAes(rawKey);
            // 获取当前时间戳
            String timestamp = String.valueOf(System.currentTimeMillis());
            // 组合数据：原始key&加密key&时间戳
            String combinedData = rawKey + "&" + encryptedKey + "&" + timestamp;
            String encrypted = encodeAes(combinedData);
            
            // Base64编码IV
            String base64Iv = Base64.getEncoder().encodeToString(IV.getBytes(StandardCharsets.UTF_8));
            
            return new EncryptionResult(encrypted, base64Iv);
        } catch (Exception e) {
            throw new AesCryptoException("AES加密失败", e);
        }
    }
    
    /**
     * AES加密（对应JavaScript的encodeAes方法）
     */
    public static String encodeAes(String data) throws AesCryptoException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return encodeAes(data, KEY, IV);
    }
    
    /**
     * 通用AES加密方法
     */
    public static String encodeAes(String data, String key, String iv) throws AesCryptoException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION); // NOSONAR 数字孪生前端 CryptoJS 固定 AES/CBC/PKCS5Padding，不可更换为 GCM
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        // prettier-ignore
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));// NOSONAR
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        return Base64.getEncoder().encodeToString(encrypted);
    }

    
    /**
     * 通用AES解密方法
     */
    public static String decodeAesString(String encrypted, String key, String iv) throws AesCryptoException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION); // NOSONAR 数字孪生前端 CryptoJS 固定 AES/CBC/PKCS5Padding，不可更换为 GCM
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        // prettier-ignore
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));// NOSONAR
        
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    
    /**
     * 生成随机密钥（对应JavaScript的generateCryptoJSKey方法）
     */
    public static String generateCryptoJSKey(int length) {
        long min = (long) Math.pow(10, (double) length - 1d);
        long max = (long) (Math.pow(10, length) - 1d);
        long randomValue = min + (long) (new SecureRandom().nextDouble() * (max - min + 1));
        return String.valueOf(randomValue);
    }
    
    public static String generateCryptoJSKey() {
        return generateCryptoJSKey(16);
    }
    
    /**
     * 加密结果封装类
     */
    public static class EncryptionResult {
        private String encryptedData;
        private String encryptionIv;
        
        public EncryptionResult(String encryptedData, String encryptionIv) {
            this.encryptedData = encryptedData;
            this.encryptionIv = encryptionIv;
        }
        
        public String getXEncryptedData() {
            return encryptedData;
        }
        
        public String getXEncryptionIv() {
            return encryptionIv;
        }
        
        @Override
        public String toString() {
            return "EncryptionResult{" +
                    "encryptedData='" + encryptedData + '\'' +
                    ", encryptionIv='" + encryptionIv + '\'' +
                    '}';
        }
    }

}