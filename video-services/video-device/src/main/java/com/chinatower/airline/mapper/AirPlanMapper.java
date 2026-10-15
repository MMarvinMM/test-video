package com.chinatower.airline.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.airline.dto.response.AirTaskRes;
import com.chinatower.device.entity.dto.AirTaskReq;
import com.chinatower.airline.dto.response.AirPlanRep;
import com.chinatower.device.entity.dto.AirPlanPageReq;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author lize
 * @Date 2024/11/4 9:59
 */
@Mapper
public interface AirPlanMapper {


    /**
     * 计划管理列表查询
     * @param vo
     * @return
     */
    List<AirPlanRep> findByCondition(AirPlanPageReq vo);

    /**
     * 查询任务
     */
    List<AirTaskReq> airTask(AirPlanPageReq vo);


    /**
     * 计划管理列表查询
     * @param vo
     * @return
     */
    List<AirPlanRep> airPlanList(AirPlanPageReq vo);

    /**
     * 获取计划下最新任务的实际飞行时间
     * @return
     */
    List<AirTaskReq> getRealityTimeList(AirPlanPageReq vo);

    /**
     * 根据planId查询详情
     * @param planId
     * @return
     */
    AirPlanRep findAirPlanById(Integer planId);

    /**
     * 新增计划管理
     * @param airPlan
     * @return
     */
    Integer addAirPlan (AirPlanRep airPlan);

    /**
     * 编辑计划管理
     * @param airPlan
     * @return
     */
    Integer editAirPlan (AirPlanRep airPlan);

    /**
     * 删除计划管理
     * @param airPlan
     * @return
     */
    Integer deleteByPlanId(AirPlanRep airPlan);

    /**
     * 系统根据计划新增的数量自动生成一个名称“新建计划N”，可修改。
     * @param tenantId
     * @param industryCode
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    String getPlanName(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode);

    /**
     *  唯一性校验，重复时输入框高亮显示，同时在输入框下方显示提示“名称已存在！请重新输入。
     * @param tenantId
     * @param industryCode
     * @param planName
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    AirPlanRep planNameIsRepeat(@Param("tenantId") String tenantId,
                                @Param("industryCode") String industryCode,
                                @Param("planName") String planName);

    /**
     * 对“所选航线”与“执行时间”进行校验，同一条航线，每天仅限执行一次飞行任务
     * @param planTimes
     * @param flightRouteId
     * @param tenantId
     * @param industryCode
     * @return
     */
    Integer findFlightRouteIdAndPlanTimeInteger(@Param("planTimes") List<String> planTimes,
                                                @Param("flightRouteId") String flightRouteId,
                                                @Param("tenantId") String tenantId,
                                                @Param("industryCode") String industryCode);

    /**
     * 是否启用按钮 更新状态
     * @param airPlan
     * @return
     */
    Integer updateIsItEnabled(AirPlanRep airPlan);

    /**
     *计划名称自动填充为“复制的计划名称_复制N”
     * @param planName
     * @param tenantId
     * @param industryCode
     * @return
     */
    Integer copyPlanName(@Param("planName") String planName,
                         @Param("tenantId") String tenantId,
                         @Param("industryCode") String industryCode);

    /**
     * 根据planId获取到planName
     * @param planId
     * @return
     */
    String findPlanNameByPlanId(String planId);

    /**
     * 根据id去修改planId
     * @param airPlan 计划参数
     */
    Integer updatePlanIdById (AirPlanRep airPlan);

    AirPlanRep findAirPlanByPlanId (String planId);
}
