package com.chinatower.emergency.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.IResultCode;
import com.chinatower.emergency.dto.*;
import com.chinatower.emergency.mapper.*;
import com.chinatower.emergency.service.DigitalService;
import com.chinatower.emergency.service.EmergencyAttachService;
import com.chinatower.emergency.service.EmergencyResourceService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileObsFeignClient;
import com.chinatower.file.response.FileObsEncryptResponse;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.emergency.dto.Constant.*;

@Slf4j
@Service
public class DigitalServiceImpl implements DigitalService {

    @Resource
    DigitalMapper digitalMapper;

    @Resource
    EmergencyDeptMapper emergencyDeptMapper;

    @Resource
    private EmergencyResourceService emergencyResourceService;

    @Resource
    private EmergencyUserMapper emergencyUserMapper;

    @Resource
    FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    FileObsFeignClient fileObsFeignClient;

    @Autowired
    private EmergencyDeptServiceImpl emergencyDeptService;

    @Autowired
    private EmergencyResourceServiceImpl emergencyResourceServiceImpl;

    @Autowired
    private EmergencyAttachService attachService;

    @Autowired
    private EmergencyPlanMapper emergencyPlanMapper;

    @Resource
    private EmergencyAttachMapper emergencyAttachMapper;

    public static final String COMMA = ",";

    public static final Integer BUSINESS_TYPE_DIGITAL = 4;

    public static final Integer RECORD_TYPE_DIGITAL = 3;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Digital digital) {

        log.info("新增数字预案:{}", JSON.toJSONString(digital));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        String appVerCode = user.getAppVerCode();
        String operator = SecurityUtils.getOperator();

        //判断预案是否存在
        int count = digitalMapper.findByNodeTenantAppVerCode(user.getPlatformCode(), tenantId, industryCode, digital.getEventName());
        if (count > 0) {
            throw new com.chinatower.platform.common.exception.CustomException("预案名称已存在", 400);
        }
        //新增数字预案
        String digitalId = IdUtils.simpleUUID();
        digital.setDigitalId(digitalId);
        digital.setCreator(operator);
        digital.setTenantId(tenantId);
        digital.setIndustryCode(industryCode);
        digital.setAppVerCode(appVerCode);
        digital.setAcceptNode(user.getPlatformCode());
        digitalMapper.save(digital);

        //文件
        if (!digital.getAttachList().isEmpty()) {
            List<EmergencyAttachVO> attachList = digital.getAttachList();
            for (EmergencyAttachVO emergencyAttach : attachList) {
                emergencyAttach.setBusinessCode(digitalId);
                emergencyAttach.setBusinessType(BUSINESS_TYPE_DIGITAL);
                emergencyAttach.setRecordType(RECORD_TYPE_DIGITAL);
                emergencyAttach.setCreator(operator);
            }
            emergencyAttachMapper.insertEmergencyAttach(attachList);
        }


        //指挥部

        //新增一个指挥部
        EmergencyDeptDTO emergencyDeptDTO = new EmergencyDeptDTO();
        emergencyDeptDTO.setParentId(ZERO_STR);
        emergencyDeptDTO.setDeptLevel(1);
        emergencyDeptDTO.setAncestors(ZERO_STR);
        emergencyDeptDTO.setCommanderFlag(ONE);
        emergencyDeptDTO.setEmergencyDeptName("指挥官");
        emergencyDeptDTO.setEventCode(digitalId);
        emergencyDeptDTO.setCreator(operator);
        emergencyDeptDTO.setTenantId(tenantId);
        emergencyDeptDTO.setIndustryCode(industryCode);
        emergencyDeptDTO.setAppVerCode(appVerCode);
        emergencyDeptDTO.setCommanderFlag(1);
        emergencyDeptMapper.addEmergencyGroup(emergencyDeptDTO);
        if (StringUtils.isNotEmpty(digital.getCommander())) {
            //新增指挥官
            EmergencyUserDTO emergencyUserDTO = new EmergencyUserDTO();
            emergencyUserDTO.setUserIdList(Collections.singletonList(digital.getCommander()));
            emergencyUserDTO.setEmergencyDeptId(emergencyDeptDTO.getId());
            emergencyUserDTO.setTenantId(tenantId);
            emergencyUserDTO.setCreator(operator);
            emergencyUserDTO.setAppVerCode(appVerCode);
            emergencyUserDTO.setIndustryCode(industryCode);
            emergencyUserMapper.addCommander(emergencyUserDTO);
        }

        //新增部门、人员
        addDeptUser(digital, digitalId, operator, tenantId, industryCode, appVerCode);


        //新增数字预案计划
        String planId = IdUtils.simpleUUID();
        DigitalPlan digitalPlan = digital.getDigitalPlan();
        digitalPlan.setPlanId(planId);
        digitalPlan.setDigitalId(digitalId);
        digitalPlan.setCreator(operator);
        digitalPlan.setTenantId(user.getTenantId());
        digitalPlan.setIndustryCode(industryCode);
        digitalMapper.savePlan(digitalPlan);

        //新增数字预案阶段
        List<DigitalStage> digitalStages = digitalPlan.getDigitalStage();

        if (!digitalStages.isEmpty()) {
            //保存的task
            List<DigitalTask> saveTaskList = new ArrayList<>();

            //保存的资源
            List<EmergencyResourceVO> saveResourceList = new ArrayList<>();

            for (DigitalStage digitalStage : digitalStages) {
                String stageId = IdUtils.simpleUUID();
                digitalStage.setStageId(stageId);
                digitalStage.setDigitalId(digitalId);
                digitalStage.setPlanId(planId);
                digitalStage.setCreator(operator);
                digitalStage.setTenantId(user.getTenantId());
                digitalStage.setIndustryCode(industryCode);

                //新增数字预案任务
                List<DigitalTask> taskList = digitalStage.getTaskList();
                for (DigitalTask digitalTask : taskList) {
                    String taskId = IdUtils.simpleUUID();
                    digitalTask.setTaskId(taskId);
                    digitalTask.setDigitalId(digitalId);
                    digitalTask.setPlanId(planId);
                    digitalTask.setStageId(stageId);
                    digitalTask.setTenantId(user.getTenantId());
                    digitalTask.setIndustryCode(industryCode);
                    saveTaskList.add(digitalTask);
                    //资源
                    if (!digitalTask.getResourceList().isEmpty()) {
                        digitalTask.getResourceList().forEach(item -> {
                            EmergencyResourceVO resource = new EmergencyResourceVO();
                            resource.setEventCode(digitalId);
                            resource.setStageId(stageId);
                            resource.setTaskId(taskId);
                            resource.setResourceId(item);
                            resource.setTenantId(tenantId);
                            resource.setIndustryCode(industryCode);
                            resource.setAppVerCode(appVerCode);
                            resource.setCreator(operator);
                            saveResourceList.add(resource);
                        });
                    }
                }

            }
            digitalMapper.saveStageBatch(digitalStages);


            if (!saveTaskList.isEmpty()) {
                digitalMapper.saveTaskBatch(saveTaskList);
            }

            if (!saveResourceList.isEmpty()) {
                emergencyResourceService.addEmergencyResource(saveResourceList);
            }
        }

    }


    /**
     * 组织、人员批量入库
     * @param digital
     * @param digitalId
     * @param operator
     * @param tenantId
     * @param industryCode
     * @param appVerCode
     */
    public void addDeptUser(Digital digital, String digitalId, String operator, String tenantId, String industryCode, String appVerCode) {
        //拆分树结构，入库
        List<DigitalDeptUserTreeVO> deptTree = digital.getDeptTree();
        Map<Integer, List<DigitalDeptUserTreeVO>> map = new TreeMap<>();


        /*
        构建格式：级别:List<Object>
        {
            1:[{},{}],
            2:[{},{}],
            3:[{},{}],
            ...
        }
         */
        Map<Integer, List<DigitalDeptUserTreeVO>> flattenListMap = flattenList(map, deptTree);
        log.info("构建后的部门数据:{}", JSON.toJSONString(flattenListMap));
        Map<String, String> oldParentIdNewIdMap = new HashMap<>();
        Map<String, String> parentIdAncestors = new HashMap<>();
        //循环入库 入库顺序 dept_level  0, 1 ,2 ,3 ...
        for (Map.Entry<Integer, List<DigitalDeptUserTreeVO>> stringListEntry : flattenListMap.entrySet()) {
            List<DigitalDeptUserTreeVO> value = stringListEntry.getValue();
            value.forEach(item -> {
                log.info("map:{}", JSON.toJSONString(oldParentIdNewIdMap));
                setParentId(oldParentIdNewIdMap, parentIdAncestors, item);
                item.setEventCode(digitalId);
                item.setCreator(operator);
                item.setTenantId(tenantId);
                item.setIndustryCode(industryCode);
                item.setAppVerCode(appVerCode);
            });
            log.info("保存部门数据:{}", JSON.toJSONString(value));
            emergencyDeptMapper.batchSave(value);
            log.info("保存后的部门数据:{}", JSON.toJSONString(value));
            value.forEach(item -> {
                if (!item.getUserList().isEmpty()) {
                    //新增用户
                    EmergencyUserDTO emergencyUserDTO = new EmergencyUserDTO();
                    emergencyUserDTO.setUserIdList(item.getUserList());
                    emergencyUserDTO.setTenantId(tenantId);
                    emergencyUserDTO.setIndustryCode(industryCode);
                    emergencyUserDTO.setAppVerCode(appVerCode);
                    emergencyUserDTO.setCreator(operator);
                    emergencyUserDTO.setEmergencyDeptId(item.getId());
                    log.info("保存用户:{}", JSON.toJSONString(emergencyUserDTO));
                    emergencyUserMapper.addCommander(emergencyUserDTO);
                }

                //赋值id
                putOldIdNewId(oldParentIdNewIdMap, parentIdAncestors, item);
            });
        }
    }


    /**
     * 树dept结构扁平化Map   {level:[{Object},{Object}]}
     * @param treeList
     * @return
     */
    public Map<Integer, List<DigitalDeptUserTreeVO>> flattenList(Map<Integer, List<DigitalDeptUserTreeVO>> map, List<DigitalDeptUserTreeVO> treeList) {
        for (DigitalDeptUserTreeVO node : treeList) {
            List<DigitalDeptUserTreeVO> list;
            if (map.containsKey(node.getDeptLevel())) {
                list = map.get(node.getDeptLevel());
            } else {
                list = new ArrayList<>();
            }
            list.add(node);
            map.put(node.getDeptLevel(), list);
            if (node.getChildren() != null) {
                flattenList(map, node.getChildren());
            }
        }
        return map;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Digital digital) {
        log.info("修改数字预案入参:{}", JSON.toJSONString(digital));
        SysUser user = SecurityUtils.getLoginUser().getUser();

        String operator = SecurityUtils.getOperator();
        digital.setModifier(operator);
        digitalMapper.update(digital);

        //文件
        List<EmergencyAttachVO> attachList = digital.getAttachList();
        if (!attachList.isEmpty()) {
            EmergencyAttachVO emergencyAttachVO = new EmergencyAttachVO();
            emergencyAttachVO.setBusinessCode(digital.getDigitalId());
            emergencyAttachVO.setBusinessType(BUSINESS_TYPE_DIGITAL);
            emergencyAttachMapper.deleteEmergencyAttach(emergencyAttachVO);

            for (EmergencyAttachVO emergencyAttach : attachList) {
                emergencyAttach.setBusinessCode(digital.getDigitalId());
                emergencyAttach.setBusinessType(BUSINESS_TYPE_DIGITAL);
                emergencyAttach.setRecordType(RECORD_TYPE_DIGITAL);
                emergencyAttach.setCreator(operator);
            }
            emergencyAttachMapper.insertEmergencyAttach(attachList);
        }






        //修改计划
        DigitalPlan digitalPlan = digital.getDigitalPlan();
        digitalPlan.setModifier(operator);
        digitalMapper.updatePlan(digitalPlan);

        //部门 人员 先删除 后新增
        if (!digital.getDeptTree().isEmpty()) {
            emergencyUserMapper.deleteByEventCode(digital.getDigitalId());
            emergencyDeptMapper.deleteByEventCode(digital.getDigitalId());
        }

        if (StringUtils.isNotEmpty(digital.getCommander())) {
            //修改指挥官
            String deptId = emergencyDeptMapper.findCommanderDept(digital.getDigitalId());
            EmergencyUserDTO emergencyUserDTO = new EmergencyUserDTO();
            emergencyUserDTO.setUserId(digital.getCommander());
            emergencyUserDTO.setEmergencyDeptId(deptId);
            emergencyUserDTO.setModifier(operator);
            emergencyUserDTO.setIndustryCode(user.getIndustryCode());
            emergencyUserDTO.setTenantId(user.getTenantId());
            emergencyUserMapper.updateCommander(emergencyUserDTO);
        }


        //新增用户
        addDeptUser(digital, digital.getDigitalId(), operator, user.getTenantId(), user.getIndustryCode(), user.getAppVerCode());

        //预案处理
        if (!digitalPlan.getDigitalStage().isEmpty()) {

            List<DigitalStage> digitalStages = digitalPlan.getDigitalStage();

            //先删除，后新增
            digitalMapper.deleteStageByDigitalId(digital.getDigitalId());
            digitalMapper.deleteTaskByDigitalId(digital.getDigitalId());

            EmergencyResourceVO delEmergency = new EmergencyResourceVO();
            delEmergency.setEventCode(digital.getDigitalId());
            emergencyResourceService.deleteEmergencyResource(delEmergency);

            //保存的task
            List<DigitalTask> saveTaskList = new ArrayList<>();

            List<EmergencyResourceVO> saveResourceList = new ArrayList<>();

            for (DigitalStage digitalStage : digitalStages) {
                String stageId = IdUtils.simpleUUID();
                digitalStage.setStageId(stageId);
                digitalStage.setDigitalId(digital.getDigitalId());
                digitalStage.setPlanId(digitalPlan.getPlanId());
                digitalStage.setCreator(operator);
                digitalStage.setTenantId(user.getTenantId());
                digitalStage.setIndustryCode(user.getIndustryCode());

                //新增数字预案任务
                List<DigitalTask> taskList = digitalStage.getTaskList();
                for (DigitalTask digitalTask : taskList) {
                    String taskId = IdUtils.simpleUUID();
                    digitalTask.setTaskId(taskId);
                    digitalTask.setDigitalId(digital.getDigitalId());
                    digitalTask.setPlanId(digitalPlan.getPlanId());
                    digitalTask.setStageId(stageId);
                    digitalTask.setTenantId(user.getTenantId());
                    digitalTask.setIndustryCode(user.getIndustryCode());
                    saveTaskList.add(digitalTask);
                    //资源
                    if (!digitalTask.getResourceList().isEmpty()) {
                        digitalTask.getResourceList().forEach(item -> {
                            EmergencyResourceVO resource = new EmergencyResourceVO();
                            resource.setEventCode(digital.getDigitalId());
                            resource.setStageId(stageId);
                            resource.setTaskId(taskId);
                            resource.setResourceId(item);
                            resource.setTenantId(user.getTenantId());
                            resource.setIndustryCode(user.getIndustryCode());
                            resource.setAppVerCode(user.getAppVerCode());
                            resource.setCreator(operator);
                            saveResourceList.add(resource);
                        });
                    }
                }

            }
            digitalMapper.saveStageBatch(digitalStages);

            if (!saveTaskList.isEmpty()) {
                digitalMapper.saveTaskBatch(saveTaskList);
            }

            if (!saveResourceList.isEmpty()) {
                emergencyResourceService.addEmergencyResource(saveResourceList);
            }
        }

    }

    @Override
    public Digital findById(Digital digital) {
        //查询基础信息
        Digital digitalInfo = digitalMapper.findById(digital);
        if (null == digitalInfo) {
            throw new com.chinatower.platform.common.exception.CustomException("数字预案不存在", 400);
        }
        if (!digitalInfo.getAttachList().isEmpty()) {

            try {
                List<EmergencyAttachVO> attachList = digitalInfo.getAttachList();
                List<String> fileUrlList = attachList.stream().map(EmergencyAttachVO::getOriginUrl).collect(Collectors.toList());
                //文件加密
                Map<String, String> stringStringMap = fileObsFeignClient.encryptBatch(fileUrlList);
                attachList.forEach(item -> {
                    if (stringStringMap.containsKey(item.getOriginUrl())) {
                        item.setOriginUrl(stringStringMap.get(item.getOriginUrl()));
                    }
                });
            } catch (Exception e) {
                log.info("文件加密报错:{}",e);
            }

        }

        //查询计划、阶段、任务
        DigitalPlan digitalPlan;
        if (null != digital.getIsAllReturn() && digital.getIsAllReturn()) {
            digitalPlan = digitalMapper.findAllPlanById(digital);
            if (!digitalPlan.getDigitalStage().isEmpty()) {
                //sql查询用户名
                List<String> userIds = new ArrayList<>();
                //task:[userId1,userId2]
                Map<String, List<SysUser>> taskAndUserNames = new HashMap<>();

                //用户 task 对应关系，循环填用户名
                Map<String, List<String>> userIdTaskMap = new HashMap<>();

                for (DigitalStage digitalStage : digitalPlan.getDigitalStage()) {
                    if (!digitalStage.getTaskList().isEmpty()) {
                        List<DigitalTask> taskList = digitalStage.getTaskList();
                        setUserIdTaskMap(taskList, userIdTaskMap, userIds);
                    }
                }

                log.info("userIds:{}",JSON.toJSONString(userIds));
                setTaskAndUserNames(taskAndUserNames, userIds, userIdTaskMap);

                for (DigitalStage digitalStage : digitalPlan.getDigitalStage()) {
                    if (!digitalStage.getTaskList().isEmpty()) {
                        List<DigitalTask> taskList = digitalStage.getTaskList();
                        taskSetUser(taskList, taskAndUserNames);
                    }
                }
            }
        } else {
            digitalPlan = digitalMapper.findPlanById(digital);
        }
        digitalInfo.setDigitalPlan(digitalPlan);

        return digitalInfo;
    }

    private void taskSetUser(List<DigitalTask> taskList, Map<String, List<SysUser>> taskAndUserNames) {
        if (!taskList.isEmpty()) {
            for (DigitalTask task : taskList) {
                String userIdStr = task.getUserIds();
                if (!StringUtil.isEmpty(userIdStr)) {
                    if (taskAndUserNames.containsKey(task.getTaskId())) {
                        List<SysUser> users = taskAndUserNames.get(task.getTaskId());
                        task.setUsers(users);
                    }
                }
            }
        }
    }

    private void setTaskAndUserNames(Map<String, List<SysUser>> taskAndUserNames, List<String> userIds, Map<String, List<String>> userIdTaskMap) {
        if (!userIds.isEmpty()) {
            List<SysUser> userList = digitalMapper.selectUserByUserIds(userIds);
            userList.forEach(item -> {
                String userId = item.getUserId();
                if (userIdTaskMap.containsKey(userId)) {
                    List<String> taskIds = userIdTaskMap.get(userId);
                    for (String taskId : taskIds) {
                        taskAndUserNames.computeIfAbsent(taskId, k -> new ArrayList<>()).add(item);

                    }
                }
            });
        }
    }

    private void setUserIdTaskMap(List<DigitalTask> taskList, Map<String, List<String>> userIdTaskMap, List<String> userIds) {
        if (!taskList.isEmpty()) {
            for (DigitalTask task : taskList) {
                String userIdStr = task.getUserIds();
                if (!StringUtil.isEmpty(userIdStr)) {

                    //放置  user:[taskId1,taskId2]
                    List<String> list = Arrays.asList(userIdStr.split(","));
                    for (String s : list) {
                        if (userIdTaskMap.containsKey(s)) {
                            List<String> strings = userIdTaskMap.get(s);
                            if (!strings.contains(task.getTaskId())) {
                                strings.add(task.getTaskId());
                                userIdTaskMap.put(s, strings);
                            }
                        } else {
                            List<String> strings = new ArrayList<>();
                            strings.add(task.getTaskId());
                            userIdTaskMap.put(s, strings);
                        }
                    }
                    userIds.addAll(list);
                }

            }
        }
    }

    @Override
    public List<Digital> list(DigitalQueryVO digital) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (digital.getPageNum() == null) {
            digital.setPageNum(1);
        }
        if (digital.getPageSize() == null) {
            digital.setPageSize(10);
        }
        if (digital.getIsTower() == null || digital.getIsTower() == 0) {

            digital.setTenantId(user.getTenantId());
            digital.setIndustryCode(user.getIndustryCode());
        }
        digital.setAcceptNode(user.getPlatformCode());
        PageHelper.startPage(digital.getPageNum(), digital.getPageSize());
        return digitalMapper.list(digital);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBatch(List<String> ids) {
        String operator = SecurityUtils.getOperator();
        digitalMapper.deleteInfo(ids, operator);
        digitalMapper.deletePlan(ids, operator);
        digitalMapper.deleteDeptUser(ids, operator);
        digitalMapper.deleteDept(ids, operator);
        digitalMapper.deleteStage(ids, operator);
        digitalMapper.deleteTask(ids, operator);
        digitalMapper.deleteResource(ids, operator);
    }

    @Override
    public String checkDigitalImportEmergency(Digital digital) {
        //判断部门
        String isValid = emergencyDeptMapper.checkImportDept(digital.getEventCode());
        if (!YES.equals(isValid)) {
            return ZERO_STR;
        }
        //判断用户
        if (emergencyUserMapper.checkImportUser(digital.getEventCode()) > 0) {
            return ZERO_STR;
        }
        //判断阶段
        if (emergencyPlanMapper.checkImportTarget(digital.getEventCode()) > 0) {
            return ZERO_STR;
        }
        return ONE_STR;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void digitalImportEmergency(Digital digital) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //清除原应急预案数据
        deletePlan(digital);
        //导入组别组织数据
        Map<String, String> oldIdNewIdMap = importDept(digital,user);
        //导入用户数据
        importUser(oldIdNewIdMap,user);
        //导入阶段任务资源
        importStageTask(digital,user);
    }

    @Override
    public List<DigitalStage> findStage(DigitalQueryVO digital) {
        PageHelper.startPage(digital.getPageNum(), digital.getPageSize());
        return digitalMapper.findStage(digital);
    }

    @Override
    public List<DigitalTask> findTasksByStage(DigitalQueryVO digital) {

        //sql查询用户名
        List<String> userIds = new ArrayList<>();
        //task:[userId1,userId2]
        Map<String, List<SysUser>> taskAndUserNames = new HashMap<>();


        //用户 task 对应关系，循环填用户名
        Map<String, List<String>> userIdTaskMap = new HashMap<>();

        List<DigitalTask> taskList = digitalMapper.findTasksByStage(digital);


        setUserIdTaskMap(taskList, userIdTaskMap, userIds);

        setTaskAndUserNames(taskAndUserNames, userIds, userIdTaskMap);

        taskSetUser(taskList, taskAndUserNames);
        return taskList;
    }


    private void importStageTask(Digital digital, SysUser user) {
        //查询导入行动计划整体目标
        TargetInfoVO targetInfo = digitalMapper.getPlanByDigitalId(digital.getDigitalId());
        if (targetInfo != null) {
            targetInfo.setEventCode(digital.getEventCode());
            targetInfo.setTenantId(user.getTenantId());
            targetInfo.setIndustryCode(user.getIndustryCode());
            targetInfo.setAppVerCode(user.getAppVerCode());
            targetInfo.setCreator(user.getUserName());
            targetInfo.setGlobalStartDate(getDate(0));
            targetInfo.setGlobalStopDate(getDate(targetInfo.getTargetDays()));
            emergencyPlanMapper.insertTargetInfo(targetInfo);
        }
        //查询导入阶段表
        List<StageInfoVO> stageInfoVOList = digitalMapper.getStageByEventCode(digital.getDigitalId());
        if(!CollectionUtils.isEmpty(stageInfoVOList)){
            Map<String, Integer> stageOldIdNewIdMap = new HashMap<>();
            Map<String, Integer> taskOldIdNewIdMap = new HashMap<>();
            stageInfoVOList.forEach(item->{
                item.setEventCode(digital.getEventCode());
                item.setTenantId(user.getTenantId());
                item.setAppVerCode(user.getAppVerCode());
                item.setIndustryCode(user.getIndustryCode());
                item.setCreator(user.getUserName());
                item.setStageStartDate(getDate(0));
                item.setStageStopDate(getDate(item.getStageDays()));
                item.setStageStatus(ZERO);
            });
            log.info("阶段数据(旧):{}", JSON.toJSONString(stageInfoVOList));
            emergencyPlanMapper.insertStageInfo(stageInfoVOList);
            log.info("阶段数据(新):{}", JSON.toJSONString(stageInfoVOList));
            stageInfoVOList.forEach(item->{
                stageOldIdNewIdMap.put(item.getDigitalStageId(), item.getStageId());
            });
            //查询导入任务表
            List<TaskInfoVO> taskInfoVOList = digitalMapper.getTaskByEventCode(digital.getDigitalId());
            if(!CollectionUtils.isEmpty(taskInfoVOList)){
                taskInfoVOList.forEach(item->{
                    item.setStageId(stageOldIdNewIdMap.get(item.getDigitalStageId()));
                    item.setEventCode(digital.getEventCode());
                    item.setTaskProgress(ZERO);
                    item.setTaskStatus(ZERO);
                    item.setTenantId(user.getTenantId());
                    item.setAppVerCode(user.getAppVerCode());
                    item.setIndustryCode(user.getIndustryCode());
                    item.setCreator(user.getUserName());
                });
                log.info("任务数据(旧):{}", JSON.toJSONString(taskInfoVOList));
                emergencyPlanMapper.insertTaskInfo(taskInfoVOList);
                log.info("任务数据(新):{}", JSON.toJSONString(taskInfoVOList));
                taskInfoVOList.forEach(item->{
                    taskOldIdNewIdMap.put(item.getDigitalTaskId(), item.getTaskId());
                });
                //导入应急资源
                List<EmergencyResourceVO> emergencyResourceList = digitalMapper.getResourceByEventCode(digital.getDigitalId());
                if(!CollectionUtils.isEmpty(emergencyResourceList)){
                    emergencyResourceList.forEach(item->{
                        item.setTaskId(String.valueOf(taskOldIdNewIdMap.get(item.getTaskId())));
                        item.setStageId(String.valueOf(stageOldIdNewIdMap.get(item.getStageId())));
                        item.setEventCode(digital.getEventCode());
                        item.setTenantId(user.getTenantId());
                        item.setAppVerCode(user.getAppVerCode());
                        item.setIndustryCode(user.getIndustryCode());
                        item.setCreator(user.getUserName());
                    });
                    emergencyResourceService.addEmergencyResource(emergencyResourceList);
                }
            }
        }
    }

    private String getDate(int day) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedCurrentDate = currentDate.format(formatter);
        if(day == 0){
            return formattedCurrentDate;
        }else{
            LocalDate threeDaysLater = currentDate.plusDays(day - 1);
            return threeDaysLater.format(formatter);
        }
    }

    private void importUser(Map<String, String> oldIdNewIdMap,SysUser user) {
        List<String> keysList = new ArrayList<>(oldIdNewIdMap.keySet());
        List<EmergencyUserDTO> emergencyUserList = emergencyUserMapper.getUserByDeptId(keysList);
        if(!CollectionUtils.isEmpty(emergencyUserList)){
            for (EmergencyUserDTO emergencyUserDTO : emergencyUserList) {
                emergencyUserDTO.setEmergencyDeptId(oldIdNewIdMap.get(emergencyUserDTO.getEmergencyDeptId()));
                emergencyUserDTO.setTenantId(user.getTenantId());
                emergencyUserDTO.setAppVerCode(user.getAppVerCode());
                emergencyUserDTO.setIndustryCode(user.getIndustryCode());
                emergencyUserDTO.setCreator(user.getUserName());
            }
            emergencyUserMapper.batchSave(emergencyUserList);
        }
    }

    private Map<String, String> importDept(Digital digital,SysUser user) {
        //查询所有数据
        List<DigitalDeptUserTreeVO> emergencyDeptDTO = emergencyDeptMapper.getDeptByEventCode(digital.getDigitalId());
        Map<Integer, List<DigitalDeptUserTreeVO>> collect = emergencyDeptDTO.stream().collect(Collectors.groupingBy(DigitalDeptUserTreeVO::getDeptLevel));
        Map<String, String> oldIdNewIdMap = new HashMap<>();
        Map<String, String> parentIdAncestors = new HashMap<>();
        for (Map.Entry<Integer, List<DigitalDeptUserTreeVO>> entry : collect.entrySet()) {
            List<DigitalDeptUserTreeVO> value = entry.getValue();
            for (DigitalDeptUserTreeVO item : value) {
                setParentId(oldIdNewIdMap, parentIdAncestors, item);
                item.setEventCode(digital.getEventCode());
                item.setTenantId(user.getTenantId());
                item.setAppVerCode(user.getAppVerCode());
                item.setIndustryCode(user.getIndustryCode());
                item.setCreator(user.getUserName());
            }
            log.info("部门数据(旧):{}", JSON.toJSONString(value));
            emergencyDeptMapper.batchSave(value);
            log.info("部门数据(新):{}", JSON.toJSONString(value));
            value.forEach(item->{
                //赋值id
                putOldIdNewId(oldIdNewIdMap, parentIdAncestors, item);
            });
        };
        return oldIdNewIdMap;
    }

    private void putOldIdNewId(Map<String, String> oldIdNewIdMap, Map<String, String> parentIdAncestors, DigitalDeptUserTreeVO item) {
        oldIdNewIdMap.put(item.getDeptId(), item.getId());
        if (parentIdAncestors.containsKey(item.getParentId())) {
            String ancestors = parentIdAncestors.get(item.getParentId()) + COMMA + item.getParentId();
            parentIdAncestors.put(item.getId(), ancestors);
        } else {
            parentIdAncestors.put(item.getId(), item.getParentId());
        }
    }

    private void setParentId(Map<String, String> oldIdNewIdMap, Map<String, String> parentIdAncestors, DigitalDeptUserTreeVO item) {
        if (!"0".equals(item.getParentId())) {
            if (oldIdNewIdMap.containsKey(item.getParentId())) {
                String realParentId = oldIdNewIdMap.get(item.getParentId());
                item.setParentId(realParentId);
                item.setAncestors(parentIdAncestors.get(item.getParentId()) + COMMA + item.getParentId());
            }
        } else {
            item.setAncestors("0");
        }
    }

    private void deletePlan(Digital digital) {
        List<String> eventCodes = new ArrayList<>();
        eventCodes.add(digital.getEventCode());
        // 查出世间任务ID作为删除反馈的传参
        TaskInfoReq taskInfoReq = new TaskInfoReq();
        taskInfoReq.setEventCodes(eventCodes);
        List<TaskInfoVO> taskInfos = emergencyPlanMapper.getTaskList(taskInfoReq);
        List<Integer> taskIdList = taskInfos.stream().map(TaskInfoVO::getTaskId).collect(Collectors.toList());
        // 删除部门用户
        emergencyDeptService.clearDeptUserInfo(eventCodes);
        // 删除事件阶段
        emergencyPlanMapper.deleteStageInfo(eventCodes);
        emergencyPlanMapper.deleteTargetInfo(eventCodes);
        // 删除事件阶段
        List<String> taskCodes = taskIdList.stream().map(String::valueOf).collect(Collectors.toList());
        // 删除未下发的任务
        emergencyPlanMapper.deleteTaskList(eventCodes);
        // 将已下发的任务状态置为已中止
        emergencyPlanMapper.updateTaskStatus(eventCodes);
        // 删除任务附件
        /*if(!CollectionUtils.isEmpty(taskCodes)) {
            attachService.deleteEmergencyAttach(new EmergencyAttachVO(null, Constant.BUSINESS_TYPE_TASK, taskCodes));
        }
        if (!CollectionUtils.isEmpty(taskIdList)) {
            FeedbackInfoVO feedbackInfoVO = new FeedbackInfoVO();
            feedbackInfoVO.setTaskIdList(taskIdList);
            List<FeedbackInfoVO> feedbackInfos = emergencyPlanMapper.getFeedbackList(feedbackInfoVO);
            List<String> feedbackCodes = feedbackInfos.stream().map(feedbackInfo -> String.valueOf(feedbackInfo.getId())).collect(Collectors.toList());
            // 删除事件反馈
            emergencyPlanMapper.deleteFeedbackInfoVO(taskIdList);
            if(!CollectionUtils.isEmpty(feedbackCodes)) {
                // 删除事件反馈附件
                attachService.deleteEmergencyAttach(new EmergencyAttachVO(null, Constant.BUSINESS_TYPE_FEEDBACK, feedbackCodes));
            }
        }*/
        // 更新资源标识
        EmergencyResourceVO resourceVO = new EmergencyResourceVO();
        resourceVO.setEventCodes(eventCodes);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        resourceVO.setTenantId(user.getTenantId());
        resourceVO.setIndustryCode(user.getIndustryCode());
        //emergencyResourceServiceImpl.deleteEmergencyResourceByEventCodes(resourceVO);
        emergencyResourceServiceImpl.updateResourceStatus(resourceVO);
    }
}
