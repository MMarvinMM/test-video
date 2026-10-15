package com.chinatower.bdm.modules.licensePlateLibrary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.bdm.modules.licensePlateLibrary.entity.CheckpointVehicleLicensePlateImportVO;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;

import java.util.List;

public interface CheckpointVehicleLicensePlateService extends IService<CheckpointVehicleLicensePlate> {

    /**
     * 黑白名单新增修改接口
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    Result<String> insertOrUpdateVehicleLicensePlate(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);

    /**
     * 黑白名单删除接口
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    Result<String> deleteVehicleLicensePlate(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);

    /**
     * 获取黑白名单信息详情
     *
     * @return String
     * @Author：Liqi
     * @requestID 中国铁塔_铁塔视联平台__出入口管理
     */
    Result<CheckpointVehicleLicensePlate> getVehicleLicensePlateDetail(String id);

    /**
     * 黑白名单按条件查询
     * @param checkpointVehicleLicensePlate
     * @return
     */
    List<CheckpointVehicleLicensePlate> listVehicleLicensePlateByCondition(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);

    List<SysImportDetail> importData(ImportParams<CheckpointVehicleLicensePlateImportVO> p);
}
