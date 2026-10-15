package com.chinatower.emergency.service;


import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.emergency.dto.*;

import java.util.List;

public interface EmergencyPlanService {

    TargetInfoVO getTargetInfo(TargetInfoVO vo);

    Result<String> setTargetInfo(TargetInfoVO vo);

    TaskInfoVO getTaskInfo(TaskInfoVO vo);

    Result<String> addTaskInfo(TaskInfoVO vo);

    List<TaskInfoVO> getTaskList(TaskInfoReq vo);

    Result<String> doFeedback(FeedbackInfoVO vo);

    List<FeedbackInfoVO> getFeedbackList(FeedbackInfoVO vo);

    Result<String> editStatus(PlanStatusReq vo);

    Result<String> editStageInfo(StageInfoVO vo);

    StageInfoVO getStageInfo(StageInfoVO vo);

    List<StageInfoVO> getStageInfoList(StageInfoVO vo);

    TaskInfoVO getDutyUserInfo(TaskInfoVO vo);

    Result<String>  updateTask(TaskInfoVO vo);
}
