package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * 任务巡查列表入参扩展
 *
 * @author dencycheng
 */
@Data
public class InspectionTaskParamExtend extends InspectionTaskParam {

    /**
     * 省编码
     */
    private String provinceId;
    /**
     * 市编码
     */
    private String cityId;

}
