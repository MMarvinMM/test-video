package com.chinatower.platform.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Locale;

/**
 * @desription: 拼音转换工具
 * @data 创建时间：2023/8/11 22:01
 * @author chuan hao
 * @version 1.0
 *
 */
public class PinyinUtils {
    public static String getFirstLetter(String chinese) {
        StringBuilder firstLetter = new StringBuilder();
        char[] chars = chinese.toCharArray();
        for (char c : chars) {
            String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyin != null && pinyin.length > 0) {
                firstLetter.append(pinyin[0].charAt(0));
            }
        }
        return firstLetter.toString().toLowerCase(Locale.ROOT);
    }
}
