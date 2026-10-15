package com.chinatower.platform.project.custom.entity;

import com.chinatower.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * api注册表
 */
@Data
@ToString
public class AuthRegisterApiVO {
    //客户编码
    private String id;

    //客户编码
    private String tenantId;

    //客户名称
    private String tenantName;

    //appKey
    private String appKey;

    //secretKey
    private String secretKey;

    //是否开放api 0:否  1:是
    @Excel(name = "是否开放api", readConverterExp = "1=是,0=否")
    private String isOpen;

    //是否有效 0失效 1有效
    private String deleteFlag;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    /** 更新者 */
    private String updateBy;

    /** 创建人 */
    private String createBy;

    private List<AuthRegisterFuncApiVO> authRegisterFuncApiList;

    //客户编码
    private String customerCode;

    //客户名称
    private String customerName;

    //客户名称
    private String openModifier;

    //api接口名称
    private String apiIntfName;

    //api接口编码
    private String apiIntfCode;

    //api接口类型 1000：服务端，1100：客户端
    private String apiIntfType;

    //订购失效 0 订购生效 1
    private String status;


}