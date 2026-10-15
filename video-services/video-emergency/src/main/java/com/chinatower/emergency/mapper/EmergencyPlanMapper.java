package com.chinatower.emergency.mapper;

import com.chinatower.emergency.dto.*;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Liqi
 */
@Repository
public interface EmergencyPlanMapper {

    List<SysUser> selectUserByUserName(@Param("userName") String userName, @Param("userNameList") Set<String> userNameList, @Param("userIdList")Set<String> userIdList);

    TargetInfoVO getTargetInfo(TargetInfoVO vo);

    int insertTargetInfo(TargetInfoVO vo);

    int updateTargetInfo(TargetInfoVO vo);

    int insertStageInfo(List<StageInfoVO> list);

    StageInfoVO getStageInfo(int stageId);

    List<Integer> getStageIdList(String eventCode);

    TaskInfoVO getTaskInfo(TaskInfoVO vo);

    int addTaskInfo(TaskInfoVO vo);

    List<TaskInfoVO> getTaskList(TaskInfoReq vo);

    int doFeedback(FeedbackInfoVO vo);

    int updateTaskInfo(TaskInfoVO vo);

    int updateStageInfo(StageInfoVO vo);

    List<FeedbackInfoVO> getFeedbackList(FeedbackInfoVO vo);

    Integer getEventStatus(int stageId);

    int updateEventStatus(@Param("modifier") String modifier, @Param("stageId")int stageId);

    List<StageInfoVO> getStageInfoList(StageInfoVO stageInfoVO);

    int deleteStageInfo(@Param("eventCodes") List<String> eventCodes);
    int deleteTaskInfo(@Param("eventCodes") List<String> eventCodes);
    int updateTaskStatus(@Param("eventCodes") List<String> eventCodes);
    int deleteFeedbackInfoVO(@Param("taskIdList") List<Integer> taskIdList);


    void insertTaskInfo(List<TaskInfoVO> taskInfoVOList);

    Integer checkImportTarget(String eventCode);

    void updateTask(TaskInfoVO vo);

    void deleteTargetInfo(List<String> eventCodes);

    void deleteTaskList(List<String> eventCodes);
}