package com.chinatower.emergency.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 用户对象 sys_user
 *
 * @author bruce
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmergencySysUser {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String phone;
    private String avatar;
    private String userName;
}