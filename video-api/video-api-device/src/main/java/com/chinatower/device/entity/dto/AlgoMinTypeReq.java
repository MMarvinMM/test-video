package com.chinatower.device.entity.dto;

import lombok.Data;


/**
 * Created by zheng.yan on 2022/2/17.
 */
@Data
public class AlgoMinTypeReq implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 算法小类编码
     */
    private String algoMinTypeCode;
    /** 算法小类名称 */
    private String algoMinTypeName;
 }
