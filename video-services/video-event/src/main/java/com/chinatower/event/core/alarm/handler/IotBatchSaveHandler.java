package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.core.alarm.util.subscenes.SubScenesWriteHelper;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.mapper.*;
import com.chinatower.event.mongodb.InterceptMongoMapper;
import com.chinatower.event.mongodb.OperateMongoMapper;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.repository.PushThirdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description 物联设备批量处理保存表操作
 * @Author luo.zhenghong
 * @Date 2022/7/19
 * @Version release_v1.5
 **/
@Component
@Slf4j
@Scope("prototype")
public class IotBatchSaveHandler extends AbstractHandler {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;
    @Autowired
    private WarningOrderUtil warningOrderUtil;
    @Autowired
    private WarningOrderMapper warningOrderMapper;
    @Autowired
    private WarningInterceptMapper warningInterceptMapper;
    @Autowired
    private AlarmOperateMapper alarmOperateMapper;
    @Resource
    private WarningSensorMonitorMapper warningSensorMonitorMapper;

    @Autowired
    private PushThirdService pushThirdService;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private SecondRecogConfigMapper secondRecogConfigMapper;

    @Autowired
    private IVideoDeviceService  videoDeviceService;
    /**
     * 执行逻辑
     *
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    @Transactional(timeout = 300)
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("物联设备批量处理保存表操作入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            long beginTime = System.currentTimeMillis();
            List<ReceiveAlarmVO> alarms = receiveAlarmDTO.getList();
            List<ReceiveAlarmVO> operations = receiveAlarmDTO.getOperations();
            List<ReceiveAlarmVO> intercepts = receiveAlarmDTO.getIntercepts();

            //告警事件、运营研判、告警压制下所有物联因子信息集合
            //List<WarningSensorMonitorVO> monitorVOList = new ArrayList<>();
            // 批量保存告警信息
            if (!CollectionUtils.isEmpty(alarms)) {
                //是否有二次识别 默认否
                warningOrderUtil.setSecondConfig(alarms);
                this.getAllSensorMonitorList(alarms);
                // 创建流程
                List<Map> recordList = warningOrderRepository.createRecord(alarms);
                // 回填当前环节相关字段
                warningOrderRepository.setWarningLinkInfo(alarms, recordList);
                //物联设备告警批量上报中不确定是否存在跨月份的数据，这里当做会有跨月份的告警数据上报
                Map<String, List<ReceiveAlarmVO>> alarmsMap = alarms.stream().collect(Collectors.groupingBy(alarmVO -> alarmVO.getWarningOrderId().substring(0, 6)));
                alarmsMap.entrySet().stream().forEach(item -> {
                    ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
                    receiveAlarmVODB.setTableName(TableConstant.WARN_ORDER_TEBLE + item.getKey());
                    receiveAlarmVODB.setList(item.getValue());
                    warningOrderMapper.insertWarningOrders(receiveAlarmVODB);
                });
                //异步推送第三方
                try{
                    pushThirdService.pushThirdOrder(JSON.toJSONString(alarms),alarms);
                }catch (Exception e){
                    log.error("人工上报推送第三方异常:{}",e);
                }
            }
            // 批量保存运营研判数据
            if (!CollectionUtils.isEmpty(operations)) {
                //是否有二次识别 默认否
                warningOrderUtil.setSecondConfig(operations);
                this.getAllSensorMonitorList(operations);
                operations.forEach(SubScenesWriteHelper::fillSubScenes);
                alarmOperateMapper.insertAlarmOperates(operations);
                try{
                    operateMongoMapper.insertOperate(operations,operations.get(0).getAlarmTime());
                }catch (Exception e){
                    log.info("运营研判插入mongo失败:",e);
                }
            }

            //批量保存压制数据
            if (!CollectionUtils.isEmpty(intercepts)) {
                //是否有二次识别 默认否
                warningOrderUtil.setSecondConfig(intercepts);
                this.getAllSensorMonitorList(intercepts);
                warningInterceptMapper.insertInterceptAlarm(intercepts);
                try{
                    interceptMongoMapper.insertIntercept(intercepts,intercepts.get(0).getAlarmTime());
                }catch (Exception e){
                    log.info("回收站插入mongo失败:",e);
                }
            }

            long endTime = System.currentTimeMillis();
            log.info("IotBatchSaveHandler excute cost: {}", (endTime - beginTime));
        }catch(Exception e){
            log.error("物联设备批量处理保存表操作异常：{}", e);
        }

        return this.excuteNextHandler(receiveAlarmDTO);
    }

    /**
     * 获取所有要插入的物联因子/物联设备及联动摄像机
     * @return
     */
    private List<ReceiveAlarmVO> getAllSensorMonitorList(List<ReceiveAlarmVO> alarms) {
        List<String> iotList = alarms.stream().flatMap(v -> v.getIotDeviceExtraFieldVOList().stream().map(i -> i.getIotDeviceCode())).distinct().collect(Collectors.toList());
        TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
        qryCond.setAlarmReporting("1");
        qryCond.setDeviceCodeList(iotList);
        Result<List<TenantIotDeviceConfVO>> result = videoDeviceService.getTenantIotDevConfBatch(qryCond);
        if (result.getCode() == ResultCode.SUCCESS.getCode() && Objects.nonNull(result.getData())) {
            List<TenantIotDeviceConfVO> mapList = result.getData();
            Map<String, List<TenantIotConfRelVO>> deviceCodeToConfRelsMap = mapList.stream()
                    .collect(Collectors.toMap(

                            v-> v.getTenantId()+"_"+v.getIndustryCode()+"_"+v.getDeviceCode(),   // key 是 deviceCode物联设备编号
                            TenantIotDeviceConfVO::getConfRels      // value 是 confRels联动的摄像机信息
                    ));
            for (ReceiveAlarmVO alarmVO : alarms) {
                if (!CollectionUtils.isEmpty(alarmVO.getIotDeviceExtraFieldVOList())) {
                    for (IotDeviceExtraFieldVO iotDeviceExtraFieldVO : alarmVO.getIotDeviceExtraFieldVOList()) {
                        //key  租户id+行业编码+物联设备编码 取出对应关联设备
                        List<TenantIotConfRelVO> confRelVOS = deviceCodeToConfRelsMap.get(alarmVO.getTenantId()+"_" + alarmVO.getIndustryCode()+"_" + iotDeviceExtraFieldVO.getIotDeviceCode());
                        List<IotDeviceExtraFieldVO.RelDeviceInfo> devList = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(confRelVOS)){
                            for (TenantIotConfRelVO relVO : confRelVOS) {
                                IotDeviceExtraFieldVO.RelDeviceInfo deviceInfo = new IotDeviceExtraFieldVO.RelDeviceInfo();
                                deviceInfo.setDeviceCode(relVO.getDeviceCode());
                                deviceInfo.setIsSecondIdentify("0");
                                devList.add(deviceInfo);
                            }
                            iotDeviceExtraFieldVO.setRelDevice(devList);
                        }
                    }
                }

                Map sensorMonitorMap = new HashMap();
                //放入因子信息
                if (!CollectionUtils.isEmpty(alarmVO.getSensorMonitorVOList())) {
                    sensorMonitorMap.put("monitors", alarmVO.getSensorMonitorVOList());
                }
                //放入物联设备信息
                if (!CollectionUtils.isEmpty(alarmVO.getIotDeviceExtraFieldVOList())) {
                    sensorMonitorMap.put("deviceInfo", alarmVO.getIotDeviceExtraFieldVOList());
                }

                alarmVO.setExtraField(JSON.toJSONString(sensorMonitorMap));

            }
        }
        return alarms;
    }


}
