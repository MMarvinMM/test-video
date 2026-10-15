package com.chinatower.sync.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.sync.consumer.dao.ChannelInfoMapper;
import com.chinatower.sync.consumer.entity.ChannelInfoDO;
import com.chinatower.sync.consumer.service.IChannelInfoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
@Service
public class ChannelInfoRepositoryImpl extends ServiceImpl<ChannelInfoMapper, ChannelInfoDO> implements IChannelInfoRepository {
    @Override
    public List<ChannelInfoDO> listChannelByCodes(List<String> channelCodes) {
        List<ChannelInfoDO> channelInfoList = new ArrayList<>(0);
        if (channelCodes == null || channelCodes.isEmpty()) {
            return channelInfoList;
        }
        QueryWrapper<ChannelInfoDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(DeviceNorthIntfConstants.CHANNEL_ID, DeviceNorthIntfConstants.CHANNEL_CODE);
        queryWrapper.eq(DeviceNorthIntfConstants.DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL).in(DeviceNorthIntfConstants.CHANNEL_CODE, channelCodes);
        channelInfoList = this.baseMapper.selectList(queryWrapper);
        return channelInfoList;
    }
}
