package com.chinatower.event.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.entity.dto.OperateMongo;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.event.mapper.AlarmOperateMapper;
import com.chinatower.event.mapper.WarningOrderCollectionMapper;
import com.chinatower.event.service.OperateService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.PageHelper;
import com.mongodb.BasicDBList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
public class OperateServiceImpl implements OperateService {

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WarningOrderCollectionMapper warningOrderCollectionMapper;

    @Autowired
    private OperateService operateService;

    @Override
    public AlarmOperateDTO getOperateInfo(String warningOrderId,String userId) {
        AlarmOperateDTO map = new AlarmOperateDTO();
        // 获取表名
        String tableName = TableConstant.WARN_ALARM_OPERATE +"_"+ warningOrderId.substring(0, 6);

        String flag = Constants.ONE;
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_OPERATE_POWER_FLAG);
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        List<WarningOrderListVO> collList = new ArrayList<>();
        if(!StringUtils.isEmpty(userId)){
            AlarmOrderDTO dto = new AlarmOrderDTO();
            dto.setOrderId(warningOrderId);
            dto.setCollectionCreator(userId);
            collList = warningOrderCollectionMapper.qryWarningOrderCollection(dto);
        }
        if(!Constants.ONE.equals(flag)){
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).is(warningOrderId));
            List<OperateMongo> operateMongo = mongoTemplate.find(query,OperateMongo.class,tableName);
            if(CollectionUtils.isEmpty(operateMongo)){
                return null;
            }
            BeanUtil.copyProperties(operateMongo.get(0),map);
            map.setTradeId(operateMongo.get(0).getIndustryCode());
            map.setTradeVerCode(operateMongo.get(0).getAppVerCode());

            String finishTime = "";
            if(!"3".equals(String.valueOf(operateMongo.get(0).getJudgeResult()))){
                finishTime = operateMongo.get(0).getModifierTime();
            }
            map.setFinishTime(finishTime);
        }else{
            map =  alarmOperateMapper.getAlarmOperate(warningOrderId);
        }
        if(map != null){
            if(!CollectionUtils.isEmpty(collList)){
                map.setIsCollection(Constants.ONE);
            }else {
                map.setIsCollection(Constants.ZERO);
            }
        }
        return map;
    }

    @Override
    public List<AlarmOperateDTO> getOperateListByOrderIds(List<String> list) {
        List<AlarmOperateDTO> resultList = new ArrayList<>();
        String flag = Constants.ONE;
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_OPERATE_POWER_FLAG);
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        if(!Constants.ONE.equals(flag)){
            List<String> tableNameList = new ArrayList<>();
            List<String> orderList = new ArrayList<>();
            for (String orderId:list) {
                if(!StringUtils.isEmpty(orderId)){
                    orderList.add(orderId);
                    String tableName = TableConstant.WARN_ALARM_OPERATE +"_"+ orderId.substring(0, 6);
                    if(!tableNameList.contains(tableName)){
                        tableNameList.add(tableName);
                    }
                }
            }
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).in(orderList));
            query.addCriteria(Criteria.where("operate_type").is(Constants.TWO));
            query.addCriteria(Criteria.where("judge_result").is(Constants.THREE));
            for (String tableName: tableNameList) {
                List<OperateMongo> operateMongoList = mongoTemplate.find(query,OperateMongo.class,tableName);
                for (OperateMongo vo:operateMongoList) {
                    AlarmOperateDTO dto = new AlarmOperateDTO();
                    BeanUtil.copyProperties(vo,dto);
                    resultList.add(dto);
                }
            }
        }else{
            resultList= alarmOperateMapper.getAlarmOperateList(list);
        }
        return resultList;
    }


    private BasicDBList createCriteriaList(Criteria[] criteria) {
        BasicDBList bsonList = new BasicDBList();
        Criteria[] var3 = criteria;
        int var4 = criteria.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Criteria c = var3[var5];
            bsonList.add(c.getCriteriaObject());
        }

        return bsonList;
    }
}
