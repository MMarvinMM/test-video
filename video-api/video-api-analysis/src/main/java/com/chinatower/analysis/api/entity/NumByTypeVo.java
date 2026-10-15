package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 区域公共参数VO
 */
@Data
public class NumByTypeVo implements Serializable {

    private static final long serialVersionUID = 1L;
        /**
         * 告警类型id
         * @required true
         */
        private String typeId;

        /**
         * 告警类型名称
         * @required true
         */
        private String typeName;

        /**
         * 告警数量
         * @required true
         */
        private Integer num;
}
