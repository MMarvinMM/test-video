package com.chinatower.receive.core.alarm.common;

import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.receive.core.util.AlarmStrategy;
import com.chinatower.receive.core.util.GisUtil;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.ISuppressStrategyService;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.DeviceSiteReceiveVO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 告警接收核心策略类
 * @Author: songxinfeng
 * @Date: 2024/8/6 9:49
 * @version release_v1.6.90
 */
@Component("alarmStrategy")
@Slf4j
public abstract class AlarmBaseStategy{

    @Autowired
    private ISuppressStrategyService strategyService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private GisUtil gisUtil;
    /**
     * 根据条件判断告警类型
     * @param receiveAlarmVO
     * @return
     */
    public abstract boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO);

    /**
     * 告警上报
     * @param receiveAlarmDTO
     * @throws Exception
     */
    public abstract void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception;

    /**
     * 获取告警的租户信息，该策略方法用于AI/摄像机/雷达告警上报
     * @param receiveAlarmVO
     * @return
     * @throws Exception
     */
    public abstract List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception;

    /**
     * 告警的地址和经纬度数据处理，该策略方法用于AI/摄像机/雷达/卫星告警上报
     * @param receiveAlarmVO
     */
    public abstract void getAddress(ReceiveAlarmVO receiveAlarmVO);


    /**
     * 获取处理策略名称
     * @return
     */
    public abstract String getStrategyName();

    /**
     * 告警压制
     * @param alarmVO
     * @param isIntercept 压制开关是否开启标识
     * @param strategyMap 告警压制策略配置信息
     * @param configDTOMap 雨天告警压制配置数据
     * @param intercepts  告警压制信息集合
     */
    public boolean judgeIntercept(ReceiveAlarmVO alarmVO, String isIntercept, Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap,
                                  Map<String, AlarmSuppressConfigDTO> configDTOMap, List<ReceiveAlarmVO> intercepts) {

        if (Constants.ONE.equals(isIntercept) && MapUtils.isNotEmpty(strategyMap)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyTenantList = strategyMap.get(alarmVO.getTenantId()+alarmVO.getIndustryCode());
            if (strategyTenantList != null && !strategyTenantList.isEmpty()) {  //没有查询到匹配得告警压制策略
                boolean interceptFlag = strategyService.doStrategyIntercept(alarmVO, strategyTenantList);
                if (interceptFlag) {
                    // 压制成功，放入压制集合
                    intercepts.add(alarmVO);
                    return true;
                }
            }
        }
        String tenantId = alarmVO.getTenantId();
        String deviceCode = alarmVO.getDeviceCode();
        String mapKey = tenantId + "_" + deviceCode;
        String alarmTime = alarmVO.getAlarmTime();

        //雨天模式告警规则生效则进行压制
        log.info("雨天压制获取到的配置信息：{}", configDTOMap);
        if (doIntercept(configDTOMap, mapKey, DateUtils.string2Date(alarmTime))) {
            alarmVO.setIntercept(AlgorithmEventVO.RAINY_ALGORITHM);
            intercepts.add(alarmVO);
            return true;
        }

        //获取研判压制,压制规则保存到redis中,redis中有数据时进行压制
        String key = Constants.INTERCEPT_CONFIG + tenantId + "_" + alarmVO.getIndustryCode() + "_" + deviceCode + "_" + alarmVO.getAlarmType();
        String interceptConfig = RedisUtil.get(key, RedisDBType.PRIMARY);
        log.info("研判压制获取到的redis值：{}", interceptConfig);
        if (!StringUtils.isEmpty(interceptConfig)) {
            alarmVO.setIntercept(AlgorithmEventVO.FOR_JUDGE);
            intercepts.add(alarmVO);
            return true;
        }
        return false;
    }


    /**
     * 校验是否走雨天压制规则
     *
     * @param configDTOMap
     * @param key
     * @return
     */
    private boolean doIntercept(Map<String, AlarmSuppressConfigDTO> configDTOMap, String key, Date alarmTime) {
        if (MapUtils.isNotEmpty(configDTOMap)) {
            AlarmSuppressConfigDTO configDTO = configDTOMap.get(key);
            if (null != configDTO && configDTO.getSuppressBeginTime().compareTo(alarmTime) <= 0 && configDTO.getSuppressEndTime().compareTo(alarmTime) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置告警的地址和经纬度数据，AI/摄像机告警上报调用
     * @param receiveAlarmVO
     */
    public void setAlarmLocationAndAddress(ReceiveAlarmVO receiveAlarmVO) {
        //告警事件坐标（目前为设备坐标）为空，则取站址的坐标
        String latitude = receiveAlarmVO.getLatitude();
        String longitude = receiveAlarmVO.getLongitude();
        String channelCode = receiveAlarmVO.getChannelCode();
        String deviceCode = receiveAlarmVO.getDeviceCode();
        // 去除上报过来得经纬度，直接取摄像机的经纬度进行告警定位计算
        DeviceSiteReceiveVO deviceSiteReceiveVO = deviceRepository.queryDevice(deviceCode,channelCode);
        if (deviceSiteReceiveVO != null) {
            latitude = deviceSiteReceiveVO.getLatitude();
            longitude = deviceSiteReceiveVO.getLongitude();
            receiveAlarmVO.setAddress(deviceSiteReceiveVO.getLocation());
            receiveAlarmVO.setDeviceAddress(deviceSiteReceiveVO.getLocation()); //设备地址
            receiveAlarmVO.setDeviceName(deviceSiteReceiveVO.getDeviceName()); //替换摄像机名称
            if(!StringUtils.isEmpty(deviceSiteReceiveVO.getChannelName())){
                receiveAlarmVO.setChannelName(deviceSiteReceiveVO.getChannelName()); //替换通道名称
            }

            //1.8.10 同步mongo 增加摄像机类型、摄像机子类型
            receiveAlarmVO.setDeviceType(deviceSiteReceiveVO.getDeviceType());
            receiveAlarmVO.setDeviceTypeName(deviceSiteReceiveVO.getDeviceTypeName());
            receiveAlarmVO.setDeviceSubType(deviceSiteReceiveVO.getDeviceSubType());
            receiveAlarmVO.setDeviceSubTypeName(deviceSiteReceiveVO.getDeviceSubTypeName());
        }
        receiveAlarmVO.setLatitude(latitude);
        receiveAlarmVO.setLongitude(longitude);
        //设置告警点相关信息
        warningOrderUtil.setAlarmLocation(deviceSiteReceiveVO, receiveAlarmVO);
        gisUtil.getAddressFromAmap(receiveAlarmVO, deviceSiteReceiveVO);
    }
}
