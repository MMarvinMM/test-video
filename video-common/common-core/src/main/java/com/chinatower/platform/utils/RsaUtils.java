package com.chinatower.platform.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密解密
 * 
 * @author ruoyi
 **/
@SuppressWarnings("all")
public class RsaUtils {

    //前端加密公钥对应的解密私钥
    private static final String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALjuvkONY6lOo4bj8/" +
            "i4vZWt66Se1i1I863QBTYXG2mp5MPAtfRCSdjp6QM4QT8ulmny3ptiTvfOwqNXJwzhpuuo18YyRiTJcZrXkJQfOJu5Wex" +
            "jeDTCPe0ekb9TUIbsszb9htrfisqYtCeIani6YfFbloI9BK7V2LX+aV/8+PbfAgMBAAECgYBX02oggsFV4rbelNuEv5ep" +
            "whMq9xQYJZE0dppC+e5W3Ax8usSu39xaW5wBn3G9I87jOihl+uYt4c+l6vnssC2o5qic0LHeZPB+VVDpUBZmyRbAj2qtE" +
            "kMEQikiXMIhb05WbiGVLjxAAmWbfU/gWajOqqZPU5eYFX9/8tpWqeg3YQJBAN7dDwsSZ/TNSM3cDLrS5woJvgbVFUqxER" +
            "U2S06KCg7qhzL8EW+YrQStx84EuiYRPIEtYkZXWk/zbfQ98aLM260CQQDUbeervJyhdSWbbawRdQONSAIXt3OWFNEz2np" +
            "znRSofJsomuGFpf9zzUaZ1LzSr3Updn/JnK9ium7PbFTilm47AkA5YZ1/8ktF4p0+M8wrRiYqlXuR8sc6pgCJ/6LAJBaW" +
            "5JfO7svmed8Cd2QcjWcIaiOz9uV4ywD72OeUnWZbznc5AkEA0bqwwB23hrDiMFJXT+T3XMV7S28T71keMtPBkePEQZTEm" +
            "5JOlmUNkIda7QXcZyZTxlv4TlUj05W9UYzjQdLDgQJBAKjFYPNx5fuhvlgdYOvU19As/RhoLwn2h4Gj5wizSebBAnj+f0" +
            "PxOPs2bCk2cMQlhOg1OFB98dyhPt+q16MBQqY=";


    private static final String publicKey ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+JcsQghyUPv5+SCmxB2bNuAtX5F9t/" +
            "Q0IplUnqRNrdeO40QTn8T60ozcGaggl7EFnMAmCJD0FyPwGaBKxFo9A01w5NvFZj6yf6Wqu8OhEh/EnmOIkm+oox13tcrIuqvuQ8" +
            "swlUodcsl342bqA7eypqjskx+O/tewO3NaoD+x5vQIDAQAB";


    /**
     * 私钥解密
     * @param text 待解密的文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String text) {
        try {
            return decryptByPrivateKey(privateKey, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String encryptByPublicKey(String text){
        try {
            return encryptByPublicKey(publicKey,text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 私钥加密
     * @param privateKeyString 私钥
     * @param text 待加密的信息
     * @return 加密后的文本
     */
    public static String encryptByPrivateKey(String privateKeyString, String text) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.encodeBase64String(result);
    }

    /**
     * 公钥加密
     * @param publicKeyString 公钥
     * @param text 待加密的文本
     * @return 加密后的文本
     */
    public static String encryptByPublicKey(String publicKeyString, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec2);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.encodeBase64String(result);
    }


    /**
     * 公钥解密
     * @param publicKeyString 公钥
     * @param text 待解密的信息
     * @return 解密后的文本
     */
    public static String decryptByPublicKey(String publicKeyString, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(Base64.decodeBase64(text));
        return new String(result);
    }


    /**
     * 私钥解密
     * @param privateKeyString 私钥
     * @param text 待解密的文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String privateKeyString, String text) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(Base64.decodeBase64(text));
        return new String(result);
    }

    /**
     * 构建RSA密钥对
     *
     * @return 生成后的公私钥信息
     */
    public static RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = Base64.encodeBase64String(rsaPublicKey.getEncoded());
        String privateKeyString = Base64.encodeBase64String(rsaPrivateKey.getEncoded());
        return new RsaKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * RSA密钥对对象
     */
    public static class RsaKeyPair {
        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

}