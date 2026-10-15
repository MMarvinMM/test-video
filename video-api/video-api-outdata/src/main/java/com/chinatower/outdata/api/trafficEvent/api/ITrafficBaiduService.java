package com.chinatower.outdata.api.trafficEvent.api;


import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.trafficEvent.enums.TrafficEventEnum;
import com.chinatower.outdata.api.trafficEvent.TrafficEventUtil;
import com.chinatower.outdata.api.trafficEvent.entity.TrafficAlarmCurvelRsp;
import com.chinatower.outdata.api.trafficEvent.entity.TrafficAlarmDetailReq;
import com.chinatower.outdata.api.trafficEvent.entity.TrafficAlarmDetailRsp;
import com.chinatower.outdata.api.trafficEvent.entity.TrafficAlarmListReq;
import com.chinatower.outdata.api.trafficEvent.entity.TrafficAlarmListRsp;
import com.chinatower.outdata.api.trafficEvent.entity.TrafficUgcListReq;
import com.chinatower.outdata.api.trafficEvent.entity.TrafficUgcListRsp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ITrafficBaiduService {


    @Autowired
    private TrafficEventUtil trafficEventUtil;


    @PostMapping("/traffic/getAlarmList")
    public Result<List<TrafficAlarmListRsp>> getAlarmList(@RequestBody TrafficAlarmListReq trafficAlarmListReq){
        try {
            String resp = trafficEventUtil.restGet(trafficAlarmListReq, TrafficEventEnum.InterfaceConfig.TRAFFIC_ALARMLIST,
                    trafficAlarmListReq.getTrafficEventAk());
            List resultList = JSONObject.parseObject(resp, List.class);
            List<TrafficAlarmListRsp> rsp = new ArrayList<>(resultList.size());
            for (Object resultObject : resultList) {
                String result = assembleLinkStates(JSONObject.toJSONString(resultObject));
                TrafficAlarmListRsp trafficAlarmListRsp = JSONObject.parseObject(result, TrafficAlarmListRsp.class);
                rsp.add(trafficAlarmListRsp);
            }
            return Result.data(rsp);
        } catch (Exception e) {
            log.error("获取告警列表异常", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/traffic/getAlarmDetail")
    public Result<TrafficAlarmDetailRsp> getAlarmDetail(@RequestBody TrafficAlarmDetailReq trafficAlarmDetailReq){
        try {
            String resp = trafficEventUtil.restGet(trafficAlarmDetailReq, TrafficEventEnum.InterfaceConfig.TRAFFIC_ALARMDETAIL,
                    trafficAlarmDetailReq.getTrafficEventAk());
            String result = assembleLinkStates(resp);
            TrafficAlarmDetailRsp trafficAlarmDetailRsp = JSONObject.parseObject(result, TrafficAlarmDetailRsp.class);
            return Result.data(trafficAlarmDetailRsp);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

    }

    @PostMapping("/traffic/getAlarmCurve")
    public Result<List<TrafficAlarmCurvelRsp>> getAlarmCurve(@RequestBody TrafficAlarmDetailReq trafficAlarmDetailReq){
        try {
            String resp = trafficEventUtil.restGet(trafficAlarmDetailReq, TrafficEventEnum.InterfaceConfig.TRAFFIC_ALARMCURVE,
                    trafficAlarmDetailReq.getTrafficEventAk());
            List resultList = JSONObject.parseObject(resp, List.class);
            List<TrafficAlarmCurvelRsp> rsp = new ArrayList<>(resultList.size());
            for (Object resultObject : resultList) {
                String result = assembleLinkStates(JSONObject.toJSONString(resultObject));
                TrafficAlarmCurvelRsp trafficAlarmCurvelRsp = JSONObject.parseObject(result, TrafficAlarmCurvelRsp.class);
                rsp.add(trafficAlarmCurvelRsp);
            }
            return Result.data(rsp);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/traffic/getUgcList")
    public Result<List<TrafficUgcListRsp>> getUgcList(@RequestBody TrafficUgcListReq trafficUgcListReq){
        try {
            String resp = trafficEventUtil.restGet(trafficUgcListReq, TrafficEventEnum.InterfaceConfig.TRAFFIC_UGCLIST,
                    trafficUgcListReq.getTrafficEventAk());
            List resultList = JSONObject.parseObject(resp, List.class);
            List<TrafficUgcListRsp> rsp = new ArrayList<>(resultList.size());
            for (Object resultObject : resultList) {
                String result = assembleLinkStates(JSONObject.toJSONString(resultObject));
                TrafficUgcListRsp trafficUgcListRsp = JSONObject.parseObject(result, TrafficUgcListRsp.class);
                rsp.add(trafficUgcListRsp);
            }
            return Result.data(rsp);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }



    private String assembleLinkStates(String response){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode linkStatesNode = rootNode.get("linkStates");
            if (!Objects.isNull(linkStatesNode) && linkStatesNode.isInt()) {
                ((ObjectNode) rootNode).remove("linkStates");
            }
            response = rootNode.toString();
        } catch (JsonProcessingException e) {
            log.error("解析linkStates异常", e);
        }
        return response;
    }
}
