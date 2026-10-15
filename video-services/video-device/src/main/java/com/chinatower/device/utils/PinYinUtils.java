package com.chinatower.device.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinYinUtils {
    /**
     * 根据拼音获取首字母(大写)
     * eg：李大宝 ==> 得到 L
     * @param pinyin
     * @return
     */
    public static String getFirstLetterOfPinyin(String pinyin){
        if (pinyin == null || "".equals(pinyin.trim())) {
            return "";
        }
        String firstLetter = "";
        for (int i = 0; i < pinyin.length(); i++) {
            char charAt = pinyin.charAt(i);
            if(isLetter(charAt)){
                firstLetter = String.valueOf(charAt);
                break;
            }
        }
        return firstLetter.toUpperCase();
    }

    /**
     * 判断是否是字母
     * @param c
     * @return
     */
    public static boolean isLetter(char c){
        if((c >= 'a' && c <= 'z') ||(c >= 'A' && c <= 'Z')){
            return true;
        }
        return false;
    }

    /**
     * 将汉字转成拼音
     * 全拼：eg: 李大宝 ==> lidabao
     * 首字母： eg: 李大宝 ==> ldb
     * @param hanzi 汉字字符串
     * @param isFull 是否全拼 true-全拼 false-首字母
     * @return 拼音
     */
    public static String convertHanziToPinyin(String hanzi, boolean isFull) {
        /***
         * ^[\u2E80-\u9FFF]+$ 匹配所有东亚区的语言
         * ^[\u4E00-\u9FFF]+$ 匹配简体和繁体
         * ^[\u4E00-\u9FA5]+$ 匹配简体
         */
        String regExp = "^[\u4E00-\u9FFF]+$";
        StringBuffer sb = new StringBuffer();
        if (hanzi == null || "".equals(hanzi.trim())) {
            return "";
        }
        String pinyin = "";
        for (int i = 0; i < hanzi.length(); i++) {
            char unit = hanzi.charAt(i);
            //是汉字，则转拼音
            if (match(String.valueOf(unit), regExp)) {
                pinyin = convertSingleHanziToPinyin(unit);
                if (isFull) {
                    sb.append(pinyin);
                } else {
                    sb.append(pinyin.charAt(0));
                }
            } else {
                sb.append(unit);
            }
        }
        return sb.toString();
    }

    /***
     * 匹配
     * <P>
     * 根据字符和正则表达式进行匹配
     *
     * @param str 源字符串
     * @param regex 正则表达式
     *
     * @return true：匹配成功  false：匹配失败
     */
    public static boolean match(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 将单个汉字转成拼音
     * @param hanzi 汉字字符
     * @return 拼音
     */
    public static String convertSingleHanziToPinyin(char hanzi) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String[] res;
        StringBuffer sb = new StringBuffer();
        try {
            res = PinyinHelper.toHanyuPinyinStringArray(hanzi, outputFormat);
            sb.append(res[0]);//对于多音字，只用第一个拼音
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }
}
