package com.chinatower.event.asyncTask;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.entity.dto.AirAlTaskLogReq;
import com.chinatower.device.entity.dto.AirHistoryReq;
import com.chinatower.event.api.IReceiveApiService;
import com.chinatower.event.api.model.AlarmByTaskReq;
import com.chinatower.event.api.model.IntelligentJudgeTaskRequest;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constant.AlarmConstants;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.mapper.*;
import com.chinatower.event.mongodb.*;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.IMessageRepository;
import com.chinatower.event.repository.MessagePushService;
import com.chinatower.event.service.OperateService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.OkhttpUtil;
import com.chinatower.event.util.SignatureUtil;
import com.chinatower.event.vo.*;
import com.chinatower.event.vo.entity.ReceiveSatelliteAlarmDto;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.api.IIndustryDeviceService;
import com.chinatower.industry.dto.DeviceLayerReq;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.entity.IVmMessagePushDTO;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AsyncCommon {

    @Autowired
    private ReceiveSatelliteMapper receiveSatelliteMapper;

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private IMessageRepository messageRepository;

    @Autowired
    private VmFlowLinkAttrMapper vmFlowLinkAttrMapper;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private VideoQueryAPI videoQueryAPI;

    @Autowired
    private VideoPlayerAPI videoPlayerAPI;

    @Autowired
    private IIndustryDeviceService industryDeviceService;

    @Autowired
    private SystemFeignClient systemFeignClient;


    @Autowired
    private ReceiveAlarmPointMapper receiveAlarmPointMapper;

    @Autowired
    private ReceiveAlarmOriginalAreaMapper receiveAlarmOriginalAreaMapper;

    @Autowired
    private WarningInterceptMapper warningInterceptMapper;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private InterceptMongoMapper interceptMongoMapper;

    @Autowired
    private OperateService operateService;

    @Autowired
    private IotConfigMongoMapper iotConfigMongoMapper;

    @Autowired
    private SecondRecogConfigMapper secondRecogConfigMapper;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private AsyncVideoCommon asyncVideoCommon;

    @Autowired
    private MessagePushService messagePushService;

    @Autowired
    private OverdueRemindMapper overdueRemindMapper;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${algorithm.ak}")
    private String APP_KEY;

    @Value("${algorithm.appCode}")
    private String APP_CODE;
    @Value("${algorithm.sk}")
    private String SECRET_KEY;
    @Resource
    private NetConnectUtils connectUtils;

    @Autowired
    private IAirLineService iAirLineService;
    @Autowired
    private SysMapper sysMapper;

    @Value("${algorithm.mcp.ak}")
    private String MCP_APP_KEY;

    @Value("${algorithm.mcp.appCode}")
    private String MCP_APP_CODE;
    @Value("${algorithm.mcp.sk}")
    private String MCP_SECRET_KEY;

    /**
     * 保存告警数据入mongo
     *
     * @param receiveSatelliteAlarmVos
     */
    @Async("taskExecutor")
    public void saveMongoDb(List<ReceiveSatelliteAlarmVo> receiveSatelliteAlarmVos) {
        try {
            //空白处遗留建表语句
            Date date = DateUtils.string2Date(receiveSatelliteAlarmVos.get(0).getAlarmTime());
            String yearMonth = DateUtils.dateToStringYM(date);
            String collectionName = TableConstant.WARN_ORDER_SATELLITE_TEBLE + yearMonth;
            Boolean exists = receiveSatelliteMapper.collectionExists(collectionName, MongoDBType.PRIMARY);
            if (!exists) {
                receiveSatelliteMapper.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
            }
            //卫星告警数据入mongo
            String dateStr = DateUtils.date2Str(new Date(), Constants.DATE_YMDHMS);
            List<ReceiveSatelliteAlarmDto> receiveDtoList = new ArrayList<>();
            for (ReceiveSatelliteAlarmVo vo : receiveSatelliteAlarmVos) {
                ReceiveSatelliteAlarmDto dto = new ReceiveSatelliteAlarmDto();
                BeanUtil.copyProperties(vo, dto);
                if (vo.getExtendField() != null) {
                    dto.setExtendField(JSON.toJSONString(vo.getExtendField()));
                }
                dto.setPushTime(dateStr);
                receiveDtoList.add(dto);
            }
            receiveSatelliteMapper.insertAll(receiveDtoList, collectionName, MongoDBType.PRIMARY);

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
            log.error("异步存储卫星告警信息到mongo异常", e);
        }
    }

    /**
     * 获取索引列
     *
     * @return
     */
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

    /**
     * 检查索引是否已存在
     *
     * @param tableName
     * @param primary
     * @return
     */
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

    /**
     * 消息推送（新）
     *
     * @param messagePushDTO
     */
    @Async("taskExecutor")
    public void messagePushNew(IVmMessagePushDTO messagePushDTO) {
        messagePushService.messagePush(messagePushDTO);
    }

    /**
     * 删除工单受理提醒数据
     *
     * @param alarmOrderVO
     * @param overdueRemindSign
     */
    @Async("taskExecutor")
    public void deleteOverdueRemind(SendAlarmOrderDTO alarmOrderVO, String overdueRemindSign) {
        overdueRemindMapper.deleteOverdueRemind(alarmOrderVO.getOrderId(), overdueRemindSign);
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

    /**
     * 消息推送
     *
     * @param dto
     */
    @Async("taskExecutor")
    public void messagePush(MessagePushDTO dto) {
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
                } else if (!StringUtils.isEmpty(dto.getRoleId())) {
                    dto.setUserType(Constants.USER_TYPE_2);
                }
                //flowId等于模板id 则flowId置空
                Long alarmFlowId = org.apache.commons.lang3.StringUtils.isNotBlank(dto.getFlowTemplate()) && dto.getFlowTemplate().equals(dto.getAlarmFlowId()) ? null : Long.valueOf(dto.getAlarmFlowId());
                Long flowTemplate = org.apache.commons.lang3.StringUtils.isNotBlank(dto.getFlowTemplate()) ? Long.valueOf(dto.getFlowTemplate()) : null;
                VmFlowLinkDto vmFlowLinkDto = flowLinkService.getFlowLinkAndConfig(dto.getAppVerCode(), dto.getIndustryCode(), tenantId, flowTemplate, alarmFlowId, Integer.parseInt(linkId));
                VmFlowLinkAttrDto vmFlowLinkAttrDto = null;
                if (vmFlowLinkDto != null) {
                    vmFlowLinkAttrDto = vmFlowLinkDto.getFlowLinkAttrDto();
                    dto.setVmFlowLinkAttrDto(vmFlowLinkAttrDto);
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
                                    dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
                                }
                            }
                        } catch (Exception e) {
                            log.error("ReceiveRepositoryImpl-messagePush-短信模板配置时间比较报错-默认发送:", e);
                        }
                    }
                    //定时推送不发送短信消息
                    if (Constants.ONE.equals(vmFlowLinkAttrDto.getTimePushType())) {
                        dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
                    }
                }
                if (!StringUtils.isEmpty(msgNotifyType)) {
                    dto.setMsgNotifyType(msgNotifyType);
                    dto.setNotifyAdminFlag(notifyAdminFlag);
                    // 根据事件状态以及事件编码查询事发地点
                    String orderId = dto.getOrderId();
                    String address = dto.getAddress() == null ? "" : dto.getAddress();
                    String alarmTime = dto.getAlarmTime() == null ? "" : dto.getAlarmTime();
                    String warningTypeId = dto.getTypeValue() == null ? "" : dto.getTypeValue();
                    String warningTypeName = "";
                    String gridId = dto.getGridId();
                    try {
                        if (Constants.SYS_MSG_TYPE_2.equals(dto.getNotifyType())) {
                            //AlarmOperateDTO alarmOperateDTO = alarmOperateMapper.getAlarmOperate(orderId);
                            AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(orderId, null);
                            if (alarmOperateDTO != null) {
                                if (StringUtils.isEmpty(gridId)) {
                                    gridId = alarmOperateDTO.getGridId();
                                }
                                if (StringUtils.isEmpty(dto.getAddress())) {
                                    address = alarmOperateDTO.getAddress();
                                } else {
                                    address = dto.getAddress();
                                }
                                alarmTime = alarmOperateDTO.getAlarmTime();
                                if (StringUtils.isEmpty(dto.getWarningTypeId())) {
                                    warningTypeId = alarmOperateDTO.getWarningTypeId();
                                } else {
                                    warningTypeId = dto.getWarningTypeId();
                                }
                                //行业版本编码
                                if (StringUtils.isEmpty(dto.getIndustryCode())) {
                                    dto.setIndustryCode(alarmOperateDTO.getTradeId());
                                }
                                if (StringUtils.isEmpty(dto.getAppVerCode())) {
                                    dto.setAppVerCode(alarmOperateDTO.getTradeVerCode());
                                }
                                if (StringUtils.isEmpty(dto.getDeviceCode())) {
                                    dto.setDeviceCode(alarmOperateDTO.getDeviceCode());
                                }
                                if (StringUtils.isEmpty(dto.getCityId())) {
                                    dto.setCityId(alarmOperateDTO.getCityId());
                                }
                                if (StringUtils.isEmpty(dto.getProvinceId())) {
                                    dto.setProvinceId(alarmOperateDTO.getProvinceId());
                                }
                                if (StringUtils.isEmpty(dto.getCountyId())) {
                                    dto.setCountyId(alarmOperateDTO.getCountyId());
                                }
                                if (StringUtils.isEmpty(dto.getWarningSource())) {
                                    dto.setWarningSource(alarmOperateDTO.getWarningSource());
                                }
                                if (StringUtils.isEmpty(dto.getNotifyTitle())) {
                                    dto.setNotifyTitle(alarmOperateDTO.getAlarmBody());
                                }
                                if (StringUtils.isEmpty(dto.getAlarmTime())) {
                                    dto.setAlarmTime(alarmOperateDTO.getAlarmTime());
                                }
                            }
                        } else {
                            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);
                            AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
                            alarmOrderDTO.setOrderId(orderId);
                            alarmOrderDTO.setTableName(alarmTableName);
                            WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderDTO);
                            if (warnOrderStatusDTO != null) {
                                if (StringUtils.isEmpty(gridId)) {
                                    gridId = warnOrderStatusDTO.getGridId();
                                }
                                if (StringUtils.isEmpty(dto.getAddress())) {
                                    address = warnOrderStatusDTO.getAddress();
                                } else {
                                    address = dto.getAddress();
                                }
                                alarmTime = warnOrderStatusDTO.getAlarmTime();
                                if (StringUtils.isEmpty(dto.getWarningTypeId())) {
                                    warningTypeId = warnOrderStatusDTO.getWarningTypeId();
                                } else {
                                    warningTypeId = dto.getWarningTypeId();
                                }
                                //行业版本编码
                                if (StringUtils.isEmpty(dto.getIndustryCode())) {
                                    dto.setIndustryCode(warnOrderStatusDTO.getIndustryCode());
                                }
                                if (StringUtils.isEmpty(dto.getAppVerCode())) {
                                    dto.setAppVerCode(warnOrderStatusDTO.getTradeVerCode());
                                }
                                if (StringUtils.isEmpty(dto.getDeviceCode())) {
                                    dto.setDeviceCode(warnOrderStatusDTO.getDeviceCode());
                                }
                                if (StringUtils.isEmpty(dto.getCityId())) {
                                    dto.setCityId(warnOrderStatusDTO.getCityId());
                                }
                                if (StringUtils.isEmpty(dto.getProvinceId())) {
                                    dto.setProvinceId(warnOrderStatusDTO.getProvinceId());
                                }
                                if (StringUtils.isEmpty(dto.getCountyId())) {
                                    dto.setCountyId(warnOrderStatusDTO.getCountyId());
                                }
                                if (StringUtils.isEmpty(dto.getWarningSource())) {
                                    dto.setWarningSource(warnOrderStatusDTO.getWarningSource());
                                }
                                if (StringUtils.isEmpty(dto.getNotifyTitle())) {
                                    dto.setNotifyTitle(warnOrderStatusDTO.getAlarmBody());
                                }
                                if (StringUtils.isEmpty(dto.getAlarmTime())) {
                                    dto.setAlarmTime(warnOrderStatusDTO.getAlarmTime());
                                }
                                if (StringUtils.isEmpty(dto.getExtraField())) {
                                    dto.setExtraField(warnOrderStatusDTO.getExtraField());
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("根据事件状态以及事件编码查询事发地点，查询告警信息异常", e);
                    }
                    //如果不是从事件流转过来有指定用户得，查询该工单匹配流程得角色和用户
                    if (!Constants.ONE.equals(dto.getFlowFlag())) {
                        if (vmFlowLinkDto != null) {
                            if (vmFlowLinkAttrDto != null) {
                                /*msgNotifyType = vmFlowLinkAttrDto.getMsgNotifyType();
                                notifyAdminFlag = vmFlowLinkAttrDto.getNotifyAdminFlag();
                                leaderFlag = vmFlowLinkAttrDto.getLeaderFlag();
                                userType = vmFlowLinkAttrDto.getUserType()==null?dto.getUserType():vmFlowLinkAttrDto.getUserType();
                                vmFlowLinkAttrDto.setUserType(userType);
                                // 查询用户和角色信息
                                List<Map> resultAttrSub = vmFlowLinkAttrMapper.getAttrUserOrRoleInfo(vmFlowLinkAttrDto);
                                if(resultAttrSub!=null && !resultAttrSub.isEmpty()){
                                    Map attrSub = resultAttrSub.get(0);
                                    if(Constants.USER_TYPE1.equals(userType)){
                                        dto.setUserId(MapUtils.getString(attrSub,"userOrRoles"));
                                    }else{
                                        dto.setRoleId(MapUtils.getString(attrSub,"userOrRoles"));
                                    }
                                }*/
                                //有勾选网格员处理模式
                                this.getUserOrRoleList(vmFlowLinkAttrDto, gridId, alarmFlowId, linkId, flowTemplate, dto);

                            }
                        }
                    }

                    AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
                    if (alarmTypeInfo != null) {
                        warningTypeName = alarmTypeInfo.getTypeName();
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
                    if (Constants.SYS_MSG_TYPE_3.equals(dto.getNotifyType())) {
                        if (Constants.YES_FLAG.equals(leaderFlag)) {
                            messageRepository.messagePush(dto);
                        }
                    }
                    //其他通知
                    else {
                        dto.setWarningTypeName(warningTypeName);
                        dto.setWxSendFlag(Constants.ONE);
                        messageRepository.messagePush(dto);
                    }
                }
            } else if (Constants.TWO.equals(dto.getNotifyType())) {
                // 运营研判消息推送

                log.info(Constants.LOG_MESSAGEPUSH_TOWER_PARAM + JSON.toJSONString(dto));

                dto.setUserType(Constants.USER_TYPE_1);
                dto.setNotifyAdminFlag(Constants.ZERO);
                // 根据事件状态以及事件编码查询事发地点
                String orderId = dto.getOrderId();
                String address = dto.getAddress() == null ? "" : dto.getAddress();
                String alarmTime = dto.getAlarmTime() == null ? "" : dto.getAlarmTime();
                String warningTypeId = dto.getTypeValue() == null ? "" : dto.getTypeValue();
                String warningTypeName = "";
                try {
                    //AlarmOperateDTO alarmOperateDTO = alarmOperateMapper.getAlarmOperate(orderId);
                    AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(orderId, null);
                    if (alarmOperateDTO != null) {
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
                } catch (Exception e) {
                    log.error("根据事件状态以及事件编码查询事发地点，查询告警信息异常", e);
                }
                //地址为空时，赋值地址
                if (StringUtils.isEmpty(dto.getAddress()) || !StringUtils.isEmpty(address)) {
                    dto.setAddress(address);
                }
                AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
                if (alarmTypeInfo != null) {
                    warningTypeName = alarmTypeInfo.getTypeName();
                }
                if (StringUtils.isEmpty(dto.getNotifyTitle())) {
                    dto.setNotifyTitle(warningTypeName);
                }
                // 运营研判数据给铁塔角色运营研判下的所有用户进行推送
                List<String> userIdList = deviceRepository.getAlarmOperateByRole(ObjectUtil.isNull(dto.getProvinceId()) ? "" : dto.getProvinceId().substring(0, 4), ObjectUtil.isNull(dto.getCityId()) ? "" : dto.getCityId());
                dto.setUserId(userIdList.stream().collect(Collectors.joining(Constants.COMMA)));
                dto.setMsgNotifyType("1,3,4");
                dto.setOrderStatus(Constants.ONE);
                //行业版本编码
                dto.setIndustryCode(Constants.PARAM_INDUSTRY_CODE_999999);
                dto.setAppVerCode(Constants.PARAM_APP_VER_CODE_1);
                messageRepository.messagePush(dto);
            }
        } catch (Exception e) {
            log.error(MessageConstants.MESSAGE_PUSH_ERROR_MESSAGE, e);
        }
    }

    /**
     * 获取流程匹配得所以用户及角色
     */
    public void getUserOrRoleList(VmFlowLinkAttrDto vmFlowLinkAttrDto, String gridId, Long alarmFlowId, String linkId, Long flowTemplate, MessagePushDTO dto) {
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
            List<FlowModel> flowModelList = flowLinkService.getFlowLink(configModel);
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

    public String getSignature(String SecretKey, String misId, String requestTime) throws Exception {
        String singStr = APP_KEY + misId + requestTime;
        String generateSign = SignatureUtil.signByHmacSHA1(SecretKey, singStr);
        System.out.println(generateSign);
        return generateSign;
    }

    @Async("taskExecutor")
    public void satelliteSnapShot(List<ReceiveAlarmVO> list, String tableType) {
        try {
            log.info("卫星告警联动抓图入参:{}", JSON.toJSONString(list));
            List<ReceiveAlarmVO> alarms = JSON.parseArray(JSON.toJSONString(list), ReceiveAlarmVO.class);
            //从字典配置中获取物联设备发生告警抓图回调地址
            String imageUploadUrl = "";
            Result<List<SysDictDataResponse>> dictDataresult = systemFeignClient.dictDataType(AlarmConstants.SATELLITE_SNAP_SHOT_BACK_URL);
            if (dictDataresult.getCode() == ResultCode.SUCCESS.getCode()) {
                List<SysDictDataResponse> sysDictDataList = dictDataresult.getData();
                if (!CollectionUtils.isEmpty(sysDictDataList)) {
                    imageUploadUrl = sysDictDataList.get(0).getDictValue();
                }
            }
            log.info("从字典配置中获取抓图回调地址:{}", imageUploadUrl);
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
            if (!StringUtils.isEmpty(imageUploadUrl)) {

                String finalImageUploadUrl = imageUploadUrl;
                alarms.forEach(alarm -> {
                    // 没有透传字段则视为脏数据，不进行联动抓图
                    if (StringUtils.isEmpty(alarm.getExtraField())) {
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
                    log.info("请求联动摄像机报文：{}", JSON.toJSONString(deviceLayerReq));
                    Result<List<IndustryDeviceTree>> deviceResult = industryDeviceService.queryDevice3km(deviceLayerReq);
                    log.info("获取到的联动摄像机信息：{}", JSON.toJSONString(deviceResult));
                    if (ObjectUtil.isNotNull(deviceResult) && Constants.CODE_200 == deviceResult.getCode() && !CollectionUtils.isEmpty(deviceResult.getData())) {
                        List<IndustryDeviceTree> industryDeviceTrees = deviceResult.getData();

                        // 批量获取所有设备的有效参考点
                        List<String> deviceCodeList = industryDeviceTrees.stream().map(IndustryDeviceTree::getDeviceCode).collect(Collectors.toList());
                        List<DeviceRefPointVO> deviceRefPointList = deviceRepository.getDevicePointsValidBatch(deviceCodeList);

                        if (!CollectionUtils.isEmpty(deviceRefPointList)) {
                            log.info("获取到的有效参考点信息：{}，对应租户：{}", JSON.toJSONString(deviceRefPointList), alarm.getTenantId());

                            // 从远到近循环所有设备，取存在有效参考点的设备进行联动抓图，都没有则不抓图
                            for (IndustryDeviceTree industryDeviceTree : industryDeviceTrees) {
                                List<DeviceRefPointVO> deviceRefPointVOS = deviceRefPointList.stream().filter(temp -> industryDeviceTree.getDeviceCode().equals(temp.getDeviceCode())).collect(Collectors.toList());
                                // 获取当前设备的有效参考点
                                if (ObjectUtil.isNotNull(industryDeviceTree) && ObjectUtil.isNotNull(industryDeviceTree.getChannelInfoVOList()) &&
                                        !CollectionUtils.isEmpty(deviceRefPointVOS)) {
                                    // 根据设备+经纬度做聚合处理，同一设备同一经纬度只做一次联动抓图操作，同步更新所有匹配告警
                                    List<IndustryDeviceTree> treeList = shotDeviceList.stream().filter(temp -> temp.getDeviceCode().equals(industryDeviceTree.getDeviceCode())
                                            && temp.getAlarmLatitude().equals(alarm.getLatitude()) && temp.getAlarmLongitude().equals(alarm.getLongitude())).collect(Collectors.toList());
                                    if (!CollectionUtils.isEmpty(treeList)) {
                                        treeList.forEach(temp -> temp.setOrderIds(temp.getOrderIds() + Constants.COMMA + alarm.getWarningOrderId()));
                                    } else {
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


                    } else {
                        log.info("3公里范围内未查询到订购的摄像机信息：{},对应租户加设备:{}", alarm.getAlarmCode(), alarm.getTenantId() + Constants.COMMA + alarm.getDeviceCode());
                    }
                });
                log.info("卫星告警聚合后需要抓图的设备列表:{}", JSON.toJSONString(shotDeviceList));
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
                                log.info("转动到指定经纬度请求报文：{}", JSON.toJSONString(videoPtzQueryReq));
                                Result<PresetData> turnResult = videoQueryAPI.turnToPtzByPointAngle2(videoPtzQueryReq);
                                log.info("对摄像机：{}，通道：{}，转动到指定经纬度返回信息：{}", industryDeviceTree.getDeviceCode(), indChannelInfoVO.getChannelCode(), JSON.toJSONString(turnResult));
                                if (ObjectUtil.isNotNull(turnResult) && ObjectUtil.isNotNull(turnResult.getCode()) && Constants.CODE_200 == turnResult.getCode()) {

                                    //调用转动摄像头停止五秒后开始联动抓图
                                    Thread.sleep(5000);

                                    //组装抓图参数
                                    PlayerSnapShotReq playerSnapShotReq = new PlayerSnapShotReq();
                                    playerSnapShotReq.setStreamType(1);
                                    playerSnapShotReq.setTenantId(industryDeviceTree.getTenantId());
                                    playerSnapShotReq.setIsNeedOfs("1");
                                    String relDeviceCode = industryDeviceTree.getDeviceCode();
                                    String relChannelCode = indChannelInfoVO.getChannelCode();

                                    String imgName = relDeviceCode + relChannelCode;
                                    playerSnapShotReq.setDeviceCode(relDeviceCode);
                                    playerSnapShotReq.setChannelCode(relChannelCode);
                                    playerSnapShotReq.setImageName(imgName);
                                    //抓图告警ids
                                    String orderIds = "";
                                    //二次配置抓图告警ids
                                    String secondOrderIds = "";
                                    String[] split = industryDeviceTree.getOrderIds().split(",");
                                    for (String orderId : split) {
                                        //是否需要二次抓图
                                        boolean ifSecond = false;
                                        List<IndustryDeviceTree> treeList = secondIfList.stream().filter(v -> v.getOrderIds().equals(orderId)).collect(Collectors.toList());
                                        if (!CollectionUtils.isEmpty(treeList) && treeList.size() > 0) {
                                            IndustryDeviceTree deviceTree = treeList.get(0);
                                            if (!StringUtils.isEmpty(deviceTree.getLinkageAction())) {
                                                if (deviceTree.getLinkageAction().contains("0")) {
                                                    ifSecond = true;
                                                    if (!StringUtils.isEmpty(secondOrderIds)) {
                                                        secondOrderIds = secondOrderIds + "," + orderId;
                                                    } else {
                                                        secondOrderIds = orderId;
                                                    }
                                                }
                                                if (deviceTree.getLinkageAction().contains("1")) {
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
                                                    String uuId = System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                                                    videoDownloadVO.setBatchId(uuId);
                                                    downList.add(videoDownloadVO);
                                                }
                                            }
                                        }
                                        if (!ifSecond) {
                                            if (!StringUtils.isEmpty(orderIds)) {
                                                orderIds = orderIds + "," + orderId;
                                            } else {
                                                orderIds = orderId;
                                            }
                                        }
                                    }
                                    if (!StringUtils.isEmpty(orderIds)) {
                                        playerSnapShotReq.setImageUpUrl(finalImageUploadUrl + tableType + Constants.SPLIT_SIGN + orderIds);
                                        // 联动抓图
                                        log.info("snapShot request:{}", JSON.toJSONString(playerSnapShotReq));
                                        Result videoResult = videoPlayerAPI.snapShot(playerSnapShotReq);
                                        log.info("snapShot response:{}", JSON.toJSONString(videoResult));
                                    }
                                    if (!StringUtils.isEmpty(secondOrderIds)) {
                                        playerSnapShotReq.setImageUpUrl(finalImageUploadUrl + tableType + Constants.SPLIT_SIGN + secondOrderIds);
                                        // 联动抓图
                                        log.info("snapShot request:{}", JSON.toJSONString(playerSnapShotReq));
                                        Result videoResult = videoPlayerAPI.snapShot(playerSnapShotReq);
                                        log.info("snapShot response:{}", JSON.toJSONString(videoResult));
                                    }


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.info("通过关联摄像机调用看这里并抓图异常：{}", JSON.toJSONString(industryDeviceTree));
                                Thread.currentThread().interrupt();
                            }
                        })
                );
                //异步调用抓视频
                asyncVideoCommon.captureVideo(downList);
            }

        } catch (Exception e) {
            log.error("卫星告警获取联动摄像机抓图异常", e);
        }
    }

    /**
     * 异步批量保存告警原始图片区域/区域点,更新图片
     */
    @Async("taskExecutor")
    public void saveAlarmOriginalPicMongoDB(List<AlarmOriginalAreaVO> alarmAreas, List<AlarmOriginalPointVO> alarmPointLists, boolean flag, Map mapIn) {
        try {
            if (!CollectionUtils.isEmpty(alarmAreas)) {
                String collectionName = TableConstant.VM_WARNING_ORIGINAL_AREA_T;
                Boolean exists = receiveAlarmOriginalAreaMapper.collectionExists(collectionName, MongoDBType.PRIMARY);
                if (!exists) {
                    receiveAlarmOriginalAreaMapper.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
                }
                receiveAlarmOriginalAreaMapper.insertAll(alarmAreas, collectionName, MongoDBType.PRIMARY);
                // warningOriginalAreaMapper.insertOriginalAreas(alarmAreas);
            }
            if (!CollectionUtils.isEmpty(alarmPointLists)) {
                String collectionName = TableConstant.VM_WARNING_ORIGINAL_POINT_T;
                Boolean exists = receiveAlarmPointMapper.collectionExists(collectionName, MongoDBType.PRIMARY);
                if (!exists) {
                    receiveAlarmPointMapper.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
                }
                receiveAlarmPointMapper.insertAll(alarmPointLists, collectionName, MongoDBType.PRIMARY);

                //warningOriginalPointMapper.insertOriginalPoints(alarmPointLists);
            }
            if (flag) {
                alarmOperateMapper.updateAlarmOperateImg(mapIn);
                // 20220104 zzh 更新压制表图片
                warningInterceptMapper.updateInterceptImg(mapIn);
                warningOrderMapper.updateAlarmImg(mapIn);
                try {
                    OperateMongo mongo = JSON.parseObject(JSON.toJSONString(mapIn), OperateMongo.class);
                    List<OperateMongo> operateMongoList = new ArrayList<>();
                    operateMongoList.add(mongo);
                    operateMongoMapper.updateOperate(operateMongoList);
                } catch (Exception e) {
                    log.error("checkAlarmOrder-修改运营研判mongo异常:", e);
                }
                try {
                    InterceptDTO mongo = JSON.parseObject(JSON.toJSONString(mapIn), InterceptDTO.class);
                    mongo.setOrderId(String.valueOf(mapIn.get(Constants.PARAM_WARNING_ORDER_ID)));
                    interceptMongoMapper.updateIntercept(mongo);
                } catch (Exception e) {
                    log.error("checkAlarmOrder-修改回收站mongo异常:", e);
                }
            }
        } catch (Exception e) {
            log.error("异步批量保存告警原始图片区域/区域点异常", e);
        }
    }

    //物联配置信息累计值入MongoDB
    @Async("taskExecutor")
    public void factoryValueMongoHandle(List<Map<String, String>> mongoFactoryList, String mergeTime) {
        if (!CollectionUtils.isEmpty(mongoFactoryList)) {
            String collectionName = Constants.IOT_CONFIG_FACTORY_SUM_VALUE;
            //更新Mongo对应已有设备因子数据
            List<String> mongoDeviceCodeList = mongoFactoryList.stream().map(v -> v.get("deviceCode")).distinct().collect(Collectors.toList());
            List<IotFactoryMapDTO> iotFactoryMapDTOS = iotConfigMongoMapper.groupMongoFactoryValue(mongoDeviceCodeList, mergeTime);
            iotConfigMongoMapper.updateAll(mongoFactoryList, iotFactoryMapDTOS);
        }

    }

    //持续超标/突然超标 防抖次数累计值入MongoDB
    @Async("taskExecutor")
    public void antiShakeValueMongoHandle(List<AntiShakeValueVO> antiShakeSaveList) {
        //更新Mongo对应配置规则下的防抖次数
        iotConfigMongoMapper.updateAntiShakeAll(antiShakeSaveList);
    }

    //触发次数 防抖次数累计值入Redis
    @Async("taskExecutor")
    public void antiShakeValueRedisHandle(List<AntiShakeValueVO> antiShakeSaveList) {
        //更新Mongo对应配置规则下的防抖次数
        for (AntiShakeValueVO antiShakeValueVO : antiShakeSaveList) {
            //key 配置id+规则id
            String key = Constants.ANTI_SHAKE_REDIS_KEY + antiShakeValueVO.getConfigId() + antiShakeValueVO.getGroupId();
            //获取剩余过期时间
            Long expire = RedisUtil.getExpire(key, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            if (expire > 0) {
                RedisUtil.set(key, antiShakeValueVO.getTriggerValue(), RedisDBType.PRIMARY);

            } else {
                RedisUtil.set(key, antiShakeValueVO.getTriggerValue(), antiShakeValueVO.getTimeoutValue(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
            }
        }
    }

    //告警生效物联设备存入设备池Redis
    @Async("taskExecutor")
    public void deviceGroupsValueRedisHandle(List<IotDeviceGroupsVO> deviceGroupsVOList) {
        //更新Mongo对应配置规则下的防抖次数
        for (IotDeviceGroupsVO deviceGroupsVO : deviceGroupsVOList) {
            //key 配置id+规则id
            String key = Constants.IOT_DEVICE_GROUPS_REDIS_KEY + deviceGroupsVO.getConfigId();
            //获取剩余过期时间
            Long expire = RedisUtil.getExpire(key, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            //不存在过期时间则新增数值及时效 否则更新数值
            if (expire > 0) {
                RedisUtil.set(key, deviceGroupsVO.getDeviceGroupList(), RedisDBType.PRIMARY);

            } else {
                RedisUtil.set(key, deviceGroupsVO.getDeviceGroupList(), 5, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            }
        }
    }

    //告警生效物联设备存入设备池Redis
    @Async("taskExecutor")
    public void iotAlarmSuppressRedisHandle(List<IotAlarmSuppressVO> alarmSuppressVOList) {
        //更新Mongo对应配置规则下的防抖次数
        for (IotAlarmSuppressVO iotAlarmSuppressVO : alarmSuppressVOList) {
            //key 配置id+规则id
            String key = Constants.IOT_ALARM_SUPPRESS_REDIS_KEY + iotAlarmSuppressVO.getConfigId();
            //获取剩余过期时间
            Long expire = RedisUtil.getExpire(key, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            //不存在过期时间则新增数值及时效
            if (expire <= 0) {
                RedisUtil.set(key, iotAlarmSuppressVO.getSuppressTime(), iotAlarmSuppressVO.getSuppressTime(), TimeUnit.SECONDS, RedisDBType.PRIMARY);
            }
        }
    }

    //查询二次识别配置 判断是否 二次识别任务下发操作
    @Async("taskExecutor")
    public void secondRecogIfExecute(OrderInfoVO orderInfoVO, String picUrl, String deviceCode, String tableType) {
        try {
            //查询二次识别配置条件
            SecondRecogReceiveVO receiveVO = new SecondRecogReceiveVO();
            receiveVO.setAlarmSourceValue(orderInfoVO.getWarningSource());
            receiveVO.setAlarmTypeValue(orderInfoVO.getWarningTypeId());
            receiveVO.setTenantId(orderInfoVO.getTenantId());
            receiveVO.setIndustryCode(orderInfoVO.getTradeId());
            receiveVO.setAppVerCode(orderInfoVO.getAppVerCode());
            //查询二次识别配置
            List<SecondRecogReceiveVO> configList = secondRecogConfigMapper.selectSecondRecogByOne(receiveVO);
            log.info(Constants.SECOND_SELECT_CONFIG_LOG + JSONObject.toJSONString(configList) + Constants.ALARM_INFORMATION_LOG + JSONObject.toJSONString(orderInfoVO));
            //二次识别配置不为空进行二次识别任务下发
            if (!CollectionUtils.isEmpty(configList) && configList.size() > 0) {
                //二次识别下发集合
                List<SecondRecogHandleVO> handleList = new ArrayList<>();
                SecondRecogHandleVO handleVO = new SecondRecogHandleVO();
                handleVO.setTaskId(orderInfoVO.getWarningOrderId());
                handleVO.setPicUrl(picUrl);
                handleVO.setConfigList(configList);
                handleVO.setSecondRecogTableType(tableType);
                handleVO.setDeviceCode(deviceCode);
                handleList.add(handleVO);
                this.recogSend(handleList);
            }
        } catch (Exception e) {
            log.error(Constants.SECOND_TASK_FALSE_LOG, e);
        }

    }

    //卫星查询二次识别配置 判断是否 二次识别任务下发操作
    @Async("taskExecutor")
    public void secondRecogIfExecuteBySatellite(List<OrderInfoVO> orderInfoVOs, String picUrl, String tableType) {
        try {
            //查询告警来源-告警类型下的二次识别配置
            List<String> list = orderInfoVOs.stream().map(v -> v.getWarningSource() + "-" + v.getWarningTypeId()).collect(Collectors.toList());
            List<SecondRecogReceiveVO> configList = secondRecogConfigMapper.selectSecondRecogByOnes(list);
            //二次识别下发集合
            List<SecondRecogHandleVO> handleList = new ArrayList<>();
            for (OrderInfoVO orderInfoVO : orderInfoVOs) {
                // 过滤configList中与orderInfoVO的属性相等的数据
                List<SecondRecogReceiveVO> filteredList = configList.stream()
                        .filter(config ->
                                config.getTenantId().equals(orderInfoVO.getTenantId()) &&
                                        config.getIndustryCode().equals(orderInfoVO.getIndustryCode()) &&
                                        config.getAlarmSourceValue().equals(orderInfoVO.getWarningSource()) &&
                                        config.getAlarmTypeValue().equals(orderInfoVO.getWarningTypeId())
                        )
                        .collect(Collectors.toList());
                //不为空则需要二次识别
                log.info(Constants.SECOND_SELECT_CONFIG_LOG + JSONObject.toJSONString(filteredList) + Constants.ALARM_INFORMATION_LOG + JSONObject.toJSONString(orderInfoVO));
                if (!CollectionUtils.isEmpty(filteredList) && filteredList.size() > 0) {
                    SecondRecogHandleVO handleVO = new SecondRecogHandleVO();
                    handleVO.setTaskId(orderInfoVO.getWarningOrderId());
                    handleVO.setPicUrl(picUrl);
                    handleVO.setConfigList(filteredList);
                    handleVO.setSecondRecogTableType(tableType);
                    handleList.add(handleVO);
                }
            }
            //二次识别下发
            this.recogSend(handleList);
        } catch (Exception e) {
            log.error(Constants.SECOND_TASK_FALSE_LOG, e);
        }
    }

    //卫片二次识别任务下发操作
    @Async("taskExecutor")
    public void secondRecogIfExecuteBySatelliteImages(List<OrderInfoVO> orderInfoVOs, String picUrl, String tableType, String selectAlgorithm) {
        log.info(Constants.ALARM_INFORMATION_LOG + JSONObject.toJSONString(orderInfoVOs));
        try {
            //二次识别下发集合
            List<SecondRecogHandleVO> handleList = new ArrayList<>();
            for (OrderInfoVO orderInfoVO : orderInfoVOs) {
                //不为空则需要二次识别
                SecondRecogHandleVO handleVO = new SecondRecogHandleVO();
                handleVO.setTaskId(orderInfoVO.getWarningOrderId());
                handleVO.setPicUrl(picUrl);
                List<SecondRecogReceiveVO> configList = new ArrayList<>();
                SecondRecogReceiveVO secondRecogReceiveVO = new SecondRecogReceiveVO();
                secondRecogReceiveVO.setSelectAlgorithm(selectAlgorithm);
                configList.add(secondRecogReceiveVO);
                handleVO.setConfigList(configList);
                handleVO.setSecondRecogTableType(tableType);
                handleList.add(handleVO);
            }
            //二次识别下发
            this.recogSend(handleList);
        } catch (Exception e) {
            log.error(Constants.SECOND_TASK_FALSE_LOG, e);
        }
    }

    //雷达查询二次识别配置 判断是否 二次识别任务下发操作
    @Async("taskExecutor")
    public void secondRecogIfExecuteByRadar(List<ReceiveAlarmVO> orderInfoVOs, String tableType) {
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
                    log.info(Constants.SECOND_SELECT_CONFIG_LOG + JSONObject.toJSONString(filteredList) + Constants.ALARM_INFORMATION_LOG + JSONObject.toJSONString(orderInfoVO));
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
        } catch (Exception e) {
            log.error(Constants.SECOND_TASK_FALSE_LOG, e);
        }
    }

    public String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if (Objects.nonNull(configInfo.get(Constants.RESULT_DATA))) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }

    //二次识别下发
    private void recogSend(List<SecondRecogHandleVO> handleList) {
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
                fileVO.setImgUrls(picUrl);
                fileVO.setType("image");
                //二次识别配置
                List<SecondRecogReceiveVO> configList = handleVO.getConfigList();
                //生成随机6位
                String uuId = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                recogSendVO.setTaskId(handleVO.getTaskId() + "/" + handleVO.getSecondRecogTableType() + "/" + configList.get(0).getSelectAlgorithm() + "/" + platformNode + "/" + uuId);
                recogSendVO.setAlgorithmCode(configList.get(0).getSelectAlgorithm());
                recogSendVO.setAcceptNode(platformNode);
                recogSendVO.setFileInfo(fileVO);
                recogSendVO.setLinkageCamera(configList.get(0).getLinkageCamera());
                recogSendVO.setLinkageAction(configList.get(0).getLinkageAction());
                recogSendVO.setSelectAlgorithm(configList.get(0).getSelectAlgorithm());
                SecondExtraFieldInfoVo extraFieldInfoVo = new SecondExtraFieldInfoVo();
                extraFieldInfoVo.setDeviceCode(handleVO.getDeviceCode());
                recogSendVO.setExtraFieldInfo(extraFieldInfoVo);
                list.add(recogSendVO);
            }
            //二次下发 返回taskBatchId
            String taskBatchId = this.synAnalysisTask(list);
            if (StringUtil.isNotEmpty(taskBatchId)) {
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
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Constants.SECOND_RECOG_TASK_INFO);
        //接收需要更新新增的二次识别批次信息
        for (SecondRecogBatchVO listVO : list) {
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.DESC_TASK_ID_PARAM).is(listVO.getTaskId()));
            Update update = new Update();
            update.set(Constants.DESC_TASK_ID_PARAM, listVO.getTaskId());
            update.set("taskBatchId", listVO.getTaskBatchId());
            update.set("taskType", listVO.getTaskType());
            update.set("createTime", com.chinatower.common.core.utils.DateUtils.getTime());
            update.set("analyType", listVO.getAnalyType());
            update.set("deleteFlag", listVO.getDeleteFlag());
            update.set("acceptNode", listVO.getAcceptNode());
            update.set("accessNode", listVO.getAccessNode());
            update.set("algorithmCode", listVO.getAlgorithmCode());
            //如果是无人机告警 或预置位
            if (Constants.TWO.equals(listVO.getTaskType()) || Constants.THREE.equals(listVO.getTaskType())) {
                update.set("picId", listVO.getPicId());
                update.set("videoId", listVO.getVideoId());
                update.set("deviceCode", listVO.getDeviceCode());
                update.set("channelCode", listVO.getChannelCode());
                update.set("airTaskId", listVO.getAirTaskId());
                update.set("airLineId", listVO.getAirLineId());
                update.set("tenantId", listVO.getTenantId());
                update.set("tenantName", listVO.getTenantName());
                update.set("industryCode", listVO.getIndustryCode());
                update.set("appVerCode", listVO.getAppVerCode());
                update.set("longitude", listVO.getLongitude());
                update.set("latitude", listVO.getLatitude());
                update.set("makeAlarmFlag", listVO.getMakeAlarmFlag());
                update.set("referenceId", listVO.getReferenceId());
                update.set("uavRecogSource", listVO.getUavRecogSource());
            }
            if (Constants.THREE.equals(listVO.getTaskType())) {
                update.set("ptzInfoJson", listVO.getPtzInfoJson());
            }
            bulkOperations.upsert(query, update);
        }
        bulkOperations.execute();
    }

    //二次识别下发
    public String uavSynAnalysisTask(List<SecondRecogSendVO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            //查询节点编码
            String platformNode = this.getPlatformNode();
            String queryTaskId = null;
            for (SecondRecogSendVO secondRecogSendVO : list) {
                if (StringUtil.isEmpty(secondRecogSendVO.getTaskId())) {
                    //生成随机6位
                    String uuId = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                    //uuid+算法编码+无人机设备编码+飞行任务id+飞行航线id
                    String taskId = uuId + "/" + secondRecogSendVO.getExtraFieldInfo().getDeviceCode() + "/" + secondRecogSendVO.getAlgorithmCode() + "/" + secondRecogSendVO.getExtraFieldInfo().getAirTaskId() + "/" + secondRecogSendVO.getExtraFieldInfo().getAirLineId();
                    secondRecogSendVO.setTaskId(taskId);
                    queryTaskId = secondRecogSendVO.getExtraFieldInfo().getAirTaskId();
                }
            }
            //查询任务所属设备的接入节点
            AirAlTaskLogReq airAlTaskLogReq = new AirAlTaskLogReq();
            airAlTaskLogReq.setTaskId(queryTaskId);
            Result result = iAirLineService.queryTaskNode(airAlTaskLogReq);
            String accessNode = null;
            if (ObjectUtil.isNotNull(result)) {
                accessNode = (String) result.getData();
            }
            LoginUser loginUser = SecurityUtils.getLoginUser();
            for (SecondRecogSendVO secondRecogSendVO : list) {
                secondRecogSendVO.setAccessNode(accessNode);
                secondRecogSendVO.setTaskType(Constants.TWO);
                if (loginUser != null && loginUser.getUser() != null) {
                    secondRecogSendVO.setTenantId(loginUser.getUser().getTenantId());
                    secondRecogSendVO.setTenantName(loginUser.getUser().getTenantName());
                    secondRecogSendVO.setIndustryCode(loginUser.getUser().getIndustryCode());
                }
            }
            String taskBatchId = this.publicSynAnalysisTask(list);
            log.info("无人机二次识别下发结果taskId{}", taskBatchId);
            return taskBatchId;
        }
        return "";
    }


    public String publicSynAnalysisTask(List<SecondRecogSendVO> list) {
        String taskBatchId = "";
        String tenantName = "";
        if (!CollectionUtils.isEmpty(list)) {
            //查询节点编码
            String platformNode = this.getPlatformNode();
            String accessNode = list.get(0).getAccessNode();
            String taskType = list.get(0).getTaskType();
            String tenantId = list.get(0).getTenantId();
            if (!StringUtils.isEmpty(tenantId)) {
                tenantName = sysMapper.queryUserById(tenantId);
            }
            taskBatchId = this.synCasCadeAnalysisTask(list, accessNode, platformNode);
            String videoId = "";
            String airTaskId = "";
            List<AirHistoryRep> historyList = new ArrayList<>();
            if (Constants.TWO.equals(taskType)) {
                videoId = list.get(0).getExtraFieldInfo().getVideoId();
                airTaskId = list.get(0).getExtraFieldInfo().getAirTaskId();
                //判断是否有视频id 有查询历史轨迹获取经纬度
                if (StringUtil.isNotEmpty(videoId)) {
                    AirHistoryReq historyReq = new AirHistoryReq();
                    historyReq.setTaskRecordId(airTaskId);
                    JSONArray historyArray = iAirLineService.queryHisRecord(historyReq);
                    log.info("通过飞行任务id查询历史轨迹结果{}", JSON.toJSONString(historyArray));
                    if (historyArray != null) {
                        historyList = JSONObject.parseArray(JSON.toJSONString(historyArray), AirHistoryRep.class);
                    }
                }
            }
            if (StringUtil.isNotEmpty(taskBatchId)) {
                List<SecondRecogBatchVO> batchList = new ArrayList<>();
                for (SecondRecogSendVO vo : list) {
                    SecondRecogBatchVO batchVO = new SecondRecogBatchVO();
                    batchVO.setTaskId(vo.getTaskId());
                    batchVO.setTaskBatchId(taskBatchId);
                    batchVO.setTaskType(taskType);
                    batchVO.setAnalyType(Constants.ZERO);
                    batchVO.setDeleteFlag(Constants.ZERO);
                    batchVO.setAcceptNode(platformNode);
                    batchVO.setAccessNode(accessNode);
                    batchVO.setAlgorithmCode(vo.getAlgorithmCode());
                    batchVO.setPicId(vo.getExtraFieldInfo().getPicId());
                    batchVO.setVideoId(vo.getExtraFieldInfo().getVideoId());
                    batchVO.setDeviceCode(vo.getExtraFieldInfo().getDeviceCode());
                    batchVO.setChannelCode(vo.getExtraFieldInfo().getChannelCode());
                    batchVO.setAirTaskId(vo.getExtraFieldInfo().getAirTaskId());
                    batchVO.setAirLineId(vo.getExtraFieldInfo().getAirLineId());
                    batchVO.setTenantId(vo.getTenantId());
                    batchVO.setTenantName(vo.getTenantName());
                    batchVO.setUavRecogSource(vo.getUavRecogSource());
                    if (Constants.THREE.equals(taskType)) {
                        batchVO.setTenantName(tenantName);
                    }
                    batchVO.setIndustryCode(vo.getIndustryCode());
                    batchVO.setAppVerCode(vo.getAppVerCode());
                    if (StringUtils.isEmpty(vo.getMakeAlarmFlag())) {
                        batchVO.setMakeAlarmFlag(Constants.ONE);
                    } else {
                        batchVO.setMakeAlarmFlag(vo.getMakeAlarmFlag());
                    }
                    batchVO.setReferenceId(vo.getReferenceId());
                    //视频下发任务取视频轨迹第一个点位
                    if (StringUtil.isNotEmpty(videoId) && Constants.TWO.equals(taskType)) {
                        this.matchFirstPoint(vo.getExtraFieldInfo().getTrailTime(), historyList, batchVO);
                    } else {
                        batchVO.setLongitude(vo.getLongitude());
                        batchVO.setLatitude(vo.getLatitude());
                    }
                    //存储预置位PTZ信息
                    if (ObjectUtil.isNotNull(vo.getTranslnglatPeopleDTO())) {
                        batchVO.setPtzInfoJson(JSONObject.toJSONString(vo.getTranslnglatPeopleDTO()));
                    }
                    batchList.add(batchVO);
                }
                //更新新增的二次识别批次信息
                this.updateSecondRecogInFo(batchList);
            }
        }
        return taskBatchId;
    }

    public void matchFirstPoint(String trailTime, List<AirHistoryRep> historyList, SecondRecogBatchVO batchVO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS);
        LocalDateTime trailDateTime = LocalDateTime.parse(trailTime, formatter);
        if (!CollectionUtils.isEmpty(historyList)) {
            // 匹配视频开始时间找到第一个符合条件的对象
            AirHistoryRep result = historyList.stream()
                    .filter(history -> {
                        LocalDateTime saveDateTime = LocalDateTime.parse(history.getSaveDateTime(), formatter);
                        return saveDateTime.isAfter(trailDateTime) || saveDateTime.isEqual(trailDateTime);
                    })
                    .findFirst()
                    .orElse(null);
            if (ObjectUtil.isNotNull(result) && ObjectUtil.isNotNull(result.getAircraftLocation())) {
                batchVO.setLongitude(String.valueOf(result.getAircraftLocation().getLongitude()));
                batchVO.setLatitude(String.valueOf(result.getAircraftLocation().getLatitude()));
            }
        }

    }

    //二次识别下发
    public String synAnalysisTask(List<SecondRecogSendVO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            String joIn = String.valueOf(JSONObject.toJSON(list));
            NetConnectUtils.RequestParamter<List<SecondRecogSendVO>> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(list);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYN_ANALYSIS_TASK);
            paramter.setPath(path);
            log.info("二次识别推送入参：" + joIn);
            String msgId = UUID.randomUUID().toString().replace("-", "");
            String requestTime = new SimpleDateFormat(Constants.DATE_YMDHMS).format(new Date());
            try {
                String signature = OkhttpUtil.getSignature(APP_KEY, SECRET_KEY, msgId, requestTime);
                log.info("调用网关二次识别推送入参：" + JSONObject.toJSONString(paramter));
                ResultEntity cr = connectUtils.recordPost(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_MsgId.of(msgId),
                        NetConnectUtils.HeaderKeyEnum.X_RequsetTime.of(requestTime),
                        NetConnectUtils.HeaderKeyEnum.X_AppCode.of(APP_CODE),
                        NetConnectUtils.HeaderKeyEnum.X_Signature.of(signature),
                        NetConnectUtils.HeaderKeyEnum.X_AppKey.of(APP_KEY));
                log.info("二次识别推送结果：" + JSONObject.toJSONString(cr));
                if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
                    Map map = JSON.parseObject(JSON.toJSONString(cr.getData()), Map.class);
                    return String.valueOf(map.get(Constants.PARAM_TASK_BATCH_ID));
                }

            } catch (Exception e) {
                log.error("调用网关二次下发失败:{}", e);
            }
        }
        return "";
    }

    //无人机 二次识别下发 跨节点 级联网关
    public String synCasCadeAnalysisTask(List<SecondRecogSendVO> list, String toPlatformCode, String platformCode) {
        if (!CollectionUtils.isEmpty(list)) {
            String joIn = String.valueOf(JSONObject.toJSON(list));
            NetConnectUtils.RequestParamter<List<SecondRecogSendVO>> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(list);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYN_ANALYSIS_TASK);
            paramter.setPath(path);
            log.info("二次识别推送入参：" + joIn);
            String msgId = UUID.randomUUID().toString().replace("-", "");
            String requestTime = new SimpleDateFormat(Constants.DATE_YMDHMS).format(new Date());
            try {
                String signature = OkhttpUtil.getSignature(APP_KEY, SECRET_KEY, msgId, requestTime);
                log.info("调用网关二次识别推送入参：" + JSONObject.toJSONString(paramter));
                ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_AppCode.of(APP_CODE),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toPlatformCode),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("algorithm"),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info("二次识别推送结果：" + JSONObject.toJSONString(cr));
                if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
                    Map map = JSON.parseObject(JSON.toJSONString(cr.getData()), Map.class);
                    return String.valueOf(map.get(Constants.PARAM_TASK_BATCH_ID));
                }
            } catch (Exception e) {
                log.error("调用网关二次下发失败:{}", e);
            }
        }
        return "";
    }

    //二次识别任务告警批量查询
    public List<SecondRecogBackVO> alarmByTaskBatchId(String taskBatchId, String platFormCode, String toPlatformCode, String type) {
        List<SecondRecogBackVO> backVOS = new ArrayList<>();
        if (StringUtil.isNotEmpty(taskBatchId)) {
            AlarmByTaskReq alarmByTaskReq = new AlarmByTaskReq();
            NetConnectUtils.RequestParamter<AlarmByTaskReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            alarmByTaskReq.setTaskBatchId(taskBatchId);
            paramter.setBody(alarmByTaskReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.ALARM_BY_TASK_BATCH_ID);
            paramter.setPath(path);
            String msgId = UUID.randomUUID().toString().replace("-", "");
            String requestTime = new SimpleDateFormat(Constants.DATE_YMDHMS).format(new Date());
            try {
                String signature = OkhttpUtil.getSignature(APP_KEY, SECRET_KEY, msgId, requestTime);
                log.info("调用网关二次识别任务告警批量查询推送入参：" + JSONObject.toJSONString(paramter));
                ResultEntity cr = new ResultEntity();
                if ("1".equals(type)) {
                    cr = connectUtils.recordPost(paramter, ResultEntity.class,
                            NetConnectUtils.HeaderKeyEnum.X_MsgId.of(msgId),
                            NetConnectUtils.HeaderKeyEnum.X_RequsetTime.of(requestTime),
                            NetConnectUtils.HeaderKeyEnum.X_AppCode.of(APP_CODE),
                            NetConnectUtils.HeaderKeyEnum.X_Signature.of(signature),
                            NetConnectUtils.HeaderKeyEnum.X_AppKey.of(APP_KEY));
                }
                if ("2".equals(type) || Constants.THREE.equals(type)) {
                    cr = connectUtils.post(paramter, ResultEntity.class,
                            /*NetConnectUtils.HeaderKeyEnum.X_MsgId.of(msgId),
                            NetConnectUtils.HeaderKeyEnum.X_RequsetTime.of(requestTime),
                            NetConnectUtils.HeaderKeyEnum.X_Signature.of(signature),
                            NetConnectUtils.HeaderKeyEnum.X_AppKey.of(APP_KEY),*/
                            NetConnectUtils.HeaderKeyEnum.X_AppCode.of(APP_CODE),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(toPlatformCode),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(platFormCode),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("algorithm"),
                            NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                }


                log.info("二次识别任务告警批量查询推送结果：" + JSONObject.toJSONString(cr));
                if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
                    backVOS = JSON.parseArray(JSON.toJSONString(cr.getData()), SecondRecogBackVO.class);
                }
                log.info("二次识别任务告警批量查询转换结果：" + JSONObject.toJSONString(backVOS));
            } catch (Exception e) {
                log.error("调用网关二次识别任务告警批量查询失败:{}", e);
            }
        }
        return backVOS;
    }

    /**
     * 智能研判任务识别
     */
    public JSONObject intelligentJudgeTask(IntelligentJudgeTaskRequest taskRequest) {
        try {
            log.info("开始智能研判任务识别，taskRequest：{}", taskRequest);
            JSONObject result = synIntelligentJudgeTask(taskRequest);
            log.info("智能研判任务识别结果：{}", result);
            return result;
        } catch (Exception e) {
            log.error("智能研判任务下发异常", e);
            return null;
        }
    }

    /**
     * 同步调用智能研判接口
     * 4.55.	同步单次任务下发V2.0 /v2/sync/algorithms/synAnalysisTask
     * @param taskRequest 任务请求参数
     * @return 任务批次ID
     */
    private JSONObject synIntelligentJudgeTask(IntelligentJudgeTaskRequest taskRequest) {
        try {
            String joIn = JSON.toJSONString(taskRequest);
            NetConnectUtils.RequestParamter<IntelligentJudgeTaskRequest> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(taskRequest);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址 - 智能研判接口路径，使用新的枚举
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SYN_INTELLIGENT_JUDGE_TASK);
            paramter.setPath(path);

            log.info("智能研判任务推送入参：{}", joIn);
            String msgId = UUID.randomUUID().toString().replace("-", "");
            String requestTime = new SimpleDateFormat(Constants.DATE_YMDHMS).format(new Date());

            String signature = OkhttpUtil.getSignature(MCP_APP_KEY, MCP_SECRET_KEY, msgId, requestTime);
            log.info("调用网关智能研判任务推送入参：{}", JSONObject.toJSONString(paramter));

            ResultEntity cr = connectUtils.recordPost(paramter, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_MsgId.of(msgId),
                    NetConnectUtils.HeaderKeyEnum.X_RequsetTime.of(requestTime),
                    NetConnectUtils.HeaderKeyEnum.X_AppCode.of(MCP_APP_CODE),
                    NetConnectUtils.HeaderKeyEnum.X_Signature.of(signature),
                    NetConnectUtils.HeaderKeyEnum.X_AppKey.of(MCP_APP_KEY));

            log.info("智能研判任务推送结果：{}", JSONObject.toJSONString(cr));

            if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getData())) {
                JSONObject result = JSON.parseObject(JSON.toJSONString(cr.getData()));
                return result;
            }

        } catch (Exception e) {
            log.error("调用网关智能研判任务下发失败", e);
        }
        return null;
    }

}
