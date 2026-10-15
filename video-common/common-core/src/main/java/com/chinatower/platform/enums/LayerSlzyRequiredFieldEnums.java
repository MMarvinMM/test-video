package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 深林资源必填字段
 * @data 创建时间：2023/12/8 13:53
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum LayerSlzyRequiredFieldEnums {

    BSM("bsm","标识码"),
    TBBH("tbbh","图斑编号"),
    XIAO_BAN("xiao_ban","小班"),
    XBMJ("xbmj","小班面积"),
    DI_LEI("di_lei","地类"),
    YOU_SHI_SZ("you_shi_sz","优势树(灌)种"),
    SEN_LIN_LB("sen_lin_lb","森林类别");

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段描述
     */
    private String desc;
}
