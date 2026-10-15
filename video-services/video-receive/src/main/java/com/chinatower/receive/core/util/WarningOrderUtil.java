package com.chinatower.receive.core.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.deprecated.IIndustryDeviceServiceMigrate;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.entity.dto.TransparentFieldsDto;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.SecondRecogReceiveVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.industry.dto.DeviceIndReqDTO;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.receive.constant.MessageConstants;
import com.chinatower.receive.mapper.SecondRecogConfigMapper;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.IFlowLinkService;
import com.chinatower.receive.service.ITransLntlagRepository;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.dto.VmFlowTemplateLinkDto;
import com.chinatower.receive.vo.dto.VmFlowTemplateLinkModel;
import com.chinatower.receive.vo.entity.DeviceSiteReceiveVO;
import com.chinatower.receive.vo.entity.TranslnglatVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.IVideoPresetService;
import com.chinatower.video.api.entity.PresetImgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.chinatower.receive.kafka.KafkaProducer;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 处理告警相关公共方法集合
 * @Author: songxinfeng
 * @Date: 2024/7/2 10:46
 */

@Component("warningOrderUtil")
@Slf4j
public class WarningOrderUtil {

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private IFlowLinkService IFlowLinkService;

    @Autowired
    private SecondRecogConfigMapper secondRecogConfigMapper;

    @Autowired
    private ITransLntlagRepository transLntlagRepository;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IVideoPresetService videoPresetService;

    @Autowired
    private IIndustryDeviceServiceMigrate iIndustryDeviceServiceMigrate;

    /**
     * 获取告警压制开关
     *
     * @return
     */
    public String getInterceptSwitch() {
        Result<List<SysDictDataResponse>> sysResult = systemFeignClient.dictDataType(Constants.ALARM_SWITCH_VAR);
        if (sysResult.getCode() == ResultCode.SUCCESS.getCode()) {
            return sysResult.getData().get(0).getDictValue();
        }
        return Constants.ZERO;
    }

    /**
     * 判断是否符合研判压制的条件，放入告警压制集合
     * 判断告警等级、压制规则
     *
     * @param alarmVO
     * @param intercepts
     * @return
     */
    public boolean judgeOperationIntercept(ReceiveAlarmVO alarmVO, List<ReceiveAlarmVO> intercepts) {
        String tenantId = alarmVO.getTenantId();
        String deviceCode = alarmVO.getDeviceCode();
        String alarmType = String.valueOf(alarmVO.getAlarmType());

        //获取研判压制,压制规则保存到redis中,redis中有数据时进行压制
        String key = Constants.INTERCEPT_CONFIG + tenantId + "_" + alarmVO.getIndustryCode() + "_" + deviceCode + "_" + alarmType;
        String interceptConfig = RedisUtil.get(key, RedisDBType.PRIMARY);
        if (!StringUtils.isEmpty(interceptConfig)) {
            alarmVO.setIntercept(AlgorithmEventVO.FOR_JUDGE);
            intercepts.add(alarmVO);
            return true;
        }

        return false;
    }

    public void setSecondConfig(List<ReceiveAlarmVO> dataList) {
        String warningSource = dataList.get(0).getWarningSource();
        if ("8".equals(warningSource) || "10".equals(warningSource)|| "9".equals(warningSource)) {
            //查询告警来源-告警类型下的二次识别配置
            List<String> list = dataList.stream().map(v -> v.getWarningSource() + "-" + v.getAlarmType()).collect(Collectors.toList());
            List<SecondRecogReceiveVO> configList = secondRecogConfigMapper.selectSecondRecogByOnes(list);
            for (ReceiveAlarmVO orderInfoVO : dataList) {
                List<SecondRecogReceiveVO> filteredList = configList.stream()
                        .filter(config ->
                                config.getTenantId().equals(orderInfoVO.getTenantId()) &&
                                        config.getIndustryCode().equals(orderInfoVO.getIndustryCode()) &&
                                        config.getAlarmSourceValue().equals(orderInfoVO.getWarningSource()) &&
                                        config.getAlarmTypeValue().equals(orderInfoVO.getAlarmType())
                        )
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(filteredList) && filteredList.size()>0){
                    orderInfoVO.setIsSecondIdentify("0");
                }

            }
        }

    }


    //告警信息中新增：俯仰角、水平视场角、垂直视场角字段呈现
    public void setAiAndDeviceAngle(List<ReceiveAlarmVO> dataList) {
        String warningSource = dataList.get(0).getWarningSource();
        if ("1".equals(warningSource) || "4".equals(warningSource)) {
            //获取当前节点
            SysConfig sysConfig = new SysConfig();
            sysConfig.setConfigKey(com.chinatower.event.constants.Constants.VISIBLE_PTZ_CONFIG);
            String visiblePtzConfig = "";
            //可视域ptz使用配置  0-默认ptz逻辑、1-调用基础平台接口
            AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
            if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
                SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
                if (sysConfigInfo != null) {
                    visiblePtzConfig = sysConfigInfo.getConfigValue();
                }
            }
            if (!StringUtils.isEmpty(visiblePtzConfig) && "1".equals(visiblePtzConfig)) {
                for (ReceiveAlarmVO orderInfoVO : dataList) {
                    DeviceIndReqDTO deviceIndReqDTO = new DeviceIndReqDTO();
                    deviceIndReqDTO.setDeviceCode(orderInfoVO.getDeviceCode());
                    deviceIndReqDTO.setChannelCode(orderInfoVO.getChannelCode());
                    deviceIndReqDTO.setTenantId(orderInfoVO.getTenantId());
                    deviceIndReqDTO.setIndustryCode(orderInfoVO.getIndustryCode());
                    log.info("调用iIndustryDeviceServiceMigrate-getViewPitchAngle入参" + JSONObject.toJSONString(deviceIndReqDTO));
                    //查询摄像机俯仰角水平角
                    Result<JSONObject> viewPitchAngle = iIndustryDeviceServiceMigrate.getViewPitchAngle(deviceIndReqDTO);
                    log.info("调用iIndustryDeviceServiceMigrate-getViewPitchAngle结果" + JSONObject.toJSONString(viewPitchAngle));
                    if (ResultCode.SUCCESS.getCode() == viewPitchAngle.getCode()) {
                        JSONObject json = viewPitchAngle.getData();
                        //俯仰角
                        if (json.get("pitchAngle") != null){
                            orderInfoVO.setPitchAngle(json.getString("pitchAngle"));
                        }
                        //水平视场角
                        if (json.get("viewXAngle") != null){
                            orderInfoVO.setHoriFieldAngle(json.getString("viewXAngle"));
                        }
                        //垂直视场角
                        if (json.get("viewYAngle") != null){
                            orderInfoVO.setVerticalFieldAngle(json.getString("viewYAngle"));
                        }
                    }
                }
            }
        }

    }

    /**
     * 根据taskId批量更新二次识别mongo表工单号
     * @param dataList
     */
    public void setOrderToSecondTask(List<ReceiveAlarmVO> dataList,String orderType) {
        log.info("setOrderToSecondTask-开始批量二次识别任务Mongo：" + JSONObject.toJSONString(dataList));
        try {
            //根据任务id更新二次识别任务
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,Constants.SECOND_RECOG_TASK_INFO);
            for (ReceiveAlarmVO alarmVO : dataList) {
                Query query = new Query(Criteria.where(Constants.DESC_TASK_ID_PARAM).is(alarmVO.getTaskId()));
                Update update = new Update().set("warningOrderId", alarmVO.getWarningOrderId()).set("orderType",orderType);
                bulkOperations.upsert(query, update);
                //君南-预置位vm_video_preset_snapshot mongo表更新warningOrderId
                if (Constants.ZERO.equals(alarmVO.getIsPresetMakeAlarm())){
                    PresetImgVO presetImgVO = new PresetImgVO();
                    presetImgVO.setWarningOrderId(alarmVO.getWarningOrderId());
                    presetImgVO.setTaskId(alarmVO.getTaskId());
                    log.info("调用updateWarningOrderId-入参{}：" + JSONObject.toJSONString(presetImgVO));
                    Result<String> result = videoPresetService.updateWarningOrderId(presetImgVO);
                    log.info("调用updateWarningOrderId-返回结果{}：" + JSONObject.toJSONString(result));
                }
            }
            bulkOperations.execute();
            log.info("setOrderToSecondTask-批量更新完成!");
        } catch (Exception e) {
            log.error("更新二次识别任务失败", e);
        }
    }

    /**
     * 判断租户行业版本中是否包含需要特殊处理的行业版本，若包含则为true
     *
     * @param dictList
     * @param alarmVO
     * @return
     */
    public boolean judgeSpecialAppVer(List<SysDictDataResponse> dictList, ReceiveAlarmVO alarmVO) {
        //特殊行业版本发kafak
        if (!CollectionUtils.isEmpty(dictList)) {
            for (SysDictDataResponse sysDictDataResponse : dictList) {
                log.info("app_ver_code_topic:" + sysDictDataResponse.getDictValue());
                String[] appTopics = sysDictDataResponse.getDictValue().split("##");
                String appVerCode = appTopics[0];
                String topic = appTopics[1];

                if (alarmVO.getAppVerCode().equals(appVerCode)) {
                    log.info("告警推送行业版本编码:" + alarmVO.getAppVerCode() + "特殊处理");
                    String jsonStr = JSONObject.toJSONString(alarmVO);
                    kafkaProducer.sendToKafka(topic, jsonStr);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设置告警点省市区及告警定位信息
     * @param deviceSiteReceiveVO
     * @param receiveAlarmVO
     */
    public void setAlarmLocation(DeviceSiteReceiveVO deviceSiteReceiveVO, ReceiveAlarmVO receiveAlarmVO) {
        if (null != deviceSiteReceiveVO) {
            // 获取摄像头区域
            receiveAlarmVO.setProvinceCode(deviceSiteReceiveVO.getProvinceId());
            receiveAlarmVO.setCityCode(deviceSiteReceiveVO.getCityId());
            receiveAlarmVO.setCountyCode(deviceSiteReceiveVO.getCountyId());
            receiveAlarmVO.setProvinceName(deviceSiteReceiveVO.getProvinceName());
            receiveAlarmVO.setCityName(deviceSiteReceiveVO.getCityName());
            receiveAlarmVO.setCountyName(deviceSiteReceiveVO.getCountyName());
            //仅摄像机告警和AI告警才可开启告警定位功能
            if("1".equals(deviceSiteReceiveVO.getAlarmLocation()) && (Constants.ONE.equals(receiveAlarmVO.getAlarmSource()) || Constants.FOUR.equals(receiveAlarmVO.getAlarmSource()) || "11".equals(receiveAlarmVO.getAlarmSource()))){
                //告警定位
                TranslnglatVO aiRelaLntlag = transLntlagRepository.getAiRelaLntlag(receiveAlarmVO);
                if (Objects.nonNull(aiRelaLntlag)
                        && !"0".equals(aiRelaLntlag.getLatitude())
                        && !"0".equals(aiRelaLntlag.getLongitude())) {
                    receiveAlarmVO.setLatitude(aiRelaLntlag.getLatitude());
                    receiveAlarmVO.setLongitude(aiRelaLntlag.getLongitude());
                    String flagT = aiRelaLntlag.getFlatA();
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(flagT)) {
                        flagT = new BigDecimal(flagT).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
                    }
                    receiveAlarmVO.setHoriAzimuthAngle(flagT);
                    receiveAlarmVO.setDeviceHorizontal(flagT);
                }
            }
        }
    }

    /**
     * 设置网格信息
     * @param receiveAlarmDTO
     */
    public void setAlarmGridInfo(ReceiveAlarmDTO receiveAlarmDTO) {
        ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();

        //替换经纬度中的逗号
        if(!StringUtils.isEmpty(receiveAlarmVO.getLatitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLongitude())){
            receiveAlarmVO.setLatitude(receiveAlarmVO.getLatitude().replaceAll(Constants.COMMA,""));
            receiveAlarmVO.setLongitude(receiveAlarmVO.getLongitude().replaceAll(Constants.COMMA,""));
        }
        //告警事件坐标（目前为设备坐标）为空，则取站址的坐标
        String latitude = receiveAlarmVO.getLatitude();
        String longitude = receiveAlarmVO.getLongitude();
        // 根据经纬度获取网格信息
        if (!StringUtils.isEmpty(receiveAlarmVO.getLatitude()) && !StringUtils.isEmpty(receiveAlarmVO.getLongitude())) {
            List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();
            try {
                GridByLngLatParam param = new GridByLngLatParam();
                param.setLatitude(latitude);
                param.setLongitude(longitude);
                List<BaseIndustryEntity> tenantList = new ArrayList<>();
                customers.forEach(item ->{
                    BaseIndustryEntity baseIndustryEntity = new BaseIndustryEntity();
                    baseIndustryEntity.setIndustryCode(item.getIndustryCode());
                    baseIndustryEntity.setTenantId(item.getTenantId());
                    tenantList.add(baseIndustryEntity);
                });
                param.setTenantList(tenantList);
                log.info("setAlarmGridInfo-getMessageByLngLatNew-req:{}", JSON.toJSONString(param));
                Result<List<GridByLngLat>> result = gridAPI.getMessageByLngLatNew(param);

                log.info(MessageFormat.format(MessageConstants.GRID_MESSAGE, result));
                if (result != null && ResultCode.SUCCESS.getCode() == result.getCode()) {
                    List<GridByLngLat> gridList = result.getData();
                    if (!CollectionUtils.isEmpty(gridList)) {
                        customers = customers.stream().map(item -> {
                            GridByLngLat gridInfo = gridList.stream().filter(grid -> Objects.equals(item.getTenantId(), grid.getTenantId()) && Objects.equals(item.getIndustryCode(), grid.getIndustryCode()))
                                    .max(Comparator.comparing(GridByLngLat::getGridLevel)).orElse(new GridByLngLat());
                            item.setGridId(gridInfo.getGridId());
                            item.setGridName(gridInfo.getGridName());
                            item.setGridUserList(gridInfo.getUserList());
                            return item;
                        }).collect(Collectors.toList());

                        receiveAlarmDTO.setCustomers(customers);
                    }
                }
            }
            catch (Exception e) {
                log.error(MessageConstants.GRID_ERROR_MESSAGE, e);
            }
        }
    }

    /**
     * 判断运营研判条件并保存运营研判集合
     *
     * @param alarmVO
     * @param alarms
     * @param operations
     * @return
     */
    public boolean doOperate(ReceiveAlarmVO alarmVO, List<ReceiveAlarmVO> alarms, List<ReceiveAlarmVO> operations) {
        if (this.getIsOpenOperation(alarmVO)) {
            alarmVO.setIsOperation(Constants.ONE);
            if (this.getIsParallelOperation(alarmVO)) {//并行流程，同时生成告警
                alarmVO.setNotifyType(Constants.YES_FLAG);
                alarmVO.setJudgeType(Constants.JUDGE_TYPE_PARALLEL);
                alarmVO.setIsOperation(Constants.TWO);
                alarmVO.setOperationDept(alarmVO.getParallelOperationDept());
                alarmVO.setOperationNode(alarmVO.getParallelOperationNode());
                alarms.add(alarmVO);

                // 并行流程时，如果配置的是简化流程，没有研判环节，不生成运营研判
                // 获取简易流程配置
                VmFlowTemplateLinkModel vmFlowTemplateLinkDto = this.getOperateLinkModel(alarmVO);
                if (vmFlowTemplateLinkDto == null) {
                    return true;
                }
            }
            // 保存运营研判数据
            ReceiveAlarmVO operateVo = new ReceiveAlarmVO();
            BeanUtils.copyProperties(alarmVO, operateVo);
            operateVo.setNotifyType(Constants.TWO_FLAG);
            operations.add(operateVo);
            return true;
        }
        return false;
    }

    /**
     * 获取运营研判的环节
     *
     * @param receiveAlarmVO
     * @return
     */
    public VmFlowTemplateLinkDto getOperateLinkModel(ReceiveAlarmVO receiveAlarmVO) {
        //行业版本编码
        String appVerCoe = receiveAlarmVO.getAppVerCode();
        //行业编码
        String industryCode = receiveAlarmVO.getIndustryCode();
        String tenantId = receiveAlarmVO.getTenantId();
        String warningSource = receiveAlarmVO.getWarningSource();
        String alarmType = receiveAlarmVO.getAlarmType();
        FlowAlarmTemplateDto flowAlarmTemplateDto = IFlowLinkService.getFlowTemplate(appVerCoe, industryCode, tenantId, warningSource, alarmType);
        if (null != flowAlarmTemplateDto) {
            //查询流程是否初始环节有研判环节
            VmFlowTemplateLinkDto dto= IFlowLinkService.getFlowTemplateLink(flowAlarmTemplateDto.getTemplateId(), flowAlarmTemplateDto.getAlarmFlowId(), Long.valueOf(EnumUtil.FlowNode.PENDING_JUDGE.getCode()),null,industryCode,appVerCoe);
            if(dto != null && !StringUtils.isEmpty(dto.getNextLink()) && !dto.getNextLink().contains(",")){
                return dto;
            }
        }
        return null;
    }

    /**
     * 是否为并行研判
     */
    public boolean getIsParallelOperation(ReceiveAlarmVO alarmVO){
        return (alarmVO.getOperationAlarmSource().contains(Constants.FIRST_LINK) && alarmVO.getOperationType().contains(Constants.TWO))
                || (!StringUtils.isEmpty(alarmVO.getParallelAlarmTypeStr()) && Arrays.asList(alarmVO.getParallelAlarmTypeStr().split(Constants.COMMA)).contains(alarmVO.getAlarmType()));
    }

    /**
     * 是否开启运营研判并且当前告警类型是否符合配置
     */
    public boolean getIsOpenOperation(ReceiveAlarmVO alarmVO){
        return !Constants.VALID_STATE.equals(alarmVO.getIsOperation()) && !StringUtils.isEmpty(alarmVO.getOperationAlarmSource()) &&
                ( (alarmVO.getOperationAlarmSource().contains(Constants.FIRST_LINK)  && Constants.OPERATION_ALARM_SOURCE.contains(alarmVO.getAlarmSource())) ||
                        (!StringUtils.isEmpty(alarmVO.getParallelAlarmTypeStr()) && Arrays.asList(alarmVO.getParallelAlarmTypeStr().split(Constants.COMMA)).contains(alarmVO.getAlarmType())) ||
                        (!StringUtils.isEmpty(alarmVO.getSerialAlarmTypeStr()) && Arrays.asList(alarmVO.getSerialAlarmTypeStr().split(Constants.COMMA)).contains(alarmVO.getAlarmType())));
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
     * AI/摄像机/雷达告警上报调用：
     * 更新租户版本信息以确保兼容历史版本。
     * @param customers 租户信息列表
     */
    public void updateCustomerVersions(List<OrderCustomerVO> customers) {
        // 当前同一租户同一行业下只会有一个版本-------由于同一租户同一行业版本升级(eg：1.0升级为2.0)，历史存量的配置数据(1.0版本信息)并不会更新，
        // 因此可能上述查询租户信息会出现查询到该租户该行业的历史版本(1.0版本信息)，从而导致查询到旧版本(1.0)的流程，新版本(2.0)流程始终无法正式生效，因此作此版本覆盖，仅作版本覆盖
        List<OrderCustomerVO> versionCustomers = deviceRepository.queryRealNowVersion(customers);
        customers.forEach(vo -> versionCustomers.stream()
                .filter(version -> version.getTenantId().equals(vo.getTenantId()) && version.getIndustryCode().equals(vo.getIndustryCode()))
                .findFirst()
                .ifPresent(version -> vo.setAppVerCode(version.getAppVerCode())));
    }

    /**
     * AI/摄像机/雷达告警上报调用：
     * 填充告警工单的参数，例如工单号、告警时间等。
     * @param receiveAlarmVO 包含告警信息的对象
     */
    public void populateAlarmWorkOrderParams(ReceiveAlarmVO receiveAlarmVO) {
        receiveAlarmVO.setWarningSource(receiveAlarmVO.getAlarmSource());
        receiveAlarmVO.setWarningTypeId(String.valueOf(receiveAlarmVO.getAlarmType()));
        receiveAlarmVO.setEmergencyLevel(EnumUtil.AlarmLevel.GENERALLY.getCode());
        /*if (StringUtils.isEmpty(receiveAlarmVO.getImportanceLevel()) || Constants.PARAM_NULL.equals(receiveAlarmVO.getImportanceLevel())) {
            receiveAlarmVO.setImportanceLevel(Constants.IMPORTANCE_LEVEL_1);
        }*/
        String fileImgUrlSrc = receiveAlarmVO.getOriginalPictureUrl();
        String assistPicUrl = receiveAlarmVO.getAssistPicUrl();
        //将双通道抓图拼接到原图字段中
        if (!StringUtils.isEmpty(assistPicUrl)) {
            String newFileImgUrlSrc = StringUtils.isEmpty(fileImgUrlSrc)
                    ? assistPicUrl
                    : fileImgUrlSrc + Constants.COMMA + assistPicUrl;
            receiveAlarmVO.setOriginalPictureUrl(newFileImgUrlSrc);
        }
        receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        TransparentFieldsDto dto = new TransparentFieldsDto();
        Optional.ofNullable(receiveAlarmVO.getLinkageAlgorithmCode())
                .ifPresent(dto::setLinkageAlgorithmCode);
        Optional.ofNullable(receiveAlarmVO.getModelCode())
                .ifPresent(dto::setModelCode);
        Optional.ofNullable(receiveAlarmVO.getAttribute())
                .ifPresent(dto::setAttribute);
        Optional.ofNullable(receiveAlarmVO.getOriginalPictureUrl())
                .ifPresent(dto::setFileImgUrlSrc);
        Optional.ofNullable(receiveAlarmVO.getAssistPicUrl())
                .ifPresent(dto::setAssistPicUrl);

        //1.8.10 增加摄像机类型、摄像机子类型 放入扩展字段
        Optional.ofNullable(receiveAlarmVO.getDeviceType())
                .ifPresent(dto::setDeviceType);
        Optional.ofNullable(receiveAlarmVO.getDeviceTypeName())
                .ifPresent(dto::setDeviceTypeName);
        Optional.ofNullable(receiveAlarmVO.getDeviceSubType())
                .ifPresent(dto::setDeviceSubType);
        Optional.ofNullable(receiveAlarmVO.getDeviceSubTypeName())
                .ifPresent(dto::setDeviceSubTypeName);

        receiveAlarmVO.setTransparentFieldsDto(dto);
        receiveAlarmVO.setTransparentFields(JSON.toJSONString(dto));
    }
}
