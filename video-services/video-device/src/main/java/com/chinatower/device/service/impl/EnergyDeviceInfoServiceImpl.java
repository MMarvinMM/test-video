package com.chinatower.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.dto.CasCadeOrgInitDTO;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.EnergyDeviceInfoMapper;
import com.chinatower.device.mongodb.EnergyDeviceMongoDAO;
import com.chinatower.device.service.IEnergyDeviceInfoService;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.CasCadeOrgBindVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnergyDeviceInfoServiceImpl implements IEnergyDeviceInfoService {

    @Autowired
    private EnergyDeviceInfoMapper energyDeviceInfoMapper;

    @Autowired
    private EnergyDeviceMongoDAO energyDeviceMongoDAO;
    @Autowired
    private SysAreaRootService sysAreaRootService;

    @Autowired
    private GisMapApi gisMapApi;

    private static final String USER_NOT_LOGIN_MSG = "用户未登录";

    @Override
    public List<EnergyDeviceStationVO> queryStationList(EnergyDeviceStationQueryReq param) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(USER_NOT_LOGIN_MSG);
        }
        if (param.getPageNum() == null) {
            param.setPageNum(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        SysUser user = loginUser.getUser();
        List<String> orderStationCodeList = energyDeviceInfoMapper.queryOrderStationCodeList(user.getTenantId(), user.getIndustryCode());
        if (CollectionUtils.isEmpty(orderStationCodeList)) {
            return Lists.newArrayList();
        }
        param.setOrderStationCodeList(orderStationCodeList);

        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        return energyDeviceInfoMapper.queryStationList(param);
    }

    @Override
    public List<EnergyDeviceVO> queryDeviceList(EnergyDeviceQueryReq param) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(USER_NOT_LOGIN_MSG);
        }
        if (param.getPageNum() == null) {
            param.setPageNum(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        SysUser user = loginUser.getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        return energyDeviceInfoMapper.queryDeviceList(param);
    }

    @Override
    public List<EnergyDeviceTreeVO> queryDeviceTree(EnergyDeviceTreeQueryReq param) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(USER_NOT_LOGIN_MSG);
        }
        SysUser user = loginUser.getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        //组织与绑定的设备
        List<EnergyDeviceTreeVO> treeList = energyDeviceInfoMapper.queryDeviceTree(param);
        List<EnergyDeviceTreeVO> list = buildIotMonitorTree(treeList);
        List<EnergyDeviceTreeVO> rootTree = new ArrayList<>(1);
        if (!treeList.isEmpty()) {
            // 补充中国根节点
            EnergyDeviceTreeVO root = new EnergyDeviceTreeVO();
            sysAreaRootService.createRootTreeVO(root);
            root.setOnlineNum(list.stream().mapToInt(EnergyDeviceTreeVO::getOnlineNum).sum());
            root.setDeviceNum(list.stream().mapToInt(EnergyDeviceTreeVO::getDeviceNum).sum());
            //去掉异常省份
            list = list.stream().filter(dto ->
                    StringUtils.isNotEmpty(dto.getCode())).collect(Collectors.toList());
            root.setList(list);
            rootTree.add(root);
        }
        return rootTree;
    }

    public static List<EnergyDeviceTreeVO> buildIotMonitorTree(List<EnergyDeviceTreeVO> treeList) {
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        country.setOnlineNum((int) country.getList().stream().filter(deviceVo -> Objects.equals(Constants.ONE, deviceVo.getStatus())).count());
                        country.setDeviceNum(country.getList().size());
                        country.getList().clear();
                    });

                    city.setOnlineNum(city.getList().stream().mapToInt(EnergyDeviceTreeVO::getOnlineNum).sum());
                    city.setDeviceNum(city.getList().stream().mapToInt(EnergyDeviceTreeVO::getDeviceNum).sum());
                    //如果下级设备都为收藏，则上级设置为收藏
                    //去掉异常区县
                    city.setList(city.getList().stream().filter(areaDto ->
                            StringUtils.isNotEmpty(areaDto.getCode())).collect(Collectors.toList()));
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(EnergyDeviceTreeVO::getOnlineNum).sum());
                pro.setDeviceNum(pro.getList().stream().mapToInt(EnergyDeviceTreeVO::getDeviceNum).sum());
                //去掉异常地市
                pro.setList(pro.getList().stream().filter(proDto ->
                        StringUtils.isNotEmpty(proDto.getCode())).collect(Collectors.toList()));
            } else {
                pro.setOnlineNum(0);
                pro.setDeviceNum(0);
            }
        });
        return treeList;
    }

    @Override
    public Page<EnergyMonitorDataMapVO> queryMonitorDataList(EnergyDeviceMonitorQueryReq param) {
        if (param.getPageNum() == null) {
            param.setPageNum(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        return energyDeviceMongoDAO.pageMonitorDataList(param);
    }

    @Override
    public Page<EnergyDeviceAlarmVO> queryDeviceAlarmList(EnergyDeviceAlarmQueryReq param) {
        // 根据devName、devType、stationCodeList查询出来deviceCodeList
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(USER_NOT_LOGIN_MSG);
        }
        SysUser user = loginUser.getUser();
        EnergyDeviceQueryDTO deviceQueryParam = new EnergyDeviceQueryDTO();
        deviceQueryParam.setTenantId(user.getTenantId());
        deviceQueryParam.setIndustryCode(user.getIndustryCode());
        deviceQueryParam.setDevCode(param.getDevCode());
//        deviceQueryParam.setDevName(param.getDevName());
        if (CollectionUtils.isNotEmpty(param.getDevTypeList())) {
            deviceQueryParam.setDevTypeList(param.getDevTypeList());
        }
        deviceQueryParam.setStationCodeList(param.getStationCodeList());
        List<String> deviceCodeList = energyDeviceInfoMapper.queryDeviceListByCondition(deviceQueryParam);

        if (CollectionUtils.isEmpty(deviceCodeList)) {
            Page<EnergyDeviceAlarmVO> resultPage = new Page<>();
            resultPage.setCount(0L);
            resultPage.setList(Lists.newArrayList());
            return resultPage;
        }
        if (param.getPageNum() == null) {
            param.setPageNum(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        return energyDeviceMongoDAO.pageDeviceAlarmDataList(param, deviceCodeList);
    }

    @Override
    public List<EnergyDeviceStatusStatisticsVO> queryDeviceStatusStatisticsByStation(EnergyDeviceStatusStatisticsQueryReq param) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(USER_NOT_LOGIN_MSG);
        }
        SysUser user = loginUser.getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        return energyDeviceInfoMapper.queryDeviceStatusStatisticsByStation(param);
    }

    @Override
    public EnergyDeviceStatusStatisticsVO queryDeviceStatusStatistics(EnergyDeviceStatusStatisticsQueryReq param) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(USER_NOT_LOGIN_MSG);
        }
        SysUser user = loginUser.getUser();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(user.getIndustryCode());
        return energyDeviceInfoMapper.queryDeviceStatusStatistics(param);
    }

    @Override
    public EnergyDeviceAlarmStatisticsVO queryDeviceAlarmStatistics(EnergyDeviceAlarmStatisticsQueryReq param) {

        // 根据businessType、stationCodeList查询出来deviceCodeList
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new CustomException(USER_NOT_LOGIN_MSG);
        }
        SysUser user = loginUser.getUser();
        EnergyDeviceQueryDTO deviceQueryParam = new EnergyDeviceQueryDTO();
        deviceQueryParam.setTenantId(user.getTenantId());
        deviceQueryParam.setIndustryCode(user.getIndustryCode());
        deviceQueryParam.setBusinessType(param.getBusinessType());
        deviceQueryParam.setStationCodeList(param.getStationCodeList());
        List<String> deviceCodeList = energyDeviceInfoMapper.queryDeviceListByCondition(deviceQueryParam);

        EnergyDeviceAlarmStatisticsVO resultData = new EnergyDeviceAlarmStatisticsVO();

        if (CollectionUtils.isEmpty(deviceCodeList)) {
            resultData.setAlarmTotal(0L);
            resultData.setAlarmProgress(0L);
            resultData.setAlarmCompleted(0L);
            return resultData;
        }

        resultData.setAlarmTotal(energyDeviceMongoDAO.countDeviceAlarm(param, deviceCodeList));
        // 1活动告警；0清除告警
        param.setAlarmStatus("1");
        resultData.setAlarmProgress(energyDeviceMongoDAO.countDeviceAlarm(param, deviceCodeList));
        // 1活动告警；0清除告警
        param.setAlarmStatus("0");
        resultData.setAlarmCompleted(energyDeviceMongoDAO.countDeviceAlarm(param, deviceCodeList));
        return resultData;
    }

    @Override
    public void refreshStationRegion() {
        List<EnergyDeviceStationVO> stationInfoList = energyDeviceInfoMapper.queryAllStationInfo();
        List<EnergyDeviceStationVO> updateList = fillStationRegion(stationInfoList);
        if (CollectionUtils.isNotEmpty(updateList)) {
            energyDeviceInfoMapper.updateStationRegionBatch(stationInfoList);
        }
    }

    private List<EnergyDeviceStationVO> fillStationRegion(List<EnergyDeviceStationVO> stationInfoList) {
        List<EnergyDeviceStationVO> resultList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(stationInfoList)) {
            return resultList;
        }
        for (EnergyDeviceStationVO stationVO : stationInfoList) {
            PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
            req.setLocation(String.format("%s,%s", stationVO.getLon(), stationVO.getLat()));
            Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
            log.info("fillStationRegion gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
            if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
                try {
                    PoiContraryGeocodingQueryRes data = result.getData().get(0);
                    if (data.getRegion() != null) {
                        Region region = data.getRegion();
                        if (StringUtils.isNotEmpty(region.getProvinceCode())) {
                            stationVO.setProvinceId(region.getProvinceCode());
                        }
                        if (StringUtils.isNotEmpty(region.getCityCode())) {
                            stationVO.setCityId(region.getCityCode());
                        }
                        if (StringUtils.isNotEmpty(region.getCountyCode())) {
                            stationVO.setCountyId(region.getCountyCode());
                        }
                        if (StringUtils.isNotEmpty(region.getProvince())) {
                            stationVO.setProvinceName(region.getProvince());
                        }
                        if (StringUtils.isNotEmpty(region.getCity())) {
                            stationVO.setCityName(region.getCity());
                        }
                        if (StringUtils.isNotEmpty(region.getCounty())) {
                            stationVO.setCountyName(region.getCounty());
                        }
                    }
                    if (StringUtils.isNoneBlank(data.getAddress())) {
                        stationVO.setAddress(data.getAddress());
                    }
                    resultList.add(stationVO);
                } catch (Exception e) {
                    log.error("addIotDevices gisMapApi poiContraryGeocodingQuery error, e:", e);
                }
            }
        }
        return resultList;
    }

    @Override
    public void handleHistoryData() {
        /*
         * 数据迁移
         *  1、 customerCode迁移到sys_tenant_cascade_device_rel表的custom_channel_code
         *  2、 摄像机的countyCode迁移到sys_tenant_cascade_device_rel表的org_code
         *  3、 级联组织表初始化 sys_tenant_cascade_org_rel
         */
        energyDeviceInfoMapper.refreshChannelCustomCode();
        energyDeviceInfoMapper.refreshOrgCode();

        String userName = "system-init";
        Date now = new Date();

        List<CasCadeOrgBindVO> existOrgList = energyDeviceInfoMapper.queryCascadeOrgInfo();

        List<String> existsKeyList = existOrgList.stream().map(x -> {
            return x.getTenantId() + "_" + x.getPlatCode() + "_" + x.getOrgCode();
        }).collect(Collectors.toList());

        List<CasCadeOrgBindVO> insertList = Lists.newArrayList();
        List<CasCadeOrgInitDTO> cascadeDeviceList = energyDeviceInfoMapper.queryCascadeDeviceOrgInfo();
        if (CollectionUtils.isNotEmpty(cascadeDeviceList)) {
            for (CasCadeOrgInitDTO initDTO : cascadeDeviceList) {

                String bindCountyKey = initDTO.getTenantId() + "_" + initDTO.getPlatCode() + "_" + initDTO.getCountyId();
                if (!existsKeyList.contains(bindCountyKey)) {
                    CasCadeOrgBindVO bindCountyVO = new CasCadeOrgBindVO();
                    bindCountyVO.setTenantId(initDTO.getTenantId());
                    bindCountyVO.setPlatCode(initDTO.getPlatCode());
                    bindCountyVO.setOrgCode(initDTO.getCountyId());
                    bindCountyVO.setOrgName(initDTO.getCountyName());
                    bindCountyVO.setParentOrgCode(initDTO.getCityId());
                    bindCountyVO.setCreator(userName);
                    bindCountyVO.setCreateTime(now);
                    insertList.add(bindCountyVO);
                    // 增加之后加入存在key列表，防止重复添加
                    existsKeyList.add(bindCountyKey);
                }

                String bindCityKey = initDTO.getTenantId() + "_" + initDTO.getPlatCode() + "_" + initDTO.getCityId();
                if (!existsKeyList.contains(bindCityKey)) {
                    CasCadeOrgBindVO bindCityVO = new CasCadeOrgBindVO();
                    bindCityVO.setTenantId(initDTO.getTenantId());
                    bindCityVO.setPlatCode(initDTO.getPlatCode());
                    bindCityVO.setOrgCode(initDTO.getCityId());
                    bindCityVO.setOrgName(initDTO.getCityName());
                    bindCityVO.setParentOrgCode(initDTO.getProvinceId());
                    bindCityVO.setCreator(userName);
                    bindCityVO.setCreateTime(now);
                    insertList.add(bindCityVO);
                    // 增加之后加入存在key列表，防止重复添加
                    existsKeyList.add(bindCityKey);
                }

                String bindProvinceKey = initDTO.getTenantId() + "_" + initDTO.getPlatCode() + "_" + initDTO.getProvinceId();
                if (!existsKeyList.contains(bindProvinceKey)) {
                    CasCadeOrgBindVO bindProvinceVO = new CasCadeOrgBindVO();
                    bindProvinceVO.setTenantId(initDTO.getTenantId());
                    bindProvinceVO.setPlatCode(initDTO.getPlatCode());
                    bindProvinceVO.setOrgCode(initDTO.getProvinceId());
                    bindProvinceVO.setOrgName(initDTO.getProvinceName());
                    bindProvinceVO.setParentOrgCode(null);
                    bindProvinceVO.setCreator(userName);
                    bindProvinceVO.setCreateTime(now);
                    insertList.add(bindProvinceVO);
                    // 增加之后加入存在key列表，防止重复添加
                    existsKeyList.add(bindProvinceKey);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(insertList)) {
            log.info("EnergyDeviceInfoServiceImpl handleHistoryData insertList:{}", JSON.toJSONString(insertList));
            energyDeviceInfoMapper.insertCascadeOrgBatch(insertList);
        }
    }
}