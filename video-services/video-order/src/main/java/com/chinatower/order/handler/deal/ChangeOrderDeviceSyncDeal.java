package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import com.chinatower.common.core.domain.OrderUavDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.api.IAlgorithmConfigService;
import com.chinatower.device.entity.dto.AirTaskOperateReq;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.device.AlgorithmGiveMergeOrderVO;
import com.chinatower.video.api.IVideoPatrolPlanService;
import com.chinatower.video.api.IVideoRecordPlanService;
import com.chinatower.video.api.entity.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ChangeOrderDeviceSetDeal
 * @Deacription 变更订单设备配置关系处理模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class ChangeOrderDeviceSyncDeal extends AbstractOrderDeal {

    @Autowired
    private IVideoRecordPlanService videoRecordPlanService;

    @Autowired
    private IVideoPatrolPlanService videoPatrolPlanService;

    @Autowired
    private IAlgorithmConfigService iAlgorithmConfigService;

    @Autowired
    private IAirLineService airLineService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {
        log.info("变更订单设备配置关系处理模块");
        List<OrderDeviceHisDO> reduceDeviceList = orderGateway.getReduceDeviceList();
        if (!CollectionUtils.isEmpty(reduceDeviceList)) {
            //退订--删除预置位、巡航、录像计划
            deleteDeviceSet(reduceDeviceList);
        }
        // 调用接口停用前端算法
        stopFrontAlarmConfig(orderGateway);

        List<OrderUavDeviceHisDO> reduceUavDeviceList = orderGateway.getReduceUavDeviceList();
        if (!CollectionUtils.isEmpty(reduceUavDeviceList)) {
            // 调用接口停用无人机计划、算法
            stopUavDevicePlanConfig(reduceUavDeviceList);
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }
    }

    /**
     * 功能描述 退订--删除预置位、巡航、录像计划
     *
     * @return
     * @author chenmf
     * @date 2022/1/11
     * [reduceDevList]
     */
    private void deleteDeviceSet(List<OrderDeviceHisDO> reduceDevList) {
        Map<String, List<OrderDeviceHisDO>> reduceDevMap = reduceDevList.stream().collect(Collectors.groupingBy(item -> item.getTenantId() + "_" + item.getIndustryCode()));
        for (Map.Entry<String, List<OrderDeviceHisDO>> entry : reduceDevMap.entrySet()) {
            OrderDeviceHisDO orderDeviceHisDO = entry.getValue().get(0);
            String orderCode = orderDeviceHisDO.getOrderCode();
            String tenantId = orderDeviceHisDO.getTenantId();
            String industryCode = orderDeviceHisDO.getIndustryCode();

            List<String> deviceIdList = entry.getValue().stream().map(OrderDeviceHisDO::getDeviceCode).distinct().collect(Collectors.toList());

            QueryOrderDeviceSetDTO queryOrderDeviceSetDTO = new QueryOrderDeviceSetDTO();
            queryOrderDeviceSetDTO.setOrderCode(orderCode);
            queryOrderDeviceSetDTO.setTenantId(tenantId);
            queryOrderDeviceSetDTO.setDeviceCodeList(deviceIdList);
            queryOrderDeviceSetDTO.setIndustryCode(industryCode);
            try {
                //查询通道的录像列表
                Result<List<ChannelVideoPlanVO>> planResult = videoRecordPlanService.queryOrderRecordPlanInfo(queryOrderDeviceSetDTO);
                List<ChannelVideoPlanVO> planList = planResult.getData();
                log.info("ChangeOrderDeviceSetDeal queryOrderRecordPlanInfo param:{}, result:{}",
                        JSON.toJSONString(queryOrderDeviceSetDTO), JSON.toJSONString(planList));
                if (!CollectionUtils.isEmpty(planList)) {
                    for (ChannelVideoPlanVO channelVideoPlanVO : planList) {
                        channelVideoPlanVO.setRecordPlanStatus(0);
                        channelVideoPlanVO.setTenantId(tenantId);
                        channelVideoPlanVO.setOrderCode(orderCode);
                        channelVideoPlanVO.setIndustryCode(industryCode);
                    }
                    //批量启停通道的录像
                    ChannelVideoPlanRequestVO channelVideoPlanRequest = new ChannelVideoPlanRequestVO();
                    channelVideoPlanRequest.setIndustryCode(industryCode);
                    channelVideoPlanRequest.setRecordPlanList(planList);
                    Result<String> result = videoRecordPlanService.batchStartEndRecordPlan(channelVideoPlanRequest);
                    log.info("ChangeOrderDeviceSetDeal batchStartEndRecordPlan param:{}, result:{}",
                            JSON.toJSONString(channelVideoPlanRequest), JSON.toJSONString(result));
                }
            } catch (Exception e) {
                log.error("批量停用通道的录像列表失败", e);
            }

            try {
                //查询巡航路径
                Result<List<PatrolDTO>> patrolResult = videoPatrolPlanService.queryOrderPatrolInfo(queryOrderDeviceSetDTO);
                List<PatrolDTO> patrolDTOList = patrolResult.getData();
                log.info("ChangeOrderDeviceSetDeal queryOrderPatrolInfo param:{}, result:{}",
                        JSON.toJSONString(queryOrderDeviceSetDTO), JSON.toJSONString(patrolResult));
                if (!CollectionUtils.isEmpty(patrolDTOList)) {
                    //批量启停巡航路径
                    List<SttPatrolReq> sttPatrolReqList = Lists.newArrayList();
                    for (PatrolDTO patrolDTO : patrolDTOList) {
                        SttPatrolReq sttPatrolReq = new SttPatrolReq();
                        sttPatrolReq.setPatrolId(patrolDTO.getPatrolId());
                        sttPatrolReq.setPatrolType(DeviceConstants.PATROL_PLAN_STATUS_STOP);
                        sttPatrolReq.setTenantId(tenantId);
                        sttPatrolReq.setDeviceCode(patrolDTO.getDeviceCode());
                        sttPatrolReq.setChannelCode(patrolDTO.getChannelCode());
                        sttPatrolReqList.add(sttPatrolReq);
                    }
                    Result<String> result = videoPatrolPlanService.batchStartEndPatrol(sttPatrolReqList);
                    log.info("ChangeOrderDeviceSetDeal batchStartEndPatrol param:{}, result:{}",
                            JSON.toJSONString(sttPatrolReqList), JSON.toJSONString(result));
                }
            } catch (Exception e) {
                log.error("批量停用巡航路径失败", e);
            }
        }
    }

    private void stopFrontAlarmConfig(OrderGatewayDTO orderGateway) {
        List<OrderDeviceHisDO> reduceDevList = orderGateway.getReduceDeviceList();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(reduceDevList)) {
            Map<String, List<OrderDeviceHisDO>> reduceDevMap = reduceDevList.stream().collect(Collectors.groupingBy(item -> item.getTenantId() + "_" + item.getIndustryCode()));
            for (Map.Entry<String, List<OrderDeviceHisDO>> entry : reduceDevMap.entrySet()) {
                OrderDeviceHisDO orderDeviceHisDO = entry.getValue().get(0);
                String orderCode = orderDeviceHisDO.getOrderCode();
                String tenantId = orderDeviceHisDO.getTenantId();
                String industryCode = orderDeviceHisDO.getIndustryCode();

                List<String> deviceCodeList = entry.getValue().stream().map(OrderDeviceHisDO::getDeviceCode).distinct().collect(Collectors.toList());
                try {
                    AlgorithmGiveMergeOrderVO request = new AlgorithmGiveMergeOrderVO();
                    request.setIndustryCode(industryCode);
                    request.setOrderCode(orderCode);
                    request.setTenantId(tenantId);
                    request.setDeviceCodeList(deviceCodeList);
                    Result<String> result = iAlgorithmConfigService.algorithmGiveMergeOrder(request);
                    log.info("ChangeOrderDeviceSetDeal stopFrontAlarmConfig algorithmGiveMergeOrder param:{}, result:{}",
                            JSON.toJSONString(request), JSON.toJSONString(result));
                } catch (Exception e) {
                    log.error("ChangeOrderDeviceSetDeal stopFrontAlarmConfig 处理前端告警配置失败 orderCode:{}, tenantId:{}, e:", orderCode, tenantId, e);
                }
            }
        } else {
            String orderCode = orderGateway.getOrderCode();
            String tenantId = orderGateway.getCustomer().getCustomerCode();
            for (OrderGatewayDTO.OrderPackages orderPackage : orderGateway.getPackages()) {
                String industryCode = orderPackage.getApplication().getAppCode();
                try {
                    AlgorithmGiveMergeOrderVO request = new AlgorithmGiveMergeOrderVO();
                    request.setIndustryCode(industryCode);
                    request.setOrderCode(orderCode);
                    request.setTenantId(tenantId);
                    request.setDeviceCodeList(Lists.newArrayList());
                    Result<String> result = iAlgorithmConfigService.algorithmGiveMergeOrder(request);
                    log.info("ChangeOrderDeviceSetDeal stopFrontAlarmConfig param:{}, result:{}",
                            JSON.toJSONString(request), JSON.toJSONString(result));
                } catch (Exception e) {
                    log.error("ChangeOrderDeviceSetDeal stopFrontAlarmConfig 处理前端告警配置失败 orderCode:{}, tenantId:{}, e:", orderCode, tenantId, e);
                }
            }
        }

    }

    private void stopUavDevicePlanConfig(List<OrderUavDeviceHisDO> reduceUavDevList) {
        Map<String, List<OrderUavDeviceHisDO>> reduceUavDeviceMap = reduceUavDevList.stream().collect(Collectors.groupingBy(item -> item.getTenantId() + "_" + item.getIndustryCode()));
        for (Map.Entry<String, List<OrderUavDeviceHisDO>> entry : reduceUavDeviceMap.entrySet()) {
            OrderUavDeviceHisDO orderDeviceHisDO = entry.getValue().get(0);
            String tenantId = orderDeviceHisDO.getTenantId();
            String industryCode = orderDeviceHisDO.getIndustryCode();
            List<String> deviceCodeList = entry.getValue().stream().map(OrderUavDeviceHisDO::getDeviceCode).distinct().collect(Collectors.toList());
            try {
                AirTaskOperateReq request = new AirTaskOperateReq();
                request.setTenantId(tenantId);
                request.setIndustryCode(industryCode);
                request.setDeviceCodeList(deviceCodeList);
                Result result = airLineService.closeAirTaskAndAlgorithms(request);
                log.info("ChangeOrderDeviceSyncDeal stopUavDevicePlanConfig param:{}, result:{}", JSON.toJSONString(request), JSON.toJSONString(result));
            } catch (Exception e) {
                log.error("ChangeOrderDeviceSyncDeal stopUavDevicePlanConfig error:", e);
            }
        }
    }
}
