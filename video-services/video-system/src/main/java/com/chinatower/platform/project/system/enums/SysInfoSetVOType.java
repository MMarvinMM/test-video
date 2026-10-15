package com.chinatower.platform.project.system.enums;

import com.chinatower.common.core.utils.StringUtils;
import lombok.Getter;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 个性化配置类型
 * @data 创建时间：2023/3/9 10:58
 */
@Getter
public enum SysInfoSetVOType {
    ZERO("0", null,"默认配置"),
    ONE("1", "3","本人个性化配置"),
    TWO("2",null,"个性化主题"),
    THREE("3","4","本组织个性化配置"),
    FOUR("4",null,"本客户个性化配置"),
    FIVE("5",null,"图层管理模式");

    /**
     * 类型值
     */
    private String type;
    /**
     * 下级
     */
    private String nextType;
    /**
     * 类型描述
     */
    private String remark;


    SysInfoSetVOType(String type,String nextType, String remark) {
        this.type = type;
        this.nextType = nextType;
        this.remark = remark;
    }

    public static SysInfoSetVOType get(String type){
        if (StringUtils.isEmpty(type)){
            return null;
        }
        for (SysInfoSetVOType voType : SysInfoSetVOType.values()){
            if (type.equals(voType.getType())){
                return voType;
            }
        }
        return null;
    }
}
