package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.core.alarm.util.GisUtil;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.event.vo.RadarParam;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.constants.OrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @Description 处理雷达地址和经纬度信息
 * @Author luo.zhenghong
 * @Date 2022/8/10
 * @Version release_v1.5
 **/
@Component("radarAddressAndGpsHandler")
@Scope("prototype")
@Slf4j
public class RadarAddressAndGpsHandler extends AbstractHandler{

    @Autowired
    private GisUtil gisUtil;
    @Autowired
    private IDeviceRepository deviceRepository;


    @Autowired
    private WarningOrderUtil warningOrderUtil;

    /**
     * 执行逻辑
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("处理雷达地址和经纬度信息入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();

            //告警事件坐标（目前为设备坐标）为空，则取站址的坐标
            String latitude = receiveAlarmVO.getLatitude();
            String longitude = receiveAlarmVO.getLongitude();
            //String deviceCode = receiveAlarmVO.getDeviceCode();
            RadarParam extraField = JSON.parseObject(String.valueOf(receiveAlarmVO.getExtraField()), RadarParam.class);
            // 获取雷达设备信息   ---取第一个雷达设备的经纬度来当做事发地点的经纬度
            DeviceSiteVO deviceSiteVO = deviceRepository.querySensorDevice(extraField.getRadarCodes()[0], OrderConstants.ORDER_RADAR_CODE);
            if (StringUtils.isEmpty(latitude) || StringUtils.isEmpty(longitude) || "null".equals(latitude) || "null".equals(longitude)) {
                if (deviceSiteVO != null) {
                    receiveAlarmVO.setLatitude(deviceSiteVO.getLatitude());
                    receiveAlarmVO.setLongitude(deviceSiteVO.getLongitude());
                    receiveAlarmVO.setAddress(deviceSiteVO.getLocation());
                }
            }

            //设置告警点相关信息
            warningOrderUtil.setAlarmLocation(deviceSiteVO, receiveAlarmVO);
            //设置乡镇信息
            gisUtil.getAddressFromAmap(receiveAlarmVO,null);
            log.info("通过经纬度获取省市区乡镇编码:" + JSONObject.toJSONString(receiveAlarmVO));
            //设置告警网格信息
            warningOrderUtil.setAlarmGridInfo(receiveAlarmDTO);
        }catch(Exception e){
            log.info("处理雷达地址和经纬度信息异常：{}", e);
        }
        return this.excuteNextHandler(receiveAlarmDTO);
    }
}
