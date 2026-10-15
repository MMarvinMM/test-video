package com.chinatower.device.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.SiteInfoDto;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.dataobject.IotDeviceDO;
import com.chinatower.device.dto.SiteInfoMsgDTO;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.device.repository.IChannelInfoRepository;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.industry.dto.DeviceIndReqDTO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.device.DeviceVideoDTO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * video-device
 *
 * Created by zheng.yan on 2022/1/18.
 */
@RestController
@Slf4j
@Api(value = "设备通道信息推送模块", tags = "设备通道信息推送模块")
public class DeviceMessageController {

    @Autowired
    IDeviceInfoRepository deviceInfoRepository;

    @Autowired
    IIotDeviceRepository iotDeviceRepository;

    @Autowired
    IChannelInfoRepository channelInfoRepository;

    @Autowired
    private GisMapApi gisMapApi;

    /**
     * 站址点位信息通知推送
     *
     * @param siteInfoMsgDTO
     * @return com.chinatower.common.entity.Result
     * @author zheng.yan
     * @date 2022/1/18
     */
    @PostMapping("/video/message/siteInfoDataPush")
    @Deprecated
    public Result<String> siteInfoDataPush(@RequestBody SiteInfoMsgDTO siteInfoMsgDTO) {
        log.info("站址点位信息通知推送：" + JSON.toJSONString(siteInfoMsgDTO));
        try {
            if (!CollectionUtils.isEmpty(siteInfoMsgDTO.getData())) {
                for (SiteInfoDto siteInfo : siteInfoMsgDTO.getData()) {
                    if (StringUtils.isBlank(siteInfo.getSiteCode())) {
                        return Result.fail(ResultCode.FAILURE, "【站址编码】不能为空");
                    }
                    if (siteInfo.getLatitude() == null || siteInfo.getLongitude() == null) {
                        return Result.fail(ResultCode.FAILURE, "经纬度不能为空");
                    }
                    // 坐标系转换 84坐标系  to 火星坐标系
//                    GPS gcj02Gps = GPSConverterUtils.gps84_To_Gcj02(siteInfo.getLatitude(), siteInfo.getLongitude());
//                    if (gcj02Gps != null) {
//                        siteInfo.setLatitude(gcj02Gps.getLat());
//                        siteInfo.setLongitude(gcj02Gps.getLon());
//                    }
                    // 更新设备上的站址信息
                    deviceInfoRepository.updateDeviceInfoBySiteInfo(siteInfo);
                }
            }
        } catch (Exception e) {
            log.error("=================5.4.11.站址点位信息通知推送异常====================：", e);
            return Result.success(ResultCode.FAILURE);
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 新增通道(设备绑定通道) (MQ推送)
     *
     * @param
     * @return Result
     * @author zheng.yan
     * @date 2022/1/24
     */
//    @PostMapping("/video/message/addChannels")
//    public Result<String> addChannels(@RequestBody List<BasicDeviceDTO> deviceDTOList) {
//        log.info("addChannels message：" + JSON.toJSONString(deviceDTOList));
//        if (deviceDTOList == null || deviceDTOList.isEmpty()) {
//            return Result.success(ResultCode.SUCCESS);
//        }
//        try {
//            List<DeviceInfoDO> deviceInfoList = new ArrayList<>(deviceDTOList.size());
//            List<ChannelInfoDO> channelInfoList = new ArrayList<>(deviceDTOList.size() * 3);
//
//            // 获取报文中的设备、通道编码
//            List<String> deviceCodes = new ArrayList<>(deviceDTOList.size());
//            List<String> channelCodes = new ArrayList<>(deviceDTOList.size() * 3);
//            for (BasicDeviceDTO basicDeviceDTO : deviceDTOList) {
//                if (StringUtils.isNotBlank(basicDeviceDTO.getDeviceCode())) {
//                    deviceCodes.add(basicDeviceDTO.getDeviceCode());
//                }
//                if (!CollectionUtils.isEmpty(basicDeviceDTO.getChannels())) {
//                    for (BasicDeviceChannelBo channelBo : basicDeviceDTO.getChannels()) {
//                        if (StringUtils.isBlank(channelBo.getChannelCode())) {
//                            continue;
//                        }
//                        channelCodes.add(channelBo.getChannelCode());
//                    }
//                }
//            }
//            List<DeviceInfoDO> sameDeviceInfoList = deviceInfoRepository.listDeviceByCodes(deviceCodes);
//            List<ChannelInfoDO> sameChannelInfoList = channelInfoRepository.listChannelByCodes(channelCodes);
//            Date now = new Date();
//            for (BasicDeviceDTO basicDeviceDTO : deviceDTOList) {
//                DeviceInfoDO deviceInfoDO = new DeviceInfoDO();
//                DeviceInfoDO sameDevice = this.getSameDeviceByCode(basicDeviceDTO.getDeviceCode(), sameDeviceInfoList);
//                if (sameDevice != null) {
//                    // 应用修改后，只能修改状态
//                    if (DeviceConstants.YES_FLAG.equals(sameDevice.getAppModFlag())) {
//                        deviceInfoDO.setDeviceStatus(sameDevice.getDeviceStatus());
//                        deviceInfoDO.setAppModFlag(sameDevice.getAppModFlag());
//                    }
//                    else {
//                        BeanUtils.copyProperties(basicDeviceDTO, deviceInfoDO);
//                    }
//                    deviceInfoDO.setDeviceId(sameDevice.getDeviceId());
//                    deviceInfoDO.setModifierDate(now);
//                } else {
//                    BeanUtils.copyProperties(basicDeviceDTO, deviceInfoDO);
//                    deviceInfoDO.setCreatorDate(now);
//                }
//
//                if (!DeviceConstants.YES_FLAG.equals(deviceInfoDO.getAppModFlag())) {
//                    // 高度保留2位小数
//                    BigDecimal height = new BigDecimal(deviceInfoDO.getHeight());
//                    deviceInfoDO.setHeight(height.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
//                    // 84坐标系  to 火星坐标系，精度保留9位小数
//                    String longitude = deviceInfoDO.getLongitude();
//                    String latitude = deviceInfoDO.getLatitude();
//                    if (!org.springframework.util.StringUtils.isEmpty(longitude) && !org.springframework.util.StringUtils.isEmpty(latitude)) {
//                        double lat = Double.parseDouble(latitude);
//                        double lon = Double.parseDouble(longitude);
//                        GPS gps = GPSConverterUtils.gps84_To_Gcj02(lat, lon);
//                        deviceInfoDO.setLatitude(String.format("%.9f", gps.getLat()));
//                        deviceInfoDO.setLongitude(String.format("%.9f", gps.getLon()));
//                        log.info("84to火星坐标系坐标转换返回报文：经度：" + gps.getLon() + ";纬度：" + gps.getLat());
//                    }
//
//                    deviceInfoDO.setModelCode(basicDeviceDTO.getModelId());
//                    deviceInfoDO.setDeviceName(basicDeviceDTO.getDevName());
//                    deviceInfoDO.setDeviceStatus(basicDeviceDTO.getStatus());
//                    deviceInfoDO.setBusinStatus(basicDeviceDTO.getEnable());
//                    deviceInfoDO.setPlatFormCode(basicDeviceDTO.getPlatformCode());
//                    deviceInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
//
//                    // 2022/1/24 根据经纬度转换国家行政区域
//                    GisKeyWordQueryReq gisKeyWordQueryReq = new GisKeyWordQueryReq();
//                    gisKeyWordQueryReq.setLat(deviceInfoDO.getLatitude());
//                    gisKeyWordQueryReq.setLon(deviceInfoDO.getLongitude());
//                    gisKeyWordQueryReq.setMapType(DeviceConstants.MAP_TYPE_AMAP);
//
//                    Result result = gisAPI.countryAreaQuery(gisKeyWordQueryReq);
//                    if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
//                        Map<String, Object> resMap = (Map) result.getData();
//                        deviceInfoDO.setProvinceId((String) resMap.get("provinceCode"));
//                        deviceInfoDO.setCityId((String) resMap.get("cityCode"));
//                        deviceInfoDO.setCountyId((String) resMap.get("districtCode"));
//                        deviceInfoDO.setTownCode((String) resMap.get("townCode"));
//                    }
//                }
//
//                deviceInfoList.add(deviceInfoDO);
//                // 通道信息处理
//                if (basicDeviceDTO.getChannels() == null || basicDeviceDTO.getChannels().isEmpty()) {
//                    continue;
//                }
//                for (BasicDeviceChannelBo channelBo : basicDeviceDTO.getChannels()) {
//                    ChannelInfoDO channelInfoDO = new ChannelInfoDO();
//                    BeanUtils.copyProperties(channelBo, channelInfoDO);
//                    channelInfoDO.setDeviceCode(basicDeviceDTO.getDeviceCode());
//                    channelInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
//                    ChannelInfoDO sameChannel = this.getSameChannelByCode(channelBo.getChannelCode(), sameChannelInfoList);
//                    if (sameChannel != null) {
//                        channelInfoDO.setChannelId(sameChannel.getChannelId());
//                        channelInfoDO.setModifierDate(now);
//                    }
//                    else {
//                        channelInfoDO.setCreatorDate(now);
//                    }
//                    channelInfoList.add(channelInfoDO);
//                }
//            }
//            if (!deviceInfoList.isEmpty()) {
//                deviceInfoRepository.saveOrUpdateBatch(deviceInfoList);
//            }
//            if (!channelInfoList.isEmpty()) {
//                channelInfoRepository.saveOrUpdateBatch(channelInfoList);
//            }
//        } catch (Exception e) {
//            log.error("addChannels", e);
//            return Result.fail(ResultCode.FAILURE);
//        }
//        return Result.success(ResultCode.SUCCESS);
//    }

    /**
     * 查看设备属性
     *
     * @param deviceIndReqDTO
     * @return DeviceIndDTO
     * @author trk
     * @date 2022/4/1
     */
    @PostMapping("/video/message/queryDeviceAttribute")
    public DeviceIndDTO queryDeviceAttribute(@RequestBody DeviceIndReqDTO deviceIndReqDTO) {
        return deviceInfoRepository.getDeviceBasicInfo(deviceIndReqDTO);
    }

    /**
     * 查看通道属性
     *
     * @param deviceIndReqDTO
     * @return DeviceIndDTO
     * @author trk
     * @date 2022/4/2
     */
    @PostMapping("/video/message/queryChannelAttribute")
    public DeviceIndDTO queryChannelAttribute(@RequestBody DeviceIndReqDTO deviceIndReqDTO) {
        return deviceInfoRepository.getChannelBasicInfo(deviceIndReqDTO);
    }

    /**
     * 新增传感器设备（含雷达）
     *
     * @param iotDevices
     * @return Result
     * @author zheng.yan
     * @date 2022/5/10
     */
    @PostMapping("/video/message/addIotDevices")
    public Result<String> addIotDevices(@RequestBody List<IotDeviceMsgDTO> iotDevices) {
        log.info("addIotDevices message：" + JSON.toJSONString(iotDevices));
        if (iotDevices == null || iotDevices.isEmpty()) {
            return Result.success(ResultCode.SUCCESS);
        }
        try {
            List<IotDeviceDO> iotDeviceDOList = new ArrayList<>(iotDevices.size());
            List<String> iotDeviceCodes = new ArrayList<>(iotDevices.size());
            iotDevices.forEach(e -> iotDeviceCodes.add(e.getDeviceCode()));
            List<IotDeviceDO> sameDeviceInfoList = iotDeviceRepository.listIotDeviceByCodes(iotDeviceCodes);
            for(IotDeviceMsgDTO iotDeviceMsg:iotDevices){
                IotDeviceDO iotDeviceDO = iotDeviceRepository.buildIotDeviceDo(iotDeviceMsg, sameDeviceInfoList);

                if (!DeviceConstants.YES_FLAG.equals(iotDeviceDO.getAppModFlag())) {
                    // 高度保留2位小数
                    BigDecimal height = new BigDecimal(iotDeviceDO.getHeight());
                    iotDeviceDO.setHeight(height.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                    iotDeviceDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);

                    // 2022/1/24 根据经纬度转换国家行政区域
                    PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                    req.setLocation(String.format("%s,%s", iotDeviceDO.getLongitude(), iotDeviceDO.getLatitude()));
                    Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                    log.info("addIotDevices gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                    if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
                        try {
                            PoiContraryGeocodingQueryRes data = result.getData().get(0);
                            if (data.getRegion() != null) {
                                Region region = data.getRegion();
                                if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                                    iotDeviceDO.setProvinceId(region.getProvinceCode());
                                }
                                if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                                    iotDeviceDO.setCityId(region.getCityCode());
                                }
                                if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                                    iotDeviceDO.setCountyId(region.getCountyCode());
                                }
                                if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                                    iotDeviceDO.setTownCode(region.getTownCode());
                                }
                            }
                        } catch (Exception e) {
                            log.error("addIotDevices gisMapApi poiContraryGeocodingQuery error, e:", e);
                        }
                    }
                }
                iotDeviceDOList.add(iotDeviceDO);
            }
            if (!iotDeviceDOList.isEmpty()) {
                iotDeviceRepository.saveOrUpdateBatch(iotDeviceDOList);
            }
        }
        catch (Exception e) {
            log.error("addChannels", e);
            return Result.fail(ResultCode.FAILURE);
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 更新设备用户数缓存
     * @author chenmf
     * @date 2022/5/17
     * @param
     * @return 设备列表
     */
    @PostMapping("/video/updateDeviceUserNum")
    Result<String> updateDeviceUserNum(@RequestBody DeviceVideoDTO deviceVideoDTO) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -1);
        Date date = calendar.getTime();
        String dateStr = DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, date).substring(0, 12);
        String key = deviceVideoDTO.getVideoType() + "_" + dateStr + "_" + deviceVideoDTO.getDeviceCode();

        RedisUtil.incrBy(key, 1L, RedisDBType.PRIMARY);
        RedisUtil.expire(key, 11 * 60, TimeUnit.SECONDS, RedisDBType.PRIMARY);
        return Result.success("");
    }
}
