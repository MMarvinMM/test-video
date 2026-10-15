package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.device.dataobject.ChannelInfoDO;

import java.util.List;

/**
 *
 * 功能描述: 租户视角--摄像机管理 ChannelInfoMapper
 *
 * @auther: zhaozj
 * @date: 2022/8/2
 * @version: release_v1.5
 * @requestID: 资源管理_摄像机管理
 */
public interface ChannelInfoMapper extends BaseMapper<ChannelInfoDO> {

    /**
     * 功能描述: 查找客户设备关联的通道
     * @param: deviceInfoVO
     * @return: List<ChannelInfoDO>
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    List<ChannelInfoVO> listChannelInfoForCustDev(DeviceInfoVO deviceInfoVO);

    List<ChannelInfoVO> listChannelInfoForCustDevNew(DeviceInfoVO deviceInfoVO);

    List<ChannelInfoVO> getAllChannelInfo(DeviceInfoVO deviceInfoVO);
}
