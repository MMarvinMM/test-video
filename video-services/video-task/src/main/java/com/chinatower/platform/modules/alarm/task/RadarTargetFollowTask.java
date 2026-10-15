package com.chinatower.platform.modules.alarm.task;


import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.modules.alarm.dao.RadarTargetFollowDAO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * 雷达目标联动定时任务
 */
@Component
@Slf4j
public class RadarTargetFollowTask {

    private static final String LIMIT_TIME = "limit_time";

    @Autowired
    private RadarTargetFollowDAO radarTargetFollowDAO;

    /**
     * 定时任务-释放超时雷达联动
     * @param param
     * @return
     */
    @XxlJob("releaseExpireRadarTargetFollow")
    public ReturnT<String> releaseExpireRadarTargetFollow(String param) {
        try{
            XxlJobLogger.log("定时任务-释放超时雷达联动开始！");
            //先查询设置的阈值
            SysConfig sysConfig = radarTargetFollowDAO.getSysConfig(LIMIT_TIME);
            if (sysConfig == null || sysConfig.getConfigValue() == null) {
                XxlJobLogger.log("定时任务-释放超时雷达联动-超时时间参数没有配置！");
                return ReturnT.FAIL;
            }
            Date nowDate = new Date();
            Date expireCreateTime = new Date(nowDate.getTime() - Long.parseLong(sysConfig.getConfigValue()));
            radarTargetFollowDAO.deleteByExpireCreateTime(expireCreateTime);
            XxlJobLogger.log("定时任务-释放超时雷达联动执行完毕！");
            return ReturnT.SUCCESS;
        } catch (Exception e){
            XxlJobLogger.log("定时任务-释放超时雷达联动异常:",e);
            return ReturnT.FAIL;
        }
    }
}
