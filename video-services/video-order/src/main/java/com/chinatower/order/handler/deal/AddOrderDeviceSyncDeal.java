package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.IVideoManageRemoteService;
import com.chinatower.manage.api.dto.ScreenDataConfigVO;
import com.chinatower.platform.constants.OrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AddOrderDeviceSyncDeal extends AbstractOrderDeal {

    @Autowired
    private IVideoManageRemoteService iVideoManageRemoteService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("同步新增设备处理模块");
        try {
            Map<String, List<OrderGatewayDTO.Device>> addDeviceMap = orderGateway.getAddDeviceMap();
            Map<String, List<OrderGatewayDTO.SensorDevice>> addSensorDeviceMap = orderGateway.getAddSensorDeviceMap();
            Map<String, List<OrderGatewayDTO.Device>> addUavDeviceMap = orderGateway.getAddUavDeviceMap();
            Map<String, List<OrderGatewayDTO.SensorDevice>> addHornDeviceMap = orderGateway.getAddHornDeviceMap();
            Map<String, List<OrderGatewayDTO.Device>> addRecorderDeviceMap = orderGateway.getAddRecordDeviceMap();

            String tenantId = orderGateway.getCustomer().getCustomerCode();
            List<ScreenDataConfigVO> paramList = Lists.newArrayList();
            for (OrderGatewayDTO.OrderPackages packageInfo : orderGateway.getPackages()) {
                String industryCode = packageInfo.getApplication().getAppCode();
                List<OrderGatewayDTO.Device> addDeviceList = addDeviceMap != null ? addDeviceMap.get(industryCode) : null;
                List<OrderGatewayDTO.SensorDevice> addSensorDeviceList = addSensorDeviceMap != null ? addSensorDeviceMap.get(industryCode) : null;
                List<OrderGatewayDTO.Device> addUavDeviceList = addUavDeviceMap != null ? addUavDeviceMap.get(industryCode) : null;
                List<OrderGatewayDTO.SensorDevice> addHornDeviceList = addHornDeviceMap != null ? addHornDeviceMap.get(industryCode) : null;
                List<OrderGatewayDTO.Device> addRecorderDeviceList = addRecorderDeviceMap != null ? addRecorderDeviceMap.get(industryCode) : null;

                boolean hasDataFlag = false;
                ScreenDataConfigVO param = new ScreenDataConfigVO();
                param.setTenantId(tenantId);
                param.setIndustryCode(industryCode);
                if (CollectionUtils.isNotEmpty(addDeviceList)) {
                    hasDataFlag = true;
                    param.setCameraCodeList(addDeviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).distinct().collect(Collectors.toList()));
                }
                if (CollectionUtils.isNotEmpty(addSensorDeviceList)) {
                    hasDataFlag = true;

                    param.setIotCodeList(addSensorDeviceList.stream()
                            .filter(x -> Objects.equals(OrderConstants.ORDER_SENSOR_CODE, x.getCategoryCode()))
                            .map(OrderGatewayDTO.SensorDevice::getDeviceCode).distinct().collect(Collectors.toList()));
                    param.setRadarCodeList(addSensorDeviceList.stream()
                            .filter(x -> Objects.equals(OrderConstants.ORDER_RADAR_CODE, x.getCategoryCode()))
                            .map(OrderGatewayDTO.SensorDevice::getDeviceCode).distinct().collect(Collectors.toList()));
                }
                if (CollectionUtils.isNotEmpty(addUavDeviceList)) {
                    hasDataFlag = true;
                    param.setUavCodeList(addUavDeviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).distinct().collect(Collectors.toList()));
                }
                if (CollectionUtils.isNotEmpty(addHornDeviceList)) {
                    hasDataFlag = true;
                    param.setSpeakerCodeList(addHornDeviceList.stream().map(OrderGatewayDTO.SensorDevice::getDeviceCode).distinct().collect(Collectors.toList()));
                }
                if (CollectionUtils.isNotEmpty(addRecorderDeviceList)) {
                    hasDataFlag = true;
                    param.setRecorderCodeList(addRecorderDeviceList.stream().map(OrderGatewayDTO.Device::getDeviceCode).distinct().collect(Collectors.toList()));
                }
                if (hasDataFlag) {
                    paramList.add(param);
                }
            }

            if (CollectionUtils.isNotEmpty(paramList)) {
                Result result = iVideoManageRemoteService.addConfig(paramList);
                log.info("AddOrderDeviceSyncDeal 同步大屏配置，参数:{}, 返回值:{}", JSON.toJSONString(paramList), JSON.toJSONString(result));
            }
        } catch (Exception e) {
            log.error("AddOrderDeviceSyncDeal error:", e);
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

}
