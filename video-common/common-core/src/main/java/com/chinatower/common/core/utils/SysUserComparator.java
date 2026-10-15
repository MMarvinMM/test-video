package com.chinatower.common.core.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class SysUserComparator<T>  implements Comparator<T> {

    private final String propertyName;
    private  String propertyName1;

    public SysUserComparator(String propertyName,String propertyName1) {
        this.propertyName = propertyName;
        this.propertyName1 = propertyName1;
    }
    public SysUserComparator(String propertyName) {
        this.propertyName = propertyName;
    }

    private static final HanyuPinyinOutputFormat PINYIN_FORMAT =
            new HanyuPinyinOutputFormat();


    static {
        // 设置拼音大小写和声调格式
        PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }
    @Override
    public int compare(T o1, T o2) {
        String name1 = (String)getPropertyValue(o1, propertyName);
        String name2 = (String)getPropertyValue(o2, propertyName);
        Date createTime1 = null;
        Date createTime2 =null;

        // 判断name1和name2是中文还是英文
        boolean isChinese1 = isChinese(name1);
        boolean isChinese2 = isChinese(name2);
        if (StringUtils.isNotEmpty(propertyName1)) {
            createTime1 = (Date) getPropertyValue(o1, propertyName1);
            createTime2 = (Date) getPropertyValue(o2, propertyName1);
        }
        return  comparePublic(isChinese1,isChinese2,name1,name2,createTime1,createTime2);
//        else{
//            // 两个都是中文
//            if (isChinese1 && isChinese2) {
//                String pinyin1 = getPinyin(name1);
//                String pinyin2 = getPinyin(name2);
//                int chinesePx = pinyin1.compareToIgnoreCase(pinyin2);
//                return chinesePx;
//            } else if (!isChinese1 && !isChinese2) {
//                int nameCompare = name1.substring(0, 1).compareToIgnoreCase(name2.substring(0, 1));
//                return nameCompare;
//            } else if (isChinese1 && !isChinese2) {
//                // name1是中文，name2是英文
//                String firstChar1 = null;
//                try {
//                    firstChar1 = getFirstChar(name1);
//                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
//                    badHanyuPinyinOutputFormatCombination.printStackTrace();
//                }
//                int firstCharCompare = (firstChar1 + name1).substring(0, 1).compareToIgnoreCase(name2.substring(0, 1));
//                    return firstCharCompare;
//            } else {
//                // name1是英文，name2是中文
//                String firstChar2 = null;
//                try {
//                    firstChar2 = getFirstChar(name2);
//                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
//                    badHanyuPinyinOutputFormatCombination.printStackTrace();
//                }
//                int firstCharCompare = name1.substring(0, 1).compareToIgnoreCase((firstChar2 + name2).substring(0, 1));
//                    return firstCharCompare;
//            }
//        }
    }

    private boolean isChinese(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                return true;
            }
        }
        return false;
    }

    private String getPinyin(String name) {
        StringBuilder sb = new StringBuilder();
        char[] chars = name.toCharArray();
        for (char c : chars) {
            try {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, PINYIN_FORMAT);
                if (pinyins !=null && pinyins.length > 0) {
                    sb.append(pinyins[0]);
                } else {
                    sb.append(c);
                }
            } catch (Exception e) {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    private String getFirstChar(String name) throws BadHanyuPinyinOutputFormatCombination {
        char c = name.charAt(0);
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
            return String.valueOf(c).toUpperCase();
        } else {
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, PINYIN_FORMAT);
            if (pinyins != null && pinyins.length > 0) {
                return pinyins[0].substring(0, 1).toUpperCase();
            } else {
                return String.valueOf(c).toUpperCase();
            }
        }
    }


    private Object getPropertyValue(T object, String propertyName) {
        try {
            Field field = object.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Could not get property value for property " + propertyName + " of object " + object, e);
        }
    }

    private int comparePublic(boolean isChinese1,boolean isChinese2,String name1,String name2,Date createTime1,Date createTime2){
        // 两个都是中文
        if (isChinese1 && isChinese2) {
            String pinyin1 = getPinyin(name1);
            String pinyin2 = getPinyin(name2);
            int chinesePx = pinyin1.compareToIgnoreCase(pinyin2);
            if (chinesePx == 0 && Objects.nonNull(createTime1)&& Objects.nonNull(createTime2)) {
                return -createTime1.compareTo(createTime2);
            } else {
                return chinesePx;
            }
        } else if (!isChinese1 && !isChinese2) {
            int nameCompare = name1.substring(0, 1).compareToIgnoreCase(name2.substring(0, 1));
            if (nameCompare == 0&& Objects.nonNull(createTime1)&& Objects.nonNull(createTime2)) {
                // 如果name1和name2的firstchar相等，则按照creatime降序排列
                return -createTime1.compareTo(createTime2);
            } else {
                return nameCompare;
            }
        } else if (isChinese1 && !isChinese2) {
            // name1是中文，name2是英文
            String firstChar1 = null;
            try {
                firstChar1 = getFirstChar(name1);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            int firstCharCompare = (firstChar1 + name1).substring(0, 1).compareToIgnoreCase(name2.substring(0, 1));
            if (firstCharCompare == 0&& Objects.nonNull(createTime1)&& Objects.nonNull(createTime2)) {
                // 如果name1的firstchar和name2相等，则按照creatime降序排列
                return -createTime1.compareTo(createTime2);
            } else {
                return firstCharCompare;
            }
        } else {
            // name1是英文，name2是中文
            String firstChar2 = null;
            try {
                firstChar2 = getFirstChar(name2);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            int firstCharCompare = name1.substring(0, 1).compareToIgnoreCase((firstChar2 + name2).substring(0, 1));
            if (firstCharCompare == 0&& Objects.nonNull(createTime1)&& Objects.nonNull(createTime2)) {
                // 如果name1和name2的firstchar相等，则按照creatime降序排列
                return -createTime1.compareTo(createTime2);
            } else {
                return firstCharCompare;
            }
        }
    }

}

