package com.chinatower.sync.consumer.schedule;

import com.chinatower.sync.consumer.service.BroadcastDeviceService;
import com.chinatower.sync.consumer.service.SensorStatusService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * AIot设备稽核
 */
@Component
@Slf4j
public class AuditDeviceSchedule {

    @Autowired
    private BroadcastDeviceService broadcastDeviceService;
    @Autowired
    private SensorStatusService sensorStatusService;

    /**
     * 设备基本信息稽核定时任务
     *
     * @param param 定时任务参数
     * @return 返回执行结果
     */
    @XxlJob("auditDeviceInfoJob")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> auditDeviceInfoJob(String param) {
        try{
            log.info("开始执行设备基本信息稽核定时任务");
            // 广播设备
            broadcastDeviceService.auditDeviceInfo(param);
            // 物联设备
            sensorStatusService.auditDeviceInfo(param);
            log.info("设备基本信息稽核定时任务执行完毕");
            return ReturnT.SUCCESS;
        } catch (Exception e){
            log.error("设备基本信息稽核定时任务异常:",e);
            return ReturnT.FAIL;
        }
    }

    /**
     * 设备状态稽核定时任务
     *
     * @param param 定时任务参数
     * @return 返回执行结果
     */
    @XxlJob("auditDeviceStatusJob")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> auditDeviceStatusJob(String param) {
        try{
            log.info("开始执行设备状态稽核定时任务");
            // 广播设备
            broadcastDeviceService.auditDeviceStatusInfo(param);
            // 物联设备
            sensorStatusService.auditDeviceStatusInfo(param);
            log.info("设备状态稽核定时任务执行完毕");
            return ReturnT.SUCCESS;
        } catch (DuplicateKeyException ee) {
            log.warn("auditDeviceStatusJob duplicate msg:{}", ee.getMessage());
            return ReturnT.FAIL;
        }  catch (Exception e){
            log.error("设备状态稽核定时任务异常:",e);
            return ReturnT.FAIL;
        }
    }

}
