package com.chinatower.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.order.domain.*;
import com.chinatower.order.repository.*;
import com.chinatower.order.service.IOrderVideoDeviceService;
import com.chinatower.order.util.PasswordUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.device.SaveOrUpdateChannelDTO;
import com.chinatower.platform.entity.device.SaveOrUpdateDeviceDTO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName IOrderVideoDeviceService
 * @Deacription 设备服务类
 * （为保证订购事务一致性，相关接口在order工程重写一份，后续逻辑有改动需要同步修改）
 * @Author chenmf
 * @Date 2022/6/10 16:17
 * @Version 1.0
 **/
@Slf4j
@Service
public class OrderVideoDeviceService implements IOrderVideoDeviceService {

    private static final String JOIN_COMMA_STR_TEMPLATE = "%s,%s";

    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;

    @Autowired
    private IIotDeviceRepository iotDeviceRepository;

    @Autowired
    private IChannelInfoRepository channelInfoRepository;

    @Autowired
    private IUavDeviceRepository uavDeviceRepository;

    @Autowired
    private IUavNestDeviceRepository uavNestDeviceRepository;

    @Autowired
    private IHornDeviceRepository hornDeviceRepository;

    @Autowired
    private IRecorderDeviceRepository recorderDeviceRepository;

    @Autowired
    private IRecorderChannelRepository recorderChannelRepository;

    @Autowired
    private GisMapApi gisMapApi;

    private static final String SYSTEM_STR = "system";

    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 批量保存或更新设备信息
     *
     * @return void
     * @param: deviceDTOList
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void batchSaveOrUpdateDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList) {
        if (!CollectionUtils.isEmpty(deviceDTOList)) {
            //新增设备列表
            List<DeviceInfoDO> addDeviceDTOList = new ArrayList<>();

            if(isDependenceFlag()){
                log.info("OrderVideoDeviceService batchSaveOrUpdateDevice localization release need not add device !");
                return;
            }

            // 根据设备编码列表查询设备信息
            List<DeviceInfoDO> deviceInfoList
                    = deviceInfoRepository.getDeviceByCodeList(deviceDTOList.stream()
                    .map(SaveOrUpdateDeviceDTO::getDeviceCode).distinct().collect(Collectors.toList()));
            // 摄像机map key -> 设备编码 value -> 设备信息
            Map<String, DeviceInfoDO> deviceInfoMap = deviceInfoList.stream().collect(Collectors.toMap(DeviceInfoDO::getDeviceCode, (p) -> p, (a,b) -> a));
            Iterator<SaveOrUpdateDeviceDTO> it = deviceDTOList.iterator();
            while (it.hasNext()) {
                SaveOrUpdateDeviceDTO deviceDTO = it.next();
                //查询设备是否存在
                DeviceInfoDO deviceInfoDO = deviceInfoMap.get(deviceDTO.getDeviceCode());
                // 2022-10-31跟学斌确认，订购只新增设备，不对已存在的设备进行更改
                if (null == deviceInfoDO) {
                    try {
                        //根据经纬度转换国家行政区域
                        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                        req.setLocation(String.format(JOIN_COMMA_STR_TEMPLATE, deviceDTO.getLongitude(), deviceDTO.getLatitude()));
                        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                        log.info("batchSaveOrUpdateDevice gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
                            PoiContraryGeocodingQueryRes data = result.getData().get(0);
                            if (data.getRegion() != null) {
                                Region region = data.getRegion();
                                if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                                    deviceDTO.setProvinceId(region.getProvinceCode());
                                }
                                if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                                    deviceDTO.setCityId(region.getCityCode());
                                }
                                if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                                    deviceDTO.setCountyId(region.getCountyCode());
                                }
                                if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                                    deviceDTO.setTownCode(region.getTownCode());
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("batchSaveOrUpdateDevice gisMapApi poiContraryGeocodingQuery error, e:", e);
                    }

                    deviceInfoDO = new DeviceInfoDO();
                    BeanUtils.copyProperties(deviceDTO, deviceInfoDO);
                    deviceInfoDO.setAccessNode(deviceDTO.getAccessNode());
                    deviceInfoDO.setCreator(SYSTEM_STR);
                    deviceInfoDO.setCreatorDate(DateUtils.getNowDate());
                    deviceInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                    addDeviceDTOList.add(deviceInfoDO);
                } else {
                    log.info("OrderVideoDeviceService 摄像机设备已经存在, 编码:{}，不对已有设备进行更改", deviceInfoDO.getDeviceCode());
                }
            }

            if (!addDeviceDTOList.isEmpty()) {
                deviceInfoRepository.saveBatch(addDeviceDTOList);
            }
        }
    }

    @Override
    public boolean isDependenceFlag() {
        // 是否属地化
        boolean isDependenceFlag = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(PasswordUtil.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                isDependenceFlag = true;
            }
        }
        return isDependenceFlag;
    }

    /**
     * 批量保存或更新物联设备信息
     *
     * @return void
     * @param: deviceDTOList
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void batchSaveOrUpdateIotDevice(List<IotDeviceMsgDTO> iotDevices) {
        if (iotDevices.isEmpty()) {
            return;
        }
        // 是否属地化
        Boolean isDependenceFlag = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(PasswordUtil.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                isDependenceFlag = true;
            }
        }
        if(isDependenceFlag){
            log.info("OrderVideoDeviceService batchSaveOrUpdateIotDevice localization release need not add device !");
            return;
        }
        List<IotDeviceDO> iotDeviceDOList = new ArrayList<>(iotDevices.size());
        List<String> iotDeviceCodes = new ArrayList<>(iotDevices.size());
        iotDevices.forEach(e -> iotDeviceCodes.add(e.getDeviceCode()));
        List<IotDeviceDO> sameDeviceInfoList = iotDeviceRepository.listIotDeviceByCodes(iotDeviceCodes);
        for (IotDeviceMsgDTO iotDeviceMsg : iotDevices) {
            IotDeviceDO iotDeviceDO = iotDeviceRepository.buildIotDeviceDo(iotDeviceMsg, sameDeviceInfoList);
            if (iotDeviceDO == null) {
                continue;
            }
            if (StringUtils.isNotBlank(iotDeviceDO.getAdmCode())) {
                //根据经纬度转换国家行政区域
                PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                req.setLocation(String.format(JOIN_COMMA_STR_TEMPLATE, iotDeviceDO.getLongitude(), iotDeviceDO.getLatitude()));
                Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                log.info("batchSaveOrUpdateIotDevice gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
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
                        log.error("batchSaveOrUpdateIotDevice gisMapApi poiContraryGeocodingQuery error, e:", e);
                    }
                }
            }
            iotDeviceDOList.add(iotDeviceDO);
        }
        if (!iotDeviceDOList.isEmpty()) {
            iotDeviceRepository.saveOrUpdateBatch(iotDeviceDOList);
        }
    }

    @Override
    public void batchSaveOrUpdateHornDevice(List<HornDeviceDO> deviceList) {
        if (deviceList.isEmpty()) {
            return;
        }
        // 是否属地化
        boolean isDependenceFlag = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(PasswordUtil.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                isDependenceFlag = true;
            }
        }
        if(isDependenceFlag){
            log.info("OrderVideoDeviceService batchSaveOrUpdateHornDevice localization release need not add device !");
            return;
        }

        List<HornDeviceDO> hornDeviceList = new ArrayList<>(deviceList.size());
        List<String> deviceCodeList = deviceList.stream().map(HornDeviceDO::getDeviceCode).collect(Collectors.toList());
        List<String> existDeviceCodeList = hornDeviceRepository.selectExistDeviceCodeByCodes(deviceCodeList);
        for (HornDeviceDO hornDeviceDO : deviceList) {
            if (existDeviceCodeList.contains(hornDeviceDO.getDeviceCode())) {
                continue;
            }
            if (StringUtils.isNotBlank(hornDeviceDO.getAdmCode())) {
                //根据经纬度转换国家行政区域
                PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                req.setLocation(String.format(JOIN_COMMA_STR_TEMPLATE, hornDeviceDO.getLongitude(), hornDeviceDO.getLatitude()));
                Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                log.info("batchSaveOrUpdateHornDevice gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
                    try {
                        PoiContraryGeocodingQueryRes data = result.getData().get(0);
                        if (data.getRegion() != null) {
                            Region region = data.getRegion();
                            if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                                hornDeviceDO.setProvinceCode(region.getProvinceCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                                hornDeviceDO.setCityCode(region.getCityCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                                hornDeviceDO.setCountyCode(region.getCountyCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                                hornDeviceDO.setTownCode(region.getTownCode());
                            }
                        }
                    } catch (Exception e) {
                        log.error("batchSaveOrUpdateHornDevice gisMapApi poiContraryGeocodingQuery error, e:", e);
                    }
                }
            }
            hornDeviceList.add(hornDeviceDO);
        }
        if (!hornDeviceList.isEmpty()) {
            hornDeviceRepository.saveOrUpdateBatch(hornDeviceList);
        }
    }

    /**
     * 批量保存或更新设备通道信息
     *
     * @return void
     * @param: channelDTOList
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void batchSaveOrUpdateChannel(List<SaveOrUpdateChannelDTO> channelDTOList) {
        if (!CollectionUtils.isEmpty(channelDTOList)) {
            //新增设备通道列表
            List<ChannelInfoDO> addDeviceDTOList = new ArrayList<>();

            Iterator<SaveOrUpdateChannelDTO> it = channelDTOList.iterator();

            // 通道信息列表
            List<ChannelInfoDO> channelInfoList
                    = channelInfoRepository.getChannelByCodeList(channelDTOList.stream()
                    .map(SaveOrUpdateChannelDTO::getChannelCode).distinct().collect(Collectors.toList()));
            // 通道map key -> 通道编码 value -> 通道信息
            Map<String, ChannelInfoDO> channelInfoMap = channelInfoList.stream().collect(Collectors.toMap(ChannelInfoDO::getChannelCode, (p) -> p, (a,b) -> a));
            while (it.hasNext()) {
                SaveOrUpdateChannelDTO channelDTO = it.next();
                ChannelInfoDO channelInfoDO = channelInfoMap.get(channelDTO.getChannelCode());
                // 2022-10-31跟学斌确认，订购只新增设备，不对已存在的设备进行更改
                if (null == channelInfoDO) {
                    channelInfoDO = new ChannelInfoDO();
                    BeanUtils.copyProperties(channelDTO, channelInfoDO);
                    channelInfoDO.setCreator(SYSTEM_STR);
                    channelInfoDO.setCreatorDate(DateUtils.getNowDate());
                    channelInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                    addDeviceDTOList.add(channelInfoDO);
                }
            }

            if (!addDeviceDTOList.isEmpty()) {
                channelInfoRepository.saveBatch(addDeviceDTOList);
            }
        }
    }

    @Override
    public void batchSaveOrUpdateUavDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList) {
        if (CollectionUtils.isEmpty(deviceDTOList)) {
            return;
        }

        // 是否属地化
        boolean isDependenceFlag = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(PasswordUtil.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                isDependenceFlag = true;
            }
        }
        if(isDependenceFlag){
            log.info("OrderVideoDeviceService batchSaveOrUpdateUavDevice localization release need not add device !");
            return;
        }

        //新增设备列表
        List<UavDeviceDO> addDeviceInfoList = new ArrayList<>();

        // 设备信息列表
        List<UavDeviceDO> deviceInfoList
                = uavDeviceRepository.listDeviceInfoBySet(deviceDTOList.stream()
                .map(SaveOrUpdateDeviceDTO::getDeviceCode).collect(Collectors.toSet()));
        // 设备map key -> 设备编码 value -> 设备信息
        Map<String, UavDeviceDO> deviceInfoMap = deviceInfoList.stream().collect(Collectors.toMap(UavDeviceDO::getDeviceCode, (p) -> p, (a,b) -> a));

        Iterator<SaveOrUpdateDeviceDTO> it = deviceDTOList.iterator();
        Date now = DateUtils.getNowDate();
        while (it.hasNext()) {
            SaveOrUpdateDeviceDTO deviceDTO = it.next();
            //查询设备是否存在
            UavDeviceDO deviceInfoDO = deviceInfoMap.get(deviceDTO.getDeviceCode());
            // 2022-10-31跟学斌确认，订购只新增设备，不对已存在的设备进行更改
            if (null == deviceInfoDO) {
                //根据经纬度转换国家行政区域
                PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                req.setLocation(String.format(JOIN_COMMA_STR_TEMPLATE, deviceDTO.getLongitude(), deviceDTO.getLatitude()));
                Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                log.info("batchSaveOrUpdateUavDevice gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
                    try {
                        PoiContraryGeocodingQueryRes data = result.getData().get(0);
                        if (data.getRegion() != null) {
                            Region region = data.getRegion();
                            if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                                deviceDTO.setProvinceId(region.getProvinceCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                                deviceDTO.setCityId(region.getCityCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                                deviceDTO.setCountyId(region.getCountyCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                                deviceDTO.setTownCode(region.getTownCode());
                            }
                        }
                    } catch (Exception e) {
                        log.error("batchSaveOrUpdateUavDevice gisMapApi poiContraryGeocodingQuery error, e:", e);
                    }
                }

                deviceInfoDO = new UavDeviceDO();
                BeanUtils.copyProperties(deviceDTO, deviceInfoDO);
                if (deviceDTO.getCategoryCode() != null) {
                    deviceInfoDO.setDeviceType(String.valueOf(deviceDTO.getCategoryCode()));
                }
                deviceInfoDO.setAccessNode(deviceDTO.getAccessNode());
                deviceInfoDO.setCreator(SYSTEM_STR);
                deviceInfoDO.setCreateTime(now);
                deviceInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                addDeviceInfoList.add(deviceInfoDO);
            } else {
                log.info("OrderVideoDeviceService 无人机设备已经存在, 编码:{}，不对已有设备进行更改", deviceInfoDO.getDeviceCode());
            }
        }

        if (!addDeviceInfoList.isEmpty()) {
            uavDeviceRepository.saveBatch(addDeviceInfoList);
        }
    }

    @Override
    public void batchSaveOrUpdateUavNestDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList) {
        log.info("batchSaveOrUpdateUavNestDevice size:{}", deviceDTOList.size());
    }

    @Override
    public void batchSaveOrUpdateRecorderDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList) {
        if (CollectionUtils.isEmpty(deviceDTOList)) {
            return;
        }
        // 是否属地化
        boolean isDependenceFlag = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(PasswordUtil.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                isDependenceFlag = true;
            }
        }
        if(isDependenceFlag){
            log.info("OrderVideoDeviceService batchSaveOrUpdateRecorderDevice localization release need not add device !");
            return;
        }

        // 设备信息列表
        List<RecorderDeviceDO> deviceInfoList
                = recorderDeviceRepository.listDeviceInfoBySet(deviceDTOList.stream()
                .map(SaveOrUpdateDeviceDTO::getDeviceCode).collect(Collectors.toSet()));
        // 设备map key -> 设备编码 value -> 设备信息
        Map<String, RecorderDeviceDO> deviceInfoMap = deviceInfoList.stream()
                .collect(Collectors.toMap(RecorderDeviceDO::getDeviceCode, (p) -> p, (a,b) -> a));

        //新增设备列表
        List<RecorderDeviceDO> addDeviceInfoList = new ArrayList<>();
        Iterator<SaveOrUpdateDeviceDTO> it = deviceDTOList.iterator();
        Date now = DateUtils.getNowDate();
        while (it.hasNext()) {
            SaveOrUpdateDeviceDTO deviceDTO = it.next();
            //查询设备是否存在
            RecorderDeviceDO deviceInfoDO = deviceInfoMap.get(deviceDTO.getDeviceCode());
            // 2022-10-31跟学斌确认，订购只新增设备，不对已存在的设备进行更改
            if (null == deviceInfoDO) {

                //根据经纬度转换国家行政区域
                PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                req.setLocation(String.format(JOIN_COMMA_STR_TEMPLATE, deviceDTO.getLongitude(), deviceDTO.getLatitude()));
                Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                log.info("batchSaveOrUpdateRecorderDevice gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
                    try {
                        PoiContraryGeocodingQueryRes data = result.getData().get(0);
                        if (data.getRegion() != null) {
                            Region region = data.getRegion();
                            if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                                deviceDTO.setProvinceId(region.getProvinceCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                                deviceDTO.setCityId(region.getCityCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                                deviceDTO.setCountyId(region.getCountyCode());
                            }
                            if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                                deviceDTO.setTownCode(region.getTownCode());
                            }
                        }
                    } catch (Exception e) {
                        log.error("batchSaveOrUpdateRecorderDevice gisMapApi poiContraryGeocodingQuery error, e:", e);
                    }
                }

                deviceInfoDO = new RecorderDeviceDO();
                BeanUtils.copyProperties(deviceDTO, deviceInfoDO);
//                if (deviceDTO.getCategoryCode() != null) {
//                    deviceInfoDO.setDeviceType(String.valueOf(deviceDTO.getCategoryCode()));
//                }
                deviceInfoDO.setAccessNode(deviceDTO.getAccessNode());
                deviceInfoDO.setCreator(SYSTEM_STR);
                deviceInfoDO.setCreateTime(now);
                deviceInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                addDeviceInfoList.add(deviceInfoDO);
            } else {
                log.info("OrderVideoDeviceService 执法记录仪设备已经存在, 编码:{}，不对已有设备进行更改", deviceInfoDO.getDeviceCode());
            }
        }

        if (!addDeviceInfoList.isEmpty()) {
            recorderDeviceRepository.saveBatch(addDeviceInfoList);
        }
    }

    @Override
    public void batchSaveOrUpdateRecorderChannel(List<SaveOrUpdateChannelDTO> channelDTOList) {
        if (!CollectionUtils.isEmpty(channelDTOList)) {

            // 通道信息列表
            List<RecorderDeviceChannelDO> channelInfoList
                    = recorderChannelRepository.getChannelByCodeList(channelDTOList.stream()
                    .map(SaveOrUpdateChannelDTO::getChannelCode).distinct().collect(Collectors.toList()));
            // 通道map key -> 通道编码 value -> 通道信息
            Map<String, RecorderDeviceChannelDO> channelInfoMap = channelInfoList.stream()
                    .collect(Collectors.toMap(RecorderDeviceChannelDO::getChannelCode, (p) -> p, (a,b) -> a));

            //新增设备通道列表
            List<RecorderDeviceChannelDO> addDeviceDTOList = new ArrayList<>();
            Iterator<SaveOrUpdateChannelDTO> it = channelDTOList.iterator();
            Date now = DateUtils.getNowDate();
            while (it.hasNext()) {
                SaveOrUpdateChannelDTO channelDTO = it.next();
                RecorderDeviceChannelDO channelInfoDO = channelInfoMap.get(channelDTO.getChannelCode());
                // 2022-10-31跟学斌确认，订购只新增设备，不对已存在的设备进行更改
                if (null == channelInfoDO) {
                    channelInfoDO = new RecorderDeviceChannelDO();
                    BeanUtils.copyProperties(channelDTO, channelInfoDO);
                    channelInfoDO.setCreator(SYSTEM_STR);
                    channelInfoDO.setCreateTime(now);
                    channelInfoDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL);
                    addDeviceDTOList.add(channelInfoDO);
                }
            }

            if (!addDeviceDTOList.isEmpty()) {
                recorderChannelRepository.saveBatch(addDeviceDTOList);
            }
        }
    }
}
