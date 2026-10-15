package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenxinhui
 * @Date 2021/11/23
 */
@Data
public class UserOrgDto implements Serializable {
    /**
     * 组织级别
     */
    private Integer orgLevel;
    /**
     * 组织路径
     */
    private String orgPath;
    /**
     * 登录名
     */
    private String userName;

    /**
     * 组织编码
     */
    private String orgCode;


    private String timeDimensionType;

    private String lastMonth;
}
