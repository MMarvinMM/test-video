package com.chinatower.platform.utils;


import lombok.Getter;

/**
 * 功能说明 : 导出菜单模块枚举
 * @author 杨洪洲
 * @date 9/29/21
 * @requestID R-ZBZB-202109280789713 web-admin拆分改造
 */
@Getter
public enum ExportModuleEnums {

    EXPORT_SYS_AREA("EXPORT_SYS_AREA","区域列表导出文件");

    private String key;
    private String value;

    ExportModuleEnums(String code, String message) {
        this.key = code;
        this.value = message;
    }

    public static String getValue(String code) {
        if(null==code||"null".equals(code)){
            code="";
        }
        for (ExportModuleEnums ele : values()) {
            if (code.equals(ele.getKey())) {
                return ele.getValue()+".zip";
            }
        }
        return "";
    }

}
