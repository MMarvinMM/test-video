package com.chinatower.analysis.api.entity;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class EventBoardParamVO extends BaseDataScopeEntity {

    /**
     * 区域编码
     * @required true
     */
    private String areaCode;

    /**
     * 年月 YYYYMM
     * @required true
     */
    private String date;
}
