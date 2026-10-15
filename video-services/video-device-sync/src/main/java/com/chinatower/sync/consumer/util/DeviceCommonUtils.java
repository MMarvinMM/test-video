package com.chinatower.sync.consumer.util;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.vo.AIotDeviceCenterRsp;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.enums.AIotPlatformCommonEnum;
import com.chinatower.sync.consumer.item.*;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Component
public class DeviceCommonUtils {

    @Value("${unified.gateway.userCode:typt-fangy}")
    private String userCode;

    @Autowired
    private GisMapApi gisMapApi;

    @Resource
    private NetConnectUtils netConnectUtils;

    @Resource
    private SystemFeignClient systemFeignClient;

    private static final String CONFIG_PLAT_CODE = "platform_code";

    public PoiContraryGeocodingQueryRes getRegionInfoByLonAndLat(String longitude, String latitude) {
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", longitude, latitude));
        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
        log.info("poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && CollectionUtils.isNotEmpty(result.getData())) {
            try {
                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                if (data.getRegion() != null) {
                    return data;
                }
            } catch (Exception e) {
                log.error("通过经纬度转化省市区失败:", e);
            }
        }
        return null;
    }

    public AIotDeviceCenterRsp callDeviceCenter(Object inParam, NetConnectUtils.RequestParamter.URLMappingEnum urlMappingEnum, String accessNode) {
        NetConnectUtils.RequestParamter<Object> body = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(urlMappingEnum);
        body.setPath(path);
        body.setBody(inParam);
        String platformCode = getCurrentLocationCode();
        try {
            log.info("请求设备中心参数:{}", body);
            AIotDeviceCenterRsp response = netConnectUtils.post(body, AIotDeviceCenterRsp.class,
                    NetConnectUtils.HeaderKeyEnum.X_UserCode.of(userCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(StringUtils.isEmpty(accessNode) ? platformCode : accessNode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_AIOT),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(netConnectUtils.getRealUrl(body)));
            log.info("设备中心响应参数:{}", response);
            return response;
        } catch (Exception e) {
            log.info("callDeviceCenter 调用设备中心异常：", e);
            throw new CustomException(e.getMessage(), HttpStatus.ERROR_4001);
        }
    }

    private String getCurrentLocationCode() {
        AjaxResult ajaxResult = systemFeignClient.getConfValue(CONFIG_PLAT_CODE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (Objects.isNull(sysConfig)) {
                throw new CustomException("当前平台编码未配置，请配置后重试");
            }
            return sysConfig.getConfigValue();
        }
        else {
            throw new CustomException("获取平台编码配置失败，请稍后重试！");
        }
    }

    public HornStatusReport hornStatusDTOConvert(AIotDeviceStatusSync item) {
        HornStatusReport one = new HornStatusReport();
        one.setDeviceCode(item.getDeviceCode());
        one.setOnlineStatus(item.getDeviceStatus());
        one.setStatusVersion(item.getSourceVersion());
        return one;
    }

    public DeviceBroadcastReport hornDTOConvert(AIotDeviceSync item) {
        DeviceBroadcastReport one = new DeviceBroadcastReport();
        one.setDeviceCode(item.getDeviceCode());
        one.setDeviceName(item.getDeviceName());
        one.setCatalogType(item.getSubDevType());
        one.setSiteCode(item.getSiteCode());
        one.setSiteName(item.getSiteName());
        PoiContraryGeocodingQueryRes regionInfo = getRegionInfoByLonAndLat(item.getLongitude(), item.getLatitude());
        if (regionInfo != null) {
            if (StringUtils.isEmpty(regionInfo.getRegion().getProvinceCode())) {
                one.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                one.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
                one.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
            } else {
                one.setProvinceCode(regionInfo.getRegion().getProvinceCode());
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCityCode())) {
                    one.setCityCode(regionInfo.getRegion().getCityCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCountyCode())) {
                    one.setCountyCode(regionInfo.getRegion().getCountyCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getTownCode())) {
                    one.setTownCode(regionInfo.getRegion().getTownCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getAddress())) {
                    one.setAddress(regionInfo.getAddress());
                }
            }
        } else {
            one.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
            one.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
            one.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
        }
        one.setLongitude(item.getLongitude());
        one.setLatitude(item.getLatitude());
        one.setDept(item.getManageArea());
        one.setAdminArea(item.getAdminArea());
        one.setManufacturer(item.getFactoryCode());
        one.setDeviceModel(item.getDeviceModel());
        if (Objects.nonNull(item.getDeviceExtInfo())) {
            Map<String, String> extInfo = getExtInfo(item.getDeviceExtInfo());
            one.setHangingHeight(extInfo.get("height"));
            one.setBroadcastResourceCode(extInfo.get("broadcastResourceCode"));
        }
        one.setUpdateBy(item.getUpdateBy());
        one.setDelFlag(item.getDelFlag());
        one.setLocationCode(item.getLocationCode());
        Date updateTime = item.getUpdateTime() != null ? item.getUpdateTime() : new Date();
        one.setUpdateTime(DateFormatUtils.format(updateTime, "yyyy-MM-dd HH:mm:ss"));
        one.setDeviceVersion(item.getSourceVersion());
        return one;
    }

    private Map<String, String> getExtInfo(String deviceExtInfo) {
        List<DeviceExtInfo> extInfoList = JSON.parseArray(deviceExtInfo, DeviceExtInfo.class);
        return extInfoList.stream()
                .collect(Collectors.toMap(
                        DeviceExtInfo::getExtendField,
                        DeviceExtInfo::getExtendvalue,
                        (existingValue, newValue) -> newValue
                ));
    }

    public SensorStatusReport sensorStatusDTOConvert(AIotDeviceStatusSync item) {
        SensorStatusReport data = new SensorStatusReport();
        data.setDeviceCode(item.getDeviceCode());
        if (AIotPlatformCommonEnum.AIOT_ONLINE_STATUS == item.getOnlineStatus()) {
            data.setOnlineStatus(AIotPlatformCommonEnum.PLATFORM_SENSOR_ONLINE_STATUS);
        }
        else if (AIotPlatformCommonEnum.AIOT_OFFLINE_STATUS == item.getOnlineStatus()) {
            data.setOnlineStatus(AIotPlatformCommonEnum.PLATFORM_SENSOR_OFFLINE_STATUS);
        }
        else {
            data.setOnlineStatus(item.getOnlineStatus());
        }
        data.setStatusVersion(item.getSourceVersion());
        return data;
    }

    public SensorStatusReport sensorDTOConvert(AIotDeviceSync item) {
        SensorStatusReport data = new SensorStatusReport();
        data.setDeviceCode(item.getDeviceCode());
        data.setDeviceName(item.getDeviceName());
        data.setCatalogType(item.getSubDevType());
        data.setSiteCode(item.getSiteCode());
        // 物联设备
        data.setDeviceType(2);
        data.setSiteName(item.getSiteName());
        PoiContraryGeocodingQueryRes regionInfo = getRegionInfoByLonAndLat(item.getLongitude(), item.getLatitude());
        if (regionInfo != null) {
            if (StringUtils.isEmpty(regionInfo.getRegion().getProvinceCode())) {
                data.setProvinceId(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                data.setCityId(DeviceNorthIntfConstants.BEIJING_CITY);
                data.setCountyId(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
            } else {
                data.setProvinceId(regionInfo.getRegion().getProvinceCode());
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCityCode())) {
                    data.setCityId(regionInfo.getRegion().getCityCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCountyCode())) {
                    data.setCountyId(regionInfo.getRegion().getCountyCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getTownCode())) {
                    data.setTownCode(regionInfo.getRegion().getTownCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getAddress())) {
                    data.setAddress(regionInfo.getAddress());
                }
            }
        } else {
            data.setProvinceId(DeviceNorthIntfConstants.BEIJING_PROVINCE);
            data.setCityId(DeviceNorthIntfConstants.BEIJING_CITY);
            data.setCountyId(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
        }
        data.setLongitude(item.getLongitude());
        data.setLatitude(item.getLatitude());
        data.setDept(item.getManageArea());
        data.setAdminArea(item.getAdminArea());
        data.setManufacturer(item.getFactoryCode());
        data.setDeviceModel(item.getDeviceModel());
        if (Objects.nonNull(item.getDeviceExtInfo())) {
            Map<String, String> extInfo = getExtInfo(item.getDeviceExtInfo());
            data.setHangingHeight(extInfo.get("height"));
            data.setShareProperty(extInfo.get("shareProperty"));
            data.setInstallTime(extInfo.get("installTime"));
            data.setAreaCode(extInfo.get("deptId"));
        }
        data.setUpdateBy(item.getUpdateBy());
        data.setDeleteFlag(item.getDelFlag());
        data.setLocationCode(item.getLocationCode());
        Date updateTime = item.getUpdateTime() != null ? item.getUpdateTime() : new Date();
        data.setUpdateTime(DateFormatUtils.format(updateTime, "yyyy-MM-dd HH:mm:ss"));
        data.setDeviceVersion(item.getSourceVersion());
        return data;
    }

    public List<AIotDeviceSync> auditDeviceInfo(String param, String platformCode) {
        AuditDeviceReq req = assembleAuditReq(param, platformCode);
        List<AIotDeviceSync> allDeviceDataList = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            log.info("当前调用设备中心稽核设备次数{}", i);
            AIotDeviceCenterRsp resultVO = callDeviceCenter(req, NetConnectUtils.RequestParamter.URLMappingEnum.DEVICE_CENTER_QRY_DEVICE_LIST, "");
            if (Objects.isNull(resultVO)) {
                log.error("调用设备中心稽核设备失败");
                break;
            }
            if (!AIotPlatformCommonEnum.CODE_SUCCESS_CODE.equals(resultVO.getCode())) {
                log.error("调用设备中心稽核设备失败{}", JSON.toJSONString(resultVO));
                break;
            }
            AuditDeviceInfoDataRsp resultData = JSON.parseObject(JSON.toJSONString(resultVO.getData()), AuditDeviceInfoDataRsp.class);
            allDeviceDataList.addAll(resultData.getData());
            req.setLastDataId(resultData.getLastDataId());
            boolean hasMore = !Objects.isNull(resultData.getHasMore()) && resultData.getHasMore();
            if (!hasMore) {
                break;
            }
        }
        return allDeviceDataList;
    }

    public List<AIotDeviceStatusSync> auditDeviceStatusInfo(String param, String platformCode) {
        AuditDeviceReq req = assembleAuditReq(param, platformCode);
        List<AIotDeviceStatusSync> allDeviceDataList = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            log.info("当前调用设备中心稽核状态次数{}", i);
            AIotDeviceCenterRsp resultVO = callDeviceCenter(req, NetConnectUtils.RequestParamter.URLMappingEnum.DEVICE_CENTER_GET_DEVICE_STATUS, "");
            if (Objects.isNull(resultVO)) {
                log.error("调用设备中心稽核状态失败");
                break;
            }
            if (!AIotPlatformCommonEnum.CODE_SUCCESS_CODE.equals(resultVO.getCode())) {
                log.error("调用设备中心稽核状态失败{}", JSON.toJSONString(resultVO));
                break;
            }
            AuditDeviceStatusDataRsp resultData = JSON.parseObject(JSON.toJSONString(resultVO.getData()), AuditDeviceStatusDataRsp.class);
            allDeviceDataList.addAll(resultData.getData());
            req.setLastDataId(resultData.getLastDataId());
            boolean hasMore = !Objects.isNull(resultData.getHasMore()) && resultData.getHasMore();
            if (!hasMore) {
                break;
            }
        }
        return allDeviceDataList;
    }

    private AuditDeviceReq assembleAuditReq(String paramString, String platformCode) {
        AuditDeviceReq req = new AuditDeviceReq();
        if (org.springframework.util.StringUtils.isEmpty(paramString)) {
            long now = Instant.now().toEpochMilli();
            long oneHourAgo = Instant.now().minusSeconds(3600).toEpochMilli();
            req.setStartTime(oneHourAgo);
            req.setEndTime(now);
        }
        else {
            AuditDeviceJobParam param = JSON.parseObject(paramString, AuditDeviceJobParam.class);
            req.setStartTime(param.getStartTime());
            req.setEndTime(param.getEndTime());
        }
        req.setPlatformCode(platformCode);
        return req;
    }
}

