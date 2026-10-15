package com.chinatower.platform.modules.keeper.entity;


import com.chinatower.mongodb.dto.BaseMongoDTO;
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
    private Date createTime;
}
