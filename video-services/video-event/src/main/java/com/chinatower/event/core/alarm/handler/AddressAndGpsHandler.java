package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.core.alarm.util.GisUtil;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;



/**
 * @Description 处理地址和经纬度信息
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
@Component("addressAndGpsHandler")
@Scope("prototype")
@Slf4j
public class AddressAndGpsHandler extends AbstractHandler{

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
        log.info("处理地址和经纬度数据信息入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();

            //告警事件坐标（目前为设备坐标）为空，则取站址的坐标
            String latitude = receiveAlarmVO.getLatitude();
            String longitude = receiveAlarmVO.getLongitude();
            String deviceCode = receiveAlarmVO.getDeviceCode();
            String channelCode = receiveAlarmVO.getChannelCode();
            // 摄像机告警定位配置判断
            DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(deviceCode,channelCode);
            // 去除上报过来得经纬度，直接取摄像机的经纬度进行告警定位计算
            if (deviceSiteVO != null) {
                latitude = deviceSiteVO.getLatitude();
                longitude = deviceSiteVO.getLongitude();
                receiveAlarmVO.setAddress(deviceSiteVO.getLocation());
                // 替换摄像机名称
                receiveAlarmVO.setDeviceName(deviceSiteVO.getDeviceName());

                // 覆盖上报来的通道名称
                if(!StringUtils.isEmpty(deviceSiteVO.getChannelName())){
                    receiveAlarmVO.setChannelName(deviceSiteVO.getChannelName());
                }

            } /*else if(!StringUtils.isEmpty(latitude) && !StringUtils.isEmpty(longitude) && !"null".equals(latitude) && !"null".equals(longitude)){
                // 2022-01-10 调用高德API 通过经纬度返回地址
                //gisUtil.getAddressFromAmap(receiveAlarmVO);

                //通过经纬度调用高德API得到火星坐标系——GCJ-02
                try {
                    GPS gps = GPSConverterUtils.gps84_To_Gcj02(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    if(gps == null){
                        log.info("通过经纬度调用高德API得到火星坐标系返回空指针,告警编码{}",receiveAlarmVO.getAlarmCode());
                    }else{
                        longitude = String.valueOf(gps.getLon());
                        latitude = String.valueOf(gps.getLat());
                    }
                }
                catch (Exception e) {
                    log.error(MessageConstants.AMP_COORDINATE_CONVERT_MESSAGE, e);
                }
            }*/
            receiveAlarmVO.setLatitude(latitude);
            receiveAlarmVO.setLongitude(longitude);

            //设置告警点相关信息
            warningOrderUtil.setAlarmLocation(deviceSiteVO, receiveAlarmVO);

            gisUtil.getAddressFromAmap(receiveAlarmVO,deviceSiteVO);
            log.info("通过经纬度获取省市区乡镇编码:" + JSONObject.toJSONString(receiveAlarmVO));

            // 根据经纬度获取网格信息
            warningOrderUtil.setAlarmGridInfo(receiveAlarmDTO);
        }catch(Exception e){
            log.error("处理地址和经纬度数据信息异常：{}", e);
        }
        return this.excuteNextHandler(receiveAlarmDTO);
    }
}
