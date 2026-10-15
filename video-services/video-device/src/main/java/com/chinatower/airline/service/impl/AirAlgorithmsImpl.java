package com.chinatower.airline.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.mapper.AirAlgorithmsMapper;
import com.chinatower.airline.service.AirAlgorithmsService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.device.entity.dto.AirAlTaskLogReq;
import com.chinatower.device.entity.dto.SyncAlgorithmsReq;
import com.chinatower.event.constants.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/11 12:13
 */
@Service
public class AirAlgorithmsImpl implements AirAlgorithmsService {
    @Resource
    private AirAlgorithmsMapper airAlgorithmsMapper;
    @Resource
    private UavOkhttpUtil okhttpUtil;
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 算法任务下发新增
     *
     * @param syncAlgorithmsReq
     */
    @Override
    public void edit(SyncAlgorithmsReq syncAlgorithmsReq) {
        airAlgorithmsMapper.edit(syncAlgorithmsReq);
    }

    /**
     * @param airAlTaskLogReqs
     */
    @Override
    public void retryAirAlSync(List<AirAlTaskLogReq> airAlTaskLogReqs) {
        if (ObjectUtil.isNotEmpty(airAlTaskLogReqs)) {
            ObjectMapper objectMapper = new ObjectMapper();
            //重新下发算法任务
            airAlTaskLogReqs.stream().forEach(airAlTaskLogReq -> {
                String reqJson = airAlTaskLogReq.getTaskJson();
                SyncAlgorithmsReq syncAlgorithmsReq = new SyncAlgorithmsReq();
                // 从字符串 reqJson 转换为实体类 AirAlTaskLogReq
                try {
                    syncAlgorithmsReq = objectMapper.readValue(reqJson, SyncAlgorithmsReq.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                NetConnectUtils.RequestParamter<SyncAlgorithmsReq> paramterAl = new NetConnectUtils.RequestParamter<>();
                NetConnectUtils.RequestParamter.Path pathAl = new NetConnectUtils.RequestParamter.Path();
                pathAl.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYNC_UAV_ALGORITHMS_TASK);
                paramterAl.setPath(pathAl);
                paramterAl.setBody(syncAlgorithmsReq);
                ResultEntity result = okhttpUtil.doPostAlgGateWay(paramterAl,null,null);
                if (ObjectUtil.isNotEmpty(result) && "0000".equals(result.getReturnCode())) {
                    //任务存入本地
                    this.edit(syncAlgorithmsReq);
                    //下发成功更新mongo表
                    AirAlTaskLogReq airAlTaskLogReqUpdate = new AirAlTaskLogReq();
                    Update update = new Update();
                    Query query = new Query();
                    org.springframework.data.mongodb.core.query.Query queryUpdate = new org.springframework.data.mongodb.core.query.Query();
                    query.addCriteria(Criteria.where("taskId").is(airAlTaskLogReq.getTaskId()));
                    airAlTaskLogReqUpdate.setTaskId(syncAlgorithmsReq.getTaskId());
                    airAlTaskLogReqUpdate.setTaskJson(JSONObject.toJSONString(syncAlgorithmsReq));
                    airAlTaskLogReqUpdate.setReturnMsg(result.getReturnMsg());
                    airAlTaskLogReqUpdate.setReturnCode(result.getReturnCode());
                    BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Constants.AIR_TASK_AL_SYNC_LOG);
                    bulkOperations.updateOne(queryUpdate, update);
                    bulkOperations.execute();
                }
            });
        }
    }
}
