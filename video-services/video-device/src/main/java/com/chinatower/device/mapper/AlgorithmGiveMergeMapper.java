package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dto.AlgorithmGiveMergeDO;
import com.chinatower.platform.entity.device.DeviceChannelVO;

import java.util.List;

public interface AlgorithmGiveMergeMapper extends BaseMapper<AlgorithmGiveMergeDO> {

    List<AlgorithmGiveMergeDO> queryAlgorithmGiveMerge(List<DeviceChannelVO> deviceChannelList);
    int editBatchById(List<AlgorithmGiveMergeDO> updateList);
    int deleteBatchById(List<AlgorithmGiveMergeDO> deleteList);
    int editBatchSyncById(List<AlgorithmGiveMergeDO> updateList);
    List<AlgorithmGiveMergeDO> queryAlgorithmGiveMergeXXLJob();
}
