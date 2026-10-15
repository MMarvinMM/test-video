package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.IReceiveAlarmService;
import com.chinatower.event.api.IReceiveApiService;
import com.chinatower.event.api.ITowerEventWarningService;
import com.chinatower.event.asyncTask.AsyncCommon;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.core.alarm.util.subscenes.SubScenesWriteHelper;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.mapper.AlarmOperateMapper;
import com.chinatower.event.mapper.WarningInterceptMapper;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.mapper.WarningOriginalPointMapper;
import com.chinatower.event.mongodb.InterceptMongoMapper;
import com.chinatower.event.mongodb.OperateMongoMapper;
import com.chinatower.event.mongodb.ReceiveAlarmPointMapper;
import com.chinatower.event.repository.IReceiveRepository;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.repository.PushThirdService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.model.dto.CascadeDto;
import com.chinatower.platform.utils.CascadeConnectUtils;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 批量处理保存表操作
 * @Author guyuefei
 * @Date 2022/7/19
 * @Version release_v1.5
 **/
@Component("batchSaveHandler")
@Scope("prototype")
@Slf4j
public class BatchSaveHandler extends AbstractHandler {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;
    @Autowired
    private WarningOrderUtil warningOrderUtil;
    @Autowired
    private IReceiveRepository receiveRepository;
    @Autowired
    private WarningOrderMapper warningOrderMapper;
    @Autowired
    private WarningInterceptMapper warningInterceptMapper;
    @Autowired
    private AlarmOperateMapper alarmOperateMapper;
    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private WarningOriginalPointMapper warningOriginalPointMapper;

    @Autowired
    private PushThirdService pushThirdService;

    @Autowired
    private ReceiveAlarmPointMapper receiveAlarmPointMapper;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    @Autowired
    private CascadeConnectUtils cascadeConnectUtils;

    @Autowired
    private AsyncCommon asyncCommon;

    /**
     * 执行逻辑
     *
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    @Transactional(timeout = 300)
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("批量保存的研判信息：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            String originalJson = JSON.toJSONString(receiveAlarmDTO.getList());
            String alarmTime = receiveAlarmDTO.getReceiveAlarmVO().getAlarmTime();
            List<ReceiveAlarmVO> alarms = receiveAlarmDTO.getList();
            List<ReceiveAlarmVO> operations = receiveAlarmDTO.getOperations();
            List<ReceiveAlarmVO> intercepts = receiveAlarmDTO.getIntercepts();

            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

            List<AlarmOriginalPointVO> alarmPointList = new ArrayList<>();

            // 批量保存告警信息
            if (!CollectionUtils.isEmpty(alarms)) {
                    log.info("批量保存告警信息入参：{}", JSON.toJSONString(alarms));
                // 创建流程
                List<Map> recordList = warningOrderRepository.createRecord(alarms);
                // 回填当前环节相关字段
                warningOrderRepository.setWarningLinkInfo(alarms, recordList);
                alarms.forEach(item -> {
                    if(!WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(item.getAlarmSource())){
                        setConfidenceLevelAndAddAlarmPoint(item,alarmPointList);
                    }
                });
                //是否有二次识别 默认否
                warningOrderUtil.setSecondConfig(alarms);
                receiveAlarmVODB.setList(alarms);
                int i = warningOrderMapper.insertWarningOrders(receiveAlarmVODB);
                //异步推送第三方
                try{
                    pushThirdService.pushThirdOrder(originalJson,alarms);
                }catch (Exception e){
                    log.error("人工上报推送第三方异常:",e);
                }
                if (i > 0) {
                    // 20211223 zzh 将最新告警时间保存到缓存中，用于压制判断
                    receiveRepository.setAlarmTimeToRedis(alarms);
                }
                //异步查询二次识别配置 判断是否二次识别任务下发
                asyncCommon.secondRecogIfExecuteByRadar(alarms,"2");
            }
            // 批量保存运营研判数据
            if (!CollectionUtils.isEmpty(operations)) {
                log.info("批量保存运营研判数据信息入参：{}", JSON.toJSONString(operations));
                operations.forEach(item -> {
                    setConfidenceLevelAndAddAlarmPoint(item,alarmPointList);
                    if(!StringUtils.isEmpty(item.getOperationNode()) && !item.getOperationNode().contains(item.getAlarmNode())){
                        //将此运营研判数据推同步送到指定受理节点
                        asyncCommon.sync(item);
                        /*log.info("推送属地运营研判开始:{}",JSON.toJSONString(item));
                        try {
                        Result result = RemoteNodeUtils.create(item.getOperationNode())
                                .getFeign(IReceiveApiService.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                                .syncAlarm(item);
                        log.info("推送属地运营研判结束:{}",JSON.toJSONString(result));

                            *//*CascadeDto cascadeDto = new CascadeDto();
                            cascadeDto.setFromPlatformCode(item.getAlarmNode());
                            cascadeDto.setToPlatformCode(item.getOperationNode());
                            cascadeDto.setPath("/api/event/alarm/receive/syncAlarm");
                            cascadeDto.setAppId("general");
                            cascadeDto.setMethod("POST");
                            cascadeDto.setBody(JSON.toJSONString(item));
                            cascadeDto.setUri("/gw/proxy");

                            //给级联网关发送请求
                            cascadeConnectUtils.sendToCascade(cascadeDto);*//*
                        }catch (Exception e){
                            log.error("推送属地运营研判开始异常:{}",e);
                        }*/
                    }
                });
                //是否有二次识别 默认否
                warningOrderUtil.setSecondConfig(operations);
                operations.forEach(SubScenesWriteHelper::fillSubScenes);
                int j = alarmOperateMapper.insertAlarmOperates(operations);
                try{
                    operateMongoMapper.insertOperate(operations,operations.get(0).getAlarmTime());
                }catch (Exception e){
                    log.info("运营研判插入mongo失败:",e);
                }

                if(j>0){
                    // 进入研判的告警也当作正常接收了告警，用于压制判断
                    receiveRepository.setAlarmTimeToRedis(operations);
                }
                //异步查询二次识别配置 判断是否二次识别任务下发
                asyncCommon.secondRecogIfExecuteByRadar(operations,"1");
            }

            //批量保存压制数据
            if (!CollectionUtils.isEmpty(intercepts)) {
                log.info("批量保存压制数据数据信息入参：{}", JSON.toJSONString(intercepts));
                intercepts.forEach(item -> {
                    setConfidenceLevelAndAddAlarmPoint(item,alarmPointList);
                });
                //是否有二次识别 默认否
                warningOrderUtil.setSecondConfig(intercepts);
                warningInterceptMapper.insertInterceptAlarm(intercepts);
                try{
                    interceptMongoMapper.insertIntercept(intercepts,alarmTime);
                }catch (Exception e){
                    log.info("回收站插入mongo失败:",e);
                }
                //异步查询二次识别配置 判断是否二次识别任务下发
                asyncCommon.secondRecogIfExecuteByRadar(intercepts,"3");
            }

            //add by liuxt 20211021 start
            // 保存图片和视频 报警区域 及 告警点列表
            if (!CollectionUtils.isEmpty(alarmPointList)) {
                //告警框图的点位数据入库mongo
                log.info("告警框图的点位数据入库mongo参数：{}", JSON.toJSONString(alarmPointList));
                this.savealarmPointMongo(alarmPointList);
            }

        }catch(Exception e){
            log.info("批量保存的信息异常：", e );
        }

        return this.excuteNextHandler(receiveAlarmDTO);
    }

    /**
     * 告警点位数据入mongo
     */
    public void savealarmPointMongo(List<AlarmOriginalPointVO> alarmPointLists){
        try{
            String yearMonth = alarmPointLists.get(0).getWarningOrderId().substring(0,6);
            String collectionName = TableConstant.WARN_ORIGINAL_POINT_TABLE + yearMonth;
            Boolean exists = receiveAlarmPointMapper.collectionExists(collectionName, MongoDBType.PRIMARY);
            if (!exists) {
                receiveAlarmPointMapper.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
            }
            //告警点位数据入mongo
            receiveAlarmPointMapper.insertAll(alarmPointLists,collectionName,MongoDBType.PRIMARY);
            //先检查索引是否已存在
            boolean listIndexs = checkListIndexs(collectionName, MongoDBType.PRIMARY);
            if (!listIndexs) {
                try {
                    List<IndexModel> indexModels = getIndexModels();
                    receiveAlarmPointMapper.mongoCreateIndexs(collectionName, MongoDBType.PRIMARY, indexModels);
                } catch (Exception e) {
                    log.error("创建索引异常", e);
                }
            }
        }catch (Exception e){
            log.error("存储告警框图的点位数据到mongo异常",e);
        }
    }

    private List<IndexModel> getIndexModels() {
        List<IndexModel> indexModels = new ArrayList<>();
        BasicDBObject index = new BasicDBObject();
        index.put(Constants.PARAM_WARNING_ORDER_ID, 1);
        IndexOptions indexOptions = new IndexOptions().background(true).name("warningOrderId");
        indexModels.add(new IndexModel(index, indexOptions));
        return indexModels;
    }

    private boolean checkListIndexs(String tableName, MongoDBType primary) {
        boolean success = false;
        try {
            ListIndexesIterable<Document> indexList = receiveAlarmPointMapper.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    if (keyDocument.containsKey(Constants.PARAM_WARNING_ORDER_ID)) {
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
     * 告警对象回填置信度字段，同时填充告警点数组对象
     * @param item
     * @param alarmPointList
     */
    private void setConfidenceLevelAndAddAlarmPoint(ReceiveAlarmVO item,List<AlarmOriginalPointVO> alarmPointList){
        List<AlarmOriginalAreaVO> alarmAreaList = item.getAlarmArea();
        if (!CollectionUtils.isEmpty(alarmAreaList)) {
            //置信度取AlarmArea集合里面得ConfidenceLevel置信度，然后逗号隔开
            item.setConfidenceLevel(alarmAreaList.stream().map(AlarmOriginalAreaVO::getConfidenceLevel).collect(Collectors.joining(Constants.COMMA)));
            item.setConfidenceLevelMin(alarmAreaList.stream().min(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
            item.setConfidenceLevelMax(alarmAreaList.stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
            //添加告警点
            addAlarmPoint(item.getWarningOrderId(),item.getTenantId(),alarmAreaList, alarmPointList);
        }
    }

    /**
     * 保存告警点数组对象
     * @param warningOrderId
     * @param tenantId
     * @param alarmAreaList
     * @param alarmPointLists
     */
    private void addAlarmPoint(String warningOrderId,String tenantId,List<AlarmOriginalAreaVO> alarmAreaList, List<AlarmOriginalPointVO> alarmPointLists) {
        if (!CollectionUtils.isEmpty(alarmAreaList)) {
            for (AlarmOriginalAreaVO area : alarmAreaList) {
                area.setWarningOrderId(warningOrderId);
                List<AlarmOriginalPointVO> alarmPointList = area.getAlarmPointList();
                if (!CollectionUtils.isEmpty(alarmPointList)) {
                    String alarmPictureId = PKUtils.getAlarmPictureId(tenantId);
                    for (AlarmOriginalPointVO point : alarmPointList) {
                        AlarmOriginalPointVO alarmOriginalPointVO = new AlarmOriginalPointVO();
                        alarmOriginalPointVO.setAlarmAreaId(area.getAlarmAreaId());
                        alarmOriginalPointVO.setWarningOrderId(warningOrderId);
                        alarmOriginalPointVO.setAlarmPictureId(alarmPictureId);
                        alarmOriginalPointVO.setConfidenceLevel(area.getConfidenceLevel());
                        alarmOriginalPointVO.setAlarmPointId(point.getAlarmPointId());
                        alarmOriginalPointVO.setPercentX(point.getPercentX());
                        alarmOriginalPointVO.setPercentY(point.getPercentY());
                        alarmPointLists.add(alarmOriginalPointVO);
                    }
                }
            }
        }
    }
}
