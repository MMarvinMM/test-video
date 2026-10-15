package com.chinatower.device.service.impl;

import com.chinatower.device.service.IVideoChannelInfoService;
import org.springframework.beans.BeanUtils;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import com.chinatower.device.dataobject.ChannelInfoDO;
import com.chinatower.device.repository.IChannelInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName ChannelInfoService
 * @Deacription
 * @Author chenmf
 * @Date 2022/2/11 14:58
 * @Version 1.0
 **/

@Service
@Slf4j
public class VideoChannelInfoService implements IVideoChannelInfoService {
    
    @Autowired
    private IChannelInfoRepository channelInfoRepository;
    
    /**
     * 批量保存和更新设备通道信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    @Override
    public void batchSaveOrUpdateChannel(List<SaveOrUpdateChannelDTO> channelDTOList) {
    
        if (!CollectionUtils.isEmpty(channelDTOList)) {
            //更新设备通道列表
            List<ChannelInfoDO> updateDeviceDTOList = new ArrayList<>();
            //新增设备通道列表
            List<ChannelInfoDO> addDeviceDTOList = new ArrayList<>();
    
            Iterator<SaveOrUpdateChannelDTO> it = channelDTOList.iterator();
            while (it.hasNext()) {
                SaveOrUpdateChannelDTO channelDTO = it.next();
    
                ChannelInfoDO channelInfoDO = channelInfoRepository.getChannelByCode(channelDTO.getChannelCode());
                if (null != channelInfoDO) {
                    BeanUtils.copyProperties(channelDTO, channelInfoDO);
                    channelInfoDO.setModifierDate(new Date());
                    updateDeviceDTOList.add(channelInfoDO);
                }
                else
                {
                    channelInfoDO = new ChannelInfoDO();
                    BeanUtils.copyProperties(channelDTO, channelInfoDO);
                    channelInfoDO.setCreatorDate(new Date());
                    channelInfoDO.setModifierDate(new Date());
                    channelInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                    addDeviceDTOList.add(channelInfoDO);
                }
                
            }
            channelInfoRepository.saveBatch(addDeviceDTOList);
            channelInfoRepository.updateBatchById(updateDeviceDTOList);
        }
    }
}
