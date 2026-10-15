package com.chinatower.platform.utils;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;

/**
 * AES加密工具 使用Hutool工具包实现，使用前请先添加依赖
 * @author WANGZIC
 */
@Slf4j
public class AESUtil {

    /**
     * 该KEY的长度必须为16/24/32位中的一种
     */
    private static final String AES_KEY ="chinatowerenergy";

    private static final AES aes = SecureUtil.aes(AES_KEY.getBytes());

    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    private static final String ERROR_MSG_TEMPLATE = "字符串[{}]加密失败：{}";

    /**
     * AES加密并进行哈希
     * @param str
     * @return
     */
    public static String encrypt(String str){
        if(StrUtil.isBlank(str)){
            return str;
        }
        try {
            return aes.encryptHex(str);
        }catch (Exception e){
            log.error(ERROR_MSG_TEMPLATE, str,e.getMessage());
            return str;
        }
    }
    /**
     * AES加密并,动态密钥，进行哈希
     * @param str
     * @return
     */
    public static String encrypt(String str,String key){
        if(StrUtil.isBlank(str)){
            return str;
        }

        try {
            AES aes = getAes(key);
            return aes.encryptHex(str);
        }catch (Exception e){
            log.error(ERROR_MSG_TEMPLATE, str,e.getMessage());
            return str;
        }
    }

    public static String Base64encrypt(String str,String key){
        if(StrUtil.isBlank(str)){
            return str;
        }
        try {
            AES aes = getAes(key);
            return aes.encryptBase64(str);
        }catch (Exception e){
            log.error(ERROR_MSG_TEMPLATE, str,e.getMessage());
            return str;
        }
    }

    public static AES getAes(String key){
        if(StrUtil.isBlank(key)){
            return aes;
        }

        return SecureUtil.aes(key.getBytes());
    }

    /**
     * 先进行哈希并AES解密
     * @param str
     * @return
     */
    public static String decrypt(String str){
        if(StrUtil.isBlank(str)){
            return str;
        }
        try {
            return aes.decryptStr(HexUtil.decodeHex(str));
        }catch (Exception e){
            log.error(ERROR_MSG_TEMPLATE,str,e.getMessage());
            return str;
        }
    }


    /**
     * 先进行哈希并AES解密,动态密钥key
     * @param str
     * @return
     */
    public static String decrypt(String str,String key){
        if(StrUtil.isBlank(str)){
            return str;
        }
        try {
            AES aes = getAes(key);
            return aes.decryptStr(HexUtil.decodeHex(str));
        }catch (Exception e){
            log.error(ERROR_MSG_TEMPLATE,str,e.getMessage());
            return str;
        }
    }

    /**
     * AES加密并进行哈希
     * @param str
     * @return
     */
    public static String encryptHttp(String str){
        if(StrUtil.isBlank(str)){
            return str;
        }
        if(!str.startsWith("http")){
            return str;
        }
        String res ;
        try {
            res = aes.encryptHex(str);
        }catch (Exception e){
            log.error(ERROR_MSG_TEMPLATE, str,e.getMessage());
            res = str;
        }
        return res;
    }

    /**
     * 先进行哈希并AES解密
     * @param str
     * @return
     */
    public static String decryptHttp(String str){

        if(StrUtil.isBlank(str)){
            return str;
        }
        if(str.startsWith("http")){
            return str;
        }
        String res ;
        try {
            res = aes.decryptStr(HexUtil.decodeHex(str));
        }catch (Exception e){
            log.error(ERROR_MSG_TEMPLATE,str,e.getMessage());
            res = str;
        }
        return res;
    }

}
