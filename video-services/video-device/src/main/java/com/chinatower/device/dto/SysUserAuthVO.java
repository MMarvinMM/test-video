package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUserAuthVO  {

    private String authId;

    private String menuId;

    private String industryCode;

    private String tenantId;

    private String appVerCode;

    private String roleId;

}
