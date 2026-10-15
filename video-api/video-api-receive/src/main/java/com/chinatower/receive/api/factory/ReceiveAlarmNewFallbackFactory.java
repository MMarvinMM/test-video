package com.chinatower.receive.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.ReceiveUavAlarmVO;
import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.receive.model.TranslnglatReq;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/29 15:18
 */

@Component
public class ReceiveAlarmNewFallbackFactory implements FallbackFactory<IReceiveAlarmNewService> {
    @Override
    public IReceiveAlarmNewService create(Throwable throwable) {
        return new IReceiveAlarmNewService() {
            @Override
            public Result receiveIotAlarm(List<ReceiveIotAlarmVO> receiveIotAlarmVOList) {
                return Result.fail("生成告警事件失败:" + throwable.getMessage());
            }

            @Override
            public Result receiveRegionAlarm(List<ReceiveRegionAlarmDto> receiveIotAlarmVOList) {
                return Result.fail("生成卫片告警事件失败:" + throwable.getMessage());
            }

            @Override
            public Result receiveUavAlarm(ReceiveUavAlarmVO receiveUavAlarmVO) {
                return Result.fail("生成无人机告警事件失败:" + throwable.getMessage());
            }

            @Override
            public Result addThirdAlarmOrder(List<ReceiveAlarmVO> receiveAlarmVO) {
                return Result.fail("第三方告警事件失败:" + throwable.getMessage());
            }

            @Override
            public Result addAlarmOrder(ReceiveAlarmVO receiveAlarmVO) {
                return Result.fail("人工上报告警事件失败:" + throwable.getMessage());
            }

            @Override
            public Result getAngleByDeviceParam(TranslnglatReq req) {
                return Result.fail("告警摄像机角度计算异常:" + throwable.getMessage());
            }

            /**
             * 接收告警（AI/摄像机/物联/雷达/卫星/卡口/故障）
             *
             * @return Result
             * @apiNote 接收告警（AI/摄像机/物联/雷达/卫星/卡口/故障）
             * @apiVersion 4.1.36
             * @tag 告警事件能力
             * @requestDemo
             * @responseDemo
             * @param req 接受告警入参
             */
            @Override
            public Result receiveAlarm(ReceiveAlarmVOReq req) {
                return Result.fail("接收告警（AI/摄像机/物联/雷达/卫星/卡口/故障）:" + throwable.getMessage());
            }

            @Override
            public Result receiveSatelliteAlarmKafka(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos) {
                return Result.fail("接收告警卫星告警异常:" + throwable.getMessage());
            }
        };
    }
}
