package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 功能描述: 摄像机收藏实体类
 *
 * @auther: hued
 * @date: 2022年8月11日10:04:35
 * @version: release_v1.5
 * @requestID:
 */
@Data
public class UserDeviceCollectionDTO extends BaseDataScopeEntity {
    /**
     * 收藏对象类型 1.行政区域 2.租户组织 3.标签 4.管理区域 5.摄像机 6.通道 7.物联设备
     */
    @ApiModelProperty(value = "收藏对象类型 1.行政区域 2.租户组织 3.标签 4.管理区域 5.摄像机 6.通道 7.物联设备")
    @NotEmpty(message = "收藏对象类型不能为空")
    private  String collObjType;
    /**
     * 收藏对象编码
     */
    @ApiModelProperty(value = "收藏对象编码")
    @NotEmpty(message = "收藏对象编码不能为空")
    private  List<String> collObjCode;
    /**
     * 操作标识 1：新增，2：删除
     */
    @ApiModelProperty(value = "操作标识 1：新增，2：删除")
    @NotNull(message = "操作标识不能为空")
    private Integer optType;


}
