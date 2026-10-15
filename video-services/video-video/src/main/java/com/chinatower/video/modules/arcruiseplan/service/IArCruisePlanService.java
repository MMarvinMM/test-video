package com.chinatower.video.modules.arcruiseplan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.video.api.entity.ArCruisePlanReq;
import com.chinatower.video.api.entity.ArPlanDTO;
import com.chinatower.video.api.entity.DoLinkageReq;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlan;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * AR标签信息表 服务类
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
public interface IArCruisePlanService extends IService<ArCruisePlan> {

    Result doLinkage(DoLinkageReq doLinkageReq, Map<String, String> headerMap, boolean b);

    List<ArCruisePlan> ttList(ArCruisePlanVo arCruisePlanVo);

    void doPlanByGateway(ArPlanDTO arPlan, SysUser currentUser);

    List<ArCruisePlan> pageList(ArCruisePlanReq arCruisePlanReq);
}
