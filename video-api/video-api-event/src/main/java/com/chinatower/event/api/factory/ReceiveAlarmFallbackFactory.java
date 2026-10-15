package com.chinatower.event.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.event.api.IReceiveAlarmService;
import com.chinatower.event.entity.dto.IotAlarmConfigDTO;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: ReceiveAlarmFallbackFactory.java
 *
 * @author luo.zhenghong
 * @date 2022/7/25 10:23
 * <p>
 */
@Component
public class ReceiveAlarmFallbackFactory implements FallbackFactory<IReceiveAlarmService> {

    @Override
    public IReceiveAlarmService create(Throwable throwable) {

        return new IReceiveAlarmService() {
            @Override
            public Result receiveIotAlarm(List<ReceiveIotAlarmVO> receiveIotAlarmVOList) {
                return Result.fail("生成告警事件失败:" + throwable.getMessage());
            }

            @Override
            public Result receiveRegionAlarm(List<ReceiveRegionAlarmDto> receiveIotAlarmVOList) {
                return Result.fail("生成卫片告警事件失败:" + throwable.getMessage());
            }

            @Override
            public Result<Object> queryIotAlarmConfigList(IotAlarmConfigDTO model) {
                return Result.fail("分页查询物联网告警配置信息失败:" + throwable.getMessage());
            }
        };
    }
}
