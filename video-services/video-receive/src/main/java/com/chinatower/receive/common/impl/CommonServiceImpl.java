package com.chinatower.receive.common.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.event.api.ITowerEventWarningService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.api.model.receive.PushThirdReq;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.core.alarm.util.SubScenesWriteHelper;
import com.chinatower.event.util.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.entity.IVmMessagePushDTO;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.service.MessageSendService;
import com.chinatower.platform.util.PKUtil;
import com.chinatower.receive.asyncTask.AsyncCommon;
import com.chinatower.receive.common.CommonService;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.core.util.GisUtil;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.receive.mapper.AlarmOperateMapper;
import com.chinatower.receive.mapper.ISysMapper;
import com.chinatower.receive.mapper.WarningInterceptMapper;
import com.chinatower.receive.mapper.VmAgentOperationTaskExecutionMapper;
import com.chinatower.receive.mapper.WarningOrderMapper;
import com.chinatower.receive.mongodb.InterceptMongoMapper;
import com.chinatower.receive.mongodb.OperateMongoMapper;
import com.chinatower.receive.mongodb.ReceiveAlarmPointMapper;
import com.chinatower.receive.service.*;
import com.chinatower.receive.support.rule.OperationJudgeRuleExecutor;
import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.model.RuleContext;
import com.chinatower.receive.vo.dto.AlarmTypeInfoDTO;
import com.chinatower.receive.vo.dto.OperationTaskListResponse;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.AlarmTypeLevelVO;
import com.chinatower.receive.vo.entity.RedisPTVo;
import com.chinatower.receive.vo.entity.RedisPositionVo;
import com.chinatower.receive.vo.entity.VmAgentOperationTaskExecutionDO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Author: songxinfeng
 * @Date: 2024/8/21 15:49
 */

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {


    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private WarningInterceptMapper warningInterceptMapper;

    @Autowired
    private AsyncCommon asyncCommon;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private VmAgentOperationTaskExecutionMapper vmAgentOperationTaskExecutionMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private ISuppressStrategyService suppressStrategyService;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    private MessageSendService messageSendService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private IFlowLinkService IFlowLinkService;

    @Autowired
    private ISuppressStrategyService strategyService;

    @Autowired
    private ReceiveAlarmPointMapper receiveAlarmPointMapper;

    @Autowired
    private IOperationJudgeNotificationService operationJudgeNotificationService;

    @Autowired
    private GisUtil gisUtil;

    @Autowired
    private ISysMapper sysMapper;

    @Autowired
    private IVideoDeviceService videoDeviceService;

    @Autowired
    private OperationJudgeRuleExecutor operationJudgeRuleExecutor;
    @Autowired
    private ITowerEventWarningService iTowerEventWarningService;
    @Autowired
    private IOperationTaskService operationTaskService;

    @Override
    public void saveAlarm(ReceiveAlarmDTO receiveAlarmVODB, String alarmTime, String warningSource, String originalJson, List<AlarmOriginalPointVO> alarmPointList) {
        if (!CollectionUtils.isEmpty(receiveAlarmVODB.getIntercepts())) {
            receiveAlarmVODB.getIntercepts().forEach(item -> {
                setConfidenceLevelAndAddAlarmPoint(item, alarmPointList);
            });
            saveIntercept(receiveAlarmVODB.getIntercepts(), alarmTime, warningSource);
        }
        if (!CollectionUtils.isEmpty(receiveAlarmVODB.getList())) {
            receiveAlarmVODB.getList().forEach(item -> {
                if (!WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(item.getAlarmSource())) {
                    setConfidenceLevelAndAddAlarmPoint(item, alarmPointList);
                }
            });
            saveOrder(receiveAlarmVODB, warningSource, originalJson);
        }
        // 批量保存运营研判数据
        if (!CollectionUtils.isEmpty(receiveAlarmVODB.getOperations())) {
            receiveAlarmVODB.getOperations().forEach(item -> {
                setConfidenceLevelAndAddAlarmPoint(item, alarmPointList);
            });
            saveOperate(receiveAlarmVODB.getOperations(), warningSource);
        }
        // 保存图片和视频 报警区域 及 告警点列表
        if (!CollectionUtils.isEmpty(alarmPointList)) {
            //告警框图的点位数据入库mongo
            log.info("告警框图的点位数据入库mongo参数：{}", JSON.toJSONString(alarmPointList));
            this.savealarmPointMongo(alarmPointList);
        }

    }

    public void saveOperate(List<ReceiveAlarmVO> operations, String warningSource) {
        try {
            log.info("批量保存运营研判数据信息入参：{}", JSON.toJSONString(operations));
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(operations);
            //告警信息中新增：俯仰角、水平视场角、垂直视场角字段呈现
            warningOrderUtil.setAiAndDeviceAngle(operations);
            //填充告警等级 默认1
            this.setAlarmLevel(operations);
            // 写 Mongo 前补齐 subScenes（与 saveOrder 一致；Kafka 链路可能丢失该字段）
            operations.forEach(SubScenesWriteHelper::fillSubScenes);
            int i = alarmOperateMapper.insertAlarmOperates(operations);
            operations.forEach(item -> {
                if (!StringUtils.isEmpty(item.getOperationNode()) && !item.getOperationNode().contains(item.getAlarmNode())) {
                    //将此运营研判数据推同步送到指定受理节点
                    asyncCommon.sync(item);
                    /*log.info("推送属地运营研判开始:{}", JSON.toJSONString(item));
                    try {
                        Result result = RemoteNodeUtils.create(item.getOperationNode())
                                .getFeign(IReceiveApiService.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                                .syncAlarm(item);
                        log.info("推送属地运营研判上报告警结束:{}",JSON.toJSONString(result));
                    } catch (Exception e) {
                        log.error("推送属地运营研判开始异常:{}", e);
                    }*/
                }
            });

            try {
                operateMongoMapper.insertOperate(operations, operations.get(0).getAlarmTime());
                operations.forEach(item -> {
                    // 根据告警单子的省市获取需要推送的用户信息
                    List<SysUser> needNotifyUsers = getAvailableUsersForOperationJudge(item.getProvinceId(), item.getCityId());
                    // AI智能研判通知小塔是否打开弹窗
                    operationJudgeNotificationService.handleOperationJudgeNotification(item, needNotifyUsers);
                    // 若是开启自动持续执行任务时，进行智能研判
                    intelligentJudgeTask(item, needNotifyUsers);
                });


            } catch (Exception e) {
                log.info("运营研判插入mongo失败:", e);
            }
            if (i > 0) {
                //将最新告警时间保存到缓存中，用于压制判断
                setAlarmTimeToRedis(operations);
                if (Constants.NINE.equals(warningSource)) {
                    //卫星告警抓图抓视频
                    asyncCommon.satelliteSnapShot(operations, Constants.THREE);
                } else if (Constants.EIGHT.equals(warningSource)) {
                    //物联告警抓图抓视频
                    this.iotAlarmShot(operations, Constants.THREE);
                } else if (Constants.TEN.equals(warningSource)) {
                    asyncCommon.secondRecogIfExecuteByRadar(operations, "3");
                }
            }
        } catch (Exception e) {
            log.info("运营研判插入mongo失败:", e);
        }
    }

    /**
     * 获取运营研判可用用户列表
     *
     * @return 用户列表
     */
    private List<SysUser> getAvailableUsersForOperationJudge(String provinceId, String city) {
        return deviceRepository.getUserAlarmOperateByRole(
                ObjectUtil.isNull(provinceId) ? "" : provinceId.substring(0, 4),
                ObjectUtil.isNull(city) ? "" : city
        );
    }

    //v1.7.20 获取告警类型对应的告警等级
    private void setAlarmLevel(List<ReceiveAlarmVO> dataList) {
        try {
            // 查询所有的告警类型等级配置数据
            List<AlarmTypeLevelVO> alarmTypeLevelVOList = sysMapper.selectAlarmTypeLevelAll();
            if (!CollectionUtils.isEmpty(alarmTypeLevelVOList)) {
                // 根据租户 + 行业 + 告警类型ID 分组
                Map<String, String> groupedMap = alarmTypeLevelVOList.stream()
                        .collect(Collectors.toMap(
                                vo -> generateKey(vo.getTenantId(), vo.getIndustryCode(), vo.getTypeValue()),
                                vo -> vo.getAlarmLevel(),
                                (existing, replacement) -> replacement // 如果有重复 key，保留后一个
                        ));
                log.info("setAlarmLevel-查询告警类型等级resp:{}", JSON.toJSONString(groupedMap));
                dataList.forEach(order -> {
                    String key = generateKey(order.getTenantId(), order.getIndustryCode(), !StringUtils.isEmpty(order.getWarningTypeId()) ? order.getWarningTypeId() : order.getAlarmType());
                    order.setImportanceLevel(groupedMap.getOrDefault(key, Constants.ONE));
                });
            }
        } catch (Exception e) {
            log.info("setAlarmLevel-查询告警类型等级异常:", e);
        }
    }

    // 生成分组的唯一key
    private String generateKey(String tenantId, String industryCode, String typeValue) {
        return tenantId + Constants.SPLIT_SIGN + industryCode + Constants.SPLIT_SIGN + typeValue;
    }

    //物联告警抓图抓视频
    public void iotAlarmShot(List<ReceiveAlarmVO> list, String tableType) {
        try {
            log.info("iotAlarmShot抓图抓视频：{}", JSON.toJSONString(list));
            List<String> iotList = list.stream().flatMap(v -> v.getIotDeviceExtraFieldVOList().stream().map(i -> i.getIotDeviceCode())).distinct().collect(Collectors.toList());
            TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
            qryCond.setDeviceCodeList(iotList);
            //批量查询物联设备联动设备
            Result<List<TenantIotDeviceConfVO>> result = videoDeviceService.getTenantIotDevConfBatch(qryCond);
            if (result.getCode() == ResultCode.SUCCESS.getCode() && Objects.nonNull(result.getData())) {
                List<TenantIotDeviceConfVO> mapList = result.getData();
                log.info("getTenantIotDevConfBatch：{}", JSON.toJSONString(mapList));
                for (ReceiveAlarmVO alarmVO : list) {
                    //获取这条告警，对应物联设备的联动设备信息
                    List<String> codeList = alarmVO.getIotDeviceExtraFieldVOList().stream().map(v -> v.getIotDeviceCode()).collect(Collectors.toList());
                    List<TenantIotDeviceConfVO> relDeviceList = mapList.stream().filter(v -> codeList.contains(v.getDeviceCode()) && v.getTenantId().equals(alarmVO.getTenantId()) && v.getIndustryCode().equals(alarmVO.getIndustryCode())).collect(Collectors.toList());
                    log.info("relDeviceList：{}", JSON.toJSONString(relDeviceList));
                    for (TenantIotDeviceConfVO deviceConfVO : relDeviceList) {
                        //物联设备告警联动动作处理
                        asyncCommon.iotDeviceDeal(alarmVO, deviceConfVO, tableType);
                    }

                }
            }


        } catch (Exception e) {
            log.error("物联设备联动动作失败!", e);
        }
    }


    /**
     * 告警对象回填置信度字段，同时填充告警点数组对象
     *
     * @param item
     * @param alarmPointList
     */
    private void setConfidenceLevelAndAddAlarmPoint(ReceiveAlarmVO item, List<AlarmOriginalPointVO> alarmPointList) {
        List<AlarmOriginalAreaVO> alarmAreaList = item.getAlarmArea();
        if (!CollectionUtils.isEmpty(alarmAreaList)) {
            //置信度取AlarmArea集合里面得ConfidenceLevel置信度，然后逗号隔开
            item.setConfidenceLevel(alarmAreaList.stream().map(AlarmOriginalAreaVO::getConfidenceLevel).collect(Collectors.joining(Constants.COMMA)));
            item.setConfidenceLevelMin(alarmAreaList.stream().min(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
            item.setConfidenceLevelMax(alarmAreaList.stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
            //添加告警点
            if (!Constants.NINE.equals(item.getAlarmSource())) {
                addAlarmPoint(item.getWarningOrderId(), item.getTenantId(), alarmAreaList, alarmPointList);
            }

        }
    }


    /**
     * 保存告警点数组对象
     *
     * @param warningOrderId
     * @param tenantId
     * @param alarmAreaList
     * @param alarmPointLists
     */
    private void addAlarmPoint(String warningOrderId, String tenantId, List<AlarmOriginalAreaVO> alarmAreaList, List<AlarmOriginalPointVO> alarmPointLists) {
        if (!CollectionUtils.isEmpty(alarmAreaList)) {
            for (AlarmOriginalAreaVO area : alarmAreaList) {
                area.setWarningOrderId(warningOrderId);
                List<AlarmOriginalPointVO> alarmPointList = area.getAlarmPointList();
                if (!CollectionUtils.isEmpty(alarmPointList)) {
                    String alarmPictureId = PKUtil.getAlarmPictureId(tenantId);
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

    public void saveIntercept(List<ReceiveAlarmVO> intercepts, String alarmTime, String warningSource) {
        try {
            //卫星告警入库告警压制表
            log.info("批量保存压制数据数据信息入参：{}", JSON.toJSONString(intercepts));
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(intercepts);
            //告警信息中新增：俯仰角、水平视场角、垂直视场角字段呈现
            warningOrderUtil.setAiAndDeviceAngle(intercepts);
            //设置二次识别任务表的工单号
            warningOrderUtil.setOrderToSecondTask(intercepts, Constants.ONE);
            //填充告警等级 默认1
            this.setAlarmLevel(intercepts);
            // 写 Mongo 前补齐 subScenes（与 saveOrder 一致）
            intercepts.forEach(SubScenesWriteHelper::fillSubScenes);
            int i = warningInterceptMapper.insertInterceptAlarm(intercepts);
            interceptMongoMapper.insertIntercept(intercepts, alarmTime);
            // 异步尝试获取联动摄像机抓图
            if (i > 0) {
                if (Constants.NINE.equals(warningSource)) {
                    //卫星告警抓图抓视频
                    asyncCommon.satelliteSnapShot(intercepts, Constants.ONE);
                } else if (Constants.EIGHT.equals(warningSource)) {
                    //物联告警抓图抓视频
                    this.iotAlarmShot(intercepts, Constants.ONE);
                } else if (Constants.TEN.equals(warningSource)) {
                    asyncCommon.secondRecogIfExecuteByRadar(intercepts, "1");
                }
            }
        } catch (Exception e) {
            log.info("回收站插入mongo失败:", e);
        }
    }

    public void saveOrder(ReceiveAlarmDTO receiveAlarmVODB, String warningSource, String originalJson) {
        try {
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(receiveAlarmVODB.getList());
            //告警信息中新增：俯仰角、水平视场角、垂直视场角字段呈现
            warningOrderUtil.setAiAndDeviceAngle(receiveAlarmVODB.getList());
            //设置二次识别任务表的工单号
            warningOrderUtil.setOrderToSecondTask(receiveAlarmVODB.getList(), Constants.ZERO);
            //填充告警等级 默认1
            this.setAlarmLevel(receiveAlarmVODB.getList());
            // 入库前填充 subScenes（大模型写实际触发；非大模型/解析失败为 null）
            receiveAlarmVODB.getList().forEach(SubScenesWriteHelper::fillSubScenes);
            // 批量保存告警信息
            int i = warningOrderMapper.insertWarningOrders(receiveAlarmVODB);
            if (i > 0) {
                //将最新告警时间保存到缓存中，用于压制判断
                setAlarmTimeToRedis(receiveAlarmVODB.getList());
                String alarmType = receiveAlarmVODB.getList().get(0).getAlarmType();
                String[] arr = {"4110101", "4110102", "4110103", "4110104"};
                //如果是卫片告警
                if (Arrays.asList(arr).contains(alarmType)) {
                    //卫片告警抓图抓视频
                    asyncCommon.satelliteImagesSnapShot(receiveAlarmVODB.getList(), Constants.TWO);
                } else if (Constants.NINE.equals(warningSource)) {
                    //卫星告警抓图抓视频
                    asyncCommon.satelliteSnapShot(receiveAlarmVODB.getList(), Constants.TWO);
                } else if (Constants.EIGHT.equals(warningSource)) {
                    //物联告警抓图抓视频
                    this.iotAlarmShot(receiveAlarmVODB.getList(), Constants.TWO);
                } else if (Constants.TEN.equals(warningSource)) {
                    asyncCommon.secondRecogIfExecuteByRadar(receiveAlarmVODB.getList(), "2");
                }
            }
            log.info("====== 保存告警数据,成功 {} 条", i);
        } catch (Exception e) {
            log.error("保存告警信息异常:" + e);
        }
        //异步推送第三方
        try {
            if (!StringUtils.isEmpty(originalJson)) {
                PushThirdReq pushThirdReq = new PushThirdReq();
                pushThirdReq.setList(receiveAlarmVODB.getList());
                pushThirdReq.setOriginalJson(originalJson);
                iTradeWarningOrderService.pushThirdOrder(pushThirdReq);
            }
        } catch (Exception e) {
            log.error("人工上报推送第三方异常", e);
        }
    }


   /* @Override
    public boolean judgeIntercept(ReceiveAlarmVO alarmVO, String isIntercept, Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap,
                               Map<String, AlarmSuppressConfigDTO> configDTOMap, List<ReceiveAlarmVO> intercepts) {

        if (Constants.ONE.equals(isIntercept) && MapUtils.isNotEmpty(strategyMap)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyTenantList = strategyMap.get(alarmVO.getTenantId()+alarmVO.getIndustryCode());
            if (strategyTenantList != null && !strategyTenantList.isEmpty()) {  //没有查询到匹配得告警压制策略
                boolean interceptFlag = strategyService.doStrategyIntercept(alarmVO, strategyTenantList);
                if (interceptFlag) {
                    // 压制成功，放入压制集合
                    intercepts.add(alarmVO);
                    return true;
                }
            }
        }
        String tenantId = alarmVO.getTenantId();
        String deviceCode = alarmVO.getDeviceCode();
        String mapKey = tenantId + "_" + deviceCode;
        String alarmTime = alarmVO.getAlarmTime();

        //雨天模式告警规则生效则进行压制
        log.info("雨天压制获取到的配置信息：{}", configDTOMap);
        if (doIntercept(configDTOMap, mapKey, DateUtils.string2Date(alarmTime))) {
            alarmVO.setIntercept(AlgorithmEventVO.RAINY_ALGORITHM);
            intercepts.add(alarmVO);
            return true;
        }

        //获取研判压制,压制规则保存到redis中,redis中有数据时进行压制
        String key = Constants.INTERCEPT_CONFIG + tenantId + "_" + alarmVO.getIndustryCode() + "_" + deviceCode + "_" + alarmVO.getAlarmType();
        String interceptConfig = RedisUtil.get(key, RedisDBType.PRIMARY);
        log.info("研判压制获取到的redis值：{}", interceptConfig);
        if (!StringUtils.isEmpty(interceptConfig)) {
            alarmVO.setIntercept(AlgorithmEventVO.FOR_JUDGE);
            intercepts.add(alarmVO);
            return true;
        }
        return false;
    }*/

    /**
     * 校验是否走雨天压制规则
     *
     * @param configDTOMap
     * @param key
     * @return
     */
    private boolean doIntercept(Map<String, AlarmSuppressConfigDTO> configDTOMap, String key, Date alarmTime) {
        if (MapUtils.isNotEmpty(configDTOMap)) {
            AlarmSuppressConfigDTO configDTO = configDTOMap.get(key);
            if (null != configDTO && configDTO.getSuppressBeginTime().compareTo(alarmTime) <= 0 && configDTO.getSuppressEndTime().compareTo(alarmTime) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 告警点位数据入mongo
     */
    public void savealarmPointMongo(List<AlarmOriginalPointVO> alarmPointLists) {
        try {
            String yearMonth = alarmPointLists.get(0).getWarningOrderId().substring(0, 6);
            String collectionName = TableConstant.WARN_ORIGINAL_POINT_TABLE + yearMonth;
            Boolean exists = receiveAlarmPointMapper.collectionExists(collectionName, MongoDBType.PRIMARY);
            if (!exists) {
                receiveAlarmPointMapper.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
            }
            //告警点位数据入mongo
            receiveAlarmPointMapper.insertAll(alarmPointLists, collectionName, MongoDBType.PRIMARY);
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
        } catch (Exception e) {
            log.error("存储告警框图的点位数据到mongo异常", e);
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
     * 将最新的告警时间放入Redis中 20211223 zzh
     *
     * @param list
     */
    public void setAlarmTimeToRedis(List<ReceiveAlarmVO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (ReceiveAlarmVO vo : list) {
                // 若是并行研判数据则跳过，因为在事件集合中已经set过了
                if (Constants.TWO_FLAG.equals(vo.getIsOperation()) && Constants.TWO_FLAG.equals(vo.getNotifyType())) {
                    continue;
                }

                if (vo.getOrderTimeLimit() != null) {
                    reSetRedisPtList(vo); //设置并更新历史redis中告警点PT信息
                    reSetRedisositionList(vo);
                }

                String value = vo.getAlarmTime();
                log.info("告警编码:{},存储的redis值:{}", vo.getAlarmCode(), value);
                String key = Constants.ALARM_TIME_KEY + vo.getTenantId() + vo.getIndustryCode() + vo.getWarningTypeId() + vo.getDeviceCode();
                if (RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
                    Date oldTime = DateUtils.string2Date((String) RedisUtil.get(key, RedisDBType.PRIMARY));
                    Date newTime = DateUtils.string2Date(value);
                    if (oldTime.before(newTime)) {
                        // 如果传入的时间比Redis中时间晚，更新成最新的时间
                        RedisUtil.delete(key, RedisDBType.PRIMARY);
                        RedisUtil.set(key, value, RedisDBType.PRIMARY);
                    }
                } else {
                    RedisUtil.set(key, value, RedisDBType.PRIMARY);
                }
            }
        }
    }


    private void reSetRedisPtList(ReceiveAlarmVO receiveAlarmVO) {
        try {

            String cachePtKey = Constants.ALARM_PT_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId() + receiveAlarmVO.getDeviceCode();

            String value = RedisUtil.get(cachePtKey, RedisDBType.PRIMARY);

            log.info("告警编码：{}，redisKey：{}，处理前redisValue：{}", receiveAlarmVO.getAlarmCode(), cachePtKey, value);

            List<RedisPTVo> redisPTList = StringUtils.isEmpty(value) ? new ArrayList<>() : JSON.parseArray(value, RedisPTVo.class);

            RedisPTVo redisPTVo = new RedisPTVo();
            // 没有告警区域则不进行PT转义
            if (receiveAlarmVO.getAlarmArea() != null && !receiveAlarmVO.getAlarmArea().isEmpty()) {
                ReceiveAlarmVO alarmVO = suppressStrategyService.dealPtzByIconArea(receiveAlarmVO);
                if (!StringUtils.isEmpty(alarmVO.getPan()) && !StringUtils.isEmpty(alarmVO.getTilt())) {
                    // 本次告警事件正常拿到告警点PT值，进行累加
                    redisPTVo.setP(alarmVO.getPan());
                    redisPTVo.setT(alarmVO.getTilt());
                    redisPTVo.setAt(receiveAlarmVO.getAlarmTime());
                    redisPTList.add(redisPTVo);
                }
            }

            if (!ArrayUtil.isEmpty(redisPTList) && !redisPTList.isEmpty()) {
                // 过滤掉redis中已经超出时间限制的事件的redis
                redisPTList = redisPTList.stream().filter(temp ->
                        DateUtils.string2Date(receiveAlarmVO.getAlarmTime()).getTime() - DateUtils.string2Date(temp.getAt()).getTime() <= receiveAlarmVO.getOrderTimeLimit()
                ).collect(Collectors.toList());
            }

            dealRedisPtList(redisPTList, new ArrayList<>());

            log.info("告警编码：{}，redisKey：{}，处理后redisValue：{}", receiveAlarmVO.getAlarmCode(), cachePtKey, JSON.toJSONString(redisPTList));

            // 更新redis，清楚历史已经失效的，同时新增本次新增的
            RedisUtil.setEx(cachePtKey, JSON.toJSONString(redisPTList), Constants.ONE_NUM, TimeUnit.DAYS, RedisDBType.PRIMARY);
        } catch (Exception e) {
            log.error("告警上报设置Redis信息异常,告警编码：{},接口调用异常:{}", receiveAlarmVO.getAlarmCode(), e);
        }
    }


    private void dealRedisPtList(List<RedisPTVo> redisPTList, List<RedisPositionVo> redisPositionList) {
        Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.WARNING_ORDER_REDIS_MAX);
        List<SysDictDataResponse> enumList = res.getData();
        List<SysDictData> enumListMax = new ArrayList<>();
        enumList.forEach(item -> {
            SysDictData sysDictData = new SysDictData();
            BeanUtils.copyProperties(item, sysDictData);
            enumListMax.add(sysDictData);
        });
        Result<List<SysDictDataResponse>> res2 = systemFeignClient.dictDataType(Constants.WARNING_ORDER_REDIS_NUM);
        List<SysDictDataResponse> enumList2 = res2.getData();
        List<SysDictData> enumListNum = new ArrayList<>();
        enumList2.forEach(item -> {
            SysDictData sysDictData = new SysDictData();
            BeanUtils.copyProperties(item, sysDictData);
            enumListNum.add(sysDictData);
        });
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理前的redisPTList：{}", enumListMax.get(0).getDictValue(), enumListNum.get(0).getDictValue(), JSON.toJSONString(redisPTList));
        if (!CollectionUtils.isEmpty(redisPTList) && !CollectionUtils.isEmpty(enumListMax) && !CollectionUtils.isEmpty(enumListNum) && redisPTList.size() > Integer.parseInt(enumListMax.get(0).getDictValue()) &&
                Integer.parseInt(enumListMax.get(0).getDictValue()) > Integer.parseInt(enumListNum.get(0).getDictValue())) {
            redisPTList = redisPTList.subList(Integer.parseInt(enumListNum.get(0).getDictValue()), redisPTList.size());
        }
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理后的redisPTList：{}", enumListMax.get(0).getDictValue(), enumListNum.get(0).getDictValue(), JSON.toJSONString(redisPTList));
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理前的redisPositionList：{}", enumListMax.get(0).getDictValue(), enumListNum.get(0).getDictValue(), JSON.toJSONString(redisPositionList));
        if (!CollectionUtils.isEmpty(redisPositionList) && !CollectionUtils.isEmpty(enumListMax) && !CollectionUtils.isEmpty(enumListNum) && redisPositionList.size() > Integer.parseInt(enumListMax.get(0).getDictValue()) &&
                Integer.parseInt(enumListMax.get(0).getDictValue()) > Integer.parseInt(enumListNum.get(0).getDictValue())) {
            redisPositionList = redisPositionList.subList(Integer.parseInt(enumListNum.get(0).getDictValue()), redisPositionList.size());
        }
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理后的redisPositionList：{}", enumListMax.get(0).getDictValue(), enumListNum.get(0).getDictValue(), JSON.toJSONString(redisPositionList));

    }

    private void reSetRedisositionList(ReceiveAlarmVO receiveAlarmVO) {
        try {

            String cachePositionKey = Constants.ALARM_POSITION_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId() + receiveAlarmVO.getDeviceCode();

            String value = RedisUtil.get(cachePositionKey, RedisDBType.PRIMARY);

            log.info("告警编码：{}，redisKey：{}，处理前redisValue：{}", receiveAlarmVO.getAlarmCode(), cachePositionKey, value);

            List<RedisPositionVo> redisPositionList = StringUtils.isEmpty(value) ? new ArrayList<>() : JSON.parseArray(value, RedisPositionVo.class);
            if (!StringUtils.isEmpty(receiveAlarmVO.getLongitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLatitude())) {
                RedisPositionVo redisPositionVo = new RedisPositionVo();
                redisPositionVo.setAlarmTime(receiveAlarmVO.getAlarmTime());
                redisPositionVo.setLatitude(receiveAlarmVO.getLatitude());
                redisPositionVo.setLongitude(receiveAlarmVO.getLongitude());
                redisPositionList.add(redisPositionVo);
            }

            if (!CollectionUtils.isEmpty(redisPositionList)) {
                // 过滤掉redis中已经超出时间限制的事件的redis
                redisPositionList = redisPositionList.stream().filter(temp ->
                        DateUtils.string2Date(receiveAlarmVO.getAlarmTime()).getTime() - DateUtils.string2Date(temp.getAlarmTime()).getTime() <= receiveAlarmVO.getOrderTimeLimit()
                ).collect(Collectors.toList());
            }

            dealRedisPtList(new ArrayList<>(), redisPositionList);

            log.info("告警编码：{}，redisKey：{}，处理后redisValue：{}", receiveAlarmVO.getAlarmCode(), cachePositionKey, JSON.toJSONString(redisPositionList));

            // 更新redis，清楚历史已经失效的，同时新增本次新增的
            RedisUtil.setEx(cachePositionKey, JSON.toJSONString(redisPositionList), Constants.ONE_NUM, TimeUnit.DAYS, RedisDBType.PRIMARY);
        } catch (Exception e) {
            log.error("告警上报设置Redis信息异常,告警编码：{},接口调用异常:{}", receiveAlarmVO.getAlarmCode(), e);
        }
    }

    @Override
    @Async("taskExecutor")
    public void batchPushMessage(List<ReceiveAlarmVO> alarms) {
        log.info("====== 告警推送开始，{}", JSON.toJSONString(alarms));
        for (ReceiveAlarmVO alarm : alarms) {
            IVmMessagePushDTO iMessagePushDTO = new IVmMessagePushDTO();
            setBaseAttributes(iMessagePushDTO, alarm);
            // 设置用户类型
            if (!StringUtils.isEmpty(iMessagePushDTO.getUserId())) {
                iMessagePushDTO.setUserType(Constants.USER_TYPE_1);
            } else if (!StringUtils.isEmpty(iMessagePushDTO.getRoleId())) {
                iMessagePushDTO.setUserType(Constants.USER_TYPE_2);
            }
            // 检查必要属性是否为空
            if ((StringUtils.isEmpty(iMessagePushDTO.getAlarmFlowId()) || StringUtils.isEmpty(iMessagePushDTO.getLinkId())) && Constants.ONE.equals(alarm.getNotifyType())) {
                continue; // 不处理该告警
            }
            if (Constants.TWO.equals(iMessagePushDTO.getNotifyType())) {
                populateMessagePushDTO(iMessagePushDTO, alarm, null, null);
                // 运营研判消息推送
                configureOperationAnalysis(iMessagePushDTO);
            } else {
                // 处理短信发送时间配置
                handleMessageSendTime(alarm.getVmFlowLinkAttrDto(), iMessagePushDTO);

                //flowId等于模板id 则flowId置空
                String flowTemp = alarm.getFlowTemplate();
                Long alarmFlowId = org.apache.commons.lang3.StringUtils.isNotBlank(flowTemp) && flowTemp.equals(iMessagePushDTO.getAlarmFlowId()) ? null : Long.valueOf(iMessagePushDTO.getAlarmFlowId());
                Long flowTemplate = org.apache.commons.lang3.StringUtils.isNotBlank(flowTemp) ? Long.valueOf(flowTemp) : null;
                if (!StringUtils.isEmpty(iMessagePushDTO.getMsgNotifyType())) {
                    populateMessagePushDTO(iMessagePushDTO, alarm, alarmFlowId, flowTemplate);

                    // 领导批示消息推送
                    if (Constants.SYS_MSG_TYPE_3.equals(iMessagePushDTO.getNotifyType()) && Constants.YES_FLAG.equals(iMessagePushDTO.getLeaderFlag())) {
                        messageSendService.sendMessagePush(iMessagePushDTO);
                        return;
                    }
                }
            }
            // 其他通知类型推送
            iMessagePushDTO.setWxSendFlag(Constants.ONE);
            if (!Constants.TWO.equals(iMessagePushDTO.getNotifyType())) {
                iMessagePushDTO.setUserId(alarm.getProcessId());
            }
            messageSendService.sendMessagePush(iMessagePushDTO);
        }

    }

    /*@Override
    public void setAlarmLocationAndAddress(ReceiveAlarmVO receiveAlarmVO) {
        //告警事件坐标（目前为设备坐标）为空，则取站址的坐标
        String latitude = receiveAlarmVO.getLatitude();
        String longitude = receiveAlarmVO.getLongitude();
        String channelCode = receiveAlarmVO.getChannelCode();
        String deviceCode = receiveAlarmVO.getDeviceCode();
        // 去除上报过来得经纬度，直接取摄像机的经纬度进行告警定位计算
        DeviceSiteReceiveVO deviceSiteReceiveVO = deviceRepository.queryDevice(deviceCode,channelCode);
        if (deviceSiteReceiveVO != null) {
            latitude = deviceSiteReceiveVO.getLatitude();
            longitude = deviceSiteReceiveVO.getLongitude();
            receiveAlarmVO.setAddress(deviceSiteReceiveVO.getLocation());
            receiveAlarmVO.setDeviceName(deviceSiteReceiveVO.getDeviceName()); //替换摄像机名称
            if(!StringUtils.isEmpty(deviceSiteReceiveVO.getChannelName())){
                receiveAlarmVO.setChannelName(deviceSiteReceiveVO.getChannelName()); //替换通道名称
            }
        }
        receiveAlarmVO.setLatitude(latitude);
        receiveAlarmVO.setLongitude(longitude);
        //设置告警点相关信息
        warningOrderUtil.setAlarmLocation(deviceSiteReceiveVO, receiveAlarmVO);
        gisUtil.getAddressFromAmap(receiveAlarmVO, deviceSiteReceiveVO);
    }*/


    private void setBaseAttributes(IVmMessagePushDTO dto, ReceiveAlarmVO alarm) {
        dto.setRegionAlarmFlag(alarm.getRegionAlarmFlag());
        dto.setOrderId(alarm.getWarningOrderId());
        dto.setMsgId(alarm.getWarningOrderId());
        dto.setTenantId(alarm.getTenantId());
        dto.setAlarmTime(alarm.getAlarmTime());
        dto.setAlarmFlowId(alarm.getFlowId());
        dto.setFlowTemplate(alarm.getFlowTemplate());
        dto.setIndustryCode(alarm.getIndustryCode());
        dto.setAppVerCode(alarm.getAppVerCode());
        dto.setLinkId(alarm.getLinkId());
        dto.setNotifyType(!StringUtils.isEmpty(alarm.getNotifyType()) ? alarm.getNotifyType() : Constants.SYS_MSG_TYPE_1);
        dto.setCreator("");
        dto.setNotifyTitle(alarm.getAlarmBody());
        dto.setTypeValue(String.valueOf(alarm.getAlarmType()));
        dto.setAddress(alarm.getAddress());
        dto.setUserType(alarm.getUserType());
        dto.setMsgNotifyType(alarm.getMsgNotifyType());
        dto.setEnableAcceptanceMode(alarm.getEnableAcceptanceMode());
        String processId = alarm.getProcessId();
        if (!StringUtils.isEmpty(processId)) {
            if (processId.startsWith(Constants.COMMA)) {
                processId = processId.substring(1, processId.length());
            }
            if (processId.endsWith(Constants.COMMA)) {
                processId = processId.substring(0, processId.length() - 1);
            }
        }
        dto.setUserId(processId);
        String roleId = alarm.getRoleId();
        if (!StringUtils.isEmpty(roleId)) {
            if (roleId.startsWith(Constants.COMMA)) {
                roleId = roleId.substring(1, roleId.length());
            }
            if (roleId.endsWith(Constants.COMMA)) {
                roleId = roleId.substring(0, roleId.length() - 1);
            }
        }
        dto.setRoleId(roleId);
        dto.setOrderStatus(alarm.getOrderStatus());
        dto.setCountyId(alarm.getCountyCode());
        dto.setCityId(alarm.getCityCode());
        dto.setProvinceId(alarm.getProvinceCode());
        dto.setWarningSource(alarm.getWarningSource());
        dto.setDeviceCode(alarm.getDeviceCode());
        dto.setRealLinkType(alarm.getRealLinkType());
        dto.setGridId(alarm.getGridId());
        dto.setExtraField(alarm.getExtraField());
        dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_Y);
    }

    private void handleMessageSendTime(VmFlowLinkAttrDto vmFlowLinkAttrDto, IVmMessagePushDTO IMessagePushDTO) {
        if (vmFlowLinkAttrDto != null) {
            IMessagePushDTO.setVmFlowLinkAttrDto(vmFlowLinkAttrDto);
            //配置的短信模板时间区间
            String msgSendTimeInterval = vmFlowLinkAttrDto.getMsgSendTimeInterval();
            log.info("ReceiveRepositoryImpl-messagePush-短信模板配置时间区间-" + JSON.toJSONString(msgSendTimeInterval));
            if (!StringUtils.isEmpty(msgSendTimeInterval) && msgSendTimeInterval.contains(Constants.SEP)) {
                try {
                    //根据-切分为配置开始时间和结束后时间
                    String[] msgSendTimeArr = msgSendTimeInterval.split(Constants.SEP);
                    if (!msgSendTimeArr[0].contains(Constants.COLON) || !msgSendTimeArr[1].contains(Constants.COLON)) {
                        log.info("ReceiveRepositoryImpl-messagePush-短信模板配置时间格式有误-默认发送");
                    } else {
                        //判断当前时间是否在该时间区间内（HH:mm:ss）
                        String[] nowTime = com.chinatower.common.core.utils.DateUtils.dateTimeNow("HH:mm:ss").split(Constants.COLON);
                        String[] beginTime = msgSendTimeArr[0].split(Constants.COLON);
                        String[] endTime = msgSendTimeArr[1].split(Constants.COLON);
                        //如果当前时间不在配置范围内，不予发送短信
                        if (!this.compareTime(nowTime, beginTime, endTime)) {
                            IMessagePushDTO.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
                        }
                    }
                } catch (Exception e) {
                    log.error("ReceiveRepositoryImpl-messagePush-短信模板配置时间比较报错-默认发送:", e);
                }
            }
            //定时推送不发送短信消息
            if (Constants.ONE.equals(vmFlowLinkAttrDto.getTimePushType())) {
                IMessagePushDTO.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
            }
        }
    }


    /**
     * 判断当前时间是否在配置时间区间内
     *
     * @param nowTime   当前时间
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public boolean compareTime(String[] nowTime, String[] beginTime, String[] endTime) {
        int nowTotal = Integer.valueOf(nowTime[0]) * 3600 + Integer.valueOf(nowTime[1]) * 60 + Integer.valueOf(nowTime[2]);
        int beginTotal = Integer.valueOf(beginTime[0]) * 3600 + Integer.valueOf(beginTime[1]) * 60 + Integer.valueOf(beginTime[2]);
        int endTotal = Integer.valueOf(endTime[0]) * 3600 + Integer.valueOf(endTime[1]) * 60 + Integer.valueOf(endTime[2]);
        if (nowTotal >= beginTotal && nowTotal <= endTotal) {
            return true;
        } else {
            return false;
        }
    }


    private void configureOperationAnalysis(IVmMessagePushDTO IMessagePushDTO) {
        IMessagePushDTO.setUserType(Constants.USER_TYPE_1);
        IMessagePushDTO.setNotifyAdminFlag(Constants.ZERO);

        String warningTypeId = IMessagePushDTO.getTypeValue() == null ? "" : IMessagePushDTO.getTypeValue();
        AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
        String warningTypeName = (alarmTypeInfo != null) ? alarmTypeInfo.getTypeName() : "";

        if (StringUtils.isEmpty(IMessagePushDTO.getAddress())) {
            IMessagePushDTO.setAddress(IMessagePushDTO.getAddress() == null ? "" : IMessagePushDTO.getAddress());
        }
        if (StringUtils.isEmpty(IMessagePushDTO.getNotifyTitle())) {
            IMessagePushDTO.setNotifyTitle(warningTypeName);
        }

        List<String> userIdList = deviceRepository.getAlarmOperateByRole(
                ObjectUtil.isNull(IMessagePushDTO.getProvinceId()) ? "" : IMessagePushDTO.getProvinceId().substring(0, 4),
                ObjectUtil.isNull(IMessagePushDTO.getCityId()) ? "" : IMessagePushDTO.getCityId()
        );
        IMessagePushDTO.setUserId(String.join(Constants.COMMA, userIdList));
        IMessagePushDTO.setMsgNotifyType("1,3,4");
        IMessagePushDTO.setOrderStatus(Constants.ONE);
        IMessagePushDTO.setIndustryCode(Constants.PARAM_INDUSTRY_CODE_999999);
        IMessagePushDTO.setAppVerCode(Constants.PARAM_APP_VER_CODE_1);
    }


    private void populateMessagePushDTO(IVmMessagePushDTO IMessagePushDTO, ReceiveAlarmVO alarm, Long alarmFlowId, Long flowTemplate) {
        String address = IMessagePushDTO.getAddress() == null ? "" : IMessagePushDTO.getAddress();
        String alarmTime = IMessagePushDTO.getAlarmTime() == null ? "" : IMessagePushDTO.getAlarmTime();
        String warningTypeId = IMessagePushDTO.getTypeValue() == null ? "" : IMessagePushDTO.getTypeValue();
        AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
        String warningTypeName = (alarmTypeInfo != null) ? alarmTypeInfo.getTypeName() : "";

        //如果不是从事件流转过来有指定用户得，查询该工单匹配流程得角色和用户
        if (!Constants.ONE.equals(IMessagePushDTO.getFlowFlag())) {
            if (alarm.getVmFlowLinkAttrDto() != null && alarmFlowId != null && flowTemplate != null) {
                //有勾选网格员处理模式
                this.getUserOrRoleList(alarm.getVmFlowLinkAttrDto(), IMessagePushDTO.getGridId(), alarmFlowId, IMessagePushDTO.getLinkId(), flowTemplate, IMessagePushDTO);
            }
        }

        if (StringUtils.isEmpty(IMessagePushDTO.getAddress())) {
            IMessagePushDTO.setAddress(address);
        }
        if (StringUtils.isEmpty(IMessagePushDTO.getNotifyTitle())) {
            IMessagePushDTO.setNotifyTitle(warningTypeName);
        }

        //包含短信时，通知组装短信通知内容
        //if (IMessagePushDTO.getMsgNotifyType().contains(Constants.MESSAGE_PUSH_TYPE_SMS)) {
        IMessagePushDTO.setAlarmTime(alarmTime);
        IMessagePushDTO.setWarningTypeName(warningTypeName);
        //}
    }


    /**
     * 获取流程匹配得所以用户及角色
     */
    public void getUserOrRoleList(VmFlowLinkAttrDto vmFlowLinkAttrDto, String gridId, Long alarmFlowId, String linkId, Long flowTemplate, IVmMessagePushDTO dto) {
        //有勾选网格员处理模式
        if (vmFlowLinkAttrDto.getUserType() != null) {
            String gridFlag = null;
            String gridUserList = null;
            if (vmFlowLinkAttrDto.getUserType().contains(Constants.THREE)) {
                //查询是否有网格员
                if (!StringUtils.isEmpty(gridId)) {
                    gridUserList = deviceRepository.getGridUserByGridId(gridId);
                }
                if (!org.apache.commons.lang3.StringUtils.isEmpty(gridUserList)) {
                    gridFlag = Constants.ONE;
                }
            }
            AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
            configModel.setId(alarmFlowId);
            configModel.setLinkId(Long.valueOf(linkId));
            configModel.setGridFlag(gridFlag);
            configModel.setTemplateId(flowTemplate);
            configModel.setAppVerCode(dto.getAppVerCode());
            configModel.setIndustryCode(dto.getIndustryCode());
            configModel.setWarningSource(dto.getWarningSource());
            List<FlowModel> flowModelList = IFlowLinkService.getFlowLink(configModel);
            FlowModel flowLinkAttrModel = flowModelList.get(0);
            String flowUserList = "";
            String flowRoleList = "";
            List<String> userTypeList = Arrays.asList(vmFlowLinkAttrDto.getUserType().split(Constants.COMMA));
            //用户
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(flowLinkAttrModel.getUserId())) {
                flowUserList = flowLinkAttrModel.getUserId();
            }
            //角色
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(flowLinkAttrModel.getRoleId())) {
                flowRoleList = flowLinkAttrModel.getRoleId();
            }
            //网格员
            if (userTypeList.contains(Constants.USER_TYPE_3) && gridFlag != null) {
                if (StringUtils.isEmpty(flowUserList)) {
                    flowUserList = gridUserList;
                } else {
                    flowUserList += "," + gridUserList;
                }
            }
            dto.setUserId(flowUserList);
            dto.setRoleId(flowRoleList);
        }
    }

    /**
     * 智能研判
     *
     * @param alarmVO 运营研判告警信息
     */
    @Async("taskExecutor")
    public void intelligentJudgeTask(ReceiveAlarmVO alarmVO, List<SysUser> needNotifyUsers) {
        try {
            for (SysUser needNotifyUser : needNotifyUsers) {
                // 开启自动持续执行任务时，未开启返回true
                RuleContext ruleContext = RuleContext.builder().sysUser(needNotifyUser).build();
                boolean checkResult = checkAutoAIJudgeTask(ruleContext);
                if (checkResult) {
                    log.info("当前用户user:【{}】自动持续研判任务未开启,不进行智能研判", needNotifyUser.getUserId());
                    continue;
                }
                // 查询该用户的最新任务记录id
                Long latestExecutionId = getLatestExecutionIdByUserId(needNotifyUser.getUserId());
                alarmVO.setTaskExecutionId(latestExecutionId);
                Result<JSONObject> result = iTowerEventWarningService.aiJudgeAlarmAndSave(alarmVO);
                if (result.getCode() == 200 && result.getData() != null) {
                    log.error("智能研判成功，告警工单：{},用户id：{},任务记录id：{}, 研判结果：{}", alarmVO.getWarningOrderId(), needNotifyUser.getUserId(), alarmVO.getTaskExecutionId(), result.getData().toJSONString());
                } else {
                    log.error("智能研判失败，告警工单：{},用户id：{},任务记录id：{}, 失败原因：{}", alarmVO.getWarningOrderId(), needNotifyUser.getUserId(), alarmVO.getTaskExecutionId(), result.getMsg());
                }
            }
        } catch (Exception e) {
            log.error("智能研判任务识别异常，告警工单号：{}", alarmVO.getWarningOrderId(), e);
        }
    }

    private Long createTaskExecution(Long taskId,String groupId,String userId) {
        VmAgentOperationTaskExecutionDO vmAgentOperationTaskExecutionDO = new VmAgentOperationTaskExecutionDO();
        vmAgentOperationTaskExecutionDO.setTaskId(taskId);
        vmAgentOperationTaskExecutionDO.setUserId(userId);
        vmAgentOperationTaskExecutionDO.setTaskExecutionType(Constants.TWO); //任务执行类型，1 单次研判  2自动研判
        vmAgentOperationTaskExecutionDO.setExecutionStatus(1);
        vmAgentOperationTaskExecutionDO.setCreateTime(new Date());
        vmAgentOperationTaskExecutionDO.setGroupId(groupId);
        vmAgentOperationTaskExecutionMapper.insertExecution(vmAgentOperationTaskExecutionDO);
        return vmAgentOperationTaskExecutionDO.getExecutionId();
    }

    private boolean checkAutoAIJudgeTask(RuleContext ruleContext) {
        List<String> ruleCodes = new ArrayList<>();
        ruleCodes.add(OperationJudgeRuleEnum.AUTO_JUDGE_TASK_DISABLED.getCode());
        return operationJudgeRuleExecutor.executeRules(ruleCodes, ruleContext);
    }

    /**
     * 根据用户ID查询最新的任务执行记录ID
     *
     * @param userId 用户ID
     * @return 最新的执行记录ID，如果没有则返回null
     */
    private Long getLatestExecutionIdByUserId(String userId) {
        try {
            if (StringUtils.isEmpty(userId)) {
                log.info("用户ID为空，无法查询任务执行记录");
                return null;
            }
            OperationTaskListResponse userTaskInfo = operationTaskService.getUserTaskInfo(userId);
            if(Objects.isNull(userTaskInfo)){
                log.info("用户{}未创建任务", userId);
                return null;
            }
            Long executionId = vmAgentOperationTaskExecutionMapper.getLatestExecutionId(userTaskInfo.getTaskId(), userTaskInfo.getGroupId());
            log.debug("查询用户【{}】的最新任务执行记录ID: {}", userId, executionId);
            if(executionId == null){
                executionId = createTaskExecution(userTaskInfo.getTaskId(),userTaskInfo.getGroupId(),userId);
            }
            return executionId;

        } catch (Exception e) {
            log.error("查询用户【{}】的最新任务执行记录ID异常", userId, e);
            return null;
        }
    }


}
