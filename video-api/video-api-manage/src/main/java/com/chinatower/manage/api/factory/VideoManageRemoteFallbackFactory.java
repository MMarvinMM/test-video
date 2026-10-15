package com.chinatower.manage.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.IVideoManageRemoteService;
import com.chinatower.manage.api.dto.ScreenDataConfigVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName: VideoManageRemoteFallbackFactory
 * @Author: wangyt
 * @CreateTime: 2025-01-13  10:08
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Component
public class VideoManageRemoteFallbackFactory implements FallbackFactory<IVideoManageRemoteService> {
    @Override
    public IVideoManageRemoteService create(Throwable throwable) {
        log.error("kafka消费新增告警类型失败:{}", throwable.getMessage());
        return new IVideoManageRemoteService() {
            @Override
            public Result addConfig(List<ScreenDataConfigVO> screenDataConfigList) {
                return Result.fail("新增大屏配置告警类型失败:" + throwable.getMessage());
            }

            @Override
            public Result editThirdPartyAlarmConfig(List<ScreenDataConfigVO> screenDataConfigList) {
                return Result.fail("第三方告警类型同步大屏配置失败:" + throwable.getMessage());
            }

            @Override
            public Result<ScreenDataConfigVO> selectConfigById(ScreenDataConfigVO screenDataConfig) {
                log.error("第三方告警类型同步大屏配置失败:" + throwable.getMessage());
                return Result.data(new ScreenDataConfigVO());
            }
        };
    }
}
