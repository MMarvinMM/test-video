package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.outdata.api.entity.DeviceInfoMongoDO;
import com.chinatower.outdata.api.entity.DeviceInfoMongoKafka;
import com.chinatower.outdata.api.entity.DeviceModelMongoDO;
import com.chinatower.outdata.api.entity.DeviceModelMongoKafka;
import com.chinatower.platform.consumer.dao.DeviceInfoMongoDAO;
import com.chinatower.platform.consumer.dao.DeviceModelMongoDAO;
import com.chinatower.platform.consumer.dao.VmOrderSensorDevicesDAO;
import com.chinatower.platform.consumer.item.Constants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@Slf4j
public class VmDeviceSyncService {
    @Resource
    DeviceInfoMongoDAO deviceInfoMongoDAO;

    @Resource
    DeviceModelMongoDAO deviceModelMongoDAO;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    VmOrderSensorDevicesDAO vmOrderSensorDevicesDAO;


    @Transactional
    public void vmDeviceInfoSync(List<String> messages) {
        if(!CollectionUtils.isEmpty(messages)){
            //新增集合
            List<DeviceInfoMongoDO> saveList = new ArrayList<>();

            //更新集合
            List<DeviceInfoMongoDO> updateList = new ArrayList<>();

            //删除集合
            List<DeviceInfoMongoDO> deleteList = new ArrayList<>();

            for (String message : messages) {
                log.info("vmDeviceInfoSync消费的message为:{}",message);
                try {
                    DeviceInfoMongoKafka infoVO = JSON.parseObject(message, DeviceInfoMongoKafka.class);
                    if(ObjectUtils.isEmpty(infoVO)){
                        throw new Exception();
                    }
                    if(Constants.INSERT.equals(infoVO.getType())){
                        saveList.addAll(infoVO.getData());
                    }

                    if(Constants.UPDATE.equals(infoVO.getType())){
                        updateList.addAll(infoVO.getData());
                    }

                    if(Constants.DELETE.equals(infoVO.getType())){
                        deleteList.addAll(infoVO.getData());
                    }
                }catch (Exception e){
                    log.error("vmDeviceInfoSync请求报文{}，异常信息：{}", message, e);
                }
            }

            if(!CollectionUtils.isEmpty(saveList)){
                List<Long> deviceIds = saveList.stream().map(DeviceInfoMongoDO::getDeviceId).collect(Collectors.toList());
                Query query = new Query(where(Constants.DEVICEID).in(deviceIds));
                List<DeviceInfoMongoDO> mongoList = mongoTemplate.find(query, DeviceInfoMongoDO.class, Constants.VM_DEVICE_INFO_T);
                //mongo中已存在，先删除
                if(!CollectionUtils.isEmpty(mongoList)){
                    this.batchDeleteByDeviceCode(saveList);
                }
                deviceInfoMongoDAO.insertAll(saveList, Constants.VM_DEVICE_INFO_T, MongoDBType.PRIMARY);
            }
            if(!CollectionUtils.isEmpty(updateList)){
                this.batchDeleteByDeviceCode(updateList);
                deviceInfoMongoDAO.insertAll(updateList, Constants.VM_DEVICE_INFO_T, MongoDBType.PRIMARY);
            }
            if(!CollectionUtils.isEmpty(deleteList)){
                this.batchDeleteByDeviceCode(deleteList);
            }
        }
    }

    private void batchDeleteByDeviceCode(List<DeviceInfoMongoDO> deleteList){
        List<Long> collect = deleteList.stream().map(DeviceInfoMongoDO::getDeviceId).collect(Collectors.toList());
        Query deleteQuery = new Query(where(Constants.DEVICEID).in(collect));
        mongoTemplate.remove(deleteQuery, DeviceInfoMongoDO.class);
    }

    @Transactional
    public void vmDeviceModelInfoSync(List<String> messages) {
        if(!CollectionUtils.isEmpty(messages)){
            //新增集合
            List<DeviceModelMongoDO> saveList = new ArrayList<>();

            //更新集合
            List<DeviceModelMongoDO> updateList = new ArrayList<>();

            //删除集合
            List<DeviceModelMongoDO> deleteList = new ArrayList<>();

            for (String message : messages) {
                log.info("vmDeviceModelInfoSync消费的message为:{}",message);
                try {
                    DeviceModelMongoKafka infoVO = JSON.parseObject(message, DeviceModelMongoKafka.class);
                    if(ObjectUtils.isEmpty(infoVO)){
                        throw new Exception();
                    }
                    if(Constants.INSERT.equals(infoVO.getType())){
                        saveList.addAll(infoVO.getData());
                    }

                    if(Constants.UPDATE.equals(infoVO.getType())){
                        updateList.addAll(infoVO.getData());
                    }

                    if(Constants.DELETE.equals(infoVO.getType())){
                        deleteList.addAll(infoVO.getData());
                    }
                }catch (Exception e){
                    log.error("vmDeviceModelInfoSync请求报文{}，异常信息：{}", message, e);
                }
            }

            if(!CollectionUtils.isEmpty(saveList)){
                List<Integer> modelIds = saveList.stream().map(DeviceModelMongoDO::getModelId).collect(Collectors.toList());
                Query query = new Query(where(Constants.MODELID).in(modelIds));
                List<DeviceModelMongoDO> mongoList = mongoTemplate.find(query, DeviceModelMongoDO.class, Constants.VM_DEVICE_MODEL_T);
                //mongo中已存在，先删除
                if(!CollectionUtils.isEmpty(mongoList)){
                    this.batchDeleteByModelId(saveList);
                }

                deviceModelMongoDAO.insertAll(saveList, Constants.VM_DEVICE_MODEL_T, MongoDBType.PRIMARY);
            }
            if(!CollectionUtils.isEmpty(updateList)){
                this.batchDeleteByModelId(updateList);
                deviceModelMongoDAO.insertAll(updateList, Constants.VM_DEVICE_MODEL_T, MongoDBType.PRIMARY);
            }
            if(!CollectionUtils.isEmpty(deleteList)){
                this.batchDeleteByModelId(deleteList);
            }
        }
    }

    private void batchDeleteByModelId(List<DeviceModelMongoDO> deleteList){
        List<Integer> collect = deleteList.stream().map(DeviceModelMongoDO::getModelId).collect(Collectors.toList());
        Query deleteQuery = new Query(where(Constants.MODELID).in(collect));
        mongoTemplate.remove(deleteQuery, DeviceModelMongoDO.class);
    }

    @Transactional
    public Result<String> initMongoDeviceData() {
        int ONE_THOUSAND = 1000;
        List<DeviceInfoMongoDO> deivceInfoList = vmOrderSensorDevicesDAO.getDeviceInfoList();
        if(!CollectionUtils.isEmpty(deivceInfoList)){
            Query deleteQuery = new Query();
            mongoTemplate.remove(deleteQuery, DeviceInfoMongoDO.class);

            List<List<DeviceInfoMongoDO>> partitionList = Lists.partition(deivceInfoList,ONE_THOUSAND);
            partitionList.forEach(item ->
                    deviceInfoMongoDAO.insertAll(item, Constants.VM_DEVICE_INFO_T, MongoDBType.PRIMARY)
            );
        }


        List<DeviceModelMongoDO> deviceModelInfoList = vmOrderSensorDevicesDAO.getDeviceModelInfoList();
        if(!CollectionUtils.isEmpty(deviceModelInfoList)){
            Query deleteQuery = new Query();
            mongoTemplate.remove(deleteQuery, DeviceModelMongoDO.class);

            List<List<DeviceModelMongoDO>> partitionList = Lists.partition(deviceModelInfoList,ONE_THOUSAND);
            partitionList.forEach(item ->
                    deviceModelMongoDAO.insertAll(item, Constants.VM_DEVICE_MODEL_T, MongoDBType.PRIMARY)
            );
        }
        return Result.success("初始化成功" + (deivceInfoList.size() + deviceModelInfoList.size()) + "条");
    }

}
