package com.chinatower.platform.entity.order;

import com.chinatower.common.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @date 2022-03-21
 */
@Data
public class OrganStreetRelVO extends BaseEntity implements Serializable {
    private String tenantId;

    private String deptId;

    private String provinceCode;

    private String cityCode;

    private String countyCode;

    private String streetCode;

    private int categoryCode;

    private String deleteFlag;

    private String industryCode;

    private String appVerCode;

    private Long id;
}
