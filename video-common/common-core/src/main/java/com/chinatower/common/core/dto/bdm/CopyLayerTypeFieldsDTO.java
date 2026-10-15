package com.chinatower.common.core.dto.bdm;

import lombok.Data;

/**
 * @desription: 图层类型字段复制
 * @data 创建时间：2024/1/18 11:33
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class CopyLayerTypeFieldsDTO {

    /**
     *待复制的类型简称
     */
    private String treatAbbreviation;
    /**
     *需要复制类型简称
     */
    private String targetAbbreviation;
}
