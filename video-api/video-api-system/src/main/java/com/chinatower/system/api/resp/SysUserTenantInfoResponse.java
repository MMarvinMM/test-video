package com.chinatower.system.api.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 获得租户下所有用户信息（区域、角色、性别）
 * @data 创建时间：2023/3/21 9:07
 */
@ApiModel(value = "SysUserTenantInfoResponse", description = "用户信息返回参数")
@Data
public class SysUserTenantInfoResponse {

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;
    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别")
    private String sex;
    /**
     * 区域信息
     */
    @ApiModelProperty(value = "区域信息")
    private List<SysUserTenantAreaResponse> areas;

    /**
     * 角色信息
     */
    @ApiModelProperty(value = "角色信息")
    private String roles;

}
