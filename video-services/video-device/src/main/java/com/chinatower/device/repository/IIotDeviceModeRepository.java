package com.chinatower.device.repository;

import com.chinatower.common.core.dto.IotDeviceModeDTO;
import com.chinatower.common.entity.Result;

public interface IIotDeviceModeRepository {

    /**
     * 查询雷达设备类型
     * @param model
     * @return
     */
    Result<Object> queryIotDeviceModeList(IotDeviceModeDTO model);

}
