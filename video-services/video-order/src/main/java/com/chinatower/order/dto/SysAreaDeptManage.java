package com.chinatower.order.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * $tableInfo.comment
 * table:SYS_AREA_DEPT_MANAGE
 *
 * @Author: zs
 * @Date: 2021-03-14 12:03:30
 **/
@Data
public class SysAreaDeptManage implements Serializable {
    private static final long serialVersionUID = 692997753386644628L;

    private Integer id;

    private String provinceAreaCode;

    private String provinceAreaName;

    private String cityAreaCode;

    private String cityAreaName;

    private String countyAreaCode;

    private String countyAreaName;

    private String provinceDeptCode;

    private String provinceDeptName;

    private String cityDeptCode;

    private String cityDeptName;

    private String manageCode;

    private String manageName;


}
