package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.entity.dto.AirTaskOperateReq;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.video.api.IVideoPatrolPlanService;
import com.chinatower.video.api.IVideoRecordPlanService;
import com.chinatower.video.api.entity.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName OrderDeviceSetDeal
 * @Deacription 启停订单设备配置（预置位、巡航、录像计划）通用关系模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class EnableOrderDeviceSetDeal extends AbstractOrderDeal {

    @Autowired
    private IVideoRecordPlanService videoRecordPlanService;
    @Autowired
    private IVideoPatrolPlanService videoPatrolPlanService;

    @Autowired
    private IAirLineService airLineService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        int orderStatus = orderGateway.getStatus();
        String orderCode = orderGateway.getOrderCode();
        String tenantId = orderGateway.getCustomer().getCustomerCode();

        Set<String> industryCodeSet = Sets.newHashSet();

        // key -> industryCode value -> uavDeviceCodeList
        Map<String, List<String>> uavDeviceIndustryMap = Maps.newHashMap();
        for (OrderGatewayDTO.OrderPackages orderPackage : orderGateway.getPackages()) {
            String industryCode = orderPackage.getApplication().getAppCode();
            industryCodeSet.add(industryCode);

            if (CollectionUtils.isNotEmpty(orderPackage.getUavDevice())) {
                List<String> uavDeviceCodeList = orderPackage.getUavDevice().stream()
                        .map(OrderGatewayDTO.Device::getDeviceCode).distinct().collect(Collectors.toList());
                uavDeviceIndustryMap.put(industryCode, uavDeviceCodeList);
            }
        }
        // 处理摄像机录像计划、巡航路径信息
        handleDeviceSet(industryCodeSet, orderCode, tenantId, orderStatus);
        // 调用接口停用无人机计划、算法
        handleUavDevicePlanConfig(uavDeviceIndustryMap, tenantId, orderStatus);

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    private void handleDeviceSet(Set<String> industryCodeSet, String orderCode, String tenantId, int orderStatus) {
        for (String industryCode : industryCodeSet) {
            QueryOrderDeviceSetDTO queryOrderDeviceSetDTO = new QueryOrderDeviceSetDTO();
            queryOrderDeviceSetDTO.setOrderCode(orderCode);
            queryOrderDeviceSetDTO.setTenantId(tenantId);
            queryOrderDeviceSetDTO.setIndustryCode(industryCode);
            try {
                //查询通道的录像列表
                Result<List<ChannelVideoPlanVO>> planResult = videoRecordPlanService.queryOrderRecordPlanInfo(queryOrderDeviceSetDTO);
                List<ChannelVideoPlanVO> planList = planResult.getData();
                log.info("EnableOrderDeviceSetDeal queryOrderRecordPlanInfo param:{}, result:{}",
                        JSON.toJSONString(queryOrderDeviceSetDTO), JSON.toJSONString(planList));
                if (!CollectionUtils.isEmpty(planList)) {
                    int recordPlanStatus = OrderConstants.STATUS_FOR_ENABLE == orderStatus ? 1 : 0;
                    for (ChannelVideoPlanVO channelVideoPlanVO : planList) {
                        channelVideoPlanVO.setRecordPlanStatus(recordPlanStatus);
                        channelVideoPlanVO.setTenantId(tenantId);
                        channelVideoPlanVO.setOrderCode(orderCode);
                        channelVideoPlanVO.setIndustryCode(industryCode);
                    }
                    //批量启停通道的录像
                    ChannelVideoPlanRequestVO channelVideoPlanRequest = new ChannelVideoPlanRequestVO();
                    channelVideoPlanRequest.setIndustryCode(industryCode);
                    channelVideoPlanRequest.setRecordPlanList(planList);
                    Result<String> result = videoRecordPlanService.batchStartEndRecordPlan(channelVideoPlanRequest);
                    log.info("EnableOrderDeviceSetDeal batchStartEndRecordPlan param:{}, result:{}",
                            JSON.toJSONString(channelVideoPlanRequest), JSON.toJSONString(result));
                }
            } catch (Exception e) {
                log.error("批量启停通道的录像失败", e);
            }

            try {
                //查询巡航路径
                Result<List<PatrolDTO>> patrolResult = videoPatrolPlanService.queryOrderPatrolInfo(queryOrderDeviceSetDTO);
                List<PatrolDTO> patrolDTOList = patrolResult.getData();
                log.info("EnableOrderDeviceSetDeal queryOrderPatrolInfo param:{}, result:{}",
                        JSON.toJSONString(queryOrderDeviceSetDTO), JSON.toJSONString(patrolDTOList));
                if (!CollectionUtils.isEmpty(patrolDTOList)) {
                    //批量启停巡航路径
                    int patrolType = OrderConstants.STATUS_FOR_ENABLE == orderStatus
                            ? DeviceConstants.PATROL_PLAN_STATUS_START : DeviceConstants.PATROL_PLAN_STATUS_STOP;
                    List<SttPatrolReq> sttPatrolReqList = Lists.newArrayList();
                    for (PatrolDTO patrolDTO : patrolDTOList) {
                        SttPatrolReq sttPatrolReq = new SttPatrolReq();
                        sttPatrolReq.setPatrolId(patrolDTO.getPatrolId());
                        sttPatrolReq.setPatrolType(patrolType);
                        sttPatrolReq.setTenantId(tenantId);
                        sttPatrolReq.setDeviceCode(patrolDTO.getDeviceCode());
                        sttPatrolReq.setChannelCode(patrolDTO.getChannelCode());
                        sttPatrolReqList.add(sttPatrolReq);
                    }
                    Result<String> result = videoPatrolPlanService.batchStartEndPatrol(sttPatrolReqList);
                    log.info("EnableOrderDeviceSetDeal batchStartEndPatrol param:{}, result:{}",
                            JSON.toJSONString(sttPatrolReqList), JSON.toJSONString(result));
                }
            } catch (Exception e) {
                log.error("批量启停巡航路径失败", e);
            }
        }
    }

    private void handleUavDevicePlanConfig(Map<String, List<String>> uavDeviceIndustryMap, String tenantId, int ordStatus) {
        for (Map.Entry<String, List<String>> entry : uavDeviceIndustryMap.entrySet()) {
            try {
                String industryCode = entry.getKey();
                List<String> deviceCodeList = entry.getValue();
                AirTaskOperateReq request = new AirTaskOperateReq();
                request.setTenantId(tenantId);
                request.setIndustryCode(industryCode);
                request.setDeviceCodeList(deviceCodeList);
                Result result = null;
                if (OrderConstants.STATUS_FOR_ENABLE == ordStatus) {
                    result = airLineService.openAirTaskAndAlgorithms(request);
                } else if (OrderConstants.STATUS_FOR_DEACTIVATE == ordStatus) {
                    result = airLineService.closeAirTaskAndAlgorithms(request);
                }
                log.info("EnableOrderDeviceSetDeal handleUavDevicePlanConfig param:{}, result:{}", JSON.toJSONString(request), JSON.toJSONString(result));
            } catch (Exception e) {
                log.error("EnableOrderDeviceSetDeal handleUavDevicePlanConfig error:", e);
            }
        }
    }
}
