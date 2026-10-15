package com.chinatower.system.api.model;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class SysUserWhiteListVO extends PageDTO {

    private static final long serialVersionUID = 1L;

    private String id;
    /**
     * 登陆账号
     */
    @Excel(name = "账号")
    private String userName;

    /**
     * 客户名称
     */
    private String nickName;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 加密手机号
     */
    private String aesPhoneNumber;

    private String phoneNumberDesensitization;
    /**
     * 来源：0铁塔 1租户
     */
    private String source;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 修改人
     */
    private String updateBy;
}
