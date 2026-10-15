package com.chinatower.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.RadarObject;
import com.chinatower.platform.consumer.item.Constants;
import com.chinatower.platform.consumer.service.DeviceIndexInfoService;
import com.chinatower.platform.consumer.service.PresetArrivalNoticeService;
import com.chinatower.platform.consumer.service.RadarMsgHandler;
import com.chinatower.platform.consumer.service.VmDeviceSyncService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 测试类
 */
@Slf4j
@RestController
public class TestController {


    @Resource
    RadarMsgHandler radarMsgHandler;
    @Resource
    DeviceIndexInfoService deviceIndexInfoService;

    @Resource
    PresetArrivalNoticeService presetArrivalNoticeService;

    @Resource
    VmDeviceSyncService vmDeviceSyncService;

    @Resource
    MongoScriptSecurityRebuilder securityRebuilder;

    @GetMapping("/genMessage")
    public void pushMessage() {
        //得到Optional实例中的值
        String message = "{\"course\":183.2,\"displacement\":1807.5,\"displayId\":8562,\"duration\":3102861,\"fixed\":false,\"flags\":0,\"heading\":183.2,\"id\":\"11010912456875249\",\"idLink\":\"3509877734451453952\",\"idR\":315,\"lastDt\":\"2023-05-12 05:50:49\",\"lastTm\":1683841849099,\"latitude\":20.24183351,\"len\":19,\"longitude\":110.05587994,\"maxLen\":23,\"minLen\":0,\"period\":1,\"processTime\":\"2023-05-12 05:50:52\",\"province\":\"JiangXi\",\"quality\":100,\"rangeMaxDistance\":157.98695546108652,\"receiveTime\":1683841852277,\"sClass\":\"RADAR\",\"shortLived\":false,\"snapshotId\":1683841852499,\"sources\":[{\"ids\":[\"315\"],\"provider\":\"HLX\",\"type\":\"RADAR\"}],\"speed\":1.67,\"state\":1,\"staticState\":\"2\",\"status\":\"UPDATE\",\"targetId\":\"3509877734451453952\",\"uidR\":\"JiangXi3153\"} ";
        log.info("接收到雷达目标：{}  ", message);
        RadarObject radarObject = JSON.parseObject(message, RadarObject.class);

        if (radarObject.getStatus().equals("DELETED")) {
            return;
        }
        //推送的消息映射信息，key 是 租户id_行业编码
        Map<Object, Object> mapping = radarMsgHandler.needPushMapping(radarObject);
        if (MapUtils.isEmpty(mapping)) {
            return;
        }

        radarMsgHandler.doPush(mapping);
    }

    @PostMapping("/mytestHset")
    public void hset(@RequestBody String str) {
        log.info("============================== {}", str);
        JSONArray jsonArray = JSON.parseArray(str);
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            String industryCode = (String) jsonObject.keySet().toArray()[0];

            JSONObject vo = (JSONObject) jsonObject.get(industryCode);
            JSONArray authPathLis1 = vo.getJSONArray(Constants.AUTHPATHLIS);

            String industryAkAuthConfig = RedisUtil.hGet("industryAkAuthConfig", industryCode, RedisDBType.PRIMARY);
            if (industryAkAuthConfig != null) {
                JSONObject object1 = JSON.parseObject(industryAkAuthConfig);
                JSONArray authPathLis = object1.getJSONArray(Constants.AUTHPATHLIS);
                Set set = new HashSet();
                for (Object authPathLi : authPathLis) {
                    set.add(authPathLi);
                }
                for (Object o : authPathLis1) {
                    set.add(o);
                }
                vo.put(Constants.AUTHPATHLIS, set);
            }

            RedisUtil.hset("industryAkAuthConfig", industryCode, vo.toJSONString(), RedisDBType.PRIMARY);
        }
    }

    @PostMapping("/test")
    public String test1(@RequestBody String key) {
        String sensorValue = key;
        List<String> list = new ArrayList<>();
        list.add(sensorValue);
        deviceIndexInfoService.delDeviceIndexInfo(list);
        return "test1";
    }

    @PostMapping("/delPresetArrivalNotice")
    public void delPresetArrivalNotice(@RequestBody List<String> messages) {
        presetArrivalNoticeService.delPresetArrivalNotice(messages,null);
    }

    @PostMapping("/fetchFromRedis")
    public void fetchFromRedis() {
        presetArrivalNoticeService.fetchFromRedis();
    }

    @PostMapping("/initMongoDeviceData")
    public Result<String> initMongoDeviceData() {
        return vmDeviceSyncService.initMongoDeviceData();
    }

    /**
     * 测试方法：验证执行mongo脚本
     * @param args
     */
/*    public static void main(String[] args) {
        MongoScriptSecurityRebuilder rebuilder = new MongoScriptSecurityRebuilder();
        testBusinessQuery(rebuilder);
    }
    private static void testBusinessQuery(MongoScriptSecurityRebuilder rebuilder) {
        String yourQuery = "{"
                + "\"aggregate\": \"vm_video_preset_snapshot\","
                + "\"pipeline\": ["
                + "  {\"$project\": {"
                + "    \"snapshotTime\": {\"$dateToString\": {\"format\": \"%Y-%m-%d %H:%M:%S\", \"date\": \"$snapshotTime\"}},"
                + "    \"deviceCode\": \"$deviceCode\","
                + "    \"channelCode\": \"$channelCode\","
                + "    \"picId\": {\"$toString\": \"$_id\"},"
                + "    \"imgUrl\": \"$imgUrl\","
                + "    \"presetId\": {\"$toString\": \"$presetId\"},"
                + "    \"deleteFlag\": \"$deleteFlag\","
                + "    \"tenantId\": \"$tenantId\","
                + "    \"industryCode\": \"$industryCode\""
                + "  }},"
                + "  {\"$match\": {"
                + "    \"tenantId\": \"125049916\","
                + "    \"industryCode\": \"700002\","
                + "    \"deleteFlag\": 0,"
                + "    \"imgUrl\": {\"$ne\": \"\"},"
                + "    \"deviceCode\": {"
                + "      \"$in\": [\"35011200001327000002\", \"11010800001327000019\", \"35018100001327000008\"]"
                + "    },"
                + "    \"snapshotTime\": {"
                + "      \"$lte\": \"2026-01-08 23:59:59\","
                + "      \"$gte\": \"2024-11-29 00:00:00\""
                + "    }"
                + "  }},"
                + "  {\"$sort\": {\"snapshotTime\": -1}},"
                + "  {\"$skip\": 0},"
                + "  {\"$limit\": 8}"
                + "],"
                + "\"cursor\": {}"
                + "}";

        try {
            String safeScript = rebuilder.rebuildSafeScript(yourQuery);
            System.out.println("✅ 您的查询通过安全检查！");
            System.out.println("安全脚本: " + safeScript);
        } catch (SecurityException e) {
            System.out.println("❌ 查询被拒绝: " + e.getMessage());
        }
    }*/


}
