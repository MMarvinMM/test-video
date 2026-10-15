package com.chinatower.bdm.api.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@AllArgsConstructor
public class GridKeeperTrailNew implements Serializable {


    /**
     * 网格员ID
     */
    private String keeperId;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 创建时间（字符串类型）
     */
    private String createTime;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 网格员名称
     */
    private String keeperName;


    /**
     * 网格员类型：1-网格长；2-网格员
     */
    private String keeperType;
}
