package com.chinatower.device.controller;


import com.chinatower.common.entity.Result;
import com.chinatower.device.service.IVideoChannelInfoService;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-device
 * @author AutoGen
 * @since 2022-01-17
 */
@RestController
@Api(value = "设备通道信息模块",tags = "设备通道信息模块")
public class ChannelInfoController {
    
    @Autowired
    private IVideoChannelInfoService videoChannelInfoService;
    
    /**
     * 批量保存和更新设备通道信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    @PostMapping("/video/channel/batchSaveOrUpdateChannel")
    public Result<String> batchSaveOrUpdateChannel(@RequestBody List<SaveOrUpdateChannelDTO> channelDTOList) {
        videoChannelInfoService.batchSaveOrUpdateChannel(channelDTOList);
        return Result.success("批量保存和更新设备通道信息成功!");
    }

}
