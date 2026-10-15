package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class GridByTenantId {

    private String tenantId;

    private List<GridVO> list;
}
