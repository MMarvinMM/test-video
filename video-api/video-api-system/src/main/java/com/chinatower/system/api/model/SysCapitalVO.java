package com.chinatower.system.api.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 省会
 * @data 创建时间：2023/8/22 9:51
 */
@Data
@ToString
public class SysCapitalVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 数据ID
     */
    private String Id;
    /**
     * 省编码
     */
    private String provCode;
    /**
     * 省名称
     */
    private String provName;
    /**
     * 省会编码
     */
    private String capitalCode;
    /**
     * 省会名称
     */
    private String capitalName;
    /**
     * 状态
     */
    private String status;

}
