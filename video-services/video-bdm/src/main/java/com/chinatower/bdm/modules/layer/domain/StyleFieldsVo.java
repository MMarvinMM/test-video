package com.chinatower.bdm.modules.layer.domain;

import cn.hutool.core.date.DateTime;
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
public class StyleFieldsVo implements Serializable {


    private String id;

    /**
     * 是否显示label  1-展示 0-不展示
     */
    private Integer showLabel;

    private String code;

    private String name;

    private Integer defaultShow;

    /**
     * 配置方式 1-按字典值 2-按数值区间
     */
    private Integer type;

    /**
     * 字典配置
     */
    private List<FieldStyleDictJson> dictJson;

}
