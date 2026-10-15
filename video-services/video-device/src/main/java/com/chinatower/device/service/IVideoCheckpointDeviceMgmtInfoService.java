package com.chinatower.device.service;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceExportVO;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;

import java.util.List;

public interface IVideoCheckpointDeviceMgmtInfoService {

    /**
     * 功能描述：根据条件分页查询设备信息
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @return List<CheckpointDeviceInfoVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    List<CheckpointDeviceInfoVO> queryCheckpointDeviceInfoPage(CheckpointDeviceInfoVO checkpointDeviceInfoVO);

    /**
     * 功能描述：根据条件不分页查询设备信息
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @return List<CheckpointDeviceInfoVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    List<CheckpointDeviceInfoVO> queryCheckpointDeviceInfo(CheckpointDeviceInfoVO checkpointDeviceInfoVO);




    /**
     * 功能描述：新增卡口设备
     * @Author：zl
     * @param channelCodeList
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    int insertCheckpointDeviceInfo(List<String> channelCodeList);

    /**
     * 功能描述：删除卡口设备
     * @Author：zl
     * @param checkpointDeviceInfoVO
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    void deleteCheckpointDeviceInfoBatch(CheckpointDeviceInfoVO checkpointDeviceInfoVO);

    /**
     * 功能描述：查询租户设备通道树
     * @Author：zl
     * @return Result<List<TreeSelect>>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    Result<List<TreeSelect>> getCheckpointDeviceTree(CheckpointDeviceInfoVO checkpointDeviceInfoVO);
}
