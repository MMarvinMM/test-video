package com.chinatower.event.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.event.api.IReceiveAlarmService;
import com.chinatower.event.api.IReceiveApiService;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
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
public class ReceiveApiFallbackFactory implements FallbackFactory<IReceiveApiService> {

    @Override
    public IReceiveApiService create(Throwable throwable) {

        return new IReceiveApiService() {
            @Override
            public Result syncAlarm(ReceiveAlarmVO req) {
                return Result.fail("同步属地运营研判生成告警事件失败:" + throwable.getMessage());
            }

            @Override
            public Result uavSynAnalysisTask(List<SecondRecogSendVO> list) {
                return Result.fail("无人机二次识别下发失败:" + throwable.getMessage());
            }
        };
    }
}
