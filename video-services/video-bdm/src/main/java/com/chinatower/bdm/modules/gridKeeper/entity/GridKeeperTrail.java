package com.chinatower.bdm.modules.gridKeeper.entity;


import com.chinatower.bdm.common.Constants;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class GridKeeperTrail extends BaseMongoDTO implements Serializable {


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
     * 创建时间
     */
    @JsonFormat(pattern= Constants.DATE_FORMATE,timezone = "GMT+8")
    private Date createTime;

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
