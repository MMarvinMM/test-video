package com.chinatower.common.core.dto.bdm;

import lombok.Data;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2024/1/23 10:14
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerFieldsDTO {
    /**
     * 字段代码
     */
    private String code;
    /**
     * 字段名称
     */
    private String name;
    /**
     * 字段描述
     */
    private String remark;

    /**
     * 筛选类型 1文本 2-下拉 3-多选
     */
    private Integer filterType;
    /**
     * 类型 1:int 2:double 3:varchart 4:timestamp
     */
    private Integer type;
    /**
     * 是否字典 1-是
     */
    private Integer matchDictFlag;
    /**
     * 是否弹窗 1-是
     */
    private Integer needAlertFlag;
    /**
     * 是否 图斑编号
     */
    private int tbbhFlag;
    /**
     * 是否标识码
     */
    private int bsmFlag;
    /**
     * 是否面积字段
     */
    private int areaFlag;
    /**
     * 结果排序
     */
    private Integer resultIndex;
    /**
     * 识别功能的顺序号
     */
    private Integer identifyIndex;
    /**
     * 属性查询功能的顺序号（默认为0，起始为1）
     */
    private Integer filterIndex;
    /**
     * 下拉筛选值
     */
    private List<LayerTypeAttrFilterVO> dictList;
}
