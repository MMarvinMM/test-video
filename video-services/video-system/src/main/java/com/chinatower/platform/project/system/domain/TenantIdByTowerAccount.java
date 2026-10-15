package com.chinatower.platform.project.system.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantIdByTowerAccount implements Serializable {

    private List<String> tenantIds;

    /**
     * true-总部 false-非总部
     */
    private Boolean headquarters;

}
