package com.chinatower.emergency.mapper;


import com.chinatower.emergency.dto.*;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DigitalMapper {

    void save(Digital digital);

    void savePlan(DigitalPlan digitalPlan);

    void saveStageBatch(List<DigitalStage> list);

    void saveTaskBatch(List<DigitalTask> list);

    void deleteStageByDigitalId(String digitalId);

    void deleteTaskByDigitalId(String digitalId);

    void update(Digital digital);

    void updatePlan(DigitalPlan digitalPlan);

    Digital findById(Digital digital);

    DigitalPlan findPlanById(Digital digital);

    List<Digital> list(DigitalQueryVO digital);

    void deleteInfo(@Param("ids") List<String> ids, @Param("operator") String operator);

    void deletePlan(@Param("ids") List<String> ids, @Param("operator") String operator);

    void deleteDept(@Param("ids") List<String> ids, @Param("operator") String operator);

    void deleteDeptUser(@Param("ids") List<String> ids, @Param("operator") String operator);

    void deleteStage(@Param("ids") List<String> ids, @Param("operator") String operator);

    void deleteTask(@Param("ids") List<String> ids, @Param("operator") String operator);

    void deleteResource(@Param("ids") List<String> ids, @Param("operator") String operator);

    List<SysUser> selectUserByUserIds(List<String> userIds);

    List<DigitalStage> findStage(DigitalQueryVO digital);

    List<DigitalTask> findTasksByStage(DigitalQueryVO digital);

    List<StageInfoVO> getStageByEventCode(String digitalId);

    TargetInfoVO getPlanByDigitalId(String digitalId);

    List<TaskInfoVO> getTaskByEventCode(String digitalId);

    List<EmergencyResourceVO> getResourceByEventCode(String digitalId);

    DigitalPlan findAllPlanById(Digital digital);

    int findByNodeTenantAppVerCode(@Param("acceptNode") String acceptNode, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("eventName") String eventName);
}
