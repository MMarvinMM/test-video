package com.chinatower.video.modules.arcruiseplan.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.video.modules.arcruiseplan.entity.ArCruisePlanRel;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * AR巡航计划表 Mapper 接口
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
public interface ArCruisePlanRelMapper extends BaseMapper<ArCruisePlanRel> {

    int countPlanByDeviceCode(@Param("arViewId") String arViewId);
}
