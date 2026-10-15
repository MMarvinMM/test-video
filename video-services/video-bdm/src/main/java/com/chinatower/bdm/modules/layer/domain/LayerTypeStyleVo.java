package com.chinatower.bdm.modules.layer.domain;

import cn.hutool.core.date.DateTime;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LayerTypeStyleVo extends BaseDataScopeEntity implements Serializable {

    private String id;

    /**
     * 图层类型id
     */
    private String layerTypeId;


    private String abbreviation;

    /**
     * 是否绑定字段 1-是 0-否
     */
    private Integer showBindField;

    private List<StyleFieldsVo> fields;

    /**
     * 字典配置 绑定字段=否 时 使用
     */
    private List<FieldStyleDictJson> dictJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private DateTime createTime;

    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private DateTime updateTime;

    private String modifier;

    private String tenantId;

    private String industryCode;
}
