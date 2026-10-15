package com.chinatower.event.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.entity.vo.UavDeviceQueryVO;
import com.chinatower.event.mapper.*;
import com.chinatower.event.vo.entity.DeviceTypeStatisticsVO;
import com.chinatower.event.vo.entity.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.RecorderDeviceDTO;
import com.chinatower.platform.entity.device.RecorderDeviceVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.event.vo.entity.DeviceTypeInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceLabelVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceService extends TowerBaseController {

    @Resource
    private DeviceInfoMapper deviceInfoMapper;
    @Resource
    private WorkbenchMapper workbenchMapper;
    @Resource
    private UavDeviceMapper uavDeviceMapper;
    @Resource
    private HornDeviceMapper hornDeviceMapper;
    @Resource
    private IotDeviceMapper iotDeviceMapper;
    @Resource
    private OrgIotDevRelMapper orgIotDevMapper;

    /**
     * 统计监控设备数量
     *
     * @param deviceInfoVO (year 年 month 月 day 日)
     * @return deviceCountTotal 摄像机总数
     * @return devCountOnline 摄像机在线数
     * @return devCountOffline 摄像机离线数
     * @return channelCountTotal 通道总数
     * @return channelCountOnline   通道在线数
     * @return channelCountOffline 通道离线数
     */
    @DataScope(tenantColumn = "od.tenant_id", acceptColumn = "od.accept_node", deviceColumn = "od.device_code", industryCode = "od.industry_code",dbType = EbikeDataBaseConstant.BASE)
    public Result<IotDeviceStatisticsVo> getDeviceStatistics(DeviceInfoVO deviceInfoVO) {
        log.info("查询设备---请求报文------{}", JSON.toJSONString(deviceInfoVO));
        if (StringUtils.isNoneBlank(deviceInfoVO.getOrgCode())) {
            // 根据部门查询下级部门
            deviceInfoVO.setDeptIds(deviceInfoMapper.getDeptIds(deviceInfoVO.getOrgCode()));
        }
        startPage();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        deviceInfoVO.setTenantId(user.getTenantId());
        deviceInfoVO.setOrderStatus("1");
        //根据条件分页查询设备信息
        // 补充设备归属组织
        IotDeviceStatisticsVo vo = new IotDeviceStatisticsVo();
        List<String> deviceCodeList = new ArrayList<>();
        if (deviceInfoVO.getQueryType().equals("1")) {
            List<DeviceInfoVO> resultList = deviceInfoMapper.queryDeviceInfoList(deviceInfoVO);
            deviceCodeList = resultList.stream().map(DeviceInfoVO::getDeviceCode).distinct().collect(Collectors.toList());
        } else {
            List<DeviceInfoVO> resultList = deviceInfoMapper.queryDeviceInfoList(deviceInfoVO);
            deviceCodeList = resultList.stream().map(DeviceInfoVO::getDeviceCode).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deviceCodeList)) {
                List<TenantDeviceLabelVO> labelInfoList = deviceInfoMapper
                        .selectLabelInfoByDeviceCode(deviceCodeList, user.getTenantId(), user.getIndustryCode());
                deviceCodeList = labelInfoList.stream().map(TenantDeviceLabelVO::getDeviceCode).distinct().collect(Collectors.toList());
            }
        }
        AnalysisVO analysisVO = new AnalysisVO();
        analysisVO.setDeviceCodeList(deviceCodeList);
        Map<String, Object> resultMap = workbenchMapper.getStatisticsDevices(analysisVO);
        //设备数量
        int deviceCountTotal = Integer.parseInt(resultMap.get("deviceCountTotal").toString());
        //在线设备数量
        int devCountOnline = Integer.parseInt(resultMap.get("devCountOnline").toString());
        //离线设备数量
        int devCountOffline = Integer.parseInt(resultMap.get("devCountOffline").toString());
        //在线数
        String onlineDev = "0";
        if (0 != deviceCountTotal) {
            onlineDev = String.valueOf(String.format("%.2f", (double) devCountOnline / deviceCountTotal));//修改后
        }
        resultMap.put("onlinePercentageDev", onlineDev);
        vo.setIotDeviceCount(deviceCountTotal);
        vo.setIotDeviceOfflineCount(devCountOffline);
        vo.setIotDeviceOnlineCount(devCountOnline);
        vo.setIotDeviceOnlinePercent(onlineDev);
        return Result.data(vo);
    }

    @DataScope(tenantColumn = "od.tenant_id", acceptColumn = "od.accept_node", deviceColumn = "od.device_code", industryCode = "od.industry_code",dbType = EbikeDataBaseConstant.BASE)
    public Result<List<DeviceTypeInfoVO>> getDeviceTypeList(DeviceInfoVO deviceInfoVO) {
        log.info("摄像机类别数量信息查询---请求报文------{}", JSON.toJSONString(deviceInfoVO));
        if (StringUtils.isNoneBlank(deviceInfoVO.getOrgCode())) {
            // 根据部门查询下级部门
            deviceInfoVO.setDeptIds(deviceInfoMapper.getDeptIds(deviceInfoVO.getOrgCode()));
        }
        if (StringUtils.isNotNull(deviceInfoVO.getPageNum()) && StringUtils.isNotNull(deviceInfoVO.getPageSize())) {
            PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
        } else {
            startPage();
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        deviceInfoVO.setTenantId(user.getTenantId());
        deviceInfoVO.setOrderStatus("1");
        List<DeviceInfoVO> resultList = deviceInfoMapper.queryDeviceInfoList(deviceInfoVO);
        List<String> deviceCodeList = resultList.stream().map(DeviceInfoVO::getDeviceCode).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deviceCodeList)) {
            return Result.data(Lists.newArrayList());
        }
        List<DeviceTypeInfoVO> deviceTypeList;
        if (deviceInfoVO.getQueryType().equals("1")) {
            deviceInfoVO.setDeviceCodeList(deviceCodeList);
            deviceTypeList = deviceInfoMapper.getDeviceTypeList(deviceInfoVO);
        } else {
            List<TenantDeviceLabelVO> labelInfoList = deviceInfoMapper
                    .selectLabelInfoByDeviceCode(deviceCodeList, user.getTenantId(), user.getIndustryCode());
            deviceTypeList = labelInfoList.stream()
                    .collect(Collectors.groupingBy(TenantDeviceLabelVO::getLabelId))
                    .entrySet().stream()
                    .map(entry -> {
                        Long labelId = entry.getKey();
                        List<TenantDeviceLabelVO> group = entry.getValue();

                        DeviceTypeInfoVO vo = new DeviceTypeInfoVO();
                        vo.setDeviceTypeName(group.get(0).getLabelName());
                        vo.setDeviceTypeNum(group.size());
                        vo.setCategoryCode(String.valueOf(labelId));
                        return vo;
                    })
                    .collect(Collectors.toList());
        }
        return Result.data(deviceTypeList);
    }


    /**
     * 统计无人机设备数量
     *
     * @param param
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "uav.device_code", alarmSourceVal = "11",dbType = EbikeDataBaseConstant.BASE)
    public Result<IotDeviceStatisticsVo> getUavDeviceStatistics(UavDeviceDTO param) {
        IotDeviceStatisticsVo vo = new IotDeviceStatisticsVo();
        PageMethod.startPage(param.getPageNum(), param.getPageSize());
        param.setOrderStatus(0);
        List<UavDeviceQueryVO> deviceVOList = uavDeviceMapper.queryUavDeviceInfoList(param);
        if (CollectionUtils.isEmpty(deviceVOList)) {
            return Result.data(vo);
        }
        int total = deviceVOList.size();
        int deviceOnlineCount = Math.toIntExact(deviceVOList.stream()
                .filter(device -> device.getDeviceStatus() == 0)
                .count());
        vo.setIotDeviceCount(total);
        vo.setIotDeviceOfflineCount(total - deviceOnlineCount);
        vo.setIotDeviceOnlineCount(deviceOnlineCount);
        vo.setIotDeviceOnlinePercent(String.format("%.2f", (double) deviceOnlineCount / total));
        return Result.data(vo);
    }

    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "uav.device_code", alarmSourceVal = "11",dbType = EbikeDataBaseConstant.BASE)
    public Result<List<DeviceTypeStatisticsVO>> getUavDeviceStatisticsByType(UavDeviceDTO param) {
        param.setOrderStatus(0);
        List<DeviceTypeStatisticsVO> statisticsByType = new ArrayList<>();
        PageMethod.startPage(param.getPageNum(), param.getPageSize());
        List<UavDeviceQueryVO> deviceVOList = uavDeviceMapper.queryUavDeviceInfoList(param);
        if (CollectionUtils.isEmpty(deviceVOList)) {
            return Result.data(statisticsByType);
        }
//        无人机机巢编码
        List<String> uavNestCodeList = deviceVOList.stream().map(UavDeviceQueryVO::getUavNestCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(uavNestCodeList)) return Result.data(new ArrayList<>());
        AnalysisVO analysisVO = new AnalysisVO();
        analysisVO.setDeviceCodeList(uavNestCodeList);
        statisticsByType = workbenchMapper.getUavDeviceStatisticsByType(analysisVO);
        return Result.data(statisticsByType);
    }

    /**
     * 统计喇叭设备数量
     *
     * @param hornDeviceDTO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10",dbType = EbikeDataBaseConstant.BASE)
    public Result<IotDeviceStatisticsVo> getHornDeviceStatistics(HornDeviceDTO hornDeviceDTO) {
        SysUser tokenUser = SecurityUtils.getLoginUser().getUser();
        if (null == hornDeviceDTO.getPageNum() || hornDeviceDTO.getPageNum() <= com.chinatower.platform.constants.Constants.ZERO_NUM) {
            hornDeviceDTO.setPageNum(com.chinatower.platform.constants.Constants.ONE_NUM);
        }
        if (null == hornDeviceDTO.getPageSize() || hornDeviceDTO.getPageSize() <= com.chinatower.platform.constants.Constants.ZERO_NUM) {
            hornDeviceDTO.setPageSize(com.chinatower.platform.constants.Constants.TEN_NUM);
        }
        PageMethod.startPage(hornDeviceDTO.getPageNum(), hornDeviceDTO.getPageSize());
        hornDeviceDTO.setTenantId(tokenUser.getTenantId());
        hornDeviceDTO.setIndustryCode(tokenUser.getIndustryCode());
        hornDeviceDTO.setAppVerCode(tokenUser.getAppVerCode());
        hornDeviceDTO.setUserId(tokenUser.getUserId());
        hornDeviceDTO.setOrderStatus("1");
        //查询设备集合
        List<HornDeviceVo> hornDeviceList = hornDeviceMapper.queryHornDeviceList(hornDeviceDTO);
        IotDeviceStatisticsVo vo = new IotDeviceStatisticsVo();
        if(CollectionUtil.isNotEmpty(hornDeviceList)) {
            int total = hornDeviceList.size();
            int deviceOnlineCount = Math.toIntExact(hornDeviceList.stream()
                    .filter(device -> (device.getDeviceStatus().equals("1") || device.getDeviceStatus().equals("2")))
                    .count());
            vo.setIotDeviceCount(total);
            vo.setIotDeviceOfflineCount(total - deviceOnlineCount);
            vo.setIotDeviceOnlineCount(deviceOnlineCount);
            if (total > 0) {
                vo.setIotDeviceOnlinePercent(String.format("%.2f", (double) deviceOnlineCount / total));
            }
        }
        return Result.data(vo);

    }

    /**
     * 统计喇叭设备数量
     *
     * @param hornDeviceDTO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10",dbType = EbikeDataBaseConstant.BASE)
    public Result<List<DeviceTypeStatisticsVO>> getHornDeviceStatisticsByType(HornDeviceDTO hornDeviceDTO) {
        SysUser tokenUser = SecurityUtils.getLoginUser().getUser();
        if (null == hornDeviceDTO.getPageNum() || hornDeviceDTO.getPageNum() <= com.chinatower.platform.constants.Constants.ZERO_NUM) {
            hornDeviceDTO.setPageNum(com.chinatower.platform.constants.Constants.ONE_NUM);
        }
        if (null == hornDeviceDTO.getPageSize() || hornDeviceDTO.getPageSize() <= com.chinatower.platform.constants.Constants.ZERO_NUM) {
            hornDeviceDTO.setPageSize(com.chinatower.platform.constants.Constants.TEN_NUM);
        }
        PageMethod.startPage(hornDeviceDTO.getPageNum(), hornDeviceDTO.getPageSize());
        hornDeviceDTO.setTenantId(tokenUser.getTenantId());
        hornDeviceDTO.setIndustryCode(tokenUser.getIndustryCode());
        hornDeviceDTO.setAppVerCode(tokenUser.getAppVerCode());
        hornDeviceDTO.setUserId(tokenUser.getUserId());
        hornDeviceDTO.setOrderStatus("1");
        //查询设备集合
        List<HornDeviceVo> hornDeviceList = hornDeviceMapper.queryHornDeviceList(hornDeviceDTO);
        List<String> hornCodeList = hornDeviceList.stream().map(HornDeviceVo::getDeviceCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(hornCodeList))  return Result.data(new ArrayList<>());
        AnalysisVO analysisVO = new AnalysisVO();
        analysisVO.setDeviceCodeList(hornCodeList);
        List<DeviceTypeStatisticsVO> statisticsByType = workbenchMapper.getHornDeviceStatisticsByType(analysisVO);
        return Result.data(statisticsByType);

    }

    /**
     * 统计物联设备数量
     *
     * @param deviceInfoVO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10",dbType = EbikeDataBaseConstant.BASE)
    public Result<IotDeviceStatisticsVo> getIotDeviceStatistics(IotDeviceVO deviceInfoVO) {
        if (deviceInfoVO.isExport()) {
            deviceInfoVO.setPageNum(null);
            deviceInfoVO.setPageSize(null);
        } else {
            if (StringUtils.isNotNull(deviceInfoVO.getPageNum()) && StringUtils.isNotNull(deviceInfoVO.getPageSize())) {
                PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
            } else {
                startPage();
            }
        }
        deviceInfoVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        deviceInfoVO.setOrderStatus("1");
        List<IotDeviceVO> iotDeviceList = queryIotDeviceListNew(deviceInfoVO);
        IotDeviceStatisticsVo vo = new IotDeviceStatisticsVo();
        if(CollectionUtil.isNotEmpty(iotDeviceList)) {
            int total = iotDeviceList.size();
            int deviceOnlineCount = Math.toIntExact(iotDeviceList.stream()
                    .filter(device -> device.getDeviceStatus() == 0 )
                    .count());
            vo.setIotDeviceCount(total);
            vo.setIotDeviceOfflineCount(total - deviceOnlineCount);
            vo.setIotDeviceOnlineCount(deviceOnlineCount);
            if (total > 0) {
                vo.setIotDeviceOnlinePercent(String.format("%.2f", (double) deviceOnlineCount / total));
            }
        }
        return Result.data(vo);
    }

    /**
     * 统计物联设备数量
     *
     * @param deviceInfoVO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10",dbType = EbikeDataBaseConstant.BASE)
    public Result<List<DeviceTypeStatisticsVO>> getIotDeviceStatisticsByType(IotDeviceVO deviceInfoVO) {
        if (deviceInfoVO.isExport()) {
            deviceInfoVO.setPageNum(null);
            deviceInfoVO.setPageSize(null);
        } else {
            if (StringUtils.isNotNull(deviceInfoVO.getPageNum()) && StringUtils.isNotNull(deviceInfoVO.getPageSize())) {
                PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
            } else {
                startPage();
            }
        }
        deviceInfoVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        deviceInfoVO.setOrderStatus("1");
        List<IotDeviceVO> iotDeviceList = queryIotDeviceListNew(deviceInfoVO);
        List<String> iotCodeList = iotDeviceList.stream().map(IotDeviceVO::getDeviceCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(iotCodeList)) return Result.data(new ArrayList<>());
        AnalysisVO analysisVO = new AnalysisVO();
        analysisVO.setDeviceCodeList(iotCodeList);
        List<DeviceTypeStatisticsVO> statisticsByType = workbenchMapper.getIotDeviceStatisticsByType(analysisVO);
        return Result.data(statisticsByType);
    }


    /**
     * 统计雷达设备数量
     *
     * @param deviceInfoVO
     * @return
     */
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10",dbType = EbikeDataBaseConstant.BASE)
    public Result<IotDeviceStatisticsVo> getRadarDeviceStatistics(IotDeviceVO deviceInfoVO) {
        if (deviceInfoVO.isExport()) {
            deviceInfoVO.setPageNum(null);
            deviceInfoVO.setPageSize(null);
        } else {
            if (StringUtils.isNotNull(deviceInfoVO.getPageNum()) && StringUtils.isNotNull(deviceInfoVO.getPageSize())) {
                PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
            } else {
                startPage();
            }
        }
        deviceInfoVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        deviceInfoVO.setCategoryCode(1);
        deviceInfoVO.setOrderStatus("1");
        List<IotDeviceVO> iotDeviceList = queryIotDeviceListNew(deviceInfoVO);
        IotDeviceStatisticsVo vo = new IotDeviceStatisticsVo();
        if(CollectionUtil.isNotEmpty(iotDeviceList)) {
            int total = iotDeviceList.size();
            int deviceOnlineCount = Math.toIntExact(iotDeviceList.stream()
                    .filter(device -> device.getDeviceStatus() == 0)
                    .count());
            vo.setIotDeviceCount(total);
            vo.setIotDeviceOfflineCount(total - deviceOnlineCount);
            vo.setIotDeviceOnlineCount(deviceOnlineCount);
            if (total > 0) {
                vo.setIotDeviceOnlinePercent(String.format("%.2f", (double) deviceOnlineCount / total));
            }
        }
        return Result.data(vo);
    }

    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10",dbType = EbikeDataBaseConstant.BASE)
    public Result<List<DeviceTypeStatisticsVO>> getRadarDeviceStatisticsByType(IotDeviceVO deviceInfoVO) {
        if (deviceInfoVO.isExport()) {
            deviceInfoVO.setPageNum(null);
            deviceInfoVO.setPageSize(null);
        } else {
            if (StringUtils.isNotNull(deviceInfoVO.getPageNum()) && StringUtils.isNotNull(deviceInfoVO.getPageSize())) {
                PageHelper.startPage(deviceInfoVO.getPageNum(), deviceInfoVO.getPageSize());
            } else {
                startPage();
            }
        }
        deviceInfoVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        deviceInfoVO.setCategoryCode(1);
        deviceInfoVO.setOrderStatus("1");
        List<IotDeviceVO> iotDeviceList = queryIotDeviceListNew(deviceInfoVO);
        List<String> iotCodeList = iotDeviceList.stream().map(IotDeviceVO::getDeviceCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(iotCodeList)) return Result.data(new ArrayList<>());
        AnalysisVO analysisVO = new AnalysisVO();
        analysisVO.setDeviceCodeList(iotCodeList);
        List<DeviceTypeStatisticsVO> statisticsByType = workbenchMapper.getRadarDeviceStatisticsByType(analysisVO);
        return Result.data(statisticsByType);
    }


    public List<IotDeviceVO> queryIotDeviceListNew(IotDeviceVO iotDevice) {

        if (StringUtils.isNotEmpty(iotDevice.getIsMonitorFlag())) {
            iotDevice.setUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<IotDeviceVO> iotDeviceList;
        if (iotDevice.getCategoryCode() != null && Objects.equals(iotDevice.getCategoryCode(), Constants.ONE_NUM)) {
            iotDeviceList = iotDeviceMapper.queryRadarListPage(iotDevice);
            iotDevice.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_10));
        } else {
            iotDeviceList = iotDeviceMapper.queryIotDeviceList(iotDevice);
            iotDevice.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_8));
        }
        if (iotDeviceList.isEmpty()) {
            return iotDeviceList;
        }
        iotDevice.setIndustryCode(user.getIndustryCode());
        List<OrganDeviceRelVO> orgIotDevRels = orgIotDevMapper.queryOrgIotDevRels(iotDevice);
        if (!orgIotDevRels.isEmpty()) {
            for (IotDeviceVO iotDeviceVO : iotDeviceList) {
                iotDeviceVO.setOrgRels(orgIotDevRels.stream().filter(e -> iotDeviceVO.getDeviceCode().equals(e.getDeviceCode())).collect(Collectors.toList()));
                iotDeviceVO.setOrgName(iotDeviceVO.getOrgRels().stream().map(OrganDeviceRelVO::getDeptName).collect(Collectors.joining(",")));
            }
        }
        return iotDeviceList;
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE, tenantColumn = "od.tenant_id", industryCode = "od.industry_code", deviceColumn = "recorder.device_code", alarmSourceVal = "13", acceptColumn = "od.accept_node")
    public Result<IotDeviceStatisticsVo> getRecorderDeviceStatistics(RecorderDeviceVO recorderDeviceVO) {
        recorderDeviceVO.setOrderStatus(0);
        List<RecorderDeviceDTO> deviceList = workbenchMapper.getRecorderDeviceStatistics(recorderDeviceVO);
        IotDeviceStatisticsVo vo = new IotDeviceStatisticsVo();
        if(CollectionUtil.isNotEmpty(deviceList)) {
            int total = deviceList.size();
            int deviceOnlineCount = Math.toIntExact(deviceList.stream()
                    .filter(device -> device.getDeviceStatus() == 0)
                    .count());
            vo.setIotDeviceCount(total);
            vo.setIotDeviceOfflineCount(total - deviceOnlineCount);
            vo.setIotDeviceOnlineCount(deviceOnlineCount);
            if (total > 0) {
                vo.setIotDeviceOnlinePercent(String.format("%.2f", (double) deviceOnlineCount / total));
            }
        }
        return Result.data(vo);
    }
}
