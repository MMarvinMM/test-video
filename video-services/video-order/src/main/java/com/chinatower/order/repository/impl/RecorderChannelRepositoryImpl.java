package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.order.domain.RecorderDeviceChannelDO;
import com.chinatower.order.mapper.RecorderChannelMapper;
import com.chinatower.order.repository.IRecorderChannelRepository;
import com.chinatower.platform.constants.DeviceConstants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName RecorderChannelRepositoryImpl
 * @Deacription 设备通道服务实现类
 * @Author chenmf
 * @Date 2022/6/13 14:30
 * @Version 1.0
 **/
@Slf4j
@Service
public class RecorderChannelRepositoryImpl extends ServiceImpl<RecorderChannelMapper, RecorderDeviceChannelDO> implements IRecorderChannelRepository {
    
    /**
     * 根据通道编码获取通道
     * @param: channelCode
     * @return com.chinatower.order.domain.ChannelInfoDO
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    @Override
    public RecorderDeviceChannelDO getChannelByCode(String channelCode) {
        if (StringUtils.isBlank(channelCode)) {
            return null;
        }
        RecorderDeviceChannelDO channelInfoDO = null;
        QueryWrapper<RecorderDeviceChannelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_code", channelCode).eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL);
        List<RecorderDeviceChannelDO> channelInfoList = this.baseMapper.selectList(queryWrapper);
        if (!channelInfoList.isEmpty()) {
            channelInfoDO = channelInfoList.get(0);
        }
        return channelInfoDO;
    }

    @Override
    public List<RecorderDeviceChannelDO> getChannelByCodeList(List<String> channelCodeList) {
        if (CollectionUtils.isEmpty(channelCodeList)) {
            return Lists.newArrayList();
        }
        RecorderDeviceChannelDO channelInfoDO = null;
        QueryWrapper<RecorderDeviceChannelDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("channel_code", channelCodeList).eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL);
        return this.baseMapper.selectList(queryWrapper);
    }
}
