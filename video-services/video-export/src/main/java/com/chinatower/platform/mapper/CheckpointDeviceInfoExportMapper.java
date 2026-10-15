package com.chinatower.platform.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceExportVO;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import com.chinatower.platform.model.CheckpointDeviceInfoExportDO;

import java.util.List;

/**
 * @Author：zl
 * @Description：卡口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__卡口管理
 */
public interface CheckpointDeviceInfoExportMapper extends BaseMapper<CheckpointDeviceInfoExportDO> {



    /**
     * 导出卡口设备数据
     *
     * @param checkpointDeviceInfoVO
     * @return
     */
    List<CheckpointDeviceExportVO> queryCheckpointDeviceExportList(CheckpointDeviceInfoVO checkpointDeviceInfoVO);



}
