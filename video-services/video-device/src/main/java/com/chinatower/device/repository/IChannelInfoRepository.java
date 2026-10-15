package com.chinatower.device.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.dataobject.ChannelInfoDO;

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
     * @param channelCode
     * @return ChannelInfoDO
     * @author zheng.yan
     * @date 2022/1/24
     */
    ChannelInfoDO getChannelByCode(String channelCode);
}
