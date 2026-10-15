package com.chinatower.event.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.common.core.domain.AlarmOperateDO;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.asyncTask.AsyncCommon;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dto.JudgeAlarmOrderDTO;
import com.chinatower.event.dto.MessagePushDTO;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.entity.dto.OperateMongo;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.mapper.*;
import com.chinatower.event.mongodb.OperateMongoMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.service.OperateService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.IGuardService;
import com.chinatower.video.api.entity.VideoRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 运营研判表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
@Slf4j
@Service
public class AlarmOperateRepositoryImpl extends ServiceImpl<AlarmOperateMapper, AlarmOperateDO> implements IAlarmOperateRepository {

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private IReceiveRepository receiveRepository;

    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Autowired
    private WarningOriginalAreaMapper warningOriginalAreaMapper;

    @Autowired
    private WarningOriginalPointMapper warningOriginalPointMapper;

    @Autowired
    private WarningInterceptMapper warningInterceptMapper;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private IGuardService guardService;

    @Autowired
    private ISysService sysService;

    @Autowired
    private AsyncCommon asyncCommon;

    @Autowired
    private OperateMongoMapper operateMongoMapper;

    @Autowired
    private OperateService operateService;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;


    /**
     * 对工单图片进行拼接（非中心节点&受理节点与当前节点不一致得单子进行操作，其余保持原有路径）
     * @param accessNode
     * @param url
     * @return
     */
    public String montageUrl(String accessNode,String url, boolean mergeUrlFlag){
        log.info("getAlarmImgs-montageUrl-接入节点为空-入参:{}",JSON.toJSONString(accessNode));
        if(!mergeUrlFlag || StringUtils.isEmpty(accessNode) || "10000000001".equals(accessNode) || StringUtils.isEmpty(url)){
            return url;
        }

        String[] urls = url.split(Constants.COMMA);
        String resUrl="";
        for (String realUrl:urls) {
            if(realUrl.contains("http") || realUrl.contains("https")){
                //将属地编码拼接在域名之后
                int thirdSlashIndex = realUrl.indexOf('/', realUrl.indexOf('/', realUrl.indexOf('/') + 1) + 1);
                // 拼接字符串，在第三个斜杠之后
                StringBuilder res = new StringBuilder(realUrl.substring(0, thirdSlashIndex + 1));
                res.append(accessNode+"/");
                res.append(realUrl.substring(thirdSlashIndex + 1));
                realUrl = res.toString();
            }else{
                realUrl = "/"+accessNode+realUrl;
            }
            if(StringUtils.isEmpty(resUrl)){
                resUrl = realUrl;
            }else{
                resUrl +=Constants.COMMA+realUrl;
            }
        }
        return resUrl;
    }

    /**
     * 调用6.13接口获取图片详情
     *
     * @param orderInfoVO
     * @return
     */
    @Override
    public void getAlarmImgs(OrderInfoVO orderInfoVO) {
        String warningOrderId = orderInfoVO.getWarningOrderId();
//        String imgUrl = (String) map.get("imgUrl");
//        List<AlarmImgVO> list = alarmDAO.getAlarmImgList(warningOrderId);
//        if(CollectionUtils.isEmpty(list)){
        // 没有图片，调用6.13接口
        boolean mergeUrlFlag = true;
        //纯属化不拼接节点编码
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey("dependence_manage");
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        log.info("获取当前环境字段返回:{}",JSON.toJSONString(result));
        if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null && Constants.ZERO.equals(sysConfigInfo.getConfigValue())) {
                mergeUrlFlag = false;
            }
        }
        VideoRequestDTO videoRequestDTO = new VideoRequestDTO();
        videoRequestDTO.setDeviceCode(orderInfoVO.getDeviceCode());
        videoRequestDTO.setChannelCode(orderInfoVO.getChannelCode());
        videoRequestDTO.setAlarmId(orderInfoVO.getAlarmCode());
        DeviceSiteVO deviceSiteVO = deviceRepository.queryDevice(orderInfoVO.getDeviceCode(),null);
        String accessNode ="";
        if(deviceSiteVO != null){
            accessNode = deviceSiteVO.getAccessNode();
        }
        log.info("getAlarmImgs-device-accessNode:{}",accessNode);
        Result<JSONObject> pr = guardService.getAlarmInfo(videoRequestDTO);
        if (ResultCode.SUCCESS.getCode() == pr.getCode()) {
            JSONObject jo = pr.getData();
            log.info("6.13返回结果：{}", jo);
            if (!jo.isEmpty() && ObjectUtil.isNotNull(jo.get(Constants.PARAM_DATA))) {
                jo = JSON.parseObject(JSON.toJSONString(jo.get(Constants.PARAM_DATA)));
                //告警录像信息
                String videoStr = "";
                if(ObjectUtil.isNotNull((jo.get(Constants.PARAM_ALARM_VIDEO)))){
                    List<Map> videos = (List<Map>) (jo.get(Constants.PARAM_ALARM_VIDEO));
                    if (!CollectionUtils.isEmpty(videos)) {
                        for (Map videosMap : videos) {
                            if (videosMap.size()<=0){break;}
                            videoStr += montageUrl(accessNode,String.valueOf(videosMap.get(Constants.PARAM_FILE_VIDEO_URL)),mergeUrlFlag) + Constants.COMMA;
                        }
                    }
                }
                //告警原图信息
                String imgStr = "";
                if(ObjectUtil.isNotNull(jo.get(Constants.PARAM_ALARM_TAG_PIC))){
                    List<Map> imgs = (List<Map>) jo.get(Constants.PARAM_ALARM_TAG_PIC);
                    if (!CollectionUtils.isEmpty(imgs)) {
                        for (Map imgsMap : imgs) {
                            if (imgsMap.size()<=0){break;}
                            imgStr += montageUrl(accessNode,String.valueOf(imgsMap.get(Constants.PARAM_TAG_PICTURE_URL)),mergeUrlFlag) + Constants.COMMA;
                        }
                    }
                }
                // 告警框抓图信息
                List<Map> alarmOrigPic = new ArrayList();
                if(ObjectUtil.isNotNull(jo.get(Constants.PARAM_ALARM_ORIG_PIC))){
                    alarmOrigPic = (List<Map>)jo.get(Constants.PARAM_ALARM_ORIG_PIC);
                }
                String alarmOrigPicStr = "";
                List<AlarmOriginalAreaVO> alarmAreas = new ArrayList<>();
                List<AlarmOriginalPointVO> alarmPointLists = new ArrayList<>();
                if (!CollectionUtils.isEmpty(alarmOrigPic)) {
                    for (Map imgMap : alarmOrigPic) {
                        if (imgMap.size()<=0){break;}
                        String originalId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                        AlarmImgVO img = JSON.toJavaObject(JSONObject.parseObject(JSON.toJSONString(imgMap)), AlarmImgVO.class);
                        if (!StringUtils.isEmpty(img.getOriginalPictureUrl())) {
                            alarmOrigPicStr += montageUrl(accessNode,String.valueOf(img.getOriginalPictureUrl()),mergeUrlFlag) + Constants.COMMA;
                        }
                        List<AlarmOriginalAreaVO> alarmArea = img.getAlarmArea();
                        if (!CollectionUtils.isEmpty(alarmArea)) {
                            for (AlarmOriginalAreaVO area : alarmArea) {
                                area.setOriginalId(originalId);
                                area.setWarningOrderId(warningOrderId);
                                List<AlarmOriginalPointVO> alarmPointList = area.getAlarmPointList();
                                if (!CollectionUtils.isEmpty(alarmPointList)) {
                                    for (AlarmOriginalPointVO point : alarmPointList) {
                                        point.setAlarmAreaId(area.getAlarmAreaId());
                                    }
                                    alarmPointLists.addAll(alarmPointList);
                                }
                            }
                            alarmAreas.addAll(alarmArea);
                        }
                    }
                }
                Map mapIn = new HashMap();
                mapIn.put(Constants.PARAM_WARNING_ORDER_ID, warningOrderId);
                mapIn.put(Constants.PARAM_TABLE_NAME, TableConstant.WARN_ORDER_TEBLE
                        + warningOrderId.substring(0, 6));
                boolean flag = false;
                if (!StringUtils.isEmpty(videoStr)) {
                    mapIn.put(Constants.PARAM_VIDEO_URL,
                            videoStr.substring(0, videoStr.length() - 1));
                    flag = true;
                    orderInfoVO.setVideoUrl(videoStr.substring(0, videoStr.length() - 1));
                }
                if (!StringUtils.isEmpty(imgStr)) {
                    mapIn.put(Constants.PARAM_FILE_IMG_URL_SRC,
                            imgStr.substring(0, imgStr.length() - 1));
                    flag = true;
                    orderInfoVO.setFileImgUrlSrc(imgStr.substring(0, imgStr.length() - 1));
                }
                if (!StringUtils.isEmpty(alarmOrigPicStr)) {
                    mapIn.put(Constants.PARAM_FILE_IMG_URL_ICON, alarmOrigPicStr.substring(0,
                            alarmOrigPicStr.length() - 1));
                    flag = true;
                    orderInfoVO.setFileImgUrlIcon(alarmOrigPicStr.substring(0,
                            alarmOrigPicStr.length() - 1));
                }
                //异步批量保存告警原始图片区域/区域点 更新图片
                asyncCommon.saveAlarmOriginalPicMongoDB(alarmAreas,alarmPointLists,flag,mapIn);
            }
        }
    }

    /**
     * 研判：假警、真警、重复告警处理
     *
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result checkAlarmOrder(Map map) {
        try {
            log.info("checkAlarmOrder接收入参:{}",JSONObject.toJSONString(map));
            // 获取当前登录人
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String warningOrderId = (String) map.get(Constants.PARAM_WARNING_ORDER_ID);
            // 查询运营研判详情
            //AlarmOperateDTO alarmOperateDTO = alarmOperateMapper.getAlarmOperate(warningOrderId);
            AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(warningOrderId,null);
            if (null == alarmOperateDTO) {
                return Result.fail(Constants.ORDER_ERROR_MESSAGE);
            }
            else if (!Constants.JUDGE_RESULT_3.equals(alarmOperateDTO.getJudgeResult())) {
                // 如果已经研判过，直接返回
                return Result.fail(Constants.ORDER_JUDGED);
            }
            // 防止出现串行告警研判为重复告警的情况
            if (Constants.TWO.equals(map.get(Constants.PARAM_JUDGE_RESULT))
                    && Constants.YES_FLAG.equals(alarmOperateDTO.getOperateType())) {
                // 串行告警不可研判为重复告警
                return Result.fail(Constants.ORDER_JUDGED_TWO);
            }

            map.put(Constants.PARAM_CREATE_USER, sysService.getLoginUserId());
            map.put(Constants.PARAM_CREATE_USER_NAME, loginUser.getUser().getUserName());
            // R-ZBZB-202112310835395 20220105 zzh 来源为铁塔
            if (StringUtils.isEmpty(map.get(Constants.PARAM_SOURCE))) {
                map.put(Constants.PARAM_SOURCE, Constants.VALID_STATE);
            }
            alarmOperateMapper.updateAlarmOperate(map);
            try{
                OperateMongo mongo = JSON.parseObject(JSON.toJSONString(map),OperateMongo.class);
                List<OperateMongo> operateMongoList = new ArrayList<>();
                mongo.setModifierTime(DateUtils.currentDateToString());
                operateMongoList.add(mongo);
                operateMongoMapper.updateOperate(operateMongoList);
            }catch (Exception e){
                log.error("checkAlarmOrder-修改运营研判mongo异常:",e);
            }
            if (Constants.TWO_FLAG.equals(alarmOperateDTO.getOperateType()) && Constants.VALID_STATE.equals(map.get(Constants.PARAM_SOURCE))) {
                // R-ZBZB-202112310835395 20220105 zzh 并行流程并且来源是铁塔时，同步更新告警状态
                JudgeAlarmOrderDTO alarmOrderVO = new JudgeAlarmOrderDTO();
                alarmOrderVO.setOrderId(warningOrderId);
                if (Constants.YES_FLAG.equals(map.get(Constants.PARAM_JUDGE_RESULT))) {
                    alarmOrderVO.setOrderStatus(EnumUtil.FlowNode.PENDING_DISPATCH.getCode());
                }
                else {
                    alarmOrderVO.setOrderStatus(EnumUtil.FlowNode.MISINFORMATION.getCode());
                }
                Result result = warningOrderRepository.checkAlarmOrder(alarmOrderVO);
                if (ResultCode.FAILURE.getCode() == result.getCode()) {
                    // 调用接口异常，回滚
                    throw new Exception((String) result.getData());
                }
            }
            else if (Constants.YES_FLAG.equals(map.get(Constants.PARAM_JUDGE_RESULT))
                    && Constants.YES_FLAG.equals(alarmOperateDTO.getOperateType())) {
                // 批量保存告警信息
                ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
                receiveAlarmVODB.setTableName(TableConstant.WARN_ORDER_TEBLE + warningOrderId.substring(0, 6));
                List<ReceiveAlarmVO> alarms = new ArrayList<>();
                ReceiveAlarmVO receiveAlarmVO = new ReceiveAlarmVO();
                BeanUtils.copyProperties(alarmOperateDTO, receiveAlarmVO );
                receiveAlarmVO.setWarningSource(EnumUtil.WarningSource.AI.getCode());
                receiveAlarmVO.setAlarmType(receiveAlarmVO.getWarningTypeId());
                receiveAlarmVO.setProvinceCode(receiveAlarmVO.getProvinceId());
                receiveAlarmVO.setCityCode(receiveAlarmVO.getCityId());
                receiveAlarmVO.setCountyCode(receiveAlarmVO.getCountyId());
                if (!StringUtils.isEmpty(alarmOperateDTO.getPan())) {
                    receiveAlarmVO.setPan(alarmOperateDTO.getPan());
                }
                if (!StringUtils.isEmpty(alarmOperateDTO.getTilt())) {
                    receiveAlarmVO.setTilt(alarmOperateDTO.getTilt());
                }
                if (!StringUtils.isEmpty(alarmOperateDTO.getZoom())) {
                    receiveAlarmVO.setZoom(alarmOperateDTO.getZoom());
                }
                if (!StringUtils.isEmpty(alarmOperateDTO.getImgUrl())) {
                    receiveAlarmVO.setOriginalPictureUrl(alarmOperateDTO.getImgUrl());
                }
                if (!StringUtils.isEmpty(alarmOperateDTO.getVideoUrl())) {
                    receiveAlarmVO.setAlarmVideoUrl(alarmOperateDTO.getVideoUrl());
                }
                if (!StringUtils.isEmpty(alarmOperateDTO.getFileImgUrlIcon())) {
                    receiveAlarmVO.setAlarmPictureUrl(alarmOperateDTO.getFileImgUrlIcon());
                }

                // 默认流程，第一个节点为研判环节
                receiveAlarmVO.setCurrentLinkId(EnumUtil.FlowNode.PENDING_JUDGE.getCode());
                receiveAlarmVO.setCurrentLinkName(EnumUtil.FlowNode.PENDING_JUDGE.getName());
                // 人工上报、一键告警历史数据会有流程环节，保持现状
                receiveAlarmVO.setLinkName(EnumUtil.WarningSource.AI.getName());
                receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
                receiveAlarmVO.setJudgeResult((String) map.get(Constants.PARAM_JUDGE_RESULT));
                receiveAlarmVO.setAppVerCode(loginUser.getUser().getAppVerCode());
                receiveAlarmVO.setIndustryCode(loginUser.getUser().getIndustryCode());
                alarms.add(receiveAlarmVO);

                // 20211229 zzh 研判为真警后，走压制的逻辑
                // 运营研判校验压制逻辑时告警时间取当前时间
                ReceiveAlarmVO tempVO = new ReceiveAlarmVO();
                BeanUtils.copyProperties(receiveAlarmVO, tempVO);
                tempVO.setAlarmTime(DateUtils.date2Str(new Date(), null));
                List<ReceiveAlarmVO> tempAlarms = new ArrayList<>();
                tempAlarms.add(tempVO);

                // 走正常逻辑
                // 创建流程
                List<Map> recordList = warningOrderRepository.createRecord(alarms);
                // 回填当前环节相关字段
                warningOrderRepository.setWarningLinkInfo(alarms, recordList);
                receiveAlarmVODB.setList(alarms);
                int num = warningOrderMapper.insertWarningOrders(receiveAlarmVODB);
                if (num > 0) {
                    // 20211229 zzh 将最新告警时间保存到Redis
                    receiveRepository.setAlarmTimeToRedis(tempAlarms);
                }
                // 保存运营研判真警临时表（定时补偿机制使用） vm_alarm_operate_temp_t
//                try {
//                    alarmOperateMapper.insertOperateTemp(receiveAlarmVO);
//                }
//                catch (Exception e) {
//                    log.info("保存运营研判真警临时表 error" + e);
//                }


                MessagePushDTO messagePushDTO = new MessagePushDTO();
                messagePushDTO.setOrderId(warningOrderId);
                messagePushDTO.setMsgId(warningOrderId);
                messagePushDTO.setTenantId(alarmOperateDTO.getTenantId());
                messagePushDTO.setAlarmFlowId(alarms.get(0).getFlowId());
                messagePushDTO.setLinkId(alarms.get(0).getLinkId());
                messagePushDTO.setCreator("");
                messagePushDTO.setNotifyTitle(alarmOperateDTO.getAlarmBody());
                messagePushDTO.setTypeValue(alarmOperateDTO.getWarningTypeId());
                messagePushDTO.setAddress(alarms.get(0).getAddress());
                messagePushDTO.setUserType(String.valueOf(map.get("userType")));
                messagePushDTO.setMsgNotifyType(String.valueOf(map.get("msgNotifyType")));
                messagePushDTO.setFlowFlag(Constants.ONE);
                String processId = alarms.get(0).getProcessId();
                if (!StringUtils.isEmpty(processId)) {
                    if (processId.startsWith(Constants.COMMA)) {
                        processId = processId.substring(1, processId.length());
                    }
                    if (processId.endsWith(Constants.COMMA)) {
                        processId = processId.substring(0, processId.length() - 1);
                    }
                }
                messagePushDTO.setUserId(processId);
                String roleId = alarms.get(0).getRoleId();
                if (!StringUtils.isEmpty(roleId)) {
                    if (roleId.startsWith(Constants.COMMA)) {
                        roleId = roleId.substring(1, roleId.length());
                    }
                    if (roleId.endsWith(Constants.COMMA)) {
                        roleId = roleId.substring(0, roleId.length() - 1);
                    }
                }
                messagePushDTO.setRoleId(roleId);
                messagePushDTO.setNotifyType(Constants.SYS_MSG_TYPE_2);
                messagePushDTO.setOrderStatus(alarms.get(0).getOrderStatus());
                messagePushDTO.setFlowTemplate(alarms.get(0).getFlowTemplate());
                messagePushDTO.setAlarmTime(alarms.get(0).getAlarmTime());
                messagePushDTO.setAppVerCode(alarms.get(0).getAppVerCode());
                messagePushDTO.setIndustryCode(alarms.get(0).getIndustryCode());
                messagePushDTO.setRealLinkType(alarms.get(0).getRealLinkType());
                receiveRepository.messagePush(messagePushDTO);

            }
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(MessageConstants.JUDGED_FAIL_MESSAGE, e);
            return Result.fail(Constants.DEFAULT_ERROR_MESSAGE);
        }

        return Result.success(ResultCode.SUCCESS);
    }
}