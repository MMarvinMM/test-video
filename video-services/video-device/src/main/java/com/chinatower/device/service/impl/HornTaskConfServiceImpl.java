package com.chinatower.device.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.VmHornContentConfDTO;
import com.chinatower.device.entity.dto.VmHornTaskConfDO;
import com.chinatower.device.entity.dto.VmHornTaskDeviceRelDO;
import com.chinatower.device.entity.dto.VmHornTaskDiffNodeRelDO;
import com.chinatower.device.entity.dto.VmHornTaskStrategyConfDO;
import com.chinatower.device.entity.dto.VmHornTaskContentRelDO;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.device.entity.vo.HornTaskConfVO;
import com.chinatower.device.entity.vo.HornTaskStrategyConfVO;
import com.chinatower.device.entity.vo.HornTimeInfo;
import com.chinatower.device.entity.vo.TalkResultVO;
import com.chinatower.device.enums.HornCommonEnum;
import com.chinatower.device.mapper.HornDeviceMapper;
import com.chinatower.device.mapper.HornTaskConfMapper;
import com.chinatower.device.mapper.VmHornContentMapper;
import com.chinatower.device.mongodb.HornTaskMongoDAO;
import com.chinatower.device.mongodb.dto.AlgorithmHornTaskDTO;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.device.service.IHornTaskConfService;
import com.chinatower.device.utils.HornRequestUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.ICasCadeInfoService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 广播任务业务层
 */
@Service
@Slf4j
public class HornTaskConfServiceImpl implements IHornTaskConfService {


    private static final String TASK_TYPE = "TIMING_TASK";

    private static final String TASK_NAME_EXIST = "任务名称已存在";

    private static final String SYNC_ALGORITHM_MONGO_FAIL = "同步算法仓失败:";

    @Autowired
    private HornRequestUtils hornRequestUtils;

    @Resource
    private HornTaskConfMapper hornTaskConfMapper;

    @Resource
    private VmHornContentMapper vmHornContentMapper;

    @Autowired
    private ICasCadeInfoService iCasCadeInfoService;

    @Autowired
    private HornDeviceMapper hornDeviceMapper;

    @Autowired
    private HornTaskMongoDAO hornTaskMongoDAO;

    @Autowired
    private IHornDeviceRepository iHornDeviceRepository;

    String TASK_ID = "taskId";

    String ALGORITHM_HORN_TASK_INFO = "algorithm_horn_task_info";

    @Override
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code")
    public List<HornTaskConfVO> getListByConditionPage(HornTaskConfVO hornTaskConfVO) {
        if (Objects.isNull(hornTaskConfVO.getPageNum())) {
            hornTaskConfVO.setPageNum(Constants.ONE_NUM);
        }
        if (Objects.isNull(hornTaskConfVO.getPageSize())) {
            hornTaskConfVO.setPageSize(Constants.TEN_NUM);
        }
        PageHelper.startPage(hornTaskConfVO.getPageNum(), hornTaskConfVO.getPageSize());
        return hornTaskConfMapper.getListByCondition(hornTaskConfVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addHornTask(HornTaskConfVO hornTaskConfVO, SysUser currentUser) {
        VmHornTaskConfDO existTask = hornTaskConfMapper.selectTaskByName(hornTaskConfVO.getTaskName(),
                currentUser.getTenantId(), currentUser.getIndustryCode());
        //任务名称已存在则不允许新增
        if (!HornCommonEnum.OptType.OPT_TYPE_0.getOptTypeValue().equals(hornTaskConfVO.getHornOptType())
                && !Objects.isNull(existTask) ) {
            return Result.fail(TASK_NAME_EXIST);
        }
        if (HornCommonEnum.OptType.OPT_TYPE_1.getOptTypeValue().equals(hornTaskConfVO.getHornOptType())
                || HornCommonEnum.OptType.OPT_TYPE_2.getOptTypeValue().equals(hornTaskConfVO.getHornOptType())) {
            saveHornTask(hornTaskConfVO, currentUser);
        }
        if (HornCommonEnum.OptType.OPT_TYPE_0.getOptTypeValue().equals(hornTaskConfVO.getHornOptType())
                || HornCommonEnum.OptType.OPT_TYPE_2.getOptTypeValue().equals(hornTaskConfVO.getHornOptType())) {
            playRightNow(hornTaskConfVO, currentUser);
        }
        return Result.success("操作成功");
    }

    @Override
    public Result<String> addHornTaskV2(HornTaskConfVO hornTaskConfVO) {
        if (hornTaskConfVO == null
                || StringUtils.isEmpty(hornTaskConfVO.getTenantId())
                || StringUtils.isEmpty(hornTaskConfVO.getIndustryCode())) {
            throw new CustomException("必填参数不能为空");
        }
        SysUser currentUser = getSysUser(hornTaskConfVO);
        return addHornTask(hornTaskConfVO, currentUser);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addAndStartHornTask(HornTaskConfVO hornTaskConfVO) {
        if (hornTaskConfVO == null
                || StringUtils.isEmpty(hornTaskConfVO.getTenantId())
                || StringUtils.isEmpty(hornTaskConfVO.getIndustryCode())
                || CollectionUtils.isEmpty(hornTaskConfVO.getVmHornTaskContentRelList())) {
            throw new CustomException("必填参数不能为空");
        }

        List<Long> contentIdList = hornTaskConfVO.getVmHornTaskContentRelList().stream().map(VmHornTaskContentRelDO::getContentId).collect(Collectors.toList());
        // vm_horn_content_conf
        int notApprovedContentCount = vmHornContentMapper.countNotApprovedContent(contentIdList);
        if (notApprovedContentCount > 0) {
            throw new CustomException("有未审核通过的内容，不允许新增任务");
        }
        // 组装登录信息
        SysUser currentUser = getSysUser(hornTaskConfVO);

        VmHornTaskConfDO existTask = hornTaskConfMapper.selectTaskByName(hornTaskConfVO.getTaskName(),
                currentUser.getTenantId(), currentUser.getIndustryCode());
        //任务名称已存在则不允许新增
        if (!HornCommonEnum.OptType.OPT_TYPE_0.getOptTypeValue().equals(hornTaskConfVO.getHornOptType())
                && !Objects.isNull(existTask) ) {
            return Result.fail(TASK_NAME_EXIST);
        }
        Long taskId = saveHornTask(hornTaskConfVO, currentUser);
        HornTaskConfVO playParam = new HornTaskConfVO();
        playParam.setId(taskId);
        // 立即播放
        Result<String> playResult = playRightNowLogic(playParam, currentUser);
        if (playResult == null || !Objects.equals(ResultCode.SUCCESS.getCode(), playResult.getCode())) {
            return playResult;
        }
        return Result.success(taskId != null ? String.valueOf(taskId) : null);
    }

    private SysUser getSysUser(HornTaskConfVO hornTaskConfVO) {
        SysUser currentUser = new SysUser();
        currentUser.setTenantId(hornTaskConfVO.getTenantId());
        currentUser.setIndustryCode(hornTaskConfVO.getIndustryCode());
        // 默认1.0
        currentUser.setAppVerCode("1.0");
        // 乡镇、交通sdk调用
        currentUser.setUserName("system-sdk");
        Result<String> platformCodeResult = iCasCadeInfoService.getCurrentPlatformCode();
        currentUser.setPlatformCode(platformCodeResult.getData());
        currentUser.setAreaCode(DeviceConstants.TOWER_ROOT_AREA);
        return currentUser;
    }

    private void saveHornTaskRelInfo(HornTaskConfVO hornTaskConfVO, Long taskId) {
        //2.批量新增喇叭任务内容关联表
        hornTaskConfMapper.batchInsertContentRel(hornTaskConfVO.getVmHornTaskContentRelList(), taskId);
        //3.新增喇叭任务设备关联表
        hornTaskConfMapper.batchInsertDeviceRel(hornTaskConfVO.getDeviceCodeList(), taskId);
        //4.新增喇叭任务管理策略配置表
        //组装入库
        HornTaskStrategyConfVO hornTaskStrategyConfVO = hornTaskConfVO.getStrategyConf();
        List<HornTimeInfo> timeInfos = hornTaskStrategyConfVO.getTimeInfo();
        List<VmHornTaskStrategyConfDO> vmHornTaskConfDOs = new ArrayList<>(timeInfos.size());
        timeInfos.forEach(timeInfo -> {
            VmHornTaskStrategyConfDO vmHornTaskStrategyConfDO = new VmHornTaskStrategyConfDO();
            BeanUtils.copyProperties(hornTaskStrategyConfVO, vmHornTaskStrategyConfDO);
            vmHornTaskStrategyConfDO.setCycleDate(JSONObject.toJSONString(hornTaskStrategyConfVO.getCycleDate()));
            vmHornTaskStrategyConfDO.setStartTime(timeInfo.getStartTime());
            vmHornTaskStrategyConfDO.setEndTime(timeInfo.getEndTime());
            vmHornTaskConfDOs.add(vmHornTaskStrategyConfDO);
        });
        hornTaskConfMapper.batchInsertStrategyRel(vmHornTaskConfDOs, taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> modifyHornTask(HornTaskConfVO hornTaskConfVO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        HornTaskConfVO reqParam = new HornTaskConfVO();
        reqParam.setId(hornTaskConfVO.getId());
        VmHornTaskConfDO vmHornTaskConfDO = hornTaskConfMapper.selectByTaskId(hornTaskConfVO.getId());
        if (Objects.isNull(vmHornTaskConfDO)) {
            return Result.fail("任务不存在，修改失败！");
        }
        SysUser currentUser = loginUser.getUser();

        //任务名称有变化的情况下任务名称已存在则不允许修改
        if (!StringUtils.equals(hornTaskConfVO.getTaskName(), vmHornTaskConfDO.getTaskName()) ){
            VmHornTaskConfDO existTask = hornTaskConfMapper.selectTaskByName(hornTaskConfVO.getTaskName(),
                    currentUser.getTenantId(), currentUser.getIndustryCode());
            if (!Objects.isNull(existTask)) {
                return Result.fail(TASK_NAME_EXIST);
            }
        }

        hornTaskConfVO.setTenantId(currentUser.getTenantId());
        hornTaskConfVO.setIndustryCode(currentUser.getIndustryCode());
        hornTaskConfVO.setAppVerCode(currentUser.getAppVerCode());
        hornTaskConfVO.setModifier(currentUser.getUserName());
        //2. 修改喇叭任务内容关联表 需要更新操作其余先删后插入
        hornTaskConfMapper.modifyTaskConf(hornTaskConfVO);
        hornTaskConfMapper.deleteContentRelByTaskId(hornTaskConfVO.getId());
        hornTaskConfMapper.deleteDeviceRelByTaskId(hornTaskConfVO.getId());
        hornTaskConfMapper.deleteStrategyRelByTaskId(hornTaskConfVO.getId());
        saveHornTaskRelInfo(hornTaskConfVO, hornTaskConfVO.getId());
        try {
            syncTaskInfoToAlgorithmMongo(hornTaskConfVO.getId(), null, null);
        }
        catch (Exception e) {
            log.error(SYNC_ALGORITHM_MONGO_FAIL, e);
        }
        return Result.success("修改成功");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteHornTaskV2(HornTaskConfVO hornTaskConfVO) {
        return deleteHornTaskLogic(hornTaskConfVO, "system-sdk");
    }

    private Result<String> deleteHornTaskLogic(HornTaskConfVO hornTaskConfVO, String userName) {
        HornTaskConfVO reqParam = new HornTaskConfVO();
        reqParam.setId(hornTaskConfVO.getId());
        VmHornTaskConfDO vmHornTaskConfDO = hornTaskConfMapper.selectByTaskId(hornTaskConfVO.getId());
        if (Objects.isNull(vmHornTaskConfDO)) {
            return Result.fail("任务不存在！");
        }
        if (HornCommonEnum.OperatorType.OPERATOR_TYPE_1.getOperatorTypeValue().equals(vmHornTaskConfDO.getOperatorType())) {
            return Result.fail("任务处于开始状态，不允许删除！");
        }
        vmHornTaskConfDO.setModifier(userName);
        vmHornTaskConfDO.setUpdateTime(new Date());
        vmHornTaskConfDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_EXCEPTION_INT);
        hornTaskConfMapper.updateByTaskId(vmHornTaskConfDO);
        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteHornTask(HornTaskConfVO hornTaskConfVO) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();
        return deleteHornTaskLogic(hornTaskConfVO, currentUser.getUserName());
    }

    @Override
    public Result<String> stopHornTask(HornTaskConfVO hornTaskConfVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();
        VmHornTaskConfDO vmHornTaskConfDO = hornTaskConfMapper.selectByTaskId(hornTaskConfVO.getId());
        if (Objects.isNull(vmHornTaskConfDO)) {
            return Result.fail("任务不存在！");
        }
        List<VmHornTaskDiffNodeRelDO> vmHornTaskDiffNodeRelDOList = hornTaskConfMapper.selectDiffNodeRelDOsByTaskId(hornTaskConfVO.getId());

        ThreadParallelUtil.run(vmHornTaskDiffNodeRelDOList, relDo -> this.callHornSystemForDiffNodeDelete(relDo, currentUser),
                ThreadParallelUtil.ConfigFactory.HORN_TASK);

        vmHornTaskDiffNodeRelDOList = hornTaskConfMapper.selectDiffNodeRelDOsByTaskId(hornTaskConfVO.getId());
        boolean isNotALLStop = vmHornTaskDiffNodeRelDOList.stream().anyMatch(relDO ->
                Objects.equals(HornCommonEnum.ExecutStatus.EXECUT_STATUS_1.getExecutStatusValue(), relDO.getExecutStatus()));
        if (isNotALLStop) {
            return Result.fail("部分停止成功");
        }

        //第三步如果是文本任务，调用删除文本接口
        if (HornCommonEnum.BroadcastType.BROADCAST_1.getTaskType().equals(vmHornTaskConfDO.getTaskType())) {
            deleteHornTxt(hornTaskConfVO, currentUser);
        }

        //删除关联表 移动到历史表
        hornTaskConfMapper.moveDiffNodeRelToHis(hornTaskConfVO.getId(), IdUtils.fastUUID());
        hornTaskConfMapper.deleteDiffNodeRelByTaskId(hornTaskConfVO.getId());

        vmHornTaskConfDO.setOperator(currentUser.getUserName());
        vmHornTaskConfDO.setOperateTime(new Date());
        vmHornTaskConfDO.setOperatorType(HornCommonEnum.OperatorType.OPERATOR_TYPE_2.getOperatorTypeValue());
        vmHornTaskConfDO.setStartupStatus(HornCommonEnum.StartupStatus.STARTUP_STATUS_0.getStartupStatusValue());
        vmHornTaskConfDO.setExecutStatus(HornCommonEnum.ExecutStatus.EXECUT_STATUS_0.getExecutStatusValue());
        vmHornTaskConfDO.setHornTaskId("");
        hornTaskConfMapper.updateByTaskId(vmHornTaskConfDO);

        try {
            syncTaskInfoToAlgorithmMongo(hornTaskConfVO.getId(), null, null);
        }
        catch (Exception e) {
            log.error(SYNC_ALGORITHM_MONGO_FAIL, e);
        }
        return Result.success("结束成功");
    }

    @Override
    public Result<HornTaskConfVO> taskDetail(HornTaskConfVO hornTaskConfVO) {
        VmHornTaskConfDO vmHornTaskConfDO = hornTaskConfMapper.selectByTaskId(hornTaskConfVO.getId());
        if (Objects.isNull(vmHornTaskConfDO)) {
            return Result.fail("任务不存在");
        }
        HornTaskConfVO result = new HornTaskConfVO();
        BeanUtils.copyProperties(vmHornTaskConfDO, result);
        result.setVmHornTaskContentRelList(hornTaskConfMapper.selectContentRelByTaskId(hornTaskConfVO.getId()));
        List<VmHornTaskDeviceRelDO> deviceRelDOS = hornTaskConfMapper.selectDeviceRelByTaskId(hornTaskConfVO.getId());
        result.setDeviceCodeList(deviceRelDOS.stream().map(VmHornTaskDeviceRelDO::getDeviceCode).collect(Collectors.toList()));
        List<VmHornTaskStrategyConfDO> vmHornTaskStrategyConfDOS = hornTaskConfMapper.selectStrategyRelByTaskId(hornTaskConfVO.getId());
        HornTaskStrategyConfVO strategyConf = new HornTaskStrategyConfVO();
        List<HornTimeInfo> timeInfoList = new ArrayList<>(vmHornTaskStrategyConfDOS.size());
        if (CollectionUtil.isNotEmpty(vmHornTaskStrategyConfDOS)) {
            BeanUtils.copyProperties(vmHornTaskStrategyConfDOS.get(0),strategyConf);
            strategyConf.setCycleDate(JSONObject.parseObject(vmHornTaskStrategyConfDOS.get(0).getCycleDate(), List.class));
            vmHornTaskStrategyConfDOS.forEach(item -> {
                HornTimeInfo timeInfo = new HornTimeInfo();
                timeInfo.setStartTime(item.getStartTime());
                timeInfo.setEndTime(item.getEndTime());
                timeInfoList.add(timeInfo);
            });
            strategyConf.setTimeInfo(timeInfoList);
        }
        result.setStrategyConf(strategyConf);
        return Result.data(result, "查询成功");
    }

    @Override
    public Result<String> playRightNow(HornTaskConfVO hornTaskConfVO, SysUser currentUser) {
        return playRightNowLogic(hornTaskConfVO, currentUser);
    }

    private Result<String> playRightNowLogic(HornTaskConfVO hornTaskConfVO, SysUser currentUser) {
        //不为空说明是工作台任务管理发起的播放 为空就是大屏 app 以及喇叭管理发起的立即播放
        if (!Objects.isNull(hornTaskConfVO.getId())) {
            Result<HornTaskConfVO> exitsVoResult = this.taskDetail(hornTaskConfVO);
            HornTaskConfVO exitsVo = exitsVoResult.getData();
            callHornPlayRightNow(exitsVo, currentUser);
            VmHornTaskConfDO vmHornTaskConfDO = hornTaskConfMapper.selectByTaskId(hornTaskConfVO.getId());
            vmHornTaskConfDO.setOperator(currentUser.getUserName());
            vmHornTaskConfDO.setOperateTime(new Date());
            hornTaskConfMapper.updateByTaskId(vmHornTaskConfDO);
        }
        else {
            callHornPlayRightNow(hornTaskConfVO, currentUser);
        }
        return Result.success("执行成功");
    }

    @Override
    public Result<String> callHornTaskPlay(List<String> fileIdList) {
        log.info("callHornTaskPlay fileIdList:{}", JSON.toJSONString(fileIdList));
        if (!CollectionUtils.isNotEmpty(fileIdList)) {
            return Result.success("执行成功");
        }
        List<HornTaskPlayParamDTO> hornTaskPlayParamList = hornTaskConfMapper.selectTaskPlayParam(fileIdList);
        for (HornTaskPlayParamDTO playParam : hornTaskPlayParamList) {
            HornTaskNorthVO hornTaskNorthVO = JSON.parseObject(playParam.getParamJson(), HornTaskNorthVO.class);
            String accessNode = hornTaskNorthVO.getAccessNode();
            try {
                //能走到这里肯定是文本任务 取第一个
                List<TaskContentWithStrategyInfo> oldList = hornTaskNorthVO.getTaskContentWithStrategyInfoList();
                List<TaskContentWithStrategyInfo> newList = new ArrayList<>(oldList.size());
                for (TaskContentWithStrategyInfo item : oldList) {
                    item.setMd5(playParam.getFileMd5());
                    item.setResourceUrl(playParam.getFileUrl());
                    item.setTrackLength(playParam.getFileDuration());
                    newList.add(item);
                }
                hornTaskNorthVO.setTaskContentWithStrategyInfoList(newList);
                hornTaskNorthVO.setAccessNode(null);
                List<String> deviceCodeList = hornTaskNorthVO.getRangeInfoList().get(0).getRangeDevInfoList().stream().map(RangeDevInfo::getDeviceCode).collect(Collectors.toList());
                TalkResultVO<HornAddTimingInfRspVO> result = hornRequestUtils.callHornSystemDiffNode(hornTaskNorthVO, NetConnectUtils.RequestParamter.URLMappingEnum.ADD_NODE_TIMING, HornAddTimingInfRspVO.class, accessNode, deviceCodeList);
                if (!Objects.isNull(playParam.getTaskId())) {
                    hornTaskConfMapper.updateHornTaskDiffNodeHornTaskId(playParam.getTaskId(), result.getData().getTaskId(), accessNode);
                }
            } catch (Exception e) {
                if (!Objects.isNull(playParam.getTaskId())) {
                    hornTaskConfMapper.saveFailReason(playParam.getTaskId(), e.getMessage(), accessNode);
                }
                log.error("callTaskPlay error fileId:{}, e:", playParam.getFileId(), e);
            }
        }
        deleteTaskPlayParam(fileIdList);
        try {
            for (HornTaskPlayParamDTO playParam : hornTaskPlayParamList) {
                syncTaskInfoToAlgorithmMongo(playParam.getTaskId(), playParam.getFileUrl(), playParam.getFileId());
            }
        }
        catch (Exception e) {
            log.error(SYNC_ALGORITHM_MONGO_FAIL, e);
        }
        return Result.success("执行成功");
    }

    @Override
    public Result<String> deleteTaskPlayParam(List<String> fileIdList) {
        hornTaskConfMapper.deleteTaskPlayParam(fileIdList);
        return Result.success("执行成功");
    }

    @Override
    @Async
    public void syncTaskInfoToAlgorithmMongo(Long taskId, String txtFileUrl, String fileId) {
        if (Objects.isNull(taskId)) {
            return;
        }
        VmHornTaskConfDO vmHornTaskConfDO = hornTaskConfMapper.selectById(taskId);
        if (Objects.isNull(vmHornTaskConfDO)) {
            return;
        }
        AlgorithmHornTaskDTO mongoData = new AlgorithmHornTaskDTO();
        mongoData.setTaskId(vmHornTaskConfDO.getId());
        mongoData.setTaskType(vmHornTaskConfDO.getTaskType());
        mongoData.setIndustryCode(vmHornTaskConfDO.getIndustryCode());
        SysIndustryConf industry = hornTaskConfMapper.getByIndustryCode(vmHornTaskConfDO.getIndustryCode());
        if (Objects.nonNull(industry)) {
            mongoData.setIndustryName(industry.getIndustryName());
        }
        List<AlgorithmHornContentVO> contents = assembleHornContent(vmHornTaskConfDO, txtFileUrl, fileId);
        List<AlgorithmHornInfoVO> deviceInfos = assembleHornDeviceInfo(vmHornTaskConfDO);
        mongoData.setContentInfos(contents);
        mongoData.setDeviceInfos(deviceInfos);
        String acceptNode = hornRequestUtils.getCurrentLocationCode();
        mongoData.setAcceptNode(acceptNode);
        mongoData.setCreateTime(vmHornTaskConfDO.getCreateTime());
        mongoData.setUpdateTime(new Date());
        mongoData.setStartupStatus(vmHornTaskConfDO.getStartupStatus());
        hornTaskMongoDAO.insert(mongoData, MongoDBType.PRIMARY);
    }

    @Override
    public Result<String> startTask(HornTaskConfVO hornTaskConfVO) {
        VmHornTaskConfDO vmHornTaskConfDO = hornTaskConfMapper.selectByTaskId(hornTaskConfVO.getId());
        if (Objects.isNull(vmHornTaskConfDO)) {
            return Result.fail("任务不存在");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        // 如果第一次启动全部失败，则删一边
        hornTaskConfMapper.deleteDiffNodeRelByTaskId(hornTaskConfVO.getId());

        Result<HornTaskConfVO> result = this.taskDetail(hornTaskConfVO);
        hornTaskConfVO = result.getData();
        List<HornTaskNorthVO> hornTaskNorthVOList = assembleHornTaskToHornSystem(hornTaskConfVO, currentUser,false, false);
        if (HornCommonEnum.BroadcastType.BROADCAST_1.getTaskType().equals(hornTaskConfVO.getTaskType())) {
            hornTaskConfMapper.setHornContentRelTxtIdByTaskId(hornTaskConfVO.getId(), hornTaskConfVO.getFileId());
        }
        List<HornTaskAddRspVO> addRsp = callHornSystemForDiffNodePlay(hornTaskNorthVOList, hornTaskConfVO, hornTaskConfVO.getFileId(), false);
        if (HornCommonEnum.BroadcastType.BROADCAST_0.getTaskType().equals(hornTaskConfVO.getTaskType())) {
            boolean isAnyStart = addRsp.stream().anyMatch(vo -> StringUtil.isNotEmpty(vo.getHornTaskId()));
            if (!isAnyStart) {
                String failReason = dealFailReason(addRsp);
                return Result.fail(failReason);
            }
        }

        vmHornTaskConfDO.setOperator(currentUser.getUserName());
        vmHornTaskConfDO.setOperateTime(new Date());
        vmHornTaskConfDO.setOperatorType(HornCommonEnum.OperatorType.OPERATOR_TYPE_1.getOperatorTypeValue());
        vmHornTaskConfDO.setStartupStatus(HornCommonEnum.StartupStatus.STARTUP_STATUS_1.getStartupStatusValue());
        hornTaskConfMapper.updateByTaskId(vmHornTaskConfDO);

        try {
            if (HornCommonEnum.BroadcastType.BROADCAST_0.getTaskType().equals(hornTaskConfVO.getTaskType())) {
                syncTaskInfoToAlgorithmMongo(hornTaskConfVO.getId(), null, null);
            }
        }
        catch (Exception e) {
            log.error(SYNC_ALGORITHM_MONGO_FAIL, e);
        }
        return Result.success("执行成功");
    }


    @Override
    public Result<String> syncOldTaskToMongo(HornTaskConfVO hornTaskConfVO) {
        List<HornTaskConfVO> data = hornTaskConfMapper.getAllHornTask(hornTaskConfVO);
        List<Long> idList = data.stream().map(HornTaskConfVO::getId).collect(Collectors.toList());
        for (Long id : idList) {
            syncTaskInfoToAlgorithmMongo(id, null, null);
        }
        return Result.success("执行成功");
    }

    private String dealFailReason(List<HornTaskAddRspVO> addRsp) {
        String failReason = "任务启动失败";
        if (CollectionUtils.isNotEmpty(addRsp)) {
            boolean isTimePass = addRsp.stream().filter(vo -> StringUtil.isNotEmpty(vo.getPushFailReason())).
                    anyMatch(vo -> (vo.getPushFailReason().contains("将来或现在的时间") || vo.getPushFailReason().contains("小于当前")
                            || vo.getPushFailReason().contains("过期")));
            if (isTimePass) {
                failReason = "广播任务已过期";
            }
            else {
                long count = addRsp.stream().map(HornTaskAddRspVO::getPushFailReason).distinct().count();
                if (count == 1) {
                    failReason = addRsp.get(0).getPushFailReason();
                }
            }
        }
        return failReason;
    }



    /**
     * 为不同的节点调用广播系统
     *
     * @param hornTaskNorthVOList 北向任务列表
     * @param hornTaskConfVO     任务配置信息
     * @param fileId            文本任务存临时表用
     */
    private List<HornTaskAddRspVO> callHornSystemForDiffNodePlay(List<HornTaskNorthVO> hornTaskNorthVOList, HornTaskConfVO hornTaskConfVO, String fileId, Boolean isPlayRightNow) {
        List<HornTaskAddRspVO> result = Lists.newArrayList();
        if (!isPlayRightNow) {
            //先保存
            List<VmHornTaskDiffNodeRelDO>  insertList = new ArrayList<>(hornTaskNorthVOList.size());
            hornTaskNorthVOList.forEach(item -> {
                VmHornTaskDiffNodeRelDO vmHornTaskDiffNodeRelDO = new VmHornTaskDiffNodeRelDO();
                vmHornTaskDiffNodeRelDO.setAccessNode(item.getAccessNode());
                insertList.add(vmHornTaskDiffNodeRelDO);
            });
            hornTaskConfMapper.saveHornTaskDiffNodeRelBatch(insertList, hornTaskConfVO.getId());
        }
        //因为文转语需要时间 文本类型走临时表等待kafka回调后拼接url和md5字段
        if (HornCommonEnum.BroadcastType.BROADCAST_1.getTaskType().equals(hornTaskConfVO.getTaskType())) {
            saveTaskPlayParam(fileId, hornTaskNorthVOList, hornTaskConfVO.getId());
        }
        //文件任务正常调用
        else {
            //多线程处理
            result = ThreadParallelUtil.run(hornTaskNorthVOList, vo -> specialDealAddTimingRsp(vo, hornTaskConfVO, isPlayRightNow),
                    ThreadParallelUtil.ConfigFactory.HORN_TASK);
        }
        return result;
    }

    /**
     * 特殊处理创建任务返回结果
     * @param hornTaskNorthVO 接口信息
     * @return 创建任务调用广播返回结果（包含接入节点）
     */
    public HornTaskAddRspVO specialDealAddTimingRsp(HornTaskNorthVO hornTaskNorthVO, HornTaskConfVO hornTaskConfVO, Boolean isPlayRightNow) {
        String accessNode = hornTaskNorthVO.getAccessNode();
        HornTaskAddRspVO item = new HornTaskAddRspVO();
        item.setTaskId(hornTaskConfVO.getId());
        item.setAccessNode(accessNode);
        try {
            hornTaskNorthVO.setAccessNode(null);
            List<String> deviceCodeList = hornTaskNorthVO.getRangeInfoList().get(0).getRangeDevInfoList().stream().map(RangeDevInfo::getDeviceCode).collect(Collectors.toList());
            TalkResultVO<HornAddTimingInfRspVO> rsp = hornRequestUtils.callHornSystemDiffNode(hornTaskNorthVO, NetConnectUtils.RequestParamter.URLMappingEnum.ADD_NODE_TIMING, HornAddTimingInfRspVO.class, accessNode, deviceCodeList);
            if (!isPlayRightNow) {
                int result = hornTaskConfMapper.updateHornTaskDiffNodeHornTaskId(hornTaskConfVO.getId(), rsp.getData().getTaskId(), accessNode);
                log.info("specialDealAddTimingRsp taskId:{}, hornTaskId:{}, accessNode:{} result:{}", hornTaskConfVO.getId(), rsp.getData().getTaskId(), accessNode, result);
            }
            item.setHornTaskId(rsp.getData().getTaskId());
        }
        catch (Exception e) {
            if (!isPlayRightNow) {
                hornTaskConfMapper.saveFailReason(hornTaskConfVO.getId(), e.getMessage(), accessNode);
            }
            log.error("节点{}，创建广播任务失败，原因：{}", accessNode, e.getMessage());
            item.setPushFailReason(e.getMessage());
        }
        return item;
    }

    /**
     * 组装调用广播平台创建任务
     *
     * @param hornTaskConfVO 任务配置信息
     * @param currentUser    当前操作用户信息
     * @param autoDelete     是否自动删除
     * @param autoExecute     创建任务后是否立即播放
     */
    public List<HornTaskNorthVO> assembleHornTaskToHornSystem(HornTaskConfVO hornTaskConfVO, SysUser currentUser, boolean autoDelete, boolean autoExecute) {
        HornTaskNorthVO hornTaskNorthVO = new HornTaskNorthVO();
        hornTaskNorthVO.setTaskType(TASK_TYPE);
        hornTaskNorthVO.setTaskName(hornTaskConfVO.getTaskName());
        hornTaskNorthVO.setWeightedPriority(hornTaskConfVO.getTaskPriority());
        hornTaskNorthVO.setVolume(Integer.valueOf(hornTaskConfVO.getTaskVolumn()));
        hornTaskNorthVO.setCreateUser(currentUser.getUserName());
        hornTaskNorthVO.setBroadcastType(HornCommonEnum.BroadcastType.getEnum(hornTaskConfVO.getTaskType()).getBroadcastType());
        hornTaskNorthVO.setAcceptNode(currentUser.getPlatformCode());

        // 立即播放就传true
        hornTaskNorthVO.setAutoDelete(autoDelete);
        hornTaskNorthVO.setAutoExecute(autoExecute);
        assembleTaskStrategyInfo(hornTaskNorthVO, hornTaskConfVO);
        assembleTaskContentWithStrategyInfo(hornTaskNorthVO, hornTaskConfVO, currentUser);
        assembleTaskScheduleModelInfo(hornTaskNorthVO, hornTaskConfVO);

        //对喇叭节点进行区分
        Map<String, List<RangeInfo>> rangeInfoListMap = assembleRangeInfoList(hornTaskConfVO);
        List<HornTaskNorthVO> hornTaskNorthVOList = new ArrayList<>(rangeInfoListMap.size());
        rangeInfoListMap.forEach((accessNode, rangeInfoList) -> {
            HornTaskNorthVO item = new HornTaskNorthVO();
            BeanUtils.copyProperties(hornTaskNorthVO, item);
            item.setRangeInfoList(rangeInfoList);
            item.setAccessNode(accessNode);
            hornTaskNorthVOList.add(item);
        });
        return hornTaskNorthVOList;
    }


    /**
     * 组装任务策略信息
     *
     * @param hornTaskNorthVO 任务北向VO对象
     * @param hornTaskConfVO 任务配置VO对象
     */
    private void assembleTaskStrategyInfo(HornTaskNorthVO hornTaskNorthVO, HornTaskConfVO hornTaskConfVO) {
        TaskStrategyInfo taskStrategyInfo =  new TaskStrategyInfo();
        HornTaskStrategyConfVO hornTaskStrategyConfVO = hornTaskConfVO.getStrategyConf();
        taskStrategyInfo.setDuration(hornTaskStrategyConfVO.getPlayDuration());
        taskStrategyInfo.setLoopTimes(hornTaskStrategyConfVO.getPlayTimes());
        if (HornCommonEnum.StartupPolicy.STARTUP_POLICY_1.getStartupPolicyValue().equals(hornTaskStrategyConfVO.getStartupPolicy())) {
            taskStrategyInfo.setValidTimeBegin(hornTaskStrategyConfVO.getValidDate());
            taskStrategyInfo.setValidTimeEnd(hornTaskStrategyConfVO.getInvalidDate());
        }
        hornTaskNorthVO.setTaskStrategyInfo(taskStrategyInfo);
    }

    /**
     * 组装内容信息
     *
     * @param hornTaskNorthVO 任务北向VO对象
     * @param hornTaskConfVO 任务配置VO对象
     */
    private void assembleTaskContentWithStrategyInfo(HornTaskNorthVO hornTaskNorthVO, HornTaskConfVO hornTaskConfVO, SysUser currentUser) {
        // 如果是文件广播 直接取表里的resourceId字段 如果是文本广播 则需要调用创建文本接口生成
        List<VmHornTaskContentRelDO> vmHornTaskContentRelList = hornTaskConfVO.getVmHornTaskContentRelList();
        List<Long> contentIdList = vmHornTaskContentRelList.stream().map(VmHornTaskContentRelDO::getContentId).collect(Collectors.toList());
        List<VmHornContentConfDTO> vmHornTaskContentConfDOList = vmHornContentMapper.selectByTaskIds(contentIdList);
        List<TaskContentWithStrategyInfo> taskContentWithStrategyInfoList = new ArrayList<>(vmHornTaskContentConfDOList.size());
        if (HornCommonEnum.BroadcastType.BROADCAST_0.getTaskType().equals(hornTaskConfVO.getTaskType())) {
            vmHornTaskContentRelList.forEach(item -> {
                TaskContentWithStrategyInfo taskContentWithStrategyInfo = new TaskContentWithStrategyInfo();
                VmHornContentConfDTO vmHornContentConfDTO = vmHornTaskContentConfDOList.stream().filter(DO -> DO.getId().equals(item.getContentId())).findFirst()
                        .orElseThrow(() -> new CustomException("没有找到对应内容数据，contentId为: " + item.getContentId()));
                taskContentWithStrategyInfo.setContentName(vmHornContentConfDTO.getContentTitle());
                taskContentWithStrategyInfo.setContentType(HornCommonEnum.BroadcastType.BROADCAST_0.getContentType());
                taskContentWithStrategyInfo.setResourceUrl(vmHornContentConfDTO.getFileUrl());
                taskContentWithStrategyInfo.setMd5(vmHornContentConfDTO.getFileMd5());
                taskContentWithStrategyInfo.setTrackLength(vmHornContentConfDTO.getFileDuration());
                TaskContentStrategyInfo taskContentStrategyInfo =  new TaskContentStrategyInfo();
                taskContentStrategyInfo.setPlayOrder(item.getPlayOrder());
                taskContentWithStrategyInfo.setTaskContentStrategyInfo(taskContentStrategyInfo);
                taskContentWithStrategyInfoList.add(taskContentWithStrategyInfo);
            });
        }
        else if (HornCommonEnum.BroadcastType.BROADCAST_1.getTaskType().equals(hornTaskConfVO.getTaskType())) {
            // 只能添加一个文本内容
            if (!CollectionUtils.isEmpty(vmHornTaskContentConfDOList)) {
                VmHornTaskContentRelDO item = vmHornTaskContentRelList.get(0);
                HornTxtContentNorthVO hornTxtContentNorthVO = new HornTxtContentNorthVO();
                hornTxtContentNorthVO.setRegionCode(currentUser.getAreaCode());
                hornTxtContentNorthVO.setSpeechRate(item.getSpeechRate());
                VmHornContentConfDTO vmHornTaskContentRelDO = vmHornTaskContentConfDOList.stream().filter(DO -> DO.getId().equals(item.getContentId())).findFirst()
                        .orElseThrow(() -> new CustomException("没有找到对应内容数据，contentId为: " + item.getContentId()));
                hornTxtContentNorthVO.setTxtContent(vmHornTaskContentRelDO.getContentValue());
                hornTxtContentNorthVO.setTxtName(vmHornTaskContentRelDO.getContentTitle());
                hornTxtContentNorthVO.setVolume(item.getVolume());
                hornTxtContentNorthVO.setXunfeiVcn(item.getXunfeiVcn());
                hornTxtContentNorthVO.setAccessNode(currentUser.getPlatformCode());
                hornTxtContentNorthVO.setAcceptNode(currentUser.getPlatformCode());
                //调用创建文本内容接口拿到txtId
                TalkResultVO<HornAddTextInfRspVO> talkResultVO = hornRequestUtils.callHornSystem(hornTxtContentNorthVO, NetConnectUtils.RequestParamter.URLMappingEnum.ADD_TEXT, HornAddTextInfRspVO.class, null);
                String txtId = talkResultVO.getData().getTxtId();
                item.setTxtId(txtId);
                //临时存储fileId
                hornTaskConfVO.setFileId(txtId);
                //勾建文本广播的taskContentWithStrategyInfo
                TaskContentWithStrategyInfo taskContentWithStrategyInfo =  new TaskContentWithStrategyInfo();
                taskContentWithStrategyInfo.setContentName(vmHornTaskContentRelDO.getContentTitle());
                taskContentWithStrategyInfo.setContentType(HornCommonEnum.BroadcastType.BROADCAST_1.getContentType());
                TaskContentStrategyInfo taskContentStrategyInfo =  new TaskContentStrategyInfo();
                taskContentStrategyInfo.setPlayOrder(item.getPlayOrder());
                taskContentWithStrategyInfo.setTaskContentStrategyInfo(taskContentStrategyInfo);
                taskContentWithStrategyInfoList.add(taskContentWithStrategyInfo);
            }
        }
        hornTaskNorthVO.setTaskContentWithStrategyInfoList(taskContentWithStrategyInfoList);
    }

    /**
     * 组装范围信息
     *
     * @param hornTaskConfVO 任务配置VO对象
     */
    private Map<String, List<RangeInfo>> assembleRangeInfoList(HornTaskConfVO hornTaskConfVO) {
        Map<String, List<RangeInfo>> rangeInfoListMap = new HashMap<>(16);
        //根据接入节点区分不同节点的设备
        List<String> deviceCodeList = hornTaskConfVO.getDeviceCodeList();
        List<DeviceInfoVO> deviceInfoVOList = hornTaskConfMapper.getDeviceInfoByDeviceCodeList(deviceCodeList);
        if (CollectionUtils.isEmpty(deviceInfoVOList)) {
            throw new CustomException("任务所选喇叭设备不存在");
        }
        Map<String, List<DeviceInfoVO>> mapByAccessNode = deviceInfoVOList.stream()
                .collect(Collectors.groupingBy(DeviceInfoVO::getAccessNode));

        mapByAccessNode.forEach((key, value) -> {
            RangeInfo rangeInfo =  new RangeInfo();
            // 只有一层
            rangeInfo.setOrder(0);
            List<RangeDevInfo> rangeDevInfoList = new ArrayList<>();
            value.forEach(item -> {
                RangeDevInfo rangeDevInfo = new RangeDevInfo();
                rangeDevInfo.setDeviceCode(item.getDeviceCode());
                rangeDevInfoList.add(rangeDevInfo);
                rangeInfo.setRangeDevInfoList(rangeDevInfoList);
            });
            rangeInfoListMap.put(key, Collections.singletonList(rangeInfo));
        });
        return rangeInfoListMap;
    }

    /**
     * 组装任务执行信息
     *
     * @param hornTaskNorthVO 任务北向VO对象
     * @param hornTaskConfVO 任务配置VO对象
     */
    private void assembleTaskScheduleModelInfo(HornTaskNorthVO hornTaskNorthVO, HornTaskConfVO hornTaskConfVO) {
        TaskScheduleModelInfo taskScheduleModelInfo =  new TaskScheduleModelInfo();
        HornTaskStrategyConfVO hornTaskStrategyConfVO = hornTaskConfVO.getStrategyConf();
        Integer startupPolicy = hornTaskStrategyConfVO.getStartupPolicy();
        taskScheduleModelInfo.setJobType(HornCommonEnum.JobType.getEnum(startupPolicy).getJobTypeValue());
        List<HornTimeInfo> timeInfo = hornTaskStrategyConfVO.getTimeInfo();
        //如果是单次 拼接执行时间
        if (startupPolicy.equals(HornCommonEnum.StartupPolicy.STARTUP_POLICY_0.getStartupPolicyValue())) {
            taskScheduleModelInfo.setDate(hornTaskStrategyConfVO.getValidDate() + " " + judgeIsOldTask(timeInfo.get(0).getStartTime()) + ":00");
        }
        else if (startupPolicy.equals(HornCommonEnum.StartupPolicy.STARTUP_POLICY_1.getStartupPolicyValue())) {
            taskScheduleModelInfo.setDayOfWeeks(hornTaskStrategyConfVO.getCycleDate());
            List<TimeInfo> timeInfoList = new ArrayList<>(timeInfo.size());
            timeInfo.forEach(item -> {
                TimeInfo time =  new TimeInfo();
                Date startTime;
                try {
                    startTime = DateUtils.parseDate(judgeIsOldTask(item.getStartTime()), "HH:mm");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startTime);
                    time.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    time.setMinute(calendar.get(Calendar.MINUTE));
                    time.setSecond(0);
                } catch (ParseException e) {
                    throw new CustomException("时间转化异常");
                }

                timeInfoList.add(time);
            });
            taskScheduleModelInfo.setPlayTimeInfoList(timeInfoList);
        }
        //如果是播放时间段 需要TaskStrategyInfo.stopTime
        if (HornCommonEnum.PlayPolicy.PLAY_POLICY_2.getPlayPolicyValue().equals(hornTaskStrategyConfVO.getPlayPolicy())) {
            taskScheduleModelInfo.setDate(hornTaskStrategyConfVO.getValidDate() + " " + judgeIsOldTask(timeInfo.get(0).getStartTime()) + ":00");
            hornTaskNorthVO.getTaskStrategyInfo().setStopTime(judgeIsOldTask(timeInfo.get(0).getEndTime()) + ":59");
        }
        hornTaskNorthVO.setTaskScheduleModelInfo(taskScheduleModelInfo);
    }


    public Long saveHornTask(HornTaskConfVO hornTaskConfVO, SysUser currentUser) {
        hornTaskConfVO.setTenantId(currentUser.getTenantId());
        hornTaskConfVO.setIndustryCode(currentUser.getIndustryCode());
        hornTaskConfVO.setCreator(currentUser.getUserName());
        hornTaskConfVO.setAppVerCode(currentUser.getAppVerCode());
        //1.新增任务
        int count = hornTaskConfMapper.addTaskConf(hornTaskConfVO);
        //任务id
        Long taskId = hornTaskConfVO.getId();
        if (count == 0 || Objects.isNull(taskId)) {
            throw new CustomException("新增任务失败");
        }
        saveHornTaskRelInfo(hornTaskConfVO, taskId);
        try {
            syncTaskInfoToAlgorithmMongo(taskId, null, null);
        }
        catch (Exception e) {
            log.error(SYNC_ALGORITHM_MONGO_FAIL, e);
        }
        return taskId;
    }

    private void deleteHornTxt(HornTaskConfVO hornTaskConfVO, SysUser currentUser) {
        try {
            List<VmHornTaskContentRelDO> vmHornTaskContentRelDOS = hornTaskConfMapper.selectContentRelByTaskId(hornTaskConfVO.getId());
            if (CollectionUtil.isNotEmpty(vmHornTaskContentRelDOS)) {
                //目前文本只能播放一个
                VmHornTaskContentRelDO item = vmHornTaskContentRelDOS.get(0);
                if (StringUtil.isNotEmpty(item.getTxtId())) {
                    HornDeleteTxtReqVO hornDeleteTxtReqVO = new HornDeleteTxtReqVO();
                    hornDeleteTxtReqVO.setTxtIds(Collections.singletonList(item.getTxtId()));
                    hornDeleteTxtReqVO.setAcceptNode(currentUser.getPlatformCode());
                    hornRequestUtils.callHornSystem(hornDeleteTxtReqVO, NetConnectUtils.RequestParamter.URLMappingEnum.DELETE_TEXT, null, null);
                    hornTaskConfMapper.setHornContentRelTxtIdByTaskId(hornTaskConfVO.getId(), "");
                }
            }
        } catch (Exception e) {
            log.error("调用广播平台删除文本失败:", e);
        }
    }

    /**
     * 组装立即播放信息
     *
     * @param hornTaskConfVO 任务配置VO对象
     */
    private void assemblePlayRightInfo(HornTaskConfVO hornTaskConfVO) {
        hornTaskConfVO.setTaskName(StringUtil.isEmpty(hornTaskConfVO.getTaskName()) ? "立即播放_"+System.currentTimeMillis() : hornTaskConfVO.getTaskName());
        hornTaskConfVO.setTaskPriority(100);
        hornTaskConfVO.setTaskVolumn(StringUtil.isEmpty(hornTaskConfVO.getTaskVolumn()) ? "5" : hornTaskConfVO.getTaskVolumn());

        HornTaskStrategyConfVO strategyConf = new HornTaskStrategyConfVO();
        strategyConf.setPlayTimes(1);
        strategyConf.setStartupPolicy(HornCommonEnum.StartupPolicy.STARTUP_POLICY_2.getStartupPolicyValue());
        hornTaskConfVO.setStrategyConf(strategyConf);
    }

    private void callHornPlayRightNow(HornTaskConfVO hornTaskConfVO, SysUser currentUser) {
        assemblePlayRightInfo(hornTaskConfVO);
        List<HornTaskNorthVO> hornTaskNorthVOList = assembleHornTaskToHornSystem(hornTaskConfVO, currentUser, true, true);
        if (HornCommonEnum.BroadcastType.BROADCAST_1.getTaskType().equals(hornTaskConfVO.getTaskType())) {
            saveTaskPlayParam(hornTaskConfVO.getFileId(), hornTaskNorthVOList, null);
        } else {
            callHornSystemForDiffNodePlay(hornTaskNorthVOList, hornTaskConfVO, hornTaskConfVO.getFileId(), true);
        }
    }

    private void saveTaskPlayParam(String fileId, List<HornTaskNorthVO> hornTaskNorthVOList, Long taskId) {
        List<HornTaskPlayParamDTO> list = new ArrayList<>(hornTaskNorthVOList.size());
        hornTaskNorthVOList.forEach(hornTaskNorthVO -> {
            HornTaskPlayParamDTO param = new HornTaskPlayParamDTO();
            param.setTaskId(taskId);
            param.setFileId(fileId);
            param.setParamJson(JSON.toJSONString(hornTaskNorthVO));
            param.setCreateTime(new Date());
            list.add(param);
        });
        hornTaskConfMapper.addTaskPlayParam(list);
    }

    private Result<String> callHornSystemForDiffNodeDelete(VmHornTaskDiffNodeRelDO vmHornTaskDiffNodeRelDO, SysUser currentUser) {
        if (StringUtil.isEmpty(vmHornTaskDiffNodeRelDO.getHornTaskId())
                || vmHornTaskDiffNodeRelDO.getExecutStatus().equals(HornCommonEnum.ExecutStatus.EXECUT_STATUS_STOP.getExecutStatusValue())) {
            return null;
        }
        HornStopTaskReqVO hornStopTaskReqVO = new HornStopTaskReqVO();
        hornStopTaskReqVO.setTaskId(vmHornTaskDiffNodeRelDO.getHornTaskId());
        hornStopTaskReqVO.setAcceptNode(currentUser.getPlatformCode());

        List<VmHornTaskDeviceRelDO> deviceRelDOS = hornTaskConfMapper.selectDeviceRelByTaskId(Long.valueOf(vmHornTaskDiffNodeRelDO.getTaskId()));
        List<String> deviceCodes = deviceRelDOS.stream().map(VmHornTaskDeviceRelDO::getDeviceCode).collect(Collectors.toList());
        //第一步调用停止任务接口
        hornRequestUtils.callHornSystemDiffNode(hornStopTaskReqVO, NetConnectUtils.RequestParamter.URLMappingEnum.STOP_TASKT, null, vmHornTaskDiffNodeRelDO.getAccessNode(), deviceCodes);

        //第二步调用任务删除接口
        HornDeleteTaskReqVO hornDeleteTaskReqVO = new HornDeleteTaskReqVO();
        hornDeleteTaskReqVO.setTaskIdList(CollectionUtil.toList(vmHornTaskDiffNodeRelDO.getHornTaskId()));
        hornDeleteTaskReqVO.setAcceptNode(currentUser.getPlatformCode());
        hornRequestUtils.callHornSystemDiffNode(hornDeleteTaskReqVO, NetConnectUtils.RequestParamter.URLMappingEnum.DELETE_TIMING, null, vmHornTaskDiffNodeRelDO.getAccessNode(), deviceCodes);

        hornTaskConfMapper.updateDiffNodeRelStatus(vmHornTaskDiffNodeRelDO.getHornTaskId(), HornCommonEnum.ExecutStatus.EXECUT_STATUS_STOP.getExecutStatusValue());
        return Result.success("停止成功");
    }

    /**
     * 判断是否为老任务。
     *
     * @param time 时间字符串
     * @return 如果是老任务，则报错否则返回原始值
     */
    private String judgeIsOldTask(String time) {
        // 因为需求改了，前端全部去掉了秒，广播平台又要秒，所以这里在特殊处理前判断是否为改造前的老任务
        String[] split = time.split(":");
        if (split.length > 2) {
            throw new CustomException("广播任务已过期");
        }
        return time;
    }

    private List<AlgorithmHornContentVO> assembleHornContent(VmHornTaskConfDO vmHornTaskConfDO, String txtFileUrl, String fileId) {
        List<AlgorithmHornContentVO> contentInfos = Lists.newArrayList();
        List<VmHornTaskContentRelDO> contentRel = hornTaskConfMapper.selectContentRelByTaskId(vmHornTaskConfDO.getId());
        List<Long> contentIdList = contentRel.stream().map(VmHornTaskContentRelDO::getContentId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(contentIdList)) {
            return contentInfos;
        }
        List<VmHornContentConfDTO> vmHornTaskContentConfDOList = vmHornContentMapper.selectByTaskIds(contentIdList);
        for (VmHornContentConfDTO vmHornTaskContentConfDO : vmHornTaskContentConfDOList) {
            AlgorithmHornContentVO contentInfo = new AlgorithmHornContentVO();
            contentInfo.setContentTitle(vmHornTaskContentConfDO.getContentTitle());
            if (HornCommonEnum.BroadcastType.BROADCAST_1.getTaskType().equals(vmHornTaskConfDO.getTaskType())) {
                contentInfo.setContentValue(vmHornTaskContentConfDO.getContentValue());
            }
            else {
                contentInfo.setFileName(vmHornTaskContentConfDO.getFileName());
            }
            contentInfo.setFileUrl(StringUtil.isEmpty(txtFileUrl) ? vmHornTaskContentConfDO.getFileUrl() : txtFileUrl);
            contentInfo.setFileId(StringUtil.isEmpty(fileId) ? vmHornTaskContentConfDO.getResourceId() : fileId);
            contentInfos.add(contentInfo);
        }
       return contentInfos;
    }

    private List<AlgorithmHornInfoVO> assembleHornDeviceInfo(VmHornTaskConfDO vmHornTaskConfDO) {
        List<VmHornTaskDeviceRelDO> deviceRelDOS = hornTaskConfMapper.selectDeviceRelByTaskId(vmHornTaskConfDO.getId());
        List<AlgorithmHornInfoVO> deviceInfos = Lists.newArrayList();
        List<String> deviceCodes =  deviceRelDOS.stream().map(VmHornTaskDeviceRelDO::getDeviceCode).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deviceCodes)) {
            return deviceInfos;
        }
        HornDeviceDTO req = new HornDeviceDTO();
        req.setTenantId(vmHornTaskConfDO.getTenantId());
        req.setIndustryCode(vmHornTaskConfDO.getIndustryCode());
        req.setAppVerCode(vmHornTaskConfDO.getAppVerCode());
        SysUser user = iHornDeviceRepository.selectUserOnlyByUserName(vmHornTaskConfDO.getCreator());
        req.setUserId(user.getUserId());
        req.setDeviceCodeList(deviceCodes);
        //防止报错
        req.setDataScope("");
        List<HornDeviceVo> deviceInfoList = hornDeviceMapper.queryHornDeviceList(req);
        for (HornDeviceVo item : deviceInfoList) {
            AlgorithmHornInfoVO one = new AlgorithmHornInfoVO();
            BeanUtils.copyProperties(item, one);
            deviceInfos.add(one);
        }
        return deviceInfos;
    }
}
