package com.chinatower.platform.entity.gis;

import lombok.Data;

/**
 * @desription: PG 类型字段
 * @data 创建时间：2024/1/17 9:45
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SysGisFieldsDTO {
    /**
     * 类型代码
     */
    private String abbreviation;
    /**
     * 字段编码
     */
    private String code;
    /**
     * 字段显示名称
     */
    private String name;
    /**
     * 备注说明
     */
    private String remark;
    /**
     * 类型 1-int 2-double 3-character 3-date
     */
    private Integer type;
    /**
     * 长度
     */
    private Integer chartLength;
    /**
     * 是否不能为空
     */
    private Integer notNullFlag;
    /**
     * 查询顺序
     */
    private Integer filterIndex;
    /**
     * 筛选类型 0-输入框 1-下拉框
     */
    private Integer filterType;
    /**
     * 结果列表的顺序号
     */
    private Integer resultIndex;
}
