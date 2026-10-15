package com.chinatower.outdata.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;

@Data
public class QuerySensorCountReq extends CommonFeignEntity {

    /**
     * 区域类型
     * @required false
     */
    private String areaType;

    /**
     * 区域编码
     * @required false
     */
    private String areaCode;
}
