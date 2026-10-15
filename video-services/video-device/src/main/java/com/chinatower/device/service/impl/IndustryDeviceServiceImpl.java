package com.chinatower.device.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.controller.IotDeviceController;
import com.chinatower.device.dto.RadarTargetFollowLookToResult;
import com.chinatower.device.dto.RadarTargetFollowRelayResult;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.mapper.IndustryDeviceMapper;
import com.chinatower.device.mapper.RadarTargetFollowMapper;
import com.chinatower.device.service.IIndustryDeviceService;
import com.chinatower.device.service.IVideoDeviceMgmtInfoService;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author yy
 */
@Slf4j
@Service
public class IndustryDeviceServiceImpl implements IIndustryDeviceService {


    @Resource
    private IVideoDeviceMgmtInfoService videoDeviceMgmtInfoService;

    @Resource
    private IotDeviceController iotDeviceApi;

    @Resource
    private IndustryDeviceMapper industryDeviceMapper;

    @Resource
    private RadarTargetFollowMapper radarTargetFollowMapper;

    @Resource
    private NetConnectUtils connectUtils;

    private final String QUERY_TYPE_SDK = "sdk";

    private static final String TARGET_FOLLOW_START = "start";

    private static final String TARGET_FOLLOW_STOP = "stop";

    private static final String TENANT_OCCUPY_MSG_TEMPLATE = "%s等租户正在占用";

    @Override
    public List<DeviceAndIotDeviceDTO> queryDeviceList(DeviceInfoVO deviceInfoVO) {
        List<DeviceAndIotDeviceDTO> list = new ArrayList<>();

        if (null == deviceInfoVO.getPageSize() && null == deviceInfoVO.getPageNum()) {
            deviceInfoVO.setPageNum(1);
            deviceInfoVO.setPageSize(10);
        }

        SysUser user = SecurityUtils.getLoginUser().getUser();

        //查询 设备
        deviceInfoVO.setQueryType(QUERY_TYPE_SDK);
        deviceInfoVO.setOrderStatus(IndustryConstants.DEVICE_ORDER_STATUS_1);
        deviceInfoVO.setOperType(IndustryConstants.DEVICE_QUERY_OPERATE_TYPE_0);
        deviceInfoVO.setTenantId(user.getTenantId());
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
        List<DeviceInfoVO> devices = videoDeviceMgmtInfoService.queryDeviceInfoPage(deviceInfoVO);
        if (!devices.isEmpty()) {
            for (DeviceInfoVO device : devices) {
                DeviceAndIotDeviceDTO devTemp = new DeviceAndIotDeviceDTO();
                BeanUtils.copyProperties(device, devTemp);
                devTemp.setDeviceName(device.getDevName());
                devTemp.setChannels(device.getChildren());
                devTemp.setDeviceType(IndustryConstants.DEVICE_TYPE_DEVICE);
                list.add(devTemp);
            }
        }
        //查询 iot设备
        PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        iotDeviceVO.setDeviceName(deviceInfoVO.getDevName());
        iotDeviceVO.setTenantId(user.getTenantId());
        iotDeviceVO.setIndustryCode(user.getIndustryCode());
        iotDeviceVO.setOrderStatus(IndustryConstants.DEVICE_ORDER_STATUS_1);
        TableDataInfo tableDataInfo = iotDeviceApi.queryIotDevicePage(iotDeviceVO);
        if (!tableDataInfo.getRows().isEmpty()) {
            for (Object device : tableDataInfo.getRows()) {
                DeviceAndIotDeviceDTO devTemp = new DeviceAndIotDeviceDTO();
                BeanUtils.copyProperties(device, devTemp);
                devTemp.setStatus(String.valueOf(((IotDeviceVO) device).getDeviceStatus()));
                devTemp.setDeviceType(IndustryConstants.DEVICE_TYPE_IOT);
                list.add(devTemp);
            }
        }
        return list;
    }

    @Override
    public Object queryDeviceDetail(DeviceQueryVO deviceInfoQueryVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (IndustryConstants.DEVICE_TYPE_DEVICE.equals(deviceInfoQueryVO.getDeviceType())) {
            //摄像机
            DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
            deviceInfoVO.setDeviceCode(deviceInfoQueryVO.getDeviceCode());
            deviceInfoVO.setTenantId(user.getTenantId());
            deviceInfoVO.setIndustryCode(user.getIndustryCode());
            deviceInfoVO.setQueryType(QUERY_TYPE_SDK);
            deviceInfoVO.setOperType(IndustryConstants.DEVICE_QUERY_OPERATE_TYPE_1);
            Result<DeviceInfoVO> deviceInfo = videoDeviceMgmtInfoService.getDeviceInfo(deviceInfoVO);
            if (ObjectUtil.isNotEmpty(deviceInfo.getData())) {
                return deviceInfo.getData();
            }
        } else {
            //iot
            IotDeviceVO iotDeviceVO = new IotDeviceVO();
            iotDeviceVO.setDeviceCode(deviceInfoQueryVO.getDeviceCode());
            iotDeviceVO.setTenantId(user.getTenantId());
            iotDeviceVO.setIndustryCode(user.getIndustryCode());
            iotDeviceVO.setOrderStatus(IndustryConstants.DEVICE_ORDER_STATUS_1);
            TableDataInfo tableDataInfo = iotDeviceApi.queryIotDevicePage(iotDeviceVO);
            if (!tableDataInfo.getRows().isEmpty()) {
                return tableDataInfo.getRows().get(0);
            }
        }
        return null;
    }

    @Override
    @DataScope(industryCode = "ord.industry_code", acceptColumn="ord.accept_node", tenantColumn = "ord.tenant_id", deviceColumn = "de.device_code")
    public List<DeviceByDistanceDTO> queryDeviceByDistance(DeviceQueryVO deviceQueryVO) {

        DeviceByDistanceDTO device = industryDeviceMapper.getLonLatByDeviceCode(deviceQueryVO);
        if (null == device) {
            return null;
        }
        deviceQueryVO.setLatitude(device.getLatitude());
        deviceQueryVO.setLongitude(device.getLongitude());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceQueryVO.setIndustryCode(user.getIndustryCode());
        deviceQueryVO.setTenantId(user.getTenantId());
        return industryDeviceMapper.queryDeviceByDistance(deviceQueryVO);
    }

    @Override
    @DataScope(industryCode = "ord.industry_code", acceptColumn="ord.accept_node", tenantColumn = "ord.tenant_id", deviceColumn = "de.device_code")
    public List<DeviceInfoByDistanceDTO> queryDeviceInfoByDistance(DeviceInfoQueryVO param) {
        if (param == null
                || param.getDistance() == null
                || StringUtils.isEmpty(param.getLatitude())
                || StringUtils.isEmpty(param.getLongitude())) {
            throw new CustomException("必填参数不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        param.setIndustryCode(user.getIndustryCode());
        param.setTenantId(user.getTenantId());
        // 结果列表
        List<DeviceInfoByDistanceDTO> resultList = Lists.newArrayList();
        // 站址编码列表
        List<String> siteCodeList = Lists.newArrayList();
        // 根据经纬度 + 距离查询范围内的设备信息
        List<DeviceInfoByDistanceDTO> deviceInfoList = industryDeviceMapper.queryDeviceInfoByDistance(param);
        // 根据站址编码去重，一个站址编码保留第一条
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceInfoList)) {
            if (BooleanUtils.isTrue(param.getQueryAll())) {
                resultList = deviceInfoList;
            } else {
                for (DeviceInfoByDistanceDTO deviceInfo : deviceInfoList) {
                    if (!siteCodeList.contains(deviceInfo.getSiteCode())) {
                        resultList.add(deviceInfo);
                        siteCodeList.add(deviceInfo.getSiteCode());
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    @DataScope(industryCode = "t1.industry_code", tenantColumn = "t1.tenant_id", acceptColumn = "t1.accept_node", deviceColumn = "t1.device_code", alarmSourceVal = "4")
    public List<DeviceByConditionDTO> queryDevicesByCondition(DevicesByConditionVO devicesByConditionVO) {
        if (null == devicesByConditionVO.getPageSize() || null == devicesByConditionVO.getPageNum()) {
            devicesByConditionVO.setPageNum(1);
            devicesByConditionVO.setPageSize(10);
        }
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        devicesByConditionVO.setUserId(userId);
        PageHelper.startPage(devicesByConditionVO.getPageNum(), devicesByConditionVO.getPageSize());
        return industryDeviceMapper.queryDevicesByCondition(devicesByConditionVO);
    }

    @Override
    @DataScope(industryCode = "t1.industry_code", acceptColumn="t1.accept_node", tenantColumn = "t1.tenant_id", deviceColumn = "t1.device_code", alarmSourceVal = "4")
    public Result<DevicesStatisticsResVO> queryDevicesStatistics(DevicesStatisticsReqVO devicesStatisticsReqVO) {
        //组织编码list为空，摄像机总数+在线数量
        //组织编码list不为空，摄像机总数+在线数量+组织下（仅本组织）设备及在线状态
        List<String> deptList = devicesStatisticsReqVO.getDeptList();
        //查询摄像机总数，在线数量
        DevicesStatisticsResVO devicesStatisticsResVO = industryDeviceMapper.queryDevicesStatistics(devicesStatisticsReqVO);
        if(!CollectionUtils.isEmpty(deptList)){
            //查询组织下（仅本组织）设备及在线状态
            devicesStatisticsResVO.setDeptList(industryDeviceMapper.queryDevicesByDept(devicesStatisticsReqVO));
        }
        return Result.data(devicesStatisticsResVO);
    }

    @Override
    @Transactional
    public RadarTargetFollowResultVO radarTargetFollowRelay(RadarTargetFollowRelayVO param) {
        if (param == null || StringUtils.isEmpty(param.getType())) {
            throw new CustomException("接力类型参数不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("用户未登录");
        }
        if (StringUtils.equals(param.getType(), TARGET_FOLLOW_START)) {
            // 开始接力
            return doStartRelayLogic(param, loginUser);
        } else if (StringUtils.equals(param.getType(), TARGET_FOLLOW_STOP)) {
            // 停止接力
            return doStopRelayLogic(param, loginUser);
        } else {
            throw new CustomException("接力状态参数不正确");
        }
    }

    private RadarTargetFollowResultVO doStartRelayLogic(RadarTargetFollowRelayVO param, LoginUser loginUser) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(param.getDeviceCodeList())) {
            throw new CustomException("接力设备列表参数不能为空");
        }
        if (StringUtils.isEmpty(param.getTargetId())) {
            throw new CustomException("接力目标编码参数不能为空");
        }
        // 需要处理的设备编码列表
        List<String> deviceCodeList = param.getDeviceCodeList();
        // 参数设备正在联动数据列表
        List<RadarTargetFollowVO> targetFollowStatusList = radarTargetFollowMapper.queryByDeviceCodeList(deviceCodeList);
        // 其他租户占用列表，直接返回不处理
        List<RadarTargetFollowVO> otherTenantList = targetFollowStatusList.stream()
                .filter(x -> (!StringUtils.equals(x.getTenantId(), loginUser.getUser().getTenantId())
                        || !StringUtils.equals(x.getIndustryCode(), loginUser.getUser().getIndustryCode()))).collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(otherTenantList)) {
            throw new CustomException(String.format(TENANT_OCCUPY_MSG_TEMPLATE, otherTenantList.get(0).getTenantId()));
        }

        // 处理失败的设备编码列表
        List<String> failDeviceCodeList = Lists.newArrayList();
        // 传参设备中已经正在接力的数据列表
        List<RadarTargetFollowVO> alreadyRelayTargetFollowList = targetFollowStatusList.stream()
                .filter(x -> Objects.equals(x.getStatus(), Constants.TWO_NUM)).collect(Collectors.toList());
        Date createTime = new Date();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(alreadyRelayTargetFollowList)) {
            // 如果已经有接力的，取已经接力的开始时间，同目标编码的设备接力时间保持一致
            createTime = alreadyRelayTargetFollowList.get(0).getCreateTime();
            // 传参设备中已经正在接力的设备编码列表
            List<String> relayDeviceCodeList = alreadyRelayTargetFollowList.stream().map(RadarTargetFollowVO::getDeviceCode).collect(Collectors.toList());
            deviceCodeList.removeAll(relayDeviceCodeList);

            // 如果目标编码不相同，放到处理失败列表里面，目标编码相同，不处理
            List<String> relayDiffTargetDeviceCodeList = alreadyRelayTargetFollowList.stream()
                    .filter(x -> !StringUtils.equals(x.getTargetId(), param.getTargetId()))
                    .map(RadarTargetFollowVO::getDeviceCode).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(relayDiffTargetDeviceCodeList)) {
                // 处理失败设备编码列表增加 参数设备已经在接力但是目标和参数不同的数据
                failDeviceCodeList.addAll(relayDiffTargetDeviceCodeList);
            }
        }
        // 处理参数设备取消随动失败的设备编码
        List<String> handleLookToFailDeviceCodeList = handleRelayDeleteLookToLogic(targetFollowStatusList,loginUser);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(handleLookToFailDeviceCodeList)) {
            // 处理列表删除失败的设备编码
            deviceCodeList.removeAll(handleLookToFailDeviceCodeList);
            failDeviceCodeList.addAll(handleLookToFailDeviceCodeList);
        }

        // 处理接力插入逻辑
        return handleRelayInsertLogic(deviceCodeList, loginUser.getUser(), createTime, param.getTargetId(), failDeviceCodeList);
    }

    private List<String> handleRelayDeleteLookToLogic(List<RadarTargetFollowVO> targetFollowStatusList,LoginUser loginUser) {
        // 处理失败的设备编码列表
        List<String> failDeviceCodeList = Lists.newArrayList();

        // 参数设备正在联动的设备信息列表
        List<RadarTargetFollowVO> targetFollowList = targetFollowStatusList.stream()
                .filter(x -> Objects.equals(x.getStatus(), Constants.ONE_NUM)).collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(targetFollowList)) {
            String platformCode = loginUser.getUser().getPlatformCode();
            String accessNode = radarTargetFollowMapper.getAccessNodeByDeviceCodeList(
                    targetFollowList.stream().map(RadarTargetFollowVO::getDeviceCode).distinct().collect(Collectors.toList()));
            if (accessNode == null) {
                accessNode = platformCode;
            }
            // 根据目标编码分组 key -> targetId
            Map<String, List<RadarTargetFollowVO>> targetMap = targetFollowList.stream().collect(Collectors.groupingBy(RadarTargetFollowVO::getTargetId));
            for (Map.Entry<String, List<RadarTargetFollowVO>> entry : targetMap.entrySet()) {
                try {
                    String targetId = entry.getKey();
                    List<String> stopDeviceCodeList = entry.getValue().stream().map(RadarTargetFollowVO::getDeviceCode).distinct().collect(Collectors.toList());
                    // 调用联动停止接口
                    RadarTargetFollowLookToVO lookToParam = new RadarTargetFollowLookToVO();
                    lookToParam.setType(TARGET_FOLLOW_STOP);
                    lookToParam.setTargetId(targetId);
                    lookToParam.setDeviceCodeList(stopDeviceCodeList);
                    lookToParam.setAcceptNode(platformCode);
                    lookToParam.setAccessNode(accessNode);
                    RadarTargetFollowLookToResult lookToResult = targetFollowLookTo(lookToParam);
                    if (lookToResult == null || !StringUtils.equals(lookToResult.getCode(), Constants.ZERO)) {
                        // 调用联动停止失败，设备编码加入到处理失败列表
                        failDeviceCodeList.addAll(stopDeviceCodeList);
                    } else if (lookToResult.getData() != null) {
                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(lookToResult.getData().getSuccess())) {
                            // 调用联动停止返回成功的设备，删除mysql表数据
                            radarTargetFollowMapper.deleteByDeviceCodeListAndTargetId(lookToResult.getData().getSuccess(), targetId);
                        }
                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(lookToResult.getData().getFail())) {
                            // 调用联动停止返回失败的设备加入到处理失败列表
                            failDeviceCodeList.addAll(lookToResult.getData().getFail());
                        }
                    }
                } catch (Exception e) {
                    log.error("IndustryDeviceServiceImpl doStartRelayLogic 删除联动数据失败 entry:{}, e:", JSON.toJSONString(entry), e);
                }
            }
        }
        return failDeviceCodeList;
    }

    private RadarTargetFollowResultVO handleRelayInsertLogic(List<String> deviceCodeList, SysUser user,
                                                             Date createTime, String targetId,
                                                             List<String> failDeviceCodeList) {
        RadarTargetFollowResultVO result = new RadarTargetFollowResultVO();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceCodeList)) {
            String acceptNode = user.getPlatformCode();
            String accessNode = radarTargetFollowMapper.getAccessNodeByDeviceCodeList(deviceCodeList);
            if (accessNode == null) {
                accessNode = acceptNode;
            }

            RadarTargetFollowRelayVO relayParam = new RadarTargetFollowRelayVO();
            relayParam.setType(TARGET_FOLLOW_START);
            relayParam.setDeviceCodeList(deviceCodeList);
            relayParam.setTargetId(targetId);
            relayParam.setAcceptNode(acceptNode);
            relayParam.setAccessNode(accessNode);
            // 调用接力sdk 4.12 进行设备接力操作
            RadarTargetFollowRelayResult resultEntity = targetFollowRelay(relayParam);
            if (resultEntity == null || !StringUtils.equals(resultEntity.getCode(), Constants.ZERO)
                    || resultEntity.getData() == null || StringUtils.isEmpty(resultEntity.getData().getMessageId())) {
                // 接力请求失败，所有请求设备加入处理失败列表
                failDeviceCodeList.addAll(deviceCodeList);
            } else {
                // 接力请求成功，所有请求设备记录到mysql表中
                String messageId = resultEntity.getData().getMessageId();
                result.setMessageId(messageId);
                // 插入mysql表数据
                List<RadarTargetFollowVO> insertList = deviceCodeList.stream().map(x -> {
                    RadarTargetFollowVO entity = new RadarTargetFollowVO();
                    entity.setDeviceCode(x);
                    entity.setStatus(Constants.TWO_NUM);
                    entity.setTargetId(targetId);
                    entity.setMessageId(messageId);
                    entity.setTenantId(user.getTenantId());
                    entity.setIndustryCode(user.getIndustryCode());
                    entity.setAppVerCode(user.getAppVerCode());
                    entity.setCreator(user.getUserId());
                    entity.setCreateTime(createTime);
                    return entity;
                }).collect(Collectors.toList());
                radarTargetFollowMapper.insertBatch(insertList);
            }
        }
        result.setFailDeviceCodeList(failDeviceCodeList);
        return result;
    }

    private RadarTargetFollowResultVO doStopRelayLogic(RadarTargetFollowRelayVO param, LoginUser loginUser) {
        if (StringUtils.isEmpty(param.getMessageId())) {
            throw new CustomException("接力消息编码参数不能为空");
        }
        // 客户编码
        String tenantId = loginUser.getUser().getTenantId();
        // 行业编码
        String industryCode = loginUser.getUser().getIndustryCode();
        // 参数设备正在联动的设备信息列表
        List<RadarTargetFollowVO> targetFollowStatusList = radarTargetFollowMapper.queryByMessageId(param.getMessageId());

        for (RadarTargetFollowVO radarTargetFollowStatusVO : targetFollowStatusList) {
            if (!Objects.equals(radarTargetFollowStatusVO.getStatus(), Constants.TWO_NUM)) {
                throw new CustomException("设备中有非接力状态的数据，不能取消接力");
            } else if ((!StringUtils.equals(radarTargetFollowStatusVO.getTenantId(), tenantId)
                    || !StringUtils.equals(radarTargetFollowStatusVO.getIndustryCode(), industryCode))) {
                throw new CustomException(String.format(TENANT_OCCUPY_MSG_TEMPLATE, radarTargetFollowStatusVO.getTenantId()));
            }
        }

        //增加受理节点
        String acceptNode = loginUser.getUser().getPlatformCode();
        param.setAcceptNode(acceptNode);
        //增加接入节点
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(targetFollowStatusList)
                && StringUtils.isNotEmpty(targetFollowStatusList.get(0).getAccessNode())) {
            param.setAccessNode(targetFollowStatusList.get(0).getAccessNode());
        } else {
            param.setAccessNode(acceptNode);
        }

        // 取消接力sdk 4.12 调用基础取消接力接口
        RadarTargetFollowRelayResult resultEntity = targetFollowRelay(param);
        if (resultEntity == null || !StringUtils.equals(resultEntity.getCode(), Constants.ZERO)) {
            throw new CustomException("取消接力失败:" + (resultEntity != null ? resultEntity.getMsg() : "接口请求失败"));
        }
        // 取消接力成功的设备 删除mysql表数据
        radarTargetFollowMapper.deleteByMessageId(param.getMessageId());
        return null;
    }

    public RadarTargetFollowRelayResult targetFollowRelay(RadarTargetFollowRelayVO param) {

        RadarTargetFollowRelayResult resultEntity = null;
        try{
            NetConnectUtils.RequestParamter<RadarTargetFollowRelayVO> parameter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            parameter.setBody(param);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TARGET_FOLLOW_RELAY);
            parameter.setPath(path);
            resultEntity = connectUtils.post(parameter, RadarTargetFollowRelayResult.class);
            // 获取返回日志
            log.info("IndustryDeviceServiceImpl targetFollowRelay param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(resultEntity));
            return resultEntity;
        } catch (Exception e) {
            log.error("IndustryDeviceServiceImpl targetFollowRelay error:", e);
        }
        return resultEntity;
    }

    public RadarTargetFollowLookToResult targetFollowLookTo(RadarTargetFollowLookToVO param) {

        RadarTargetFollowLookToResult resultEntity = null;
        try{
            NetConnectUtils.RequestParamter<RadarTargetFollowLookToVO> parameter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            parameter.setBody(param);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.TARGET_FOLLOW_LOOK_TO);
            parameter.setPath(path);
            resultEntity = connectUtils.post(parameter, RadarTargetFollowLookToResult.class);
            // 获取返回日志
            log.info("IndustryDeviceServiceImpl targetFollowLookTo param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(resultEntity));
            return resultEntity;
        } catch (Exception e) {
            log.error("IndustryDeviceServiceImpl targetFollowLookTo error:", e);
        }
        return resultEntity;
    }

    @Override
    @Transactional
    public RadarTargetFollowResultVO radarTargetFollowLookTo(RadarTargetFollowLookToVO param) {
        if (param == null || StringUtils.isEmpty(param.getType())) {
            throw new CustomException("接力类型参数不能为空");
        }
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(param.getDeviceCodeList())) {
            throw new CustomException("接力设备列表参数不能为空");
        }
        if (StringUtils.isEmpty(param.getTargetId())) {
            throw new CustomException("接力目标编码参数不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("用户未登录");
        }
        if (StringUtils.equals(param.getType(), TARGET_FOLLOW_START)) {
            // 开始联动
            return doStartLookToLogic(param, loginUser);
        } else if (StringUtils.equals(param.getType(), TARGET_FOLLOW_STOP)) {
            // 停止联动
            return doStopLookToLogic(param, loginUser);
        } else {
            throw new CustomException("同时随动状态参数不正确");
        }
    }

    private RadarTargetFollowResultVO doStartLookToLogic(RadarTargetFollowLookToVO param, LoginUser loginUser) {
        // 需要处理的设备编码列表
        List<String> deviceCodeList = param.getDeviceCodeList();
        // 参数设备正在联动的设备信息列表
        List<RadarTargetFollowVO> targetFollowStatusList = radarTargetFollowMapper.queryByDeviceCodeList(deviceCodeList);

        // 参数设备中被其他租户占用的列表，如果存在直接返回错误信息
        List<RadarTargetFollowVO> otherTenantList = targetFollowStatusList.stream()
                .filter(x -> (!StringUtils.equals(x.getTenantId(), loginUser.getUser().getTenantId())
                        || !StringUtils.equals(x.getIndustryCode(), loginUser.getUser().getIndustryCode()))).collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(otherTenantList)) {
            throw new CustomException(String.format(TENANT_OCCUPY_MSG_TEMPLATE, otherTenantList.get(0).getTenantId()));
        }

        // 处理失败的设备编码列表
        List<String> failDeviceCodeList = Lists.newArrayList();
        // 参数设备已经在同时随动数据列表
        List<RadarTargetFollowVO> alreadyLookToTargetFollowList = targetFollowStatusList.stream()
                .filter(x -> Objects.equals(x.getStatus(), Constants.ONE_NUM)).collect(Collectors.toList());
        Date createTime = new Date();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(alreadyLookToTargetFollowList)) {
            createTime = alreadyLookToTargetFollowList.get(0).getCreateTime();

            List<String> lookToDeviceCodeList = alreadyLookToTargetFollowList.stream().map(RadarTargetFollowVO::getDeviceCode).collect(Collectors.toList());
            deviceCodeList.removeAll(lookToDeviceCodeList);

            // 如果目标编码不相同，放到处理失败列表里面，目标编码相同，不处理
            List<String> lookToDiffTargetDeviceCodeList = alreadyLookToTargetFollowList.stream()
                    .filter(x -> !StringUtils.equals(x.getTargetId(), param.getTargetId()))
                    .map(RadarTargetFollowVO::getDeviceCode).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(lookToDiffTargetDeviceCodeList)) {
                failDeviceCodeList.addAll(lookToDiffTargetDeviceCodeList);
            }
        }

        // 处理失败的设备编码
        List<String> handleRelayFailDeviceCodeList = handleLookToDeleteRelayLogic(targetFollowStatusList);

        RadarTargetFollowResultVO result = new RadarTargetFollowResultVO();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(handleRelayFailDeviceCodeList)) {
            // 处理列表删除失败的设备编码
            deviceCodeList.removeAll(handleRelayFailDeviceCodeList);
            failDeviceCodeList.addAll(handleRelayFailDeviceCodeList);
        }

        // 处理同时随动随动逻辑
        List<String> handleLookToFailDeviceCodeList = handleLookToInsertLogic(deviceCodeList, loginUser.getUser(), createTime, param.getTargetId());
        if (!CollectionUtils.isEmpty(handleLookToFailDeviceCodeList)) {
            failDeviceCodeList.addAll(handleLookToFailDeviceCodeList);
        }
        result.setFailDeviceCodeList(failDeviceCodeList);
        return result;
    }

    private List<String> handleLookToDeleteRelayLogic(List<RadarTargetFollowVO> targetFollowStatusList) {
        // 处理失败的设备编码
        List<String> failDeviceCodeList = Lists.newArrayList();

        // 参数设备已经在接力的数据列表
        List<RadarTargetFollowVO> targetFollowList = targetFollowStatusList.stream()
                .filter(x -> Objects.equals(x.getStatus(), Constants.TWO_NUM)).collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(targetFollowList)) {
            Map<String, List<RadarTargetFollowVO>> messageIdMap = targetFollowList.stream()
                    .filter(x -> Objects.nonNull(x.getMessageId())).collect(Collectors.groupingBy(RadarTargetFollowVO::getMessageId));
            List<String> deleteMessageIdList = new ArrayList<>();
            for (Map.Entry<String, List<RadarTargetFollowVO>> entry : messageIdMap.entrySet()) {
                String messageId = entry.getKey();
                List<String> messageDeviceCodeList = entry.getValue().stream().map(RadarTargetFollowVO::getDeviceCode).distinct().collect(Collectors.toList());
                try {
                    // 调用接力停止接口
                    RadarTargetFollowRelayVO relayParam = new RadarTargetFollowRelayVO();
                    relayParam.setType(TARGET_FOLLOW_STOP);
                    relayParam.setMessageId(messageId);
                    RadarTargetFollowRelayResult resultEntity = targetFollowRelay(relayParam);
                    if (resultEntity == null || !StringUtils.equals(resultEntity.getCode(), Constants.ZERO)) {
                        // 停止接力失败，所有请求设备编码加入失败列表
                        failDeviceCodeList.addAll(messageDeviceCodeList);
                    } else {
                        // 停止接力成功，删除mysql表数据
                        deleteMessageIdList.add(messageId);
                    }
                } catch (Exception e) {
                    log.error("IndustryDeviceServiceImpl doStartLookToLogic 删除接力数据失败 messageId:{}, e:", messageId, e);
                }
            }
            if (!CollectionUtils.isEmpty(deleteMessageIdList)){
                radarTargetFollowMapper.deleteByMessageIdBatch(deleteMessageIdList);
            }
        }
        return failDeviceCodeList;
    }

    private List<String> handleLookToInsertLogic(List<String> deviceCodeList, SysUser user, Date createTime, String targetId) {
        // 处理失败的设备编码
        List<String> failDeviceCodeList = Lists.newArrayList();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceCodeList)) {
            RadarTargetFollowLookToVO lookToParam = new RadarTargetFollowLookToVO();
            lookToParam.setType(TARGET_FOLLOW_START);
            lookToParam.setDeviceCodeList(deviceCodeList);
            lookToParam.setTargetId(targetId);
            String acceptNode = user.getPlatformCode();
            lookToParam.setAcceptNode(acceptNode);
            String accessNode = radarTargetFollowMapper.getAccessNodeByDeviceCodeList(deviceCodeList);
            if (accessNode == null) {
                accessNode = acceptNode;
            }
            lookToParam.setAccessNode(accessNode);
            // 调用同时随动sdk 4.14
            RadarTargetFollowLookToResult resultEntity = targetFollowLookTo(lookToParam);
            if (resultEntity == null || !StringUtils.equals(resultEntity.getCode(), Constants.ZERO)) {
                // 请求同时随动失败，所有请求的设备加入处理失败列表
                failDeviceCodeList.addAll(deviceCodeList);
            } else {
                if (resultEntity.getData() != null) {
                    if (!CollectionUtils.isEmpty(resultEntity.getData().getFail())) {
                        // 请求同时随动成功，返回失败的设备加入处理失败列表
                        failDeviceCodeList.addAll(resultEntity.getData().getFail());
                    }
                    if (!CollectionUtils.isEmpty(resultEntity.getData().getSuccess())) {
                        // 请求同时随动成功的数据插入mysql表数据
                        List<RadarTargetFollowVO> insertList = resultEntity.getData().getSuccess().stream().map(x -> {
                            RadarTargetFollowVO entity = new RadarTargetFollowVO();
                            entity.setDeviceCode(x);
                            entity.setStatus(Constants.ONE_NUM);
                            entity.setTargetId(targetId);
                            entity.setTenantId(user.getTenantId());
                            entity.setIndustryCode(user.getIndustryCode());
                            entity.setAppVerCode(user.getAppVerCode());
                            entity.setCreator(user.getUserId());
                            entity.setCreateTime(createTime);
                            return entity;
                        }).collect(Collectors.toList());
                        radarTargetFollowMapper.insertBatch(insertList);
                    }
                }
            }
        }
        return failDeviceCodeList;
    }

    private RadarTargetFollowResultVO doStopLookToLogic(RadarTargetFollowLookToVO param, LoginUser loginUser) {
        // 参数设备正在联动的设备信息列表
        List<RadarTargetFollowVO> targetFollowStatusList = radarTargetFollowMapper.queryByDeviceCodeList(param.getDeviceCodeList());
        // 客户编码
        String tenantId = loginUser.getUser().getTenantId();
        // 行业编码
        String industryCode = loginUser.getUser().getIndustryCode();
        // 返回结果
        RadarTargetFollowResultVO targetFollowResult = new RadarTargetFollowResultVO();
        // 失败设备编码列表
        List<String> failDeviceCodeList = Lists.newArrayList();

        // 如果数据库查出来为空，全部按失败处理
        if (CollectionUtils.isEmpty(targetFollowStatusList)) {
            failDeviceCodeList.addAll(param.getDeviceCodeList());
            targetFollowResult.setFailDeviceCodeList(failDeviceCodeList);
            return targetFollowResult;
        }

        // 参数设备在数据库中存在的设备编码列表
        List<String> existDeviceCodeList = Lists.newArrayList();
        for (RadarTargetFollowVO entity : targetFollowStatusList) {
            if (!Objects.equals(entity.getStatus(), Constants.ONE_NUM)) {
                throw new CustomException("设备中有接力状态的数据，不能取消同时随动");
            } else if ((!StringUtils.equals(entity.getTenantId(), tenantId)
                    || !StringUtils.equals(entity.getIndustryCode(), industryCode))) {
                throw new CustomException(String.format(TENANT_OCCUPY_MSG_TEMPLATE, entity.getTenantId()));
            }
            existDeviceCodeList.add(entity.getDeviceCode());
        }

        // 参数设备编码
        List<String> handleDeviceCodeList = param.getDeviceCodeList();
        // 参数有数据库中没有的设备，当失败处理
        if (existDeviceCodeList.size() != handleDeviceCodeList.size()) {
            // 参数中有但数据库没有的设备编码列表
            List<String> notExistDeviceCodeList = handleDeviceCodeList.stream().filter(x -> !existDeviceCodeList.contains(x)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(notExistDeviceCodeList)) {
                failDeviceCodeList.addAll(notExistDeviceCodeList);
                handleDeviceCodeList.removeAll(notExistDeviceCodeList);
            }
        }

        // 参数目标编码不匹配的设备编码加入到处理失败列表
        List<String> notMatchDeviceCodeList = targetFollowStatusList.stream()
                .filter(x -> !StringUtils.equals(x.getTargetId(), param.getTargetId()))
                .map(RadarTargetFollowVO::getDeviceCode).collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(notMatchDeviceCodeList)) {
            failDeviceCodeList.addAll(notMatchDeviceCodeList);
            handleDeviceCodeList.removeAll(notMatchDeviceCodeList);
        }
        if (CollectionUtils.isEmpty(handleDeviceCodeList)) {
            targetFollowResult.setFailDeviceCodeList(failDeviceCodeList);
            return targetFollowResult;
        }
        // 取消同时随动sdk
        param.setDeviceCodeList(handleDeviceCodeList);
        String acceptNode = loginUser.getUser().getPlatformCode();
        param.setAcceptNode(acceptNode);
        String accessNode = radarTargetFollowMapper.getAccessNodeByDeviceCodeList(handleDeviceCodeList);
        if (accessNode == null) {
            accessNode = acceptNode;
        }

        param.setAccessNode(accessNode);
        RadarTargetFollowLookToResult resultEntity = targetFollowLookTo(param);
        if (resultEntity == null || !StringUtils.equals(resultEntity.getCode(), Constants.ZERO)) {
            // 取消同时随动失败的设备加入处理失败列表
            failDeviceCodeList.addAll(param.getDeviceCodeList());
        } else if (resultEntity.getData() != null) {
            // 删除处理成功的数据
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(resultEntity.getData().getSuccess())) {
                // 删除mysql表数据
                radarTargetFollowMapper.deleteByDeviceCodeListAndTargetId(resultEntity.getData().getSuccess(), param.getTargetId());
            }
            // 如果有处理失败的数据，返回对应数据
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(resultEntity.getData().getFail())) {
                failDeviceCodeList.addAll(resultEntity.getData().getFail());
            }
        }
        targetFollowResult.setFailDeviceCodeList(failDeviceCodeList);
        return targetFollowResult;
    }

    @Override
    public Result<List<RadarDeviceStatusVO>> queryRadarTargetList(RadarTargetDTO radarTargetDTO) {
        try {
            if (CollectionUtils.isEmpty(radarTargetDTO.getDeviceCodeList())){
                return Result.fail("设备编码集合不能为空！");
            }
            radarTargetDTO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
            radarTargetDTO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
            //先查询设置的阈值
            SysConfig sysConfig = industryDeviceMapper.getSysConfig(IndustryConstants.LIMIT_TIME);
            Date nowDate = new Date();
            Date timeBefore = new Date(nowDate.getTime() - Long.parseLong(sysConfig.getConfigValue()));
            radarTargetDTO.setTimeBefore(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",timeBefore));
            //根据条件查询设备列表
            List<String> targetIdList = industryDeviceMapper.queryRadarTargetId(radarTargetDTO);
            if (!CollectionUtils.isEmpty(targetIdList)){
                //根据targetId删除对应的设备
                industryDeviceMapper.deleteRadarByTargetId(targetIdList);
            }
            //删除之后查询最终设备列表
            List<RadarDeviceStatusVO> radarDeviceList = industryDeviceMapper.queryRadarStatusList(radarTargetDTO);
            return Result.data(radarDeviceList);
        }catch (Exception e){
            log.info("获取目标设备列表接口查询失败:",e);
            return Result.fail("获取目标设备列表接口查询失败：" + e.getMessage());
        }
    }

    @Override
    public IndustryDeviceStatisticsVO queryIndustryDeviceStatistics() {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException(Constants.NO_USER_INFO);
        }
        SysUser user = loginUser.getUser();
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();
        String appVerCode = user.getAppVerCode();

        log.info("queryIndustryDeviceStatistics tenantId:{}, industryCode:{}, appVerCode:{}", tenantId, industryCode, appVerCode);

        IndustryDeviceStatisticsVO data = new IndustryDeviceStatisticsVO();
        data.setDeviceNum(industryDeviceMapper.countIndustryDeviceNum(tenantId, industryCode, appVerCode));
        data.setSensorDeviceNum(industryDeviceMapper.countIndustrySensorDeviceNum(tenantId, industryCode, appVerCode));
        data.setRadarDeviceNum(industryDeviceMapper.countIndustryRadarDeviceNum(tenantId, industryCode, appVerCode));
        data.setUavDeviceNum(industryDeviceMapper.countIndustryUavDeviceNum(tenantId, industryCode, appVerCode));
        data.setHornDeviceNum(industryDeviceMapper.countIndustryHornDeviceNum(tenantId, industryCode, appVerCode));
        data.setRecorderDeviceNum(industryDeviceMapper.countIndustryRecorderDeviceNum(tenantId, industryCode, appVerCode));
        return data;
    }

    @Override
    @DataScope(industryCode = "t1.industry_code", tenantColumn = "t1.tenant_id", acceptColumn = "t1.accept_node", deviceColumn = "t1.device_code", alarmSourceVal = "4")
    public List<DeviceByConditionDTO> queryDeviceListByCondition(DevicesByConditionVO devicesByConditionVO) {
        return industryDeviceMapper.queryDeviceListByCondition(devicesByConditionVO);
    }

}
