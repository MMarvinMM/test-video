package com.chinatower.bdm.modules.licensePlateLibrary.service;

import com.chinatower.bdm.api.entity.CheckpointVehicleTypeVO;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;

public interface CheckpointVehicleTypeService {
    /**
     * 根据客户+行业查询车辆类型
     *
     * @return List<CheckpointVehicleTypeVO>
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    TableDataInfo getVehicleTypeList(CheckpointVehicleTypeVO checkpointVehicleTypeVO);

    /**
     * 车辆类型新增接口
     * @Author：Liqi
     * @return string
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    Result<String> insertOrUpdateVehicleType(CheckpointVehicleTypeVO checkpointVehicleTypeVO);
    /**
     * 车辆类型删除接口
     * @Author：Liqi
     * @return string
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    Result<String> deleteVehicleType(CheckpointVehicleTypeVO checkpointVehicleTypeVO);
}
