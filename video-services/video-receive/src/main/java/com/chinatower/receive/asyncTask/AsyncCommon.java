package com.chinatower.receive.asyncTask;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.event.api.IReceiveApiService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.api.feign.client.AlarmFlowDeployFeign;
import com.chinatower.event.api.model.receive.MessagePushVo;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.OkhttpUtil;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.api.IIndustryDeviceService;
import com.chinatower.industry.dto.DeviceLayerReq;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.receive.constant.AlarmConstants;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.mapper.SecondRecogConfigMapper;
import com.chinatower.receive.mapper.WarningOrderMapper;
import com.chinatower.receive.mongodb.IotConfigMongoMapper;
import com.chinatower.receive.mongodb.ReceiveAlarmPointMapper;
import com.chinatower.receive.mongodb.ReceiveSatelliteMapper;
import com.chinatower.receive.service.IFlowLinkService;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.OperateService;
import com.chinatower.receive.vo.dto.*;
import com.chinatower.receive.vo.entity.AlarmOrderReceiveDTO;
import com.chinatower.receive.vo.entity.UserInfoVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.VideoControlAPI;
import com.chinatower.video.api.VideoPlayerAPI;
import com.chinatower.video.api.VideoQueryAPI;
import com.chinatower.video.api.entity.*;
import com.github.pagehelper.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 10:35
 */

@Slf4j
@Component
public class AsyncCommon {

    @Autowired
    private ReceiveSatelliteMapper receiveSatelliteMapper;

    @Autowired
    private ReceiveAlarmPointMapper receiveAlarmPointMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private SecondRecogConfigMapper secondRecogConfigMapper;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private IIndustryDeviceService industryDeviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private VideoQueryAPI videoQueryAPI;

    @Autowired
    private VideoPlayerAPI videoPlayerAPI;

    @Autowired
    private AsyncVideoCommon asyncVideoCommon;

    @Autowired
    private IFlowLinkService IFlowLinkService;

    @Autowired
    private OperateService operateService;

    @Autowired
    private IotConfigMongoMapper iotConfigMongoMapper;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    private static String APP_KEY="a1550b7692f144a486e7fd36cb0233ef";
    @Autowired
    private VideoControlAPI videoControlAPI;
    @Autowired
    private IVideoDeviceService videoDeviceService;
    @Resource
    private NetConnectUtils connectUtils;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private AlarmFlowDeployFeign alarmFlowDeployFeign;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${algorithm.appCode}")
    private String APP_CODE;
    @Value("${algorithm.sk}")
    private String SECRET_KEY;

    @Async("taskExecutor")
    public void saveMongoDb(List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmVos){
        try{
            //空白处遗留建表语句
            Date date = DateUtils.string2Date(receiveSatelliteAlarmVos.get(0).getAlarmTime());
            String yearMonth = DateUtils.dateToStringYM(date);
            String collectionName = TableConstant.WARN_ORDER_SATELLITE_TEBLE + yearMonth;
            Boolean exists = receiveSatelliteMapper.collectionExists(collectionName, MongoDBType.PRIMARY);
            if (!exists) {
                receiveSatelliteMapper.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
            }
            //卫星告警数据入mongo
            String dateStr = DateUtils.date2Str(new Date(),"yyyy-MM-dd HH:mm:ss");
            List<ReceiveSatelliteAlarmDto> receiveDtoList = new ArrayList<>();
            for (ReceiveSatelliteAlarmApiVo vo:receiveSatelliteAlarmVos) {
                ReceiveSatelliteAlarmDto dto = new ReceiveSatelliteAlarmDto();
                BeanUtil.copyProperties(vo,dto);
                if(vo.getExtendField() != null){
                    dto.setExtendField(JSON.toJSONString(vo.getExtendField()));
                }
                dto.setPushTime(dateStr);
                receiveDtoList.add(dto);
            }
            receiveSatelliteMapper.insertAll(receiveDtoList,collectionName,MongoDBType.PRIMARY);

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
            log.error("异步存储卫星告警信息到mongo异常",e);
        }
    }

    private List<IndexModel> getIndexModels() {
        List<IndexModel> indexModels = new ArrayList<>();
        BasicDBObject indexAlarmCode = new BasicDBObject();
        indexAlarmCode.put(Constants.PARAM_ALARM_CODE, 1);
        IndexOptions indexOptions1 = new IndexOptions().background(true).name("index_alarmCode");
        indexModels.add(new IndexModel(indexAlarmCode, indexOptions1));
        BasicDBObject indexAlarmTime = new BasicDBObject();
        indexAlarmTime.put(Constants.PARAM_ALARMTIME, 1);
        IndexOptions indexOptions2 = new IndexOptions().background(true).name("index_alarmTime");
        indexModels.add(new IndexModel(indexAlarmTime, indexOptions2));
        BasicDBObject indexPushCustomerFlag = new BasicDBObject();
        indexPushCustomerFlag.put(Constants.PARAM_PUSH_CUSTOMER_FLAG, 1);
        IndexOptions indexOptions3 = new IndexOptions().background(true).name("index_pushCustomerFlag");
        indexModels.add(new IndexModel(indexPushCustomerFlag, indexOptions3));
        BasicDBObject indexAlarmBody = new BasicDBObject();
        indexAlarmBody.put(Constants.PARAM_ALARM_BODY, 1);
        IndexOptions indexOptions4 = new IndexOptions().background(true).name("index_alarmBody");
        indexModels.add(new IndexModel(indexAlarmBody, indexOptions4));
        BasicDBObject indexAlarmType = new BasicDBObject();
        indexAlarmType.put(Constants.PARAM_ALARM_TYPE, 1);
        IndexOptions indexOptions5 = new IndexOptions().background(true).name("index_alarmType");
        indexModels.add(new IndexModel(indexAlarmType, indexOptions5));
        BasicDBObject indexConfidenceLevel = new BasicDBObject();
        indexConfidenceLevel.put(Constants.PARAM_CONFIDENCE_LEVEL, 1);
        IndexOptions indexOptions6 = new IndexOptions().background(true).name("index_confidenceLevel");
        indexModels.add(new IndexModel(indexConfidenceLevel, indexOptions6));
        BasicDBObject indexProvince = new BasicDBObject();
        indexProvince.put(Constants.PARAM_PROVINCE, 1);
        IndexOptions indexOptions7 = new IndexOptions().background(true).name("index_province");
        indexModels.add(new IndexModel(indexProvince, indexOptions7));
        BasicDBObject indexCity = new BasicDBObject();
        indexCity.put(Constants.PARAM_CITY, 1);
        IndexOptions indexOptions8 = new IndexOptions().background(true).name("index_city");
        indexModels.add(new IndexModel(indexCity, indexOptions8));
        BasicDBObject indexCounty = new BasicDBObject();
        indexCounty.put(Constants.PARAM_COUNTY, 1);
        IndexOptions indexOptions9 = new IndexOptions().background(true).name("index_county");
        indexModels.add(new IndexModel(indexCounty, indexOptions9));
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
                    if (keyDocument.containsKey(Constants.PARAM_ALARM_CODE)) {
                        success = true;
                    }
                }
            }
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    //物联配置信息累计值入MongoDB
    @Async("taskExecutor")
    public void factoryValueMongoHandle(List<Map<String,String>> mongoFactoryList,String mergeTime){
        if (!CollectionUtils.isEmpty(mongoFactoryList)){
            String collectionName =Constants.IOT_CONFIG_FACTORY_SUM_VALUE;
            //更新Mongo对应已有设备因子数据
            List<String> mongoDeviceCodeList = mongoFactoryList.stream().map(v-> v.get("deviceCode")).distinct().collect(Collectors.toList());
            List<IotFactoryMapDTO> iotFactoryMapDTOS = iotConfigMongoMapper.groupMongoFactoryValue(mongoDeviceCodeList, mergeTime);
            iotConfigMongoMapper.updateAll(mongoFactoryList,iotFactoryMapDTOS);
        }

    }

    //触发次数 防抖次数累计值入Redis
    @Async("taskExecutor")
    public void antiShakeValueRedisHandle(List<AntiShakeValueVO> antiShakeSaveList){
        //更新Mongo对应配置规则下的防抖次数
        for (AntiShakeValueVO antiShakeValueVO : antiShakeSaveList) {
            //key 配置id+规则id
            String key = Constants.ANTI_SHAKE_REDIS_KEY+"_"+antiShakeValueVO.getConfigId()+"_"+antiShakeValueVO.getGroupId();
            //获取剩余过期时间
            Long expire = RedisUtil.getExpire(key, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            if (expire>0){
                RedisUtil.set(key,antiShakeValueVO.getTriggerValue(),expire, TimeUnit.SECONDS,RedisDBType.PRIMARY);

            }else {
                RedisUtil.set(key,antiShakeValueVO.getTriggerValue(),antiShakeValueVO.getTimeoutValue(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
            }
        }
    }

    //持续超标/突然超标 防抖次数累计值入MongoDB
    @Async("taskExecutor")
    public void antiShakeValueMongoHandle(List<AntiShakeValueVO> antiShakeSaveList){
        //更新Mongo对应配置规则下的防抖次数
        iotConfigMongoMapper.updateAntiShakeAll(antiShakeSaveList);
    }

    //告警生效物联设备存入设备池Redis
    @Async("taskExecutor")
    public void deviceGroupsValueRedisHandle(List<IotDeviceGroupsVO> deviceGroupsVOList){
        //更新Mongo对应配置规则下的防抖次数
        for (IotDeviceGroupsVO deviceGroupsVO : deviceGroupsVOList) {
            //key 配置id+规则id
            String key = Constants.IOT_DEVICE_GROUPS_REDIS_KEY+"_"+deviceGroupsVO.getConfigId();
            RedisUtil.delete(key,RedisDBType.PRIMARY);
            RedisUtil.set(key,deviceGroupsVO.getDeviceGroupList(),RedisDBType.PRIMARY);
        }
    }

    //告警压制时效Redis
    @Async("taskExecutor")
    public void iotAlarmSuppressRedisHandle(List<IotAlarmSuppressVO> alarmSuppressVOList){
        //更新Redis对应配置规则下的告警压制时效
        for (IotAlarmSuppressVO iotAlarmSuppressVO : alarmSuppressVOList) {
            //key 配置id
            String key = Constants.IOT_ALARM_SUPPRESS_REDIS_KEY+"_"+iotAlarmSuppressVO.getConfigId();
            //获取剩余过期时间
            Long expire = RedisUtil.getExpire(key, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            //不存在过期时间则新增数值及时效
            if (expire<=0){
                RedisUtil.set(key,iotAlarmSuppressVO.getSuppressTime(),iotAlarmSuppressVO.getSuppressTime(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
            }
        }
    }

    @Async("taskExecutor")
    public void iotDeviceDeal(ReceiveAlarmVO receiveAlarmVO, TenantIotDeviceConfVO iotDeviceConf, String tableType) {
        log.info("物联设备发生告警处理开始");
        log.info("告警信息:{},物联联动设备信息{}", receiveAlarmVO,iotDeviceConf);
        if (null == receiveAlarmVO || null == iotDeviceConf) {
            return;
        }
        //从字典配置中获取物联设备发生告警抓图回调地址
        String imageUploadUrl = "";
        Result<List<SysDictDataResponse>> dictDataresult = systemFeignClient.dictDataType(AlarmConstants.IOT_SNAP_SHOT_BACK_URL);
        if (dictDataresult.getCode() == ResultCode.SUCCESS.getCode()) {
            List<SysDictDataResponse> sysDictDataList = dictDataresult.getData();
            if (!CollectionUtils.isEmpty(sysDictDataList)) {
                imageUploadUrl = sysDictDataList.get(0).getDictValue();
            }
        }
        log.info("iotDeviceDeal从字典配置中获取抓图回调地址:{}", imageUploadUrl);

            //联动动作
            String linkAction = iotDeviceConf.getLinkAction();
            if (StringUtils.isEmpty(linkAction)) {
                log.info("获取联动动作为空!");
                return;
            }
            String[] linkArray = linkAction.split(Constants.COMMA);
            List<TenantIotConfRelVO> confRelList = iotDeviceConf.getConfRels();

            if (!CollectionUtils.isEmpty(confRelList)) {
                //抓视频 入参集合
                List<VideoDownloadVO> downList = new ArrayList<>();
                for (TenantIotConfRelVO tenantIotConfRelVO : confRelList) {

                    //组装ptz参数
                    DoPtzLocationCmdReq doPtzLocationCmdReq = new DoPtzLocationCmdReq();
                    doPtzLocationCmdReq.setTenantId(receiveAlarmVO.getTenantId());
                    Float pan = tenantIotConfRelVO.getPan();
                    Float tilt = tenantIotConfRelVO.getTilt();
                    Float zoom = tenantIotConfRelVO.getZoom();
                    if (null != pan && null != tilt && null != zoom) {
                        doPtzLocationCmdReq.setPan(pan);
                        doPtzLocationCmdReq.setTilt(tilt);
                        doPtzLocationCmdReq.setZoom(zoom);
                    }

                    //组装抓图参数
                    PlayerSnapShotReq playerSnapShotReq = new PlayerSnapShotReq();
                    playerSnapShotReq.setStreamType(1);
                    playerSnapShotReq.setTenantId(receiveAlarmVO.getTenantId());
                    playerSnapShotReq.setImageUpUrl(imageUploadUrl + receiveAlarmVO.getWarningOrderId()+Constants.SPLIT_SIGN+tableType);
                    playerSnapShotReq.setIsNeedOfs("1");
                    String relDeviceCode = tenantIotConfRelVO.getDeviceCode();
                    String relChannelCode = tenantIotConfRelVO.getChannelCode();

                    doPtzLocationCmdReq.setDeviceCode(relDeviceCode);
                    doPtzLocationCmdReq.setChannelCode(relChannelCode);

                    String imgName = iotDeviceConf.getDeviceCode() + tenantIotConfRelVO.getChannelCode() + relChannelCode + receiveAlarmVO.getWarningOrderId();
                    playerSnapShotReq.setDeviceCode(relDeviceCode);
                    playerSnapShotReq.setChannelCode(relChannelCode);
                    playerSnapShotReq.setImageName(imgName);

                    for (String linkStr : linkArray) {
                        try {
                            //1:摄像机看向告警位置
                            if (linkStr.equals("1")) {
                                Result doLocationResult = videoControlAPI.doPtzLocationCmd(doPtzLocationCmdReq);
                                log.info("doPtzLocationCmd response:{}", doLocationResult);
                                if (Objects.equals(ResultCode.SUCCESS.getCode(), doLocationResult.getCode())) {
                                    //调用转动摄像头停止五秒后开始抓图和抓视频
                                    Thread.sleep(5000);
                                }

                            }
                            //2:抓图
                            else if (linkStr.equals("2")) {
                                log.info("snapShot-1-start:{}", playerSnapShotReq);
                                Result videoResult = videoPlayerAPI.snapShot(playerSnapShotReq);
                                log.info("snapShot-1-response:{}", videoResult);
                            }
                            //3:抓视频
                            else if (linkStr.equals("3")) {
                                // 联动抓视频 组装参数
                                VideoDownloadVO videoDownloadVO = new VideoDownloadVO();
                                videoDownloadVO.setDeviceCode(relDeviceCode);
                                videoDownloadVO.setChannelCode(relChannelCode);
                                videoDownloadVO.setTableType(tableType);
                                videoDownloadVO.setWarningOrderId(receiveAlarmVO.getWarningOrderId());
                                videoDownloadVO.setTenantId(receiveAlarmVO.getTenantId());
                                videoDownloadVO.setIndustryCode(receiveAlarmVO.getIndustryCode());
                                videoDownloadVO.setStorageType(2);
                                //随机16位
                                String uuId =  System.currentTimeMillis()+UUID.randomUUID().toString().replace("-", "").substring(0,16);
                                videoDownloadVO.setBatchId(uuId);
                                videoDownloadVO.setMemoryCycle(157680000);
                                videoDownloadVO.setOperateSourceType(2);
                                downList.add(videoDownloadVO);
                            }
                        } catch (InterruptedException  e) {
                            log.error("联动动作[" + linkStr + "],接口调用异常", e);
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            log.error("联动动作[" + linkStr + "],接口调用异常", e);
                        }
                    }
                }
                //异步调用抓视频
                asyncVideoCommon.captureVideo(downList);
            }
    }

    @Async("taskExecutor")
    public void satelliteSnapShot(List<ReceiveAlarmVO> list, String tableType) {
        try {
            log.info("卫星告警联动抓图入参:{}", JSON.toJSONString(list));
            List<ReceiveAlarmVO> alarms = JSON.parseArray(JSON.toJSONString(list),ReceiveAlarmVO.class);
            //从字典配置中获取物联设备发生告警抓图回调地址
            String imageUploadUrl = "";
            Result<List<SysDictDataResponse>> dictDataresult = systemFeignClient.dictDataType(AlarmConstants.SATELLITE_SNAP_SHOT_BACK_URL);
            if (dictDataresult.getCode() == ResultCode.SUCCESS.getCode()) {
                List<SysDictDataResponse> sysDictDataList = dictDataresult.getData();
                if (!CollectionUtils.isEmpty(sysDictDataList)) {
                    imageUploadUrl = sysDictDataList.get(0).getDictValue();
                }
            }
            log.info("satelliteSnapShot从字典配置中获取抓图回调地址:{}", imageUploadUrl);
            //查询告警来源-告警类型下的二次识别配置
            List<String> sourceTypeLIst = list.stream().map(v -> v.getWarningSource() + "-" + v.getAlarmType()).collect(Collectors.toList());
            List<SecondRecogReceiveVO> configList = secondRecogConfigMapper.selectSecondRecogByOnes(sourceTypeLIst);
            // 通过租户ID查询租户管理员的userId，用于调用转动到指定经纬度接口
            List<String> tenantIdList = alarms.stream().map(ReceiveAlarmVO::getTenantId).collect(Collectors.toList());
            // 预处理用户信息
            List<UserInfoVO> userInfoVOS = warningOrderMapper.getUserIdByTenantIdBatch(tenantIdList);
            Map<String, String> userInfoVOMap = userInfoVOS.stream().collect(Collectors.toMap(UserInfoVO::getTenantId, UserInfoVO::getUserId));

            // 存储所有要联动抓图的设备集合
            List<IndustryDeviceTree> shotDeviceList = new ArrayList<>();
            //需要二次识别配置的告警
            List<IndustryDeviceTree> secondIfList = new ArrayList<>();
            if(!StringUtils.isEmpty(imageUploadUrl)){

                String finalImageUploadUrl = imageUploadUrl;
                alarms.forEach(alarm -> {
                    // 没有透传字段则视为脏数据，不进行联动抓图
                    if(StringUtils.isEmpty(alarm.getExtraField())){
                        return;
                    }
                    //查询摄像机范围 默认3km
                    String distance = "3000";
                    // 过滤匹配 二次识别配置
                    List<SecondRecogReceiveVO> filteredList = configList.stream()
                            .filter(config ->
                                    config.getTenantId().equals(alarm.getTenantId()) &&
                                            config.getIndustryCode().equals(alarm.getIndustryCode()) &&
                                            config.getAlarmSourceValue().equals(alarm.getWarningSource()) &&
                                            config.getAlarmTypeValue().equals(alarm.getAlarmType())
                            )
                            .collect(Collectors.toList());
                    //不为空则需要二次识别
                    if (!CollectionUtils.isEmpty(filteredList) && filteredList.size() > 0) {
                        distance = filteredList.get(0).getLinkageCamera();
                    }

                    //调用接口查询该租户已订购的#{distance}米以内的摄像机
                    DeviceLayerReq deviceLayerReq = new DeviceLayerReq();
                    deviceLayerReq.setTenantId(alarm.getTenantId());
                    deviceLayerReq.setIndustryCode(alarm.getIndustryCode());
                    deviceLayerReq.setLongitude(Double.parseDouble(alarm.getLongitude()));
                    deviceLayerReq.setLatitude(Double.parseDouble(alarm.getLatitude()));
                    deviceLayerReq.setDistance(distance);
                    log.info("请求联动摄像机报文：{}",JSON.toJSONString(deviceLayerReq));
                    Result<List<IndustryDeviceTree>> deviceResult = industryDeviceService.queryDevice3km(deviceLayerReq);
                    log.info("获取到的联动摄像机信息：{}",JSON.toJSONString(deviceResult));
                    if(ObjectUtil.isNotNull(deviceResult) && Constants.CODE_200 == deviceResult.getCode() && !CollectionUtils.isEmpty(deviceResult.getData())){
                        List<IndustryDeviceTree> industryDeviceTrees = deviceResult.getData();

                        // 批量获取所有设备的有效参考点
                        List<String> deviceCodeList = industryDeviceTrees.stream().map(IndustryDeviceTree::getDeviceCode).collect(Collectors.toList());
                        List<DeviceRefPointVO> deviceRefPointList = deviceRepository.getDevicePointsValidBatch(deviceCodeList);

                        if(!CollectionUtils.isEmpty(deviceRefPointList)){
                            log.info("获取到的有效参考点信息：{}，对应租户：{}",JSON.toJSONString(deviceRefPointList),alarm.getTenantId());

                            // 从远到近循环所有设备，取存在有效参考点的设备进行联动抓图，都没有则不抓图
                            for (IndustryDeviceTree industryDeviceTree : industryDeviceTrees) {
                                List<DeviceRefPointVO> deviceRefPointVOS = deviceRefPointList.stream().filter(temp -> industryDeviceTree.getDeviceCode().equals(temp.getDeviceCode())).collect(Collectors.toList());
                                // 获取当前设备的有效参考点
                                if(ObjectUtil.isNotNull(industryDeviceTree) && ObjectUtil.isNotNull(industryDeviceTree.getChannelInfoVOList()) &&
                                        !CollectionUtils.isEmpty(deviceRefPointVOS)){
                                    // 根据设备+经纬度做聚合处理，同一设备同一经纬度只做一次联动抓图操作，同步更新所有匹配告警
                                    List<IndustryDeviceTree> treeList = shotDeviceList.stream().filter(temp -> temp.getDeviceCode().equals(industryDeviceTree.getDeviceCode())
                                            && temp.getAlarmLatitude().equals(alarm.getLatitude()) && temp.getAlarmLongitude().equals(alarm.getLongitude())).collect(Collectors.toList());
                                    if(!CollectionUtils.isEmpty(treeList)){
                                        treeList.forEach(temp -> temp.setOrderIds(temp.getOrderIds()+Constants.COMMA+alarm.getWarningOrderId()));
                                    }else{
                                        industryDeviceTree.setAlarmLatitude(alarm.getLatitude());
                                        industryDeviceTree.setAlarmLongitude(alarm.getLongitude());
                                        industryDeviceTree.setOrderIds(alarm.getWarningOrderId());
                                        industryDeviceTree.setTenantId(alarm.getTenantId());
                                        industryDeviceTree.setIndustryCode(alarm.getIndustryCode());
                                        shotDeviceList.add(industryDeviceTree);
                                    }
                                    //联动动作 0:抓图 1:抓视频
                                    if (!CollectionUtils.isEmpty(filteredList) && filteredList.size() > 0) {
                                        IndustryDeviceTree tree = new IndustryDeviceTree();
                                        tree.setOrderIds(alarm.getWarningOrderId());
                                        tree.setTenantId(alarm.getTenantId());
                                        tree.setIndustryCode(alarm.getIndustryCode());
                                        tree.setLinkageAction(filteredList.get(0).getLinkageAction());
                                        secondIfList.add(tree);
                                    }
                                }
                            }
                        }


                    }else{
                        log.info("3公里范围内未查询到订购的摄像机信息：{},对应租户加设备:{}",alarm.getAlarmCode(),alarm.getTenantId()+Constants.COMMA+alarm.getDeviceCode());
                    }
                });
                log.info("卫星告警聚合后需要抓图的设备列表:{}",JSON.toJSONString(shotDeviceList));
                //抓视频 入参集合
                List<VideoDownloadVO> downList = new ArrayList<>();
                shotDeviceList.forEach(industryDeviceTree ->
                        industryDeviceTree.getChannelInfoVOList().forEach(indChannelInfoVO -> {
                            //对所有通道进行联动抓图操作
                            try {
                                VideoPtzQueryRequest videoPtzQueryReq = new VideoPtzQueryRequest();
                                videoPtzQueryReq.setDeviceCode(industryDeviceTree.getDeviceCode());
                                videoPtzQueryReq.setChannelCode(indChannelInfoVO.getChannelCode());
                                videoPtzQueryReq.setCameraLon(Double.parseDouble(industryDeviceTree.getLongitude()));
                                videoPtzQueryReq.setCameraLat(Double.parseDouble(industryDeviceTree.getLatitude()));

                                videoPtzQueryReq.setPointLat(Double.parseDouble(industryDeviceTree.getAlarmLatitude()));
                                videoPtzQueryReq.setPointLon(Double.parseDouble(industryDeviceTree.getAlarmLongitude()));

                                videoPtzQueryReq.setUserId(userInfoVOMap.get(industryDeviceTree.getTenantId()));
                                videoPtzQueryReq.setTenantId(industryDeviceTree.getTenantId());
                                log.info("转动到指定经纬度请求报文：{}",JSON.toJSONString(videoPtzQueryReq));
                                Result<PresetData> turnResult = videoQueryAPI.turnToPtzByPointAngle2(videoPtzQueryReq);
                                log.info("对摄像机：{}，通道：{}，转动到指定经纬度返回信息：{}",industryDeviceTree.getDeviceCode(),indChannelInfoVO.getChannelCode(),JSON.toJSONString(turnResult));
                                if(ObjectUtil.isNotNull(turnResult) && ObjectUtil.isNotNull(turnResult.getCode()) && Constants.CODE_200 == turnResult.getCode()){

                                    //调用转动摄像头停止五秒后开始联动抓图
                                    Thread.sleep(5000);

                                    //组装抓图参数
                                    PlayerSnapShotReq playerSnapShotReq = new PlayerSnapShotReq();
                                    playerSnapShotReq.setStreamType(1);
                                    playerSnapShotReq.setTenantId(industryDeviceTree.getTenantId());
                                    playerSnapShotReq.setIsNeedOfs("1");
                                    String relDeviceCode = industryDeviceTree.getDeviceCode();
                                    String relChannelCode = indChannelInfoVO.getChannelCode();
                                    String uuIdNum = UUID.randomUUID().toString().replace("-", "").substring(0,6);
                                    String imgName = relDeviceCode + relChannelCode + uuIdNum;
                                    playerSnapShotReq.setDeviceCode(relDeviceCode);
                                    playerSnapShotReq.setChannelCode(relChannelCode);
                                    playerSnapShotReq.setImageName(imgName);
                                    //抓图告警ids
                                    String orderIds ="";
                                    //二次配置抓图告警ids
                                    String secondOrderIds ="";
                                    String[] split = industryDeviceTree.getOrderIds().split(",");
                                    for (String orderId : split) {
                                        //是否需要二次抓图
                                        boolean ifSecond = false;
                                        List<IndustryDeviceTree> treeList = secondIfList.stream().filter(v -> v.getOrderIds().equals(orderId)).collect(Collectors.toList());
                                        if (!CollectionUtils.isEmpty(treeList) && treeList.size()>0){
                                            IndustryDeviceTree deviceTree = treeList.get(0);
                                            if (!StringUtils.isEmpty(deviceTree.getLinkageAction())){
                                                if (deviceTree.getLinkageAction().contains("0")){
                                                    ifSecond = true;
                                                    if (!StringUtils.isEmpty(secondOrderIds)){
                                                        secondOrderIds =  secondOrderIds+","+orderId;
                                                    }else {
                                                        secondOrderIds =  orderId;
                                                    }
                                                }
                                                if (deviceTree.getLinkageAction().contains("1")){
                                                    // 联动抓视频 组装参数
                                                    VideoDownloadVO videoDownloadVO = new VideoDownloadVO();
                                                    videoDownloadVO.setDeviceCode(relDeviceCode);
                                                    videoDownloadVO.setChannelCode(relChannelCode);
                                                    videoDownloadVO.setTableType(tableType);
                                                    videoDownloadVO.setWarningOrderId(orderId);
                                                    videoDownloadVO.setTenantId(deviceTree.getTenantId());
                                                    videoDownloadVO.setIndustryCode(deviceTree.getIndustryCode());
                                                    videoDownloadVO.setStorageType(2);
                                                    videoDownloadVO.setMemoryCycle(157680000);
                                                    videoDownloadVO.setOperateSourceType(2);
                                                    //随机16位
                                                    String uuId =  System.currentTimeMillis()+ UUID.randomUUID().toString().replace("-", "").substring(0,16);
                                                    videoDownloadVO.setBatchId(uuId);
                                                    downList.add(videoDownloadVO);
                                                }
                                            }
                                        }
                                        if (!ifSecond){
                                            if (!StringUtils.isEmpty(orderIds)){
                                                orderIds =  orderIds+","+orderId;
                                            }else {
                                                orderIds =  orderId;
                                            }
                                        }
                                    }
                                    if (!StringUtils.isEmpty(orderIds)){
                                        playerSnapShotReq.setImageUpUrl(finalImageUploadUrl + tableType+Constants.SPLIT_SIGN+orderIds);
                                        // 联动抓图
                                        log.info("snapShot-2-request:{}", JSON.toJSONString(playerSnapShotReq));
                                        Result videoResult = videoPlayerAPI.snapShot(playerSnapShotReq);
                                        log.info("snapShot-2-response:{}", JSON.toJSONString(videoResult));
                                    }
                                    if (!StringUtils.isEmpty(secondOrderIds)){
                                        playerSnapShotReq.setImageUpUrl(finalImageUploadUrl + tableType+Constants.SPLIT_SIGN+secondOrderIds);
                                        // 联动抓图
                                        log.info("snapShot-3-request:{}", JSON.toJSONString(playerSnapShotReq));
                                        Result videoResult = videoPlayerAPI.snapShot(playerSnapShotReq);
                                        log.info("snapShot-3-response:{}", JSON.toJSONString(videoResult));
                                    }


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.info("通过关联摄像机调用看这里并抓图异常：{}",JSON.toJSONString(industryDeviceTree));
                                Thread.currentThread().interrupt();
                            }
                        })
                );
                //异步调用抓视频
                asyncVideoCommon.captureVideo(downList);
            }

        }catch (Exception e){
            log.error("卫星告警获取联动摄像机抓图异常",e);
        }
    }

    @Async("taskExecutor")
    //卫片告警抓图抓视频
    public void satelliteImagesSnapShot(List<ReceiveAlarmVO> list, String tableType) {
        try {
            log.info("卫片告警联动抓图入参:{}", JSON.toJSONString(list));
            List<ReceiveAlarmVO> alarms = JSON.parseArray(JSON.toJSONString(list),ReceiveAlarmVO.class);
            //判断卫片告警是否需要二次识别 不需要不进行抓图抓视频
            String isSecondIdentify = alarms.get(0).getIsSecondIdentify();
            String linkageCamera = alarms.get(0).getLinkageCamera();
            String linkageAction = alarms.get(0).getLinkageAction();
            String selectAlgorithm = alarms.get(0).getSelectAlgorithm();
            if (StringUtil.isEmpty(selectAlgorithm)){
                return;
            }
            //如果需要抓图获取抓图回调地址
            String imageUploadUrl = "";
            if (linkageAction.contains("0")){
                //从字典配置中获取物联设备发生告警抓图回调地址
                Result<List<SysDictDataResponse>> dictDataresult = systemFeignClient.dictDataType(AlarmConstants.SATELLITE_SNAP_SHOT_BACK_URL);
                if (dictDataresult.getCode() == ResultCode.SUCCESS.getCode()) {
                    List<SysDictDataResponse> sysDictDataList = dictDataresult.getData();
                    if (!CollectionUtils.isEmpty(sysDictDataList)) {
                        imageUploadUrl = sysDictDataList.get(0).getDictValue();
                    }
                }
                log.info("satelliteImagesSnapShot从字典配置中获取抓图回调地址:{}", imageUploadUrl);
            }
            String finalImageUploadUrl =imageUploadUrl;
            // 通过租户ID查询租户管理员的userId，用于调用转动到指定经纬度接口
            List<String> tenantIdList = alarms.stream().map(ReceiveAlarmVO::getTenantId).collect(Collectors.toList());
            // 预处理用户信息
            List<UserInfoVO> userInfoVOS = warningOrderMapper.getUserIdByTenantIdBatch(tenantIdList);
            Map<String, String> userInfoVOMap = userInfoVOS.stream().collect(Collectors.toMap(UserInfoVO::getTenantId, UserInfoVO::getUserId));
            // 存储所有要联动抓图的设备集合
            List<IndustryDeviceTree> shotDeviceList = new ArrayList<>();
            alarms.forEach(alarm -> {
                //判断联动摄像机范围 默认3km
                String distance = "3000";
                if (StringUtil.isNotEmpty(linkageCamera)) {
                    distance = linkageCamera;
                }
                //调用接口查询该租户已订购的#{distance}米以内的摄像机
                DeviceLayerReq deviceLayerReq = new DeviceLayerReq();
                deviceLayerReq.setTenantId(alarm.getTenantId());
                deviceLayerReq.setIndustryCode(alarm.getIndustryCode());
                deviceLayerReq.setLongitude(Double.parseDouble(alarm.getLongitude()));
                deviceLayerReq.setLatitude(Double.parseDouble(alarm.getLatitude()));
                deviceLayerReq.setDistance(distance);
                log.info("请求联动摄像机报文：{}",JSON.toJSONString(deviceLayerReq));
                Result<List<IndustryDeviceTree>> deviceResult = industryDeviceService.queryDevice3km(deviceLayerReq);
                log.info("获取到的联动摄像机信息：{}",JSON.toJSONString(deviceResult));
                if(ObjectUtil.isNotNull(deviceResult) && Constants.CODE_200 == deviceResult.getCode() && !CollectionUtils.isEmpty(deviceResult.getData())){
                    List<IndustryDeviceTree> industryDeviceTrees = deviceResult.getData();

                    // 批量获取所有设备的有效参考点
                    List<String> deviceCodeList = industryDeviceTrees.stream().map(IndustryDeviceTree::getDeviceCode).collect(Collectors.toList());
                    List<DeviceRefPointVO> deviceRefPointList = deviceRepository.getDevicePointsValidBatch(deviceCodeList);

                    if(!CollectionUtils.isEmpty(deviceRefPointList)){
                        log.info("获取到的有效参考点信息：{}，对应租户：{}",JSON.toJSONString(deviceRefPointList),alarm.getTenantId());

                        // 从远到近循环所有设备，取存在有效参考点的设备进行联动抓图，都没有则不抓图
                        for (IndustryDeviceTree industryDeviceTree : industryDeviceTrees) {
                            List<DeviceRefPointVO> deviceRefPointVOS = deviceRefPointList.stream().filter(temp -> industryDeviceTree.getDeviceCode().equals(temp.getDeviceCode())).collect(Collectors.toList());
                            // 获取当前设备的有效参考点
                            if(ObjectUtil.isNotNull(industryDeviceTree) && ObjectUtil.isNotNull(industryDeviceTree.getChannelInfoVOList()) &&
                                    !CollectionUtils.isEmpty(deviceRefPointVOS)){
                                // 根据设备+经纬度做聚合处理，同一设备同一经纬度只做一次联动抓图操作，同步更新所有匹配告警
                                List<IndustryDeviceTree> treeList = shotDeviceList.stream().filter(temp -> temp.getDeviceCode().equals(industryDeviceTree.getDeviceCode())
                                        && temp.getAlarmLatitude().equals(alarm.getLatitude()) && temp.getAlarmLongitude().equals(alarm.getLongitude())).collect(Collectors.toList());
                                if(!CollectionUtils.isEmpty(treeList)){
                                    treeList.forEach(temp -> temp.setOrderIds(temp.getOrderIds()+Constants.COMMA+alarm.getWarningOrderId()));
                                }else{
                                    //联动动作 0:抓图 1:抓视频
                                    industryDeviceTree.setAlarmLatitude(alarm.getLatitude());
                                    industryDeviceTree.setAlarmLongitude(alarm.getLongitude());
                                    industryDeviceTree.setOrderIds(alarm.getWarningOrderId());
                                    industryDeviceTree.setTenantId(alarm.getTenantId());
                                    industryDeviceTree.setIndustryCode(alarm.getIndustryCode());
                                    industryDeviceTree.setLinkageAction(linkageAction);
                                    shotDeviceList.add(industryDeviceTree);
                                }
                            }
                        }
                    }
                }else{
                    log.info("3公里范围内未查询到订购的摄像机信息：{},对应租户加设备:{}",alarm.getAlarmCode(),alarm.getTenantId()+Constants.COMMA+alarm.getDeviceCode());
                }
            });
            log.info("卫片告警聚合后需要抓图的设备列表:{}",JSON.toJSONString(shotDeviceList));
            //抓视频 入参集合
            List<VideoDownloadVO> downList = new ArrayList<>();
            shotDeviceList.forEach(industryDeviceTree ->
                        industryDeviceTree.getChannelInfoVOList().forEach(indChannelInfoVO -> {
                            //对所有通道进行联动抓图操作
                            try {
                                VideoPtzQueryRequest videoPtzQueryReq = new VideoPtzQueryRequest();
                                videoPtzQueryReq.setDeviceCode(industryDeviceTree.getDeviceCode());
                                videoPtzQueryReq.setChannelCode(indChannelInfoVO.getChannelCode());
                                videoPtzQueryReq.setCameraLon(Double.parseDouble(industryDeviceTree.getLongitude()));
                                videoPtzQueryReq.setCameraLat(Double.parseDouble(industryDeviceTree.getLatitude()));

                                videoPtzQueryReq.setPointLat(Double.parseDouble(industryDeviceTree.getAlarmLatitude()));
                                videoPtzQueryReq.setPointLon(Double.parseDouble(industryDeviceTree.getAlarmLongitude()));

                                videoPtzQueryReq.setUserId(userInfoVOMap.get(industryDeviceTree.getTenantId()));
                                videoPtzQueryReq.setTenantId(industryDeviceTree.getTenantId());
                                log.info("转动到指定经纬度请求报文：{}",JSON.toJSONString(videoPtzQueryReq));
                                Result<PresetData> turnResult = videoQueryAPI.turnToPtzByPointAngle2(videoPtzQueryReq);
                                log.info("对摄像机：{}，通道：{}，转动到指定经纬度返回信息：{}",industryDeviceTree.getDeviceCode(),indChannelInfoVO.getChannelCode(),JSON.toJSONString(turnResult));
                                if(ObjectUtil.isNotNull(turnResult) && ObjectUtil.isNotNull(turnResult.getCode()) && Constants.CODE_200 == turnResult.getCode()){
                                    //调用转动摄像头停止五秒后开始联动抓图
                                    Thread.sleep(5000);
                                    //组装抓图参数
                                    PlayerSnapShotReq playerSnapShotReq = new PlayerSnapShotReq();
                                    playerSnapShotReq.setStreamType(1);
                                    playerSnapShotReq.setTenantId(industryDeviceTree.getTenantId());
                                    playerSnapShotReq.setIsNeedOfs("1");
                                    String relDeviceCode = industryDeviceTree.getDeviceCode();
                                    String relChannelCode = indChannelInfoVO.getChannelCode();
                                    String uuIdNum = UUID.randomUUID().toString().replace("-", "").substring(0,6);
                                    String imgName = relDeviceCode + relChannelCode + uuIdNum;
                                    playerSnapShotReq.setDeviceCode(relDeviceCode);
                                    playerSnapShotReq.setChannelCode(relChannelCode);
                                    playerSnapShotReq.setImageName(imgName);
                                    //抓图告警ids
                                    String orderIds ="";
                                    String[] split = industryDeviceTree.getOrderIds().split(",");
                                    for (String orderId : split) {
                                        if (linkageAction.contains("0")){
                                            if (!StringUtils.isEmpty(orderIds)){
                                                orderIds =  orderIds+","+orderId;
                                            }else {
                                                orderIds =  orderId;
                                            }
                                        }
                                        if (linkageAction.contains("1")){
                                            // 联动抓视频 组装参数
                                            VideoDownloadVO videoDownloadVO = new VideoDownloadVO();
                                            videoDownloadVO.setDeviceCode(relDeviceCode);
                                            videoDownloadVO.setChannelCode(relChannelCode);
                                            videoDownloadVO.setTableType(tableType);
                                            videoDownloadVO.setWarningOrderId(orderId);
                                            videoDownloadVO.setTenantId(industryDeviceTree.getTenantId());
                                            videoDownloadVO.setIndustryCode(industryDeviceTree.getIndustryCode());
                                            videoDownloadVO.setStorageType(2);
                                            videoDownloadVO.setMemoryCycle(157680000);
                                            videoDownloadVO.setOperateSourceType(2);
                                            //随机16位
                                            String uuId =  System.currentTimeMillis()+ UUID.randomUUID().toString().replace("-", "").substring(0,16);
                                            videoDownloadVO.setBatchId(uuId);
                                            downList.add(videoDownloadVO);
                                        }
                                    }
                                    if (!StringUtils.isEmpty(orderIds) && StringUtil.isNotEmpty(finalImageUploadUrl)){
                                        playerSnapShotReq.setImageUpUrl(finalImageUploadUrl + tableType+Constants.SPLIT_SIGN+orderIds+Constants.SPLIT_SIGN+selectAlgorithm);
                                        // 联动抓图
                                        log.info("卫片snapShot-2-request:{}", JSON.toJSONString(playerSnapShotReq));
                                        Result videoResult = videoPlayerAPI.snapShot(playerSnapShotReq);
                                        log.info("卫片snapShot-2-response:{}", JSON.toJSONString(videoResult));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.info("通过关联摄像机调用看这里并抓图异常：{}",JSON.toJSONString(industryDeviceTree));
                                Thread.currentThread().interrupt();
                            }
                        })
                );
                //异步调用抓视频
                asyncVideoCommon.captureVideo(downList);

        }catch (Exception e){
            log.error("卫星告警获取联动摄像机抓图异常",e);
        }
    }

    @Async("taskExecutor")
    public void sync(ReceiveAlarmVO item) {
        log.info("推送属地运营研判开始:{}", JSON.toJSONString(item));
        try {
            Result result = RemoteNodeUtils.create(item.getOperationNode())
                    .getFeign(IReceiveApiService.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                    .syncAlarm(item);
            log.info("推送属地运营研判结束:{}", JSON.toJSONString(result));
        } catch (Exception e) {
            log.error("推送属地运营研判开始异常:{}", e);
        }
    }

    @Async("taskExecutor")
    public void messagePush(MessagePushVo dto) {
        log.info(Constants.LOG_MESSAGEPUSH_PARAM + JSON.toJSONString(dto));
        try {
            //默认在短信发送区间内
            dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_Y);
            if (!StringUtils.isEmpty(dto.getAlarmFlowId()) && !StringUtils.isEmpty(dto.getLinkId())) {
                String tenantId = dto.getTenantId();
                String linkId = dto.getLinkId();
                //消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC系统消息 4、PC告警声音
                String msgNotifyType = dto.getMsgNotifyType();
                //是否通知管理员，0，不通知，1通知
                String notifyAdminFlag = dto.getNotifyAdminFlag();
                //是否通知领导，0，不通知，1通知
                String leaderFlag = dto.getLeaderFlag();
                //用户类型，1用户，2角色
                String userType = null;
//                String roleId = null;
//                Long attrId = null;

                if (!StringUtils.isEmpty(dto.getUserId())) {
                    dto.setUserType(Constants.USER_TYPE_1);
                }
                else if (!StringUtils.isEmpty(dto.getRoleId())) {
                    dto.setUserType(Constants.USER_TYPE_2);
                }
                //flowId等于模板id 则flowId置空
                Long alarmFlowId = org.apache.commons.lang3.StringUtils.isNotBlank(dto.getFlowTemplate()) && dto.getFlowTemplate().equals(dto.getAlarmFlowId())?null:Long.valueOf(dto.getAlarmFlowId());
                Long flowTemplate = org.apache.commons.lang3.StringUtils.isNotBlank(dto.getFlowTemplate())?Long.valueOf(dto.getFlowTemplate()):null;
                VmFlowLinkDto vmFlowLinkDto = IFlowLinkService.getFlowLinkAndConfig(dto.getAppVerCode(),dto.getIndustryCode(),tenantId,flowTemplate,alarmFlowId, Integer.parseInt(linkId));
                VmFlowLinkAttrDto vmFlowLinkAttrDto = null;
                if(vmFlowLinkDto!=null){
                    vmFlowLinkAttrDto = vmFlowLinkDto.getFlowLinkAttrDto();
                    dto.setVmFlowLinkAttrDto(vmFlowLinkAttrDto);
                    //配置的短信模板时间区间
                    String msgSendTimeInterval = vmFlowLinkAttrDto.getMsgSendTimeInterval();
                    log.info("ReceiveRepositoryImpl-messagePush-短信模板配置时间区间-"+JSON.toJSONString(msgSendTimeInterval));
                    if(!StringUtils.isEmpty(msgSendTimeInterval) && msgSendTimeInterval.contains(Constants.SEP)){
                        try{
                            //根据-切分为配置开始时间和结束后时间
                            String[] msgSendTimeArr = msgSendTimeInterval.split(Constants.SEP);
                            if(!msgSendTimeArr[0].contains(Constants.COLON) || !msgSendTimeArr[1].contains(Constants.COLON)){
                                log.info("ReceiveRepositoryImpl-messagePush-短信模板配置时间格式有误-默认发送");
                            }else{
                                //判断当前时间是否在该时间区间内（HH:mm:ss）
                                String[] nowTime = com.chinatower.common.core.utils.DateUtils.dateTimeNow("HH:mm:ss").split(Constants.COLON);
                                String[] beginTime = msgSendTimeArr[0].split(Constants.COLON);
                                String[] endTime = msgSendTimeArr[1].split(Constants.COLON);
                                //如果当前时间不在配置范围内，不予发送短信
                                if(!this.compareTime(nowTime,beginTime,endTime)){
                                    dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
                                }
                            }
                        }catch (Exception e){
                            log.error("ReceiveRepositoryImpl-messagePush-短信模板配置时间比较报错-默认发送:",e);
                        }
                    }
                    //定时推送不发送短信消息
                    if(Constants.ONE.equals(vmFlowLinkAttrDto.getTimePushType())){
                        dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
                    }
                }
                if (!StringUtils.isEmpty(msgNotifyType)) {
                    dto.setMsgNotifyType(msgNotifyType);
                    dto.setNotifyAdminFlag(notifyAdminFlag);
                    // 根据事件状态以及事件编码查询事发地点
                    String orderId = dto.getOrderId();
                    String address = dto.getAddress() == null?"":dto.getAddress();
                    String alarmTime = dto.getAlarmTime() == null?"":dto.getAlarmTime();
                    String warningTypeId = dto.getTypeValue() == null?"":dto.getTypeValue();
                    String warningTypeName = "";
                    String gridId =dto.getGridId();
                    try{
                        if (Constants.SYS_MSG_TYPE_2.equals(dto.getNotifyType())) {
                            //AlarmOperateDTO alarmOperateDTO = alarmOperateMapper.getAlarmOperate(orderId);
                            AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(orderId,null);
                            if(alarmOperateDTO != null){
                                if(StringUtils.isEmpty(gridId)){
                                    gridId = alarmOperateDTO.getGridId();
                                }
                                if(StringUtils.isEmpty(dto.getAddress())){
                                    address = alarmOperateDTO.getAddress();
                                }else{
                                    address = dto.getAddress();
                                }
                                alarmTime = alarmOperateDTO.getAlarmTime();
                                if(StringUtils.isEmpty(dto.getWarningTypeId())){
                                    warningTypeId = alarmOperateDTO.getWarningTypeId();
                                }else{
                                    warningTypeId = dto.getWarningTypeId();
                                }
                                //行业版本编码
                                if(StringUtils.isEmpty(dto.getIndustryCode())){
                                    dto.setIndustryCode(alarmOperateDTO.getTradeId());
                                }
                                if(StringUtils.isEmpty(dto.getAppVerCode())){
                                    dto.setAppVerCode(alarmOperateDTO.getTradeVerCode());
                                }
                                if(StringUtils.isEmpty(dto.getDeviceCode())){
                                    dto.setDeviceCode(alarmOperateDTO.getDeviceCode());
                                }
                                if(StringUtils.isEmpty(dto.getCityId())){
                                    dto.setCityId(alarmOperateDTO.getCityId());
                                }
                                if(StringUtils.isEmpty(dto.getProvinceId())){
                                    dto.setProvinceId(alarmOperateDTO.getProvinceId());
                                }
                                if(StringUtils.isEmpty(dto.getCountyId())){
                                    dto.setCountyId(alarmOperateDTO.getCountyId());
                                }
                                if(StringUtils.isEmpty(dto.getWarningSource())){
                                    dto.setWarningSource(alarmOperateDTO.getWarningSource());
                                }
                                if(StringUtils.isEmpty(dto.getNotifyTitle())){
                                    dto.setNotifyTitle(alarmOperateDTO.getAlarmBody());
                                }
                                if(StringUtils.isEmpty(dto.getAlarmTime())){
                                    dto.setAlarmTime(alarmOperateDTO.getAlarmTime());
                                }
                            }
                        }
                        else {
                            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);
                            AlarmOrderReceiveDTO alarmOrderReceiveDTO = new AlarmOrderReceiveDTO();
                            alarmOrderReceiveDTO.setOrderId(orderId);
                            alarmOrderReceiveDTO.setTableName(alarmTableName);
                            WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderReceiveDTO);
                            if(warnOrderStatusDTO != null){
                                if(StringUtils.isEmpty(gridId)){
                                    gridId = warnOrderStatusDTO.getGridId();
                                }
                                if(StringUtils.isEmpty(dto.getAddress())){
                                    address = warnOrderStatusDTO.getAddress();
                                }else{
                                    address = dto.getAddress();
                                }
                                alarmTime = warnOrderStatusDTO.getAlarmTime();
                                if(StringUtils.isEmpty(dto.getWarningTypeId())){
                                    warningTypeId = warnOrderStatusDTO.getWarningTypeId();
                                }else{
                                    warningTypeId = dto.getWarningTypeId();
                                }
                                //行业版本编码
                                if(StringUtils.isEmpty(dto.getIndustryCode())){
                                    dto.setIndustryCode(warnOrderStatusDTO.getIndustryCode());
                                }
                                if(StringUtils.isEmpty(dto.getAppVerCode())){
                                    dto.setAppVerCode(warnOrderStatusDTO.getTradeVerCode());
                                }
                                if(StringUtils.isEmpty(dto.getDeviceCode())){
                                    dto.setDeviceCode(warnOrderStatusDTO.getDeviceCode());
                                }
                                if(StringUtils.isEmpty(dto.getCityId())){
                                    dto.setCityId(warnOrderStatusDTO.getCityId());
                                }
                                if(StringUtils.isEmpty(dto.getProvinceId())){
                                    dto.setProvinceId(warnOrderStatusDTO.getProvinceId());
                                }
                                if(StringUtils.isEmpty(dto.getCountyId())){
                                    dto.setCountyId(warnOrderStatusDTO.getCountyId());
                                }
                                if(StringUtils.isEmpty(dto.getWarningSource())){
                                    dto.setWarningSource(warnOrderStatusDTO.getWarningSource());
                                }
                                if(StringUtils.isEmpty(dto.getNotifyTitle())){
                                    dto.setNotifyTitle(warnOrderStatusDTO.getAlarmBody());
                                }
                                if(StringUtils.isEmpty(dto.getAlarmTime())){
                                    dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
                                }
                            }
                        }
                    }catch(Exception e){
                        log.error("根据事件状态以及事件编码查询事发地点，查询告警信息异常",e);
                    }
                    //如果不是从事件流转过来有指定用户得，查询该工单匹配流程得角色和用户
                    if(!Constants.ONE.equals(dto.getFlowFlag())){
                        if(vmFlowLinkDto!=null){
                            if(vmFlowLinkAttrDto!=null){
                                //有勾选网格员处理模式
                                this.getUserOrRoleList(vmFlowLinkAttrDto,gridId,alarmFlowId,linkId,flowTemplate,dto);

                            }
                        }
                    }

                    AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
                    if(alarmTypeInfo!=null){
                        warningTypeName= alarmTypeInfo.getTypeName();
                    }
                    //地址为空时，赋值地址
                    if (StringUtils.isEmpty(dto.getAddress()) || !StringUtils.isEmpty(address)) {
                        dto.setAddress(address);
                    }
                    if (StringUtils.isEmpty(dto.getNotifyTitle())) {
                        dto.setNotifyTitle(warningTypeName);
                    }
                    //包含短信时，通知组装短信通知内容
                    if (msgNotifyType.contains(Constants.MESSAGE_PUSH_TYPE_SMS)) {
                        //                        dto.setMsg(MessageFormat.format(Constants.MSG, alarmTime, address, warningTypeName));
                        dto.setAlarmTime(alarmTime);
                        dto.setAddress(address);
                        dto.setWarningTypeName(warningTypeName);
                    }
                    //领导批示时判断是否配置通知
                    if (Constants.SYS_MSG_TYPE_3.equals(dto.getNotifyType()) ) {
                        if(Constants.YES_FLAG.equals(leaderFlag)){
                            iTradeWarningOrderService.messagePush(dto);
                        }
                    }
                    //其他通知
                    else {
                        dto.setWarningTypeName(warningTypeName);
                        dto.setWxSendFlag(Constants.ONE);
                        iTradeWarningOrderService.messagePush(dto);
                    }
                }
            }else if(Constants.TWO.equals(dto.getNotifyType())){
                // 运营研判消息推送

                log.info(Constants.LOG_MESSAGEPUSH_TOWER_PARAM + JSON.toJSONString(dto));

                dto.setUserType(Constants.USER_TYPE_1);
                dto.setNotifyAdminFlag(Constants.ZERO);
                // 根据事件状态以及事件编码查询事发地点
                String orderId = dto.getOrderId();
                String address = dto.getAddress() == null?"":dto.getAddress();
                String alarmTime = dto.getAlarmTime() == null?"":dto.getAlarmTime();
                String warningTypeId = dto.getTypeValue() == null?"":dto.getTypeValue();
                String warningTypeName = "";
                try{
                    //AlarmOperateDTO alarmOperateDTO = alarmOperateMapper.getAlarmOperate(orderId);
                    AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(orderId,null);
                    if(alarmOperateDTO != null){
                        address = alarmOperateDTO.getAddress();
                        alarmTime = alarmOperateDTO.getAlarmTime();
                        warningTypeId = alarmOperateDTO.getWarningTypeId();
                        warningTypeName = alarmOperateDTO.getWarningTypeName();
                        dto.setDeviceCode(alarmOperateDTO.getDeviceCode());
                        dto.setCityId(alarmOperateDTO.getCityId());
                        dto.setProvinceId(alarmOperateDTO.getProvinceId());
                        dto.setCountyId(alarmOperateDTO.getCountyId());
                        dto.setWarningSource(alarmOperateDTO.getWarningSource());
                    }
                }catch(Exception e){
                    log.error("根据事件状态以及事件编码查询事发地点，查询告警信息异常",e);
                }
                //地址为空时，赋值地址
                if (StringUtils.isEmpty(dto.getAddress()) || !StringUtils.isEmpty(address)) {
                    dto.setAddress(address);
                }
                AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
                if(alarmTypeInfo!=null){
                    warningTypeName= alarmTypeInfo.getTypeName();
                }
                if (StringUtils.isEmpty(dto.getNotifyTitle())) {
                    dto.setNotifyTitle(warningTypeName);
                }
                // 运营研判数据给铁塔角色运营研判下的所有用户进行推送
                List<String> userIdList = deviceRepository.getAlarmOperateByRole(ObjectUtil.isNull(dto.getProvinceId())?"":dto.getProvinceId().substring(0,4),ObjectUtil.isNull(dto.getCityId())?"":dto.getCityId());
                dto.setUserId(userIdList.stream().collect(Collectors.joining(Constants.COMMA)));
                dto.setMsgNotifyType("1,3,4");
                dto.setOrderStatus(Constants.ONE);
                //行业版本编码
                dto.setIndustryCode(Constants.PARAM_INDUSTRY_CODE_999999);
                dto.setAppVerCode(Constants.PARAM_APP_VER_CODE_1);
                iTradeWarningOrderService.messagePush(dto);
            }
        }
        catch (Exception e) {
            log.error(MessageConstants.MESSAGE_PUSH_ERROR_MESSAGE, e);
        }
    }

    //雷达查询二次识别配置 判断是否 二次识别任务下发操作
    @Async("taskExecutor")
    public void secondRecogIfExecuteByRadar(List<ReceiveAlarmVO> orderInfoVOs,String tableType){
        try {
            //查询告警来源-告警类型下的二次识别配置
            List<String> list = orderInfoVOs.stream().map(v -> v.getWarningSource() + "-" + v.getAlarmType()).collect(Collectors.toList());
            List<SecondRecogReceiveVO> configList = secondRecogConfigMapper.selectSecondRecogByOnes(list);
            //二次识别下发集合
            List<SecondRecogHandleVO> handleList = new ArrayList<>();
            for (ReceiveAlarmVO orderInfoVO : orderInfoVOs) {
                //图片为空 不需要二次识别
                if (!StringUtils.isEmpty(orderInfoVO.getOriginalPictureUrl())) {


                    // 过滤configList中与orderInfoVO的属性相等的数据
                    List<SecondRecogReceiveVO> filteredList = configList.stream()
                            .filter(config ->
                                    config.getTenantId().equals(orderInfoVO.getTenantId()) &&
                                            config.getIndustryCode().equals(orderInfoVO.getIndustryCode()) &&
                                            config.getAlarmSourceValue().equals(orderInfoVO.getWarningSource()) &&
                                            config.getAlarmTypeValue().equals(orderInfoVO.getAlarmType())
                            )
                            .collect(Collectors.toList());
                    //不为空则需要二次识别
                    log.info(Constants.SECOND_SELECT_CONFIG_LOG + JSONObject.toJSONString(filteredList)+Constants.ALARM_INFORMATION_LOG+JSONObject.toJSONString(orderInfoVO));
                    if (!CollectionUtils.isEmpty(filteredList) && filteredList.size() > 0) {
                        //图片可能为多选 逗号分隔下发
                        String[] picSplit = orderInfoVO.getOriginalPictureUrl().split(",");
                        for (String picUrl : picSplit) {
                            SecondRecogHandleVO handleVO = new SecondRecogHandleVO();
                            handleVO.setTaskId(orderInfoVO.getWarningOrderId());
                            handleVO.setPicUrl(picUrl);
                            handleVO.setConfigList(filteredList);
                            handleVO.setSecondRecogTableType(tableType);
                            handleList.add(handleVO);
                        }
                    }
                }
            }
            //二次识别下发
            this.recogSend(handleList);
        }catch (Exception e){
            log.error(Constants.SECOND_TASK_FALSE_LOG, e);
        }
    }

    //二次识别下发
    private void recogSend(List<SecondRecogHandleVO> handleList) throws Exception {
        if (!CollectionUtils.isEmpty(handleList)) {
            //查询节点编码
            String platformNode = this.getPlatformNode();
            //加密后的图片
            String picUrl = "";
            //二次识别下发入参集合
            List<SecondRecogSendVO> list = new ArrayList<>();
            //加密图片集合
            List<String> imageUrls = handleList.stream().map(v -> v.getPicUrl()).collect(Collectors.toList());
            //调用obs加密方法获取加密后的路径
            log.info("obs开始获取原图/框图加密路径");
            Map<String, String> map = fileUrlEncryptUtil.dealObsFileUrlBatch(imageUrls);
            log.info("obs获取原图/框图加密路径：{}", JSON.toJSONString(map));
            for (SecondRecogHandleVO handleVO : handleList) {
                SecondRecogSendVO recogSendVO = new SecondRecogSendVO();
                SecondRecogFileVO fileVO = new SecondRecogFileVO();
                if (!CollectionUtils.isEmpty(map)) {
                    for (String key : map.keySet()) {
                        //相同则赋值加密后图片 否则用加密前图片
                        if (key.equals(handleVO.getPicUrl())) {
                            if (!StringUtils.isEmpty(map.get(key))) {
                                // 仅加密成功  使用原地址进行处理
                                picUrl = map.get(key);
                            } else {
                                picUrl = key;
                            }
                            break;
                        }
                    }
                }
                fileVO.setType("image");
                fileVO.setImgUrls(picUrl);
                //二次识别配置
                List<SecondRecogReceiveVO> configList = handleVO.getConfigList();
                //生成随机6位
                String uuId = UUID.randomUUID().toString().replace("-", "").substring(0,6);
                recogSendVO.setTaskId(handleVO.getTaskId() + "/" + handleVO.getSecondRecogTableType() + "/" + configList.get(0).getSelectAlgorithm()+"/"+platformNode+"/"+uuId);
                recogSendVO.setAlgorithmCode(configList.get(0).getSelectAlgorithm());
                recogSendVO.setAcceptNode(platformNode);
                recogSendVO.setFileInfo(fileVO);
                recogSendVO.setLinkageCamera(configList.get(0).getLinkageCamera());
                recogSendVO.setLinkageAction(configList.get(0).getLinkageAction());
                recogSendVO.setSelectAlgorithm(configList.get(0).getSelectAlgorithm());
                list.add(recogSendVO);
            }
            //二次下发 返回taskBatchId
            Result<String> stringResult = alarmFlowDeployFeign.synAnalysisTask(list);
            String taskBatchId = stringResult.getData();
            if (StringUtil.isNotEmpty(taskBatchId)){
                List<SecondRecogBatchVO> batchList = new ArrayList<>();
                for (SecondRecogSendVO vo : list) {
                    SecondRecogBatchVO batchVO = new SecondRecogBatchVO();
                    batchVO.setTaskId(vo.getTaskId());
                    batchVO.setTaskBatchId(taskBatchId);
                    batchVO.setTaskType(Constants.ONE);
                    batchVO.setAnalyType(Constants.ZERO);
                    batchVO.setDeleteFlag(Constants.ZERO);
                    batchVO.setAcceptNode(platformNode);
                    batchVO.setAccessNode(platformNode);
                    batchVO.setAlgorithmCode(vo.getAlgorithmCode());
                    batchList.add(batchVO);
                }
                //更新新增的二次识别批次信息
                this.updateSecondRecogInFo(batchList);
            }


        }
    }
    public void updateSecondRecogInFo(List<SecondRecogBatchVO> list) {
        log.info("二次识别Mongo更新入参：" + JSONObject.toJSONString(list));
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,Constants.SECOND_RECOG_TASK_INFO);
        //接收需要更新新增的二次识别批次信息
        for (SecondRecogBatchVO listVO : list) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.DESC_TASK_ID_PARAM).is(listVO.getTaskId()));
            Update update = new Update();
            update.set(Constants.DESC_TASK_ID_PARAM,listVO.getTaskId());
            update.set("taskBatchId",listVO.getTaskBatchId());
            update.set("taskType",listVO.getTaskType());
            update.set("createTime", com.chinatower.common.core.utils.DateUtils.getTime());
            update.set("analyType",listVO.getAnalyType());
            update.set("deleteFlag",listVO.getDeleteFlag());
            update.set("acceptNode",listVO.getAcceptNode());
            update.set("accessNode",listVO.getAccessNode());
            update.set("algorithmCode",listVO.getAlgorithmCode());
            update.set("orderType",Constants.ZERO);
            bulkOperations.upsert(query, update);
        }
        bulkOperations.execute();
    }

    public String getPlatformNode() {
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
     * 获取流程匹配得所以用户及角色
     */
    public void getUserOrRoleList(VmFlowLinkAttrDto vmFlowLinkAttrDto,String gridId,Long alarmFlowId,String linkId,Long flowTemplate,MessagePushVo dto){
        //有勾选网格员处理模式
        if(vmFlowLinkAttrDto.getUserType() != null) {
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
            if ( org.apache.commons.lang3.StringUtils.isNotEmpty(flowLinkAttrModel.getRoleId())) {
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
}
