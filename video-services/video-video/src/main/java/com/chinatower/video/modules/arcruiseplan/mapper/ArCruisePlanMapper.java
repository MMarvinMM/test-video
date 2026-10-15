package com.chinatower.video.modules.arcruiseplan.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.video.api.entity.ArCruisePlanReq;
import com.chinatower.video.api.entity.ArPlanDTO;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlan;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanRel;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanVo;
import com.chinatower.video.modules.artags.dataobject.ArTagsInfo;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * AR标签信息表 Mapper 接口
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
public interface ArCruisePlanMapper extends BaseMapper<ArCruisePlan> {

    List<ArCruisePlan> ttList(@Param("param") ArCruisePlanVo arCruisePlanVo);

    ArPlanDTO getArCruisePlanByArViewId(ArCruisePlanReq arCruisePlanReq);

    ArCruisePlan findTagAndOrderNum(ArCruisePlanReq arCruisePlanReq);

    int findFirstPlan(ArCruisePlanReq arCruisePlanReq);

    List<ArCruisePlan> findTags(ArCruisePlanReq arCruisePlanReq);

    List<ArTagsInfo> findTagsByArViewId(ArCruisePlanReq arCruisePlanReq);

    List<ArCruisePlan> pageList(@Param("param") ArCruisePlanReq arCruisePlanReq);

    List<ArCruisePlanRel> findTagsByCurId(long cruisePlanId);
}
