package com.chinatower.common.core.dto.bdm;

import lombok.Data;

/**
 * @desription: 图层自定义样式
 * @data 创建时间：2024/10/18 15:17
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerTypeStyleMappingVO {
    /**
     * 样式名称 唯一值，既是sld文件名，也是geoserver的样式服务名
     */
    private String styleName;

    /**
     * 字段
     */
    private String code;

    /**
     * 字典名称
     */
    private String codeName;

    /**
     * 图层类型简称
     */
    private String abbreviation;

    /**
     * 是否绑定字段 1-是 0-否
     */
    private Integer showBindField;
    /**
     * 图层类型ID
     */
    private String layerTypeId;
    /**
     * 是否显示label  1-展示 0-不展示
     */
    private Integer showLabel;

    /**
     * 工作空间名
     */
    private String workspaceName;

    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 是否默认 1-是
     */
    private Integer defaultShow;

    /**
     * 字典颜色值
     */
    private String dictJson;

    /**
     * 是否范围 1-不是 2-是
     */
    private Integer rangeType;
}
