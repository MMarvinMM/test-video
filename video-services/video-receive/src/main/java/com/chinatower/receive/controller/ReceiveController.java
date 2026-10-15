package com.chinatower.receive.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.dto.ReceiveUavAlarmVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.outdata.api.SendKafkaAPI;
import com.chinatower.platform.constants.Constants;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.core.intercept.AreaShieldingSuppressionStrategy;
import com.chinatower.receive.model.AiJudgeCallbackReq;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.receive.model.TranslnglatReq;
import com.chinatower.receive.service.AiJudgeService;
import com.chinatower.receive.service.ISuppressStrategyService;
import com.chinatower.receive.service.ReceiveService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-receive
 *
 * @Author: songxinfeng
 * @Date: 2024/6/27 10:31
 */
@Slf4j
@RestController
@RequestMapping(value = "/alarm/receive")
public class ReceiveController {

    @Autowired
    private ReceiveService receiveService;
    @Autowired
    private SendKafkaAPI sendKafkaAPI;
    @Autowired
    private AiJudgeService aiJudgeService;


    /**
     * 人工上报
     *
     * @param receiveAlarmVO
     * @return
     */
    @PostMapping("/addAlarmOrder")
    public Result addAlarmOrder(@RequestBody ReceiveAlarmVO receiveAlarmVO) {
        return receiveService.addAlarmOrder(receiveAlarmVO);
    }


    /**
     * 人工上报-行业调用
     *
     * @param receiveAlarmVO
     * @return
     */
    @PostMapping("/orderForTrade/addAlarmOrder")
    public Result forTeadeAddAlarmOrder(@RequestBody ReceiveAlarmVO receiveAlarmVO) {
        log.info("行业人工上报接收入参:{}", JSON.toJSONString(receiveAlarmVO));
        return receiveService.addAlarmOrder(receiveAlarmVO);
    }

    /**
     * 第三方告警上报
     *
     * @param receiveAlarmVO
     * @return
     */
    @PostMapping("/addThirdAlarmOrder")
    public Result addThirdAlarmOrder(@RequestBody List<ReceiveAlarmVO> receiveAlarmVO) {
        log.info("addThirdAlarmOrder第三方告警接收入参:{}", JSON.toJSONString(receiveAlarmVO));
        return receiveService.addThirdAlarmOrder(receiveAlarmVO);
    }

    /**
     * 接收卫星告警
     *
     * @param receiveSatelliteAlarmVos
     * @return
     */
    @PostMapping("/receiveSatelliteAlarm")
    public Result receiveSatelliteAlarm(@RequestBody List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos) {
        if (CollectionUtils.isEmpty(receiveSatelliteAlarmVos)) {
            return Result.success(MessageConstants.ALARM_NO_PARAMS);
        }
        Result result = null;
        if (receiveService.isCenterNode()) {
            // 中心节点处理卫星告警数据
            result = receiveService.receiveSatelliteAlarm(receiveSatelliteAlarmVos);
        }
        // kafka推送
        sendKafkaAPI.satelliteAlarm(JSON.parseArray(JSON.toJSONString(receiveSatelliteAlarmVos)));
        return result;
    }

    /**
     * 接收outdata kafka消息
     *
     * @param receiveSatelliteAlarmVos
     * @return
     */
    @PostMapping("/receiveSatelliteAlarmKafka")
    public Result receiveSatelliteAlarmKafka(@RequestBody List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos) {
        if (CollectionUtils.isEmpty(receiveSatelliteAlarmVos)) {
            return Result.success(MessageConstants.ALARM_NO_PARAMS);
        }
        Result result = null;
        if (receiveService.isCenterNode()) {
            receiveService.sendLocalSatelliteAlarm(receiveSatelliteAlarmVos);
            result = Result.success("推送属地成功");
        } else {
            result = receiveService.receiveSatelliteAlarm(receiveSatelliteAlarmVos);
        }
        return result;
    }

    /**
     * 接收告警（AI/摄像机/物联/雷达/卫星/卡口/故障）
     *
     * @param receiveAlarmVOReq
     * @return
     */
    @PostMapping("/receiveAlarm")
    public Result receiveAlarm(@RequestBody ReceiveAlarmVOReq receiveAlarmVOReq) {
        return receiveService.receiveAlarm(receiveAlarmVOReq);
    }

    @Autowired
    private AreaShieldingSuppressionStrategy areaShieldingSuppressionStrategy;
    @Autowired
    private ISuppressStrategyService suppressStrategyService;

    @PostMapping("receiveAlarmTest")
    public void testDoStrategyIntercept() {
        /*suppressStrategyService.doStrategyIntercept(null, null);
        ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();
        receiveAlarmVO.setAlarmCode("0719sxj");
        receiveAlarmVO.setDeviceCode("51150200831327000261");
        //SysLntlagConfigVO config = deviceMapper.getLntlagConfig(receiveAlarmVO.getDeviceCode());

        VmAlarmSuppressStrategyVO suppressStrategyVO = new VmAlarmSuppressStrategyVO();
        suppressStrategyVO.setAlarmInterval(180);
        suppressStrategyVO.setAlarmIntervalType("s");
        suppressStrategyVO.setAlarmRangeP("10");
        suppressStrategyVO.setAlarmRangeT("10");
        suppressStrategyVO.setAlarmRangeStatus("0");
        suppressStrategyVO.setAlarmSuppressName("海康3公里重复告警时间间隔pt10");
        suppressStrategyVO.setAlarmSuppressType("1");
        suppressStrategyVO.setDeleteFlag(0);
        suppressStrategyVO.setId(671);
        suppressStrategyVO.setIndustryCode("000000");
        suppressStrategyVO.setPeriodTime("00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,16,17,18,19,20,21,22,23,15");
        suppressStrategyVO.setStatus("0");
        suppressStrategyVO.setTenantId("120046990");
        suppressionStrategy.apply(receiveAlarmVO, suppressStrategyVO);
        areaShieldingSuppressionStrategy.apply(receiveAlarmVO, suppressStrategyVO);*/
    }

    /**
     * 接收无人机告警
     *
     * @param uavAlarmVO
     * @return
     */
    @PostMapping("/receiveUavAlarm")
    public Result receiveUavAlarm(@RequestBody ReceiveUavAlarmVO uavAlarmVO) {
        log.info("====== 接收无人机告警 传入参数：{} ======", uavAlarmVO);
        ReceiveAlarmVOReq alarmVOReq = new ReceiveAlarmVOReq();
        ReceiveAlarmVO alarmVO = new ReceiveAlarmVO();
        alarmVO.setAlarmSource(WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue());
        alarmVO.setAlarmType(uavAlarmVO.getAlarmType());
        alarmVOReq.setReceiveUavAlarmVO(uavAlarmVO);
        alarmVOReq.setData(alarmVO);
        return receiveService.receiveAlarm(alarmVOReq);
    }

    /**
     * 接收卫片告警
     *
     * @param reqList
     * @return
     */
    @PostMapping("/receiveRegionAlarm")
    public Result receiveRegionAlarm(@RequestBody List<ReceiveRegionAlarmDto> reqList) {
        return receiveService.receiveRegionAlarm(reqList);
    }

    /**
     * 接收物联设备告警
     *
     * @param receiveIotAlarmVOList
     * @return
     */
    @PostMapping("/receiveIotAlarm")
    public Result receiveIotAlarm(@RequestBody List<ReceiveIotAlarmVO> receiveIotAlarmVOList) {
        log.info("====== 接收物联设备告警 传入参数：{} ======", receiveIotAlarmVOList);
        ReceiveAlarmVOReq alarmVOReq = new ReceiveAlarmVOReq();
        ReceiveAlarmVO alarmVO = new ReceiveAlarmVO();
        //走适配流程写死物联设备告警
        alarmVO.setAlarmSource(WarningSourceEnum.IOT_ALARM.getTypeValue());
        alarmVO.setAlarmType("-1");
        alarmVOReq.setReceiveIotAlarmVOList(receiveIotAlarmVOList);
        alarmVOReq.setData(alarmVO);
        return receiveService.receiveAlarm(alarmVOReq);
    }

    /**
     * 获取水平角、俯仰角
     *
     * @param req
     * @return
     */
    @PostMapping("/getAngleByDeviceParam")
    public Result getAngleByDeviceParam(@RequestBody TranslnglatReq req) throws Exception {
        log.info("getAngleByDeviceParam:{}", JSON.toJSONString(req));
        return Result.data(receiveService.getAngleByDeviceParam(req));
    }

    /**
     * 告警分级 AI 研判回调：外部系统研判完成后回填风险等级等字段
     *
     * @param req 回调入参
     * @return 处理结果
     */
    @PostMapping("/aiJudgeCallback")
    public Result aiJudgeCallback(@RequestBody AiJudgeCallbackReq req) {
        return aiJudgeService.callback(req);
    }

    /**
     * 自动告警推送
     * @param param
     * @return
     */
    @PostMapping("/task/alarm/push")
    public String sinkingNodeAlarmHandler(@RequestBody String param) {
        RedisUtil.set(Constants.ALARM_PUSH_TASK_KEY, param, RedisDBType.PRIMARY);
        return RedisUtil.get(Constants.ALARM_PUSH_TASK_KEY, RedisDBType.PRIMARY);
    }


}
