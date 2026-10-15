package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import com.chinatower.device.api.factory.ChannelInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "channelInfoService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = ChannelInfoFallbackFactory.class)
public interface IChannelInfoService {

    @PostMapping("/video/channel/batchSaveOrUpdateChannel")
    Result batchSaveOrUpdateChannel(@RequestBody List<SaveOrUpdateChannelDTO> channelDTOList);
}
