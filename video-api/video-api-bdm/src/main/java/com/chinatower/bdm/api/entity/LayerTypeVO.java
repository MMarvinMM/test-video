package com.chinatower.bdm.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.chinatower.common.core.vo.BaseGridEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 图层类别管理vo
 * @author zl
 */
@Data
@ToString
public class LayerTypeVO {

    private String id;

    /*
     * 图层列表名称
     */
    private String typeName;

    /*
     * 创建人
     */
    private String creator;

    /*
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    private Date createTime2;

    /*
     * 修改人
     */
    private String modifier;

    /*
     * 修改时间
     */
    private Date updateTime;
    private Date updateTime2;

    /*
     * 删除状态: 原字典值：normal正常，删除deleted 现字典值：0正常，删除1
     */
    private Integer deleteFlag;

    /*
     * 租户id
     */
    private String tenantId;

    /*
     * 行业编码
     */
    private String industryCode;

    /*
     * 行业版本编码
     */
    private String appVerCode;

    /*
     * 0-默认 1-自定义
     */
    private Integer category;

    /*
     * 简称
     */
    private String abbreviation;

    /*
     * 面积字段
     */
    private String areaRef;

    /*
     * 图斑编号字段
     */
    private String tbbhRef;

    /*
     * 父类类型名称
     */
    private String superTypeName;

    /*
     * 祖类类型名称
     */
    private String initialTypeName;

    /*
     * 属性显示名称（套合）
     */
    private String label;

    /*
     * 唯一值的字段名（套合）
     */
    private String uniqueFieldCode;

    /*
     * 覆盖分类字段code（套合）
     */
    private String coveredFieldCode;

    /*
     * 覆盖显示的标签名（套合）
     */
    private String coveredLabel;

    /*
     * 未覆盖显示的标签名（套合）
     */
    private String notCoveredLabel;

    /*
     * 是否记录未填写的字段
     */
    private Integer addOtherFieldValuesFlag;
    /*
     * 建表语句
     */
    private String initSql;

    /**
     * 页码
     */
    private Integer pastIndex;
    /**
     * 页长
     */
    private Integer pageSize;


}
