package com.chinatower.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 行政区划等级
 * @data 创建时间：2024/10/12 17:22
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("all")
public enum SysAreaLevelEnums {

    COUNTRY("1","country","province"),
    PROVINCE("2","province","city"),
    CITY("3","city","district"),
    DISTRICT("4","district","street"),
    STREET("5","street",null),
    ;
    //数据库等级
    private String areaLevel;
    //高德等级
    private String level;

    private String nextLevel;

    public static SysAreaLevelEnums get(String level){
        for (SysAreaLevelEnums tempEnums : SysAreaLevelEnums.values()){
            if (tempEnums.getLevel().equals(level)){
                return tempEnums;
            }
        }
        return null;
    }

    public static String getNextLevel(String level){
        SysAreaLevelEnums enums = get(level);
        if (enums != null){
            return enums.getNextLevel();
        }
        return null;
    }
}
