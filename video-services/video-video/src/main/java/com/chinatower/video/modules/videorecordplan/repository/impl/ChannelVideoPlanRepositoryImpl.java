package com.chinatower.video.modules.videorecordplan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videorecordplan.dataobject.ChannelVideoPlanDO;
import com.chinatower.video.modules.videorecordplan.mapper.ChannelVideoPlanMapper;
import com.chinatower.video.modules.videorecordplan.repository.IChannelVideoPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 通道录像计划表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Service
public class ChannelVideoPlanRepositoryImpl extends ServiceImpl<ChannelVideoPlanMapper, ChannelVideoPlanDO> implements IChannelVideoPlanRepository {

     private static final String TENANT_ID = "tenant_id";

     private static final String DELETE_FLAG = "delete_flag";
     private static final String INDUSTRY_CODE = "industry_code";
     private static final String DEVICE_CODE = "device_code";


    @Autowired
    private ChannelVideoPlanMapper channelVideoPlanMapper;
    

    /**
     * 查询订单通道录像计划
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    @Override
    public List<ChannelVideoPlanDO> queryOrderRecordPlanInfo(QueryOrderDeviceSetDTO queryOrderDeviceSetDTO) {
    
        QueryWrapper<ChannelVideoPlanDO> queryWrapper = new QueryWrapper<>();
    
        queryWrapper.eq("order_code", queryOrderDeviceSetDTO.getOrderCode());
        if (!StringUtils.isEmpty(queryOrderDeviceSetDTO.getIndustryCode())) {
            queryWrapper.eq(INDUSTRY_CODE, queryOrderDeviceSetDTO.getIndustryCode());
        }
        queryWrapper.eq(TENANT_ID, queryOrderDeviceSetDTO.getTenantId());
        queryWrapper.eq(DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL_INT);
        List<String> deviceCodeList = queryOrderDeviceSetDTO.getDeviceCodeList();
        if (!CollectionUtils.isEmpty(deviceCodeList)) {
            queryWrapper.in(DEVICE_CODE, deviceCodeList);
        }
        
        return channelVideoPlanMapper.selectList(queryWrapper);
    }
    
    /**
     * 查询录像计划列表
     *
     * @return
     * @author chenmf
     * @date 2022/4/14
     * @version
     */
    @Override
    public List<ChannelVideoPlanDO> queryVideoPlanInfo(ChannelVideoPlanVO tempChannelVideoPlanVO) {
        QueryWrapper<ChannelVideoPlanDO> queryWrapper = new QueryWrapper<>();
    
        queryWrapper.eq("channel_code", tempChannelVideoPlanVO.getChannelCode());
        queryWrapper.eq(TENANT_ID, tempChannelVideoPlanVO.getTenantId());
        queryWrapper.eq(DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL_INT);
        queryWrapper.eq(DEVICE_CODE, tempChannelVideoPlanVO.getDeviceCode());
        queryWrapper.eq(INDUSTRY_CODE, tempChannelVideoPlanVO.getIndustryCode());
        return channelVideoPlanMapper.selectList(queryWrapper);
    }
    
}
