package com.chinatower.emergency.service.impl;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.emergency.dto.*;
import com.chinatower.emergency.mapper.EmergencyEventInfoMapper;
import com.chinatower.emergency.mapper.EmergencyPlanMapper;
import com.chinatower.emergency.service.EmergencyAttachService;
import com.chinatower.emergency.service.EmergencyDeptService;
import com.chinatower.emergency.service.EmergencyEventInfoService;
import com.chinatower.emergency.service.EmergencyWarningEventRelService;
import com.chinatower.emergency.utils.EmergencyDateUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmergencyEventInfoServiceImpl extends BaseEventInfoService implements EmergencyEventInfoService {

    @Autowired
    private EmergencyEventInfoMapper emergencyEventInfoMapper;
    @Autowired
    private EmergencyWarningEventRelService eventRelService;
    @Autowired
    private EmergencyDeptServiceImpl emergencyDeptService;
    @Autowired
    private EmergencyResourceServiceImpl emergencyResourceService;
    @Autowired
    private EmergencyAttachService attachService;
    @Autowired
    private EmergencyPlanMapper emergencyPlanMapper;


    @Transactional
    @Override
    public void addEmergencyEventInfoVO(EmergencyEventInfoVO eventInfo) throws Exception {
        try {
            String eventCode = makeEventCode();
            setUserInfo(eventInfo, true);
            eventInfo.setEventCode(eventCode);
            emergencyDeptService.generateInitialDept(eventInfo.getEventCode());
            emergencyEventInfoMapper.insert(eventInfo);
            Map<Integer, List<EmergencyAttachVO>> map = attachListToMap(eventInfo);
            attachService.addEmergencyAttach(map, eventCode, Constant.BUSINESS_TYPE_EVENT);
            if(StringUtils.isNotBlank(eventInfo.getWarningOrderId())) {
                EmergencyWarningEventRelVO relVO = new EmergencyWarningEventRelVO();
                setUserInfo(relVO, true);
                relVO.setEventCode(eventCode);
                relVO.setWarningOrderId(eventInfo.getWarningOrderId());
                eventRelService.connectedEvent(relVO);
            }
        } catch (Exception e) {
            log.error(Constant.SAVE_ERROR, e);
            throw new Exception(Constant.SAVE_ERROR);
        }
    }

    @Override
    @Transactional
    public void updateEmergencyEventInfoVO(EmergencyEventInfoVO eventInfo) {
        // 判断当前修改是不是只修改状态
        EmergencyEventInfoVO eventInfoFromDB = getEmergencyEventInfoVO(eventInfo.getEventCode());
        boolean onlyStatus = eventInfoFromDB.getStatus() == eventInfo.getStatus();
        try {
            if(eventInfo.getStatus() == 3) {
                StageInfoVO stage = new StageInfoVO();
                stage.setEventCode(eventInfo.getEventCode());
                stage.setStageStatus(2);
                TaskInfoVO task = new TaskInfoVO();
                task.setEventCode(eventInfo.getEventCode());
                task.setTaskStatus(2);
                emergencyPlanMapper.updateStageInfo(stage);
                emergencyPlanMapper.updateTaskInfo(task);

            }
            setUserInfo(eventInfo, false);
            emergencyEventInfoMapper.update(eventInfo);
            if (onlyStatus) {
                attachService.deleteEmergencyAttach(new EmergencyAttachVO(eventInfo.getEventCode(), Constant.BUSINESS_TYPE_EVENT, null));
                Map<Integer, List<EmergencyAttachVO>> map = attachListToMap(eventInfo);
                attachService.addEmergencyAttach(map, eventInfo.getEventCode(), Constant.BUSINESS_TYPE_EVENT);
            }
        } catch (Exception e) {
            log.error("更新事件信息失败", e);
            throw new RuntimeException(Constant.UPDATE_ERROR);
        }
    }

    private Map attachListToMap(EmergencyEventInfoVO eventInfo) {
        Map<Integer, List<EmergencyAttachVO>> map = new HashMap<>();
        map.put(Constant.ATTACH_TYPE_IMG, eventInfo.getImgUrlList());
        map.put(Constant.ATTACH_TYPE_VIDEO, eventInfo.getVideoUrlList());
        map.put(Constant.ATTACH_TYPE_FILE, eventInfo.getFileUrlList());
        map.put(Constant.ATTACH_TYPE_AUDIO, eventInfo.getAudioUrlList());
        return map;
    }

    @Override
    public EmergencyEventInfoVO getEmergencyEventInfoVO(String eventCode) {
        try {
            EmergencyEventInfoVO param = new EmergencyEventInfoVO();
            param.setEventCode(eventCode);
            setUserInfo(param, true);
            EmergencyEventInfoVO vo = emergencyEventInfoMapper.selectByEventCode(param);
            Map<Integer, List<EmergencyAttachVO>> attachMap = attachService.getAllEmergencyAttaches(new EmergencyAttachVO(eventCode, Constant.BUSINESS_TYPE_EVENT, null));
            if(!CollectionUtils.isEmpty(attachMap)) {
                vo.setImgUrlList(attachMap.get(Constant.ATTACH_TYPE_IMG));
                vo.setVideoUrlList(attachMap.get(Constant.ATTACH_TYPE_VIDEO));
                vo.setFileUrlList(attachMap.get(Constant.ATTACH_TYPE_FILE));
                vo.setAudioUrlList(attachMap.get(Constant.ATTACH_TYPE_AUDIO));
                if(vo != null) {
                    handleAttachPath(vo, attachMap);
                }
            }
            return vo;
        } catch (Exception e) {
            log.error("查询事件信息失败", e);
            throw e;
        }
    }

    @Override
    public List<EmergencyEventInfoVO> getEmergencyEventInfoList(EmergencyEventInfoVO eventInfo) {
        setUserInfo(eventInfo, true);
        if(StringUtils.isNotBlank(eventInfo.getEventTimeStart()) && StringUtils.isNotBlank(eventInfo.getEventTimeEnd())) {
            eventInfo.setEventTimeStart(EmergencyDateUtils.completionDateString(eventInfo.getEventTimeStart(), "00:00:00"));
            eventInfo.setEventTimeEnd(EmergencyDateUtils.completionDateString(eventInfo.getEventTimeEnd(), "23:59:59"));
        }
        List<EmergencyEventInfoVO> list = emergencyEventInfoMapper.selectList(eventInfo);
        return list;
    }

    @Override
    @Transactional
    public void deleteEvent(EmergencyEventInfoVO eventInfo) {
        if(CollectionUtils.isEmpty(eventInfo.getEventCodes()) || eventInfo.getEventCodes().size() == 0) {
            throw new RuntimeException("事件编码不能为空");
        }
        try {
            TaskInfoReq taskInfoReq = new TaskInfoReq();
            taskInfoReq.setEventCodes(eventInfo.getEventCodes());
            // 查出世间任务ID作为删除反馈的传参
            List<TaskInfoVO> taskInfoVOS = emergencyPlanMapper.getTaskList(taskInfoReq);
            List<Integer> taskIdList = taskInfoVOS.stream().map(TaskInfoVO::getTaskId).collect(Collectors.toList());

            // 删除事件
            emergencyEventInfoMapper.delete(eventInfo);
            // 删除事件相关附件
            attachService.deleteEmergencyAttach(new EmergencyAttachVO(null, Constant.BUSINESS_TYPE_EVENT, eventInfo.getEventCodes()));
            EmergencyWarningEventRelVO relVO = new EmergencyWarningEventRelVO();
            relVO.setEventCodes(eventInfo.getEventCodes());
            // 删除事件告警关系
            eventRelService.delete(relVO);
            // 删除部门用户
            emergencyDeptService.clearDeptUserInfo(eventInfo.getEventCodes());
            // 删除事件阶段
            emergencyPlanMapper.deleteStageInfo(eventInfo.getEventCodes());
            // 删除事件阶段任务
            List<String> taskCodes = taskIdList.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());
            emergencyPlanMapper.deleteTaskInfo(eventInfo.getEventCodes());
            // 删除任务附件
            if(!CollectionUtils.isEmpty(taskCodes)) {
                attachService.deleteEmergencyAttach(new EmergencyAttachVO(null, Constant.BUSINESS_TYPE_TASK, taskCodes));
            }
            if (!CollectionUtils.isEmpty(taskIdList)) {
                FeedbackInfoVO feedbackInfoVO = new FeedbackInfoVO();
                feedbackInfoVO.setTaskIdList(taskIdList);
                List<FeedbackInfoVO> feedbackInfoVOS = emergencyPlanMapper.getFeedbackList(feedbackInfoVO);
                List<String> feedbackCodes = feedbackInfoVOS.stream().map( feedbackInfoVO1 -> String.valueOf(feedbackInfoVO1.getId())).collect(Collectors.toList());
                // 删除事件反馈
                emergencyPlanMapper.deleteFeedbackInfoVO(taskIdList);
                if(!CollectionUtils.isEmpty(feedbackCodes)) {
                    // 删除事件反馈附件
                    attachService.deleteEmergencyAttach(new EmergencyAttachVO(null, Constant.BUSINESS_TYPE_FEEDBACK, feedbackCodes));
                }
            }
            // 删除资源
            EmergencyResourceVO resourceVO = new EmergencyResourceVO();
            resourceVO.setEventCodes(eventInfo.getEventCodes());
            SysUser user = SecurityUtils.getLoginUser().getUser();
            resourceVO.setTenantId(user.getTenantId());
            resourceVO.setIndustryCode(user.getIndustryCode());
            emergencyResourceService.deleteEmergencyResourceByEventCodes(resourceVO);
        } catch (Exception e) {
            log.error(Constant.DELETE_ERROR, e);
            throw new RuntimeException(Constant.DELETE_ERROR);
        }
    }

    @Override
    public List<EmergencyWarningEventRelVO> getEventWarningList(String eventCode) {
        EmergencyWarningEventRelVO emergencyWarningEventRelVO = new EmergencyWarningEventRelVO();
        emergencyWarningEventRelVO.setEventCode(eventCode);
        List<EmergencyWarningEventRelVO> relVOS = eventRelService.selectByCondition(emergencyWarningEventRelVO);
        return relVOS;
    }

    @Override
    public List<EmergencyEventInfoVO> getLayerForEventInfo(EmergencyEventInfoVO eventInfo) {
        // 查询图层默认给升序
        eventInfo.setSortType("asc");
        List<EmergencyEventInfoVO> list = getEmergencyEventInfoList(eventInfo);
        return list;
    }
}
