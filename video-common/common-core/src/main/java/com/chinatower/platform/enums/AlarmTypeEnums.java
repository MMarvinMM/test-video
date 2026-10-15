package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmTypeEnums {
    DM_4110101("4110101","耕地卫片执法"),
    DM_4110102("4110102","矿产卫片执法"),
    DM_4110103("4110103","非农化卫片执法"),
    DM_4110104("4110104","非粮化卫片执法"),
    ;
    /**
     * 类型
     */
    private String type;
    /**
     * 描述
     */
    private String desc;
}
