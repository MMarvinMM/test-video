package com.chinatower.industry.dto;

import com.chinatower.device.entity.dto.DeviceModelDTO;
import lombok.Data;

/**
 * 功能描述: 多线程使用实体类
 *
 */
@Data
public class DeviceThreadDTO {

    private DeviceModelDTO deviceModelDTO;

    private DeviceIndReqDTO deviceIndReqDTO;

    private int queryType;

}
