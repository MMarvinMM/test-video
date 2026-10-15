package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.CheckpointDeviceInfoDO;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceExportVO;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.SysAreaBusinessCheckpointVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * @Author：zl
 * @Description：卡口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__卡口管理
 */
public interface CheckpointDeviceInfoMapper extends BaseMapper<CheckpointDeviceInfoDO> {

    /**
     * 根据条件分页查询设备信息
     *
     * @param checkpointDeviceInfoVO
     * @return
     */
    List<CheckpointDeviceInfoVO> queryCheckpointDeviceInfoList(CheckpointDeviceInfoVO checkpointDeviceInfoVO);

    /**
     * 导出卡口设备数据
     *
     * @param checkpointDeviceInfoVO
     * @return
     */
    List<CheckpointDeviceExportVO> queryCheckpointDeviceExportList(CheckpointDeviceInfoVO checkpointDeviceInfoVO);

    /**
     * 删除卡口设备
     *
     * @param checkpointDeviceInfoVO
     * @return
     */
    int deleteCheckpointDeviceInfoBatch(CheckpointDeviceInfoVO checkpointDeviceInfoVO);

    List<SysAreaBusinessCheckpointVO> getDeviceList(CheckpointDeviceInfoVO checkpointDeviceInfoVO);

    List<SysAreaBusinessCheckpointVO> selectSysArea(CheckpointDeviceInfoVO checkpointDeviceInfoVO);

    List<CheckpointDeviceInfoVO> selectDeviceInfo(List<String> channelCodeList);

    /**
     * 新增卡口设备
     *
     * @param checkpointDeviceInfoVOList
     * @return
     */
    int insertCheckpointDeviceInfo(List<CheckpointDeviceInfoVO> checkpointDeviceInfoVOList);

}
