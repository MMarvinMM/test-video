package com.chinatower.platform.project.custom.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;

/**
 * api注册表
 */
@Data
@ToString
public class ApiIntfInfoVO {
    //api接口id
    private String apiIntfId;

    //api接口名称
    private String apiIntfName;

    //api接口类型 1000：服务端，1100：客户端
    private String apiIntfType;

    //调用路径
    private String callRoute;

    //是否开放，0：是，1：否
    private String isOpen;

    //开放权限 1000：行业应用，1100客户
    @Excel(name = "开放权限 1000：行业应用，1100客户")
    private String openPrivilege;


}