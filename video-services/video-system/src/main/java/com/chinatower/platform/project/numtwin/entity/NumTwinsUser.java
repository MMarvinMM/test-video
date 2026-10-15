package com.chinatower.platform.project.numtwin.entity;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 16:39
 */

@Data
public class NumTwinsUser extends PageDTO {

    private String userId;

    private String userName;

    private String nickName;

    private String deptId;

    private String roleId;

    private String level;

    private String deptName;

    private String roleName;

    private String updateUser;

    private String tenantId;

    private Long configId;

    private String sort;

    private String sortName;

    private List<String> roles;

    private String industryCode;
}
