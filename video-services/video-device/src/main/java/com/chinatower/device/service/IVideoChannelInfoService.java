package com.chinatower.device.service;

import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;

import java.util.List;

public interface IVideoChannelInfoService {
    
    /**
     * 批量保存和更新设备通道信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    void batchSaveOrUpdateChannel(List<SaveOrUpdateChannelDTO> channelDTOList);
}
