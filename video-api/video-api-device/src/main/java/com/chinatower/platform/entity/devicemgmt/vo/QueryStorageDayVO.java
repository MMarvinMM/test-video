package com.chinatower.platform.entity.devicemgmt.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName QueryStoreTimeVO
 * @Deacription 存储时长查询vo
 * @Author chenmf
 * @Date 2022/3/21 9:32
 * @Version 1.0
 **/
@Data
public class QueryStorageDayVO implements Serializable {
    
    @NotNull(message = "设备编码不能为空")
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
    
    @NotNull(message = "通道编码不能为空")
    @ApiModelProperty(value = "通道编码")
    private String channelCode;
    
    @ApiModelProperty(value = "订单编码")
    private String orderCode;
}
