package com.chinatower.common.core.comparator;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysUser;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 拼音排序
 *
 * @author qq
 */
@Slf4j
public class PinYinComparator implements Comparator<String> {

    private static final HanyuPinyinOutputFormat PINYIN_FORMAT = new HanyuPinyinOutputFormat();

    static {
        // 设置拼音大小写和声调格式
        PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    @Override
    public int compare(String o1, String o2) {
        boolean isEmpty1 = StringUtils.isEmpty(o1);
        boolean isEmpty2 = StringUtils.isEmpty(o2);
        if (isEmpty1 && isEmpty2) {
            return 0;
        }
        if (isEmpty1) {
            return -1;
        }
        if (isEmpty2) {
            return 1;
        }
        boolean isChinese1 = isChinese(o1);
        boolean isChinese2 = isChinese(o2);
        // 两个都是中文
        if (isChinese1 && isChinese2) {
            String pinyin1 = getPinyin(o1);
            String pinyin2 = getPinyin(o2);
            return pinyin1.substring(0,1).compareToIgnoreCase(pinyin2.substring(0,1));
        }
        // 两个都非中文
           if (!isChinese1 && !isChinese2) {
            return o1.substring(0, 1).compareToIgnoreCase(o2.substring(0, 1));
        }
        // name1是中文，name2非中文
        if (isChinese1) {
            String firstChar1 = "";
            try {
                firstChar1 = getFirstChar(o1);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                log.info("获取首字母失败");
                return -1;
            }
            return firstChar1 .compareToIgnoreCase(o2.substring(0, 1));
        }
            // name1非中文，name2是中文
            String firstChar2 = "";
            try {
                firstChar2 = getFirstChar(o2);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                log.info("获取首字母失败");
                return 1;
            }
            return o1.substring(0, 1).compareToIgnoreCase(firstChar2);
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
                if (pinyins != null && pinyins.length > 0) {
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

    public static void main(String[] args) {
        String fom = "YYYY-MM-DD HH:mm:ss";
        List<SysUser> list = new ArrayList<>();
        SysUser s1 = new SysUser("1");
        s1.setNickName("测试用");
        s1.setCreateTime(DateUtils.dateTime(fom,"2023-02-20 17:32:21"));
        SysUser s2 = new SysUser("2");
        s2.setNickName("测试的");
        s2.setCreateTime(DateUtils.dateTime(fom,"2023-02-20 14:35:25"));
//        SysUser s3 = new SysUser("3");
//        s3.setNickName("abc");
//        s3.setCreateTime(DateUtils.dateTime(fom,"2022-08-14 14:21:45"));
        SysUser s4 = new SysUser("4");
        s4.setNickName("测试客户zbz");
        s4.setCreateTime(DateUtils.dateTime(fom,"2022-08-14 14:21:45"));
//        SysUser s5 = new SysUser("5");
//        s5.setNickName("a俺");
//        s5.setCreateTime(DateUtils.dateTime(fom,"2023-01-01 00:00:10"));

        list.add(s1);
        list.add(s2);
//        list.add(s3);
        list.add(s4);
//        list.add(s5);
//        list.sort(Comparator.comparing(SysUser::getNickName,new PinYinComparator()));
        list.sort(Comparator.comparing(SysUser::getNickName,new PinYinComparator())
                .thenComparing(SysUser::getCreateTime,Comparator.reverseOrder()));
        System.out.println(JSON.toJSONString(list));
    }
}

