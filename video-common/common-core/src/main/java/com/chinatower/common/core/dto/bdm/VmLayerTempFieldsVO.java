package com.chinatower.common.core.dto.bdm;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @desription: 图层模版字段
 * @data 2024/05/07 10:34
 * @author lyy
 * @version v1.6.60
 *
 */
@Data
public class VmLayerTempFieldsVO {

    private String id;

    /**
     * 图层模版名称
     */
    private String layerTempName;

    /**
     * 图层模板简称
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
     * 图斑信息顺序号
     */
    private Integer identifyIndex;

    /**
     * 列表的顺序号
     */
    private Integer resultIndex;

    /**
     * 是否匹配字典 1-是
     */
    private Integer matchDictFlag;
    /**
     * 是否面积字段
     */
    private int hasArea;

    /**
     * 是否图斑编号
     */
    private int hasTbbh;

    /**
     * 图层模板来源（默认空为用户导入、1-自定义新增）
     */
    private String sourceFlag;

    /**
     * 图层模板字段类别（0-默认、1-自定义）
     */
    private Integer category;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 需要删除字段字典的ID
     */
    private List<String> ids = new ArrayList<>();

    /**
     * 字典值
     */
    private List<LayerTypeAttrFilterVO> filters = new ArrayList<>();
}
