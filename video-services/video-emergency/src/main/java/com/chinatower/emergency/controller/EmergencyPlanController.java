package com.chinatower.emergency.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.comparator.PinYinComparator;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.emergency.dto.*;
import com.chinatower.emergency.service.EmergencyPlanService;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * video-emergency
 * 应急事件-行动计划
 */
@RestController
@Slf4j
@RequestMapping("/emergency")
public class EmergencyPlanController extends TowerBaseController {

    @Autowired
    private EmergencyPlanService emergencyPlanService;

    /**
     * 总体目标查询
     * @param vo
     * @return
     */
    @PostMapping("/getTargetInfo")
    public Result<TargetInfoVO> getTargetInfo(@RequestBody TargetInfoVO vo){
        log.info("getTargetInfo请求参数=" + JSONObject.toJSONString(vo));
        return Result.data(emergencyPlanService.getTargetInfo(vo));
    }

    /**
     * 总体目标设置
     * @param vo
     * @return
     */
    @PostMapping("/setTargetInfo")
    public Result<String> setTargetInfo(@RequestBody TargetInfoVO vo){
        log.info("setTargetInfo请求参数=" + JSONObject.toJSONString(vo));
        return emergencyPlanService.setTargetInfo(vo);
    }

    /**
     * 阶段目标查询
     * @param vo
     * @return
     */
    @PostMapping("/getStageInfo")
    public Result<StageInfoVO> getStageInfo(@RequestBody StageInfoVO vo){
        log.info("getStageInfo请求参数=" + JSONObject.toJSONString(vo));
        return Result.data(emergencyPlanService.getStageInfo(vo));
    }

    /**
     * 阶段目标编辑
     * @param vo
     * @return
     */
    @PostMapping("/editStageInfo")
    public Result<String> editStageInfo(@RequestBody StageInfoVO vo){
        log.info("editStageInfo请求参数=" + JSONObject.toJSONString(vo));
        return emergencyPlanService.editStageInfo(vo);
    }
    /**
     * 阶段/任务状态变更
     * @param vo
     * @return
     */
    @PostMapping("/editStatus")
    public Result<String> editStatus(@RequestBody PlanStatusReq vo){
        log.info("editStatus请求参数=" + JSONObject.toJSONString(vo));
        return emergencyPlanService.editStatus(vo);
    }
    /**
     * 创建任务
     * @param vo
     * @return
     */
    @PostMapping("/addTaskInfo")
    public Result<String> addTaskInfo(@RequestBody TaskInfoVO vo){
        log.info("addTaskInfo请求参数=" + JSONObject.toJSONString(vo));
        return emergencyPlanService.addTaskInfo(vo);
    }
    /**
     * 任务信息查询
     * @param vo
     * @return
     */
    @PostMapping("/getTaskInfo")
    public Result<TaskInfoVO> getTaskInfo(@RequestBody TaskInfoVO vo){
        log.info("getTaskInfo请求参数=" + JSONObject.toJSONString(vo));
        return Result.data(emergencyPlanService.getTaskInfo(vo));
    }
    /**
     * 任务反馈信息列表查询
     * @param vo
     * @return
     */
    @PostMapping("/getFeedbackList")
    public Result<List<FeedbackInfoVO>> getFeedbackList(@RequestBody FeedbackInfoVO vo){
        log.info("getFeedbackList请求参数=" + JSONObject.toJSONString(vo));
        return Result.data(emergencyPlanService.getFeedbackList(vo));
    }
    /**
     * 任务反馈
     * @param vo
     * @return
     */
    @PostMapping("/doFeedback")
    public Result<String> doFeedback(@RequestBody FeedbackInfoVO vo){
        log.info("doFeedback请求参数=" + JSONObject.toJSONString(vo));
        return emergencyPlanService.doFeedback(vo);
    }

    /**
     * 更新任务时间
     * @param vo
     * @return
     */
    @PostMapping("/updateTask")
    public Result<String> updateTask(@RequestBody TaskInfoVO vo){
        log.info("updateTaskTime={}", JSONObject.toJSONString(vo));
        return emergencyPlanService.updateTask(vo);
    }



    /**
     * 待办任务列表查询
     * @param vo
     * @return
     */
    @PostMapping("/getTaskList")
    public TableDataInfo getTaskList(@RequestBody TaskInfoReq vo){
        log.info("getTaskList请求参数=" + JSONObject.toJSONString(vo));
        return getDataTable(emergencyPlanService.getTaskList(vo));
    }

    /**
     * 健康检查接口
     * @return
     */
    @GetMapping("/getHealthStatus")
    public Result<String> getHealthStatus(){
        return Result.data(VideoConstants.SUCCESS_MESSAGE);
    }

    /**
     * 阶段目标列表查询
     * @param vo
     * @return
     */
    @PostMapping("/getStageInfoList")
    public Result<List<StageInfoVO>> getStageInfoList(@RequestBody StageInfoVO vo){
        log.info("getStageInfoList请求参数=" + JSONObject.toJSONString(vo));
        return Result.data(emergencyPlanService.getStageInfoList(vo));
    }

    /**
     * 任务责任人信息查询
     * @param vo
     * @return
     */
    @PostMapping("/getDutyUserInfo")
    public Result<TaskInfoVO> getDutyUserInfo(@RequestBody TaskInfoVO vo){
        log.info("getDutyUserInfo请求参数=" + JSONObject.toJSONString(vo));
        return Result.data(emergencyPlanService.getDutyUserInfo(vo));
    }

}
