package com.chinatower.event.mongodb;

import com.alibaba.fastjson.JSON;
import com.chinatower.device.entity.vo.SensorAttrMapVO;
import com.chinatower.event.entity.dto.IotFactoryMapDTO;
import com.chinatower.event.entity.dto.ReceiveIotAlarmFactorDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmFactorMongoDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmGroupsDto;
import com.chinatower.event.entity.vo.AntiShakeValueVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.event.constants.Constants;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.mongodb.util.MongoHandler;
import com.chinatower.platform.constants.DeviceConstants;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SensorAttrValueMongoDAO
 * @Deacription
 **/
@Slf4j
@Component
public class IotConfigMongoMapper extends AbstractBaseMongoDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        this.getTemplate(type).createCollection(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate end -----------------------");
    }

    public Boolean collectionExists(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.collectionExists start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(com.chinatower.event.constants.Constants.PARAMLIST, collectionName);
        Boolean isExists = this.getTemplate(type).collectionExists(collectionName);
        long end = System.currentTimeMillis();
        log.info(com.chinatower.event.constants.Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.collectionExists end -----------------------");
        return isExists;
    }

    public String findOne(Criteria criteria, MongoDBType type, String collectionName ,String factoryCode ) {
        log.info("---------------------- BaseMongoDAOImpl.query start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(criteria), collectionName);
        // 创建匹配操作
        MatchOperation matchOperation = Aggregation.match(criteria);

        // 创建分组操作
        GroupOperation groupOperation = Aggregation.group().sum(factoryCode).as("sumValue");

        // 构建聚合查询
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

        // 执行聚合查询
        AggregationResults<ReceiveIotAlarmFactorDto> results = mongoTemplate.aggregate(aggregation, collectionName, ReceiveIotAlarmFactorDto.class);
        String sumMongoValue ="";
        if (null != results.getUniqueMappedResult()){
             sumMongoValue = results.getUniqueMappedResult().getSumValue();
        }
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.query end -----------------------");
        return sumMongoValue;
    }

    public List<IotFactoryMapDTO> groupMongoFactoryValue(List<String> mongoDeviceCodeList, String mergeTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.MERGE_TIME).is(mergeTime));
        query.addCriteria(Criteria.where(Constants.PARAM_DEVICE_CODE).in(mongoDeviceCodeList));
        List<IotFactoryMapDTO> attrValues = this.getTemplate(MongoDBType.PRIMARY).find(query, IotFactoryMapDTO.class,
                Constants.IOT_CONFIG_FACTORY_SUM_VALUE);

        return attrValues;
    }

    public AntiShakeValueVO getMongoAntiShakeValue(ReceiveIotAlarmGroupsDto groupsDto) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.PARAM_CONFIG_ID).is(groupsDto.getIotConfigId()));
        query.addCriteria(Criteria.where(Constants.PARAM_GROUP_ID).in(groupsDto.getId()));
        AntiShakeValueVO attrValues = this.getTemplate(MongoDBType.PRIMARY).findOne(query, AntiShakeValueVO.class, Constants.IOT_ANTI_SHAKE_SUM_VALUE);
        return attrValues;
    }



    public void updateAll(List<Map<String,String>> mongoFactoryList, List<IotFactoryMapDTO> iotFactoryMapDTOS) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,Constants.IOT_CONFIG_FACTORY_SUM_VALUE);
        //接收需要更新累计设备因子数据
        for (Map<String, String> factorMongoDto : mongoFactoryList) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.PARAM_DEVICE_CODE).is(factorMongoDto.get(Constants.PARAM_DEVICE_CODE)));
            query.addCriteria(Criteria.where(Constants.MERGE_TIME).is(factorMongoDto.get(Constants.MERGE_TIME)));
            boolean mark = false;
            //MongoDB中设备,因子,时间下已存的值
            for (IotFactoryMapDTO factoryMapDTO : iotFactoryMapDTOS) {
                if (String.valueOf(factoryMapDTO.get(Constants.PARAM_DEVICE_CODE)).equals(factorMongoDto.get(Constants.PARAM_DEVICE_CODE))){
                    Update update = new Update();
                    for (Map.Entry<String, String> entry : factorMongoDto.entrySet()) {
                        if (!Constants.PARAM_DEVICE_CODE.equals(entry.getKey()) && !Constants.MERGE_TIME.equals(entry.getKey())){
                            if (factoryMapDTO.get(entry.getKey()) != null) {
                                update.set(entry.getKey(),new Decimal128(new BigDecimal(entry.getValue()).add(new BigDecimal(String.valueOf(factoryMapDTO.get(entry.getKey()))))));
                            }else {
                                update.set(entry.getKey(),new Decimal128(new BigDecimal(entry.getValue())));
                            }
                        }
                    }
                    bulkOperations.upsert(query, update);
                    mark = true;
                    break;
                }
            }
            if (!mark){
                Update update = new Update();
                for (Map.Entry<String, String> entry : factorMongoDto.entrySet()) {
                    if (!Constants.PARAM_DEVICE_CODE.equals(entry.getKey()) && !Constants.MERGE_TIME.equals(entry.getKey())){
                        update.set(entry.getKey(),new Decimal128(new BigDecimal(entry.getValue())));
                    }
                }
                bulkOperations.upsert(query, update);
            }
        }
        bulkOperations.execute();
    }

    public void updateAntiShakeAll(List<AntiShakeValueVO> antiShakeSaveList) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,Constants.IOT_ANTI_SHAKE_SUM_VALUE);
        //接收需要更新累计设备因子数据
        for (AntiShakeValueVO antiShakeValueVO : antiShakeSaveList) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.PARAM_CONFIG_ID).is(antiShakeValueVO.getConfigId()));
            query.addCriteria(Criteria.where(Constants.PARAM_GROUP_ID).is(antiShakeValueVO.getGroupId()));
            Update update = new Update();
            update.set(Constants.PARAM_CONTINUE_VALUE,antiShakeValueVO.getContinueValue());
            update.set(Constants.PARAM_SUDDEN_VALUE,antiShakeValueVO.getSuddenValue());
            bulkOperations.upsert(query, update);
        }
        bulkOperations.execute();
    }




}
