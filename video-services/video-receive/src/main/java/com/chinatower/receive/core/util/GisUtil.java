package com.chinatower.receive.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.receive.vo.entity.DeviceSiteReceiveVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Description Gis服务公共方法
 * @Author guyuefei
 * @Date 2022/7/18
 * @Version release_v1.5
 **/
@Component("gisUtil")
@Slf4j
public class GisUtil {

    @Autowired
    private GisMapApi gisMapApi;

    /**
     * 调用高德API 通过经纬度返回地址
     * @param receiveAlarmVO
     */
    public ReceiveAlarmVO getAddressFromAmap(ReceiveAlarmVO receiveAlarmVO, DeviceSiteReceiveVO deviceSiteReceiveVO){
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", receiveAlarmVO.getLongitude(), receiveAlarmVO.getLatitude()));
        try {
            log.info("逆地理编码查询接口poiContraryGeocodingQuery req:{}" + JSONObject.toJSONString(req));
            Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
            log.info("逆地理编码查询接口poiContraryGeocodingQuery resp:{}" + JSONObject.toJSONString(result));
            if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                try {
                    PoiContraryGeocodingQueryRes data = result.getData().get(0);
                    //AI 摄像机 一键未开启告警定位取摄像机地址为事发地点，其余按逆编码逻辑获取事发地点
                    if((deviceSiteReceiveVO == null && !Constants.WARNING_SOURCE_8.equals(receiveAlarmVO.getAlarmSource())) ||
                            (deviceSiteReceiveVO !=null && "1".equals(deviceSiteReceiveVO.getAlarmLocation()) && (Constants.ONE.equals(receiveAlarmVO.getAlarmSource()) || Constants.FOUR.equals(receiveAlarmVO.getAlarmSource()) || Constants.THREE.equals(receiveAlarmVO.getAlarmSource())))){
                        receiveAlarmVO.setAddress(data.getAddress());
                    }

                    if (data.getRegion() != null) {
                        Region region = data.getRegion();
                        if (!StringUtils.isEmpty(region.getTownCode())) {
                            receiveAlarmVO.setTownCode(region.getTownCode());
                        }
                        if (!StringUtils.isEmpty(region.getTown())) {
                            receiveAlarmVO.setTownName(region.getTown());
                        }
                        // 按照经纬度更新事发地点后，对应省市区编码同步更新，确保省市区乡镇可以对应的上
                        if (!StringUtils.isEmpty(region.getCityCode())) {
                            receiveAlarmVO.setCityCode(region.getCityCode());
                            receiveAlarmVO.setCityName(region.getCity());
                        }
                        if (!StringUtils.isEmpty(region.getCountyCode())) {
                            receiveAlarmVO.setCountyCode(region.getCountyCode());
                            receiveAlarmVO.setCountyName(region.getCounty());
                        }
                        if (!StringUtils.isEmpty(region.getProvinceCode())) {
                            receiveAlarmVO.setProvinceCode(region.getProvinceCode());
                            receiveAlarmVO.setProvinceName(region.getProvince());
                        }

                    }
                } catch (Exception e) {
                    log.error("GisUtil.getAddressFromAmap gisMapApi poiContraryGeocodingQuery error, e:", e);
                }
            }
            log.info("AI上报通过经纬度返回地址：{};poiContraryGeocodingQuery接口返回：{}", JSON.toJSONString(receiveAlarmVO), JSON.toJSONString(result));
        }
        catch (Exception e) {
            log.error("通过经纬度获取位置信息错误!", e);
        }
        return receiveAlarmVO;
    }
}
