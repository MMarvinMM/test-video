package com.chinatower.platform.service;

import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceExportVO;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;

import java.util.List;

public interface IVideoCheckpointDeviceMgmtInfoService {


    /**
     * 功能描述：导出卡口设备数据
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @return List<CheckpointDeviceExportVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    List<CheckpointDeviceExportVO> queryCheckpointDeviceExportList(CheckpointDeviceInfoVO checkpointDeviceInfoVO);

}
