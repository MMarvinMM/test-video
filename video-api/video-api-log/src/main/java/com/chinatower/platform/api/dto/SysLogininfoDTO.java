package com.chinatower.platform.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author iwhalecloud
 */
@Data
public class SysLogininfoDTO implements Serializable
{
    /** 用户账号 */
    private String userName;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录状态 0成功 1失败 */
    private String status;

    /** 登录端类型 */
    private String clientType;

    /** 手机号码 */
    private String phonenumber;

    /** 操作系统 */
    private String os;

    /** 访问时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date beginTime;

    /** 访问时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endTime;

    private Integer pageNo;

    private Integer pageSize;

    private String orderByColumn;

    @Pattern(regexp = "asc|desc",message = "排序规则不合法")
    private String isAsc;

    /** 客户id */
    private String tenantId;

    /** 客户名称 */
    private String tenantName;

    /** 客户名称 */
    private String userType;

    /** 铁塔组织id */
    private String deptId;

    /**
     * 客户名称/ID
     */
    private String tenantIdOrName;
}
