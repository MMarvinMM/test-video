package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserDeviceCollectionQueryDTO extends BaseDataScopeEntity {
    /**
     * 收藏对象类型 1.行政区域 2.租户组织 3.标签 4.管理区域 5.摄像机 6.通道 7.物联设备
     */
    @ApiModelProperty(value = "收藏对象类型 1.行政区域 2.租户组织 3.标签 4.管理区域 5.摄像机 6.通道 7.物联设备")
    private  List<String> collObjTypeList;

}
