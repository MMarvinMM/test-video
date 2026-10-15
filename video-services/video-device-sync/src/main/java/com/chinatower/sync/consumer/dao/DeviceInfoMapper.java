package com.chinatower.sync.consumer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.sync.consumer.entity.ChannelDetectionFactorDTO;
import com.chinatower.sync.consumer.entity.DeviceInfoDO;
import com.chinatower.sync.consumer.entity.VodResultVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 *
 * 功能描述: 租户视角--摄像机管理 DeviceInfoMapper
 *
 * @auther: zhaozj
 * @date: 2022/8/2
 * @version: release_v1.5
 * @requestID: 资源管理_摄像机管理
 */
public interface DeviceInfoMapper extends BaseMapper<DeviceInfoDO> {

    /**
     * 查询设备/设备下可见光通道、设备检测信息
     * @param deviceCodeSet
     * @return
     */
    List<DeviceInfoVO> getDeviceChannelByDeviceCodeList(@Param("deviceCodeList") Set<String> deviceCodeSet);

    /**
     * 批量更新检测结果
     * @param updateList
     * @return
     */
    int batchUpdateDiagnoseInfo(List<VodResultVO> updateList);

    /**
     * 根据通道编码查询通道检测因子
     * @param channelCodeList
     * @return
     */
    List<ChannelDetectionFactorDTO> getDeviceFactorListByChannelList(@Param("channelCodeList") List<String> channelCodeList);

    /**
     * 根据通道编码批量删除检测数据
     * @param channelCodeList
     * @return
     */
    int deleteDeviceFactorListByChannelList(@Param("channelCodeList") List<String> channelCodeList);

    int batchInsertDetection(@Param("list") List<ChannelDetectionFactorDTO> detectionList);

    int batchUpdateChannelDiaFile(@Param("list") List<ChannelDetectionFactorDTO> detectionList);

}
