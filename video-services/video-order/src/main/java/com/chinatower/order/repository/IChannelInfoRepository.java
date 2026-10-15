package com.chinatower.order.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.order.domain.ChannelInfoDO;

import java.util.List;

/**
 * <p>
 *  设备通道服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
public interface IChannelInfoRepository extends IService<ChannelInfoDO> {
    
    /**
     * 根据通道编码获取通道
     * @param: channelCode
     * @return com.chinatower.order.domain.ChannelInfoDO
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    ChannelInfoDO getChannelByCode(String channelCode);

    List<ChannelInfoDO> getChannelByCodeList(List<String> channelCodeList);
}
