package com.chinatower.message.station.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.message.station.entity.*;
import com.chinatower.message.util.DistanceUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.AirLineVo;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.device.entity.vo.TenantInfoRes;
import com.chinatower.message.station.mapper.UavTaskMapper;
import com.chinatower.message.websocket.config.NettyConfig;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.util.StringUtil;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author WLF
 * @description
 * @date 2024/12/3 9:44
 */
@Slf4j
@Service
public class UavAsyncSendMsgService {
    @Resource
    UavTaskMapper uavTaskMapper;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private IAirLineService airLineService;

    /**
     * 发送抽帧进度数据
     *
     * @param uavVideoFrameMsg
     */
    @Async("threadPoolTaskExecutor")
    public void sendRadarMsg(UavVideoFrameMsg uavVideoFrameMsg) {
        log.info("抽帧进度发送websocket数据:{}", JSON.toJSONString(uavVideoFrameMsg));
        Collection<List<Channel>> channels = NettyConfig.getUserChannelListMap().values();
        channels.forEach(channelList -> {
            if (null != channelList) {
                channelList.forEach(channel -> {
                    if (null != channel) {
                        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(uavVideoFrameMsg)));
                    }
                });
            }
        });
        log.info("抽帧进度发送websocket成功!");
    }

    /**
     * 更新任务状态
     *
     * @param uavVideoFrameMsg
     */
    @Transactional
    public int updateTaskStatus(UavVideoFrameMsg uavVideoFrameMsg) {
        int status = uavTaskMapper.updateTaskStatus(uavVideoFrameMsg);
        return status;
    }

    public void extractionTaskMsgSupplement(UavVideoFrameMsg uavVideoFrameMsg) {
        if (uavVideoFrameMsg == null || uavVideoFrameMsg.getId() == null) {
            log.warn("抽帧任务消费数据补充参数为空或ID为空");
            return;
        }
        log.info("抽帧任务消费数据补充行业推送ws:{}", uavVideoFrameMsg);
        String cacheKey = CacheConstants.EXTRACTION_TASK_ID_ + uavVideoFrameMsg.getId();
        String extractionTaskDataStr = RedisUtil.get(cacheKey, RedisDBType.PRIMARY);
        UavVideoFrameMsg extractionTaskData = null;

        if (StringUtil.isEmpty(extractionTaskDataStr)) {
            try {
                extractionTaskData = uavTaskMapper.qryExtractionTaskData(uavVideoFrameMsg.getId());
            } catch (Exception e) {
                log.error("查询抽帧任务数据失败，ID:{}", uavVideoFrameMsg.getId(), e);
            }
        } else {
            try {
                extractionTaskData = JSON.parseObject(
                        extractionTaskDataStr,
                        new TypeToken<UavVideoFrameMsg>(){}.getType()
                );
            } catch (Exception e) {
                log.error("反序列化抽帧任务数据失败，缓存键:{}", cacheKey, e);
                // 反序列化失败时，回退到数据库查询
                try {
                    extractionTaskData = uavTaskMapper.qryExtractionTaskData(uavVideoFrameMsg.getId());
                } catch (Exception dbException) {
                    log.error("数据库查询抽帧任务数据也失败，ID:{}", uavVideoFrameMsg.getId(), dbException);
                }
            }
        }

        if (Objects.nonNull(extractionTaskData)) {
            uavVideoFrameMsg.setIndustryCode(extractionTaskData.getIndustryCode());
            uavVideoFrameMsg.setTenantId(extractionTaskData.getTenantId());
        }

    }

    /**
     * 插入无人机飞行信息
     *
     * @param
     */
    public void insertUavUfcpInfo(UavFlyReq uavFlyReq) {
        log.info("无人机遥测数据同步:{}", uavFlyReq);
        uavFlyReq.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.UAV_SYNC_UFNC);
        bulkOperations.insert(uavFlyReq);
        bulkOperations.execute();
    }

    /**
     *
     * @param uavFlyReqs
     */
    @Async("threadPoolTaskExecutor")
    public void sendUfcpMsg(UavFlyReq uavFlyReqs) {
        //根据航线id查询任务所在行业
        AirEditDto airEditDto = new AirEditDto();
        airEditDto.setFlightRouteId(uavFlyReqs.getFlightRouteId());
        TelemetryReq telemetryReq = new TelemetryReq();
        telemetryReq.setDeviceCode(uavFlyReqs.getDeviceCode());
        Result result = airLineService.queryUserDeviceOrder(telemetryReq);
        if (result == null || result.getData() == null) {
            log.info("sendUfcpMsg未查询到设备[{}]的订购用户，无需推送消息", uavFlyReqs.getDeviceCode());
            return;
        }
        try {
            log.info("开始计算里程：{}", JSON.toJSONString(uavFlyReqs));
            // 计算预估里程、累计里程（根据经纬度） redis key：任务id
            String taskRecordId = uavFlyReqs.getTaskRecordId();
            // 只有当任务ID存在时才计算里程
            if (StringUtil.isNotEmpty(taskRecordId)) {
                // 查询预估里程（先从Redis获取，如果没有再从数据库查询）
                String airMileage = getAirMileageFromCache(taskRecordId);
                if (StringUtil.isNotEmpty(airMileage)) {
                    uavFlyReqs.setAirMileage(airMileage);
                }
                // 计算累计里程，并仅将结果缓存到 Redis，不再更新数据库实际里程
                String flightStatus = uavFlyReqs.getFlightStatus();
                if (Constants.ONE.equals(flightStatus)) {
                    // 飞行中计算距离
                    String redisKey = CacheConstants.UAV_ACCUMULATE_MILEAGE + taskRecordId;
                    Double accumulateMileage = calculateAccumulateMileage(uavFlyReqs, redisKey);
                    // 设置累计里程到返回对象（保留两位小数，单位：米）
                    if (Objects.nonNull(accumulateMileage)) {
                        uavFlyReqs.setAccumulateMileage(String.format("%.2f", accumulateMileage));
                    }
                } else if (Constants.TWO.equals(flightStatus)) {
                    // 返航中计算距离
                    String redisKey = CacheConstants.UAV_ACCUMULATE_MILEAGE_RETURN + taskRecordId;
                    Double accumulateMileageReturn = calculateAccumulateMileage(uavFlyReqs, redisKey);
                    if (Objects.nonNull(accumulateMileageReturn)) {
                        uavFlyReqs.setAccumulateMileageReturn(String.format("%.2f", accumulateMileageReturn));
                    }
                }
            } else {
                log.info("sendUfcpMsg-任务ID为空，跳过累计里程计算，设备编码：{}", uavFlyReqs.getDeviceCode());
            }
        } catch (Exception e) {
            log.error("计算里程信息报错 {}", e.getLocalizedMessage(), e);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<LinkedHashMap<String, Object>> rawData = (List<LinkedHashMap<String, Object>>) result.getData();
            List<TenantInfoRes> tenantInfoResList = new ArrayList<>();
            for (LinkedHashMap<String, Object> map : rawData) {
                TenantInfoRes tenantInfo = objectMapper.convertValue(map, TenantInfoRes.class);
                tenantInfoResList.add(tenantInfo);
            }
            // 2. 遍历用户，推送WebSocket消息
            tenantInfoResList.stream().forEach(tenantInfo -> {
                String userId = tenantInfo.getUserId();
                String industryCode = tenantInfo.getIndustryCode();
                String uid = userId + WebSocketConstant.LINE + industryCode; // 构建用户唯一标识
                // 获取用户的WebSocket连接通道
                List<Channel> channels = NettyConfig.getUserChannelListMap().get(uid);
                if (channels == null || channels.isEmpty()) {
                    log.debug("sendUfcpMsg-用户[{}]无有效WebSocket连接，跳过推送", uid);
                    return;
                }
                // 给遥测数据补充行业编码
                uavFlyReqs.setIndustryCode(industryCode);

                String message = JSON.toJSONString(uavFlyReqs);
                // 推送消息到所有有效通道
                channels.stream()
                        .filter(Objects::nonNull)
                        .forEach(channel -> {
                            channel.writeAndFlush(new TextWebSocketFrame(message));
                            log.info("无人机遥测数据socket：用户[{}]，消息内容：{}", uid, message);
                        });
            });
        } catch (IllegalArgumentException e) {
            log.error("sendUfcpMsg-TenantInfoRes列表转换失败，设备编码：{}", uavFlyReqs.getDeviceCode(), e);
        } catch (Exception e) {
            log.error("无人机遥测数据socket异常，设备编码：{}", uavFlyReqs.getDeviceCode(), e);
        }
//
//
//
//        Result result = airLineService.getAirLineInfoByFlightRouteId(airEditDto);
//        if (ObjectUtil.isNotEmpty(result)){
//            LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) result.getData();
//            ObjectMapper objectMapper = new ObjectMapper();
//            AirLineVo airLineVo = objectMapper.convertValue(dataMap, AirLineVo.class);
//            uavFlyReqs.setIndustryCode(airLineVo.getIndustryCode());
//            String industryCode = airLineVo.getIndustryCode();
//            List<String> userIds = airLineVo.getUserIds();
//            userIds.stream().forEach(userId -> {
//                String uid = userId + WebSocketConstant.LINE + industryCode;
//                List<Channel> channels = NettyConfig.getUserChannelListMap().get(uid);
//                if (channels != null && !channels.isEmpty()) {
//                    String message = JSON.toJSONString(uavFlyReqs);
//                    channels.stream()
//                            .filter(Objects::nonNull)
//                            .forEach(channel -> {
//                                channel.writeAndFlush(new TextWebSocketFrame(message));
//                                log.info("无人机遥测数据消息发送{}成功:{}", uid, message);
//                            });
//                }
//            });
//        }
    }

    /**
     *
     * @param uavFlyTaskRecordReq
     */
    public void insertUavTaskRecord(UavFlyTaskRecordReq uavFlyTaskRecordReq) {
        log.info("无人机遥测数据同步:{}", uavFlyTaskRecordReq);
        uavFlyTaskRecordReq.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.UAV_SYNC_UFNC_TASK);
        bulkOperations.insert(uavFlyTaskRecordReq);
        bulkOperations.execute();
    }

    /**
     * 插入无人机气溶胶信息
     *
     * @param airQualityData
     */
    public void insertUavUfcpGasInfo(AirQualityData airQualityData) {
        log.info("无人机遥测大气数据同步:{}", airQualityData);
        airQualityData.setCreateTime(com.chinatower.common.core.utils.DateUtils.getTime());
        airQualityData.getLingxiu().setAirData(null);
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, com.chinatower.event.constants.Constants.UFCP_GAS_INFO);
        bulkOperations.insert(airQualityData);
        bulkOperations.execute();
    }

    /**
     * 发送气溶胶信息
     *
     * @param airQualityData
     */
    @Async("threadPoolTaskExecutor")
    public void sendUfcpGasMsg(AirQualityData airQualityData) {
        if (StringUtil.isEmpty(airQualityData.getFlightRouteId())) {
            return;
        }
        //根据航线id查询任务所在行业
        AirEditDto airEditDto = new AirEditDto();
        airEditDto.setFlightRouteId(airQualityData.getFlightRouteId());
        Result result = airLineService.getAirLineInfoByFlightRouteId(airEditDto);
        if (ObjectUtil.isNotEmpty(result)) {
            LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) result.getData();
            ObjectMapper objectMapper = new ObjectMapper();
            AirLineVo airLineVo = objectMapper.convertValue(dataMap, AirLineVo.class);
            airQualityData.setIndustryCode(airLineVo.getIndustryCode());
            String industryCode = airLineVo.getIndustryCode();
            List<String> userIds = airLineVo.getUserIds();
            userIds.stream().forEach(userId -> {
                String uid = userId + WebSocketConstant.LINE + industryCode;
                List<Channel> channels = NettyConfig.getUserChannelListMap().get(uid);
                if (channels != null && !channels.isEmpty()) {
                    String message = JSON.toJSONString(airQualityData);
                    channels.stream()
                            .filter(Objects::nonNull)
                            .forEach(channel -> {
                                channel.writeAndFlush(new TextWebSocketFrame(message));
                                log.info("无人机遥测大气数据消息发送{}成功:{}", uid, message);
                            });
                }
            });
        }
    }

    /**
     * 发送机巢实时信息
     *
     * @param telemetryData
     */
    @Async("threadPoolTaskExecutor")
    public void sendUfcpNestMsg(UavNestInfo.TelemetryData telemetryData) {
        if (telemetryData == null || telemetryData.getDeviceCode() == null) {
            log.info("发送机巢信息失败：入参telemetryData或deviceCode为空");
            return;
        }
        // 1. 调用接口查询用户订购信息（仅查询1次，避免重复调用）
        TelemetryReq telemetryReq = new TelemetryReq();
        telemetryReq.setDeviceCode(telemetryData.getDeviceCode());
        Result result = airLineService.queryUserDeviceOrder(telemetryReq);
        if (result == null || result.getData() == null) {
            log.info("sendUfcpNestMsg未查询到设备[{}]的订购用户，无需推送消息", telemetryData.getDeviceCode());
            return;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<LinkedHashMap<String, Object>> rawData = (List<LinkedHashMap<String, Object>>) result.getData();
            List<TenantInfoRes> tenantInfoResList = new ArrayList<>();
            for (LinkedHashMap<String, Object> map : rawData) {
                TenantInfoRes tenantInfo = objectMapper.convertValue(map, TenantInfoRes.class);
                tenantInfoResList.add(tenantInfo);
            }
            // 2. 遍历用户，推送WebSocket消息
            tenantInfoResList.stream().forEach(tenantInfo -> {
                String userId = tenantInfo.getUserId();
                String industryCode = tenantInfo.getIndustryCode();
                String uid = userId + WebSocketConstant.LINE + industryCode; // 构建用户唯一标识
                // 获取用户的WebSocket连接通道
                List<Channel> channels = NettyConfig.getUserChannelListMap().get(uid);
                if (channels == null || channels.isEmpty()) {
                    log.debug("sendUfcpNestMsg-用户[{}]无有效WebSocket连接，跳过推送", uid);
                    return;
                }
                // 给遥测数据补充行业编码
                telemetryData.setIndustryCode(industryCode);
                String message = JSON.toJSONString(telemetryData);
                // 推送消息到所有有效通道
                channels.stream()
                        .filter(Objects::nonNull)
                        .forEach(channel -> {
                            channel.writeAndFlush(new TextWebSocketFrame(message));
                            log.info("机巢信息推送成功：用户[{}]，消息内容：{}", uid, message);
                        });
            });
        } catch (IllegalArgumentException e) {
            log.error("sendUfcpNestMsg-TenantInfoRes列表转换失败，设备编码：{}", telemetryData.getDeviceCode(), e);
        } catch (Exception e) {
            log.error("发送机巢实时信息异常，设备编码：{}", telemetryData.getDeviceCode(), e);
        }
    }

    public int updateUavNestCode(String uavDeviceCode, String uavNestCode) {
        return uavTaskMapper.updateUavNestCode(uavDeviceCode, uavNestCode);
    }

    public UavNestBriefInfo selectNestInfoByCode(String uavDeviceCode) {
        return uavTaskMapper.selectNestInfoByCode(uavDeviceCode);
    }

    public int updateUavNestPosition(String uavNestCode, String longitude, String latitude, String altitude,
                                     Integer calibration, Date latLonUpdateTime) {
        return uavTaskMapper.updateUavNestPosition(uavNestCode, longitude, latitude, altitude, calibration, latLonUpdateTime);
    }

    /**
     * 无人机卫星数据socket
     *
     * @param satelliteInfo
     */
    @Async("threadPoolTaskExecutor")
    public void sendUfcpSatelliteInfo(SatelliteInfo satelliteInfo) {
        if (satelliteInfo == null || satelliteInfo.getDeviceCode() == null) {
            log.info("无人机卫星数据失败：入参satelliteInfo或deviceCode为空");
            return;
        }
        // 1. 调用接口查询用户订购信息（仅查询1次，避免重复调用）
        TelemetryReq telemetryReq = new TelemetryReq();
        telemetryReq.setDeviceCode(satelliteInfo.getDeviceCode());
        Result result = airLineService.queryUserDeviceOrder(telemetryReq);
        if (result == null || result.getData() == null) {
            log.info("sendUfcpSatelliteInfo未查询到设备[{}]的订购用户，无需推送消息", satelliteInfo.getDeviceCode());
            return;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<LinkedHashMap<String, Object>> rawData = (List<LinkedHashMap<String, Object>>) result.getData();
            List<TenantInfoRes> tenantInfoResList = new ArrayList<>();
            for (LinkedHashMap<String, Object> map : rawData) {
                TenantInfoRes tenantInfo = objectMapper.convertValue(map, TenantInfoRes.class);
                tenantInfoResList.add(tenantInfo);
            }
            // 2. 遍历用户，推送WebSocket消息
            tenantInfoResList.stream().forEach(tenantInfo -> {
                String userId = tenantInfo.getUserId();
                String industryCode = tenantInfo.getIndustryCode();
                String uid = userId + WebSocketConstant.LINE + industryCode; // 构建用户唯一标识
                // 获取用户的WebSocket连接通道
                List<Channel> channels = NettyConfig.getUserChannelListMap().get(uid);
                if (channels == null || channels.isEmpty()) {
                    log.debug("sendUfcpSatelliteInfo-用户[{}]无有效WebSocket连接，跳过推送", uid);
                    return;
                }
                // 给遥测数据补充行业编码
                satelliteInfo.setIndustryCode(industryCode);
                String message = JSON.toJSONString(satelliteInfo);
                // 推送消息到所有有效通道
                channels.stream()
                        .filter(Objects::nonNull)
                        .forEach(channel -> {
                            channel.writeAndFlush(new TextWebSocketFrame(message));
                            log.info("无人机卫星数据socket：用户[{}]，消息内容：{}", uid, message);
                        });
            });
        } catch (IllegalArgumentException e) {
            log.error("sendUfcpSatelliteInfo-TenantInfoRes列表转换失败，设备编码：{}", satelliteInfo.getDeviceCode(), e);
        } catch (Exception e) {
            log.error("无人机卫星数据socket异常，设备编码：{}", satelliteInfo.getDeviceCode(), e);
        }
    }

    /**
     * 从缓存获取预估里程
     * 先从Redis获取，如果没有再从数据库查询并缓存
     *
     * @param taskRecordId 任务ID
     * @return 预估里程（单位：米）
     */
    private String getAirMileageFromCache(String taskRecordId) {
        if (StringUtil.isEmpty(taskRecordId)) {
            return null;
        }

        try {
            // Redis key：任务id
            String redisKey = "uav:air:mileage:" + taskRecordId;

            // 先从Redis获取预估里程
            Object cacheData = RedisUtil.get(redisKey, RedisDBType.PRIMARY);
            if (cacheData != null) {
                String airMileage = String.valueOf(cacheData);
                log.info("任务[{}]从Redis获取预估里程：{}米", taskRecordId, airMileage);
                return airMileage;
            }

            // Redis中没有，从数据库查询
            AirTaskReq airTaskReq = new AirTaskReq();
            airTaskReq.setTaskRecordId(taskRecordId);
            Result<AirLineVo> airLineVoResult = airLineService.queryAirLineByTaskId(airTaskReq);

            if (Objects.nonNull(airLineVoResult.getData())
                    && Objects.nonNull(airLineVoResult.getData().getAirMileage())
                    && StringUtil.isNotEmpty(airLineVoResult.getData().getAirMileage())) {
                String airMileage = airLineVoResult.getData().getAirMileage();

                // 存入Redis，预估里程是固定的，设置较长的过期时间（24小时 = 86400秒）
                RedisUtil.setEx(redisKey, airMileage, 86400L, java.util.concurrent.TimeUnit.SECONDS, RedisDBType.PRIMARY);
                log.info("任务[{}]从数据库查询预估里程并缓存：{}米", taskRecordId, airMileage);
                return airMileage;
            }
        } catch (Exception e) {
            log.error("任务[{}]获取预估里程异常", taskRecordId, e);
        }

        return null;
    }

    /**
     * 计算累计里程
     * 将当前经纬度存到redis里，key为任务id，计算逻辑是下一次循环的时候取上一次存的经纬度和当前经纬度计算距离
     *
     * @param uavFlyReqs 无人机飞行请求对象
     */
    private Double calculateAccumulateMileage(UavFlyReq uavFlyReqs, String redisKey) {
        String taskRecordId = uavFlyReqs.getTaskRecordId();
        String lng = uavFlyReqs.getLng();
        String lat = uavFlyReqs.getLat();

        // 参数校验：任务ID、经纬度必须存在
        if (StringUtil.isEmpty(taskRecordId)) {
            log.info("calculateAccumulateMileage-任务ID为空，无法计算累计里程，任务ID：{}", taskRecordId);
            return null;
        }
        if (StringUtil.isEmpty(lng) || StringUtil.isEmpty(lat)) {
            log.info("calculateAccumulateMileage-经纬度为空，无法计算累计里程，任务ID：{}，lng：{}，lat：{}",
                    taskRecordId, lng, lat);
            return null;
        }

        double accumulateMileage = 0.0;
        try {
            log.info("calculateAccumulateMileage-开始计算累计里程，任务ID：{}，Redis key：{}，当前经纬度：({}, {})",
                    taskRecordId, redisKey, lng, lat);

            // 从Redis获取上一次的经纬度和累计里程
            Object cacheData = RedisUtil.get(redisKey, RedisDBType.PRIMARY);

            if (cacheData != null) {
                try {
                    // 解析上一次存储的数据
                    String cacheStr = String.valueOf(cacheData);
                    JSONObject lastData = JSON.parseObject(cacheStr);
                    String lastLng = lastData.getString("lng");
                    String lastLat = lastData.getString("lat");
                    Double lastAccumulateMileage = lastData.getDouble("accumulateMileage");

                    if (lastAccumulateMileage != null) {
                        accumulateMileage = lastAccumulateMileage;
                    }

                    // 如果存在上一次的经纬度，计算距离并累加
                    if (StringUtil.isNotEmpty(lastLng) && StringUtil.isNotEmpty(lastLat)) {
                        try {
                            // 计算当前经纬度和上一次经纬度的距离（单位：米）
                            double distance = DistanceUtils.calculateDistance(lastLng, lastLat, lng, lat);
                            // 累加累计里程
                            accumulateMileage += distance;
                            log.info("任务[{}]计算距离：{}米，累计里程：{}米，上次经纬度：({}, {})，当前经纬度：({}, {})",
                                    taskRecordId, distance, accumulateMileage, lastLng, lastLat, lng, lat);
                        } catch (Exception e) {
                            log.warn("任务[{}]计算距离失败，经纬度：({}, {}) -> ({}, {})",
                                    taskRecordId, lastLng, lastLat, lng, lat, e);
                        }
                    } else {
                        log.info("任务[{}]Redis中无上一次经纬度，累计里程从0开始", taskRecordId);
                    }
                } catch (Exception e) {
                    log.info("任务[{}]解析Redis缓存数据失败，将重新开始计算累计里程，缓存数据：{}",
                            taskRecordId, cacheData, e);
                }
            } else {
                log.info("任务[{}]Redis中无历史数据，累计里程从0开始", taskRecordId);
            }

            // 将当前经纬度和累计里程存储到Redis，过期时间3小时（10800秒）
            JSONObject currentData = new JSONObject();
            currentData.put("lng", lng);
            currentData.put("lat", lat);
            currentData.put("accumulateMileage", String.format("%.2f", accumulateMileage));
            // 3小时 = 10800秒，使用setEx方法设置过期时间
            RedisUtil.setEx(redisKey, currentData.toJSONString(), 10800L, java.util.concurrent.TimeUnit.SECONDS, RedisDBType.PRIMARY);
            log.info("任务[{}]累计里程计算完成：{}米，已存储到Redis", taskRecordId, accumulateMileage);
        } catch (Exception e) {
            log.error("任务[{}]计算累计里程异常，设备编码：{}", taskRecordId, uavFlyReqs.getDeviceCode(), e);
        }
        return accumulateMileage;
    }
}
