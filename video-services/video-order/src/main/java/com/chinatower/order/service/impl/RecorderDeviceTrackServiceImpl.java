package com.chinatower.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.order.util.MongoHashUtil;
import com.chinatower.order.util.PasswordUtil;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.order.RecorderDeviceTrackDTO;
import com.chinatower.order.mongodb.RecorderDeviceTrackMongoDAO;
import com.chinatower.order.service.IRecorderDeviceTrackService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 *
 */
@Service
@Slf4j
public class RecorderDeviceTrackServiceImpl implements IRecorderDeviceTrackService {

    @Resource
    private RecorderDeviceTrackMongoDAO recorderDeviceTrackMongoDAO;

    @Autowired
    private GisMapApi gisMapApi;

    @Resource
    private MongoHashUtil mongoHashUtil;

    @Resource
    private SystemFeignClient systemFeignClient;

    /**
     * 无人遥控上报信息处理
     * @param param 遥控报文
     * @return
     */
    @Override
    public Result receiveRecorderTrack(RecorderDeviceTrackDTO param) {
        List<RecorderDeviceTrackDTO> saveList = new ArrayList<>();
        //获取分月集合名
        String dateYM = DateUtils.dateToStringYM(param.getTime());
        String tableRecorderDevice = Constants.TABLE_NAME_RECORDER_DEVICE_TRACK + dateYM;
        saveList.add(param);
        //无人机轨迹表,按月分表
        recorderDeviceTrackMongoDAO.insertAll(saveList, tableRecorderDevice, MongoDBType.PRIMARY);
        //先检查索引是否已存在
        boolean indexExists = checkListIndexs(tableRecorderDevice, MongoDBType.PRIMARY);
        if (!indexExists) {
            try {
                List<IndexModel> indexModels = getIndexModels();
                recorderDeviceTrackMongoDAO.mongoCreateIndexs(tableRecorderDevice, MongoDBType.PRIMARY, indexModels);
            } catch (Exception e) {
                log.error("创建索引异常", e);
            }
        }

        boolean isCenter = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(PasswordUtil.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (StringUtils.equals(sysConfig.getConfigValue(), com.chinatower.platform.constants.Constants.ONE)) {
                isCenter = true;
            }
        }

        // 只有中心节点才处理分片
        if (isCenter) {
            // 创建hash索引
            mongoHashUtil.createHashedIndexIfNeed(tableRecorderDevice, "deviceCode", "idx_deviceCode_hash");
            // 处理分片
            mongoHashUtil.shardCollectionIfNeed(tableRecorderDevice, "deviceCode");
        }

        return Result.success("保存轨迹信息成功!");
    }

    /**
     * 校验是否存在索引
     * @param tableName
     * @param primary
     * @return
     */
    private boolean checkListIndexs(String tableName, MongoDBType primary) {
        boolean success = false;
        try {
            ListIndexesIterable<Document> indexList = recorderDeviceTrackMongoDAO.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    log.info("checkListIndexs keyDocument:{}", JSON.toJSONString(keyDocument));
                    if (keyDocument.containsKey(Constants.INDEXFIELD_UAV_DEVICECODE)) {
                        success = true;
                    }
                }
            }
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    /**
     * 构建需要创建索引的请求参数
     * @return
     */
    private List<IndexModel> getIndexModels() {
        List<IndexModel> indexModels = new ArrayList<>();
        BasicDBObject index = new BasicDBObject();
        index.put(Constants.INDEXFIELD_UAV_DEVICECODE, 1);
        index.put(Constants.INDEXFIELD_TIME, 1);
        IndexOptions indexOptions = new IndexOptions().background(true).name(Constants.RECORDER_INDEX_NAME);
        indexModels.add(new IndexModel(index, indexOptions));
        return indexModels;
    }

    /**
     * 查询执法记录仪轨迹信息
     * @param param
     * @return
     */
    @Override
    public Result<List<RecorderDeviceTrackDTO>> getRecorderDeviceTrackList(UavDeviceTrackParam param) {
        if (StringUtils.isBlank(param.getEndTime())) {
            param.setEndTime(DateUtils.getTime());
        }
        Date beginTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, param.getBeginTime());
        Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, param.getEndTime());
        int days = (int) ((endTime.getTime() - beginTime.getTime()) / (1000 * 3600 * 24));
        //查询轨迹最多跨越一天
        if (days > Constants.ONE_NUM) {
            return Result.fail("查询失败,查询时间最大跨度为1天！");
        }
        String qryBegin = DateUtils.dateToStringYM(beginTime);
        String qryEnd = DateUtils.dateToStringYM(endTime);
        String beginTableRecorderDevice = Constants.TABLE_NAME_RECORDER_DEVICE_TRACK + qryBegin;
        List<RecorderDeviceTrackDTO> dataList = new ArrayList<>();
        dataList.addAll(recorderDeviceTrackMongoDAO.findList(beginTime, endTime, param.getDeviceCode(), beginTableRecorderDevice));
        //如果存在跨月，还需要查询结束时间那个月的数据，目前只让最多跨越一天，不考虑跨多月情况
        if (!qryBegin.equals(qryEnd)) {
            String endTableRecorderDevice = Constants.TABLE_NAME_RECORDER_DEVICE_TRACK + qryEnd;
            dataList.addAll(recorderDeviceTrackMongoDAO.findList(beginTime, endTime, param.getDeviceCode(), endTableRecorderDevice));
        }
        // 抽稀逻辑
        return trackDilutionLogic(dataList);
    }

    public Result<List<RecorderDeviceTrackDTO>> trackDilutionLogic(List<RecorderDeviceTrackDTO> paramDataList) {
        try {
            // 结果集
            List<RecorderDeviceTrackDTO> resultList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(paramDataList)) {
                Map<String, RecorderDeviceTrackDTO> longLatMap = Maps.newHashMap();
                JSONArray jsonArray = new JSONArray();
                for (RecorderDeviceTrackDTO trackData : paramDataList) {
                    JSONArray trackJson = new JSONArray();
                    trackJson.add(Double.valueOf(trackData.getLongitude()));
                    trackJson.add(Double.valueOf(trackData.getLatitude()));
                    jsonArray.add(trackJson);

                    String lng6 = new BigDecimal(trackData.getLongitude()).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                    String lat6 = new BigDecimal(trackData.getLatitude()).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                    String mapKey = String.format("%s-%s", lng6, lat6);;

                    if (!longLatMap.containsKey(mapKey)) {
                        longLatMap.put(mapKey, trackData);
                    }
                }
                log.info("jsonArray:{} longLatMap:{}", JSONObject.toJSONString(jsonArray), JSON.toJSONString(longLatMap));
                //调pg数据库 对数据进行抽稀
                String newRangeStr =  "{ \"type\": \"LineString\", \"coordinates\": " + JSONObject.toJSONString(jsonArray)  + "}";
                log.info("newRangeStr:{} jsonArraySize:{}", newRangeStr, jsonArray.size());
                Result<String> gisResult = gisMapApi.gridCompress(newRangeStr);
                log.info("gisResult : {}", JSON.toJSONString(gisResult));
                if(gisResult.getCode() == 200){

                    String resultDataStr = gisResult.getData();
                    if (StringUtils.isNotBlank(resultDataStr)) {
                        cn.hutool.json.JSONObject gisResultData = new cn.hutool.json.JSONObject(resultDataStr);
                        cn.hutool.json.JSONArray rangeArray = gisResultData.getJSONArray("coordinates");
                        if (rangeArray != null && rangeArray.size() > 0) {
                            for (Object o : rangeArray) {
                                cn.hutool.json.JSONArray longLatArray = (cn.hutool.json.JSONArray) o;
                                String longitude = String.valueOf(longLatArray.get(0));
                                String latitude = String.valueOf(longLatArray.get(1));
                                String resultKey = String.format("%s-%s", longitude, latitude);
                                if (longLatMap.containsKey(resultKey)) {
                                    resultList.add(longLatMap.get(resultKey));
                                }
                            }
                        }
                    }
                }
            }
            // 如果抽稀结果为空，还使用抽稀前的轨迹信息
            if (CollectionUtils.isEmpty(resultList)) {
                resultList = paramDataList;
            }
            return Result.data(resultList);
        } catch (Exception e) {
            log.error("RecorderDeviceTrackServiceImpl trackDilutionLogic error:", e);
            return Result.data(paramDataList);
        }
    }

    public static void main(String[] args) {
//        List<RecorderDeviceTrackDTO> resultList = Lists.newArrayList();
//        RecorderDeviceTrackDTO data1 = new RecorderDeviceTrackDTO();
//        data1.setLongitude("116.293420000");
//        data1.setLatitude("39.923521000");
//        RecorderDeviceTrackDTO data2 = new RecorderDeviceTrackDTO();
//        data2.setLongitude("116.393420000");
//        data2.setLatitude("39.823521000");
//        RecorderDeviceTrackDTO data3 = new RecorderDeviceTrackDTO();
//        data3.setLongitude("116.493420000");
//        data3.setLatitude("39.723521000");
//        RecorderDeviceTrackDTO data4 = new RecorderDeviceTrackDTO();
//        data4.setLongitude("116.593420000");
//        data4.setLatitude("39.623521000");
//        resultList.add(data1);
//        resultList.add(data2);
//        resultList.add(data3);
//        resultList.add(data4);
//        JSONArray jsonArray = new JSONArray();
//        for (RecorderDeviceTrackDTO trackData : resultList) {
//            JSONArray trackJson = new JSONArray();
//            trackJson.add(Double.valueOf(trackData.getLongitude()));
//            trackJson.add(Double.valueOf(trackData.getLatitude()));
//            jsonArray.add(trackJson);
//        }
//        log.info("jsonArray:"+ JSONObject.toJSONString(jsonArray));

        String str = "{\"code\":200,\"data\":\"{\\\"type\\\":\\\"LineString\\\",\\\"coordinates\\\":[[113.372023,23.101795],[113.37212,23.101805],[113.372022,23.101802],[113.372022,23.121802],[113.372022,23.101802],[113.371932,23.101756],[113.372021,23.101795],[113.371912,23.101766],[113.372017,23.101827],[113.371912,23.10179],[113.372009,23.101803],[113.37209,23.10186],[113.372008,23.101797]]}\",\"msg\":\"请求成功\",\"time\":1715844380209}";
        Result<String> gisResult = JSON.parseObject(str, Result.class);
        log.info("gisResult : {}", JSON.toJSONString(gisResult));
        if(gisResult.getCode() == 200){
            String resultDataStr = gisResult.getData();
            cn.hutool.json.JSONObject resultData = new cn.hutool.json.JSONObject(resultDataStr);
            cn.hutool.json.JSONArray rangeArray = resultData.getJSONArray("coordinates");
            for (int i = Constants.ZERO_NUM; i < rangeArray.size(); i++) {
                cn.hutool.json.JSONArray longLatArray = (cn.hutool.json.JSONArray) rangeArray.get(i);
                System.out.println(longLatArray.get(0));
                System.out.println(longLatArray.get(1));
                System.out.println("###########################");
            }
        }
    }

}
