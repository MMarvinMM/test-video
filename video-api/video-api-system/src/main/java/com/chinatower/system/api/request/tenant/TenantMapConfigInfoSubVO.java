package com.chinatower.system.api.request.tenant;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantMapConfigInfoSubVO implements Serializable {

    private Integer minZoom;

    private Integer maxZoom;

    private String url;
}