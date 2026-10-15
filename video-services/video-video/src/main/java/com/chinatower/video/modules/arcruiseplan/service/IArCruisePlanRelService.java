package com.chinatower.video.modules.arcruiseplan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.video.api.entity.ArCruisePlanReq;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlan;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanRel;

import java.util.List;

/**
 * <p>
 * AR巡航计划表 服务类
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
public interface IArCruisePlanRelService extends IService<ArCruisePlanRel> {

    int countPlanByDeviceCode(String arViewId);

    String doPlan(ArCruisePlanReq arCruisePlanReq);

    ArCruisePlan findTagAndOrderNum(ArCruisePlanReq arCruisePlanReq);

    int findFirstPlan(ArCruisePlanReq arCruisePlanReq);

    List<ArCruisePlan> findTags(ArCruisePlanReq arCruisePlanReq);
}
