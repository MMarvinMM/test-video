package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 探而未采图层计算状态返回
 * @data 创建时间：2024/4/29 21:25
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum CalculateLayersEnums {

    ZERO(0,"计算中"),
    ONE(1,"计算完成"),
    TWO(2,"数据变更"),
    THREE(3,"丢弃"),
    FOUR(4,"图层丢失"),
    FIVE(5,"失败"),
    ;
    private Integer status;
    private String remark;
}
