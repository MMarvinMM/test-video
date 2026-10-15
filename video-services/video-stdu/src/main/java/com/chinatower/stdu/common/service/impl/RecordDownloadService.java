package com.chinatower.stdu.common.service.impl;

import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.UpdateEntity;
import com.chinatower.stdu.common.mongo.RecordDownloadDAO;
import com.chinatower.stdu.common.service.IRecordDownloadService;
import com.chinatower.video.api.entity.RecordDownloadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecordDownloadService implements IRecordDownloadService {


    @Resource
    RecordDownloadDAO recordDownloadDAO;

    public static final String MONGO_COLLECTION_NAME_VALUE = "vm_record_download";
    public static final String MONGO_FILED_DOWNLOAD_STATUS = "downloadStatus";
    public static final String MONGO_FILED_BATCH_ID = "batchId";
    public static final String MONGO_FILED_DELETE_FLAG = "deleteFlag";
    public static final String MONGO_FILED_DELETE_FLAG_VALUE_1 = "1";
    public static final String MONGO_FILED_DELETE_FLAG_VALUE_0 = "0";
    public static final String MONGO_FILED_FILE_SIZE = "fileSize";
    public static final String MONGO_FILED_FILE_NAME = "fileName";
    public static final String MONGO_FILED_ORDER = "order";
    public static final String MONGO_FILED_LOCAL_URL = "localUrl";
    public static final String MONGO_FILED_MODIFIER = "modifier";
    public static final String MONGO_FILED_UPDATE_TIME = "updateTime";
    public static final String MONGO_FILED_OPERATE_SOURCE_TYPE = "operateSourceType";
    public static final String MONGO_COLLECTION_NAME_KEY = "collectionName";
    public static final String MONGO_ENTITY_CLASS_KEY = "entityClass";


    @Override
    public void batchSave(List<RecordDownloadDTO> list) {
        recordDownloadDAO.insertAll(list, MONGO_COLLECTION_NAME_VALUE, MongoDBType.PRIMARY);
    }

    @Override
    public List<String> existDownloadFinishByBatchIds(List<RecordDownloadDTO> recordDownloadList) {
        Object[] arrayObj = recordDownloadList.stream().map(RecordDownloadDTO::getBatchId).toArray();
        String[] array = Arrays.copyOf(arrayObj, arrayObj.length, String[].class);
        QueryEntity query = new QueryEntity();
        query.in(MONGO_FILED_BATCH_ID, array);
        query.eq(MONGO_FILED_DELETE_FLAG, MONGO_FILED_DELETE_FLAG_VALUE_0);
        query.eq(MONGO_FILED_DOWNLOAD_STATUS, 1);
        query.put(MONGO_COLLECTION_NAME_KEY, MONGO_COLLECTION_NAME_VALUE);
        query.put(MONGO_ENTITY_CLASS_KEY, RecordDownloadDTO.class);
        List<RecordDownloadDTO> list = recordDownloadDAO.findList(query, null, MongoDBType.PRIMARY);
        return list.stream().map(RecordDownloadDTO::getBatchId).collect(Collectors.toList());
    }

    @Override
    public void batchUpdate(List<RecordDownloadDTO> recordDownloadList) {
        //更新删除标记
        if (!CollectionUtils.isEmpty(recordDownloadList)) {
            List<Pair<Query, Update>> pairList = new ArrayList<>();
            for (RecordDownloadDTO recordDownloadDTO : recordDownloadList) {
                Query updateQuery = Query.query(Criteria.where(MONGO_FILED_BATCH_ID).is(recordDownloadDTO.getBatchId()));

                Update updateEntity = new Update();
                if (null != recordDownloadDTO.getDownloadStatus()) {
                    updateEntity.set(MONGO_FILED_DOWNLOAD_STATUS, recordDownloadDTO.getDownloadStatus());
                }
                if (null != recordDownloadDTO.getFileSize()) {
                    updateEntity.set(MONGO_FILED_FILE_SIZE, recordDownloadDTO.getFileSize());
                }
                if (null != recordDownloadDTO.getFileName()) {
                    updateEntity.set(MONGO_FILED_FILE_NAME, recordDownloadDTO.getFileName());
                }
                if (null != recordDownloadDTO.getOrder()) {
                    updateEntity.set(MONGO_FILED_ORDER, recordDownloadDTO.getOrder());
                }
                if (null != recordDownloadDTO.getLocalUrl()) {
                    updateEntity.set(MONGO_FILED_LOCAL_URL, recordDownloadDTO.getLocalUrl());
                }
                if (null != recordDownloadDTO.getModifier()) {
                    updateEntity.set(MONGO_FILED_MODIFIER, recordDownloadDTO.getModifier());
                }
                if (null != recordDownloadDTO.getUpdateTime()) {
                    updateEntity.set(MONGO_FILED_UPDATE_TIME, recordDownloadDTO.getUpdateTime());
                }

                Pair<Query, Update> updatePair = Pair.of(updateQuery, updateEntity);
                pairList.add(updatePair);
            }
            recordDownloadDAO.batchUpdateOrdered(pairList, RecordDownloadDTO.class, MongoDBType.PRIMARY);
        }
    }

    @Override
    public void batchDelete(List<String> deleteList) {

        //更新删除标记
        QueryEntity query = new QueryEntity();
        Object[] objArr = deleteList.toArray();
        String[] array = Arrays.copyOf(objArr, objArr.length, String[].class);
        query.in(MONGO_FILED_BATCH_ID, array);
        query.eq(MONGO_FILED_DELETE_FLAG, MONGO_FILED_DELETE_FLAG_VALUE_0);
        query.eq(MONGO_FILED_OPERATE_SOURCE_TYPE, 1);
        query.put(MONGO_COLLECTION_NAME_KEY, MONGO_COLLECTION_NAME_VALUE);
        query.put(MONGO_ENTITY_CLASS_KEY, RecordDownloadDTO.class);

        UpdateEntity updateEntity = new UpdateEntity();
        updateEntity.set(MONGO_FILED_DELETE_FLAG, MONGO_FILED_DELETE_FLAG_VALUE_1);
        recordDownloadDAO.updateMulti(query, updateEntity, MongoDBType.PRIMARY);
    }

    @Override
    public void batchUpdateDownLoadFail(List<String> list) {

        QueryEntity query = new QueryEntity();
        Object[] objArr = list.toArray();
        String[] array = Arrays.copyOf(objArr, objArr.length, String[].class);
        query.in(MONGO_FILED_BATCH_ID, array);
        query.eq(MONGO_FILED_DELETE_FLAG, MONGO_FILED_DELETE_FLAG_VALUE_0);
        query.put(MONGO_COLLECTION_NAME_KEY, MONGO_COLLECTION_NAME_VALUE);
        query.put(MONGO_ENTITY_CLASS_KEY, RecordDownloadDTO.class);

        UpdateEntity updateEntity = new UpdateEntity();
        updateEntity.set(MONGO_FILED_DOWNLOAD_STATUS, 2);
        recordDownloadDAO.updateMulti(query, updateEntity, MongoDBType.PRIMARY);
    }
}
