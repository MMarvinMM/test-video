package com.chinatower.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.order.mongodb.UavDeviceTrackMongoDAO;
import com.chinatower.order.service.IUavDeviceTrackService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.order.dto.UavDeviceTrackDTO;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;
import com.chinatower.order.vo.UavDeviceSyncVO;
import com.chinatower.platform.entity.order.UavDeviceTrackVO;
import com.google.common.base.Stopwatch;
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
import java.util.concurrent.TimeUnit;

/**
 * @ClassName UavDeviceTrackServiceImpl
 * @Deacription 无人机遥控上报信息处理类
 * @Author luo.zhenghong
 * @Date 2022/6/15 14:46
 * @Version release_v1.5.95
 **/
@Service
@Slf4j
public class UavDeviceTrackServiceImpl implements IUavDeviceTrackService {

    @Resource
    private UavDeviceTrackMongoDAO uavDeviceTrackMongoDAO;
    @Autowired
    private GisMapApi gisMapApi;

    /**
     * 无人遥控上报信息处理
     * @param param 遥控报文
     * @return
     */
    @Override
    public Result receiveUavTrack(UavDeviceSyncVO param) {
        List<UavDeviceTrackDTO> saveList = new ArrayList<>();
        //要存无人机轨迹信息
        UavDeviceTrackDTO saveTrackDTO = param.getData();
        Date trackTime = new Date(param.getTimestamp());
        //获取分月集合名
        String dateYM = DateUtils.dateToStringYM(trackTime);
        String tableUavDevice = Constants.TABLE_NAME_UAV_DEVICE_TRACK + dateYM;
        //无人机设备编码
        saveTrackDTO.setDeviceCode(param.getDeviceCode());
        //无人机轨迹上报时间点
        saveTrackDTO.setTrackTime(trackTime);
        saveTrackDTO.setCreateDate(DateUtils.getNowDate());
        saveList.add(saveTrackDTO);
        //无人机轨迹表,按月分表
        uavDeviceTrackMongoDAO.insertAll(saveList, tableUavDevice, MongoDBType.PRIMARY);
        //先检查索引是否已存在
        boolean indexExists = checkListIndexs(tableUavDevice, MongoDBType.PRIMARY);
        if (!indexExists) {
            try {
                List<IndexModel> indexModels = getIndexModels();
                uavDeviceTrackMongoDAO.mongoCreateIndexs(tableUavDevice, MongoDBType.PRIMARY, indexModels);
            } catch (Exception e) {
                log.error("创建索引异常", e);
            }
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
            ListIndexesIterable<Document> indexList = uavDeviceTrackMongoDAO.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
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
        index.put(Constants.INDEXFIELD_UAV_TRACKTIME, 1);
        IndexOptions indexOptions = new IndexOptions().background(true).name(Constants.UAV_INDEX_NAME);
        indexModels.add(new IndexModel(index, indexOptions));
        return indexModels;
    }

    /**
     * 查询无人机轨迹信息
     * @param param
     * @return
     */
    public Result<List<UavDeviceTrackVO>> qryUavDeviceTrackList(UavDeviceTrackParam param) {
        Stopwatch sw = Stopwatch.createStarted();
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
        log.info("qryUavDeviceTrackList step1 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        String qryBegin = DateUtils.dateToStringYM(beginTime);
        String qryEnd = DateUtils.dateToStringYM(endTime);
        String beginTableUavDevice = Constants.TABLE_NAME_UAV_DEVICE_TRACK + qryBegin;
        List<UavDeviceTrackVO> dataList = new ArrayList<>();
        dataList.addAll(uavDeviceTrackMongoDAO.findList(beginTime, endTime, param.getDeviceCode(), beginTableUavDevice));
        //如果存在跨月，还需要查询结束时间那个月的数据，目前只让最多跨越一天，不考虑跨多月情况
        if (!qryBegin.equals(qryEnd)) {
            String endTableUavDevice = Constants.TABLE_NAME_UAV_DEVICE_TRACK + qryEnd;
            dataList.addAll(uavDeviceTrackMongoDAO.findList(beginTime, endTime, param.getDeviceCode(), endTableUavDevice));
        }
        log.info("qryUavDeviceTrackList step2 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        // 抽稀逻辑
        Result<List<UavDeviceTrackVO>> result = trackDilutionLogic(dataList);
        log.info("qryUavDeviceTrackList step3 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    public Result<List<UavDeviceTrackVO>> trackDilutionLogic(List<UavDeviceTrackVO> paramDataList) {
        try {
            // 结果集
            List<UavDeviceTrackVO> resultList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(paramDataList)) {
                Map<String, UavDeviceTrackVO> longLatMap = Maps.newHashMap();
                JSONArray jsonArray = new JSONArray();
                for (UavDeviceTrackVO trackData : paramDataList) {
                    JSONArray trackJson = new JSONArray();
                    trackJson.add(Double.valueOf(trackData.getLng()));
                    trackJson.add(Double.valueOf(trackData.getLat()));
                    jsonArray.add(trackJson);

                    String lng6 = new BigDecimal(trackData.getLng()).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                    String lat6 = new BigDecimal(trackData.getLat()).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
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
            log.error("UavDeviceTrackServiceImpl trackDilutionLogic error:", e);
            return Result.data(paramDataList);
        }
    }

}
