package com.chinatower.order.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.order.domain.RecorderDeviceChannelDO;

import java.util.List;

/**
 * <p>
 *  设备通道服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
public interface IRecorderChannelRepository extends IService<RecorderDeviceChannelDO> {

    /**
     * 根据通道编码获取通道信息
     * @param channelCode 通道编码
     * @return 通道信息
     */
    RecorderDeviceChannelDO getChannelByCode(String channelCode);

    List<RecorderDeviceChannelDO> getChannelByCodeList(List<String> channelCodeList);
}
