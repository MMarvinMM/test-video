package com.chinatower.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRoleDTO implements Serializable {
    
    /**
     * 角色id
     */
    private Long roleId;
    
    /**
     * 行业编码
     */
    private String industryCode;
    
    /**
     * 行业版本编码
     */
    private String appVerCode;
}
