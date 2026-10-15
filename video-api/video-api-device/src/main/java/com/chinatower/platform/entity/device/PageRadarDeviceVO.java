package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageRadarDeviceVO extends BaseDataScopeEntity implements Serializable {


    /**
     * 租户ID
     * @required true
     */
    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    /**
     * 页数
     * @required true
     */
    @ApiModelProperty(value = "页数")
    private Integer pageNo = 0;

    /**
     * 分页数
     * @required true
     */
    @ApiModelProperty(value = "分页数")
    private Integer pageSize = 10;

    /**
     * 设备编码集合
     * @required false
     */
    @ApiModelProperty(value = "设备编码集合")
    private List<String> deviceCodeList;


    private Integer pageNum;
}
