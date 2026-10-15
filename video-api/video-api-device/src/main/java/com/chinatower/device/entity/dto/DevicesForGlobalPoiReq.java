package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class DevicesForGlobalPoiReq extends BaseDataScopeEntity {

    /**
     * 查询关键字
     */
    private String keyWord;

    /**
     * 行政区划编码
     */
    private String adCode;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

}
