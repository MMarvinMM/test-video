package com.chinatower.system.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @desription: 订购
 * @data 创建时间：2023/8/22 10:05
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class AlgoTreeVO implements Serializable {
    /**
     *算法大类编码
     */
    private String algoTypeCode;
    /**
     * 算法大类名称
     */
    private String algoTypeName;

    /**
     * 算法唯一编码
     */
    private String algorithmCode;
    /**
     * 算法名称
     */
    private String algorithmName;
    /**
     * 唯一编码
     */
    private String uniqueCode;
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 等级
     */
    private String level;
    /**
     * 上级部门
     */
    private String parentCode;
    /**
     * 算法列表
     */
    private List<AlgoTreeVO> list = new ArrayList<>();

    /**
     * 标注作为集合的第一条数据
     */
    private Integer first;

    /**
     * 行业
     */
    private String industryCode;
    /**
     * 版本
     */
    private String appVersion;
}
