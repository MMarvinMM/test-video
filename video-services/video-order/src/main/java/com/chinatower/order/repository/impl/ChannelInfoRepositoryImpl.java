package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.order.domain.ChannelInfoDO;
import com.chinatower.order.mapper.ChannelInfoMapper;
import com.chinatower.order.repository.IChannelInfoRepository;
import com.chinatower.platform.constants.DeviceConstants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ChannelInfoRepositoryImpl
 * @Deacription 设备通道服务实现类
 * @Author chenmf
 * @Date 2022/6/13 14:30
 * @Version 1.0
 **/
@Slf4j
@Service
public class ChannelInfoRepositoryImpl extends ServiceImpl<ChannelInfoMapper, ChannelInfoDO> implements IChannelInfoRepository {
    
    /**
     * 根据通道编码获取通道
     * @param: channelCode
     * @return com.chinatower.order.domain.ChannelInfoDO
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    @Override
    public ChannelInfoDO getChannelByCode(String channelCode) {
        ChannelInfoDO channelInfoDO = null;
        if (StringUtils.isBlank(channelCode)) {
            return channelInfoDO;
        }
        QueryWrapper<ChannelInfoDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_code", channelCode).eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL);
        List<ChannelInfoDO> channelInfoList = this.baseMapper.selectList(queryWrapper);
        if (!channelInfoList.isEmpty()) {
            channelInfoDO = channelInfoList.get(0);
        }
        return channelInfoDO;
    }

    @Override
    public List<ChannelInfoDO> getChannelByCodeList(List<String> channelCodeList) {
        List<ChannelInfoDO> resultList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(channelCodeList)) {
            return resultList;
        }
        QueryWrapper<ChannelInfoDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("channel_code", channelCodeList).eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL);
        return this.baseMapper.selectList(queryWrapper);
    }
}
