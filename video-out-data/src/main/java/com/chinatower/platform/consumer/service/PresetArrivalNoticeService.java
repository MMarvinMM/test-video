package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.message.api.MsgStationAPI;
import com.chinatower.message.api.model.SendPresetArrivalNoticeVO;
import com.chinatower.outdata.api.entity.PresetArrivalNotice;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lzt
 */
@Component
@Slf4j
public class PresetArrivalNoticeService {
    @Resource
    private MsgStationAPI msgStationAPI;

    @Resource
    private SystemFeignClient systemFeignClient;

    //<摄像机编码，wsId>
    private static final Map<String, Set<String>> deviceCodeMap = new ConcurrentHashMap<>();

    //有效的摄像机编码
    private static final Set<String> deviceCodeList = new HashSet<>();

    public void delPresetArrivalNotice(List<String> messages,String flag) {
        log.info("本地内存deviceCodeMap:{}", JSON.toJSONString(deviceCodeMap));
        log.info("本地内存deviceCodeList:{}", JSON.toJSONString(deviceCodeList));
        if(!CollectionUtils.isEmpty(messages)){
            List<SendPresetArrivalNoticeVO> sendList = new ArrayList<>();
            messages.forEach(message -> {
                log.info("delPresetArrivalNotice消费的message为:{},{}",flag,message);
                try {
                    PresetArrivalNotice infoVO = JSON.parseObject(message, PresetArrivalNotice.class);
                    if(ObjectUtils.isEmpty(infoVO)){
                        throw new Exception();
                    }
                    String extractionType = infoVO.getExtractionType();
                    String noticeResource = infoVO.getNoticeResource();
                    //预置位到达通知 （只在 "extractionType": "2", "noticeResource": "1"时消费）
                    if(Constants.TWO.equals(extractionType) && Constants.ONE.equals(noticeResource)){
                        String deviceCode = infoVO.getDeviceCode();
                        if(deviceCodeList.contains(deviceCode)){
                            Set<String> strings = deviceCodeMap.get(deviceCode);
                            if (!CollectionUtils.isEmpty(strings)){
                                for (String string : strings) {
                                    String[] split = string.split(Constants.FEN_HAO);
                                    SendPresetArrivalNoticeVO sendPresetArrivalNoticeVO = new SendPresetArrivalNoticeVO();
                                    BeanUtils.copyProperties(infoVO,sendPresetArrivalNoticeVO);
                                    sendPresetArrivalNoticeVO.setPresetId(infoVO.getPreset().getPresetId());
                                    sendPresetArrivalNoticeVO.setTenantId(split[0]);
                                    sendPresetArrivalNoticeVO.setUserId(split[1]);
                                    sendPresetArrivalNoticeVO.setIndustryCode(split[2]);
                                    sendList.add(sendPresetArrivalNoticeVO);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    log.error("delPresetArrivalNotice请求报文{}，异常信息：{}", message, e);
                }
            });
            if(!CollectionUtils.isEmpty(sendList)){
                msgStationAPI.sendPresetArrivalNoticeBatch(sendList);
            }
        }
    }


    /**
     * 定时任务，从Redis中获取缓存数据
     * 任务每隔 3 秒执行一次
     * 任务在应用启动后延迟 10 秒开始执行
     */
    @Scheduled(fixedRate = 3_000, initialDelay = 10_000)
    public void fetchFromRedis() {
        AjaxResult result = systemFeignClient.getConfValue("preset_arrival_notice_switch");
        //预置位到达通知是否推送到pc端，0：关闭  1：打开
        String presetArrivalNoticeSwitch = "";
        if ("200".equals(String.valueOf(result.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(result.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (sysConfig != null) {
                presetArrivalNoticeSwitch = sysConfig.getConfigValue();
            }
        }
        if(Constants.ZERO.equals(presetArrivalNoticeSwitch)){
            deviceCodeMap.clear();
            deviceCodeList.clear();
            return;
        }

        long nowTime = new Date().getTime();
        Map<Object, Object> cacheMap = RedisUtil.hGetAll(Constants.HEART_BEAT_KEY, RedisDBType.PRIMARY);

        Set<Object> keys = cacheMap.keySet();
        if (CollectionUtils.isEmpty(keys)) {
            deviceCodeMap.clear();
            deviceCodeList.clear();
            return;
        }

        //清除过期redis hash值
        for (Map.Entry<Object, Object> oj : cacheMap.entrySet()) {
            String field = (String) oj.getKey();
            String value = (String)  oj.getValue();
            List<String> list = JSON.parseArray(value, String.class);
            for (int i = list.size() - 1; i >= 0; i--) {
                String s = list.get(i);
                String[] split = s.split(Constants.UNDER_LINE);
                long time = Long.parseLong(split[0]);
                String deviceCode = split[1];
                if(nowTime > time ){
                    list.remove(i);
                    deviceCodeMap.remove(deviceCode);
                    deviceCodeList.remove(deviceCode);
                }else{
                    Set<String> os = deviceCodeMap.get(deviceCode);
                    if(CollectionUtils.isEmpty(os)){
                        os = new HashSet<>();
                    }
                    os.add(field);
                    deviceCodeMap.put(deviceCode,os);
                    deviceCodeList.add(deviceCode);
                }
            }

            //更新缓存value
            if(CollectionUtils.isEmpty(list)){
                RedisUtil.hDelete(Constants.HEART_BEAT_KEY, RedisDBType.PRIMARY, field);
            }else{
                RedisUtil.hset(Constants.HEART_BEAT_KEY, field, JSON.toJSONString(list), RedisDBType.PRIMARY);
            }
        }
        log.info("本地内存deviceCode:{}", JSON.toJSONString(deviceCodeMap));
        log.info("本地内存deviceCodeList:{}", JSON.toJSONString(deviceCodeList));
    }
}
