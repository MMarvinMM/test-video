package com.chinatower.common.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author luzc
 * @date 2021/6/7 18:21
 * @desc
 * 格式检验工具
 */
@SuppressWarnings("all")
public class FormatUtils {

    /**
     * 手机号校验
     */
    private static final Pattern PHONE_CHECK_PATTER =
            Pattern.compile("(13\\d|14[579]|15[^4\\D]|17[^49\\D]|18\\d)\\d{8}");

    /**
     * 邮箱格式校验
     */
    private static final Pattern EMAIL_CHECK_PATTER =
            Pattern.compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");


    /**
     * 判断是否是邮箱格式
     *
     * @param email 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static boolean checkEmailPattern(String email) {
        Matcher matcher = EMAIL_CHECK_PATTER.matcher(email);

        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是手机号格式
     *
     * @param phone 手机号
     * @return 判断是否为手机号，是为true，否则false
     */
    public static boolean checkPhonePattern(String phone) {

        Matcher matcher = PHONE_CHECK_PATTER.matcher(phone);

        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
