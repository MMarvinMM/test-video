package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: 深林专题查询枚举
 * @data 创建时间：2023/12/5 9:54
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
@AllArgsConstructor
public enum SpecialSubjectEnums {

    YOU_SHI_SZ(1,"优势树(灌)种","you_shi_sz"),
    YU_BI_DU(2,"乔木郁闭度/灌木覆盖度 ","yu_bi_du"),
    LIN_ZHONG(3,"林种","lin_zhong"),
    SEN_LIN_LB(4,"森林类别","sen_lin_lb"),
    DI_LEI(5,"地类","di_lei"),
    DI_MAO(6,"地貌","di_mao"),
    ZBFGLX(7,"植被覆盖类型","zbfglx"),
    GJGYL_BHDJ(8,"林地保护等级","bh_dj");

    /**
     * 专题类型
     */
    private Integer type;
    /**
     * 描述
     */
    private String desc;
    /**
     * 统计字段
     */
    private String field;

    public static SpecialSubjectEnums get(Integer type){
        for (SpecialSubjectEnums tempEnums : SpecialSubjectEnums.values()){
            if (tempEnums.getType().equals(type)){
                return tempEnums;
            }
        }
        return null;
    }
}
