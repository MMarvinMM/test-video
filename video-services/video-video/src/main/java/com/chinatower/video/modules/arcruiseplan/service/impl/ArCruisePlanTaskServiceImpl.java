package com.chinatower.video.modules.arcruiseplan.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlan;
import com.chinatower.video.modules.arcruiseplan.service.IArCruisePlanService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Component
@Slf4j
public class ArCruisePlanTaskServiceImpl {
    @Autowired
    private IArCruisePlanService arCruisePlanService;

    @XxlJob("arCruisePlanStopTask")
    public ReturnT<String> arCruisePlanStopTask(String param) {
        log.info("标签巡航计划停止定时任务开始===");
        //当前时间
        long time = DateUtil.date().getTime();
        //get redis value
        Set<String> strings = RedisUtil.zRangeByScore("ar:plan:expire:set", -1, time, RedisDBType.PRIMARY);
        //remove redis value
        RedisUtil.zRemoveRangeByScore("ar:plan:expire:set", -1, time, RedisDBType.PRIMARY);
        if(!CollectionUtils.isEmpty(strings)){
            ArCruisePlan arCruisePlan = new ArCruisePlan();
            arCruisePlan.setExecutionStatus(DeviceConstants.ZERO);
            arCruisePlan.setExecutionStep(DeviceConstants.ZERO);
            LambdaUpdateWrapper<ArCruisePlan> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ArCruisePlan::getId, strings)
                    .eq(ArCruisePlan::getExecutionStatus, DeviceConstants.ONE);
            arCruisePlanService.update(arCruisePlan,updateWrapper);
        }
        log.info("标签巡航计划停止定时任务结束===清理条数="+strings.size() + "=耗时=" + (DateUtil.date().getTime() - time));
        return ReturnT.SUCCESS;
    }
}
