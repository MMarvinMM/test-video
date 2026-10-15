package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 综合查询请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceComprehensiveReq extends BaseDataScopeEntity implements Serializable {

    /**
     * 关键字
     */
    @NotBlank(message = "关键字不能为空！")
    private String keyWord;

    /**
     * 查询类型: 1 摄像机、2雷达 、3 物联设备、4 无人机、5 大喇叭
     */
    @NotBlank(message = "查询设备类型不能为空！")
    private String queryType;

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    private Integer pageSize = 10;

    /**
     * 设备类型，用于查询雷达或物联设备
     */
    private Integer categoryCode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;
}
