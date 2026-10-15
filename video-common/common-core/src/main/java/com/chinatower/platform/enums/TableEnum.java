package com.chinatower.platform.enums;

import lombok.Getter;

/**
 * @ClassName TableEnum
 * @Description 数据库枚举
 * @Author chenshaowu
 * @Date 2022/3/1
 **/

@Getter
public class TableEnum {
    /**
     * 基础数据库
     */
    public static final String BASE = "base";
    /**
     * 业务数据库
     */
    public static final String PLATFORM = "platform";
    /**
     * 统计数据库
     */
    public static final String STATISTIC = "statistic";
    ;
}
