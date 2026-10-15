package com.chinatower.bdm.modules.inspectionRoute.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.inspectionRoute.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.mapper.InspectionRouteMapper;
import com.chinatower.bdm.modules.inspectionRoute.service.IInspectionRouteService;
import com.chinatower.common.core.dto.bdm.LayerQueryDTO;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.CompletableFutureHelper;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.entity.layer.GeojsonStruckRes;
import com.chinatower.gis.api.entity.layer.GetXjxlFeaturesByIdsReq;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.IVideoPatrolPlanService;
import com.chinatower.video.api.VideoControlAPI;
import com.chinatower.video.api.entity.DoControlPresetReq;
import com.chinatower.video.api.entity.SttPatrolPauseReq;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 巡检路线配置 接口实现类
 */
@Service
@Slf4j
public class IInspectionRouteServiceImpl implements IInspectionRouteService {

    @Autowired
    private InspectionRouteMapper inspectionRouteMapper;
    @Autowired
    private VideoControlAPI videoControlAPI;

    @Autowired
    private IVideoPatrolPlanService iVideoPatrolPlanService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private GisLayerAPI gisLayerAPI;

    private static final ExecutorService THREAD_POOL_PRESET = Executors.newFixedThreadPool(20);

    private static final String ALERT_MESSAGE = "暂无摄像机权限";

    @Override
    @DataScope(tenantColumn = "s.tenant_id",industryCode = "s.industry_code")
    public Result<PageInfo<InspectionVO>> queryListPage(InspectionDTO inspectionDTO) {

        try {
            if (null == inspectionDTO.getPageNum() || inspectionDTO.getPageNum() <= Constants.NUMBER_ZERO){
                inspectionDTO.setPageNum(1);
            }
            if (null == inspectionDTO.getPageSize() || inspectionDTO.getPageSize() <= Constants.NUMBER_ZERO){
                inspectionDTO.setPageSize(10);
            }
            PageMethod.startPage(inspectionDTO.getPageNum(),inspectionDTO.getPageSize());
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            inspectionDTO.setTenantId(loginUser.getTenantId());
            inspectionDTO.setIndustryCode(loginUser.getIndustryCode());
            inspectionDTO.setAppVerCode(loginUser.getAppVerCode());
            List<InspectionVO> inspectionVOS = inspectionRouteMapper.queryInspectionList(inspectionDTO);
            Result<PageInfo<InspectionVO>> result;
            PageInfo<InspectionVO> pageInfo = new PageInfo<>(inspectionVOS);
            result = Result.success("查询成功！");
            result.setData(pageInfo);
            return result;
        }catch (Exception e){
            log.info("查询巡检路线配置异常：",e);
            return Result.fail("查询巡检路线配置异常:"+e.getMessage());
        }
    }

    @Override
    public Result<PageInfo<InspectionRoutePage4SdkVO>> queryInspectionRoutePage4Sdk(InspectionRoutePage4SdkParam param) {
        if (null == param.getPageNum() || param.getPageNum() <= com.chinatower.platform.constants.Constants.ZERO_NUM) {
            param.setPageNum(com.chinatower.platform.constants.Constants.ONE_NUM);
        }
        if (null == param.getPageSize() || param.getPageSize() <= com.chinatower.platform.constants.Constants.ZERO_NUM) {
            param.setPageSize(com.chinatower.platform.constants.Constants.TEN_NUM);
        }
        Result<PageInfo<InspectionRoutePage4SdkVO>> result;
        PageMethod.startPage(param.getPageNum(), param.getPageSize());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        List<InspectionRoutePage4SdkVO> resultList = inspectionRouteMapper.queryInspectionRoutePage4Sdk(param);
        Map<Long, List<InspectionFeaturePlotVO>> plotInfoListMap = null;
        if (!CollectionUtils.isEmpty(resultList)){
            List<Long> inspectionRouteIdList = resultList.stream().filter(item -> item.getInspectionRouteId() != null).map(InspectionRoutePage4SdkVO::getInspectionRouteId).collect(Collectors.toList());
            List<InspectionFeaturePlotVO> featurePlotVOList = inspectionRouteMapper.queryFeaturePlotInfo(inspectionRouteIdList, param.getDataPermissions());
            if (!CollectionUtils.isEmpty(featurePlotVOList)){
                plotInfoListMap = featurePlotVOList.stream().collect(Collectors.groupingBy(InspectionFeaturePlotVO::getInspectionRouteId));
            }

            List<InspectionRelVO> durationInfoList = inspectionRouteMapper.queryDurationInfoByRouteIdList(inspectionRouteIdList);
            // key -> 巡检线路id value -> 线路点位列表
            Map<Long, List<InspectionRelVO>> durationMap = durationInfoList.stream().collect(Collectors.groupingBy(InspectionRelVO::getInspectionRouteId));

            for (InspectionRoutePage4SdkVO data : resultList) {
                if (MapUtils.isNotEmpty(plotInfoListMap)){
                    List<InspectionFeaturePlotVO> plotInfoList = plotInfoListMap.get(data.getInspectionRouteId());
                    BigDecimal totalArea = new BigDecimal("0");
                    if (!CollectionUtils.isEmpty(plotInfoList)) {
                        for (InspectionFeaturePlotVO plotInfo : plotInfoList) {
                            totalArea = totalArea.add(new BigDecimal(plotInfo.getPlotArea()));
                        }
                    }
                    data.setPlotTotalArea(totalArea.intValue());
                }

                // 填充巡检路线点位停留总时间
                if (durationMap.containsKey(data.getInspectionRouteId())) {
                    data.setTotalDurationTime(getTotalDurationTime(durationMap.get(data.getInspectionRouteId())));
                } else {
                    data.setTotalDurationTime("0秒");
                }
            }

        }

        PageInfo<InspectionRoutePage4SdkVO> pageInfo = new PageInfo<>(resultList);
        result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    private String getTotalDurationTime(List<InspectionRelVO> inspectionRelList) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(inspectionRelList)) {
            return "0秒";
        }
        int totalDurationSeconds = 0;
        for (InspectionRelVO inspectRel : inspectionRelList) {
            // 秒
            if (Objects.equals(Constants.ONE, inspectRel.getDurationUnit())) {
                totalDurationSeconds = totalDurationSeconds + inspectRel.getDurationTime();
            }
            // 分钟
            else {
                totalDurationSeconds = totalDurationSeconds + (inspectRel.getDurationTime() * 60);
            }
        }
        int hours = totalDurationSeconds / 3600;
        int minutes = (totalDurationSeconds % 3600) / 60;
        int seconds = (totalDurationSeconds % 3600) % 60;
        if (hours > 0) {
            return hours + "时" + minutes + "分" + seconds + "秒";
        } else if (minutes > 0) {
            return minutes + "分" + seconds + "秒";
        } else {
            return seconds + "秒";
        }
    }

    @Override
    @Transactional
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code", deviceColumn = "rel.device_code")
    public Result<InspectionRouteProcessVO> doProcess(InspectionRouteProcessParam param) {
        log.info("InspectionRouteServiceImpl doProcess param:{}", param == null ? "" : JSON.toJSONString(param));
        if (param == null
                || param.getInspectionRouteId() == null
                || param.getStatus() == null
                || (param.getInspectionRouteSubId() == null
                        && !Objects.equals(param.getStatus(), Constants.INSPECTION_ROUTE_PROCESS_START)
                        && !Objects.equals(param.getStatus(), Constants.INSPECTION_ROUTE_PROCESS_RESET))) {
            return Result.fail("巡航路线编码、点位编码、巡航状态必填参数不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user == null) {
            throw new CustomException(Constants.USER_NOT_LOGIN_MSG);
        }
        // 填充登录信息
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        param.setUserId(user.getUserId());
        param.setModifier(user.getUserName());
        param.setUpdateTime(new Date());
        InspectionRouteProcessVO result = new InspectionRouteProcessVO();
        if (!Objects.equals(param.getStatus(), Constants.INSPECTION_ROUTE_PROCESS_START)
                && !Objects.equals(param.getStatus(), Constants.INSPECTION_ROUTE_PROCESS_RESET)) {
            InspectionRelInfoVO inspectionRelInfo = inspectionRouteMapper.selectInspectionRelById(param.getInspectionRouteSubId());
            if (inspectionRelInfo == null) {
                throw new CustomException("巡航路线点不存在");
            }
            if (!Objects.equals(inspectionRelInfo.getInspectionRouteId(), param.getInspectionRouteId())) {
                throw new CustomException("巡航路线编码和点位编码不匹配");
            }
            if (!Objects.equals(inspectionRelInfo.getTenantId(), param.getTenantId())
                    || !Objects.equals(inspectionRelInfo.getIndustryCode(), param.getIndustryCode())) {
                throw new CustomException("巡航路线点位和客户、行业编码不匹配");
            }
            result.setInspectionRouteId(inspectionRelInfo.getInspectionRouteId());
            result.setInspectionRouteSubId(inspectionRelInfo.getId());
            result.setSortSeq(inspectionRelInfo.getSortSeq());
        }

        switch (param.getStatus()) {
            case Constants.INSPECTION_ROUTE_PROCESS_START:
                // 开始流程
                result = doStartProcess(param);
                break;
            case Constants.INSPECTION_ROUTE_PROCESS_UPDATE:
                // 更新流程
                InspectionRelInfoVO inspectionRelWithPermissionVO = doUpdateProcess(param);
                result.setRouteStatus(Constants.ROUTE_STATUS_WORK);
                // 如果根据点位id查不到权限内的点位信息，则认为没有权限，给出提示
                if (Objects.isNull(inspectionRelWithPermissionVO)) {
                    result.setAlertMessage(ALERT_MESSAGE);
                }
                break;
            case Constants.INSPECTION_ROUTE_PROCESS_PAUSE:
                // 暂停流程
                InspectionRelInfoVO pauseInspectionRelWithPermissionVO = doPauseProcess(param);
                result.setRouteStatus(Constants.ROUTE_STATUS_PAUSE);
                // 如果根据点位id查不到权限内的点位信息，则认为没有权限，给出提示
                if (Objects.isNull(pauseInspectionRelWithPermissionVO)) {
                    result.setAlertMessage(ALERT_MESSAGE);
                }
                break;
            case Constants.INSPECTION_ROUTE_PROCESS_RECOVER:
                // 恢复流程
                InspectionRelInfoVO recoveryInspectionRelWithPermissionVO = doRecoverProcess(param);
                result.setRouteStatus(Constants.ROUTE_STATUS_IDLE);
                // 如果根据点位id查不到权限内的点位信息，则认为没有权限，给出提示
                if (Objects.isNull(recoveryInspectionRelWithPermissionVO)) {
                    result.setAlertMessage(ALERT_MESSAGE);
                }
                break;
            case Constants.INSPECTION_ROUTE_PROCESS_RESET:
                // 重置流程
                result = doResetProcess(param);
                break;
            case Constants.INSPECTION_ROUTE_PROCESS_FINISH:
                // 结束流程
                doFinishProcess(param);
                result.setRouteStatus(Constants.ROUTE_STATUS_IDLE);
                break;
            default:
                return Result.fail("巡查路线操作状态不正确");
        }
        return Result.data(result);
    }

    private void initRouteRecord(Long routeId) {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            if (user == null) {
                throw new CustomException(Constants.USER_NOT_LOGIN_MSG);
            }
            Date now = new Date();
            InspectionRouteRecordDTO record = new InspectionRouteRecordDTO();
            record.setRouteId(routeId);
            record.setStartTime(now);
            record.setRouteStatus(Constants.ROUTE_STATUS_WORK);
            record.setTenantId(user.getTenantId());
            record.setIndustryCode(user.getIndustryCode());
            record.setAppVerCode(user.getAppVerCode());
            record.setCreator(user.getUserName());
            record.setCreateTime(now);
            inspectionRouteMapper.initRouteRecord(record);
        } catch (Exception e) {
            log.error("initRouteRecord error, routeId:{}, e:", routeId, e);
        }
    }

    private void updateRouteRecord(Long routeId, int status) {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            if (user == null) {
                throw new CustomException(Constants.USER_NOT_LOGIN_MSG);
            }
            if (Objects.equals(Constants.INSPECTION_ROUTE_PROCESS_PAUSE, status)) {
                // 暂停 清空开始、结束时间，计算差值增加duration
                InspectionRouteRecordDTO recordInfo = inspectionRouteMapper.getLatestRouteInfoByStatus(routeId, Constants.ROUTE_STATUS_WORK);
                if (recordInfo != null) {
                    Date now = new Date();
                    long diffInMills = now.getTime() - recordInfo.getStartTime().getTime();
                    long diffInSeconds = TimeUnit.SECONDS.convert(diffInMills, TimeUnit.MILLISECONDS);
                    InspectionRouteRecordDTO record = new InspectionRouteRecordDTO();
                    record.setId(recordInfo.getId());
                    record.setRouteStatus(Constants.ROUTE_STATUS_PAUSE);
                    record.setDuration(diffInSeconds);
                    record.setEndTime(now);
                    record.setModifier(user.getUserName());
                    record.setUpdateTime(now);
                    inspectionRouteMapper.updateRouteRecord(record);
                }
            } else if (Objects.equals(Constants.INSPECTION_ROUTE_PROCESS_RECOVER, status)) {
                // 恢复 变更开始时间为当前时间
                InspectionRouteRecordDTO recordInfo = inspectionRouteMapper.getLatestRouteInfoByStatus(routeId, Constants.ROUTE_STATUS_PAUSE);
                if (recordInfo != null) {
                    Date now = new Date();
                    InspectionRouteRecordDTO record = new InspectionRouteRecordDTO();
                    record.setId(recordInfo.getId());
                    record.setRouteStatus(Constants.ROUTE_STATUS_WORK);
                    record.setStartTime(now);
                    record.setEndTime(null);
                    record.setModifier(user.getUserName());
                    record.setUpdateTime(now);
                    inspectionRouteMapper.updateRouteRecord(record);
                }
            } else {
                // 结束 记录结束时间，计算差值增加duration
                InspectionRouteRecordDTO recordInfo = inspectionRouteMapper.getLatestRouteInfoByStatus(routeId, Constants.ROUTE_STATUS_WORK);
                if (recordInfo != null) {
                    Date now = new Date();
                    long diffInMills = now.getTime() - recordInfo.getStartTime().getTime();
                    long diffInSeconds = TimeUnit.SECONDS.convert(diffInMills, TimeUnit.MILLISECONDS);
                    InspectionRouteRecordDTO record = new InspectionRouteRecordDTO();
                    record.setId(recordInfo.getId());
                    record.setRouteStatus(Constants.ROUTE_STATUS_IDLE);
                    record.setDuration(diffInSeconds);
                    record.setEndTime(now);
                    record.setModifier(user.getUserName());
                    record.setUpdateTime(now);
                    inspectionRouteMapper.updateRouteRecord(record);
                }
            }
        } catch (Exception e) {
            log.error("finishRouteRecord error, routeId:{}, e:", routeId, e);
        }
    }

    private InspectionRouteProcessVO doStartProcess(InspectionRouteProcessParam param) {
        log.info("InspectionRouteServiceImpl doProcess doStartProcess param:{}", JSON.toJSONString(param));
        /**
         * 开始接口
         * 1、查询巡检任务第一条id
         * 2、更新巡检表巡检状态和巡检开始时间，用状态卡
         * 3、更新巡检电表状态和巡检点时间
         * 4、后台异步获取云台锁转动到预置位
         */
        InspectionRelInfoVO relInfo = inspectionRouteMapper.queryFirstTaskInfo(param);
        if (relInfo == null) {
            throw new CustomException("该客户巡航路线下没有配置巡查任务");
        }
        // 设置巡检点编码
        param.setInspectionRouteSubId(relInfo.getId());

        // 权限范围内根据点位id查询点位信息
        InspectionRelInfoVO inspectionRelWithPermissionVO = inspectionRouteMapper.selectInspectionRelByIdWithPermission(param);

        // 更新巡检表巡检状态，用状态卡
        updateInspectionStatus(param, Constants.ROUTE_STATUS_WORK, Constants.ROUTE_STATUS_IDLE);
        // 更新巡检点表巡检状态和巡检开始时间，用状态卡
        updateInspectionRelTimeAndStatus(param, Constants.ROUTE_STATUS_WORK, Constants.ROUTE_STATUS_IDLE);
        // 异步获取云台锁转动到预置位
        asyncMovePresetPosition(param);

        // 记录任务开启
        initRouteRecord(relInfo.getInspectionRouteId());

        // 返回第一条
        InspectionRouteProcessVO result = new InspectionRouteProcessVO();
        result.setInspectionRouteId(relInfo.getInspectionRouteId());
        result.setInspectionRouteSubId(relInfo.getId());
        result.setRouteStatus(Constants.ROUTE_STATUS_WORK);
        result.setSortSeq(relInfo.getSortSeq());
        if (Objects.isNull(inspectionRelWithPermissionVO)) {
            result.setAlertMessage(ALERT_MESSAGE);
        }
        return result;
    }

    private void updateInspectionStatus(InspectionRouteProcessParam param, int routeStatus, Integer originalRouteStatus) {
        InspectionStatusUpdateDTO inspectionStatusUpdateDTO = new InspectionStatusUpdateDTO();
        inspectionStatusUpdateDTO.setRouteStatus(routeStatus);
        inspectionStatusUpdateDTO.setOriginalRouteStatus(originalRouteStatus);
        inspectionStatusUpdateDTO.setModifier(param.getModifier());
        inspectionStatusUpdateDTO.setUpdateTime(param.getUpdateTime());
        inspectionStatusUpdateDTO.setId(param.getInspectionRouteId());
        int updateResult = inspectionRouteMapper.updateInspectionStatusWithOriginalStatus(inspectionStatusUpdateDTO);
        if (updateResult == 0) {
            throw new CustomException(String.format("该巡航路线状态不正确，不能开启, 路线编码：%s", param.getInspectionRouteId()));
        }
    }

    private void updateInspectionRelTimeAndStatus(InspectionRouteProcessParam param, int routeStatus, int originalRouteStatus) {
        InspectionRelStatusUpdateDTO inspectionRelStatusUpdateDTO = new InspectionRelStatusUpdateDTO();
        if (routeStatus == Constants.ROUTE_STATUS_WORK) {
            inspectionRelStatusUpdateDTO.setInspectionStartTime(param.getUpdateTime());
        } else if (routeStatus == Constants.ROUTE_STATUS_PAUSE) {
            inspectionRelStatusUpdateDTO.setPauseStartTime(param.getUpdateTime());
        }
        inspectionRelStatusUpdateDTO.setRouteStatus(routeStatus);
        inspectionRelStatusUpdateDTO.setOriginalRouteStatus(originalRouteStatus);
        inspectionRelStatusUpdateDTO.setModifier(param.getModifier());
        inspectionRelStatusUpdateDTO.setUpdateTime(param.getUpdateTime());
        inspectionRelStatusUpdateDTO.setId(param.getInspectionRouteSubId());
        int updateRelResult = inspectionRouteMapper.updateInspectionRelTimeAndStatusWithOriginalStatus(inspectionRelStatusUpdateDTO);
        if (updateRelResult == 0) {
            InspectionRelInfoVO failRelInfo = inspectionRouteMapper.selectInspectionRelById(param.getInspectionRouteSubId());
            if (failRelInfo != null) {
                log.error("IInspectionRouteServiceImpl updateInspectionRelTimeAndStatus update fail, failRelInfo:{}", JSON.toJSONString(failRelInfo));
            }
            throw new CustomException(String.format("该巡航路线点位状态不正确，不能开启, 路线点位编码：%s", param.getInspectionRouteSubId()));
        }
    }

    private void asyncMovePresetPosition(InspectionRouteProcessParam param) {
        //异步转向定位点
        CompletableFutureHelper.orTimeout(
                CompletableFuture.runAsync(() -> this.asyncMovePresetPositionLogic(param), THREAD_POOL_PRESET)
                        .exceptionally(e -> {
                            log.error("异步移动预置位失败, e :", e);
                            return null;
                        }),
                3, TimeUnit.SECONDS).exceptionally(e -> {
                    log.error("异步移动预置位超时, e:", e);
                    return null;
                }
        );
    }

    private void resumePatrolLogic(InspectionRelBriefInfoVO inspectionRelVO) {
        if (inspectionRelVO == null) {
            return;
        }
        // 如果暂停巡航状态开启，则切换设备之前先恢复之前的设备巡航
        if (Objects.equals(inspectionRelVO.getPauseCruiseFlag(), 1)) {
            pauseResumePatrolRemote(inspectionRelVO.getDeviceCode(), Constants.PATROL_PLAN_RESUME_STATUS);
        }
    }

    private void pauseResumePatrolRemote(String deviceCode, String pauseResumeStatus) {
        SttPatrolPauseReq param = new SttPatrolPauseReq();
        param.setDeviceCode(deviceCode);
        param.setPauseResumeStatus(pauseResumeStatus);
        try {
            Result result = iVideoPatrolPlanService.pauseResumePatrol(param);
            log.info("InspectionRouteServiceImpl pauseResumePatrol, param:{}, result:{}",
                    JSON.toJSONString(param), result == null ? "" : JSON.toJSONString(result));
        } catch (Exception e) {
            log.error("InspectionRouteServiceImpl pauseResumePatrol error, param:{}, e:", JSON.toJSONString(param), e);
        }
    }

    private void asyncMovePresetPositionLogic(InspectionRouteProcessParam param) {
        InspectionRelBriefInfoVO inspectionRelVO = inspectionRouteMapper.selectInspectionRelBriefInfoById(param.getInspectionRouteSubId());
        if (inspectionRelVO == null) {
            return;
        }
        // 异步异步获取云台锁转动到预置位
        String deviceCode = inspectionRelVO.getDeviceCode();
        String channelCode = inspectionRelVO.getChannelCode();
        String presetId = inspectionRelVO.getPresetId();
        try {
            // 如果暂停巡航状态开启，则先暂停设备巡航，再移动预置位
            if (Objects.equals(inspectionRelVO.getPauseCruiseFlag(), 1)) {
                pauseResumePatrolRemote(deviceCode, Constants.PATROL_PLAN_PAUSE_STATUS);
            }

            DoControlPresetReq request = new DoControlPresetReq();
            request.setDeviceCode(deviceCode);
            request.setChannelCode(channelCode);
            request.setPresetId(Integer.parseInt(presetId));
            request.setTenantId(param.getTenantId());
            request.setIndustryCode(param.getIndustryCode());
            request.setUserId(param.getUserId());
            Result result = videoControlAPI.doControlPresetNoRequest(request);
            log.info("InspectionRouteServiceImpl asyncMovePresetPosition request, request:{}, result:{}",
                    JSON.toJSONString(request), result == null ? "" : JSON.toJSONString(result));
        } catch (Exception e) {
            log.error("InspectionRouteServiceImpl asyncMovePresetPosition error, deviceCode:{}, channelCode:{}, presetId:{}, error:",
                    deviceCode, channelCode, presetId, e);
        }
    }


    private InspectionRelInfoVO doUpdateProcess(InspectionRouteProcessParam param) {
        log.info("InspectionRouteServiceImpl doProcess doUpdateProcess param:{}", JSON.toJSONString(param));
        /**
         * 1、更新巡检线路点表状态和巡检开始时间，用状态卡，防止重复更新
         * 2、同时更新其他环节的状态为空闲
         * 3、更新巡检线路表状态和时间
         * 4、先判断线路是否开启了自动暂停巡航，如果开启，则需要先把切换前的设备巡航恢复
         * 5、先判断线路是否开启了自动暂停巡航，如果开启，则需要把当前设备的巡航先暂停，然后异步获取云台锁转动到预置位
         */
        // 查询当前线路正在工作的点位
        InspectionRelBriefInfoVO inspectionRelVO = inspectionRouteMapper.selectWorkRelBriefInfoByRouteId(param.getInspectionRouteId());

        // 权限范围内根据点位id查询点位信息
        InspectionRelInfoVO inspectionRelWithPermissionVO = inspectionRouteMapper.selectInspectionRelByIdWithPermission(param);
        // 更新巡检线路点表状态和巡检开始时间，用状态卡，防止重复更新
        updateInspectionRelTimeAndStatus(param, Constants.ROUTE_STATUS_WORK, Constants.ROUTE_STATUS_IDLE);
        // 同时更新其他环节的状态为空闲
        inspectionRouteMapper.updateOtherRouteRelStatus(param, Constants.ROUTE_STATUS_IDLE);
        // 更新巡检线路表状态和时间
        updateInspectionStatus(param, Constants.ROUTE_STATUS_WORK, null);
        // 先判断线路是否开启了自动暂停巡航，如果开启，则需要先把切换前的设备巡航恢复
        resumePatrolLogic(inspectionRelVO);
        // 先判断线路是否开启了自动暂停巡航，如果开启，则需要把当前设备的巡航先暂停，然后异步获取云台锁转动到预置位
        asyncMovePresetPosition(param);
        return inspectionRelWithPermissionVO;
    }

    private InspectionRelInfoVO doPauseProcess(InspectionRouteProcessParam param) {
        log.info("InspectionRouteServiceImpl doProcess doPauseProcess param:{}", JSON.toJSONString(param));
        /**
         * 暂停接口
         * 1、sql获取当前环节的巡检线路id，更新巡检线路点表状态和暂停开始时间，用状态卡，防止重复更新
         * 2、更新巡查线路表状态和时间
         * 3、先判断线路是否开启了自动暂停巡航，如果开启，则需要先把切换前的设备巡航恢复
         */
        // 查询当前线路正在工作的点位
        InspectionRelBriefInfoVO inspectionRelVO = inspectionRouteMapper.selectWorkRelBriefInfoByRouteId(param.getInspectionRouteId());
        // 权限范围内根据点位id查询点位信息
        InspectionRelInfoVO inspectionRelWithPermissionVO = inspectionRouteMapper.selectInspectionRelByIdWithPermission(param);
        // 更新巡检线路点表状态和巡检开始时间，用状态卡，防止重复更新
        updateInspectionRelTimeAndStatus(param, Constants.ROUTE_STATUS_PAUSE, Constants.ROUTE_STATUS_WORK);
        // 更新巡检线路表状态和时间
        updateInspectionStatus(param, Constants.ROUTE_STATUS_PAUSE, Constants.ROUTE_STATUS_WORK);
        // 先判断线路是否开启了自动暂停巡航，如果开启，则需要先把切换前的设备巡航恢复
        resumePatrolLogic(inspectionRelVO);
        // 记录结束时间
        updateRouteRecord(param.getInspectionRouteId(), Constants.INSPECTION_ROUTE_PROCESS_PAUSE);
        return inspectionRelWithPermissionVO;
    }

    private InspectionRelInfoVO doRecoverProcess(InspectionRouteProcessParam param) {
        log.info("InspectionRouteServiceImpl doProcess doRecoverProcess param:{}", JSON.toJSONString(param));
        /**
         * 恢复接口
         * 1、获取当前环节的巡查线路点id，更新巡检线路表状态和累加暂停时长，用状态卡，防止重复更新
         * 3、更新巡查线路表状态和时间
         */
        int recoverResult = inspectionRouteMapper.recoverInspectionRelStatus(param);
        if (recoverResult == 0) {
            throw new CustomException(String.format("该巡航路线点位状态不正确，不能恢复, 路线点位编码：%s", param.getInspectionRouteSubId()));
        }
        // 权限范围内根据点位id查询点位信息
        InspectionRelInfoVO inspectionRelWithPermissionVO = inspectionRouteMapper.selectInspectionRelByIdWithPermission(param);
        // 更新巡检线路表状态和时间
        updateInspectionStatus(param, Constants.ROUTE_STATUS_WORK, Constants.ROUTE_STATUS_PAUSE);

        // 记录结束时间
        updateRouteRecord(param.getInspectionRouteId(), Constants.INSPECTION_ROUTE_PROCESS_RECOVER);
        return inspectionRelWithPermissionVO;
    }

    private InspectionRouteProcessVO doResetProcess(InspectionRouteProcessParam param) {
        log.info("InspectionRouteServiceImpl doProcess doResetProcess param:{}", JSON.toJSONString(param));
        /**
         * 重置
         * 1、更新第一条，更新巡检线路点表状态和巡检开始时间，清空暂停时间和时长，用状态卡，防止重复更新
         * 2、同时更新其他环节的状态为空闲
         * 3、更新巡检线路表状态和时间
         * 4、先判断线路是否开启了自动暂停巡航，如果开启，则需要先把切换前的设备巡航恢复
         * 5、先判断线路是否开启了自动暂停巡航，如果开启，则需要把当前设备的巡航先暂停，然后异步获取云台锁转动到预置位
         */
        InspectionRelInfoVO relInfo = inspectionRouteMapper.queryFirstTaskInfo(param);
        if (relInfo == null) {
            throw new CustomException("该客户巡航路线下没有配置巡查任务");
        }
        // 设置巡检点编码
        param.setInspectionRouteSubId(relInfo.getId());

        // 权限范围内根据点位id查询点位信息
        InspectionRelInfoVO inspectionRelWithPermissionVO = inspectionRouteMapper.selectInspectionRelByIdWithPermission(param);

        // 更新巡检线路点表状态和巡检开始时间，用状态卡，防止重复更新
        int recoverResult = inspectionRouteMapper.resetInspectionRelStatus(param);
        if (recoverResult == 0) {
            throw new CustomException(String.format("该巡航路线点位状态不正确，不能重置, 路线点位编码：%s", param.getInspectionRouteSubId()));
        }
        // 查询当前线路正在工作的点位
        InspectionRelBriefInfoVO inspectionRelVO = inspectionRouteMapper.selectWorkRelBriefInfoByRouteId(param.getInspectionRouteId());
        // 同时更新其他环节的状态为空闲
        inspectionRouteMapper.updateOtherRouteRelStatus(param, Constants.ROUTE_STATUS_IDLE);
        // 更新巡检线路表状态和时间
        updateInspectionStatus(param, Constants.ROUTE_STATUS_WORK, null);
        // 先判断线路是否开启了自动暂停巡航，如果开启，则需要先把切换前的设备巡航恢复
        resumePatrolLogic(inspectionRelVO);
        // 先判断线路是否开启了自动暂停巡航，如果开启，则需要把当前设备的巡航先暂停，然后异步获取云台锁转动到预置位
        asyncMovePresetPosition(param);

        // 返回第一条
        InspectionRouteProcessVO result = new InspectionRouteProcessVO();
        result.setInspectionRouteId(relInfo.getInspectionRouteId());
        result.setInspectionRouteSubId(relInfo.getId());
        result.setRouteStatus(Constants.ROUTE_STATUS_WORK);
        result.setSortSeq(relInfo.getSortSeq());

        if (Objects.isNull(inspectionRelWithPermissionVO)) {
            result.setAlertMessage(ALERT_MESSAGE);
        }

        // 记录结束时间
        updateRouteRecord(param.getInspectionRouteId(), Constants.INSPECTION_ROUTE_PROCESS_RESET);
        // 记录任务开启
        initRouteRecord(param.getInspectionRouteId());
        return result;
    }

    private void doFinishProcess(InspectionRouteProcessParam param) {
        log.info("InspectionRouteServiceImpl doProcess doFinishProcess param:{}", JSON.toJSONString(param));
        /**
         * 结束接口
         * 1、更新两表状态为空闲，清空巡查时间和暂停时间，暂停时长
         * 2、先判断线路是否开启了自动暂停巡航，如果开启，则需要先把切换前的设备巡航恢复
         */
        // 查询当前线路正在工作的点位
        InspectionRelBriefInfoVO inspectionRelVO = inspectionRouteMapper.selectWorkRelBriefInfoByRouteId(param.getInspectionRouteId());
        inspectionRouteMapper.finishInspectionInfo(param);
        inspectionRouteMapper.finishInspectionRelInfo(param);
        // 先判断线路是否开启了自动暂停巡航，如果开启，则需要先把切换前的设备巡航恢复
        resumePatrolLogic(inspectionRelVO);

        // 记录结束时间
        updateRouteRecord(param.getInspectionRouteId(), Constants.INSPECTION_ROUTE_PROCESS_FINISH);
    }

    @Override
    public Result<List<InspectionRelVO>> queryRelList(InspectionDTO inspectionDTO) {
        try {
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            inspectionDTO.setTenantId(loginUser.getTenantId());
            inspectionDTO.setIndustryCode(loginUser.getIndustryCode());
            inspectionDTO.setAppVerCode(loginUser.getAppVerCode());
            inspectionDTO.setPlatformCode(loginUser.getPlatformCode());
            List<InspectionRelVO> inspectionRelVOS = inspectionRouteMapper.queryInspectionRelList(inspectionDTO);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(inspectionRelVOS)) {
                LayerQueryDTO layerQueryDTO = new LayerQueryDTO();
                layerQueryDTO.setLayerTypeId(inspectionRelVOS.get(0).getLayerId());
                Result<List<LayerTreeVO>> listResult = this.queryLayerList(layerQueryDTO);
                inspectionRelVOS.get(0).setLayerInfoList(listResult.getData());
                for (int i = 0; i < inspectionRelVOS.size(); i++) {
                    InspectionRelVO inspectionRelVO = inspectionRelVOS.get(i);
                    Integer polygonNo = inspectionRelVO.getPolygonNo();
                    String polygonName = "电子围栏" + polygonNo;
                    inspectionRelVO.setPolygonName(polygonName);
                    inspectionRelVO.setSortSeq(i + 1);
                }
            }
            return Result.data(inspectionRelVOS);
        }catch (Exception e){
            log.info("查询异常：",e);
            return Result.fail("查询异常:"+e.getMessage());
        }
    }

    @Override
    public Result<List<LayerTreeVO>> queryLayerList(LayerQueryDTO layerQueryDTO) {

        try {
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            layerQueryDTO.setTenantId(loginUser.getTenantId());
            layerQueryDTO.setIndustryCode(loginUser.getIndustryCode());
            List<LayerTreeVO> tenantLayerListDTOS = inspectionRouteMapper.queryLayerList(layerQueryDTO);
            return Result.data(tenantLayerListDTOS);
        }catch (Exception e){
            log.info("查询客户下图层异常：",e);
            return Result.fail("查询客户下图层异常：" + e.getMessage());
        }

    }


    @Override
    public Result<List<PolygonPatchConfigVO>> queryPolygonPathInfo(PolygonPatchConfigDTO polygonPatchConfigDTO) {

        if (CollectionUtils.isEmpty(polygonPatchConfigDTO.getPolygonPatchList())){
            if ("".equals(polygonPatchConfigDTO.getLayerId()) || polygonPatchConfigDTO.getLayerId() == null){
                return Result.fail("图层ID不能为空！");
            }
            if ("".equals(polygonPatchConfigDTO.getFeatureId()) || polygonPatchConfigDTO.getFeatureId() == null){
                return Result.fail("图斑ID不能为空！");
            }
        }
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        polygonPatchConfigDTO.setTenantId(loginUser.getTenantId());
        polygonPatchConfigDTO.setIndustryCode(loginUser.getIndustryCode());
        List<PolygonPatchConfigVO> polygonPath = inspectionRouteMapper.queryPolygonPathInfo(polygonPatchConfigDTO);
        if (CollectionUtils.isEmpty(polygonPath)){
            return Result.fail("未查询到电子围栏信息！");
        }
        if (!CollectionUtils.isEmpty(polygonPatchConfigDTO.getPolygonPatchList())){
            return Result.data(polygonPath);
        }else {
            String polygonName = "";
            PolygonPatchConfigVO polygonPatchConfigVO = polygonPath.get(0);
            if(polygonPatchConfigVO.getPolygonNo() != null){
                polygonName = "电子围栏" + polygonPatchConfigVO.getPolygonNo();
            }else {
                Result<Integer> integerResult = this.selectMaxPolygonNo(polygonPatchConfigVO);
                polygonName = "电子围栏" + integerResult.getData();
            }
            polygonPatchConfigVO.setPolygonName(polygonName);
            List<PolygonPatchConfigVO> resultList = new ArrayList<>();
            resultList.add(polygonPatchConfigVO);
            return Result.data(resultList);
        }

    }

    private Result<String> checkedInspectionParam(InspectionRouteFenceVO dto) {
        if (CollectionUtils.isEmpty(dto.getInspectionRelList())){
            return Result.fail("巡检路线中至少添加一个巡检点");
        }
        if ("".equals(dto.getInspectionRouteName()) || dto.getInspectionRouteName() == null){
            return Result.fail("巡检路线名称不能为空！");
        }
        if (dto.getInspectionRouteName().length() > 30){
            return Result.fail("巡检线路名称最多支持输入30个字。");
        }
        //判断巡检路线名称是否重复
        InspectionDTO inspectionDTO = new InspectionDTO();
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        inspectionDTO.setInspectionRouteName(dto.getInspectionRouteName());
        inspectionDTO.setTenantId(loginUser.getTenantId());
        inspectionDTO.setIndustryCode(loginUser.getIndustryCode());
        inspectionDTO.setInspectionRouteId(dto.getInspectionRouteId());
        int counts = inspectionRouteMapper.selectInspectionByName(inspectionDTO);
        if (counts > 0) {
            return Result.fail("巡检路线名称已存在！");
        }
        //校验停留时长
        for (InspectionRelVO inspectionRelVO : dto.getInspectionRelList()){
            if (null == inspectionRelVO.getDurationTime()){
                throw  new CustomException("停留时长不能为空！");
            }
            if (Constants.ONE.equals(inspectionRelVO.getDurationUnit()) &&
                    (inspectionRelVO.getDurationTime() < Constants.NUMBER_ONE || inspectionRelVO.getDurationTime() > Constants.NUMBER_SIXTY)){
                throw  new CustomException("停留时长输入范围为1-60秒");
            }
            if (Constants.TWO.equals(inspectionRelVO.getDurationUnit()) &&
                    (inspectionRelVO.getDurationTime() < Constants.NUMBER_ONE || inspectionRelVO.getDurationTime() > Constants.NUMBER_TEN)){
                throw  new CustomException("停留时长输入范围为1-10分钟");
            }
        }
        return Result.success("校验通过！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> insertInspectionRoute(InspectionRouteFenceVO dto) {

        try{
            //校验参数
            Result<String> checkedResult = checkedInspectionParam(dto);
            if (!Objects.equals(200,checkedResult.getCode())){
                return checkedResult;
            }
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            String tenantId = loginUser.getTenantId();
            String industryCode = loginUser.getIndustryCode();
            String appVerCode = loginUser.getAppVerCode();
            String userName = loginUser.getUserName();

            // 查询客户+行业有效线路数量
            int routeCount = inspectionRouteMapper.selectValidRouteCount(tenantId, industryCode);
            if (routeCount >= 20) {
                return Result.fail("最多可新增20个地块巡检路线。");
            }

            List<InspectionRelVO> inspectionRelList = dto.getInspectionRelList();
            Result<String> result = this.checkedPolygon(inspectionRelList);
            if (!Objects.equals(Constants.CODE_SUCCESS,result.getCode())){
                return result;
            }

            //先插入巡检线路表
            InspectionVO inspectionVO = new InspectionVO();
            inspectionVO.setInspectionRouteName(dto.getInspectionRouteName());
            inspectionVO.setPauseCruiseFlag(dto.getPauseCruiseFlag());
            inspectionVO.setRouteStatus("0");
            inspectionVO.setTenantId(tenantId);
            inspectionVO.setIndustryCode(industryCode);
            inspectionVO.setAppVerCode(appVerCode);
            inspectionVO.setCreator(userName);
            inspectionRouteMapper.insertInspectionRoute(inspectionVO);

            return this.dealInspectionPoint(inspectionRelList, inspectionVO.getId(),1);
        }catch (Exception e){
            log.info("新增巡检线路失败：",e);
            throw new CustomException("新增巡检线路失败!");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteInspectionRoute(InspectionDTO inspectionDTO) {
        try {
            if (null == inspectionDTO.getInspectionRouteId()){
                return Result.fail("巡检路线配置ID不能为空！");
            }
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            inspectionDTO.setTenantId(loginUser.getTenantId());
            inspectionDTO.setIndustryCode(loginUser.getIndustryCode());
            //先查询巡检路线状态
            InspectionVO inspectionVO = inspectionRouteMapper.selectInspectionById(inspectionDTO.getInspectionRouteId(), inspectionDTO.getTenantId(), inspectionDTO.getIndustryCode());
            if (inspectionVO == null){
                return Result.fail("要删除的巡检路线不存在！");
            } else if (Constants.ONE.equals(inspectionVO.getRouteStatus()) || Constants.TWO.equals(inspectionVO.getRouteStatus())){
                return Result.fail("空闲状态下的巡检路线才能删除！");
            }
            //先逻辑删除巡检路线表
            inspectionRouteMapper.deleteInspectionRoute(inspectionDTO);
            //物理删除巡检路线关联电子围栏表数据
            inspectionRouteMapper.deleteInspectionRelInfo(inspectionDTO);
            return Result.success("删除成功！");
        }catch (Exception e){
            log.info("删除巡检线路异常：",e);
            throw new CustomException("删除巡检线路异常：" + e.getMessage());
        }

    }

    @Override
    public Result<String> createInspectionName() {
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        String tenantId = loginUser.getTenantId();
        String industryCode = loginUser.getIndustryCode();
        String inspectionName = inspectionRouteMapper.createInspectionName(tenantId, industryCode);
        return Result.data(inspectionName);
    }

    private Result<String> checkedPolygon(List<InspectionRelVO> inspectionRelList){
        //已经存在电子围栏的图斑集合
        List<String> existFeatureList = new ArrayList<>();
        //先判断图层图斑下是否存在电子围栏
        PolygonPatchConfigDTO polygonPatchConfigDTO = new PolygonPatchConfigDTO();
        List<PolygonPatchConfigDTO> polygonPatchConfigDTOList = new ArrayList<>();
        inspectionRelList.forEach(inspectionRelVO -> {
            if ("".equals(inspectionRelVO.getPolygonId()) || inspectionRelVO.getPolygonId() == null){
                PolygonPatchConfigDTO polygonPatchDTO = new PolygonPatchConfigDTO();
                polygonPatchDTO.setLayerId(inspectionRelVO.getLayerId());
                polygonPatchDTO.setFeatureId(inspectionRelVO.getFeatureId());
                polygonPatchConfigDTOList.add(polygonPatchDTO);
            }
        });
        if (!CollectionUtils.isEmpty(polygonPatchConfigDTOList)){
            polygonPatchConfigDTO.setPolygonPatchList(polygonPatchConfigDTOList);
            Result<List<PolygonPatchConfigVO>> result = this.queryPolygonPathInfo(polygonPatchConfigDTO);
            if (Objects.equals(Constants.CODE_SUCCESS,result.getCode())) {
                //只有存在电子围栏时CODE为200
                List<PolygonPatchConfigVO> resultData = result.getData();
                resultData.forEach(polygonPatchConfigVO -> {
                    existFeatureList.add(polygonPatchConfigVO.getTbbh());
                });
            }
        }
        if (!CollectionUtils.isEmpty(existFeatureList)){
            String featureName = StringUtils.join(existFeatureList, ",");
            return Result.fail(featureName + "下已存在电子围栏,不能重复新增，请删除对应图斑重新添加巡检点！");
        }
        return Result.success("校验通过！");
    }

    //type 1 新增 2-编辑
    private Result<String> dealInspectionPoint(List<InspectionRelVO> inspectionRelList,Long inspectionRouteId,Integer type){

        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        String tenantId = loginUser.getTenantId();
        String industryCode = loginUser.getIndustryCode();
        String appVerCode = loginUser.getAppVerCode();
        String userName = loginUser.getUserName();

        List<PolygonPatchConfigVO> polygonPatchList = new ArrayList<>();
        for (InspectionRelVO inspectionRelVO : inspectionRelList){
            //设置巡检路线ID
            inspectionRelVO.setInspectionRouteId(inspectionRouteId);
            inspectionRelVO.setCreator(userName);
            inspectionRelVO.setTenantId(tenantId);
            inspectionRelVO.setIndustryCode(industryCode);
            inspectionRelVO.setAppVerCode(appVerCode);
            //判断电子围栏ID是否存在
            if ("".equals(inspectionRelVO.getPolygonId()) || inspectionRelVO.getPolygonId() == null){

                PolygonPatchConfigVO polygonPatch;
                boolean flag = true;
                //判断是否有重复的电子围栏--不能重复添加电子围栏信息
                if (!CollectionUtils.isEmpty(polygonPatchList)){
                    for (PolygonPatchConfigVO polygonPatchConfigVO : polygonPatchList) {
                        if (inspectionRelVO.getLayerId().equals(polygonPatchConfigVO.getLayerId())
                                && inspectionRelVO.getFeatureId().equals(polygonPatchConfigVO.getFeatureId())) {
                            inspectionRelVO.setPolygonId(polygonPatchConfigVO.getPolygonId());
                            flag = false;
                            break;
                        }
                    }
                    if (flag){
                        polygonPatch = new PolygonPatchConfigVO();
                        BeanUtils.copyProperties(inspectionRelVO, polygonPatch);
                        polygonPatch.setPolygonId(UUID.randomUUID().toString().replace("-", ""));
                        polygonPatch.setCreator(userName);
                        polygonPatch.setTenantId(tenantId);
                        polygonPatch.setIndustryCode(industryCode);
                        polygonPatch.setAppVerCode(appVerCode);
                        polygonPatch.setPolygonNo(inspectionRelVO.getPolygonNo());
                        polygonPatchList.add(polygonPatch);
                        //设置电子围栏ID
                        inspectionRelVO.setPolygonId(polygonPatch.getPolygonId());
                    }
                }else {
                    //不存在 则需要插入电子围栏表-第一条信息不用校验重复
                    polygonPatch = new PolygonPatchConfigVO();
                    BeanUtils.copyProperties(inspectionRelVO, polygonPatch);
                    polygonPatch.setPolygonId(UUID.randomUUID().toString().replace("-", ""));
                    polygonPatch.setCreator(userName);
                    polygonPatch.setTenantId(tenantId);
                    polygonPatch.setIndustryCode(industryCode);
                    polygonPatch.setAppVerCode(appVerCode);
                    polygonPatch.setPolygonNo(inspectionRelVO.getPolygonNo());
                    polygonPatchList.add(polygonPatch);
                    //设置电子围栏ID
                    inspectionRelVO.setPolygonId(polygonPatch.getPolygonId());
                }

            }
        }
        //批量插入巡检路线电子围栏关联表中
        inspectionRouteMapper.insertInspectionRelList(inspectionRelList);
        if (!CollectionUtils.isEmpty(polygonPatchList)){
            //批量插入电子围栏表
            inspectionRouteMapper.insertPolygonPatchBatch(polygonPatchList);
        }
        if (Objects.equals(Constants.ONE_NUM,type)){
            return Result.success("新增成功！");
        }
        return Result.success("编辑成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateInspectionRoute(InspectionRouteFenceVO dto) {

        try {
            if (dto.getInspectionRouteId() == null){
                return Result.fail("巡检路线ID不能为空！");
            }
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            String tenantId = loginUser.getTenantId();
            String industryCode = loginUser.getIndustryCode();
            String platformCode = loginUser.getPlatformCode();
            String userName = loginUser.getUserName();
            //先查询巡检路线状态
            InspectionVO inspectionVO = inspectionRouteMapper.selectInspectionById(dto.getInspectionRouteId(), tenantId, industryCode);
            if (inspectionVO == null){
                return Result.fail("要编辑的巡检路线不存在！");
            } else if (Constants.ONE.equals(inspectionVO.getRouteStatus()) || Constants.TWO.equals(inspectionVO.getRouteStatus())){
                return Result.fail("巡查状态下的巡检路线不能编辑！");
            }
            //校验参数
            Result<String> checkedResult = checkedInspectionParam(dto);
            if (!Objects.equals(200,checkedResult.getCode())){
                return checkedResult;
            }
            List<InspectionRelVO> inspectionRelList = dto.getInspectionRelList();

            Result<String> res = this.checkedPolygon(inspectionRelList);
            if (!Objects.equals(Constants.CODE_SUCCESS,res.getCode())){
                return res;
            }

            //先修改巡检线路名称
            inspectionVO.setInspectionRouteName(dto.getInspectionRouteName());
            inspectionVO.setPauseCruiseFlag(dto.getPauseCruiseFlag());
            inspectionVO.setModifier(userName);
            inspectionRouteMapper.updateInspectionInfo(inspectionVO);

            //先获取原有的巡检点
            List<InspectionRelVO> originList = inspectionRelList.stream().filter(inspectionRelVO -> null != inspectionRelVO.getId()).collect(Collectors.toList());
            //获取新增的巡检点--包含已存在的电子围栏和新标绘的电子围栏
            List<InspectionRelVO> newInspectionPointList = inspectionRelList.stream().filter(inspectionRelVO -> null == inspectionRelVO.getId()).collect(Collectors.toList());

            Result<String> result = null;
            if (!CollectionUtils.isEmpty(originList)){
                //查询原巡检路线下的所有巡检点
                InspectionDTO inspectionDTO = new InspectionDTO();
                inspectionDTO.setInspectionRouteId(dto.getInspectionRouteId());
                inspectionDTO.setTenantId(tenantId);
                inspectionDTO.setIndustryCode(industryCode);
                inspectionDTO.setPlatformCode(platformCode);
                List<Long> allIdList = inspectionRouteMapper.queryInspectionRelIdList(inspectionDTO);

                List<Long> originIdList = originList.stream().map(InspectionRelVO::getId).collect(Collectors.toList());
                //需要删除的巡检点
                List<Long> needDeleteRelInfoId = allIdList.stream().filter(item -> !originIdList.contains(item)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(needDeleteRelInfoId)){
                    //先删除巡检路线电子围栏关联表中 减少的巡检点
                    inspectionRouteMapper.deleteInspectionRelById(needDeleteRelInfoId);
                }
                //处理需要修改的数据
                originList.forEach(inspectionRelVO -> inspectionRelVO.setModifier(userName));
                inspectionRouteMapper.updateInspectionRelBatch(originList);
                if (!CollectionUtils.isEmpty(newInspectionPointList)){
                    result = dealInspectionPoint(newInspectionPointList,dto.getInspectionRouteId(),2);
                }else {
                    result = Result.success("编辑成功！");
                }
            }else {
                //如果原有的巡检点是空的--则全部插入巡检路线关联表
                result = dealInspectionPoint(inspectionRelList, dto.getInspectionRouteId(),2);
            }

            return result;
        }catch (Exception e){
            log.info("编辑巡检路线异常:",e);
            throw new CustomException("编辑巡检路线异常:" + e.getMessage());
        }
    }

    @Override
    public Result<String> deletePolygonChecked(String polygonId) {
        if ("".equals(polygonId) || null == polygonId){
            return Result.fail("电子围栏ID不能为空！");
        }
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        String tenantId = loginUser.getTenantId();
        String industryCode = loginUser.getIndustryCode();
        InspectionRelVO inspectionRelVO = new InspectionRelVO();
        inspectionRelVO.setTenantId(tenantId);
        inspectionRelVO.setIndustryCode(industryCode);
        inspectionRelVO.setPolygonId(polygonId);
        int count = inspectionRouteMapper.queryInspectionByPolygonId(inspectionRelVO);
        if (count > 0){
            return Result.fail(201,"电子围栏已关联巡检路线，不能删除！");
        }else {
            return Result.success("未关联巡检路线，可以删除！");


        }
    }

    @Override
    public Result<Integer> selectMaxPolygonNo(PolygonPatchConfigVO polygonPatchConfigVO) {
        if ("".equals(polygonPatchConfigVO.getDeviceCode()) || polygonPatchConfigVO.getDeviceCode() == null){
            return Result.fail("设备编码不能为空！");
        }
        if ("".equals(polygonPatchConfigVO.getChannelCode()) || polygonPatchConfigVO.getChannelCode() == null){
            return Result.fail("通道编码不能为空！");
        }
        if ("".equals(polygonPatchConfigVO.getPresetId()) || polygonPatchConfigVO.getPresetId() == null){
            return Result.fail("预置位编码不能为空！");
        }
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        polygonPatchConfigVO.setTenantId(loginUser.getTenantId());
        polygonPatchConfigVO.setIndustryCode(loginUser.getIndustryCode());
        int maxPolygonNo = inspectionRouteMapper.selectMaxPolygonNo(polygonPatchConfigVO);
        return Result.data(maxPolygonNo+1);
    }

    @Override
    public Result<List<PolygonPatchConfigVO>> queryPolygonList(PolygonPatchConfigDTO dto) {
        if (StringUtils.isEmpty(dto.getLayerId())){
            return Result.fail("图层ID不能为空！");
        }
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        dto.setTenantId(loginUser.getTenantId());
        dto.setIndustryCode(loginUser.getIndustryCode());
        List<PolygonPatchConfigVO> polygonList = inspectionRouteMapper.queryPolygonList(dto);
        List<PolygonPatchConfigVO> resultList = new ArrayList<>(100);
        if (!CollectionUtils.isEmpty(polygonList)){
            //先取出配置在巡检路线中的电子围栏
            List<PolygonPatchConfigVO> configList = polygonList.stream().filter(polygonPatchConfigVO -> polygonPatchConfigVO.getInspectionRelId() != null).collect(Collectors.toList());
            //如果配置在巡检路线中的电子围栏为空
            if (CollectionUtils.isEmpty(configList)){
                //直接从所有数据中取出前100条
                if (polygonList.size() > 100){
                    resultList = polygonList.subList(0,100);
                }else {
                    resultList = polygonList;
                }
            }else {
                resultList.addAll(configList);
                //取出没配置在巡检路线表中的电子围栏
                List<PolygonPatchConfigVO> noConfigList = polygonList.stream().filter(polygonPatchConfigVO -> polygonPatchConfigVO.getInspectionRelId() == null).collect(Collectors.toList());
                int end = 100 - configList.size();
                if (noConfigList.size() > end){
                    resultList.addAll(noConfigList.subList(0,end));
                }else {
                    resultList.addAll(noConfigList);
                }
            }
        }
        return Result.data(resultList);
    }

    @Override
    public Result<InspectionNumVO> getInspectionRouteNum(InspectionNumParam param) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        int routeNum = inspectionRouteMapper.getInspectionRouteNum(user.getTenantId(), user.getIndustryCode(),
                param.getCreateTimeBegin(), param.getCreateTimeEnd());
        InspectionNumVO resultVo = new InspectionNumVO();
        resultVo.setAllNum(routeNum);
        return Result.data(resultVo);
    }

    @Override
    public Result<InspectionNumVO> getInspectionPolygonNum(InspectionNumParam param) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        int polygonNum = inspectionRouteMapper.getInspectionPolygonNum(user.getTenantId(), user.getIndustryCode(),
                param.getCreateTimeBegin(), param.getCreateTimeEnd());
        InspectionNumVO resultVo = new InspectionNumVO();
        resultVo.setAllNum(polygonNum);
        return Result.data(resultVo);
    }

    @Override
    public Result<InspectionRouteRecordVO> queryRouteRecordInfo(InspectionRouteRecordParam param) {
        if (param == null || param.getStartTime() == null || param.getEndTime() == null) {
            throw new CustomException("必填参数不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        String startTime = param.getStartTime();
        String endTime = param.getEndTime();

        InspectionRouteRecordVO record = new InspectionRouteRecordVO();
        InspectionRouteRecordVO inspectionInfo = inspectionRouteMapper.queryInspectionCountInfo(tenantId, industryCode, startTime, endTime);
        if (inspectionInfo != null) {
            record.setInspectionRouteCount(inspectionInfo.getInspectionRouteCount());
            record.setInspectPlotNum(inspectionInfo.getInspectPlotNum());
        }

        InspectionRouteRecordVO recordInfo = inspectionRouteMapper.queryInspectionRecordInfo(tenantId, industryCode, startTime, endTime);
        if (recordInfo != null) {
            record.setInspectionTimes(recordInfo.getInspectionTimes());
            BigDecimal seconds = new BigDecimal(String.valueOf(recordInfo.getInspectionDuration()));
            // 秒转小时
            BigDecimal hours = seconds.divide(new BigDecimal("3600"), 0, RoundingMode.HALF_UP);
            record.setInspectionDuration(hours.longValue());
        }
        return Result.data(record);
    }


    /**
     * 巡检进度入口
     * @param param
     * @return
     */
    @Override
    @DataScope(tenantColumn = "o.tenant_id", industryCode = "o.industry_code", deviceColumn = "rel.device_code")
    public Result<InspectionRouteProcessVO> doInspectionProgress(InspectionRouteProcessParam param) {
        log.info("巡检进度查询开始  doInspectionProgress param: {}", JSON.toJSONString(param));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 填充登录信息
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        param.setUserId(user.getUserId());
        param.setModifier(user.getUserName());
        param.setUpdateTime(DateUtils.getNowDate());
        //查询巡检路线信息
        InspectionVO inspectionVO = inspectionRouteMapper.selectRouteById(param);
        if (Objects.isNull(inspectionVO)) {
            throw new CustomException(String.format("操作失败，该巡航路线不存在, 巡检路线编码：%s", param.getInspectionRouteId()));
        }
        //巡检路线状态空闲
        if (Objects.equals(inspectionVO.getRouteStatus(), String.valueOf(Constants.ROUTE_STATUS_IDLE))) {
            log.info("该巡检路线空闲中，调用开始流程，开始第一个巡查点任务，巡检路线编码：{}", param.getInspectionRouteId());
            //调用开始流程
            param.setStatus(Constants.INSPECTION_ROUTE_PROCESS_START);
            return this.doProcess(param);
        }
        //巡检路线状态是工作中
        else if (Objects.equals(inspectionVO.getRouteStatus(), String.valueOf(Constants.ROUTE_STATUS_WORK))) {
            log.info("该巡检路线处于工作中，巡检路线编码：{}", param.getInspectionRouteId());
            return this.doPatrol(param);
        }
        else {
            log.info("该巡检路线处于暂停中，巡检路线编码：{}", param.getInspectionRouteId());
            return doPauseLogic(param);
        }
    }

    public Result<InspectionRouteProcessVO> doPauseLogic(InspectionRouteProcessParam param) {
        InspectionRelInfoVO relInfoVO = inspectionRouteMapper.selectInspectionRelByRouteIdAndStatus(param.getInspectionRouteId(), Constants.ROUTE_STATUS_PAUSE);
        if (Objects.isNull(relInfoVO)) {
            throw new CustomException("巡检路线不存在");
        }
        param.setInspectionRouteSubId(relInfoVO.getId());
        InspectionRouteProcessVO resultVO = new InspectionRouteProcessVO();
        Date nowDate = DateUtils.getNowDate();
        //重置时长
        long resetTime = this.getResetTime();
        // 计算在该环节耗费总时长，单位：秒
        long inspectionTime = DateUtil.between(relInfoVO.getPauseStartTime(), nowDate, DateUnit.SECOND);
        // 当前时间 - 暂停开始时间 <= 重置时间，返回当前节点信息
        if (inspectionTime <= resetTime) {
            resultVO.setInspectionRouteId(relInfoVO.getInspectionRouteId());
            resultVO.setInspectionRouteSubId(relInfoVO.getId());
            resultVO.setRouteStatus(relInfoVO.getRouteStatus());
            resultVO.setSortSeq(relInfoVO.getSortSeq());
        } else {
            // 当前时间 - 暂停开始时间 > 重置时间，走结束流程，返回999
            log.info("IInspectionRouteServiceImpl doPauseLogic，id：{}，subId：{}", param.getInspectionRouteId(), param.getInspectionRouteSubId());
            this.doFinishProcess(param);
            resultVO.setInspectionRouteId(relInfoVO.getInspectionRouteId());
            resultVO.setInspectionRouteSubId(relInfoVO.getId());
            resultVO.setRouteStatus(Constants.ROUTE_STATUS_IDLE);
            resultVO.setSortSeq(999);
        }
        return Result.data(resultVO);
    }

    /**
     * 巡检状态是工作中
     * 1.对比当前时间>巡查开始时间+停留时间+暂停时间 且 小于重置时间（长时间不操作的上限时间）
     *     true）查询是否还有下一巡检点任务，如果没有则调用结束接口，如果有则调用变更接口开始下一巡检点任务
     *     false）判断当前环节是否还在巡查时间范围内，如果在则直接返回当前序号id，如果不在则调用重置流程
     * @return 返回当前正在巡检点id
     */
    public Result<InspectionRouteProcessVO> doPatrol(InspectionRouteProcessParam param) {
        InspectionRelInfoVO relInfoVO = inspectionRouteMapper.selectInspectionRelByRouteIdAndStatus(param.getInspectionRouteId(), Constants.ROUTE_STATUS_WORK);
        if (Objects.isNull(relInfoVO)) {
            throw new CustomException("巡检路线不存在");
        }
        param.setInspectionRouteSubId(relInfoVO.getId());
        InspectionRouteProcessVO resultVO = new InspectionRouteProcessVO();
        Date nowDate = DateUtils.getNowDate();
        // 计算在该巡检点巡检时长
        Date patrolTime = this.calcPortalTime(relInfoVO);
        // 重置时长
        long resetTime = this.getResetTime();
        // 计算在该环节耗费总时长，单位：秒
        long inspectionTime = DateUtil.between(relInfoVO.getInspectionStartTime(), nowDate, DateUnit.SECOND);
        //对比当前时间 > 巡查开始时间 + 停留时间 + 暂停时间  且 当前时间 - 巡检开始时间 小于 重置时间
        if (DateUtil.compare(nowDate, patrolTime) > 0 && inspectionTime < resetTime) {
            //当前巡查点巡查时长已达上限，开始下一个巡查点任务
            resultVO = this.doNext(param, relInfoVO);
        }
        else {
            //当前巡查点还在巡查范围内，则直接返回当前巡检点
            if (DateUtil.compare(nowDate, patrolTime) < 0) {
                resultVO.setInspectionRouteId(relInfoVO.getInspectionRouteId());
                resultVO.setInspectionRouteSubId(relInfoVO.getId());
                resultVO.setRouteStatus(relInfoVO.getRouteStatus());
                resultVO.setSortSeq(relInfoVO.getSortSeq());
            }
            //超过时间上限，则走重置逻辑重新开始，重头播放
            else {
                resultVO = this.doResetProcess(param);
            }
        }
        return Result.data(resultVO);
    }

    /**
     * 获取重置时长
     * @return
     */
    private long getResetTime() {
        AjaxResult ajaxResult = systemFeignClient.getConfValue(Constants.CONFIG_INSPECTION_RESET_TIME);
        if (Constants.SUCCESS_CODE.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (Objects.isNull(sysConfig)) {
                throw new CustomException("重置时长未配置，请配置后重试");
            }
            return Long.parseLong(sysConfig.getConfigValue());
        }
        else {
            throw new CustomException("获取重置时长失败，请稍后重试！");
        }
    }

    /**
     * 开始下一巡查点任务
     * 判断是否还有下一巡检点任务，如果有则开始做下一巡检点任务，如果没用则结束流程
     * @param param
     * @param relInfoVO
     */
    public InspectionRouteProcessVO doNext(InspectionRouteProcessParam param, InspectionRelInfoVO relInfoVO) {
        InspectionRouteProcessVO resultVO = new InspectionRouteProcessVO();
        //获取下一巡检点信息
        InspectionRelInfoVO nextRelInfoVo = inspectionRouteMapper.findNext(relInfoVO);
        //没有下一巡检点，调用结束接口
        if (Objects.isNull(nextRelInfoVo)) {
            log.info("该巡查任务最后一个巡检点已完成巡检，开始调用结束流程接口，巡检路线编码：{}，巡检路线点编码：{}", param.getInspectionRouteId(), param.getInspectionRouteSubId());
            this.doFinishProcess(param);
            resultVO.setInspectionRouteId(relInfoVO.getInspectionRouteId());
            resultVO.setInspectionRouteSubId(relInfoVO.getId());
            resultVO.setRouteStatus(Constants.ROUTE_STATUS_IDLE);
            resultVO.setSortSeq(999);
        }
        //有下一个巡检点，则调用变更接口
        else {
            log.info("该巡查任务已完成当前巡检点任务，即将开始下一个巡检点任务，巡检路线编码：{}，当前巡检点编码：{}，下一巡检点编码：{}", param.getInspectionRouteId(), param.getInspectionRouteSubId(), nextRelInfoVo.getId());
            param.setInspectionRouteSubId(nextRelInfoVo.getId());
            InspectionRelInfoVO inspectionRelWithPermissionVO = this.doUpdateProcess(param);
            resultVO.setInspectionRouteSubId(nextRelInfoVo.getId());
            resultVO.setInspectionRouteId(nextRelInfoVo.getInspectionRouteId());
            resultVO.setRouteStatus(Constants.ROUTE_STATUS_WORK);
            resultVO.setSortSeq(nextRelInfoVo.getSortSeq());
            // 如果根据点位id查不到权限内的点位信息，则认为没有权限，给出提示
            if (Objects.isNull(inspectionRelWithPermissionVO)) {
                resultVO.setAlertMessage(ALERT_MESSAGE);
            }
        }
        return resultVO;
    }

    /**
     * 计算在该巡检点累计巡检时长
     * @param relInfoVO
     * @return
     */
    private Date calcPortalTime(InspectionRelInfoVO relInfoVO) {
        //巡查时间
        Date patrolTime = relInfoVO.getInspectionStartTime();
        //巡查点巡查总时长 = 巡查时间 + 停留时间 + 暂停时长
        //累加停留时长
        if (Objects.nonNull(relInfoVO.getDurationTime()) && relInfoVO.getDurationTime() > 0) {
            //停留时间单位 单位 秒
            if (Objects.equals(Constants.ONE, relInfoVO.getDurationUnit())) {
                patrolTime = DateUtil.offsetSecond(patrolTime, relInfoVO.getDurationTime());
            }
            //停留时间单位 单位 分
            else {
                patrolTime = DateUtil.offsetMinute(patrolTime, relInfoVO.getDurationTime());
            }
        }
        //累加暂停时长
        if (Objects.nonNull(relInfoVO.getPauseTime()) && relInfoVO.getPauseTime() > 0) {
            patrolTime = DateUtil.offsetSecond(patrolTime, relInfoVO.getPauseTime());
        }
        return patrolTime;
    }

    /**
     * 查询巡检点下图层信息
     * @param param
     * @return
     */
    public Result<PageInfo<InspectionRouteRelLayerSdkVO>> queryInspectionLayerPage(InspectionRouteRelLayerPageSdkParam param) {
        if (null == param.getPageNum() || param.getPageNum() <= com.chinatower.platform.constants.Constants.ZERO_NUM) {
            param.setPageNum(com.chinatower.platform.constants.Constants.ONE_NUM);
        }
        if (null == param.getPageSize() || param.getPageSize() <= com.chinatower.platform.constants.Constants.ZERO_NUM) {
            param.setPageSize(com.chinatower.platform.constants.Constants.TEN_NUM);
        }
        PageMethod.startPage(param.getPageNum(), param.getPageSize());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        param.setPlatformCode(user.getPlatformCode());
        List<InspectionRouteRelLayerSdkVO> resultList = inspectionRouteMapper.queryInspectionLayer(param);
        //获取地块、坐落单位信息
        if (!CollectionUtils.isEmpty(resultList)) {
            Map<String, List<InspectionRouteRelLayerSdkVO>> layerIdMap = resultList.stream().collect(Collectors.groupingBy(InspectionRouteRelLayerSdkVO::getLayerId));
            layerIdMap.entrySet().forEach(map -> {
                GetXjxlFeaturesByIdsReq req = new GetXjxlFeaturesByIdsReq();
                req.setLayerID(map.getKey());
                req.setTbbhs(map.getValue().stream().map(InspectionRouteRelLayerSdkVO::getFeatureId).distinct().collect(Collectors.toList()));
                log.info("/getFeatures/getXjxlFeaturesByIds  req:{}", JSON.toJSONString(req));
                Result<GeojsonStruckRes> result = gisLayerAPI.getXjxlFeaturesByIds(req);
                log.info("/getFeatures/getXjxlFeaturesByIds  resp:{}", JSON.toJSONString(result));
                if (Objects.nonNull(result.getData()) && Objects.nonNull(result.getData().getGeoData())) {
                    Object featuresObj = JSON.parseObject(JSON.toJSONString(result.getData().getGeoData())).get(Constants.PARAM_FEATURES);
                    if (Objects.nonNull(featuresObj)) {
                        JSON.parseArray(JSON.toJSONString(featuresObj)).stream().forEach(item -> {
                            Object obj = JSON.parseObject(JSON.toJSONString(item)).get(Constants.PARAM_PROPERTIES);
                            if (Objects.nonNull(obj)) {
                                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(obj));
                                resultList.stream().filter(filter -> Objects.equals(String.valueOf(jsonObject.get(Constants.PARAM_BSM)), filter.getFeatureId())).forEach(resp -> {
                                    resp.setLandName(Objects.nonNull(jsonObject.get(Constants.PARAM_DLMC)) ? String.valueOf(jsonObject.get(Constants.PARAM_DLMC)) : "");
                                    resp.setLocationUnit(Objects.nonNull(jsonObject.get(Constants.PARAM_ZLDWMC)) ? String.valueOf(jsonObject.get(Constants.PARAM_ZLDWMC)) : "");
                                });
                            }
                        });
                    }
                }
            });
        }
        PageInfo<InspectionRouteRelLayerSdkVO> pageInfo = new PageInfo<>(resultList);
        Result<PageInfo<InspectionRouteRelLayerSdkVO>> result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }
}
