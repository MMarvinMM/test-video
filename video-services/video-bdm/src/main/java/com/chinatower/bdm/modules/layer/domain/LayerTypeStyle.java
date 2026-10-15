package com.chinatower.bdm.modules.layer.domain;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_layer_type_style")
public class LayerTypeStyle implements Serializable {

    private String id;


    private String code;

    private String name;

    /**
     * 图层类型id
     */
    private String layerTypeId;

    private String abbreviation;

    /**
     * 配置方式 1-按字典值 2-按数值区间
     */
    private Integer type;

    /**
     * 是否绑定字段 1-是 0-否
     */
    private Integer showBindField;


    /**
     * 是否显示label  1-展示 0-不展示
     */
    private Integer showLabel;

    private Integer defaultShow;

    /**
     * 字典配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<FieldStyleDictJson> dictJson;

    private Date createTime;

    private String creator;

    private Date updateTime;

    private String modifier;

    private String tenantId;

    private String industryCode;
}
