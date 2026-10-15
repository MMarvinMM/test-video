package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.ITowerDeviceService;
import com.chinatower.device.entity.vo.IotTTDeviceVO;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.util.GisUtil;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.ITransLntlagRepository;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 物联设备告警批量处理地址和经纬度信息
 * @Author luo.zhenghong
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Component
@Slf4j
@Scope("prototype")
public class IotAddressAndGpsHandler extends AbstractHandler {

    @Autowired
    private GisUtil gisUtil;
    @Autowired
    private IDeviceRepository deviceRepository;
    @Autowired
    private ITransLntlagRepository transLntlagRepository;
    @Autowired
    private GridAPI gridAPI;
    @Autowired
    private ITowerDeviceService iTowerDeviceService;

    /**
     * 执行逻辑
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("物联设备告警批量处理地址和经纬度信息入参：{}", receiveAlarmDTO.toString());
        long beginTime = System.currentTimeMillis();
        List<ReceiveAlarmVO> list = receiveAlarmDTO.getList();
        List<String> deviceCodeList = list.stream().map(ReceiveAlarmVO::getDeviceCode).distinct().collect(Collectors.toList());
        List<ReceiveAlarmVO> receiveAlarmVOList = new ArrayList<>();
        // 摄像机告警定位配置判断
        List<DeviceSiteVO> deviceSiteVOList = deviceRepository.queryIotDeviceList(deviceCodeList);
        if (!CollectionUtils.isEmpty(deviceSiteVOList)) {
            deviceSiteVOList.stream().distinct().forEach(deviceSiteVO -> {
                ReceiveAlarmVO addressAlarmVO = new ReceiveAlarmVO();
                addressAlarmVO.setLongitude(deviceSiteVO.getLongitude());
                addressAlarmVO.setLatitude(deviceSiteVO.getLatitude());
                //V1.6.80 告警的事发地点默认为告警传感器在【铁塔设备管理-物联设备管理】中的“设备地址”
                IotTTDeviceVO iotTTDeviceVO = new IotTTDeviceVO();
                iotTTDeviceVO.setDeviceCode(deviceSiteVO.getDeviceCode());
                try {
                    gisUtil.getAddressFromAmap(addressAlarmVO,null);
                    TableDataInfo data = iTowerDeviceService.queryIotDeviceListPage(iotTTDeviceVO);
                    if(!data.getRows().isEmpty()){
                        List<Map> iotList = JSONObject.parseArray(JSON.toJSONString(data.getRows()), Map.class);
                        addressAlarmVO.setAddress(iotList.get(0).get(Constants.PARAM_LOCATION_KEY).toString());
                    }
                    List<ReceiveAlarmVO> deviceAlarmCodeList = list.stream().filter(receiveAlarmVO -> Objects.equals(deviceSiteVO.getDeviceCode(), receiveAlarmVO.getDeviceCode())).map(receiveAlarmVO -> this.buildDeviceInfo(deviceSiteVO, receiveAlarmVO, addressAlarmVO)).collect(Collectors.toList());
                    receiveAlarmVOList.addAll(deviceAlarmCodeList);
                    //组装网格信息
                    this.buildGridInfo(deviceAlarmCodeList, deviceSiteVO.getLatitude(), deviceSiteVO.getLongitude());
                } catch (Exception e) {
                    log.error("根据经纬度查询位置报错", e);
                }
            });
            receiveAlarmDTO.setList(receiveAlarmVOList);
        }

        long endTime = System.currentTimeMillis();
        log.info("IotAddressAndGpsHandler excute cost: {}", (endTime - beginTime));

        return this.excuteNextHandler(receiveAlarmDTO);
    }

    /**
     * 组装设备信息
     * @param deviceSiteVO
     * @param receiveAlarmVO
     */
    private ReceiveAlarmVO buildDeviceInfo(DeviceSiteVO deviceSiteVO, ReceiveAlarmVO receiveAlarmVO, ReceiveAlarmVO addressAlarmVO) {
        log.info("物联设备告警批量处理地址和经纬度信息入参：{}，{}，{}", deviceSiteVO.toString(),receiveAlarmVO.toString(),addressAlarmVO.toString());
        // 获取摄像头区域
        receiveAlarmVO.setProvinceCode(deviceSiteVO.getProvinceId());
        receiveAlarmVO.setCityCode(deviceSiteVO.getCityId());
        receiveAlarmVO.setCountyCode(deviceSiteVO.getCountyId());
        receiveAlarmVO.setProvinceName(deviceSiteVO.getProvinceName());
        receiveAlarmVO.setCityName(deviceSiteVO.getCityName());
        receiveAlarmVO.setCountyName(deviceSiteVO.getCountyName());
        receiveAlarmVO.setLatitude(deviceSiteVO.getLatitude());
        receiveAlarmVO.setLongitude(deviceSiteVO.getLongitude());
        receiveAlarmVO.setDeviceCode(deviceSiteVO.getDeviceCode());
        receiveAlarmVO.setDeviceName(deviceSiteVO.getDeviceName());
        receiveAlarmVO.setTownCode(addressAlarmVO.getTownCode());
        receiveAlarmVO.setTownName(addressAlarmVO.getTownName());
        receiveAlarmVO.setAddress(addressAlarmVO.getAddress());
        //物联告警暂不支持告警定位
        /*if("1".equals(deviceSiteVO.getAlarmLocation())) {
            //告警定位
            TranslnglatVO aiRelaLntlag = transLntlagRepository.getAiRelaLntlag(receiveAlarmVO);
            if (Objects.nonNull(aiRelaLntlag)
                    && !"0".equals(aiRelaLntlag.getLatitude())
                    && !"0".equals(aiRelaLntlag.getLongitude())) {
                receiveAlarmVO.setLatitude(aiRelaLntlag.getLatitude());
                receiveAlarmVO.setLongitude(aiRelaLntlag.getLongitude());
                String flagT = aiRelaLntlag.getFlatA();
                if (org.apache.commons.lang3.StringUtils.isNotBlank(flagT)) {
                    flagT = new BigDecimal(flagT).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
                }
                receiveAlarmVO.setHoriAzimuthAngle(flagT);
            }
        }*/
        return receiveAlarmVO;
    }

    /**
     * 组装网格信息
     * @param receiveAlarmVOList
     * @param latitude
     * @param longitude
     */
    private void buildGridInfo(List<ReceiveAlarmVO> receiveAlarmVOList, String latitude, String longitude) {
        log.info("组装网格信息入参：{}", JSON.toJSONString(receiveAlarmVOList));
        try{
            // 根据经纬度获取网格信息
            if (!StringUtils.isEmpty(latitude) && !StringUtils.isEmpty(longitude)) {
                try {
                    GridByLngLatParam param = new GridByLngLatParam();
                    param.setLatitude(latitude);
                    param.setLongitude(longitude);
                    List<BaseIndustryEntity> tenantList = new ArrayList<>();
                    receiveAlarmVOList.forEach(item ->{
                        BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
                        baseIndustryEntity.setIndustryCode(item.getIndustryCode());
                        baseIndustryEntity.setTenantId(item.getTenantId());
                        tenantList.add(baseIndustryEntity);
                    });
                    param.setTenantList(tenantList);
                    Result<List<GridByLngLat>> result = gridAPI.getMessageByLngLatNew(param);

                    log.info(MessageFormat.format(MessageConstants.GRID_MESSAGE, result));
                    if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                        List<GridByLngLat> gridList = result.getData();
                        if (!CollectionUtils.isEmpty(gridList)) {
                            for (ReceiveAlarmVO receiveAlarmVO : receiveAlarmVOList) {
                                GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(receiveAlarmVO.getTenantId(), grid.getTenantId()) && Objects.equals(receiveAlarmVO.getIndustryCode(), grid.getIndustryCode()))
                                        .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                                receiveAlarmVO.setGridId(gridInfo.getGridId());
                                receiveAlarmVO.setGridName(gridInfo.getGridName());
                                receiveAlarmVO.setGridUserList(gridInfo.getUserList());
                            }
                        }
                    }
                }
                catch (Exception e) {
                    log.error(MessageConstants.GRID_ERROR_MESSAGE, e);
                }
            }
        }catch(Exception e){
            log.info("组装网格信息异常：{}", e);
        }

    }
}
