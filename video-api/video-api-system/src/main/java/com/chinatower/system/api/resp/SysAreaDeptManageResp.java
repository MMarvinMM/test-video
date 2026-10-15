package com.chinatower.system.api.resp;

import lombok.Data;


/**
 * table:SYS_AREA_DEPT_MANAGE
 *
 * @Author: zheng.li
 * @Date: 2022-03-11 12:03:30
 **/
@Data
public class SysAreaDeptManageResp {
    /**
     * 数据ID
     */
    private Object id;

    /**
     * 省份区域CODE
     */
    private String provinceAreaCode;

    /**
     * 省份区域名称
     */
    private String provinceAreaName;

    /**
     * 城市区域CODE
     */
    private String cityAreaCode;

    /**
     * 城市区域名称
     */
    private String cityAreaName;

    /**
     * 区县区域CODE
     */
    private String countyAreaCode;

    /**
     * 区县区域名称
     */
    private String countyAreaName;

    /**
     * 省份部门CODE
     */
    private String provinceDeptCode;
    /**
     * 省份部门名称
     */
    private String provinceDeptName;
    /**
     * 城市部门CODE
     */
    private String cityDeptCode;

    /**
     * 城市部门名称
     */
    private String cityDeptName;

    /**
     * 管理区域CODE
     */
    private String manageCode;

    /**
     * 管理区域名称
     */
    private String manageName;


}
