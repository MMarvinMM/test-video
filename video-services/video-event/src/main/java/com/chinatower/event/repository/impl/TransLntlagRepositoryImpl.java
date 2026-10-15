package com.chinatower.event.repository.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.chinatower.common.entity.Result;
import com.chinatower.event.utils.AlarmLocationUtils;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.LongLatAngleUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.TranslnglatDTO;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.ITransLntlagRepository;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.vo.SysLntlagConfigVO;
import com.chinatower.event.vo.TranslnglatVO;

import lombok.extern.slf4j.Slf4j;

import static com.chinatower.event.constants.Constants.ALARM_SOURCE_TYPE_CAMERA;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TransLntlagRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 19:41
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class TransLntlagRepositoryImpl implements ITransLntlagRepository {

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;


    private final String TYPE_T = "T";
    private final String MODELID_HIKVISION = "hikvision";
    private final String MODELID_HIKVISION_ANG = "360";
    private final int RADIX = 16;
    /**
     * 查询真实经纬度
     *
     * @param orderId
     * @return
     */
    @Override
    public TranslnglatVO getRelaLntlag(String orderId) {
        try {
            Map<String, String> req = new HashMap<>();
            TranslnglatDTO lnglat = new TranslnglatDTO();

            BeanUtils.copyProperties(lnglat, req);
            String s = "";
//            String s = HttpUtils.postRequest("http://172.17.0.4:19999/GeographyService/GetGeography", null, req);

            log.info("获取告警事件经纬度：返回：{}", s);
        }
        catch (Exception e) {
            log.error("告警定位异常：", e);
        }


//        try {
//            OrderLnglatInfo param = alarmOrderDAO.getLntlatInfoByOrder(orderId, TableUtil.getTableNameByTablePrefixAndId(TableConstant.WARN_ORDER_TEBLE,orderId));
//            //计算经纬度
//            Translnglat lnglat = new Translnglat();
//            if (!StringUtils.isEmpty(param.getPan()) &&
//                    !StringUtils.isEmpty(param.getTilt()) &&
//                    !StringUtils.isEmpty(param.getZoom())) {
//                lnglat.setPan(param.getPan());
//                lnglat.setTilt(param.getTilt());
//                lnglat.setZoom(param.getZoom());
//                List<AlarmOriginalAreaVO> alarmArea = param.getAlarmArea();
//                if (!CollectionUtils.isEmpty(alarmArea)) {
//                    List<AlarmOriginalPointVO> alarmPointList = alarmArea.get(0).getAlarmPointList();
//                    if (!CollectionUtils.isEmpty(alarmPointList)) {
//                        BigDecimal x = new BigDecimal(alarmPointList.get(4).getPercentX()).add(new BigDecimal(alarmPointList.get(3).getPercentX()));
//                        BigDecimal y = new BigDecimal(alarmPointList.get(3).getPercentY()).add(new BigDecimal(alarmPointList.get(4).getPercentY()));
//                        lnglat.setUx(x.divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).toString());
//                        lnglat.setVy(y.divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).toString());
//                        lnglat.setL(new BigDecimal("1").divide(new BigDecimal("2.8"),10,BigDecimal.ROUND_HALF_UP).toString());
//                        lnglat.setF0("1.1");
//                        lnglat.setM("0.1");
//                        lnglat.setN("0.1");
//                        lnglat.setH("10");
//                        lnglat.setP0("0");
//                        lnglat.setT0("0");
//                    } else {
//                        log.info("缺少告警区域坐标点信息，无法获取经纬度定位");
//                    }
//                } else {
//                    log.info("缺少告警区域信息，无法获取经纬度定位");
//                }
//            } else {
//                log.info("缺少ptz信息，无法获取经纬度定位");
//            }
//            //获取真实经纬度
//
//            return new TranslnglatRep.Geography();
//        } catch (Exception e) {
//            log.error("获取真实经纬度异常",e);
//            return null;
//        }
        return null;
    }

    /**
     * 获取真实经纬度
     *
     * @param param
     * @return
     */
    @Override
    public TranslnglatVO getAiRelaLntlag(ReceiveAlarmVO param) {
        try {
            SysLntlagConfigVO config = deviceRepository.getLntlagConfig(param.getDeviceCode());
            if (Objects.isNull(config)) {
                log.error("设备参数信息为空,无法获取经纬度定位");
                return null;
            }
            DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(param.getDeviceCode(),null);
            String height = deviceSiteVO == null ? null : deviceSiteVO.getHeight();
            if (StringUtils.isEmpty(height)) {
                log.error("设备高度为空,无法获取经纬度定位");
                return null;
            }
            if (StringUtils.isEmpty(param.getLatitude()) || StringUtils.isEmpty(param.getLongitude())) {
                log.error("告警源经纬度为空，无法获取经纬度定位");
                return null;
            }
            //计算经纬度  宇视设备Z值可以为0
            TranslnglatDTO lnglat = new TranslnglatDTO();
            if (!StringUtils.isEmpty(param.getPan()) && !StringUtils.isEmpty(param.getTilt()) &&
                    !StringUtils.isEmpty(param.getZoom()) &&
                            ( (!Constants.VALID_STATE.equals(param.getZoom()) && !Constants.ZERO_STRING.equals(param.getZoom())) || "uniview".equals(config.getModelId()))) {
                DeviceModelVO deviceModel = new DeviceModelVO();
                deviceModel.setMfgrCode(config.getModelId());
                deviceModel.setFocusRange(config.getFocusRange());
                deviceModel.setVertiDownNeg(config.getVertiDownNeg());
                lnglat.setPan(LongLatAngleUtils.paramTrans(param.getPan(), deviceModel, LongLatAngleUtils.TYPE_P));
                lnglat.setTilt(LongLatAngleUtils.paramTrans(param.getTilt(), deviceModel, LongLatAngleUtils.TYPE_T));
                lnglat.setZoom(LongLatAngleUtils.paramTrans(param.getZoom(), deviceModel, LongLatAngleUtils.TYPE_Z));
                BigDecimal x = new BigDecimal("1");
                BigDecimal y = new BigDecimal("1");
                List<AlarmOriginalAreaVO> alarmArea = param.getAlarmArea();
                if (!CollectionUtils.isEmpty(alarmArea)) {
                    String confidenceLevel = "0";
                    List<AlarmOriginalPointVO> alarmPointList = new ArrayList<>();
                    for(AlarmOriginalAreaVO alarmTemp:alarmArea){
                        if(alarmTemp.getConfidenceLevel()!=null && Double.valueOf(alarmTemp.getConfidenceLevel())>Double.valueOf(confidenceLevel)){
                            confidenceLevel = alarmTemp.getConfidenceLevel();
                            alarmPointList = alarmTemp.getAlarmPointList();
                        }
                    }
//                    List<AlarmOriginalPointVO> alarmPointList = alarmArea.get(0).getAlarmPointList();
                    if (!CollectionUtils.isEmpty(alarmPointList) && alarmPointList.size()> 3) {
                        x = new BigDecimal(alarmPointList.get(1).getPercentX()).add(new BigDecimal(alarmPointList.get(2).getPercentX()));
                        y = new BigDecimal(alarmPointList.get(1).getPercentY()).add(new BigDecimal(alarmPointList.get(2).getPercentY()));
                    }
                    // 后端告警
                    lnglat.setUx(x.divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).toString());
                    lnglat.setVy(y.divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).toString());
                }else{
                    lnglat.setUx("0.50");
                    lnglat.setVy("0.50");
                }

                lnglat.setC(config.getC());
                lnglat.setD(config.getD());
                lnglat.setF0(config.getF0());
                lnglat.setM(config.getM());
                lnglat.setN(config.getN());
                lnglat.setH(height);
//                lnglat.setP0(config.getP0());
//                lnglat.setT0(config.getT0());
                lnglat.setLatitude(param.getLatitude());
                lnglat.setLongitude(param.getLongitude());

                // 处理水平角偏差和俯仰角偏差   通过有效参考点进行计算
                List<DeviceRefPointVO> pointsValid = deviceRepository.getDevicePointsValid(param.getDeviceCode());
                DeviceModelVO deviceModelVO = new DeviceModelVO();
                deviceModelVO.setRealPan(lnglat.getPan());
                Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.SYS_DEVICE_HORIZONTAL);
                List<SysDictDataResponse> enumList = res.getData();
                List<SysDictData> enumListNew = new ArrayList<>();
                enumList.forEach(item ->{
                    SysDictData sysDictData = new SysDictData();
                    BeanUtils.copyProperties(item,sysDictData);
                    enumListNew.add(sysDictData);
                });
                deviceModelVO = LongLatAngleUtils.getValidOffset(pointsValid, deviceModelVO,enumListNew);
                lnglat.setP0(deviceModelVO.getP0() == null ? null : deviceModelVO.getP0().toPlainString());
                lnglat.setT0(deviceModelVO.getT0() == null ? null : deviceModelVO.getT0().toPlainString());
            }
            else {
                log.info("缺少ptz信息，无法获取经纬度定位");
                // 未获取到PTZ信息，则使用站址经纬度

                if(deviceSiteVO!=null){
                    TranslnglatVO translnglatRep = new TranslnglatVO();
                    translnglatRep.setLongitude(deviceSiteVO.getLongitude());
                    translnglatRep.setLatitude(deviceSiteVO.getLatitude());
                    log.info("缺少ptz信息，使用站址经纬度作为实际经纬度");
                    return translnglatRep;
                }
            }
            //获取真实经纬度
            log.info("获取告警事件经纬度：入参：{}，告警编码：{}", JSONObject.toJSONString(lnglat), param.getAlarmCode());
//            String result = LatLntJNI.GetGeography(JSONObject.toJSONString(lnglat));
            TranslnglatVO translnglatRep = AlarmLocationUtils.getAlarmLocation(lnglat);
            log.info("获取告警事件经纬度：返回：{}", translnglatRep.toString());
            if (!Objects.isNull(translnglatRep)) {
//                TranslnglatVO translnglatRep = JSONObject.parseObject(result, TranslnglatVO.class);
                    translnglatRep = deviceRepository.handlerFeaturePT(translnglatRep,param.getDeviceCode(),param.getAlarmCode());
                    param.setFeaturePan(translnglatRep.getAlarmP());
                    param.setFeatureTilt(translnglatRep.getAlarmT());
                    param.setDevicePitch(translnglatRep.getDevicePitch());
                    return translnglatRep;
            }
            return null;
        }
        catch (Exception e) {
            log.error("获取真实经纬度异常", e);
            return null;
        }
        catch (UnsatisfiedLinkError e) {
            log.error("获取真实经纬度错误-----UnsatisfiedLinkError", e);
            return null;
        }
        catch (Error error) {
            log.error("获取真实经纬度错误", error);
            return null;
        }
    }
}
