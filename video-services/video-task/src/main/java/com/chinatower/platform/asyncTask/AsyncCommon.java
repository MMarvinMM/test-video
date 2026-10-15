package com.chinatower.platform.asyncTask;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ExtraInfoVO;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.ReceiveUavAlarmVO;
import com.chinatower.event.entity.dto.TranslnglatPeopleDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.SecondRecogBackVO;
import com.chinatower.event.entity.vo.SecondRecogBatchVO;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.modules.statistics.dao.StatisticsDAO;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Author: cuisj
 * @Date: 2024/7/2 10:35
 */

@Slf4j
@Component
public class AsyncCommon {
    @Autowired
    private IReceiveAlarmNewService iReceiveAlarmNewService;

    @Autowired
    private StatisticsDAO statisticsDAO;

    @Autowired
    private SystemFeignClient systemFeignClient;
    //data通过批次id查询算法仓后数据，uavDataList 批次MongoDB表初始数据
    /**
     * 处理无人机告警信息并调用告警接收接口.
     * 方法会过滤出识别结果为 "1" 的数据，为每条数据匹配相关任务信息后，构建无人机告警对象并调用接口接收告警。
     * @param data       识别结果列表，包含无人机任务的识别结果
     * @param uavDataList 无人机任务列表，包含任务的详细信息
     */
    @Async("taskExecutor")
    public void receiveUavAlarm(List<SecondRecogBackVO> data,List<SecondRecogBatchVO> uavDataList) {
        try {
            log.info("生成无人机告警入参原始值{},查询值{}", JSON.toJSONString(uavDataList), JSON.toJSONString(data));
            //过滤需要自动识别告警的数据
            uavDataList = uavDataList.stream().filter(v -> Constants.ONE.equals(v.getMakeAlarmFlag())).collect(Collectors.toList());
            log.info("生成无人机告警入参需要自动识别值{}", JSON.toJSONString(uavDataList));
            // 过滤识别结果为真，调用生成无人机告警接口
            List<SecondRecogBackVO> filterData = data.stream().filter(v -> "1".equals(v.getSecondJudgeResult())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(filterData)){
                for (SecondRecogBackVO vo : filterData) {
                    // 构建无人机告警对象
                    ReceiveUavAlarmVO uavAlarmVO = new ReceiveUavAlarmVO();
                    uavAlarmVO.setTaskId(vo.getTaskId()); // 任务 ID
                    uavAlarmVO.setAlarmTime(DateUtils.getTime()); // 告警时间（当前时间）
                    uavAlarmVO.setSecondJudgeResult(vo.getSecondJudgeResult()); // 二次识别结果
                    uavAlarmVO.setOriginalPictureUrl(vo.getOriginalPictureUrl()); // 原始图片 URL
                    uavAlarmVO.setAlarmPictureUrl(vo.getAlarmPictureUrl()); // 告警图片 URL
                    uavAlarmVO.setAlarmVideoUrl(vo.getAlarmVideoUrl()); // 告警视频 URL
                    uavAlarmVO.setAlarmArea(vo.getAlarmArea()); // 告警区域
                    uavAlarmVO.setImportanceLevel("1");
                    uavAlarmVO.setExtendField(JSON.toJSONString(vo.getExtendField()));
                    // 根据任务 ID 匹配无人机任务列表中的相关数据
                    for (SecondRecogBatchVO uavVo : uavDataList) {
                        if (vo.getTaskId().equals(uavVo.getTaskId())){
                            // 设置无人机告警对象的其他字段
                            uavAlarmVO.setFlightRouteId(uavVo.getAirLineId()); // 飞行航线 ID
                            uavAlarmVO.setTaskRecordId(uavVo.getAirTaskId()); // 任务记录 ID
                            uavAlarmVO.setDeviceCode(uavVo.getDeviceCode()); // 设备编号
                            uavAlarmVO.setAlgorithmCode(uavVo.getAlgorithmCode()); // 算法代码
                            uavAlarmVO.setAlarmType(uavVo.getAlgorithmCode().split("-")[1].substring(0, 7)); // 告警类型
                            uavAlarmVO.setLongitude(uavVo.getLongitude()); // 经度
                            uavAlarmVO.setLatitude(uavVo.getLatitude()); // 纬度
                            uavAlarmVO.setAccessNode(uavVo.getAccessNode()); // 接入节点
                            uavAlarmVO.setAcceptNode(uavVo.getAcceptNode()); // 接受节点
                            log.info("调用无人机告警接收入参{}", JSON.toJSONString(uavAlarmVO));
                            //调用无人机告警接收接口,是否延时调用
                            Result result = iReceiveAlarmNewService.receiveUavAlarm(uavAlarmVO);
                            log.info("调用无人机告警接收结果{}", JSON.toJSONString(result));
                        }
                    }
                }
            }else {
                log.info("未查询到生成的无人机告警");
            }
        }catch (Exception e){
            log.error("调用无人机上报失败",e);
        }

    }


    /**
     * 处理预置位AI告警信息并调用告警接收接口.
     * 方法会过滤出识别结果为 "1" 的数据，为每条数据匹配相关任务信息后，构建AI告警对象并调用接口接收告警。
     * @param data       识别结果列表，包含预置位任务的识别结果
     * @param aiDataList 预置位任务列表，包含任务的详细信息
     */
    @Async("taskExecutor")
    public void receiveAiAlarm(List<SecondRecogBackVO> data,List<SecondRecogBatchVO> aiDataList) {
        try {
            log.info("生成预置位AI告警入参原始值{},查询值{}", JSON.toJSONString(aiDataList), JSON.toJSONString(data));
            //过滤需要自动识别告警的数据
            aiDataList = aiDataList.stream().filter(v -> Constants.ONE.equals(v.getMakeAlarmFlag())).collect(Collectors.toList());
            log.info("生成预置位AI告警入参需要自动识别值{}", JSON.toJSONString(aiDataList));
            // 过滤识别结果为真，调用生成告警接口
            List<SecondRecogBackVO> filterData = data.stream().filter(v -> "1".equals(v.getSecondJudgeResult())).collect(Collectors.toList());
            List<String> typeList = aiDataList.stream().map(v -> v.getAlgorithmCode().split("-")[1].substring(0, 7)).distinct().collect(Collectors.toList());
            //告警类型
            List<Map<String,String>> alarmTypeList = statisticsDAO.getAlarmTypeByValue(typeList);
            Map<String, String> typeMap = alarmTypeList.stream().collect( Collectors.toMap( t -> t.get("typeId"), t -> t.get("typeName"), (o, n) -> n, HashMap::new));
            String localNode="";
            //获取当前节点
            SysConfig sysConfig = new SysConfig();
            sysConfig.setConfigKey(com.chinatower.event.constants.Constants.PLATFORM_CODE);
            AjaxResult configResult = systemFeignClient.getConfigInfo(sysConfig);
            if(configResult != null && 200 == Integer.parseInt(String.valueOf(configResult.get("code")))) {
                SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(configResult.get("data")), SysConfig.class);
                if (sysConfigInfo != null) {
                    localNode = sysConfigInfo.getConfigValue();
                }
            }
            if (!CollectionUtils.isEmpty(filterData)){
                for (SecondRecogBackVO vo : filterData) {
                    // 构建AI告警对象
                    ReceiveAlarmVO aiAlarmVO = new ReceiveAlarmVO();
                    aiAlarmVO.setTaskId(vo.getTaskId()); // 任务 ID
                    aiAlarmVO.setAlarmTime(DateUtils.getTime()); // 告警时间（当前时间）
                    aiAlarmVO.setSecondJudgeResult(vo.getSecondJudgeResult()); // 二次识别结果
                    aiAlarmVO.setOriginalPictureUrl(vo.getOriginalPictureUrl()); // 原始图片 URL
                    aiAlarmVO.setAlarmPictureUrl(vo.getAlarmPictureUrl()); // 告警图片 URL
                    aiAlarmVO.setAlarmVideoUrl(vo.getAlarmVideoUrl()); // 告警视频 URL
                    aiAlarmVO.setAlarmArea(vo.getAlarmArea()); // 告警区域
                    aiAlarmVO.setImportanceLevel(Constants.ONE); //告警等级
                    aiAlarmVO.setAlarmSource(Constants.ONE);//告警来源
                    aiAlarmVO.setIsSecondJudge(Constants.ZERO);//是否二次研判
                    aiAlarmVO.setExtendField(vo.getExtendField());
                    aiAlarmVO.setLocalNode(localNode);
                    aiAlarmVO.setIsPresetMakeAlarm(Constants.ZERO);
                    String code = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
                    aiAlarmVO.setAlarmCode(code);//告警code
                    aiAlarmVO.setEnlargeFlag(Constants.ZERO);//是否3d放大
                    // 根据任务 ID 匹配无人机任务列表中的相关数据
                    for (SecondRecogBatchVO aiVo : aiDataList) {
                        if (vo.getTaskId().equals(aiVo.getTaskId())){
                            // 设置AI告警对象的其他字段
                            if (StringUtils.isNotEmpty(aiVo.getPtzInfoJson())){
                                //ptz信息
                                TranslnglatPeopleDTO translnglatPeopleDTO = JSONObject.parseObject(aiVo.getPtzInfoJson(), TranslnglatPeopleDTO.class);
                                if (ObjectUtil.isNotNull(translnglatPeopleDTO)){
                                    aiAlarmVO.setPan(translnglatPeopleDTO.getPan()); //PTZ
                                    aiAlarmVO.setTilt(translnglatPeopleDTO.getTilt());
                                    aiAlarmVO.setZoom(translnglatPeopleDTO.getZoom());
                                    ExtraInfoVO extraInfoVO = new ExtraInfoVO();
                                    String msgId = UUID.randomUUID().toString().replace("-", "");
                                    extraInfoVO.setPresetId(translnglatPeopleDTO.getPresetId());//预置位id
                                    extraInfoVO.setMsgId(msgId); //唯一标识
                                    extraInfoVO.setNoticeResource(Constants.TWO_NUM); //通知类型
                                    aiAlarmVO.setExtraInfo(extraInfoVO);
                                }
                            }
                            aiAlarmVO.setDeviceCode(aiVo.getDeviceCode()); // 设备编号
                            aiAlarmVO.setChannelCode(aiVo.getChannelCode()); // 通道编号
                            aiAlarmVO.setAlgorithmCode(aiVo.getAlgorithmCode()); // 算法代码
                            String type = aiVo.getAlgorithmCode().split("-")[1].substring(0, 7);
                            aiAlarmVO.setAlarmType(type); // 告警类型
                            if (!CollectionUtils.isEmpty(typeMap)){
                                aiAlarmVO.setAlarmBody(MapUtil.getStr(typeMap,type));//告警标题
                            }else {
                                aiAlarmVO.setAlarmBody("Ai告警");
                            }
                            aiAlarmVO.setTenantId(aiVo.getTenantId());
                            aiAlarmVO.setTenantName(aiVo.getTenantName());
                            aiAlarmVO.setIndustryCode(aiVo.getIndustryCode());
                            aiAlarmVO.setAppVerCode(aiVo.getAppVerCode());
                            aiAlarmVO.setLongitude(aiVo.getLongitude()); // 经度
                            aiAlarmVO.setLatitude(aiVo.getLatitude()); // 纬度
                            aiAlarmVO.setAccessNode(aiVo.getAccessNode()); // 接入节点
                            aiAlarmVO.setAcceptNode(aiVo.getAcceptNode()); // 接受节点
                            log.info("调用Ai告警接收入参{}", JSON.toJSONString(aiAlarmVO));
                            ReceiveAlarmVOReq receiveAlarmVOReq = new ReceiveAlarmVOReq();
                            receiveAlarmVOReq.setData(aiAlarmVO);
                            Result result = iReceiveAlarmNewService.receiveAlarm(receiveAlarmVOReq);
                            log.info("调用Ai告警接收结果{}", JSON.toJSONString(result));
                        }
                    }
                }
            }else {
                log.info("未查询到生成的Ai告警告警");
            }
        }catch (Exception e){
            log.error("调用Ai告警上报失败",e);
        }

    }

}
