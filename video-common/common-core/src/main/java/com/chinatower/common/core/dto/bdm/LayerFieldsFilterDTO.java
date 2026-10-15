package com.chinatower.common.core.dto.bdm;

import lombok.Data;

import java.util.List;

/**
 * @desription: 图层字段过滤
 * @data 创建时间：2024/1/23 9:38
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerFieldsFilterDTO {
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
     * 筛选类型 0文本 1-下拉
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
    private List<LayerFieldsFilterChildListDTO> dictList;
}
