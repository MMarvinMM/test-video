package com.chinatower.airline.mapper;

import com.chinatower.airline.dto.request.AirPlanSafetyOfficer;
import com.chinatower.device.entity.dto.AirPlanSafetyOfficerReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 飞行计划安全员信息 Mapper（vm_air_plan_safety_officer_t）
 */
@Mapper
public interface AirPlanSafetyOfficerMapper {

    /**
     * 新增
     */
    Integer insert(AirPlanSafetyOfficer data);

    /**
     * 批量新增
     */
    Integer batchInsert(@Param("list") List<AirPlanSafetyOfficer> list);

    /**
     * 查询当前登录人历史填写的安全员（最新10条），支持按姓名模糊搜索
     */
    List<AirPlanSafetyOfficerReq> selectLatestByUser(@Param("tenantId") String tenantId,
                                                     @Param("industryCode") String industryCode,
                                                     @Param("createUser") String createUser,
                                                     @Param("safetyOfficer") String safetyOfficer);

    /**
     * 按主键更新（仅更新非空字段）
     */
    Integer updateById(AirPlanSafetyOfficer data);

    /**
     * 按主键查询（仅未删除）
     */
    AirPlanSafetyOfficer selectById(@Param("id") Long id);

    /**
     * 按计划id查询（仅未删除）
     */
    List<AirPlanSafetyOfficer> selectByPlanId(@Param("planId") Long planId);

    /**
     * 按计划id逻辑删除（delete_flag=1）
     */
    Integer deleteByPlanId(@Param("planId") Long planId,
                           @Param("updateUser") String updateUser);

    /**
     * HMS 飞行告警短信：查询无人机当前飞行任务对应计划的安全员手机号（去重）
     */
    List<String> selectSafetyOfficerPhonesByFlyingUav(@Param("uavCode") String uavCode);


    /**
     * HMS 飞行告警短信：根据无人机编码查询无人机名称
     */
    String selectUavNameByUavCode(@Param("uavCode") String uavCode);
}

