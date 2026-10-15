package com.chinatower.system.api.model;

import com.chinatower.platform.entity.admin.SysRole;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @desription: 用户角色实体
 * @data 创建时间：2023/8/22 9:35
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@ToString
public class UserRoleVO extends SysRole implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    private String userId;


    private String industryCode;


    private String appVerCode;


    private List<UserRoleVO> userList;
}