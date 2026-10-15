package com.chinatower.bdm.api.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class GridKeeperListByGridIdVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String keeperId;
    private String keeperCode;
    private String keeperName;
    private String longitude;
    private String latitude;
    private String keeperType;
    private String isGridManager;
}
