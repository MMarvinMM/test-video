package com.chinatower.sync.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.sync.consumer.entity.ChannelInfoDO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
public interface IChannelInfoRepository extends IService<ChannelInfoDO> {

    /**
     * 根据通道编码获取通道
     * @param channelCodes
     * @return ChannelInfoDO
     * @author zheng.yan
     * @date 2022/2/11
     */
    List<ChannelInfoDO> listChannelByCodes(List<String> channelCodes);
}
