package com.chinatower.bdm.modules.layer.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_layer_type_style_mapping")
@ApiModel(value="LayerTypeStyleMapping对象", description="图层类别字段管理")
public class LayerTypeStyleMapping implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 图例配置id
     */
    private String typeStyleId;

    /**
     * 样式名称 唯一值，既是sld文件名，也是geoserver的样式服务名
     */
    private String styleName;

    /**
     * 图层类型简称
     */
    private String abbreviation;

    /**
     * 工作空间名
     */
    private String workspaceName;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除状态 1-删除
     */
    private Integer deleteFlag;

    /**
     * 图例几何类型 1point点/2line线/3polygon面
     */
    private String geoType;

    /**
     * 是否默认 1-是
     */
    @TableField(exist = false)
    private Integer defaultShow;
    /**
     * 字段
     */
    @TableField(exist = false)
    private String code;

    /**
     * 字典名称
     */
    @TableField(exist = false)
    private String codeName;

    /**
     * 是否范围 1-按字典值 2-按数值区间
     */
    @TableField(exist = false)
    private Integer rangeType;

    /**
     * 字典颜色值
     */
    @TableField(exist = false)
    private String dictJson;

    /**
     * 是否绑定字段 1-是 0-否
     */
    @TableField(exist = false)
    private Integer showBindField;
    /**
     * 图层类型ID
     */
    @TableField(exist = false)
    private String layerTypeId;
    /**
     * 是否显示label  1-展示 0-不展示
     */
    @TableField(exist = false)
    private Integer showLabel;

}
