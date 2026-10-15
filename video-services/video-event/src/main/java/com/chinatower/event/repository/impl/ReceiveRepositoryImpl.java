package com.chinatower.event.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.utils.NullProcessUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.api.IWarningEventService;
import com.chinatower.event.api.IReceiveApiService;
import com.chinatower.event.asyncTask.AsyncCommon;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.core.alarm.util.subscenes.SubScenesWriteHelper;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.kafka.KafkaProducer;
import com.chinatower.event.mapper.*;
import com.chinatower.event.mongodb.InterceptMongoMapper;
import com.chinatower.event.mongodb.OperateMongoMapper;
import com.chinatower.event.mongodb.ReceiveAlarmPointMapper;
import com.chinatower.event.mongodb.ReceiveSatelliteMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.upload.BASE64DecodedMultipartFile;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.utils.*;
import com.chinatower.event.vo.*;
import com.chinatower.event.vo.entity.RedisPositionVo;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.model.dto.CascadeDto;
import com.chinatower.platform.utils.CascadeConnectUtils;
import com.chinatower.platform.utils.HttpUtils;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.VideoControlAPI;
import com.chinatower.video.api.entity.*;
import com.github.pagehelper.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: ReceiveRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 17:00
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class ReceiveRepositoryImpl implements IReceiveRepository {

    @Autowired
    private IVideoDeviceService videoDeviceService;

    @Autowired
    private VideoControlAPI videoControlAPI;

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private WarningInterceptMapper warningInterceptMapper;

    @Autowired
    private ITransLntlagRepository transLntlagRepository;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private WarningOriginalPointMapper warningOriginalPointMapper;

    @Autowired
    private IAlarmFlowConfigService alarmFlowConfigService;

    @Autowired
    private IMessageRepository messageRepository;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private IWarningEventService warningEventService;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private ISysService sysService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private FileFeignClient fileFeignClient;

    // 20220424 lc 告警来源:车辆卡口摄像机告警 start
    @Autowired
    private IReceiveCheckpointDeviceRepository receiveCheckpointDeviceRepository;

    private static final String ALARM_SOURCE_CHECKPOINT_DEVICE = "5";
    // 20220424 lc 告警来源:车辆卡口摄像机告警 end

    @Autowired
    private IOrderPackageRepository orderPackageRepository;

    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private IAlarmSuppressConfigRepository configRepository;

    @Autowired
    private ISuppressStrategyService strategyService;

    @Autowired
    private SuppressStrategyServiceImpl suppressStrategyService;

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private VmAlarmFlowConfigMapper vmAlarmFlowConfigMapper;
    @Autowired
    private VmFlowLinkAttrMapper vmFlowLinkAttrMapper;

    @Autowired
    private ReceiveSatelliteMapper receiveSatelliteMapper;

    @Autowired
    private PushThirdService pushThirdService;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private ReceiveAlarmPointMapper receiveAlarmPointMapper;

    @Autowired
    private AsyncCommon asyncCommon;

    @Autowired
    private MessagePushService messagePushService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private CascadeConnectUtils cascadeConnectUtils;


    // 高德地图参数
    private static final String COLON = ":";
    private static final String START_TIME = ":00:00";
    private static final String END_TIME = ":59:59";

    @Value("${runZhi.url}")
    private String runZhi;

    /** 润之执法系统 OAuth clientId 默认值（配置中心），字典有租户配置时优先生效 */
    @Value("${runZhi.clientId}")
    private String runZhiClientId;

    /** 润之执法系统 OAuth 密钥默认值（配置中心），字典有租户配置时优先生效 */
    @Value("${runZhi.clientSecret}")
    private String runZhiClientSecret;


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
     * 消息推送
     * @param dto
     */
    @Override
    public void messagePush(MessagePushDTO dto) {
        try {
            com.chinatower.platform.entity.IVmMessagePushDTO messagePushDTO = new com.chinatower.platform.entity.IVmMessagePushDTO();
            BeanUtils.copyProperties(dto, messagePushDTO);
            asyncCommon.messagePushNew(messagePushDTO);
        }catch (Exception e) {
            log.error(MessageConstants.MESSAGE_PUSH_ERROR_MESSAGE, e);
        }
    }




    /**
     * 批量发送告警推送
     *
     * @param alarms
     */
    public void batchPushMessage(List<ReceiveAlarmVO> alarms) {
        log.info("====== 告警推送开始，{}",JSON.toJSONString(alarms));
        for (ReceiveAlarmVO alarm : alarms) {

            MessagePushDTO messagePushDTO = new MessagePushDTO();
            messagePushDTO.setRegionAlarmFlag(alarm.getRegionAlarmFlag());
            messagePushDTO.setOrderId(alarm.getWarningOrderId());
            messagePushDTO.setMsgId(alarm.getWarningOrderId());
            messagePushDTO.setTenantId(alarm.getTenantId());
            messagePushDTO.setAlarmTime(alarm.getAlarmTime());
            messagePushDTO.setAlarmFlowId(alarm.getFlowId());
            messagePushDTO.setFlowTemplate(alarm.getFlowTemplate());
            messagePushDTO.setIndustryCode(alarm.getIndustryCode());
            messagePushDTO.setAppVerCode(alarm.getAppVerCode());
            messagePushDTO.setLinkId(alarm.getLinkId());
            messagePushDTO.setNotifyType(alarm.getNotifyType());
            messagePushDTO.setCreator("");
            messagePushDTO.setNotifyTitle(alarm.getAlarmBody());
            messagePushDTO.setTypeValue(String.valueOf(alarm.getAlarmType()));
            messagePushDTO.setAddress(alarm.getAddress());
            messagePushDTO.setUserType(alarm.getUserType());
            messagePushDTO.setMsgNotifyType(alarm.getMsgNotifyType());
            messagePushDTO.setEnableAcceptanceMode(alarm.getEnableAcceptanceMode());
            String processId = alarm.getProcessId();
            if (!StringUtils.isEmpty(processId)) {
                if (processId.startsWith(Constants.COMMA)) {
                    processId = processId.substring(1, processId.length());
                }
                if (processId.endsWith(Constants.COMMA)) {
                    processId = processId.substring(0, processId.length() - 1);
                }
            }
            messagePushDTO.setUserId(processId);
            String roleId = alarm.getRoleId();
            if (!StringUtils.isEmpty(roleId)) {
                if (roleId.startsWith(Constants.COMMA)) {
                    roleId = roleId.substring(1, roleId.length());
                }
                if (roleId.endsWith(Constants.COMMA)) {
                    roleId = roleId.substring(0, roleId.length() - 1);
                }
            }
            messagePushDTO.setRoleId(roleId);
            messagePushDTO.setOrderStatus(alarm.getOrderStatus());
            messagePushDTO.setAppVerCode(alarm.getAppVerCode());
            messagePushDTO.setIndustryCode(alarm.getIndustryCode());
            messagePushDTO.setCountyId(alarm.getCountyCode());
            messagePushDTO.setCityId(alarm.getCityCode());
            messagePushDTO.setProvinceId(alarm.getProvinceCode());
            messagePushDTO.setWarningSource(alarm.getWarningSource());
            messagePushDTO.setDeviceCode(alarm.getDeviceCode());
            messagePushDTO.setRealLinkType(alarm.getRealLinkType());
            messagePushDTO.setGridId(alarm.getGridId());
            messagePushDTO.setExtraField(alarm.getExtraField());
            //this.messagePush(messagePushDTO);
            asyncCommon.messagePush(messagePushDTO);

            log.info("====== 发送告警推送成功");
        }
    }


    /**
     * 判断当前时间是否在配置时间区间内
     * @param nowTime 当前时间
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public boolean compareTime(String[] nowTime,String[] beginTime,String[] endTime){
        int nowTotal=Integer.valueOf(nowTime[0])*3600+Integer.valueOf(nowTime[1])*60+Integer.valueOf(nowTime[2]);
        int beginTotal=Integer.valueOf(beginTime[0])*3600+Integer.valueOf(beginTime[1])*60+Integer.valueOf(beginTime[2]);
        int endTotal=Integer.valueOf(endTime[0])*3600+Integer.valueOf(endTime[1])*60+Integer.valueOf(endTime[2]);
        if(nowTotal >= beginTotal && nowTotal <= endTotal){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Result receiveSatelliteAlarm(List<ReceiveSatelliteAlarmVo> receiveSatelliteAlarmVos) {
        log.info("====== 接收卫星火情告警 传入参数：{} ======",JSON.toJSONString(receiveSatelliteAlarmVos));
        //20230727 v1.6.10 对星地通上报的卫星告警进行处理，使用统一上传的三张图片地址赋值给当前批次的所有告警
        List<String> relativePathMirList = receiveSatelliteAlarmVos.stream()
                .map(vo -> vo.getExtendField().getRelativePathMir())
                .filter(relativePathMir -> !StringUtils.isEmpty(relativePathMir))
                .collect(Collectors.toList());
        List<String> relativePathPostList = receiveSatelliteAlarmVos.stream()
                .map(vo -> vo.getExtendField().getRelativePathPose())
                .filter(relativePathPost -> !StringUtils.isEmpty(relativePathPost))
                .collect(Collectors.toList());
        List<String> relativePathThemList = receiveSatelliteAlarmVos.stream()
                .map(vo -> vo.getExtendField().getRelativePathThem())
                .filter(relativePathThem -> !StringUtils.isEmpty(relativePathThem))
                .collect(Collectors.toList());
        String relativePathMir = null;
        String relativePathPost = null;
        String relativePathThem = null;
        if (!CollectionUtils.isEmpty(relativePathMirList)) {
            relativePathMir = getObsPicUrl(relativePathMirList.get(0));
        }
        if (!CollectionUtils.isEmpty(relativePathPostList)) {
            relativePathPost = getObsPicUrl(relativePathPostList.get(0));
        }
        if (!CollectionUtils.isEmpty(relativePathThemList)) {
            relativePathThem = getObsPicUrl(relativePathThemList.get(0));
        }
        //卫星告警得时间基本所传时间增加8小时，世界时转北京时
        String finalRelativePathMir = relativePathMir;
        String finalRelativePathPost = relativePathPost;
        String finalRelativePathThem = relativePathThem;
        String localNode="";
        //获取当前节点
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        AjaxResult resultNode = systemFeignClient.getConfigInfo(sysConfig);
        if(resultNode != null && 200 == Integer.valueOf(String.valueOf(resultNode.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(resultNode.get("data")), SysConfig.class);
            if (sysConfigInfo != null) {
                localNode = sysConfigInfo.getConfigValue();
            }
        }
        String finalLocalNode = localNode;
        receiveSatelliteAlarmVos.forEach(alarmVo -> {
            alarmVo.setLocalNode(finalLocalNode);
            alarmVo.setAlarmTime(DateUtils.dateToStringNoMS(DateUtils.dateLaterString(alarmVo.getAlarmTime(), Constants.EIGHT_NUM)));
            if(alarmVo.getExtendField() != null){
                alarmVo.getExtendField().setDataSource(alarmVo.getDataSource());
            }
            try {
                SatelliteExtendFieldVo extendFieldVo = alarmVo.getExtendField();
                log.info("====== 获取图片信息 start ：{}======", alarmVo.getExtendField());
                extendFieldVo.setFireArea(alarmVo.getExtendField().getFireArea());
                extendFieldVo.setFireAreaId(alarmVo.getExtendField().getFireAreaId());
                //Eagle-Fire 星地通  njht_fires 航天宏图
                if ("Eagle-Fire".equals(alarmVo.getDataSource())) {
                    extendFieldVo.setRelativePathMir(!StringUtils.isEmpty(extendFieldVo.getRelativePathMir()) ? finalRelativePathMir : "");
                    extendFieldVo.setRelativePathPose(!StringUtils.isEmpty(extendFieldVo.getRelativePathPose()) ? finalRelativePathPost : "");
                    extendFieldVo.setRelativePathThem(!StringUtils.isEmpty(extendFieldVo.getRelativePathThem()) ? finalRelativePathThem : "");
                } else {
                    if (!StringUtils.isEmpty(extendFieldVo.getRelativePathMir())) {
                        extendFieldVo.setRelativePathMir(getObsPicUrl(extendFieldVo.getRelativePathMir()));
                    }
                    if (!StringUtils.isEmpty(extendFieldVo.getRelativePathPose())) {
                        extendFieldVo.setRelativePathPose(getObsPicUrl(extendFieldVo.getRelativePathPose()));
                    }
                    if (!StringUtils.isEmpty(extendFieldVo.getRelativePathThem())) {
                        extendFieldVo.setRelativePathThem(getObsPicUrl(extendFieldVo.getRelativePathThem()));
                    }
                }
                extendFieldVo.setDataSource(alarmVo.getExtendField().getDataSource());
                extendFieldVo.setFireArea(extendFieldVo.getFireArea().replace("公顷","平方公里"));
                extendFieldVo.setUnderlyingSurfaceType(alarmVo.getExtendField().getUnderlyingSurfaceType());
                alarmVo.setExtendField(extendFieldVo);
            } catch (Exception e) {
                log.error("获取卫星火情图片失败，参数：{}", alarmVo.getExtendField(), e);
            }

            //接收卫星数据，将告警经纬度转换为火星坐标系
            String latitude = alarmVo.getLatitude();
            String longitude = alarmVo.getLongitude();
            if (StringUtil.isNotEmpty(latitude) && StringUtil.isNotEmpty(longitude)) {
                //通过经纬度调用高德API得到火星坐标系，保留9位小数
                try {
                   /* GPS gps = GPSConverterUtils.gps84_To_Gcj02(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    if(ObjectUtil.isNotNull(gps)){
                        BigDecimal bLon = BigDecimal.valueOf(gps.getLon());
                        BigDecimal bLat = BigDecimal.valueOf(gps.getLat());
                        longitude = String.valueOf(bLon.setScale(Constants.NINE_NUM, BigDecimal.ROUND_HALF_UP));
                        latitude = String.valueOf(bLat.setScale(Constants.NINE_NUM, BigDecimal.ROUND_HALF_UP));
                    }*/
                    if(StringUtils.isEmpty(alarmVo.getProvince()) || StringUtils.isEmpty(alarmVo.getCity()) || StringUtils.isEmpty(alarmVo.getCounty())){
                        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                        req.setLocation(String.format("%s,%s", longitude, latitude));
                        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                        log.info("poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
                            try {
                                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                                if (data.getRegion() != null) {
                                    Region region = data.getRegion();
                                    if (!StringUtils.isEmpty(region.getProvinceCode())) {
                                        alarmVo.setProvince(region.getProvinceCode());
                                    }
                                    if (!StringUtils.isEmpty(region.getCityCode())) {
                                        alarmVo.setCity(region.getCityCode());
                                    }
                                    if (!StringUtils.isEmpty(region.getCountyCode())) {
                                        alarmVo.setCounty(region.getCountyCode());
                                    }
                                }
                            } catch (Exception e) {
                                log.error("receiveSatelliteAlarm gisMapApi poiContraryGeocodingQuery error, e:", e);
                            }
                        }
                        log.info("poiContraryGeocodingQuery receiveSatelliteAlarm:{}", JSON.toJSONString(alarmVo));
                    }

                }
                catch (Exception e) {
                    log.error(MessageConstants.AMP_COORDINATE_CONVERT_MESSAGE, e);
                }
            }
            log.info("卫星告警告警编码:{},卫星原始84经纬度:{},转换后的火星经纬度:{}",alarmVo.getAlarmCode(),alarmVo.getLatitude()+Constants.COMMA+alarmVo.getLongitude(),latitude+Constants.COMMA+longitude);
            alarmVo.setLatitude(latitude);
            alarmVo.setLongitude(longitude);
            //替换经纬度中的逗号
            if(!StringUtils.isEmpty(alarmVo.getLatitude()) && !StringUtils.isEmpty(alarmVo.getLongitude())){
                alarmVo.setLatitude(alarmVo.getLatitude().replaceAll(Constants.COMMA,""));
                alarmVo.setLongitude(alarmVo.getLongitude().replaceAll(Constants.COMMA,""));
            }

        });
        String originalJson = JSON.toJSONString(receiveSatelliteAlarmVos);
        if(CollectionUtils.isEmpty(receiveSatelliteAlarmVos)){
            return Result.fail(MessageConstants.ALARM_NO_PARAMS);
        }
        // 根据林业版本编码获取租户信息
//        List<OrderCustomerVO> customersByProductCode = orderPackageRepository.getCustomersByProductCode(Constants.PRODUCT_CODE_FOREST);
        //release_v1.5 订购添加卫星订购，订购约束关系为卫星区域。查询租户订购区域向租户发送告警信息
        List<ReceiveAlarmVO> receiveAlarmVOList = new ArrayList<>();
        List<ReceiveAlarmVO> alarms = new ArrayList<>();
        List<OrderCustomerVO> customerAllList = new ArrayList<>();
        receiveSatelliteAlarmVos.forEach(receiveSatelliteAlarmVo -> {
            Result<List<GridByLngLat>> result =null;
            receiveSatelliteAlarmVo.setAlarmBody(receiveSatelliteAlarmVo.getAlarmBody().replace("卫星预警","卫星告警"));
            List<OrderCustomerVO> customersByProductCode = orderPackageRepository.getCustomersByOrderAreaCode(receiveSatelliteAlarmVo);
            customerAllList.addAll(customersByProductCode);
            log.info("====== 接收卫星火情告警 租户信息：{} ======",JSON.toJSONString(customersByProductCode));
            if(! CollectionUtils.isEmpty(customersByProductCode)) {
                receiveSatelliteAlarmVo.setPushCustomerFlag(Constants.ONE);
                if (Objects.nonNull(receiveSatelliteAlarmVo.getExtendField())) {
                    try{
                        if (!StringUtils.isEmpty(receiveSatelliteAlarmVo.getLatitude()) && !StringUtils.isEmpty(receiveSatelliteAlarmVo.getLongitude())) {
                            List<String> tenantIdList = new ArrayList<>();
                            // 根据经纬度获取网格信息，预处理获取当前告警所有租户网格信息
                            GridByLngLatParam param = new GridByLngLatParam();
                            param.setLatitude(receiveSatelliteAlarmVo.getLatitude());
                            param.setLongitude(receiveSatelliteAlarmVo.getLongitude());
                            List<BaseIndustryEntity> tenantList = new ArrayList<>();
                            customersByProductCode.forEach(item ->{
                                BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
                                baseIndustryEntity.setIndustryCode(item.getIndustryCode());
                                baseIndustryEntity.setTenantId(item.getTenantId());
                                tenantList.add(baseIndustryEntity);
                            });
                            param.setTenantList(tenantList);
                            result = gridAPI.getMessageByLngLatNew(param);
                            log.info("通过经纬度获取网格，返回结果： {}", result);
                        }
                    }catch (Exception e){
                        log.error("========== 卫星告警获取网格信息异常：longitude={},latitude={},异常原因：{}",receiveSatelliteAlarmVo.getLongitude(),receiveSatelliteAlarmVo.getLatitude(),e);
                    }
                }
                customerAllList.addAll(customersByProductCode);
                // 根据订购租户生成报警对象
                Result<List<GridByLngLat>> finalResult = result;
                customersByProductCode.forEach(customer -> {
                    if (Objects.nonNull(customer)) {

                        try {
                            buildReceiveAlarmVOList(receiveSatelliteAlarmVo, customer, receiveAlarmVOList, finalResult);
                        } catch (Exception e) {
                            log.error("生成卫星报警对象异常,参数:{},订购租户信息:{}", receiveSatelliteAlarmVo, customer, e);
                        }
                    }
                });


            }else{
                //不推送客户
                receiveSatelliteAlarmVo.setPushCustomerFlag(Constants.ZERO);
            }
        });
        //入库mongo
        asyncCommon.saveMongoDb(receiveSatelliteAlarmVos);
        if(!CollectionUtils.isEmpty(receiveAlarmVOList)){
            // 处理生成告警逻辑
            try {
                fillSysAreaBack(receiveSatelliteAlarmVos,receiveAlarmVOList); // 填充告警点省市区信息
                alarms = addAiAlarm2(receiveAlarmVOList,customerAllList);
            } catch (Exception e) {
                log.error("接收卫星火情告警处理异常，传入参数：{}",receiveSatelliteAlarmVos,e);
            }

            if (CollectionUtils.isEmpty(alarms)) {
                return Result.fail(MessageConstants.INTERCEPT_FAIL_MESSAGE);
            }

            // 告警推送
            try{
                batchPushMessage(alarms);
            }catch (Exception e){
                log.error("====== 告警推送失败,异常原因",e);
            }
            //异步推送第三方
            try{
                pushThirdService.pushThirdOrder(originalJson,alarms);
            }catch (Exception e){
                log.error("人工上报推送第三方异常",e);
            }
            return Result.success(ResultCode.SUCCESS.getMsg());
        }
        return Result.fail(MessageConstants.ALARM_NO_TENANT);
    }

    /**
     * 二次研判转化
     *
     * @param alarmVO
     */
    public void setSecondJudge(ReceiveAlarmVO alarmVO) {
        //二次研判转化
        String noticeResource = String.valueOf(alarmVO.getExtraInfo().getNoticeResource());
        if (StringUtils.isEmpty(alarmVO.getIsSecondJudge())) {
            if (Constants.NOTICE_RESOURCE_2.equals(noticeResource) && Constants.MSG_TYPE_2.equals(alarmVO.getMsgType())) {
                alarmVO.setIsSecondJudge(Constants.IS_SECOND_JUDGE_N);
            } else if (Constants.NOTICE_RESOURCE_2.equals(noticeResource) && Constants.MSG_TYPE_1.equals(alarmVO.getMsgType())) {
                alarmVO.setIsSecondJudge(Constants.IS_SECOND_JUDGE_Y);
                alarmVO.setSecondJudgeResult(Constants.SECOND_JUDGE_RESULT_0);
            } else if (Constants.NOTICE_RESOURCE_2.equals(noticeResource) && Constants.MSG_TYPE_0.equals(alarmVO.getMsgType())) {
                alarmVO.setIsSecondJudge(Constants.IS_SECOND_JUDGE_Y);
                alarmVO.setSecondJudgeResult(Constants.SECOND_JUDGE_RESULT_1);
            }
            else if (StringUtils.isEmpty(noticeResource) && Constants.MSG_TYPE_0.equals(alarmVO.getMsgType())) {
                alarmVO.setIsSecondJudge(Constants.IS_SECOND_JUDGE_N);
            }
        }
    }


    /**
     * 获取字典值
     *
     * @param dataType
     * @return
     */
    public List<SysDictDataResponse> getDict(String dataType) {
        //获取需要特殊处理行业版本
        List<SysDictDataResponse> dictList = new ArrayList<>();
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(dataType);
        if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
            dictList.addAll(sysResult.getData());
        }

        return dictList;
    }

    /**
     * 保存告警点数组对象
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

    /**
     * 获取联合告警压制策略
     *
     * @return
     */
    private Map<String, List<VmAlarmSuppressStrategyVO>> getAlarmSuppressStrategy(ReceiveAlarmVO receiveAlarmVO, List<OrderCustomerVO> customerAllList, String isIntercept) {
        // 告警压制开关是否开启
        if (Constants.YES_FLAG.equals(isIntercept)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyVoList = strategyService.queryStrategyList(receiveAlarmVO, customerAllList);
            if (strategyVoList != null && !strategyVoList.isEmpty()) {
                return strategyVoList.stream().filter(item -> item.getTenantId() != null && !"".equals(item.getTenantId())).collect(Collectors.groupingBy(temp -> temp.getTenantId()+temp.getIndustryCode()));
            }
        }
        return null;
    }

    /**
     * 获取告警压制配置
     *
     * @param customers
     * @return
     */
    private Map<String, AlarmSuppressConfigDTO> getAlarmSuppressConfig(List<OrderCustomerVO> customers) {
        List<String> tenantIdsList = customers.stream().collect(Collectors.mapping(OrderCustomerVO::getTenantId, Collectors.toList()));
        if (!CollectionUtils.isEmpty(tenantIdsList)) {
            List<AlarmSuppressConfigDTO> configDTOList = configRepository.querySuppressConfigByTenantIds(tenantIdsList);
            return configDTOList.stream().collect(Collectors.toMap(k -> k.getTenantId() + "_" + k.getDeviceCode(), configDTO -> configDTO));
        }
        return null;
    }

    /**
     * 判断是否符合告警压制的条件，放入告警压制集合
     * 判断告警等级、压制规则
     *
     * @param alarmVO
     * @param isIntercept
     * @param strategyMap
     * @param configDTOMap
     * @param intercepts
     * @return
     */
    private boolean judgeIntercept(ReceiveAlarmVO alarmVO, String isIntercept, Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap,
                                   Map<String, AlarmSuppressConfigDTO> configDTOMap, List<ReceiveAlarmVO> intercepts) {

        String tenantId = alarmVO.getTenantId();
        String deviceCode = alarmVO.getDeviceCode();
        String mapKey = tenantId + "_" + deviceCode;
        String alarmTime = alarmVO.getAlarmTime();
        String alarmType = String.valueOf(alarmVO.getAlarmType());

        if (Constants.IMPORTANCE_LEVEL_2.equals(alarmVO.getImportanceLevel())) {
            // 如果告警重要等级为二级，直接落告警压制表
            intercepts.add(alarmVO);
            return true;
        } else if ("1".equals(isIntercept) && MapUtils.isNotEmpty(strategyMap)) {
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

        return false;
    }
    /**
     * 构建卫星告警参数
     * @param receiveSatelliteAlarmVo，customers，receiveAlarmVOList
     */
    private  List<ReceiveAlarmVO> buildReceiveAlarmVOList(ReceiveSatelliteAlarmVo receiveSatelliteAlarmVo, OrderCustomerVO customer, List<ReceiveAlarmVO> receiveAlarmVOList, Result<List<GridByLngLat>> result){
        ReceiveAlarmVO receiveAlarmVO =new ReceiveAlarmVO();
        BeanUtil.copyProperties(receiveSatelliteAlarmVo,receiveAlarmVO);
        receiveAlarmVO.setExtraField(JSON.toJSONString(receiveSatelliteAlarmVo.getExtendField()));
        if(StringUtils.isEmpty(receiveAlarmVO.getAlarmBody())){
            receiveAlarmVO.setAlarmBody("火点面积:" + Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                                                                            orElse(new SatelliteExtendFieldVo()).
                                                    getFireArea());
        }else{
            receiveAlarmVO.setAlarmBody(receiveAlarmVO.getAlarmBody() + " 火点面积:" + Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                    orElse(new SatelliteExtendFieldVo()).
                    getFireArea());
        }

        receiveAlarmVO.setWarningSource(String.valueOf(receiveSatelliteAlarmVo.getAlarmSource()));
        //receiveAlarmVO.setAppVerCode(Constants.PRODUCT_CODE_FOREST);
        receiveAlarmVO.setAppVerCode(customer.getAppVerCode());
        receiveAlarmVO.setIndustryCode(customer.getIndustryCode());
        receiveAlarmVO.setTenantId(customer.getTenantId());
        receiveAlarmVO.setTenantName(customer.getCustomerName());
        receiveAlarmVO.setIsOperation(customer.getIsOperation());
        receiveAlarmVO.setOperationNode(customer.getOperationNode());
        receiveAlarmVO.setParallelOperationNode(customer.getParallelOperationNode());
        receiveAlarmVO.setAlarmNode(getPlatformNode());
        receiveAlarmVO.setOperationNodeName(deviceRepository.getPlatformNameByPlatformCode(receiveAlarmVO.getAlarmNode()));
        if(!com.chinatower.common.core.utils.StringUtils.isEmpty(customer.getOperationDept())){
            receiveAlarmVO.setOperationDept(customer.getOperationDept()+Constants.COMMA);
        }
        if(!com.chinatower.common.core.utils.StringUtils.isEmpty(customer.getParallelOperationDept())){
            receiveAlarmVO.setParallelOperationDept(customer.getParallelOperationDept()+Constants.COMMA);
        }
        receiveAlarmVO.setAlarmNode(getPlatformNode());
        receiveAlarmVO.setOperationNodeName(deviceRepository.getPlatformNameByPlatformCode(receiveAlarmVO.getAlarmNode()));

        receiveAlarmVO.setWarningTypeId(String.valueOf(receiveAlarmVO.getAlarmType()));
        receiveAlarmVO.setOperationType(customer.getOperationType());
        receiveAlarmVO.setSerialAlarmTypeStr(customer.getSerialAlarmTypeStr());
        receiveAlarmVO.setParallelAlarmTypeStr(customer.getParallelAlarmTypeStr());
        receiveAlarmVO.setOperationAlarmSource(customer.getOperationAlarmSource());
        receiveAlarmVO.setEmergencyLevel(EnumUtil.AlarmLevel.GENERALLY.getCode());
        //receiveAlarmVO.setLinkName(EnumUtil.WarningSource.AI.getName());// 此处是否应该改为行业告警？
        receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        receiveAlarmVO.setNotifyType(Constants.YES_FLAG);

        receiveAlarmVO.setProvinceCode(receiveSatelliteAlarmVo.getProvince());
        receiveAlarmVO.setCityCode(receiveSatelliteAlarmVo.getCity());
        receiveAlarmVO.setCountyCode(receiveSatelliteAlarmVo.getCounty());

        receiveAlarmVO.setFileImgUrlSrc(Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                orElse(new SatelliteExtendFieldVo()).getRelativePathPose());
        receiveAlarmVO.setOriginalPictureUrl(Optional.ofNullable(receiveSatelliteAlarmVo.getExtendField()).
                orElse(new SatelliteExtendFieldVo()).getRelativePathPose());

        if (!StringUtils.isEmpty(receiveSatelliteAlarmVo.getConfidenceLevel())) {
            receiveAlarmVO.setConfidenceLevelMax(receiveSatelliteAlarmVo.getConfidenceLevel());
        }

        //2022-06-10 guyuefei： 航天宏图传入的地址按省市区地址存放，我们需要详细地址，根据经纬度调用高德接口获取详细地址填充
        try{
            receiveAlarmVO.setAddress(this.getAddressFromAmap(receiveAlarmVO).getAddress());

            // mongo事发地点同步更新
            receiveSatelliteAlarmVo.setAddress(receiveAlarmVO.getAddress());

        }catch (Exception e){
            log.error("========== 获取经纬度异常：longitude={},latitude={},异常原因：{}",receiveAlarmVO.getLongitude(),receiveAlarmVO.getLatitude(),e);
        }

        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
            List<GridByLngLat> gridList = result.getData();
            if (!CollectionUtils.isEmpty(gridList)) {
                GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(customer.getTenantId(), grid.getTenantId()) && Objects.equals(customer.getIndustryCode(), grid.getIndustryCode()))
                        .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                receiveAlarmVO.setGridId(gridInfo.getGridId());
                receiveAlarmVO.setGridName(gridInfo.getGridName());
                receiveAlarmVO.setGridUserList(gridInfo.getUserList());
            }
        }
        receiveAlarmVOList.add(receiveAlarmVO);
        return receiveAlarmVOList;
    }


    /**
     * 获取当前环境节点
     * @return
     */
    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if(Objects.nonNull(configInfo.get(Constants.RESULT_DATA))){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }

    /**
     * 图片地址转换成obs地址格式
     * @param relativeImgPath
     * @author lyy
     * @date 2022/10/26
     */
    private HttpURLConnection httpUrl = null;
    private String getObsPicUrl(String relativeImgPath) {
        // 避免重复上传
        if(relativeImgPath.contains(Constants.HTTPS_CODE) || relativeImgPath.contains("/apiImg")){
            return relativeImgPath;
        }
        //从URL读取图片,转为Base64
        URL url = null;
        InputStream in = null;
        String base64 = "";
        //默认为原始路径，调用成功会覆盖为新路径
        String postPicUrl = relativeImgPath;
        try {
            url = new URL(relativeImgPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10 * 1000);
            in = connection.getInputStream();
            byte[] bytes = IOUtils.toByteArray(in);
            base64 = Base64.getEncoder().encodeToString(bytes);
            //获取到图片，转成MultipartFile流
            String relativePath = "data:image/png;base64," + base64;
            MultipartFile mPathPose = BASE64DecodedMultipartFile.base64ToMultipart(relativePath);
            //上传图片地址到obs
            AjaxResult postResult = fileFeignClient.uploadFile(mPathPose);
            Map postFileInfo = (Map) postResult.get(AjaxResult.DATA_TAG);
            if (postFileInfo != null) {
                //得到obs地址入库
                postPicUrl = (String) postFileInfo.get(VideoConstants.ORIGIN_URL);
                if (!StringUtils.isEmpty(postPicUrl) && postPicUrl.contains(Constants.QUESTION_MARK)) {
                    postPicUrl = postPicUrl.substring(Constants.ZERO_NUM, postPicUrl.indexOf(Constants.QUESTION_MARK));
                }
            }
        } catch (Exception e) {
            log.error("图片上传obs失败！", e);
        }
        //如果卫星告警图片是原始图片，那么根据来源替换图片地址域名
        if(!StringUtils.isEmpty(postPicUrl) && !postPicUrl.contains(Constants.HTTPS_CODE)){
            //从字典获取要替换的域名
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.DOMAIN_PRE_FIX);
            String domainPrefix =null;
            if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                domainPrefix= suppressDictData.getData().get(0).getDictValue();
            }
            if(domainPrefix != null){
                //判断不同的告警推送来源（星地通/航天宏图）匹配替换不同的路径
                if(postPicUrl.contains(Constants.FIRE_IMAGE_CODE) || postPicUrl.contains(Constants.SERVICE_CODE)){
                    String urlBefore = "";
                    if(postPicUrl.contains(Constants.EAGLE_CODE) && postPicUrl.contains(Constants.SERVICE_CODE)){
                        int index = postPicUrl.indexOf(Constants.SERVICE_CODE);
                        urlBefore = postPicUrl.substring(index);
                        postPicUrl=domainPrefix+Constants.API_IMG_CODE+Constants.TWO+urlBefore;
                    }else if(postPicUrl.contains(Constants.NJHT_CODE) && postPicUrl.contains(Constants.FIRE_IMAGE_CODE)){
                        int index = postPicUrl.indexOf(Constants.FIRE_IMAGE_CODE);
                        urlBefore = postPicUrl.substring(index);
                        postPicUrl=domainPrefix+Constants.API_IMG_CODE+urlBefore;
                    }
                }
            }
        }
        return postPicUrl;
    }


    /**
     * 填充省市区详细信息
     * @param receiveAlarmVOList
     */
    private void fillSysAreaBack(List<ReceiveSatelliteAlarmVo> receiveSatelliteAlarmVos,List<ReceiveAlarmVO> receiveAlarmVOList){
        if(! CollectionUtils.isEmpty(receiveAlarmVOList) && ! CollectionUtils.isEmpty(receiveSatelliteAlarmVos)){
            Set<String> areaCodeList = new HashSet<>();
            receiveSatelliteAlarmVos.forEach(receiveAlarmVO -> {
                areaCodeList.add(receiveAlarmVO.getProvince());
                areaCodeList.add(receiveAlarmVO.getCity());
                areaCodeList.add(receiveAlarmVO.getCounty());
            });

            List<SysArea> sysAreaListByCodes = sysService.getSysAreaListByCodes(areaCodeList);
            receiveAlarmVOList.parallelStream().forEach(receiveAlarmVO -> {
                sysAreaListByCodes.forEach(sysArea -> {
                    if(sysArea.getAreaCode().equals(receiveAlarmVO.getProvinceCode())){
                        receiveAlarmVO.setProvinceId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setProvinceName(sysArea.getAreaName());
                    }else if(sysArea.getAreaCode().equals(receiveAlarmVO.getCityCode())){
                        receiveAlarmVO.setCityId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setCityName(sysArea.getAreaName());
                    }else if(sysArea.getAreaCode().equals(receiveAlarmVO.getCountyCode())){
                        receiveAlarmVO.setCountyId(String.valueOf(sysArea.getId()));
                        receiveAlarmVO.setCountyName(sysArea.getAreaName());
                    }
                });
            });

        }

    }


    /**
     * 接收AI告警
     * @param receiveAlarmVOList
     */
    private List<ReceiveAlarmVO> addAiAlarm2(List<ReceiveAlarmVO> receiveAlarmVOList,List<OrderCustomerVO> customerAllList) throws Exception {

        ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
        String alarmTime = receiveAlarmVOList.get(0).getAlarmTime();
        receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

        //如果分月表不存在，则根据告警时间按创建分月表
        warningOrderRepository.createNewTable(alarmTime);

        // 接收告警集合
        List<ReceiveAlarmVO> alarms = new LinkedList<>();

        // 运营研判告警集合
        List<ReceiveAlarmVO> operations = new LinkedList<>();
        // 告警压制集合
        List<ReceiveAlarmVO> intercepts = new LinkedList<>();
        //List<String>  tenantIdList = customerAllList.stream().map(OrderCustomerVO::getTenantId).collect(Collectors.toList());
        for (ReceiveAlarmVO receiveAlarmVO : receiveAlarmVOList) {
            if (StringUtils.isEmpty(receiveAlarmVO.getImportanceLevel()) || Constants.PARAM_NULL.equals(receiveAlarmVO.getImportanceLevel())) {
                receiveAlarmVO.setImportanceLevel(Constants.IMPORTANCE_LEVEL_1);
            }

            // 判断是否传入详细地址信息，若未传，则调用接口获取
            setAddressIfAbsent(receiveAlarmVO);

            //设置行业参数
            //setFlowModel(receiveAlarmVO);

            //更改工单编码生成规则 add by hued 20211103
            String tenantId = receiveAlarmVO.getTenantId();
            String warningOrderId = PKUtils.getWarnOrderId(tenantId,receiveAlarmVO.getAlarmTime());
            receiveAlarmVO.setWarningOrderId(warningOrderId);
            // 告警压制开关是否开启
            String isIntercept = warningOrderUtil.getInterceptSwitch();
            //联合告警压制需求代码合并
            Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap = getAlarmSuppressStrategy(receiveAlarmVO, customerAllList, isIntercept);
            //获取需要特殊处理行业版本
            List<SysDictDataResponse> dictList = this.getDict(Constants.APP_VER_CODE_TOPIC);
            //先把涉及的租户的雨天告警压制配置数据查询出来,防止后面循环查询
            Map<String, AlarmSuppressConfigDTO> configDTOMap = getAlarmSuppressConfig(customerAllList);

            //处理特殊行业版本
            if (warningOrderUtil.judgeSpecialAppVer(dictList, receiveAlarmVO)) {
                continue;
            }

            //判断并处理告警压制
            if (judgeIntercept(receiveAlarmVO, isIntercept, strategyMap, configDTOMap, intercepts)) {
                continue;
            }

            //处理运营研判,首先判断是否开启研判，并行研判
            if (warningOrderUtil.doOperate(receiveAlarmVO, alarms, operations)) {
                continue;
            }
            if(StringUtils.isEmpty(receiveAlarmVO.getJudgeType())){
                receiveAlarmVO.setJudgeType(Constants.JUDGE_TYPE_COMMON);
            }


            alarms.add(receiveAlarmVO);
        }

        if(!CollectionUtils.isEmpty(intercepts)){
            //卫星告警入库告警压制表
            log.info("批量保存压制数据数据信息入参：{}", JSON.toJSONString(intercepts));
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(intercepts);
            int i = warningInterceptMapper.insertInterceptAlarm(intercepts);
            try{
                interceptMongoMapper.insertIntercept(intercepts,alarmTime);
            }catch (Exception e){
                log.info("回收站插入mongo失败:",e);
            }
            // 异步尝试获取联动摄像机抓图
            if (i > 0) asyncCommon.satelliteSnapShot(intercepts,Constants.ONE);
        }

        // 批量保存告警信息
        List<ReceiveAlarmVO> successDatas = batchCreateWarningOrder(receiveAlarmVODB, alarms);
        // 批量保存运营研判数据
        if (!CollectionUtils.isEmpty(operations)) {
            log.info("批量保存运营研判数据信息入参：{}", JSON.toJSONString(operations));
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(operations);
            operations.forEach(SubScenesWriteHelper::fillSubScenes);
            int i = alarmOperateMapper.insertAlarmOperates(operations);
            operations.forEach(item -> {
                if (!StringUtils.isEmpty(item.getOperationNode()) && !item.getOperationNode().contains(item.getAlarmNode())) {
                    //将此运营研判数据推同步送到指定受理节点
                    log.info("推送属地运营研判开始:{}", JSON.toJSONString(item));
                    try {
                        Result result = RemoteNodeUtils.create(item.getOperationNode())
                                .getFeign(IReceiveApiService.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                                .syncAlarm(item);
                        log.info("推送属地运营研判上报告警结束:{}",JSON.toJSONString(result));
                        /*CascadeDto cascadeDto = new CascadeDto();
                        cascadeDto.setFromPlatformCode(item.getAlarmNode());
                        cascadeDto.setToPlatformCode(item.getOperationNode());
                        cascadeDto.setPath("/api/event/alarm/receive/syncAlarm");
                        cascadeDto.setAppId("general");
                        cascadeDto.setMethod("POST");
                        cascadeDto.setBody(JSON.toJSONString(item));
                        cascadeDto.setUri("/gw/proxy");

                        //给级联网关发送请求
                        cascadeConnectUtils.sendToCascade(cascadeDto);*/
                    } catch (Exception e) {
                        log.error("推送属地运营研判开始异常:{}", e);
                    }
                }
            });

            try{
                operateMongoMapper.insertOperate(operations,operations.get(0).getAlarmTime());
            }catch (Exception e){
                log.info("运营研判插入mongo失败:",e);
            }
            if (i > 0) {
                //将最新告警时间保存到缓存中，用于压制判断
                setAlarmTimeToRedis(operations);
                // 异步尝试获取联动摄像机抓图
                asyncCommon.satelliteSnapShot(operations,Constants.THREE);
            }
        }
        // 回填研判信息
        successDatas.addAll(operations);
        return successDatas;
    }




    /**
     * 给receiveSatelliteAlarmVo中address赋值
     * @param receiveAlarmVO
     */
    private void setAddressIfAbsent(ReceiveAlarmVO receiveAlarmVO){
        if(StringUtils.isEmpty(receiveAlarmVO.getAddress())){
            try{
                receiveAlarmVO.setAddress(this.getAddressFromAmap(receiveAlarmVO).getAddress());
            }catch (Exception e){
                log.error("========== 获取经纬度异常：longitude={},latitude={},异常原因：{}",receiveAlarmVO.getLongitude(),receiveAlarmVO.getLatitude(),e);
            }
        }
    }

    /**
     * 批量保存告警信息
     * @param receiveAlarmVODB,alarms
     */
    private List<ReceiveAlarmVO> batchCreateWarningOrder( ReceiveAlarmDTO receiveAlarmVODB,List<ReceiveAlarmVO> alarms){
        if (! CollectionUtils.isEmpty(alarms)) {
            // 创建流程
            List<Map> recordList = warningOrderRepository.createRecord(alarms);

            // 回填当前环节相关字段
            warningOrderRepository.setWarningLinkInfo(alarms, recordList);
            alarms.forEach(item -> {
                List<AlarmOriginalAreaVO> alarmAreaList = item.getAlarmArea();
                if (!CollectionUtils.isEmpty(alarmAreaList)) {
                    // 置信度取AlarmArea集合里面得ConfidenceLevel置信度，然后逗号隔开
                    item.setConfidenceLevel(alarmAreaList.stream().map(AlarmOriginalAreaVO::getConfidenceLevel).collect(Collectors.joining(Constants.COMMA)));
                    item.setConfidenceLevelMin(alarmAreaList.stream().min(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
                    item.setConfidenceLevelMax(alarmAreaList.stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO()).getConfidenceLevel());
                }
            });
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(alarms);
            receiveAlarmVODB.setList(alarms);
            int i = warningOrderMapper.insertWarningOrders(receiveAlarmVODB);

            if (i > 0) {
                //将最新告警时间保存到缓存中，用于压制判断
                setAlarmTimeToRedis(alarms);

                // 异步尝试获取联动摄像机抓图
                asyncCommon.satelliteSnapShot(alarms,Constants.TWO);
            }
            log.info("====== 保存告警数据,成功 {} 条",i);

            return alarms;
        }
        return new ArrayList<>();
    }




    /**
     * 调用高德API 通过经纬度返回地址
     * @param receiveAlarmVO
     */
    private ReceiveAlarmVO getAddressFromAmap(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", receiveAlarmVO.getLongitude(), receiveAlarmVO.getLatitude()));
        log.info("逆地理编码查询接口poiContraryGeocodingQuery req:{}" + JSONObject.toJSONString(req));
        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
        log.info("逆地理编码查询接口poiContraryGeocodingQuery resp:{}" + JSONObject.toJSONString(result));
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !CollectionUtils.isEmpty(result.getData())) {
            try {
                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                receiveAlarmVO.setAddress(data.getAddress());
                if (data.getRegion() != null) {
                    Region region = data.getRegion();
                    if (!StringUtils.isEmpty(region.getTownCode())) {
                        receiveAlarmVO.setTownCode(region.getTownCode());
                    }
                    if (!StringUtils.isEmpty(region.getTown())) {
                        receiveAlarmVO.setTownName(region.getTown());
                    }
                }
            } catch (Exception e) {
                log.error("getAddressFromAmap gisMapApi poiContraryGeocodingQuery error, e:", e);
            }
        }
        log.info("AI上报通过经纬度返回地址：{};poiContraryGeocodingQuery接口返回：{}", JSON.toJSONString(receiveAlarmVO), JSON.toJSONString(result));
        return receiveAlarmVO;
    }
    /**
     * 校验是否超出时间段限制，超出不做压制：返回false；在时间范围内需要压制：返回true
     * @param timeLimit 告警间隔时长
     * @param oldTime 上次告警时间
     * @param newTime 本次告警时间
     * @return
     */
    private boolean checkIntercept(Integer timeLimit, Date oldTime, Date newTime) {
        if (newTime.getTime() - oldTime.getTime() > timeLimit * 1000) {
            return false;
        }
        return true;
    }

    /**
     * 将最新的告警时间放入Redis中 20211223 zzh
     * @param list
     */
    @Override
    public void setAlarmTimeToRedis(List<ReceiveAlarmVO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (ReceiveAlarmVO vo : list) {
                // 若是并行研判数据则跳过，因为在事件集合中已经set过了
                if(Constants.TWO_FLAG.equals(vo.getIsOperation()) && Constants.TWO_FLAG.equals(vo.getNotifyType())){
                    continue;
                }

                if(vo.getOrderTimeLimit() != null){
                    reSetRedisPtList(vo); //设置并更新历史redis中告警点PT信息
                    reSetRedisositionList(vo);
                }

                String value = vo.getAlarmTime();
                log.info("告警编码:{},存储的redis值:{}",vo.getAlarmCode(),value);
                String key = Constants.ALARM_TIME_KEY + vo.getTenantId() + vo.getIndustryCode() + vo.getWarningTypeId() + vo.getDeviceCode();
                if (RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
                    Date oldTime = DateUtils.string2Date((String) RedisUtil.get(key, RedisDBType.PRIMARY));
                    Date newTime = DateUtils.string2Date(value);
                    if (oldTime.before(newTime)) {
                        // 如果传入的时间比Redis中时间晚，更新成最新的时间
                        RedisUtil.delete(key, RedisDBType.PRIMARY);
                        RedisUtil.set(key, value, RedisDBType.PRIMARY);
                    }
                }
                else {
                    RedisUtil.set(key, value, RedisDBType.PRIMARY);
                }
            }
        }
    }

    private void reSetRedisPtList(ReceiveAlarmVO receiveAlarmVO) {
        try {

            String cachePtKey = Constants.ALARM_PT_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId()+receiveAlarmVO.getDeviceCode();

            String value = RedisUtil.get(cachePtKey, RedisDBType.PRIMARY);

            log.info("告警编码：{}，redisKey：{}，处理前redisValue：{}",receiveAlarmVO.getAlarmCode(),cachePtKey,value);

            List<RedisPTVo> redisPTList = StringUtils.isEmpty(value)?new ArrayList<>():JSON.parseArray(value,RedisPTVo.class);

            RedisPTVo redisPTVo = new RedisPTVo();
            // 没有告警区域则不进行PT转义
            if(receiveAlarmVO.getAlarmArea() != null && !receiveAlarmVO.getAlarmArea().isEmpty()) {
                ReceiveAlarmVO alarmVO = suppressStrategyService.dealPtzByIconArea(receiveAlarmVO);
                if(!StringUtils.isEmpty(alarmVO.getPan()) && !StringUtils.isEmpty(alarmVO.getTilt())){
                    // 本次告警事件正常拿到告警点PT值，进行累加
                    redisPTVo.setP(alarmVO.getPan());
                    redisPTVo.setT(alarmVO.getTilt());
                    redisPTVo.setAt(receiveAlarmVO.getAlarmTime());
                    redisPTList.add(redisPTVo);
                }
            }

            if(!ArrayUtil.isEmpty(redisPTList) && !redisPTList.isEmpty() ){
                // 过滤掉redis中已经超出时间限制的事件的redis
                redisPTList = redisPTList.stream().filter(temp ->
                        DateUtils.string2Date(receiveAlarmVO.getAlarmTime()).getTime() - DateUtils.string2Date(temp.getAt()).getTime() <= receiveAlarmVO.getOrderTimeLimit()
                ).collect(Collectors.toList());
            }

            dealRedisPtList(redisPTList,new ArrayList<>());

            log.info("告警编码：{}，redisKey：{}，处理后redisValue：{}",receiveAlarmVO.getAlarmCode(),cachePtKey,JSON.toJSONString(redisPTList));

            // 更新redis，清楚历史已经失效的，同时新增本次新增的
            RedisUtil.setEx(cachePtKey, JSON.toJSONString(redisPTList),Constants.ONE_NUM, TimeUnit.DAYS, RedisDBType.PRIMARY);
        }catch (Exception e){
            log.error("告警上报设置Redis信息异常,告警编码：{},接口调用异常:{}",receiveAlarmVO.getAlarmCode(), e);
        }
    }

    private void reSetRedisositionList(ReceiveAlarmVO receiveAlarmVO) {
        try {

            String cachePositionKey = Constants.ALARM_POSITION_KEY + receiveAlarmVO.getTenantId() + receiveAlarmVO.getIndustryCode() + receiveAlarmVO.getWarningTypeId()+receiveAlarmVO.getDeviceCode();

                String value = RedisUtil.get(cachePositionKey, RedisDBType.PRIMARY);

            log.info("告警编码：{}，redisKey：{}，处理前redisValue：{}",receiveAlarmVO.getAlarmCode(),cachePositionKey,value);

            List<RedisPositionVo> redisPositionList = StringUtils.isEmpty(value)?new ArrayList<>():JSON.parseArray(value,RedisPositionVo.class);
            if(!StringUtils.isEmpty(receiveAlarmVO.getLongitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLatitude())){
                RedisPositionVo redisPositionVo = new RedisPositionVo();
                redisPositionVo.setAlarmTime(receiveAlarmVO.getAlarmTime());
                redisPositionVo.setLatitude(receiveAlarmVO.getLatitude());
                redisPositionVo.setLongitude(receiveAlarmVO.getLongitude());
                redisPositionList.add(redisPositionVo);
            }

            if(!CollectionUtils.isEmpty(redisPositionList)){
                // 过滤掉redis中已经超出时间限制的事件的redis
                redisPositionList = redisPositionList.stream().filter(temp ->
                        DateUtils.string2Date(receiveAlarmVO.getAlarmTime()).getTime() - DateUtils.string2Date(temp.getAlarmTime()).getTime() <= receiveAlarmVO.getOrderTimeLimit()
                ).collect(Collectors.toList());
            }

            dealRedisPtList(new ArrayList<>(),redisPositionList);

            log.info("告警编码：{}，redisKey：{}，处理后redisValue：{}",receiveAlarmVO.getAlarmCode(),cachePositionKey,JSON.toJSONString(redisPositionList));

            // 更新redis，清楚历史已经失效的，同时新增本次新增的
            RedisUtil.setEx(cachePositionKey, JSON.toJSONString(redisPositionList),Constants.ONE_NUM, TimeUnit.DAYS, RedisDBType.PRIMARY);
        }catch (Exception e){
            log.error("告警上报设置Redis信息异常,告警编码：{},接口调用异常:{}",receiveAlarmVO.getAlarmCode(), e);
        }
    }

    private void dealRedisPtList(List<RedisPTVo> redisPTList,List<RedisPositionVo> redisPositionList) {
        Result<List<SysDictDataResponse>> res = systemFeignClient.dictDataType(Constants.WARNING_ORDER_REDIS_MAX);
        List<SysDictDataResponse> enumList = res.getData();
        List<SysDictData> enumListMax = new ArrayList<>();
        enumList.forEach(item ->{
            SysDictData sysDictData = new SysDictData();
            BeanUtils.copyProperties(item,sysDictData);
            enumListMax.add(sysDictData);
        });
        Result<List<SysDictDataResponse>> res2 = systemFeignClient.dictDataType(Constants.WARNING_ORDER_REDIS_NUM);
        List<SysDictDataResponse> enumList2 = res2.getData();
        List<SysDictData> enumListNum = new ArrayList<>();
        enumList2.forEach(item ->{
            SysDictData sysDictData = new SysDictData();
            BeanUtils.copyProperties(item,sysDictData);
            enumListNum.add(sysDictData);
        });
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理前的redisPTList：{}",enumListMax.get(0).getDictValue(),enumListNum.get(0).getDictValue(),JSON.toJSONString(redisPTList));
        if(!CollectionUtils.isEmpty(redisPTList) && !CollectionUtils.isEmpty(enumListMax) && !CollectionUtils.isEmpty(enumListNum) && redisPTList.size()> Integer.parseInt(enumListMax.get(0).getDictValue()) &&
                Integer.parseInt(enumListMax.get(0).getDictValue()) > Integer.parseInt(enumListNum.get(0).getDictValue())){
            redisPTList = redisPTList.subList(Integer.parseInt(enumListNum.get(0).getDictValue()),redisPTList.size());
        }
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理后的redisPTList：{}",enumListMax.get(0).getDictValue(),enumListNum.get(0).getDictValue(),JSON.toJSONString(redisPTList));
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理前的redisPositionList：{}",enumListMax.get(0).getDictValue(),enumListNum.get(0).getDictValue(),JSON.toJSONString(redisPositionList));
        if(!CollectionUtils.isEmpty(redisPositionList) && !CollectionUtils.isEmpty(enumListMax) && !CollectionUtils.isEmpty(enumListNum) && redisPositionList.size()> Integer.parseInt(enumListMax.get(0).getDictValue()) &&
                Integer.parseInt(enumListMax.get(0).getDictValue()) > Integer.parseInt(enumListNum.get(0).getDictValue())){
            redisPositionList = redisPositionList.subList(Integer.parseInt(enumListNum.get(0).getDictValue()),redisPositionList.size());
        }
        log.info("设置的Redis最大长度为：{}，切割间隔为：{},处理后的redisPositionList：{}",enumListMax.get(0).getDictValue(),enumListNum.get(0).getDictValue(),JSON.toJSONString(redisPositionList));

    }

    /**
     * 功能说明：卷宗系统鉴权认证
     *
     * @author: zhang.xionghui
     * @param: map
     * @return: Result
     * @date: 2022/10/13 14:50
     * @requestID  需求单号及需求名称
     */
    @Override
    public Result oauthToken(@RequestBody Map map){
        Map<String,String> result = new HashMap();
        try {
            // 获取当前登录人执法信息
            Map userInfo = sysService.getLoginUserEnforcementInfo();
            if(userInfo != null){
                String zfzh = NullProcessUtil.nvlToString(userInfo.get(Constants.PARAM_USER_ZFZH),"");
                if(StringUtils.isEmpty(zfzh)){
                    return Result.fail("当前登录人没有配置执法编号");
                }
                String adopt = NullProcessUtil.nvlToString(userInfo.get(Constants.PARAM_USER_ADOPT),"");
                String login = NullProcessUtil.nvlToString(userInfo.get(Constants.PARAM_USER_LOGIN),"");
                byte[] bytes = zfzh.getBytes();
                Base64.Encoder encoder = Base64.getEncoder();
                String encodeToken = encoder.encodeToString(bytes);

                // 鉴权地址配置化改造，默认runzhi地址，字典中有配置其他地址则使用对应地址进行鉴权
                String address = runZhi;
                List<SysDictDataResponse> urls = systemFeignClient.dictDataType(Constants.ENFORCEMENT_ADDRESS).getData();
                if(!CollectionUtils.isEmpty(urls)){
                    for(SysDictDataResponse url : urls){
                        if (url.getDictLabel().equals(userInfo.get(Constants.PARAM_TENANT_ID))) {
                            address= url.getDictValue();
                            break;
                        }
                    }
                }
                // clientId/clientSecret：默认取配置中心，字典按租户有配置则覆盖（与 address 同模式）
                String clientId = runZhiClientId;
                String clientSecret = runZhiClientSecret;
                Object tenantId = userInfo.get(Constants.PARAM_TENANT_ID);
                List<SysDictDataResponse> clientIds = systemFeignClient.dictDataType(Constants.ENFORCEMENT_CLIENT_ID).getData();
                if (!CollectionUtils.isEmpty(clientIds)) {
                    for (SysDictDataResponse clientIdDict : clientIds) {
                        if (clientIdDict.getDictLabel().equals(tenantId)) {
                            clientId = clientIdDict.getDictValue();
                            break;
                        }
                    }
                }
                List<SysDictDataResponse> clientSecrets = systemFeignClient.dictDataType(Constants.ENFORCEMENT_CLIENT_SECRET).getData();
                if (!CollectionUtils.isEmpty(clientSecrets)) {
                    for (SysDictDataResponse clientSecretDict : clientSecrets) {
                        if (clientSecretDict.getDictLabel().equals(tenantId)) {
                            clientSecret = clientSecretDict.getDictValue();
                            break;
                        }
                    }
                }
                if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret)) {
                    return Result.fail("执法系统鉴权clientId/clientSecret未配置");
                }
                String url = address + "/" + adopt + "/oauth/token?client_id=" + clientId
                        + "&client_secret=" + clientSecret + "&grant_type=client_credentials";
                JSONObject jsonObject = JSON.parseObject(HttpUtils.getRequest(url));
                log.info("获取认证接口（润之提供),返回：{}",jsonObject);
                String accessToken = NullProcessUtil.nvlToString(jsonObject.get(Constants.PARAM_TOKEN),"");
                String backUrl = address+"/"+login+"/#/login?access_token="+accessToken+"&zfzh="+encodeToken ;
                result.put("access_token",backUrl);
                String orderId = NullProcessUtil.nvlToString(map.get(Constants.PARAM_WARNING_ORDER_ID),"");
                if (!StringUtils.isEmpty(orderId)) {
                    return rangerEvent(map,accessToken,adopt,zfzh,address);
                }
            }else{
                return Result.fail("获取认证接口（润之提供)失败，当前登录人没有配置执法编号");
            }
        } catch (Exception e) {
            log.error("获取认证接口（润之提供),异常：",e);
            return Result.fail("获取认证接口（润之提供),异常");

        }
        return Result.data(result);
    }

    /**
     * 功能说明：事件订阅推送接口
     *
     * @author: zhang.xionghui
     * @param:
     * @return:
     * @date: 2022/10/13 14:51
     * @requestID  需求单号及需求名称
     */
    public Result rangerEvent(Map map,String accessToken,String adopt,String zfzh,String address){
        Map<String,String> result = new HashMap();
        try {
            Map req = new HashMap();
            req.put("eventId", NullProcessUtil.nvlToString(map.get(Constants.PARAM_WARNING_ORDER_ID),"") );
            req.put("eventName", NullProcessUtil.nvlToString(map.get(Constants.PARAM_ALARM_BODY),"") );
            req.put("eventTime", NullProcessUtil.nvlToString(map.get(Constants.PARAM_ALARMTIME),"") );
            req.put("cameraName", NullProcessUtil.nvlToString(map.get(Constants.PARAM_DEVICE_NAME),"") );
            req.put("latitude", NullProcessUtil.nvlToString(map.get(Constants.PARAM_LATITUDE),"") );
            req.put("longitude", NullProcessUtil.nvlToString(map.get(Constants.PARAM_LONGITUDE),"") );
            req.put("address", NullProcessUtil.nvlToString(map.get(Constants.PARAM_ADDRESS),"") );
            req.put("handleUserId", zfzh);
            List<String> list = (List<String>) map.get("files");
            req.put("files", JSON.toJSONString(list));
            log.info("事件订阅推送接口（润之提供）,参数：{}",req);
            String url = address+"/"+adopt+"/api/ranger/event?access_token="+accessToken;
            JSONObject jsonObject = JSON.parseObject(HttpUtils.postRequestNew(url,req));
            log.info("事件订阅推送接口（润之提供）,返回：{}",jsonObject);
            result.put("msg",NullProcessUtil.nvlToString(jsonObject.get(Constants.PARAM_MSG),""));
            result.put("code",NullProcessUtil.nvlToString(jsonObject.get(Constants.PARAM_CODE),""));
            return Result.data(result);
        } catch (Exception e) {
            log.error("事件订阅推送接口（润之提供）,异常：",e);
            return Result.fail("事件订阅推送接口（润之提供）,异常");
        }

    }
}
