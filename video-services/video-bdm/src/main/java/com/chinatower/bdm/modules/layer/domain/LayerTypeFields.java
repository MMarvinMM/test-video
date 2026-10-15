package com.chinatower.bdm.modules.layer.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @desription: 类型字段
 * @data 创建时间：2024/1/17 16:56
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_layer_type_fields")
@ApiModel(value="LayerTypeFields对象", description="图层类别字段管理")
public class LayerTypeFields {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 图层类型简称
     */
    private String abbreviation;

    /**
     * 字段代码
     */
    private String code;

    /**
     * 字段显示名称
     */
    private String name;

    /**
     * 备注，说明
     */
    private String remark;

    /**
     * 类型 1:int 2:double 3:varchart 4:timestamp
     */
    private Integer type;

    /**
     * 字符类型长度
     */
    private Integer chartLength;

    /**
     * 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空）
     */
    private Integer notNullFlag;

    /**
     * 筛选类型（0文本，1下拉，默认：2空）
     */
    private Integer filterType;

    /**
     * 属性查询功能的顺序号（默认为0，起始为1）
     */
    private Integer filterIndex;

    /**
     * 识别功能的顺序号
     */
    private Integer identifyIndex;

    /**
     * 结果列表的顺序号
     */
    private Integer resultIndex;

    /**
     * 删除状态（0 代表 false，未删除; 1 代表 true，已删除）
     */
    private Integer deleteFlag;
    /**
     * 是否匹配字典 1-是
     */
    private Integer matchDictFlag;
    /**
     * 是否需要弹窗 1-是
     */
    private Integer needAlertFlag;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
