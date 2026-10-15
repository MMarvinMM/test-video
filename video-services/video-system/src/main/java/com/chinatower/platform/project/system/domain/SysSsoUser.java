package com.chinatower.platform.project.system.domain;

import com.chinatower.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 请求头参数配置表 vm_api_header
 *
 * @author
 */
@Data
public class SysSsoUser
{
    private static final long serialVersionUID = 1L;

    /** 参数主键 */
    @Excel(name = "主键", cellType = Excel.ColumnType.NUMERIC)
    private String userId;

    /** ssoId */
    @Excel(name = "ssoId")
    private String ssoId;

    /** 登录账号 */
    @Excel(name = "登录账号")
    private String userName;

    /** 用户名*/
    @Excel(name = "用户名")
    private String nickName;


    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    private String tenantId;//租户id

    private String password;//用户密码

    private String phoneNumber;//手机号

}
