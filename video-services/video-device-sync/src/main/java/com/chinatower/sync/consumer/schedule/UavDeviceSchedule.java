package com.chinatower.sync.consumer.schedule;

import com.chinatower.sync.consumer.service.UavDeviceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class UavDeviceSchedule {

    @Autowired
    private UavDeviceService uavDeviceService;

    @XxlJob("syncAllUavCameraModel")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> syncAllUavCameraModel(String param) {
        try{
            log.info("开始执行全量同步相机型号定时任务！");
            uavDeviceService.syncAllUavCameraModel();
            log.info("定时任务全量同步相机型号执行完毕！");
            return ReturnT.SUCCESS;
        } catch (Exception e){
            log.error("定时任务全量同步相机型号异常:",e);
            return ReturnT.FAIL;
        }
    }

    @XxlJob("syncAllUavMountModel")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> syncAllUavMountModel(String param) {
        try{
            log.info("开始执行全量同步挂载型号定时任务！");
            uavDeviceService.syncAllUavMountModel();
            log.info("定时任务全量同步挂载型号执行完毕！");
            return ReturnT.SUCCESS;
        } catch (Exception e){
            log.error("定时任务全量同步挂载型号异常:",e);
            return ReturnT.FAIL;
        }
    }

    /**
     * 全量获取无人机型号列表
     *
     * @param param 定时任务参数
     * @return 操作结果
     */
    @XxlJob("syncAllUavModel")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> syncAllUavModel(String param) {
        try{
            log.info("开始全量获取无人机型号列表执行定时任务！");
            uavDeviceService.syncAllUavModel(param);
            log.info("全量获取无人机型号列表定时任务执行完毕！");
            return ReturnT.SUCCESS;
        } catch (Exception e){
            log.error("全量获取无人机型号列表定时任务异常:",e);
            return ReturnT.FAIL;
        }
    }

    /**
     * 全量获取无人机机巢型号列表
     *
     * @param param 定时任务参数
     * @return 操作结果
     */
    @XxlJob("syncAllUavNestModel")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> syncAllUavNestModel(String param) {
        try{
            log.info("开始全量获取无人机机巢型号列表执行定时任务！");
            uavDeviceService.syncAllUavNestModel(param);
            log.info("全量获取无人机机巢型号列表定时任务执行完毕！");
            return ReturnT.SUCCESS;
        } catch (Exception e){
            log.error("全量获取无人机机巢型号列表定时任务异常:",e);
            return ReturnT.FAIL;
        }
    }
}
