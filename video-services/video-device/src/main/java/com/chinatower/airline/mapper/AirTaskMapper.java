package com.chinatower.airline.mapper;

import com.chinatower.device.entity.dto.AirDeviceDto;
import com.chinatower.device.entity.dto.AirTaskEnableReq;
import com.chinatower.airline.dto.request.AirTaskListReq;
import com.chinatower.device.entity.dto.AirTaskReq;
import com.chinatower.airline.dto.response.AirTaskRes;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.AirLineVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author lize
 * @Date 2024/11/5 20:43
 */
@Mapper
public interface AirTaskMapper {

    /**
     * 批量插入任务
     * @param airTaskReqs
     */
    void insertBath(List<AirTaskReq> airTaskReqs);

    /**
     * 根据planId删除任务
     * @param airTaskReq
     * @return
     */
    Integer deleteByPlanId(AirTaskReq airTaskReq);

    /**
     * 根据id删除任务
     * @param airTaskReq
     * @return
     */
    Integer deleteById(AirTaskReq airTaskReq);

    /**
     * 根据planId去查任务表中对应的任务数量
     * @param planId
     * @return
     */
    Integer findPlanTaskNumByPlanId(String planId);


    void updateTasks(String oldPlanId, String startTime, String planId, String updateUser);


    void updateTasksStatus( String taskRecordId);

    void updateEnabledTaskIdList(@Param("param") AirTaskEnableReq airTaskEnableReq);


    List<AirTaskReq> findAirTaskByPlanId (String planId);

    void batchUpdateFindPlanId(List<AirTaskReq> tasksToUpdate);

    List<AirTaskRes> findListById(AirTaskEnableReq airTaskReq);

    AirTaskRes findById(AirTaskReq airTaskReq);

    List<AirTaskRes> findAirTaskByTaskIds(List<String> mappedList);

    /**
     * 查询任务列表
     * @param airTaskReq
     * @return
     */
    List<AirTaskRes> queryTaskList(AirTaskListReq airTaskReq);

    /**
     *  查询任务列表
     * @param airLineVoList
     * @return
     */
    List<AirTaskReq> queryTaskByFlightRouteId(List<AirLineVo> airLineVoList);

    /**
     *
     * @param originalTaskRecordIds
     */
    void editTaskType(String originalTaskRecordIds);

    /**
     *
     * @param originalTaskRecordIds
     * @param photoId
     */
    void insertReFlyRef(String originalTaskRecordIds, String photoId,String originalTaskRecordId);

    /**
     * 查询任务信息
     * @param taskRecordId
     * @return
     */
    AirTaskRes queryTaskInfo(String taskRecordId);

    /**
     * 查询复飞任务
     */
    List<AirTaskRes> queryTaskInfoRef(String taskRecordId,String photoId);

    /**
     * 查询任务信息数量
     * @param taskRecordId
     * @return
     */
    int queryTaskInfoCount(String taskRecordId);

    /**
     *
     * @param airTaskReq
     * @return
     */
    List<AirTaskRes> queryTaskListLimit(AirTaskListReq airTaskReq);

    /**
     * 删除收藏数据
     * @param taskRecordId
     */
    void deleteCollectTask(String taskRecordId);

    int updateTaskAccumulateMileage(AirTaskReq airTaskReq);

    List<Map<String,Object>> queryCreateInfoByTaskIds(List<String> taskRecordIds);

    /**
     * 查询任务信息
     * @param taskRecordId
     * @return
     */
    AirTaskReq selectTaskInfo(String taskRecordId);

    UavDeviceDTO queryUavByNestInfo(@Param("param") AirDeviceDto param);

    /**
     * 查询任务起始机巢编号
     * @param taskRecordId 任务ID
     * @return 起始机巢编号
     */
    String queryStartNestCode(String taskRecordId);
}
