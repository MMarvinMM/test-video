package com.chinatower.event.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.entity.dto.DelayMessageDto;
import com.chinatower.event.entity.dto.DelayMsgTypeDto;
import com.chinatower.event.mongodb.OrderDelayMessageMapper;
import com.chinatower.event.service.OrderDelayMessageService;
import com.chinatower.mongodb.enums.MongoDBType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderDelayMessageServiceImpl implements OrderDelayMessageService {

    @Autowired
    OrderDelayMessageMapper orderDelayMessageMapper;

    @Override
    public void updateOrderDelayList(List<DelayMessageDto> delayMessageDtoList) {

        log.info("updateOrderDelayList-req:{}", JSON.toJSONString(delayMessageDtoList));
        for(DelayMessageDto delayMessageDto : delayMessageDtoList){
            Query updateQuery = new Query();
            if(StringUtils.isEmpty(delayMessageDto.getOrderId())){
                updateQuery = Query.query(Criteria.where(Constants.PARAM_USER_ID).is(delayMessageDto.getUserId()).
                        and(Constants.PARAM_INDUSTRY_CODE).is(delayMessageDto.getIndustryCode()).and(Constants.PARAM_OPERATE_STATUS).is(Constants.ZERO));
            }else{
                if(StringUtils.isEmpty(delayMessageDto.getUserId())){
                    updateQuery = Query.query(Criteria.where(Constants.PARAM_OPERATE_STATUS).is(Constants.ZERO).
                            and("orderId").is(delayMessageDto.getOrderId()));
                }else{
                    updateQuery = Query.query(Criteria.where(Constants.PARAM_USER_ID).is(delayMessageDto.getUserId()).
                            and(Constants.PARAM_INDUSTRY_CODE).is(delayMessageDto.getIndustryCode()).and(Constants.PARAM_OPERATE_STATUS).is(Constants.ZERO).
                            and("orderId").is(delayMessageDto.getOrderId()));
                }
            }

            Update updateEntity = new Update()
                    .set("updateTime",new Date())
                    .set(Constants.PARAM_OPERATE_STATUS,Constants.ONE);
            orderDelayMessageMapper.mongoUpdate(updateQuery,updateEntity,MongoDBType.PRIMARY, TableConstant.SYS_ORDER_DELAY_TEBLE,true);
        }
    }

    @Override
    public void updateOrderDelayMsgType(DelayMsgTypeDto delayMsgTypeDto) {
        log.info("updateOrderDelayMsgType-req:{}", JSON.toJSONString(delayMsgTypeDto));

            Query updateQuery = new Query();
                updateQuery = Query.query(Criteria.where(Constants.PARAM_USER_ID).is(delayMsgTypeDto.getUserId()).
                        and(Constants.PARAM_INDUSTRY_CODE).is(delayMsgTypeDto.getIndustryCode()).and("source").is(Constants.ONE));
            Update updateEntity = new Update()
                    .set("updateTime",new Date())
                    .set("msgType",delayMsgTypeDto.getMsgType());
            orderDelayMessageMapper.mongoUpdate(updateQuery,updateEntity,MongoDBType.PRIMARY, TableConstant.SYS_ORDER_DELAY_TEBLE,true);
        }

}
