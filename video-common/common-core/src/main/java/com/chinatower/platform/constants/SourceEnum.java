package com.chinatower.platform.constants;

/**
 * @author chmod
 * @version 1.0
 * @date 2021/12/1 16:14
 */
public enum SourceEnum {
    TT("0", "铁塔视角"),
    CUSTOMER("1", "客户视角"),
    TT_CUSTOMERS("99", "铁塔视角查看客户");


    /** 来源 **/
    private String source;
    /** 名称 **/
    private String name;

    SourceEnum(String source, String name) {
        this.source = source;
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }
}
