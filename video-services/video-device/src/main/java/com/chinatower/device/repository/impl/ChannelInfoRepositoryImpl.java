package com.chinatower.device.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.device.dataobject.ChannelInfoDO;
import com.chinatower.device.mapper.ChannelInfoMapper;
import com.chinatower.device.repository.IChannelInfoRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import org.apache.commons.lang3.StringUtils;
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
    public ChannelInfoDO getChannelByCode(String channelCode) {
        ChannelInfoDO channelInfoDO = null;
        if (StringUtils.isBlank(channelCode)) {
            return channelInfoDO;
        }
        QueryWrapper<ChannelInfoDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DeviceNorthIntfConstants.CHANNEL_CODE, channelCode).eq(DeviceNorthIntfConstants.DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
        List<ChannelInfoDO> channelInfoList = this.baseMapper.selectList(queryWrapper);
        if (!channelInfoList.isEmpty()) {
            channelInfoDO = channelInfoList.get(0);
        }
        return channelInfoDO;
    }
}
