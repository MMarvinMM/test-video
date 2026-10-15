package com.chinatower.common.core.enums;

/**
 * ExcelType导入类型
 *
 * @author zyp
 */
public enum ExcelType {
    USER("1", "用户"),
    DEVICE("2", "设备");

    private final String code;
    private final String info;

    ExcelType(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
