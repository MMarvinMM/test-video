package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageSensorDeviceVO extends BaseDataScopeEntity implements Serializable {

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
     * 设备编码列表
     * @required true
     */
    @ApiModelProperty(value = "设备编码列表")
    private List<String> deviceCodeList;


    private Integer pageNum;
}
