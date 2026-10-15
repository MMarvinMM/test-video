package com.chinatower.video.modules.videocontrol.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户对象 sys_user
 * @auther: zhaozj
 * @date: 2022/11/23
 * @version: release_v1.5.30
 */
@Data
public class SysUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //用户ID
    private String userId;

    //脱敏用户名称
    private String desensitizeName;

    //是否租户管理员(0否,1是)
    private Integer isTenantAdmin;

    //租户id
    private String tenantId;
    //用户名称
    private String userName;

    //组织编码
    private String deptId;

}