package com.chinatower.platform.project.system.domain;

import lombok.Data;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

@Data
public  class AreaDetailInfo {
    private String areaCode;
    private String areaName;
    private String province;
    private String firstLetter;
    private String provinceCode;

    // 构造方法
    public AreaDetailInfo(String code, String name, String province, String provinceCode) {
        this.areaCode = code;
        this.areaName = name;
        this.province = province;
        this.provinceCode = provinceCode;
        this.firstLetter = getFirstLetter(province);
    }

    public AreaDetailInfo(String code, String name) {
        this.areaCode = code;
        this.areaName = name;
        this.firstLetter = getFirstLetter(name);
    }

    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String code) { this.areaCode = code; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String name) {
        this.areaName = name;
        this.firstLetter = getFirstLetter(name);
    }

    public String getFirstLetter() { return firstLetter; }
    public void setFirstLetter(String firstLetter) { this.firstLetter = firstLetter; }

    @Override
    public String toString() {
        return "GeographicSimpleInfo{" +
                "code='" + areaCode + '\'' +
                ", name='" + areaName + '\'' +
                ", firstLetter='" + firstLetter + '\'' +
                '}';
    }

    public static String getFirstLetter(String chinese) {
        if (chinese == null || chinese.trim().isEmpty()) {
            return "#";
        }

        char firstChar = chinese.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            return String.valueOf(firstChar);
        } else if (firstChar >= 'a' && firstChar <= 'z') {
            return String.valueOf((char)(firstChar - 32));
        } else {
            String pinyin = convertToPinyin(String.valueOf(firstChar));
            if (pinyin != null && !pinyin.isEmpty()) {
                char pinyinFirst = pinyin.charAt(0);
                if (pinyinFirst >= 'a' && pinyinFirst <= 'z') {
                    return String.valueOf((char)(pinyinFirst - 32));
                } else if (pinyinFirst >= 'A' && pinyinFirst <= 'Z') {
                    return String.valueOf(pinyinFirst);
                }
            }
            return "#";
        }
    }

    private static String convertToPinyin(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return null;
        }
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        try {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(chinese.charAt(0), format);
            if (pinyinArray != null && pinyinArray.length > 0) {
                return pinyinArray[0];
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            // 记录日志或处理异常
            System.err.println("拼音转换失败: " + chinese + ", 错误: " + e.getMessage());
        }

        return null;
    }
}