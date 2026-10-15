package com.chinatower.platform.project.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryThirdMenuDTO implements Serializable {

    private String industryCode;

    private String tenantId;

    private String appVerCode;

    private String menuId;
}
