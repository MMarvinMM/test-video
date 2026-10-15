package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class DevicesForGlobalSurroundReq extends BaseDataScopeEntity {
    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 查询数量
     */
    private int deviceNum;

    /**
     * 查询半径
     */
    private Integer distance;

    /**
     * 在离线状态 0在线 1离线
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageSize;
    /**
     * 页长
     */
    private Integer pageNum;
}
