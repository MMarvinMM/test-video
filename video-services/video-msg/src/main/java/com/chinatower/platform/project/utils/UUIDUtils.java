package com.chinatower.platform.project.utils;

import java.util.UUID;

/**
 * @ClassName UUIDUtils
 * @Description 生成随机字符串的工具类 uuid
 * @Author D
 * @Date 2022/1/25 16:36
 */
public class UUIDUtils {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
