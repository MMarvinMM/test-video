package com.chinatower.emergency.service.impl;


import cn.hutool.core.collection.ListUtil;
import com.chinatower.common.core.comparator.PinYinComparator;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.emergency.dto.*;
import com.chinatower.emergency.mapper.EmergencyPlanMapper;
import com.chinatower.emergency.service.EmergencyPlanService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.DeptUserGroupVO;
import com.chinatower.video.api.entity.VideoConstants;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.emergency.dto.Constant.fu_ONE_NUMBER;

@Service
@Slf4j
public class EmergencyPlanServiceImpl implements EmergencyPlanService {

    @Autowired
    private EmergencyPlanMapper emergencyPlanMapper;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private EmergencyAttachServiceImpl emergencyAttachServiceImpl;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Override
    public TargetInfoVO getTargetInfo(TargetInfoVO vo) {
        TargetInfoVO targetInfo = emergencyPlanMapper.getTargetInfo(vo);
        if (!ObjectUtils.isEmpty(targetInfo)) {
            targetInfo.setStageIdList(emergencyPlanMapper.getStageIdList(vo.getEventCode()));
        }
        return targetInfo;
    }

    @Override
    @Transactional
    public Result<String> setTargetInfo(TargetInfoVO vo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        vo.setTenantId(user.getTenantId());
        vo.setIndustryCode(user.getIndustryCode());
        vo.setAppVerCode(user.getAppVerCode());
        vo.setCreator(SecurityUtils.getOperator());

        TargetInfoVO targetInfo = emergencyPlanMapper.getTargetInfo(vo);
        //新增
        if(ObjectUtils.isEmpty(targetInfo)){
            emergencyPlanMapper.insertTargetInfo(vo);
            //初始化阶段数据，状态为-1:未设置阶段
            Integer stageNum = vo.getStageNum();
            if (stageNum > Constants.ZERO_NUM) {
                List<StageInfoVO> list = new ArrayList<>();
                for (int i = 1; i <= vo.getStageNum(); i++) {
                    StageInfoVO stageInfoVO = new StageInfoVO();
                    stageInfoVO.setEventCode(vo.getEventCode());
                    stageInfoVO.setStageName("阶段" + i);
                    stageInfoVO.setTenantId(user.getTenantId());
                    stageInfoVO.setIndustryCode(user.getIndustryCode());
                    stageInfoVO.setAppVerCode(user.getAppVerCode());
                    stageInfoVO.setCreator(SecurityUtils.getOperator());
                    stageInfoVO.setStageStatus(fu_ONE_NUMBER);
                    stageInfoVO.setStageStartDate(null);
                    stageInfoVO.setStageStopDate(null);
                    stageInfoVO.setStageDetail(null);
                    list.add(stageInfoVO);
                }
                emergencyPlanMapper.insertStageInfo(list);
            }
        }
        //编辑
        else {
            vo.setId(targetInfo.getId());
            vo.setModifier(SecurityUtils.getOperator());
            emergencyPlanMapper.updateTargetInfo(vo);
        }
        return Result.success(VideoConstants.SUCCESS_MESSAGE);
    }

    @Override
    public TaskInfoVO getTaskInfo(TaskInfoVO vo) {
        TaskInfoVO taskInfo = emergencyPlanMapper.getTaskInfo(vo);
        if (!ObjectUtils.isEmpty(taskInfo)) {
            EmergencyAttachVO emergencyAttachVO = new EmergencyAttachVO();
            emergencyAttachVO.setBusinessCode(String.valueOf(taskInfo.getTaskId()));
            emergencyAttachVO.setBusinessType(Constants.TWO_NUM);
            Map<Integer, List<EmergencyAttachVO>> allEmergencyAttaches = emergencyAttachServiceImpl.getAllEmergencyAttaches(emergencyAttachVO);

            if (!ObjectUtils.isEmpty(allEmergencyAttaches)) {
                Set<String> fileUrls = new HashSet<>();
                List<EmergencyAttachVO> imgUrlList = new ArrayList<>();
                List<EmergencyAttachVO> videoUrlList = new ArrayList<>();
                List<EmergencyAttachVO> fileUrlList = new ArrayList<>();
                for (Map.Entry<Integer, List<EmergencyAttachVO>> integerListEntry : allEmergencyAttaches.entrySet()) {
                    Integer key = integerListEntry.getKey();
                    List<EmergencyAttachVO> value = integerListEntry.getValue();
                    switch (key) {
                        case 1:
                            imgUrlList.addAll(value);
                            break;
                        case 2:
                            videoUrlList.addAll(value);
                            break;
                        case 3:
                            fileUrlList.addAll(value);
                            break;
                        default:
                            break;
                    }
                    for (EmergencyAttachVO attachVO : value) {
                        fileUrls.add(attachVO.getOriginUrl());
                    }
                }

                // 处理Obs文件URL加密返回
                Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(fileUrls));

                if (!CollectionUtils.isEmpty(imgUrlList)) {
                    taskInfo.setImgUrlList(this.getUrl(imgUrlList, result));
                }

                if (!CollectionUtils.isEmpty(videoUrlList)) {
                    taskInfo.setVideoUrlList(this.getUrl(videoUrlList, result));
                }

                if (!CollectionUtils.isEmpty(fileUrlList)) {
                    taskInfo.setFileUrlList(this.getUrl(fileUrlList, result));
                }
            }

            //转义为昵称
            this.setNickName(taskInfo);
            return taskInfo;
        }
        return null;
    }

    /**
     * 处理Obs文件URL加密返回
     *
     * @param split
     * @param result
     * @return
     */
    private List<EmergencyAttachVO> getUrl(List<EmergencyAttachVO> split, Map<String, String> result) {
        for (EmergencyAttachVO emergencyAttachVO : split) {
            String originUrl = emergencyAttachVO.getOriginUrl();
            if (!ObjectUtils.isEmpty(result.get(originUrl))) {
                emergencyAttachVO.setOriginUrl(result.get(originUrl));
            }
        }
        return split;
    }

    @Override
    @Transactional
    public Result<String> addTaskInfo(TaskInfoVO vo) {
        //校验任务时间段是否在阶段时间段内
        Integer stageId = vo.getStageId();
        StageInfoVO stageInfo = emergencyPlanMapper.getStageInfo(stageId);
        if (!ObjectUtils.isEmpty(stageInfo)) {
            if(stageInfo.getStageStatus() == -1){
                return Result.fail("请先设置阶段目标");
            }
            if (!isEffectiveDateHMS(vo.getTaskStartDate(), stageInfo.getStageStartDate(), stageInfo.getStageStopDate())
                    || !isEffectiveDateHMS(vo.getTaskStopDate(), stageInfo.getStageStartDate(), stageInfo.getStageStopDate())) {
                return Result.fail("任务时间不能超出阶段时间");
            }

            SysUser user = SecurityUtils.getLoginUser().getUser();
            vo.setTenantId(user.getTenantId());
            vo.setIndustryCode(user.getIndustryCode());
            vo.setAppVerCode(user.getAppVerCode());
            vo.setCreator(SecurityUtils.getOperator());
            if(StringUtils.isEmpty(vo.getUserIds())){
                vo.setUserIds(vo.getUserNames());
            }
            emergencyPlanMapper.addTaskInfo(vo);

            //处理附件数据
            Map<Integer, List<EmergencyAttachVO>> map = new HashMap<>();
            map.put(Constants.ONE_NUM, vo.getImgUrlList());
            map.put(Constants.TWO_NUM, vo.getVideoUrlList());
            map.put(Constants.THREE_NUM, vo.getFileUrlList());
            if(!CollectionUtils.isEmpty(vo.getImgUrlList()) ||
                    !CollectionUtils.isEmpty(vo.getVideoUrlList()) ||
                    !CollectionUtils.isEmpty(vo.getFileUrlList())){
                emergencyAttachServiceImpl.addEmergencyAttach(map, String.valueOf(vo.getTaskId()), Constants.TWO_NUM);
            }
            return Result.success(VideoConstants.SUCCESS_MESSAGE);
        }
        return Result.success(VideoConstants.FAIL_MESSAGE);
    }

    private boolean isEffectiveDateHMS(String nowTime, String startTime, String endTime) {
        nowTime = nowTime + DeviceConstants.START_TIME_SUFFIX;
        startTime = startTime + DeviceConstants.START_TIME_SUFFIX;
        endTime = endTime + DeviceConstants.START_TIME_SUFFIX;
        return DateUtils.isEffectiveDateHMS(nowTime, startTime, endTime);
    }

    @Override
    @DataScope(tenantColumn="tenant_id",industryCode="industry_code")
    public List<TaskInfoVO> getTaskList(TaskInfoReq vo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<SysUser> userList = emergencyPlanMapper.selectUserByUserName(user.getUserName(),null,null);
        if(!CollectionUtils.isEmpty(userList)){
            vo.setUserId(userList.get(Constants.ZERO_NUM).getUserId());
            Integer pageNum = vo.getPageNum();
            Integer pageSize = vo.getPageSize();
            if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
                PageHelper.startPage(pageNum, pageSize);
            }
            List<TaskInfoVO> taskList = emergencyPlanMapper.getTaskList(vo);
            if (!CollectionUtils.isEmpty(taskList)) {
                //转义为昵称
                this.setNickNames(taskList);
            }
            return taskList;
        }
        return null;
    }

//    @Override
//    public TableDataInfo getTaskList(TaskInfoReq vo) {
//        TableDataInfo rspData = new TableDataInfo();
//        rspData.setCode(ResultCode.FAILURE.getCode());
//        SysUser user = SecurityUtils.getLoginUser().getUser();
//        List<SysUser> userList = emergencyPlanMapper.selectUserByUserName(user.getUserName(),null,null);
//        if(!CollectionUtils.isEmpty(userList)){
//            vo.setUserId(userList.get(Constants.ZERO_NUM).getUserId());
//            Integer pageNum = vo.getPageNum();
//            Integer pageSize = vo.getPageSize();
//
//            //不使用pageHelper 分页 删除实体类中的pageNum 和pageSize
//            List<TaskInfoVO> taskList = emergencyPlanMapper.getTaskList(vo);
//            //首字母排序，数字放后面
//            List<TaskInfoVO> newTaskList = this.taskSort(taskList);
//            if (!CollectionUtils.isEmpty(newTaskList)) {
//                //转义为昵称
//                this.setNickNames(newTaskList);
//            }
//            int pageNumNew = pageHandle(newTaskList.size(),pageSize,pageNum);
//            //操作逻辑中需要删除元素，需要进行手动分页
//            List<TaskInfoVO> result = ListUtil.page(pageNumNew - 1, pageSize, newTaskList);
//            rspData.setCode(200);
//            rspData.setRows(result);
//            rspData.setTotal(newTaskList.size());
//        }
//        return rspData;
//    }

//    public List<TaskInfoVO> taskSort(List<TaskInfoVO> taskList) {
//        List<TaskInfoVO> newTaskList = new ArrayList<>();
//        Map<String, List<TaskInfoVO>> collect = new LinkedHashMap<>();
//        for (TaskInfoVO taskInfoVO : taskList) {
//            String taskStartDate = taskInfoVO.getTaskStartDate();
//            List<TaskInfoVO> taskInfoVOS;
//            if(!CollectionUtils.isEmpty(collect.get(taskStartDate))){
//                taskInfoVOS = collect.get(taskStartDate);
//            }else{
//                taskInfoVOS = new ArrayList<>();
//            }
//            taskInfoVOS.add(taskInfoVO);
//            collect.put(taskStartDate,taskInfoVOS);
//        }
//
//        for (Map.Entry<String, List<TaskInfoVO>> entry : collect.entrySet()) {
//            List<TaskInfoVO> valueList = entry.getValue();
//            List<TaskInfoVO> numList = new ArrayList<>();
//            List<TaskInfoVO> strList = new ArrayList<>();
//            for (TaskInfoVO taskInfoVO : valueList) {
//                if(!StringUtils.isEmpty(taskInfoVO.getTaskName())){
//                    String substring = taskInfoVO.getTaskName().substring(0, 1);
//                    if(substring.matches("\\d")){
//                        numList.add(taskInfoVO);
//                    }else{
//                        strList.add(taskInfoVO);
//                    }
//                }
//            }
//            numList.sort((o1, o2) -> new PinYinComparator().compare(o1.getTaskName(), o2.getTaskName()));
//            strList.sort((o1, o2) -> new PinYinComparator().compare(o1.getTaskName(), o2.getTaskName()));
//            newTaskList.addAll(strList);
//            newTaskList.addAll(numList);
//        }
//        return newTaskList;
//    }

    public void setNickName(TaskInfoVO taskInfoVO) {
        String[] split = taskInfoVO.getUserIds().split(Constants.COMMA);
        Set<String> userIdList = Arrays.stream(split).collect(Collectors.toSet());
        userIdList = userIdList.stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        List<SysUser> userList = emergencyPlanMapper.selectUserByUserName(null,null,userIdList);

        if(!CollectionUtils.isEmpty(userList)){
            Map<String, String> map = new HashMap<>();
            for (SysUser sysUser : userList) {
                map.put(sysUser.getUserId(), sysUser.getNickName());
            }
            StringBuilder nickNames = new StringBuilder();
            for (String s : split) {
                if (!ObjectUtils.isEmpty(map.get(s))) {
                    nickNames.append(map.get(s)).append(Constants.COMMA);
                }
            }
            if(nickNames.length() > 0){
                taskInfoVO.setNickNames(nickNames.substring(0, nickNames.length() - 1));
            }else{
                taskInfoVO.setNickNames(null);
            }
            taskInfoVO.setUserIds(null);
        }
    }

    public void setNickNames(List<TaskInfoVO> taskList){
        Set<String> userIdList = new HashSet<>();
        for (TaskInfoVO taskInfoVO : taskList) {
            userIdList.addAll(Arrays.stream(taskInfoVO.getUserIds().split(Constants.COMMA)).collect(Collectors.toSet()));
        }
        List<SysUser> userList = emergencyPlanMapper.selectUserByUserName(null,null,userIdList);

        if(!CollectionUtils.isEmpty(userList)){
            Map<String,String> map = new HashMap<>();
            for (SysUser sysUser : userList) {
                map.put(sysUser.getUserId(),sysUser.getNickName());
            }
            for (TaskInfoVO taskInfoVO : taskList) {
                StringBuilder nickNames = new StringBuilder();
                String[] split = taskInfoVO.getUserIds().split(Constants.COMMA);
                for (String s : split) {
                    if(!ObjectUtils.isEmpty(map.get(s))){
                        nickNames.append(map.get(s)).append(Constants.COMMA);
                    }
                }
                taskInfoVO.setNickNames(nickNames.substring(0,nickNames.length()-1));
                taskInfoVO.setUserIds(null);
            }
        }
    }

    @Override
    @Transactional
    public Result<String> doFeedback(FeedbackInfoVO vo){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        vo.setTenantId(user.getTenantId());
        vo.setIndustryCode(user.getIndustryCode());
        vo.setAppVerCode(user.getAppVerCode());
        vo.setCreator(SecurityUtils.getOperator());
        emergencyPlanMapper.doFeedback(vo);

        //处理附件数据
        Map<Integer, List<EmergencyAttachVO>> map = new HashMap<>();
        map.put(Constants.ONE_NUM,vo.getImgUrlList());
        map.put(Constants.TWO_NUM,vo.getVideoUrlList());
        map.put(Constants.THREE_NUM,vo.getFileUrlList());
        if(!CollectionUtils.isEmpty(vo.getImgUrlList()) ||
                !CollectionUtils.isEmpty(vo.getVideoUrlList()) ||
                !CollectionUtils.isEmpty(vo.getFileUrlList())){
            emergencyAttachServiceImpl.addEmergencyAttach(map, String.valueOf(vo.getId()), Constants.THREE_NUM);
        }


        //更新任务表的进度
        TaskInfoVO taskInfoVO = new TaskInfoVO();
        taskInfoVO.setTaskId(vo.getTaskId());
        taskInfoVO.setTaskProgress(vo.getTaskProgress());
        taskInfoVO.setModifier(SecurityUtils.getOperator());
        emergencyPlanMapper.updateTaskInfo(taskInfoVO);
        return Result.success(VideoConstants.SUCCESS_MESSAGE);
    }

    @Override
    public List<FeedbackInfoVO> getFeedbackList(FeedbackInfoVO vo){
        List<FeedbackInfoVO> feedbackList = emergencyPlanMapper.getFeedbackList(vo);
        if(!CollectionUtils.isEmpty(feedbackList)){
            //obs加密
            this.setFeedbackUrls(feedbackList);
            //转义为昵称
            this.setFeedbackUser(feedbackList);
        }
        return feedbackList;
    }

    public void setFeedbackUrls(List<FeedbackInfoVO> feedbackList){
        List<String> businessCodeList = feedbackList.stream().map(e->String.valueOf(e.getId())).collect(Collectors.toList());
        EmergencyAttachVO emergencyAttachVO = new EmergencyAttachVO();
        emergencyAttachVO.setBusinessCodeList(businessCodeList);
        emergencyAttachVO.setBusinessType(Constants.THREE_NUM);
        Map<Integer, List<EmergencyAttachVO>> allEmergencyAttaches = emergencyAttachServiceImpl.getAllEmergencyAttaches(emergencyAttachVO);

        if(!ObjectUtils.isEmpty(allEmergencyAttaches)){
            Set<String> fileUrls = new HashSet<>();
            List<EmergencyAttachVO> imgUrlList = new ArrayList<>();
            List<EmergencyAttachVO> videoUrlList = new ArrayList<>();
            List<EmergencyAttachVO> fileUrlList = new ArrayList<>();
            for (Map.Entry<Integer, List<EmergencyAttachVO>> integerListEntry : allEmergencyAttaches.entrySet()) {
                Integer key = integerListEntry.getKey();
                List<EmergencyAttachVO> value = integerListEntry.getValue();
                switch (key){
                    case 1:
                        imgUrlList.addAll(value);
                        break;
                    case 2:
                        videoUrlList.addAll(value);
                        break;
                    case 3:
                        fileUrlList.addAll(value);
                        break;
                    default:
                        break;
                }
                for (EmergencyAttachVO attachVO : value) {
                    fileUrls.add(attachVO.getOriginUrl());
                }
            }

            // 处理Obs文件URL加密返回
            Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(fileUrls));

            for (FeedbackInfoVO feedbackInfoVO : feedbackList) {
                if(!CollectionUtils.isEmpty(imgUrlList)){
                    List<EmergencyAttachVO> tmpImgUrlList = new ArrayList<>();
                    for (EmergencyAttachVO attachVO : imgUrlList) {
                        if(attachVO.getBusinessCode().equals(String.valueOf(feedbackInfoVO.getId()))){
                            tmpImgUrlList.add(attachVO);
                        }
                    }
                    feedbackInfoVO.setImgUrlList(this.getUrl(tmpImgUrlList,result));
                }

                if(!CollectionUtils.isEmpty(videoUrlList)){
                    List<EmergencyAttachVO> tmpVideoUrlList = new ArrayList<>();
                    for (EmergencyAttachVO attachVO : videoUrlList) {
                        if(attachVO.getBusinessCode().equals(String.valueOf(feedbackInfoVO.getId()))){
                            tmpVideoUrlList.add(attachVO);
                        }
                    }
                    feedbackInfoVO.setVideoUrlList(this.getUrl(tmpVideoUrlList,result));
                }

                if(!CollectionUtils.isEmpty(fileUrlList)){
                    List<EmergencyAttachVO> tmpFileUrlList = new ArrayList<>();
                    for (EmergencyAttachVO attachVO : fileUrlList) {
                        if(attachVO.getBusinessCode().equals(String.valueOf(feedbackInfoVO.getId()))){
                            tmpFileUrlList.add(attachVO);
                        }
                    }
                    feedbackInfoVO.setFileUrlList(this.getUrl(tmpFileUrlList,result));
                }
            }
        }
    }

    public void setFeedbackUser(List<FeedbackInfoVO> feedbackList){
        Set<String> userNameList = feedbackList.stream().map(FeedbackInfoVO::getCreator).collect(Collectors.toSet());
        List<SysUser> userList = emergencyPlanMapper.selectUserByUserName(null,userNameList,null);
        Map<String,String> nickMap = new HashMap<>();
        Map<String,String> phoneMap = new HashMap<>();
        for (SysUser sysUser : userList) {
            nickMap.put(sysUser.getUserName(),sysUser.getNickName());
            phoneMap.put(sysUser.getUserName(),sysUser.getPhonenumber());
        }
        for (FeedbackInfoVO feedbackInfoVO : feedbackList) {
            String feedbackName = nickMap.get(feedbackInfoVO.getCreator());
            String feedbackPhone = phoneMap.get(feedbackInfoVO.getCreator());
            if(!StringUtils.isEmpty(feedbackPhone)){
                feedbackInfoVO.setFeedbackPhone(AESUtil.encrypt(feedbackPhone));
            }
            if(!StringUtils.isEmpty(feedbackName)){
                feedbackInfoVO.setFeedbackName(feedbackName);
                feedbackInfoVO.setCreator(null);
            }
        }
    }
    @Override
    @Transactional
    public Result<String> editStatus(PlanStatusReq vo){
        Integer stageId = vo.getStageId();
        Integer taskId = vo.getTaskId();
        if(!ObjectUtils.isEmpty(stageId) && !ObjectUtils.isEmpty(taskId)){
            return Result.fail(VideoConstants.TURN_TO_MSG);
        }
        //1:开始,2:中止,3:结束
        Integer status = vo.getStatus();
        if(!ObjectUtils.isEmpty(stageId)){
            StageInfoVO stageInfoVO = new StageInfoVO();
            stageInfoVO.setStageId(stageId);
            stageInfoVO.setStageStatus(status);
            stageInfoVO.setModifier(SecurityUtils.getOperator());
            emergencyPlanMapper.updateStageInfo(stageInfoVO);
            //中止阶段操作：中止阶段的同时，中止该阶段的所有任务（不包含“未开始”状态的任务）
            //结束阶段操作：结束阶段的同时，结束该阶段的所有任务（不包含“未开始”状态的任务）
            if(Constants.TWO_NUM == status || Constants.THREE_NUM == status){
                TaskInfoVO taskInfoVO = new TaskInfoVO();
                taskInfoVO.setStageId(stageId);
                taskInfoVO.setTaskStatus(status);
                taskInfoVO.setModifier(SecurityUtils.getOperator());
                emergencyPlanMapper.updateTaskInfo(taskInfoVO);
            }
            //当应急事件存在阶段状态为“执行中”的阶段信息时，事件状态由“未处置”转为“处置中”
            else if (Constants.ONE_NUM == status) {
                //1 未处置 2 处置中 3 已完成 4 已归档
                int eventStatus = emergencyPlanMapper.getEventStatus(stageId);
                if(Constants.ONE_NUM == eventStatus){
                    emergencyPlanMapper.updateEventStatus(SecurityUtils.getOperator(), stageId);
                }
            }
        }else if(!ObjectUtils.isEmpty(taskId)){
            TaskInfoVO taskInfoVO = new TaskInfoVO();
            taskInfoVO.setTaskId(taskId);
            taskInfoVO.setTaskStatus(status);
            taskInfoVO.setModifier(SecurityUtils.getOperator());
            emergencyPlanMapper.updateTaskInfo(taskInfoVO);
        }
        return Result.success(VideoConstants.SUCCESS_MESSAGE);
    }

    @Override
    public Result<String> updateTask(TaskInfoVO vo) {
        //校验任务时间段是否在阶段时间段内
        Integer stageId = vo.getStageId();
        StageInfoVO stageInfo = emergencyPlanMapper.getStageInfo(stageId);
        if (!ObjectUtils.isEmpty(stageInfo)) {
            if(stageInfo.getStageStatus() == -1){
                return Result.fail("请先设置阶段目标");
            }
            if (!isEffectiveDateHMS(vo.getTaskStartDate(), stageInfo.getStageStartDate(), stageInfo.getStageStopDate())
                    || !isEffectiveDateHMS(vo.getTaskStopDate(), stageInfo.getStageStartDate(), stageInfo.getStageStopDate())) {
                return Result.fail("任务时间不能超出阶段时间");
            }
            vo.setModifier(SecurityUtils.getOperator());
            emergencyPlanMapper.updateTask(vo);
            return Result.success("更新成功");
        }else{
            return Result.success(VideoConstants.FAIL_MESSAGE);
        }

    }

    @Override
    public Result<String> editStageInfo(StageInfoVO vo){
        //校验任务时间段是否在阶段时间段内
        TargetInfoVO targetInfoVO = new TargetInfoVO();
        targetInfoVO.setEventCode(vo.getEventCode());
        TargetInfoVO targetInfo = emergencyPlanMapper.getTargetInfo(targetInfoVO);
        if(!ObjectUtils.isEmpty(targetInfo)){
            if(!isEffectiveDateHMS(vo.getStageStartDate(),targetInfo.getGlobalStartDate(),targetInfo.getGlobalStopDate())
                    || !isEffectiveDateHMS(vo.getStageStopDate(),targetInfo.getGlobalStartDate(),targetInfo.getGlobalStopDate())){
                return Result.fail("阶段时间不能超出总体目标时间");
            }

            vo.setModifier(SecurityUtils.getOperator());
            //编辑后更新为0:未开始状态
            vo.setStageStatus(Constants.ZERO_NUM);
            vo.setEventCode(null);
            emergencyPlanMapper.updateStageInfo(vo);
            return Result.success(VideoConstants.SUCCESS_MESSAGE);
        }else{
            return Result.fail("请先设置总体目标");
        }
    }

    @Override
    public StageInfoVO getStageInfo(StageInfoVO vo){
        Integer stageId = vo.getStageId();
        StageInfoVO stageInfo = emergencyPlanMapper.getStageInfo(stageId);
        if(!ObjectUtils.isEmpty(stageInfo)){
            stageInfo.setEventStatus(emergencyPlanMapper.getEventStatus(stageId));

            TaskInfoReq taskInfoReq = new TaskInfoReq();
            taskInfoReq.setStageId(stageId);
            taskInfoReq.setTaskStatus(-1);
            List<TaskInfoVO> taskList = emergencyPlanMapper.getTaskList(taskInfoReq);
            //构造任务id集合
            List<Integer> taskIdList = taskList.stream().map(TaskInfoVO::getTaskId).collect(Collectors.toList());
            FeedbackInfoVO feedbackInfoVO = new FeedbackInfoVO();
            feedbackInfoVO.setTaskIdList(taskIdList);
            List<FeedbackInfoVO> feedbackList = emergencyPlanMapper.getFeedbackList(feedbackInfoVO);

            for (TaskInfoVO taskInfoVO : taskList) {
                Integer taskId = taskInfoVO.getTaskId();
                List<Integer> feedbackIdList = new ArrayList<>();
                for (FeedbackInfoVO infoVO : feedbackList) {
                    if(taskId.equals(infoVO.getTaskId())){
                        feedbackIdList.add(infoVO.getId());
                    }
                }
                taskInfoVO.setFeedbackIdList(feedbackIdList);
                taskInfoVO.setUserIds(null);
            }
            stageInfo.setTaskList(taskList);
        }
        return stageInfo;
    }

    @Override
    public List<StageInfoVO> getStageInfoList(StageInfoVO vo){
        return emergencyPlanMapper.getStageInfoList(vo);
    }

    @Override
    public TaskInfoVO getDutyUserInfo(TaskInfoVO taskInfoVO) {
        TaskInfoVO taskInfo = emergencyPlanMapper.getTaskInfo(taskInfoVO);
        if (!ObjectUtils.isEmpty(taskInfo)) {
            String[] split = taskInfo.getUserIds().split(Constants.COMMA);
            Set<String> userIdList = Arrays.stream(split).collect(Collectors.toSet());
            List<SysUser> userList = emergencyPlanMapper.selectUserByUserName(null,null,userIdList);

            if(!CollectionUtils.isEmpty(userList)){
                List<String> idList = userList.stream().map(SysUser::getUserId).collect(Collectors.toList());
                SysUser sysUserNew = new SysUser();
                sysUserNew.setUserIdList(idList);
                Result<List<DeptUserGroupVO>> deptUserTreePeculiar = systemFeignClient.getDeptUserTreeList(sysUserNew);
                if (deptUserTreePeculiar.getCode() == ResultCode.SUCCESS.getCode()) {
                    taskInfoVO.setDeptUserTree(deptUserTreePeculiar.getData());
                }

                List<String> fileUrls = userList.stream().map(SysUser::getAvatar).collect(Collectors.toList());
                // 处理Obs文件URL加密返回
                Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(fileUrls));
                List<EmergencySysUser> newList = new ArrayList<>();
                for (SysUser sysUser : userList) {
                    EmergencySysUser newUser = new EmergencySysUser();
                    newUser.setId(sysUser.getUserId());
                    newUser.setName(sysUser.getNickName());
                    newUser.setPhone(AESUtil.encrypt(sysUser.getPhonenumber()));
                    newUser.setAvatar(result.get(sysUser.getAvatar()));
                    newUser.setUserName(sysUser.getUserName());
                    newList.add(newUser);
                }
                taskInfoVO.setUserList(newList);
            }
        }
        return taskInfoVO;
    }


    private int pageHandle(int total,int pageSize,int pageNum){
        int pageNumNew;
        int pages;
        if (pageSize > Constants.ZERO_NUM) {
            pages = total / pageSize + ((total % pageSize == Constants.ZERO_NUM) ? Constants.ZERO_NUM :  Constants.ONE_NUM);
        }else{
            pages = Constants.ZERO_NUM;
        }
        if (pageNum > pages ){
            pageNum = pages;
        }
        pageNumNew = pageNum;
        return pageNumNew;
    }


}
