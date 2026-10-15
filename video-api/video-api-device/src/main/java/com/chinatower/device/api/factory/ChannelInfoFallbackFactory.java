package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IChannelInfoService;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ChannelInfoFallbackFactory
 * @Deacription 设备通道服务降级处理
 * @Author chenmf
 * @Date 2022/2/11 14:51
 * @Version 1.0
 **/
@Component
public class ChannelInfoFallbackFactory implements FallbackFactory<IChannelInfoService> {
    
    
    @Override
    public IChannelInfoService create(Throwable throwable) {
        
        return new IChannelInfoService() {
    
            @Override
            public Result batchSaveOrUpdateChannel(List<SaveOrUpdateChannelDTO> channelDTOList) {
                return Result.fail("批量保存和更新设备通道信息失败:" + throwable.getMessage());
            }
        };
    }
}
