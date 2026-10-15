package com.chinatower.platform.utils;

import com.chinatower.platform.common.utils.security.MD5Util;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短信工具
 *
 * @author qq on 2023/4/13
 */
public class SmsUtils {

    public static final Pattern DYNAMIC_KEY_PATTERN = Pattern.compile("[a-zA-Z0-9]{6}");

    /**
     * 生产动态密钥 6位
     *
     * @return 6位动态密钥
     */
    public static String generateDynamic() {
        return RandomStringUtils.randomAlphanumeric(6);
    }

    /**
     * 生成加密手机号
     *
     * @param phone   手机号
     * @param dynamic 动态密钥 6位字母数字
     * @return 加密手机号
     */
    public static String generateAesPhone(String phone, String dynamic) {
        Matcher matcher = DYNAMIC_KEY_PATTERN.matcher(dynamic);
        Assert.isTrue(matcher.matches(), "动态密钥不正确");
        String keyPrefix = MD5Util.MD5Encode(phone, "utf-8").substring(10, 20);
        String key = keyPrefix + dynamic;
        return AESUtil.Base64encrypt(phone, key);
    }

    /**
     * 校验动态密钥是否正确
     *
     * @param dynamic  动态密钥
     * @param phone    手机号
     * @param aesPhone 加密手机号
     * @return 动态密钥是否正确
     */
    public static boolean checkDynamicKey(String dynamic, String phone, String aesPhone) {
        String keyPrefix = MD5Util.MD5Encode(phone, "utf-8").substring(10, 20);
        String key = keyPrefix + dynamic;
        Matcher matcher = DYNAMIC_KEY_PATTERN.matcher(dynamic);
        if (!matcher.matches()) {
            return false;
        }
        String aesPhoneTemp = AESUtil.Base64encrypt(phone, key);
        return StringUtils.equals(aesPhone, aesPhoneTemp);
    }
}
