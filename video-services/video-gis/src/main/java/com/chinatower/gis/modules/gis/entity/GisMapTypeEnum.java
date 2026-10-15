package com.chinatower.gis.modules.gis.entity;

import org.apache.commons.lang3.StringUtils;

public enum GisMapTypeEnum {

    GAODE_MAP("01","com.chinatower.gis.modules.gis.service.GisGdService"),

    BAIDU_MAP("02","com.chinatower.gis.modules.gis.service.GisBdService"),

    TIAN_MAP("03","com.chinatower.gis.modules.gis.service.GisTianService"),
    
    OFFLINE_MAP("04","com.chinatower.gis.modules.gis.service.GisOfflineService");

    private String code;
    private String className;

    GisMapTypeEnum() {
    }

    GisMapTypeEnum(String code, String className) {
        this.code = code;
        this.className = className;
    }

    public static String getClassNameByCode(String code) {
        String className = "";
        if (StringUtils.isEmpty(code)) {
            return className;
        }

        for (GisMapTypeEnum e : GisMapTypeEnum.values()) {
            if (e.code.equalsIgnoreCase(code)) {
                className = e.className;
                break;
            }
        }
        return className;
    }

    public String getCode() {
        return code;
    }

    public String getClassName() {
        return className;
    }
}
