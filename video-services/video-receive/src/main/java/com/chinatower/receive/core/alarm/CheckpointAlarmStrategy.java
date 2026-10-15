package com.chinatower.receive.core.alarm;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.core.util.AlarmStrategyEnum;
import com.chinatower.receive.mapper.CheckpointDeviceAlarmMapper;
import com.chinatower.receive.mapper.CheckpointDeviceMapper;
import com.chinatower.receive.util.IPKUtils;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.CheckpointAlarmOriginalAreaVO;
import com.chinatower.receive.vo.entity.CheckpointAlarmOriginalPointVO;
import com.chinatower.receive.vo.entity.RadarParam;
import com.chinatower.receive.vo.entity.ReceiveCheckpointAlarmVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡口告警接收策略实现类
 * @Author: songxinfeng
 * @Date: 2024/8/6 10:26
 * @version release_v1.6.90
 */

@Component("checkpointAlarmStrategy")
@Slf4j
public class CheckpointAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private CheckpointDeviceAlarmMapper checkpointDeviceAlarmMapper;

    @Autowired
    private CheckpointDeviceMapper checkpointDeviceMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return AlarmStrategyEnum.VEHICLE_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }

    /**
     * 处理卡口告警上报
     * @param receiveAlarmDTO 包含卡口告警数据的传输对象
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("卡口告警上报入参：{}", JSON.toJSONString(receiveAlarmDTO));
        ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
        try {
            // 根据车辆卡口摄像机告警中的设备编码+通道编码查询已配置的有效卡口设备信息
            CheckpointDeviceInfoVO vo = new CheckpointDeviceInfoVO();
            vo.setDeviceCode(receiveAlarmVO.getDeviceCode());
            vo.setChannelCode(receiveAlarmVO.getChannelCode());
            List<CheckpointDeviceInfoVO> checkpointDeviceInfoList = checkpointDeviceMapper.getCheckpointDeviceList(vo);
            // 如果未匹配到已配置且订购的卡口设备，则记录错误日志，不保存告警信息
            if (CollectionUtils.isEmpty(checkpointDeviceInfoList)) {
                log.error(MessageConstants.NO_CUSTOMER_MESSAGE);
                return ;
            }

            // 处理并保存告警信息，包括区域和点位
            List<ReceiveCheckpointAlarmVO> list = new ArrayList<>();
            List<CheckpointAlarmOriginalAreaVO> alarmAreas = new ArrayList<>();
            List<CheckpointAlarmOriginalPointVO> alarmPointLists = new ArrayList<>();

            ReceiveCheckpointAlarmVO baseAlarmVO = createBaseAlarmVO(receiveAlarmVO);

            for (CheckpointDeviceInfoVO checkpointDeviceInfo : checkpointDeviceInfoList) {
                ReceiveCheckpointAlarmVO alarmVO = createAlarmVO(baseAlarmVO, checkpointDeviceInfo);
                list.add(alarmVO);

                // 告警信息中报警区域不为空
                List<AlarmOriginalAreaVO> alarmAreaList = receiveAlarmVO.getAlarmArea();
                if (!CollectionUtils.isEmpty(alarmAreaList)) {
                    processAlarmAreas(alarmAreaList, alarmVO.getWarningOrderId(), alarmAreas, alarmPointLists);
                }
            }

            if (!CollectionUtils.isEmpty(list)) {
                checkpointDeviceAlarmMapper.insertCheckpointDeviceAlarmInfo(list);
            }
            if (!CollectionUtils.isEmpty(alarmAreas)) {
                checkpointDeviceAlarmMapper.insertOriginalAreas(alarmAreas);
            }
            if (!CollectionUtils.isEmpty(alarmPointLists)) {
                checkpointDeviceAlarmMapper.insertOriginalPoints(alarmPointLists);
            }
        } catch(Exception e) {
            log.info("卡口告警上报异常：{}", e);
        }
    }

    @Override
    public List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        return null;
    }

    @Override
    public void getAddress(ReceiveAlarmVO receiveAlarmVO) {
        log.info("该策略无需重写此方法");
    }

    private ReceiveCheckpointAlarmVO createBaseAlarmVO(ReceiveAlarmVO receiveAlarmVO) {
        ReceiveCheckpointAlarmVO receiveCheckpointAlarmBaseVO = new ReceiveCheckpointAlarmVO();
        BeanUtils.copyProperties(receiveAlarmVO, receiveCheckpointAlarmBaseVO);
        // 获取告警信息中的卡口设备拓展字段
        RadarParam eExtendField = JSON.parseObject(String.valueOf(receiveAlarmVO.getExtendField()), RadarParam.class);
        if (!StringUtils.isEmpty(eExtendField)) {
            receiveCheckpointAlarmBaseVO.setVehicleColor(eExtendField.getVehicleColor());
            receiveCheckpointAlarmBaseVO.setVehicleNumColor(eExtendField.getVehicleNumColor());
            receiveCheckpointAlarmBaseVO.setVehicleType(eExtendField.getVehicleType());
            receiveCheckpointAlarmBaseVO.setVehicleNumber(eExtendField.getVehicleNumber());

            String vehicleLitimg = eExtendField.getVehicleLitimg();
            if (!StringUtils.isEmpty(vehicleLitimg)) {
                String newUrl = getUrl(receiveAlarmVO.getAccessNode(), vehicleLitimg);
                if (StringUtils.isEmpty(receiveCheckpointAlarmBaseVO.getOriginalPictureUrl())) {
                    receiveCheckpointAlarmBaseVO.setOriginalPictureUrl(newUrl);
                } else {
                    receiveCheckpointAlarmBaseVO.setOriginalPictureUrl(receiveCheckpointAlarmBaseVO.getOriginalPictureUrl()+ Constants.COMMA + newUrl);
                }
            }
        }
        receiveCheckpointAlarmBaseVO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
        return receiveCheckpointAlarmBaseVO;
    }

    private ReceiveCheckpointAlarmVO createAlarmVO(ReceiveCheckpointAlarmVO baseAlarmVO, CheckpointDeviceInfoVO checkpointDeviceInfo) {
        ReceiveCheckpointAlarmVO receiveCheckpointAlarmVO = new ReceiveCheckpointAlarmVO();
        BeanUtils.copyProperties(baseAlarmVO, receiveCheckpointAlarmVO);

        String warningOrderId = IPKUtils.getWarnOrderId(checkpointDeviceInfo.getTenantId(),receiveCheckpointAlarmVO.getAlarmTime());
        receiveCheckpointAlarmVO.setWarningOrderId(warningOrderId);
        receiveCheckpointAlarmVO.setIndustryCode(checkpointDeviceInfo.getIndustryCode());
        receiveCheckpointAlarmVO.setAppVerCode(checkpointDeviceInfo.getAppVerCode());
        receiveCheckpointAlarmVO.setTenantId(checkpointDeviceInfo.getTenantId());
        receiveCheckpointAlarmVO.setTenantName(checkpointDeviceInfo.getTenantName());
        receiveCheckpointAlarmVO.setProvinceCode(checkpointDeviceInfo.getProvinceId());
        receiveCheckpointAlarmVO.setProvinceName(checkpointDeviceInfo.getProvinceName());
        receiveCheckpointAlarmVO.setCityCode(checkpointDeviceInfo.getCityId());
        receiveCheckpointAlarmVO.setCityName(checkpointDeviceInfo.getCityName());
        receiveCheckpointAlarmVO.setCountyCode(checkpointDeviceInfo.getCountyId());
        receiveCheckpointAlarmVO.setCountyName(checkpointDeviceInfo.getCountyName());
        return receiveCheckpointAlarmVO;
    }

    private void processAlarmAreas(List<AlarmOriginalAreaVO> alarmAreaList, String warningOrderId,
                                   List<CheckpointAlarmOriginalAreaVO> alarmAreas, List<CheckpointAlarmOriginalPointVO> alarmPointLists) {
        for (AlarmOriginalAreaVO area : alarmAreaList) {
            CheckpointAlarmOriginalAreaVO areaVO = new CheckpointAlarmOriginalAreaVO();
            BeanUtils.copyProperties(area, areaVO);
            areaVO.setWarningOrderId(warningOrderId);
            alarmAreas.add(areaVO);

            List<AlarmOriginalPointVO> alarmPointList = area.getAlarmPointList();
            // 告警信息中告警点列表不为空
            if (!CollectionUtils.isEmpty(alarmPointList)) {
                for (AlarmOriginalPointVO point : alarmPointList) {
                    CheckpointAlarmOriginalPointVO pointVO = new CheckpointAlarmOriginalPointVO();
                    BeanUtils.copyProperties(point, pointVO);
                    pointVO.setWarningOrderId(warningOrderId);
                    pointVO.setAlarmAreaId(area.getAlarmAreaId());
                    alarmPointLists.add(pointVO);
                }
            }
        }
    }

    public String getUrl(String accessNode,String url){
        //中心节点文件地址无需拼接属地编码
        if("10000000001".equals(accessNode)){
            return url;
        }
        //纯属化不拼接节点编码
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey("dependence_manage");
        AjaxResult result1 = systemFeignClient.getConfigInfo(sysConfig);
        log.info("获取当前环境字段返回:{}",JSON.toJSONString(result1));
        if(result1 != null && 200 == Integer.valueOf(String.valueOf(result1.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result1.get("data")), SysConfig.class);
            if (sysConfigInfo != null && com.chinatower.event.constants.Constants.ZERO.equals(sysConfigInfo.getConfigValue())) {
                return url;
            }
        }
        if(StringUtils.isEmpty(url)){
            return url;
        }
        String[] urls = url.split(com.chinatower.event.constants.Constants.COMMA);
        String resUrl="";
        for (String realUrl:urls) {
            if(realUrl.contains("http") || realUrl.contains("https")){
                //将属地编码拼接在域名之后
                int thirdSlashIndex = realUrl.indexOf('/', realUrl.indexOf('/', realUrl.indexOf('/') + 1) + 1);
                // 拼接字符串，在第三个斜杠之后
                StringBuilder result = new StringBuilder(realUrl.substring(0, thirdSlashIndex + 1));
                result.append(accessNode+"/");
                result.append(realUrl.substring(thirdSlashIndex + 1));
                realUrl = result.toString();
            }else{
                realUrl = "/"+accessNode+realUrl;
            }
            if(StringUtils.isEmpty(resUrl)){
                resUrl = realUrl;
            }else{
                resUrl += com.chinatower.event.constants.Constants.COMMA+realUrl;
            }
        }
        return resUrl;
    }

    @Override
    public String getStrategyName() {
        return AlarmStrategyEnum.VEHICLE_ALARM.getAlarmStrategyBean();
    }
}
