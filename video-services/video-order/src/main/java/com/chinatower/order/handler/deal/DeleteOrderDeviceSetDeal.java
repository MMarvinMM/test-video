package com.chinatower.order.handler.deal;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.domain.OrderUavDeviceHisDO;
import com.chinatower.common.core.dto.OrderGatewayDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.entity.dto.AirTaskOperateReq;
import com.chinatower.order.repository.IOrderUavDeviceHisRepository;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.video.api.IVideoPatrolPlanService;
import com.chinatower.video.api.IVideoRecordPlanService;
import com.chinatower.video.api.entity.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName OrderDeviceSetDeal
 * @Deacription 删除订单设备配置（预置位、巡航、录像计划）通用关系模块
 * @Author chenmf
 * @Date 2022/1/7 10:58
 * @Version 1.0
 **/
@Component
@Slf4j
public class DeleteOrderDeviceSetDeal extends AbstractOrderDeal {

    @Autowired
    private IVideoRecordPlanService videoRecordPlanService;

    @Autowired
    private IVideoPatrolPlanService videoPatrolPlanService;

    @Autowired
    private IOrderUavDeviceHisRepository orderUavDeviceHisRepository;

    @Autowired
    private IAirLineService airLineService;

    @Override
    public void orderRelationshipDeal(OrderGatewayDTO orderGateway) {

        String orderCode = orderGateway.getOrderCode();
        String tenantId = orderGateway.getCustomer().getCustomerCode();

        Set<String> industryCodeSet = Sets.newHashSet();
        for (OrderGatewayDTO.OrderPackages orderPackage : orderGateway.getPackages()) {
            String industryCode = orderPackage.getApplication().getAppCode();
            industryCodeSet.add(industryCode);
        }
        // 退订--删除预置位、巡航、录像计划
        deleteDeviceSet(industryCodeSet, orderCode, tenantId);

        // 如果oldOrderCode为空，则不是续订，调用停用无人机计划、算法；续订不调用无人机计划、算法接口
        if (StringUtils.isEmpty(orderGateway.getOldOrderCode())) {
            // 运管退订报文传参的设备不全，获取上一次订购的报文当做全量的设备信息
            int moveSign = orderGateway.getOrderMoveSign() - 1;
            List<OrderUavDeviceHisDO> reduceUavDeviceList = orderUavDeviceHisRepository.getOldOrderDeviceByOrderCode(orderCode, moveSign);
            // 调用接口停用无人机计划、算法
            if (CollectionUtils.isNotEmpty(reduceUavDeviceList)) {
                stopUavDevicePlanConfig(reduceUavDeviceList);
            }
        }

        if (null != this.getNext()) {
            this.getNext().orderRelationshipDeal(orderGateway);
        }

    }

    private void deleteDeviceSet(Set<String> industryCodeSet, String orderCode, String tenantId) {
        for (String industryCode : industryCodeSet) {
            QueryOrderDeviceSetDTO queryOrderDeviceSetDTO = new QueryOrderDeviceSetDTO();
            queryOrderDeviceSetDTO.setOrderCode(orderCode);
            queryOrderDeviceSetDTO.setTenantId(tenantId);
            queryOrderDeviceSetDTO.setIndustryCode(industryCode);
            try {
                //查询通道的录像列表
                Result<List<ChannelVideoPlanVO>> planResult = videoRecordPlanService.queryOrderRecordPlanInfo(queryOrderDeviceSetDTO);
                List<ChannelVideoPlanVO> planList = planResult.getData();
                log.info("DeleteOrderDeviceSetDeal queryOrderRecordPlanInfo param:{}, result:{}",
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
                    log.info("DeleteOrderDeviceSetDeal batchStartEndRecordPlan param:{}, result:{}",
                            JSON.toJSONString(channelVideoPlanRequest), JSON.toJSONString(result));
                }
            } catch (Exception e) {
                log.error("批量删除通道的录像失败", e);
            }

            try {
                //查询巡航路径
                Result<List<PatrolDTO>> patrolResult = videoPatrolPlanService.queryOrderPatrolInfo(queryOrderDeviceSetDTO);
                List<PatrolDTO> patrolDTOList = patrolResult.getData();
                log.info("DeleteOrderDeviceSetDeal queryOrderPatrolInfo param:{}, result:{}",
                        JSON.toJSONString(queryOrderDeviceSetDTO), JSON.toJSONString(patrolDTOList));
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
                    log.info("DeleteOrderDeviceSetDeal batchStartEndPatrol param:{}, result:{}",
                            JSON.toJSONString(sttPatrolReqList), JSON.toJSONString(result));
                }
            } catch (Exception e) {
                log.error("批量删除巡航路径失败", e);
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
                log.info("DeleteOrderDeviceSetDeal stopUavDevicePlanConfig param:{}, result:{}", JSON.toJSONString(request), JSON.toJSONString(result));
            } catch (Exception e) {
                log.error("DeleteOrderDeviceSetDeal stopUavDevicePlanConfig error:", e);
            }
        }
    }
}
