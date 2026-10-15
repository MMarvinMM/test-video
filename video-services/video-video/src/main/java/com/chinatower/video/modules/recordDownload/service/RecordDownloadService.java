package com.chinatower.video.modules.recordDownload.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.mongodb.request.UpdateEntity;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.recordDownload.mapper.RecordDownloadMapper;
import com.chinatower.video.modules.recordDownload.mongo.RecordDownloadDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecordDownloadService implements IRecordDownloadService{

    @Resource
    RecordDownloadMapper recordDownloadMapper;

    @Resource
    RecordDownloadDAO recordDownloadDAO;

    public static final String MONGO_COLLECTION_NAME_VALUE = "vm_record_download";

    public static final String MONGO_FILED_DOWNLOAD_STATUS = "downloadStatus";
    public static final String MONGO_FILED_CREATE_TIME = "createTime";
    public static final String MONGO_FILED_TENANT_ID = "tenantId";
    public static final String MONGO_FILED_INDUSTRY_CODE = "industryCode";
    public static final String MONGO_FILED_BATCH_ID = "batchId";
    public static final String MONGO_FILED_ID = "id";
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

    public static final String MONGO_ACCESS_NODE = "accessNode";




    @Resource
    private FileUrlEncryptUtil obsFileUrlEncryptDeal;
    @Override
    public void batchSave(List<RecordDownloadDTO> list) {
        recordDownloadDAO.insertAll(list, MONGO_COLLECTION_NAME_VALUE, MongoDBType.PRIMARY);
    }

    @DataScope(tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code")
    @Override
    public Page<RecordDownloadDTO> list(RecordDownloadVO recordDownload) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        recordDownload.setTenantId(user.getTenantId());
        recordDownload.setIndustryCode(user.getIndustryCode());

        //获取分页参数
        Page page = new Page(recordDownload.getPageNum() == 0 ? 0 : recordDownload.getPageNum() -1, recordDownload.getPageSize());
        //获取排序参数
        SortEntity sortEntity = new SortEntity();
        sortEntity.asc(MONGO_FILED_DOWNLOAD_STATUS);
        sortEntity.asc(MONGO_FILED_CREATE_TIME);
        //获取查询参数
        QueryEntity query = new QueryEntity(RecordDownloadDTO.class);
        query.eq(MONGO_FILED_TENANT_ID, user.getTenantId());
        query.eq(MONGO_FILED_INDUSTRY_CODE, user.getIndustryCode());
        query.eq(MONGO_FILED_DELETE_FLAG, MONGO_FILED_DELETE_FLAG_VALUE_0);
        query.eq(MONGO_FILED_OPERATE_SOURCE_TYPE, 1);
        page.setSortEntity(sortEntity);
        Page<RecordDownloadDTO> mongoList = recordDownloadDAO.findPage(query, page, null, MongoDBType.PRIMARY);
        log.info("page:{}", JSON.toJSONString(mongoList));

        List<RecordDownloadDTO> list = mongoList.getList();
        log.info("list:{}", JSON.toJSONString(list));
        if (!list.isEmpty()) {
            ChannelsTenantDTO channelsTenantDTO = new ChannelsTenantDTO();
            channelsTenantDTO.setTenantId(user.getTenantId());
            channelsTenantDTO.setIndustryCode(user.getIndustryCode());

            List<String> channel = new ArrayList<>();
            List<String> fileUrls = new ArrayList<>();

            for (RecordDownloadDTO recordDownloadDTO : list) {
                fileUrls.add(recordDownloadDTO.getLocalUrl());
                channel.add(recordDownloadDTO.getChannelCode());
            }
            channelsTenantDTO.setChannels(channel);

            log.info("****" + channelsTenantDTO);
            //查询设备，通道名称
            List<RecordDownloadDTO> nameList = recordDownloadMapper.findDevChanNameByCode(channelsTenantDTO);
            Map<String, RecordDownloadDTO> channelCodeNameMap = new HashMap<>();
            nameList.forEach(item -> channelCodeNameMap.putIfAbsent(item.getChannelCode(), item));
            list.forEach(item -> {
                log.info("item:{}", JSON.toJSONString(item));
                if (channelCodeNameMap.containsKey(item.getChannelCode())) {
                    item.setChannelName(channelCodeNameMap.get(item.getChannelCode()).getChannelName());
                    item.setDeviceName(channelCodeNameMap.get(item.getChannelCode()).getDeviceName());
                }
            });
        }
        return mongoList;

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
    public void delete(String id) {
        //更新删除标记
        QueryEntity query = new QueryEntity();
        query.eq(MONGO_FILED_ID, id);
        query.put(MONGO_COLLECTION_NAME_KEY, MONGO_COLLECTION_NAME_VALUE);
        query.put(MONGO_ENTITY_CLASS_KEY, RecordDownloadDTO.class);
        UpdateEntity updateEntity = new UpdateEntity();
        updateEntity.set(MONGO_FILED_DELETE_FLAG, MONGO_FILED_DELETE_FLAG_VALUE_1);
        recordDownloadDAO.updateFirst(query, updateEntity, MongoDBType.PRIMARY);
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
                if (null != recordDownloadDTO.getAccessNode()) {
                    updateEntity.set(MONGO_ACCESS_NODE, recordDownloadDTO.getAccessNode());
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
