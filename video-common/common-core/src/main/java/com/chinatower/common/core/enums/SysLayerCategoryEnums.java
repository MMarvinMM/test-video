package com.chinatower.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SysLayerCategoryEnums {

        ONE("1", "矢量"),
        TWO("2", "非矢量"),
        THREE("3", "DEM"),
        FOUR("4", "DSM"),
        FIVE("5", "倾斜摄影"),
        SIX("6", "激光点云"),
    ;
        private String code;
        private String remark;
}
