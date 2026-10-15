package com.chinatower.event.core.alarm.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridByLngLat;
import com.chinatower.bdm.api.entity.GridByLngLatParam;
import com.chinatower.bdm.api.entity.GridInfoParam;
import com.chinatower.bdm.api.entity.GridVO;
import com.chinatower.common.core.dto.AlgorithmInfoDTO;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceMgmtService;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.device.entity.vo.TenantIotConfRelVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.event.asyncTask.AsyncVideoCommon;
import com.chinatower.event.constant.AlarmConstants;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.FlowAlarmTemplateDto;
import com.chinatower.event.api.model.OrganInfoReq;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.kafka.KafkaProducer;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.repository.ITransLntlagRepository;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.vo.*;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceLabelInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.IVideoPresetService;
import com.chinatower.video.api.VideoControlAPI;
import com.chinatower.video.api.VideoPlayerAPI;
import com.chinatower.video.api.entity.DoPtzLocationCmdReq;
import com.chinatower.video.api.entity.PlayerSnapShotReq;
import com.chinatower.video.api.entity.VideoDownloadVO;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description 处理告警相关公共方法集合
 * @Author guyuefei
 * @Date 2022/7/18
 * @Version release_v1.5
 **/
@Component("warningOrderUtil")
@Slf4j
public class WarningOrderUtil {

    @Autowired
    private FlowLinkService flowLinkService;
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private IVideoDeviceService videoDeviceService;
    @Autowired
    private VideoControlAPI videoControlAPI;
    @Autowired
    private VideoPlayerAPI videoPlayerAPI;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private ITransLntlagRepository transLntlagRepository;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private IVideoPresetService iVideoPresetService;

    @Autowired
    private AsyncVideoCommon asyncVideoCommon;

    @Autowired
    private SecondRecogConfigMapper secondRecogConfigMapper;

    @Autowired
    private ISysService sysService;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private SysDeptDeviceMapper sysDeptDeviceMapper;

    @Autowired
    private UavAlarmSourceMapper uavAlarmSourceMapper;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private IVideoDeviceMgmtService iVideoDeviceMgmtService;
    /**
     * 根据网格id获取网格信息
     * @param gridId
     * @return
     */
    public GridInfo getGrifInfo(Long gridId){
        try {
            GridInfoParam param = new GridInfoParam();
            param.setGridId(gridId);
            log.info("getGrifInfo-req:{}", JSON.toJSONString(param));
            Result<GridVO> gridVo =gridAPI.findGridInfo(param);
            log.info("getGrifInfo-resp:{}", JSON.toJSONString(gridVo));
            if(ResultCode.SUCCESS.getCode() == gridVo.getCode()){
                GridInfo gridInfo = new GridInfo();
                if(gridInfo != null){
                    BeanUtil.copyProperties(gridVo.getData(),gridInfo);
                }
                return gridInfo;
            }
        }catch (Exception e){
            log.info("根据网格id获取网格信息异常"+e);
        }
        return null;
    }



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
        FlowAlarmTemplateDto flowAlarmTemplateDto = flowLinkService.getFlowTemplate(appVerCoe, industryCode, tenantId, warningSource, alarmType);
        if (null != flowAlarmTemplateDto) {
            //查询流程是否初始环节有研判环节
            VmFlowTemplateLinkDto dto= flowLinkService.getFlowTemplateLink(flowAlarmTemplateDto.getTemplateId(), flowAlarmTemplateDto.getAlarmFlowId(), Long.valueOf(EnumUtil.FlowNode.PENDING_JUDGE.getCode()),null,industryCode,appVerCoe);
            if(dto != null && !StringUtils.isEmpty(dto.getNextLink()) && !dto.getNextLink().contains(",")){
                return dto;
            }
        }
        return null;
    }

    /**
     * 物联设备发生告警处理
     *
     * @return
     * @author chenmf
     * @date 2022/5/9
     * @version
     */
    @Async("taskExecutor")
    public void iotDeviceDeal(ReceiveAlarmDTO receiveAlarmVODB, String deviceCode) {
        log.info("物联设备发生告警处理开始");
        //该设备生成的告警事件
        List<ReceiveAlarmVO> alarms = receiveAlarmVODB.getList().stream().filter(item -> Objects.equals(deviceCode, item.getDeviceCode())).collect(Collectors.toList());
        //该设备生成的运营研判
        List<ReceiveAlarmVO> operations = receiveAlarmVODB.getOperations().stream().filter(item -> Objects.equals(deviceCode, item.getDeviceCode())).collect(Collectors.toList());
        //该设备生成的告警压制
        List<ReceiveAlarmVO> intercepts = receiveAlarmVODB.getIntercepts().stream().filter(item -> Objects.equals(deviceCode, item.getDeviceCode())).collect(Collectors.toList());
        ReceiveAlarmVO receiveAlarmVO = this.getReceiveAlarmVO(alarms, operations, intercepts);
        if (null == receiveAlarmVO || StringUtils.isEmpty(deviceCode)) {
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
        log.info("从字典配置中获取抓图回调地址:{}", imageUploadUrl);

        TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
        qryCond.setDeviceCode(deviceCode);
        qryCond.setTenantId(receiveAlarmVO.getTenantId());
        qryCond.setIndustryCode(receiveAlarmVO.getIndustryCode());
        //查询物联设备联动配置
        Result<TenantIotDeviceConfVO> result = videoDeviceService.getTenantIotDevConf(qryCond);
        if (result.getCode() == ResultCode.SUCCESS.getCode()) {
            TenantIotDeviceConfVO iotDeviceConf = result.getData();
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
                    playerSnapShotReq.setImageUpUrl(imageUploadUrl + receiveAlarmVO.getWarningOrderId());
                    playerSnapShotReq.setIsNeedOfs("1");
                    String relDeviceCode = tenantIotConfRelVO.getDeviceCode();
                    String relChannelCode = tenantIotConfRelVO.getChannelCode();

                    doPtzLocationCmdReq.setDeviceCode(relDeviceCode);
                    doPtzLocationCmdReq.setChannelCode(relChannelCode);

                    String imgName = deviceCode + tenantIotConfRelVO.getChannelCode() + relChannelCode + receiveAlarmVO.getWarningOrderId();
                    playerSnapShotReq.setDeviceCode(relDeviceCode);
                    playerSnapShotReq.setChannelCode(relChannelCode);
                    playerSnapShotReq.setImageName(imgName);

                    for (String linkStr : linkArray) {
                        try {
                            //1:摄像机看向告警位置
                            if (linkStr.equals("1")) {
                                Result doLocationResult = videoControlAPI.doPtzLocationCmd(doPtzLocationCmdReq);
                                log.info("doPtzLocationCmd response:{}", doLocationResult);
                                if (Objects.equals(ResultCode.SUCCESS, doLocationResult.getCode())) {
                                    //调用转动摄像头停止五秒后开始抓图和抓视频
                                    Thread.sleep(5000);
                                }

                            }
                            //2:抓图
                            else if (linkStr.equals("2")) {
                                Result videoResult = videoPlayerAPI.snapShot(playerSnapShotReq);
                                log.info("snapShot response:{}", videoResult);
                            }
                            //3:抓视频
                            else if (linkStr.equals("3")) {
                                // 联动抓视频 组装参数
                                VideoDownloadVO videoDownloadVO = new VideoDownloadVO();
                                videoDownloadVO.setDeviceCode(relDeviceCode);
                                videoDownloadVO.setChannelCode(relChannelCode);
                                videoDownloadVO.setTableType("2");
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
    }


    /**
     * 获取租户信息
     * @param alarms
     * @param operations
     * @param intercepts
     * @return
     */
    private ReceiveAlarmVO getReceiveAlarmVO(List<ReceiveAlarmVO> alarms, List<ReceiveAlarmVO> operations, List<ReceiveAlarmVO> intercepts) {
        if  (!CollectionUtils.isEmpty(alarms)) {
            return alarms.get(0);
        }
        if  (!CollectionUtils.isEmpty(operations)) {
            return operations.get(0);
        }
        if  (!CollectionUtils.isEmpty(intercepts)) {
            return intercepts.get(0);
        }
        return null;
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
     * 是否开启运营研判并且当前告警类型是否符合配置
     */
    public boolean getIsOpenOperation(ReceiveAlarmVO alarmVO){
        return !Constants.VALID_STATE.equals(alarmVO.getIsOperation()) && !StringUtils.isEmpty(alarmVO.getOperationAlarmSource()) &&
                ( (alarmVO.getOperationAlarmSource().contains(Constants.FIRST_LINK)  && Constants.OPERATION_ALARM_SOURCE.contains(alarmVO.getAlarmSource())) ||
                        (!StringUtils.isEmpty(alarmVO.getParallelAlarmTypeStr()) && Arrays.asList(alarmVO.getParallelAlarmTypeStr().split(Constants.COMMA)).contains(alarmVO.getAlarmType())) ||
                        (!StringUtils.isEmpty(alarmVO.getSerialAlarmTypeStr()) && Arrays.asList(alarmVO.getSerialAlarmTypeStr().split(Constants.COMMA)).contains(alarmVO.getAlarmType())));
    }

    /**
     * 是否为并行研判
     */
    public boolean getIsParallelOperation(ReceiveAlarmVO alarmVO){
        return (alarmVO.getOperationAlarmSource().contains(Constants.FIRST_LINK) && alarmVO.getOperationType().contains(Constants.TWO))
                || (!StringUtils.isEmpty(alarmVO.getParallelAlarmTypeStr()) && Arrays.asList(alarmVO.getParallelAlarmTypeStr().split(Constants.COMMA)).contains(alarmVO.getAlarmType()));
    }

    public void addImageMap(String imgUrl,List<Map> imgs){
        String[] imgUrls = imgUrl.split(Constants.COMMA);
        if (imgUrls.length > 0) {
            for (String img : imgUrls) {
                HashMap imgMap = new HashMap();
                imgMap.put(Constants.PARAM_FILE_URL, img);
                if(!imgs.contains(imgMap)){
                    imgs.add(imgMap);
                }
            }
        }
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
     * @param deviceSiteVO
     * @param receiveAlarmVO
     */
    public void setAlarmLocation(DeviceSiteVO deviceSiteVO, ReceiveAlarmVO receiveAlarmVO) {
        if (null != deviceSiteVO) {
            // 获取摄像头区域
            receiveAlarmVO.setProvinceCode(deviceSiteVO.getProvinceId());
            receiveAlarmVO.setCityCode(deviceSiteVO.getCityId());
            receiveAlarmVO.setCountyCode(deviceSiteVO.getCountyId());
            receiveAlarmVO.setProvinceName(deviceSiteVO.getProvinceName());
            receiveAlarmVO.setCityName(deviceSiteVO.getCityName());
            receiveAlarmVO.setCountyName(deviceSiteVO.getCountyName());
            //仅摄像机告警和AI告警才可开启告警定位功能
            if("1".equals(deviceSiteVO.getAlarmLocation()) && (Constants.ONE.equals(receiveAlarmVO.getAlarmSource()) || Constants.FOUR.equals(receiveAlarmVO.getAlarmSource()) || "11".equals(receiveAlarmVO.getAlarmSource()))){
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
                log.info("setAlarmGridInfo-getMessageByLngLatNew-req:{}",JSON.toJSONString(param));
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

    /**
     * 无人机告警来源详情
     * @param orderInfoVO 无人机告警信息
     * @param UavIdEnum   无人机厂商
     * @param UavNestIdEnum 机巢厂商
     */
    public void uavAlarmSource(OrderInfoVO orderInfoVO,AtomicReference<List<SysDictDataResponse>> UavIdEnum,AtomicReference<List<SysDictDataResponse>> UavNestIdEnum){
        if (AlarmConstants.ALARM_SOURCE_12.equals(orderInfoVO.getWarningSource()) && !StringUtils.isEmpty(orderInfoVO.getDeviceCode())){
            UavAlarmSourceVo uavAlarmSourceVo = uavAlarmSourceMapper.findUavAlarmSourceByDeviceCode(orderInfoVO.getDeviceCode());
            String uavDeviceAlias = uavAlarmSourceMapper.getUavDevcieAlias(orderInfoVO);
            if (!StringUtils.isEmpty(uavAlarmSourceVo)){
                if (!CollectionUtils.isEmpty(UavIdEnum.get())) {
                    UavIdEnum.get().forEach(item -> {
                        if (Objects.equals(uavAlarmSourceVo.getManufacturerCode(), item.getDictValue())) {
                            uavAlarmSourceVo.setModelFactoryName(item.getDictLabel());
                        }
                    });
                }
                if (!StringUtils.isEmpty(uavDeviceAlias)) {
                    uavAlarmSourceVo.setDeviceName(uavDeviceAlias);
                }
                List<UavNestDeviceVo> uavNestDeviceList = uavAlarmSourceVo.getUavNestDeviceList();
                if (uavNestDeviceList.isEmpty()){
                    // 初始化一个新的 UavNestDeviceVo 对象并设置字段为空字符串
                    UavNestDeviceVo uavNestDeviceVo = new UavNestDeviceVo();
                    uavNestDeviceVo.setUavNestCode("");
                    uavNestDeviceVo.setUavNestName("");
                    uavNestDeviceVo.setModelCode("");
                    uavNestDeviceVo.setModelCodeName("");
                    uavNestDeviceVo.setDeviceModel("");
                    uavNestDeviceVo.setUaLongitude("");
                    uavNestDeviceVo.setUaLatitude("");
                    // 将默认对象添加到列表中
                    uavNestDeviceList = new ArrayList<>();
                    uavNestDeviceList.add(uavNestDeviceVo);
                    uavAlarmSourceVo.setUavNestDeviceList(uavNestDeviceList);
                }
                for (UavNestDeviceVo uavNestDeviceVo : uavNestDeviceList) {
                    if (!CollectionUtils.isEmpty(UavNestIdEnum.get())) {
                        UavNestIdEnum.get().forEach(item -> {
                            if (Objects.equals(uavNestDeviceVo.getModelCode(), item.getDictValue())) {
                                uavNestDeviceVo.setModelCodeName(item.getDictLabel());
                            }
                        });
                    }
                }
                uavAlarmSourceVo.setUavNestDeviceList(uavNestDeviceList);
                orderInfoVO.setUavDevInfoList(uavAlarmSourceVo);
            }
        }
    }

    /**
     * 处理第三方告警中真实告警
     *
      * @param orderInfoVO 物联告警信息
     */
    public void handleRealAlarmInThirdParty(OrderInfoVO orderInfoVO) {
        try {
            //判断是否是 第三方告警
            if (AlarmConstants.ALARM_SOURCE_11.equals(orderInfoVO.getWarningSource())) {
                orderInfoVO.setRealAlarmSource(AlarmConstants.ALARM_SOURCE_1);
                if (!StringUtils.isEmpty(orderInfoVO.getExtraField())){
                    log.info("getOrderInfo-handleRealAlarmInThirdParty处理第三方告警中真实告警入参：{}",JSON.toJSONString(orderInfoVO));
                    String extraField = orderInfoVO.getExtraField();
                    String realAlarmSource = "";
                    String keeperName = "";
                    String presetName = "";
                    //摄像机标签
                    List<String> deviceLabel = new ArrayList<>();
                    Object object = JSON.parse(extraField);
                    if (object instanceof  JSONObject){
                        realAlarmSource = JSON.parseObject(extraField).getString(Constants.PARAM_REAL_WARNING_SOURCE);
                        keeperName = JSON.parseObject(extraField).getString(Constants.KEEPER_NAME);
                        presetName = JSON.parseObject(extraField).getString(Constants.PRESET_NAME);
                        String deviceLabelStr = JSON.parseObject(extraField).getString(Constants.PARAM_DEVICE_LABEL);
                        JSONArray labelJsonArray = JSON.parseArray(deviceLabelStr);
                        if (!CollectionUtils.isEmpty(labelJsonArray)){
                            deviceLabel = labelJsonArray.toJavaList(String.class);
                            orderInfoVO.setDeviceLabel(deviceLabel);
                        }
                    }else if (object instanceof JSONArray){
                        realAlarmSource = JSON.parseArray(extraField).getJSONObject(0).getString(Constants.PARAM_REAL_WARNING_SOURCE);
                        keeperName = JSON.parseArray(extraField).getJSONObject(0).getString(Constants.KEEPER_NAME);
                        presetName = JSON.parseArray(extraField).getJSONObject(0).getString(Constants.PRESET_NAME);
                        List<Map> polygonOperate = new ArrayList<>();
                        JSONArray jsonArray = JSON.parseArray(extraField).getJSONObject(0).getJSONArray(Constants.PARAM_POLYGON_OPERATE);
                        if (!CollectionUtils.isEmpty(jsonArray)){
                            polygonOperate = jsonArray.toJavaList(Map.class);
                            orderInfoVO.setPolygonOperate(polygonOperate);
                        }
                        JSONArray labelJsonArray = JSON.parseArray(extraField).getJSONObject(0).getJSONArray(Constants.PARAM_DEVICE_LABEL);
                        if (!CollectionUtils.isEmpty(labelJsonArray)){
                            deviceLabel = labelJsonArray.toJavaList(String.class);
                            orderInfoVO.setDeviceLabel(deviceLabel);
                        }

                    }
                    if (!StringUtils.isEmpty(realAlarmSource)){
                        orderInfoVO.setRealAlarmSource(realAlarmSource);
                    }
                        orderInfoVO.setKeeperName(keeperName);
                        orderInfoVO.setPresetName(presetName);
                    //判断是否有第三方告警
                    if (StringUtil.isNotEmpty(realAlarmSource) && !AlarmConstants.ALARM_SOURCE_1.equals(realAlarmSource) && !AlarmConstants.ALARM_SOURCE_4.equals(realAlarmSource)){
                        //如果真实告警为无人机告警
                        if (AlarmConstants.ALARM_SOURCE_12.equals(realAlarmSource)){
                            UavAlarmSourceVo uavDevInfoList = new UavAlarmSourceVo();
                            JSONObject uavJSONObject = JSON.parseObject(extraField).getJSONObject(Constants.PARAM_UAV_DEV_INFO_LIST);
                            if (!CollectionUtils.isEmpty(uavJSONObject)){
                                uavDevInfoList = uavJSONObject.toJavaObject(UavAlarmSourceVo.class);
                                orderInfoVO.setUavDevInfoList(uavDevInfoList);
                            }
                        }
                        //如果真实告警为物联告警
                        if (Constants.EIGHT.equals(realAlarmSource)){
                            List<IotDeviceInfoVO> iotDeviceInfoList = new ArrayList<>();
                            JSONArray jsonArray = JSON.parseObject(extraField).getJSONArray(Constants.PARAM_IOT_DEV_INFO_LIST);
                            if (!CollectionUtils.isEmpty(jsonArray)){
                                iotDeviceInfoList = jsonArray.toJavaList(IotDeviceInfoVO.class);
                                orderInfoVO.setIotDevInfoList(iotDeviceInfoList);
                            }
                        }
                        //如果真实告警为卫星告警
                        if (Constants.NINE.equals(realAlarmSource)){
                            List<SatelliteRelDeviceVo> satelliteDeviceList = new ArrayList<>();
                            ReceiveRegionAlarmDto spot = new ReceiveRegionAlarmDto();
                            JSONArray jsonArray = JSON.parseObject(extraField).getJSONArray(Constants.PARAM_SATELLITE_DEVICE_LIST);
                            JSONObject spotJSONObject = JSON.parseObject(extraField).getJSONObject(Constants.PARAM_SPOT);
                            if (!CollectionUtils.isEmpty(jsonArray)){
                                satelliteDeviceList = jsonArray.toJavaList(SatelliteRelDeviceVo.class);
                                orderInfoVO.setSatelliteDeviceList(satelliteDeviceList);
                            }
                            if (!CollectionUtils.isEmpty(spotJSONObject)){
                                spot = spotJSONObject.toJavaObject(ReceiveRegionAlarmDto.class);
                                orderInfoVO.setSpot(spot);
                            }

                        }
                        //如果真实告警为雷达告警
                        if (Constants.TEN.equals(realAlarmSource)){
                            SatelliteRelDeviceVo radarDevice = new SatelliteRelDeviceVo();
                            List<DeviceSiteInfoVO> deviceSiteList = new ArrayList<>();
                            JSONObject radarJSONObject = JSON.parseObject(extraField).getJSONObject(Constants.PARAM_RADAR_DEVICE);
                            JSONArray relDeviceJsonArray = JSON.parseObject(extraField).getJSONArray(Constants.PARAM_DEVICE_SITE_LIST);
                            if (!CollectionUtils.isEmpty(radarJSONObject)){
                                radarDevice = radarJSONObject.toJavaObject(SatelliteRelDeviceVo.class);
                                orderInfoVO.setRadarDevice(radarDevice);
                            }
                            if (!CollectionUtils.isEmpty(relDeviceJsonArray)){
                                deviceSiteList = relDeviceJsonArray.toJavaList(DeviceSiteInfoVO.class);
                                orderInfoVO.setDeviceSiteList(deviceSiteList);
                            }

                        }
                    }
                }
            }
        }catch (Exception e){
            log.info("handleRealAlarmInThirdParty处理第三方失败{}",e);
        }

    }

    //处理摄像机标签
    public void handleDeviceLabel(OrderInfoVO orderInfoVO) {
        try {
            log.info("handleDeviceLabel-入参{}",JSON.toJSONString(orderInfoVO));
            List<String> deviceCodeList = new ArrayList<>();
                String warningSource = orderInfoVO.getWarningSource();
                //AI，一键，摄像机 直接取deviceCode
                if (AlarmConstants.ALARM_SOURCE_1.equals(warningSource) || AlarmConstants.ALARM_SOURCE_3.equals(warningSource) || AlarmConstants.ALARM_SOURCE_4.equals(warningSource)){
                    String deviceCode = orderInfoVO.getDeviceCode();
                    deviceCodeList.add(deviceCode);
                }else if (AlarmConstants.ALARM_SOURCE_8.equals(warningSource)){
                    //物联告警
                    for (IotDeviceInfoVO iotDeviceInfoVO : orderInfoVO.getIotDevInfoList()) {
                        if (!CollectionUtils.isEmpty(iotDeviceInfoVO.getIotDeviceList())){
                            deviceCodeList.addAll(iotDeviceInfoVO.getIotDeviceList().stream().map(v -> v.getDeviceCode()).distinct().collect(Collectors.toList()));
                        }
                    }
                }else if (AlarmConstants.ALARM_SOURCE_9.equals(warningSource)){
                    //卫星告警
                    if (ObjectUtil.isNotNull(orderInfoVO.getSatelliteDeviceList())){
                        deviceCodeList.addAll(orderInfoVO.getSatelliteDeviceList().stream().map(v -> v.getDeviceCode()).distinct().collect(Collectors.toList()));
                    }
                }else if (AlarmConstants.ALARM_SOURCE_10.equals(warningSource)){
                    //雷达告警
                    if (ObjectUtil.isNotNull(orderInfoVO.getRadarDevice())){
                        deviceCodeList.add(orderInfoVO.getRadarDevice().getDeviceCode());
                    }
                }
            //查询摄像机标签并匹配赋值
            selectAndSetDeviceLabel(deviceCodeList,orderInfoVO);
        }catch (Exception e){
            log.info("handleDeviceLabel处理摄像机标签失败{}",e);
        }
    }


    //查询摄像机标签并匹配赋值
    public void selectAndSetDeviceLabel(List<String> deviceCodeList,OrderInfoVO orderInfoVO) {
        if (!CollectionUtils.isEmpty(deviceCodeList)){
            DeviceLabelReq deviceLabelReq = new DeviceLabelReq();
            deviceLabelReq.setDeviceCodeList(deviceCodeList);
            deviceLabelReq.setTenantId(orderInfoVO.getTenantId());
            if(StringUtils.isEmpty(orderInfoVO.getTradeId())){
                deviceLabelReq.setIndustryCode(orderInfoVO.getIndustryCode());
            }else {
                deviceLabelReq.setIndustryCode(orderInfoVO.getTradeId());
            }

            log.info("selectAndSetDeviceLabel-调用queryDeviceLabelInfoByCondition入参{}",JSON.toJSONString(deviceLabelReq));
            Result<List<DeviceLabelInfoVO>> result = iVideoDeviceMgmtService.queryDeviceLabelInfoByCondition(deviceLabelReq);
            log.info("selectAndSetDeviceLabel-调用queryDeviceLabelInfoByCondition返回结果{}",JSON.toJSONString(result));
            if (result != null && result.getCode() == 200 && result.getData() != null){
                List<String> labelList = new ArrayList<>();
                List<DeviceLabelInfoVO> data = result.getData();
                    String warningSource = orderInfoVO.getWarningSource();
                    //AI，一键，摄像机 直接取deviceCode
                    if (AlarmConstants.ALARM_SOURCE_1.equals(warningSource) || AlarmConstants.ALARM_SOURCE_3.equals(warningSource) || AlarmConstants.ALARM_SOURCE_4.equals(warningSource)){
                        labelList = data.get(0).getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                        orderInfoVO.setDeviceLabel(labelList);
                    }else if (AlarmConstants.ALARM_SOURCE_8.equals(warningSource)){
                        //物联告警取联动摄像机信息
                        for (IotDeviceInfoVO iotDeviceInfoVO : orderInfoVO.getIotDevInfoList()) {
                            if (!CollectionUtils.isEmpty(iotDeviceInfoVO.getIotDeviceList())){
                                for (IotRelDeviceVo deviceInfoVO : iotDeviceInfoVO.getIotDeviceList()) {
                                    for (DeviceLabelInfoVO vo : data) {
                                        if (vo.getDeviceCode().equals(deviceInfoVO.getDeviceCode())){
                                            labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                                            deviceInfoVO.setDeviceLabel(labelList);
                                        }
                                    }
                                }
                            }
                        }
                    }else if (AlarmConstants.ALARM_SOURCE_9.equals(warningSource)){
                        for (SatelliteRelDeviceVo satelliteRelDeviceVo : orderInfoVO.getSatelliteDeviceList()) {
                            for (DeviceLabelInfoVO vo : data) {
                                if (vo.getDeviceCode().equals(satelliteRelDeviceVo.getDeviceCode())){
                                    labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                                    satelliteRelDeviceVo.setDeviceLabel(labelList);
                                }
                            }
                        }
                    }else if (AlarmConstants.ALARM_SOURCE_10.equals(warningSource)){
                        for (DeviceLabelInfoVO vo : data) {
                            if (vo.getDeviceCode().equals(orderInfoVO.getRadarDevice().getDeviceCode())){
                                labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                                orderInfoVO.getRadarDevice().setDeviceLabel(labelList);
                            }
                        }
                    }
            }
        }
    }

    /**
     * 返回物联告警对应的多组物联设备信息、以及每组物联设备对应的多组联动摄像机信息
     *
     * 扩展字段extraField格式：
     * {"deviceInfo":[{"iotDeviceCode":"jtwind-uyld-3899-04","relDevice":[{"deviceCode":"11010800831327000008","isSecondIdentify":"1","secondIdentifyResult":"1","linkageAlgorithm":"ZGTT-1212102001"},{"deviceCode":"51150200831327000261","isSecondIdentify":"1","secondIdentifyResult":"0","linkageAlgorithm":"ZGTT-1010100001"}]},
     * {"iotDeviceCode":"rain-frgd-9596-01","relDevice":[{"deviceCode":"11010800831327000008","isSecondIdentify":"0"}]}],"monitors":[{.....}]}
     *
     * @param orderInfoVO 物联告警信息
     * @param iotModelIdEnum 物联设备厂商
     * @param modelIdEnum 摄像机厂商
     */
    public void buildMultIotDeviceInfo(OrderInfoVO orderInfoVO, AtomicReference<List<SysDictDataResponse>> iotModelIdEnum,
                                       AtomicReference<List<SysDictDataResponse>> modelIdEnum) {
        List<IotDeviceInfoVO> iotDeviceInfoList = new ArrayList<>();
        if (AlarmConstants.ALARM_SOURCE_8.equals(orderInfoVO.getWarningSource()) && !StringUtils.isEmpty(orderInfoVO.getDeviceCode())) {
            // 处理扩展字段，兼容历史数据
            String extraField = buildExtraField(orderInfoVO);

            List<DeviceInfoExtendFieldVo> deviceInfoList = JSON.parseObject(extraField).getJSONArray(Constants.PARAM_DEVICEINFO).toJavaList(DeviceInfoExtendFieldVo.class);
            log.info("getOrderInfo-buildMultIotDeviceInfo获取物联设备以及联动摄像机信息：{}", JSON.toJSONString(deviceInfoList));
            if (deviceInfoList != null && !deviceInfoList.isEmpty()) {
                // 获取所有物联设备编码
                List<String> iotDeviceCodes = deviceInfoList.stream().map(DeviceInfoExtendFieldVo::getIotDeviceCode).collect(Collectors.toList());
                // 批量查询物联设备信息
                List<DeviceSiteVO> deviceSiteVOS = deviceRepository.queryIotDeviceList(iotDeviceCodes);
                Map<String, DeviceSiteVO> deviceSiteMap = deviceSiteVOS.stream().collect(Collectors.toMap(DeviceSiteVO::getDeviceCode, device -> device));

                // 批量查询物联设备关联摄像机信息
                Map<String, List<TenantIotConfRelVO>> deviceConfMap = getConfRelsForDevice(iotDeviceCodes, orderInfoVO);

                // 批量查询设备收藏表
                List<String> allDeviceCodes = deviceConfMap.values().stream()
                        .flatMap(List::stream)
                        .map(TenantIotConfRelVO::getDeviceCode)
                        .collect(Collectors.toList());
                Map<String, Boolean> deviceCollectMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(allDeviceCodes)) {
                    deviceCollectMap = deviceMapper.getDeviceCodeCollectByDeviceCodes(allDeviceCodes, "5", String.valueOf(sysService.getLoginUserId()))
                            .stream()
                            .collect(Collectors.toMap(code -> code, code -> true));
                }
                // 批量查询联动算法名称
                List<String> linkageAlgorithms = deviceInfoList.stream()
                        .map(deviceInfo -> Optional.ofNullable(deviceInfo.getRelDevice()).orElse(Collections.emptyList()))
                        .flatMap(Collection::stream)
                        .map(IotRelDeviceVo::getLinkageAlgorithm)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()); //提取出所有的联动算法编码
                Map<String, String> algorithmCodeToNameMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(linkageAlgorithms)) {
                    List<AlgorithmInfoDTO> algorithmNameList = deviceMapper.getAlgorithmNameList(linkageAlgorithms);
                    algorithmCodeToNameMap = algorithmNameList.stream()
                            .collect(Collectors.toMap(AlgorithmInfoDTO::getAlgorithmCode, AlgorithmInfoDTO::getAlgorithmName));
                }
                for (DeviceInfoExtendFieldVo deviceInfoExtendFieldVo : deviceInfoList) {
                    // 处理每个物联设备
                    String iotDeviceCode = deviceInfoExtendFieldVo.getIotDeviceCode();
                    DeviceSiteVO deviceSiteVO = deviceSiteMap.get(iotDeviceCode);
                    if (deviceSiteVO != null) {
                        IotDeviceInfoVO iotDeviceInfoVO = new IotDeviceInfoVO();
                        iotDeviceInfoVO.setDeviceCode(deviceSiteVO.getDeviceCode());
                        iotDeviceInfoVO.setDeviceName(deviceSiteVO.getDeviceName());
                        iotDeviceInfoVO.setDeviceAddress(deviceSiteVO.getLocation());
                        iotDeviceInfoVO.setDeviceTypeName(deviceSiteVO.getDeviceTypeName());
                        iotDeviceInfoVO.setModelId(deviceSiteVO.getModelId());
                        iotDeviceInfoVO.setHeight(deviceSiteVO.getHeight());
                        iotDeviceInfoVO.setVideoLatitude(deviceSiteVO.getLatitude());
                        iotDeviceInfoVO.setVideoLongitude(deviceSiteVO.getLongitude());
                        iotDeviceInfoVO.setDeviceLongitude(deviceSiteVO.getLongitude());
                        iotDeviceInfoVO.setDeviceLatitude(deviceSiteVO.getLatitude());
                        iotDeviceInfoVO.setDeviceModelCode(deviceSiteVO.getDeviceModelCode());
                        iotDeviceInfoVO.setFocusRange(deviceSiteVO.getFocusRange());
                        iotDeviceInfoVO.setVertiDownNeg(deviceSiteVO.getVertiDownNeg());
                        if (!CollectionUtils.isEmpty(iotModelIdEnum.get())) {  //物联设备厂商
                            iotModelIdEnum.get().forEach(item -> {
                                if (Objects.equals(iotDeviceInfoVO.getModelId(), item.getDictValue())) {
                                    iotDeviceInfoVO.setModelFactoryName(item.getDictLabel());
                                }
                            });
                        }

                        List<IotRelDeviceVo> iotRelDeviceList = new ArrayList<>();
                        List<TenantIotConfRelVO> iotDeviceConf = deviceConfMap.getOrDefault(iotDeviceCode, Collections.emptyList());
                        // 遍历设备信息查询关联摄像机信息
                        for (TenantIotConfRelVO confRelVO : iotDeviceConf) {
                            IotRelDeviceVo iotRelDevice = new IotRelDeviceVo();
                            BeanUtil.copyProperties(confRelVO,iotRelDevice);

                            //摄像机厂商
                            if (!CollectionUtils.isEmpty(modelIdEnum.get())) {
                                modelIdEnum.get().forEach(item -> {
                                    if (Objects.equals(confRelVO.getModelCode(), item.getDictValue())) {
                                        iotRelDevice.setModelCodeName(item.getDictLabel());
                                    }
                                });
                            }
                            // 设备是否收藏标识（0-否、1-是）
                            iotRelDevice.setDeviceCodeCollect(deviceCollectMap.containsKey(confRelVO.getDeviceCode()) ? Constants.ONE : Constants.ZERO);

                            // 获取和设置额外的设备信息
                            Map<String, String> finalAlgorithmCodeToNameMap = algorithmCodeToNameMap;
                            if(deviceInfoExtendFieldVo.getRelDevice() != null){
                                deviceInfoExtendFieldVo.getRelDevice().stream()
                                        .filter(relDeviceVo -> confRelVO.getDeviceCode().equals(relDeviceVo.getDeviceCode()))
                                        .findFirst()
                                        .ifPresent(relDeviceVo -> {
                                            iotRelDevice.setIsSecondIdentify(relDeviceVo.getIsSecondIdentify());
                                            iotRelDevice.setSecondIdentifyResult(relDeviceVo.getSecondIdentifyResult());
                                            iotRelDevice.setLinkageAlgorithm(MapUtils.isNotEmpty(finalAlgorithmCodeToNameMap) ? finalAlgorithmCodeToNameMap.get(relDeviceVo.getLinkageAlgorithm()) : "");
                                        });
                            }
                            iotRelDeviceList.add(iotRelDevice);
                        }
                        iotDeviceInfoVO.setIotDeviceList(iotRelDeviceList);
                        iotDeviceInfoList.add(iotDeviceInfoVO);
                    }
                }
            }
            orderInfoVO.setIotDevInfoList(iotDeviceInfoList);
        }
    }

    /**
     * 兼容历史数据，构建扩展字段中deviceInfo的内容
     * @param orderInfoVO
     * @return
     */
    private String buildExtraField(OrderInfoVO orderInfoVO) {
        JSONObject jsonObject = JSON.parseObject(orderInfoVO.getExtraField());
        JSONArray monitorsArray = jsonObject.getJSONArray(Constants.PARAM_MONITORS);
        JSONArray deviceInfoArray = new JSONArray();
        JSONArray monitorsListArray = new JSONArray();
        JSONObject newJsonObject = new JSONObject();
        //兼容deviceInfo
        if (jsonObject.containsKey(Constants.PARAM_DEVICEINFO)) {
            deviceInfoArray = jsonObject.getJSONArray(Constants.PARAM_DEVICEINFO);
        }else {
            JSONObject deviceInfo = new JSONObject();
            deviceInfo.put("iotDeviceCode", orderInfoVO.getDeviceCode()); //物联设备编号

            //联动摄像机信息
            JSONArray relDeviceArray = buildRelDeviceArray(orderInfoVO);
            deviceInfo.put(Constants.PARAM_RELDEVICE, relDeviceArray);
            deviceInfoArray.add(deviceInfo);
        }
        //兼容monitorsList
        if (jsonObject.containsKey(Constants.PARAM_MONITORS_LIST)) {
            monitorsListArray = jsonObject.getJSONArray(Constants.PARAM_MONITORS_LIST);
        }else {
            List<WarningAllSensorMonitorVO> monitorsList = new ArrayList<>();
            List<WarningSensorMonitorVO> monitorVOS = monitorsArray.toJavaList(WarningSensorMonitorVO.class);
            List<DeviceInfoExtendFieldVo> deviceInfoList = deviceInfoArray.toJavaList(DeviceInfoExtendFieldVo.class);
            List<String> iotDeviceCodes = deviceInfoList.stream().map(DeviceInfoExtendFieldVo::getIotDeviceCode).collect(Collectors.toList());
            for (String deviceCode : iotDeviceCodes) {
                WarningAllSensorMonitorVO monitors = new WarningAllSensorMonitorVO();
                monitors.setDeviceCode(deviceCode);
                monitors.setMonitorTime(orderInfoVO.getAlarmTime());
                List<WarningAllSensorMonitorVO.IotFactor> iotFactorList = new ArrayList<>();
                for (WarningSensorMonitorVO monitorVO : monitorVOS) {
                    WarningAllSensorMonitorVO.IotFactor iotFactor = new WarningAllSensorMonitorVO.IotFactor();
                    iotFactor.setFactorCode(monitorVO.getMonitorCode());
                    iotFactor.setFactorName(monitorVO.getMonitorName());
                    iotFactor.setFactorValue(monitorVO.getMonitorValue());
                    iotFactor.setHighlight(true);
                    OneFactoryVO factoryVO = monitorVO.getNewMonitors().stream().filter(v -> "监测指标".equals(v.getItemName())).findFirst().orElse(null);
                    if (ObjectUtil.isNotNull(factoryVO) && StringUtil.isNotEmpty(factoryVO.getItemCode())){
                        String unit = this.getBracketContent(factoryVO.getItemCode());
                        iotFactor.setFactorUnit(unit);
                    }
                    iotFactorList.add(iotFactor);
                }
                monitors.setMonitorList(iotFactorList);
                monitorsList.add(monitors);
            }
            monitorsListArray = JSON.parseArray(JSON.toJSONString(monitorsList));
        }
        newJsonObject.put(Constants.PARAM_MONITORS_LIST, monitorsListArray);
        newJsonObject.put(Constants.PARAM_DEVICEINFO, deviceInfoArray);
        newJsonObject.put(Constants.PARAM_MONITORS, monitorsArray);
        orderInfoVO.setExtraField(newJsonObject.toJSONString());
        log.info("构建扩展字段中deviceInfo的内容：extraField = {}", newJsonObject.toJSONString());
        return newJsonObject.toJSONString();
    }

    private String getBracketContent(String name){
        // 使用正则表达式找到所有的括号
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(name);

        // 存储匹配到的内容
        String lastMatch = null;

        // 遍历所有匹配
        while (matcher.find()) {
            lastMatch = matcher.group(1);
        }

        // 输出结果
        if (lastMatch != null) {
            return lastMatch;
        }
        return "";
    }
    /**
     * 构建联动的摄像机信息
     * @param orderInfoVO
     * @return
     */
    private JSONArray buildRelDeviceArray(OrderInfoVO orderInfoVO) {
        JSONArray relDeviceArray = new JSONArray();

        //查询物联设备关联摄像机信息
        TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
        qryCond.setDeviceCode(orderInfoVO.getDeviceCode());
        qryCond.setTenantId(orderInfoVO.getTenantId());
        qryCond.setIndustryCode(orderInfoVO.getTradeId());
        try {
            Result<TenantIotDeviceConfVO> result = videoDeviceService.getTenantIotDevConf(qryCond);
            if (result.getCode() == ResultCode.SUCCESS.getCode()) {
                List<TenantIotConfRelVO> iotDeviceConf = result.getData().getConfRels();
                if (iotDeviceConf != null && !iotDeviceConf.isEmpty()) {
                    boolean isFirst = true;
                    for (TenantIotConfRelVO deviceSiteVO : iotDeviceConf) {
                        JSONObject relDevice = new JSONObject();
                        relDevice.put(Constants.PARAM_DEVICE_CODE, deviceSiteVO.getDeviceCode());
                        // 将二次识别结果放在第一个摄像机中
                        if (isFirst) {
                            relDevice.put("isSecondIdentify", orderInfoVO.getIsSecondIdentify());
                            relDevice.put("secondIdentifyResult", orderInfoVO.getSecondIdentifyResult());
                            relDevice.put("linkageAlgorithm", orderInfoVO.getLinkageAlgorithm());
                            isFirst = false;
                        }
                        relDeviceArray.add(relDevice);
                    }
                }
            }
        } catch(Exception e){
            log.error("物联设备关联摄像机信息查询异常，工单号：{}，异常：{}", orderInfoVO.getOrderId(), e);
        }
        return relDeviceArray;
    }

    // 提取查询关联摄像机信息的逻辑
    private Map<String, List<TenantIotConfRelVO>> getConfRelsForDevice(List<String> deviceCodeList, OrderInfoVO orderInfoVO) {
        TenantIotDeviceConfVO qryCond = new TenantIotDeviceConfVO();
        qryCond.setDeviceCodeList(deviceCodeList);
        qryCond.setTenantId(orderInfoVO.getTenantId());
        qryCond.setIndustryCode(orderInfoVO.getTradeId());
        try {
            Result<List<TenantIotDeviceConfVO>> result = videoDeviceService.getTenantIotDevConfBatch(qryCond);
            if (result.getCode() == ResultCode.SUCCESS.getCode() && Objects.nonNull(result.getData())) {
                List<TenantIotDeviceConfVO> mapList = result.getData();
                Map<String, List<TenantIotConfRelVO>> deviceCodeToConfRelsMap = mapList.stream()
                        .collect(Collectors.toMap(
                                TenantIotDeviceConfVO::getDeviceCode,   // key 是 deviceCode物联设备编号
                                TenantIotDeviceConfVO::getConfRels      // value 是 confRels联动的摄像机信息
                        ));
                return deviceCodeToConfRelsMap;
            }
        } catch (Exception e) {
            log.error("物联设备关联摄像机信息查询异常，工单号：{}，异常：{}", orderInfoVO.getOrderId(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * 获取所属组织（直属组织）
     * 1、1-AI、3-一键、4-摄像机、8-物联告警、12-无人机展示设备对应组织的所属组织
     * 2、9-卫星、10-雷达告警展示省市区对应组织的所属组织
     * 3、2-人工上报展示上报人对应的组织
     * 4、11-第三方告警暂不展示组织
     * 注：领导批示列表历史人工上报告警不兼容，不展示所属组织
     *    所属组织为空，列表显示-
     * @param paramList
     */
    public List<Map<String, String>> getOrgInfoList(List<OrganInfoReq> paramList) {
        List<Map<String, String>> resultList = new ArrayList<>();
        try {
            if (!CollectionUtils.isEmpty(paramList)) {
                Map<String, Object> paramMap = new HashMap<>();
                List<String> tenantIdList= paramList.stream().filter(item -> !StringUtils.isEmpty(item.getTenantId()))
                        .map(item -> item.getTenantId()).distinct().collect(Collectors.toList());
                List<String> industryCodeList= paramList.stream().filter(item -> !StringUtils.isEmpty(item.getIndustryCode()))
                        .map(item -> item.getIndustryCode()).distinct().collect(Collectors.toList());
                paramMap.put("tenantIdList", tenantIdList);
                paramMap.put("industryCodeList", industryCodeList);
                List<String> deviceCodeList= paramList.stream().filter(item -> !StringUtils.isEmpty(item.getDeviceCode()))
                        .map(item -> item.getDeviceCode()).distinct().collect(Collectors.toList());
                List<String> regionCodeList = paramList.stream()
                        .map(item -> item.getProvinceId() + Constants.SEP + item.getCityId() + Constants.SEP + item.getCountyId())
                        .collect(Collectors.toList());
                List<String> userNameList = paramList.stream().filter(item -> !StringUtils.isEmpty(item.getFindName()))
                        .map(item -> item.getFindName()).distinct().collect(Collectors.toList());
                log.info("getOrgInfoList查询入参，设备编号：{}, 省市区编码：{}, 用户名：{}", JSON.toJSONString(deviceCodeList), JSON.toJSONString(regionCodeList), JSON.toJSONString(userNameList));

                List<Map<String, String>> camerasOrganList = null;
                List<Map<String, String>> sensorsOrganList = null;
                List<Map<String, String>> satellitesOrganList = null;
                List<Map<String, String>> raderOrganList = null;
                List<Map<String, String>> uavOrganList = null;
                List<Map<String, String>> userOrganList = null;
                if (!CollectionUtils.isEmpty(deviceCodeList)) {
                    paramMap.put("deviceCodeList", deviceCodeList);
                    //获取AI+一键+摄像机+物联告警+无人机设备对应的组织信息
                    camerasOrganList = sysDeptDeviceMapper.getCamerasOrganDetails(paramMap);
                    sensorsOrganList = sysDeptDeviceMapper.getSensorsOrganDetails(paramMap);
                    uavOrganList = sysDeptDeviceMapper.getUavsOrganDetails(paramMap);
                }
                if (!CollectionUtils.isEmpty(regionCodeList)) {
                    paramMap.put("regionCodeList", regionCodeList);
                    //获取卫星+雷达告警省市区对应的组织信息
                    satellitesOrganList = sysDeptDeviceMapper.getSatellitesOrganDetails(paramMap);
                    raderOrganList = sysDeptDeviceMapper.getRadersOrganDetails(paramMap);
                }
                if (!CollectionUtils.isEmpty(userNameList)) {
                    //获取人工上报告警上报人的组织信息
                    paramMap.put("userNameList", userNameList);
                    userOrganList = sysDeptDeviceMapper.getOrganUserDetails(paramMap);
                }

                //处理组织信息
                for (OrganInfoReq vo : paramList) {
                    Map<String, String> resultMap = new HashMap<>();
                    String deviceCode = vo.getTenantId() + Constants.SEP + vo.getIndustryCode() + Constants.SEP + vo.getDeviceCode();
                    String regionName = vo.getTenantId() + Constants.SEP + vo.getIndustryCode() + Constants.SEP + vo.getProvinceId() + Constants.SEP + vo.getCityId() + Constants.SEP + vo.getCountyId();
                    String findUserName = vo.getTenantId() + Constants.SEP + vo.getIndustryCode() + Constants.SEP + vo.getFindName();
                    switch (vo.getWarningSource()) {
                        case Constants.WARNING_SOURCE_1:
                        case Constants.WARNING_SOURCE_3:
                        case Constants.WARNING_SOURCE_4:
                            setOrgNameFromDeviceCode(camerasOrganList, deviceCode, vo);
                            break;
                        case Constants.WARNING_SOURCE_2:
                            setOrgNameFromfindUser(userOrganList, findUserName, vo);
                            break;
                        case Constants.WARNING_SOURCE_8:
                            setOrgNameFromDeviceCode(sensorsOrganList, deviceCode, vo);
                            break;
                        case Constants.WARNING_SOURCE_9:
                            setOrgNameFromRegionName(satellitesOrganList, regionName, vo);
                            break;
                        case Constants.WARNING_SOURCE_10:
                            setOrgNameFromRegionName(raderOrganList, regionName, vo);
                            break;
                        case Constants.WARNING_SOURCE_12:
                            setOrgNameFromDeviceCode(uavOrganList, deviceCode, vo);
                            break;
                        default:
                            break;
                    }
                    resultMap.put(vo.getWarningOrderId(), vo.getOrgName());
                    resultList.add(resultMap);
                    log.info("getOrgInfoList获取所属组织-resp:" + JSON.toJSONString(resultList));
                }
            }
        } catch (Exception e) {
            log.error("getOrgInfoList获取所属组织错误：", e);
        }
        return resultList;
    }

    private void setOrgNameFromDeviceCode(List<Map<String, String>> organList, String deviceCode, OrganInfoReq vo) {
        String orgName = null;
        for (Map<String, String> item : organList) {
            if (deviceCode.equals(item.get(Constants.PARAM_DEVICE_CODE))) {
                orgName = item.get(Constants.RES_ORG_NAME);
                break;
            }
        }
        vo.setOrgName(orgName);
    }

    private void setOrgNameFromRegionName(List<Map<String, String>> organList, String regionName, OrganInfoReq vo) {
        String orgName = null;
        for (Map<String, String> item : organList) {
            if (regionName.equals(item.get(Constants.REGION_NAME))) {
                orgName = item.get(Constants.RES_ORG_NAME);
                break;
            }
        }
        vo.setOrgName(orgName);
    }

    private void setOrgNameFromfindUser(List<Map<String, String>> userOrganList, String findUserName, OrganInfoReq vo) {
        String orgName = null;
        for (Map<String, String> item : userOrganList) {
            if (findUserName.equals(item.get(Constants.USERNAME))) {
                orgName = item.get(Constants.RES_ORG_NAME);
                break;
            }
        }
        vo.setOrgName(orgName);
    }

    /**
     * 处理图片排序
     * @param orderInfoVO
     * @return
     */
    public List<Map> processImgsOrder(OrderInfoVO orderInfoVO, String userId) {
        List<Map> imgs = new ArrayList<>();
        try {
            //查询配置的图片、视频展示顺序
            List<AlarmDetailConfigVo> configVoList = warningOrderMapper.queryOrderConfig(userId);
            log.info("事件详情-processImgsOrder查询顺序配置结果：{}", JSON.toJSONString(configVoList));
            orderInfoVO.setFirstOrder("img");
            Optional.ofNullable(configVoList)
                    .filter(list -> !CollectionUtils.isEmpty(list))
                    .map(list -> list.get(0))
                    .map(AlarmDetailConfigVo::getDisplayContent)
                    .filter(Constants.PARAM_VIDEO_URL::equals)
                    .ifPresent(video -> orderInfoVO.setFirstOrder("video"));
            // 找出 imgUrlSrc 和 imgUrlIcon 的展示顺序
            Map<String, Integer> displayOrderMap = configVoList.stream()
                    .collect(Collectors.toMap(
                            AlarmDetailConfigVo::getDisplayContent,
                            AlarmDetailConfigVo::getDisplayOrder,
                            (existing, replacement) -> existing
                    ));
            int iconOrder = displayOrderMap.getOrDefault(Constants.PARAM_FILE_IMG_URL_ICON, Constants.ONE_NUM);
            int srcOrder = displayOrderMap.getOrDefault(Constants.PARAM_FILE_IMG_URL_SRC, Constants.TWO_NUM);
            log.info("fileImgUrlIcon框图展示顺序: {}, fileImgUrlSrc原图展示顺序: {}", iconOrder, srcOrder);

            // 根据顺序添加图片
            if (srcOrder < iconOrder) {
                this.addImages(imgs, orderInfoVO.getFileImgUrlSrc());
                this.addImages(imgs, orderInfoVO.getFileImgUrlIcon());
            } else {
                this.addImages(imgs, orderInfoVO.getFileImgUrlIcon());
                this.addImages(imgs, orderInfoVO.getFileImgUrlSrc());
            }
            //前端告警图片
            this.addImages(imgs, orderInfoVO.getFrontAlarmPicUrl());
        } catch (Exception e) {
            log.error("按照顺序配置给图片排序异常：{}", e);
        }
        return imgs;
    }

    //处理图片
    private void addImages(List<Map> imgs, String imgUrls) {
        if (!StringUtils.isEmpty(imgUrls)) {
            Arrays.stream(imgUrls.split(Constants.COMMA))
                    .filter(StringUtil :: isNotEmpty)
                    .map(img -> {
                        Map<String, String> imgUrlMap = new HashMap<>();
                        imgUrlMap.put(Constants.PARAM_FILE_URL, img);
                        return imgUrlMap;
                    })
                    .filter(imgUrlMap -> !imgs.contains(imgUrlMap))
                    .forEach(imgs::add);
        }
    }

    /**
     * 获取参数配置：可视域ptz使用配置  0-默认ptz逻辑、1-调用基础平台接口
     * @param orderInfoVO
     */
    public void getVisiblePtzConfig(OrderInfoVO orderInfoVO) {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.VISIBLE_PTZ_CONFIG);
        String visiblePtzConfig = Constants.ZERO;

        try {
            AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);

            if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
                SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
                if (sysConfigInfo != null) {
                    visiblePtzConfig = sysConfigInfo.getConfigValue();
                }
            }
        } catch (Exception e) {
            log.error("获取可见PTZ配置失败：{}" + e);
        }
        orderInfoVO.setIsOpenBasicFlag(Constants.ONE.equals(visiblePtzConfig) ? Constants.ONE : Constants.ZERO);
    }
}
