package com.chinatower.event.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.model.AlarmByTaskReq;
import com.chinatower.event.api.model.IntelligentJudgeTaskRequest;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dto.ReceiveAlarmVOReq;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.repository.IReceiveRepository;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.service.IAlarmService;
import com.chinatower.event.vo.ReceiveSatelliteAlarmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.chinatower.controller.TowerBaseController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * video-event
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: receiveController.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 16:06
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/receive")
public class ReceiveController extends TowerBaseController {

    @Autowired
    private IReceiveRepository receiveRepository;

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private IAlarmService alarmService;

    @Autowired
    private ISysService sysService;


    /**
     * 功能描述：物联告警事件图片更新
     * @param: data
     * @return: Result<Map>
     * @author: chenmf
     * @date: 2022/05/09
     * @version:
     */
    @PostMapping(value = "/upload/warnOrderImg",consumes = "application/json")
    public Result<String> presetImg(@RequestBody JSONObject data) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String param = request.getParameter(Constants.PARAM_WARNING_ORDER_ID);
        String[] split = param.split(Constants.SPLIT_SIGN);
        String warningOrderId = split[0];
        String tableType = split[1];
        String tableName = "";
        if(Constants.ONE.equals(tableType)){
            tableName = TableConstant.WARN_ALARM_INTERCEPT;
        }else if(Constants.TWO.equals(tableType)){
            tableName = TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6);
        }else if(Constants.THREE.equals(tableType)){
            tableName = TableConstant.WARN_ALARM_OPERATE;
        }
        log.info("物联告警事件上报联动抓图接口对应接收告警编码：{}",warningOrderId);
        warningOrderRepository.updateWarningOrderImg(warningOrderId, data,tableType,tableName);
        return Result.success("物联告警事件图片更新成功!");
    }


    /**
     * 功能描述：卫星告警事件图片更新
     * @param: data
     * @return: Result<Map>
     * @author: fanzw
     * @version:
     */
    @PostMapping(value = "/upload/warnOrderSatelliteImg",consumes = "application/json")
    public Result<String> presetSatelliteImg(@RequestBody JSONObject data) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String param = request.getParameter(Constants.PARAM);
        log.info("卫星告警事件上报联动抓图接口入参：{}",param);
        if(param.contains(Constants.SPLIT_SIGN)){
            String[] split = param.split(Constants.SPLIT_SIGN);
            String tableType = split[0];
            String[] orderIds = split[1].split(Constants.COMMA);
            String selectAlgorithm = "";
            //卫片告警多拼接选择算法编码
            if (split.length>=3){
                selectAlgorithm = split[2];
            }
            String tableName = "";
            if(Constants.ONE.equals(tableType)){
                tableName = TableConstant.WARN_ALARM_INTERCEPT;
            }else if(Constants.TWO.equals(tableType)){
                tableName = TableConstant.WARN_ORDER_TEBLE + orderIds[0].substring(0, 6);
            }else if(Constants.THREE.equals(tableType)){
                tableName = TableConstant.WARN_ALARM_OPERATE;
            }
            warningOrderRepository.updateSatelliteWarningImg(orderIds, tableName,data,tableType,selectAlgorithm);
            return Result.success("卫星告警事件图片更新成功!");
        }
        return Result.fail("参数异常!");
    }


    /**
     * 功能描述：二次识别回调
     * @param: data
     * @return: Result<Map>
     * @author: cuisj
     * @date: 2024/01/15
     * @version:
     */
    @PostMapping(value = "/algorithms/synAnalysisTaskResult",consumes = "application/json")
    public Result<String> secondRecogBack(@RequestBody List<SecondRecogBackVO> list) {
        log.info("二次识别回调接口入参：{}", JSON.toJSONString(list));
        warningOrderRepository.updateSecondRecogResult(list);
        return Result.success("二次识别回调成功");
    }

    /**
     * 功能描述：二次识别回调(遥感算法)
     * @param: data
     * @return: Result<Map>
     * @author: cuisj
     * @date: 2024/01/15
     * @version:
     */
    @PostMapping(value = "/v2/algorithms/synAnalysisTaskResult",consumes = "application/json")
    public Result<String> secondRecogBackV2(@RequestBody List<SecondRecogBackVO> list) {
        log.info("二次识别回调（遥感算法）接口入参：{}", JSON.toJSONString(list));
        warningOrderRepository.updateSecondRecogResultV2(list);
        return Result.success("二次识别回调v2成功");
    }


    /**
     * 功能描述：二次识别回调
     * @param: data
     * @return: Result<Map>
     * @author: cuisj
     * @date: 2024/01/15
     * @version:
     */
    @PostMapping(value = "/algorithms/synAnalysisTaskResultCopy",consumes = "application/json")
    public Result<String> secondRecogBack(@RequestBody SecondRecogBackParentVO secondRecogBackParentVO) {
        List<SecondRecogBackVO> list = secondRecogBackParentVO.getData();
        log.info("二次识别回调接口入参：{}", JSON.toJSONString(list));
        warningOrderRepository.updateSecondRecogResult(list);
        return Result.success("二次识别回调成功");
    }



    /**
     * 功能描述：二次识别任务告警批量查询
     * @param: data
     * @return: Result<List<SecondRecogBackVO>>
     * @author: cuisj
     * @date: 2024/01/15
     * @version:
     */
    @PostMapping("/service/alarmByTaskBatchIdSelf")
    public Result<List<SecondRecogBackVO>> alarmByTaskBatchIdSelf(@RequestBody AlarmByTaskReq alarmByTaskReq) {
        log.info("二次识别任务告警批量查询入参：{}", alarmByTaskReq);
        return Result.data(warningOrderRepository.alarmByTaskBatchId(alarmByTaskReq.getTaskBatchId(), alarmByTaskReq.getPlatFormCode(), alarmByTaskReq.getToPlatformCode(), alarmByTaskReq.getType()));
    }


    /**
     * 功能描述：二次识别任务告警批量查询
     * @param: data
     * @return: Result<List<SecondRecogBackVO>>
     * @author: cuisj
     * @date: 2024/01/15
     * @version:
     */
    @PostMapping("/service/alarmByTaskBatchId")
    public Result<List<SecondRecogBackVO>> alarmByTaskBatchId(@RequestBody String taskBatchId) {
        log.info("二次识别任务告警批量查询入参：{}", taskBatchId);
        return Result.data(warningOrderRepository.alarmByTaskBatchId(taskBatchId, "", "", "1"));
    }


    /**
     * 功能描述：二次识别下发
     * @param: data
     * @return: Result<Map>
     * @author: cuisj
     * @date: 2024/01/15
     * @version:
     */
    @PostMapping(value = "/algorithms/synAnalysisTask",consumes = "application/json")
    public Result<String> synAnalysisTask(@RequestBody List<SecondRecogSendVO> list) {
        log.info("二次识别下发入参：{}", JSON.toJSONString(list));
        return Result.data(warningOrderRepository.synAnalysisTask(list));
    }


    /**
     * 功能描述：无人机二次识别下发
     * @param: data
     * @return: Result<Map>
     * @author: cuisj
     * @date: 2024/01/15
     * @version:
     */
    @PostMapping("/algorithms/uavSynAnalysisTask")
    public Result uavSynAnalysisTask(@RequestBody List<SecondRecogSendVO> list) {
        log.info("无人机二次识别下发入参：{}", JSON.toJSONString(list));
        String taskBatchId = warningOrderRepository.uavSynAnalysisTask(list);
        return Result.data(200,taskBatchId,"二次识别下发成功");
    }



    /**
     * 功能描述：公共二次识别下发
     * @param: data
     * @return: Result<Map>
     * @author: cuisj
     * @date: 2024/01/15
     * @version:
     */
    @PostMapping("/algorithms/publicSynAnalysisTask")
    public Result publicSynAnalysisTask(@RequestBody List<SecondRecogSendVO> list) {
        log.info("公共二次识别下发：{}", JSON.toJSONString(list));
        String taskBatchId = warningOrderRepository.publicSynAnalysisTask(list);
        return Result.success("公共二次识别下发"+taskBatchId);
    }




    /**
     * 功能描述：卫星火情告警
     * @param: data
     * @return: Result<Map>
     * @author: guyuefei
     * @date: 2022/05/12
     * @version:
     */
    //@PostMapping("/receiveSatelliteAlarm")
    public Result receiveSatelliteAlarm(@RequestBody List<ReceiveSatelliteAlarmVo> receiveSatelliteAlarmVos) {
       return receiveRepository.receiveSatelliteAlarm(receiveSatelliteAlarmVos);
    }

    /**
     * 接收告警
     * @param receiveAlarmVOReq
     * @return
     */
    //@PostMapping("/receiveAlarm")
    public Result receiveAlarm(@RequestBody ReceiveAlarmVOReq receiveAlarmVOReq) {
        return alarmService.receiveAlarm(receiveAlarmVOReq);
    }

    /**
     * 接收卫片告警
     * @param reqList
     * @return
     */
    //@PostMapping("/receiveRegionAlarm")
    public Result receiveRegionAlarm(@RequestBody List<ReceiveRegionAlarmDto> reqList) {
        return alarmService.receiveRegionAlarm(reqList);
    }

    /**
     * 功能描述：同步运营研判告警
     * @param req
     * @return
     */
    @PostMapping("/syncAlarm")
    public Result syncAlarm(@RequestBody ReceiveAlarmVO req) {
        return alarmService.syncAlarm(req);
    }

    /**
     * 接收物联设备告警
     * @param receiveIotAlarmVOList
     * @return
     */
    //@PostMapping("/receiveIotAlarm")
    public Result receiveIotAlarm(@RequestBody List<ReceiveIotAlarmVO> receiveIotAlarmVOList) {
        log.info("====== 接收物联设备告警 传入参数：{} ======",receiveIotAlarmVOList);
        ReceiveAlarmVOReq alarmVOReq = new ReceiveAlarmVOReq();
        ReceiveAlarmVO alarmVO = new ReceiveAlarmVO();
        //走适配流程写死物联设备告警
        alarmVO.setAlarmSource(WarningSourceEnum.IOT_ALARM.getTypeValue());
        alarmVO.setAlarmType("-1");
        alarmVOReq.setReceiveIotAlarmVOList(receiveIotAlarmVOList);
        alarmVOReq.setData(alarmVO);
        return alarmService.receiveAlarm(alarmVOReq);
    }

    /**
     * 功能描述：鉴权第三方系统接口
     * @param data
     * @author: zhang.xionghui
     * @date: 2022/10/08
     * @return
     */
    @PostMapping("/oauthToken")
    public Result oauthToken(@RequestBody Map data){
        return receiveRepository.oauthToken(data);
    }

    /**
     * 功能描述：查询登录人执法编号信息
     *
     * @author: zhang.xionghui
     * @return:
     * @date: 2022/10/9 10:02
     */
    @GetMapping("/getLoginUserEnforcementInfo")
    public Result getLoginUserEnforcementInfo(){
        return Result.data(sysService.getLoginUserEnforcementInfo());
    }

    /**
     * 功能描述：
     * @return Result<JSONObject>
     */
    @PostMapping("/algorithms/synIntelligentJudgeTask")
    public Result<JSONObject> synIntelligentJudgeTask(@RequestBody IntelligentJudgeTaskRequest taskRequest) {
        log.info("智能研判任务识别接收入参:{}", JSON.toJSONString(taskRequest));
        return Result.data(warningOrderRepository.synIntelligentJudgeTask(taskRequest));
    }

}
