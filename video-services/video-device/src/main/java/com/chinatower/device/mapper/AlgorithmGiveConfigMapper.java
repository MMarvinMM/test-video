package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dto.AlgorithmGiveConfigDO;
import com.chinatower.device.dto.AlgorithmGiveMergeDO;
import com.chinatower.device.dto.GiveMergeReq;
import com.chinatower.platform.entity.device.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlgorithmGiveConfigMapper extends BaseMapper<AlgorithmGiveConfigDO> {
    List<AlgorithmGiveConfigSaveVO> queryById(AlgorithmGiveConfigDO algorithmGiveConfigDO);
    String queryOrderCode(AlgorithmGiveConfigDO algorithmGiveConfigDO);
    List<AlarmTypeInfoVO> queryAlarmType(AlarmTypeReq alarmTypeReq);
    List<AlgorithmGiveMergeDO> algorithmGiveConfigMerge(GiveMergeReq giveMergeReq);
    int editById(AlgorithmGiveConfigDO algorithmGiveConfigDO);
    int checkAlarmType(AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO);
    int checkConfigInfo(AlgorithmGiveConfigDO algorithmGiveConfigDO);
    List<DeviceChannelVO> getDevChanInfo(List<String> list);
    int doStopBatch(AlgorithmGiveMergeOrderVO algorithmGiveMergeOrderVO);
    int editBatchConfigByChan(List<AlgorithmGiveConfigDO> updateList);
    int selectDoStopBatch(AlgorithmGiveMergeOrderVO algorithmGiveMergeOrderVO);
    int insertGiveLicenseRel(List<BlackWhiteVO> list);
    int deleteGiveLicenseRel(int configId);
    int deleteGiveLicenseRelByVehicleNumber(String vehicleNumber,int blackOrWhite);
    List<BlackWhiteVO> selectGiveLicenseRel(BlackWhiteReq blackWhiteReq);

    List<AlgorithmGiveConfigSaveVO> checkAlarmTypeByList(AlgorithmGiveBatchSaveVO algorithmGiveBatchSave);

    List<AlgorithmGiveConfigDO> queryOrderCodeByDevices(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("addList") List<AlgorithmGiveConfigSaveVO> addList);

    List<AlgorithmGiveConfigDO> getByIds(List<AlgorithmGiveConfigSaveVO> list);

    void deleteGiveLicenseRelBatch(List<AlgorithmGiveConfigSaveVO> list);

    List<AlgorithmGiveMergeDO> findTriggerListByDeviceChannel(GiveMergeReq giveMergeReq);

    List<AlgorithmGiveConfigSaveVO> getAlgorithmGiveListMCP(AlgorithmGiveConfigSaveVO algorithmInfoDO);

    /**
     * 根据订单查询vm_order_algorithm_config精准匹配的设备+算法关系
     */
    List<AlgorithmGiveConfigDO> selectOrderAlgorithmConfig(AlgorithmGiveMergeOrderVO vo);

    /**
     * 根据订单关联查询vm_order_devices和vm_order_algorithms的设备+算法关系（非精准匹配）
     */
    List<AlgorithmGiveConfigDO> selectOrderDeviceAlgorithms(AlgorithmGiveMergeOrderVO vo);

    /**
     * 精准匹配场景：更新vm_algorithm_give_config中不在vm_order_algorithm_config匹配范围内的数据
     */
    int updateGiveConfigNotInPrecise(AlgorithmGiveMergeOrderVO vo);

    /**
     * 精准匹配场景：更新vm_video_preset中不在vm_order_algorithm_config匹配范围内的数据（解除算法关联）
     */
    int updatePresetNotInPrecise(AlgorithmGiveMergeOrderVO vo);

    /**
     * 非精准匹配场景：更新vm_algorithm_give_config中不在vm_order_devices+vm_order_algorithms匹配范围内的数据
     */
    int updateGiveConfigNotInOrder(AlgorithmGiveMergeOrderVO vo);

    /**
     * 非精准匹配场景：更新vm_video_preset中不在vm_order_devices+vm_order_algorithms匹配范围内的数据（解除算法关联）
     */
    int updatePresetNotInOrder(AlgorithmGiveMergeOrderVO vo);

    /**
     * 精准匹配场景：查询vm_algorithm_give_config中待更新的设备通道（NOT EXISTS于vm_order_algorithm_config）
     */
    List<DeviceChannelVO> selectGiveConfigChannelsNotInPrecise(AlgorithmGiveMergeOrderVO vo);

    /**
     * 非精准匹配场景：查询vm_algorithm_give_config中待更新的设备通道（NOT EXISTS于vm_order_devices+vm_order_algorithms）
     */
    List<DeviceChannelVO> selectGiveConfigChannelsNotInOrder(AlgorithmGiveMergeOrderVO vo);
}
