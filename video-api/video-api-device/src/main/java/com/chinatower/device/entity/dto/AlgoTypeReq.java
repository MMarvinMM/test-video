package com.chinatower.device.entity.dto;

import lombok.Data;


/**
 * Created by zheng.yan on 2022/2/17.
 */
@Data
public class AlgoTypeReq implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 1-新增；2-修改； 3-删除
     */
    private int optType;
    /**
     * 1-有效；2-失效；
     */
    private int status;
    /**
     * 算法小类编码
     */
    private String algoMinTypeCode;
    /** 算法小类名称 */
    private String algoMinTypeName;
    /**
     * 算法大类编码
     */
    private String algoTypeCode;
    /** 算法大类名称 */
    private String algoTypeName;
    /**
     * 算法中类编码
     */
    private String algoSubTypeCode;
    /** 算法中类名称 */
    private String algoSubTypeName;
}
