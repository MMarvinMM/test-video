package com.chinatower.platform.consumer.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.chinatower.message.api.RadarMsgServiceAPI;
import com.chinatower.message.api.model.RadarBindUserModel;
import com.chinatower.message.api.model.RadarObject;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.consumer.dao.MongoUtilDAO;
import com.chinatower.platform.consumer.dao.VmOrderSensorDevicesDAO;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chinatower.message.api.model.RadarObject.SOURCE;
import static com.chinatower.message.api.model.RadarObject.SOURCE_TYPE;
import static com.chinatower.message.api.model.RadarObject.TARGET_ID;
import static com.chinatower.platform.consumer.service.RadarMsgHandler.RadarMsgMongoModel.UPDATE_TIMESTAMP;

@Service
@Slf4j
public class RadarMsgHandler {

    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    VmOrderSensorDevicesDAO vmOrderSensorDevicesDAO;
    @Resource
    RadarMsgServiceAPI radarMsgServiceAPI;
    @Resource
    MongoUtilDAO<RadarMsgMongoModel> mongoUtilDAO;

    private static final String PREFIX_LIST = "radarRegisterList";
    private static volatile Map<Object, Object> userInfoMap = new ConcurrentHashMap<>();
    private static volatile Map<String, List<String>> keyMapping = new ConcurrentHashMap<>();
    private static List<RadarObject> radarObjectList = new CopyOnWriteArrayList<>();
    private static final ThreadPoolExecutor MONGO_UPDATED_THREAD_POOL =
            new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000), new ThreadFactory() {
                private final AtomicInteger atomicInteger = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("execute-mongo-thread-" + atomicInteger.getAndAdd(1));
                    return thread;
                }
            }, new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 推送消息 - 调用rpc接口发送消息 </br>
     * map中的内容是  key：tenantId_行业编码
     * value: kafka推送的消息内容
     *
     * @param mapping 消息用户信息映射
     */
    public void doPush(Map<Object, Object> mapping) {
        for (Object userKey : mapping.keySet()) {
            try {
                String uk = (String) userKey;
                List<String> list = keyMapping.get(uk);
                if (CollectionUtils.isEmpty(list)) {
                    return;
                }
                for (String originKey : list) {
                    RadarBindUserModel model = new RadarBindUserModel();
                    model.setRadarObject((RadarObject) mapping.get(uk));
                    model.setUserKey((String) originKey);
                    // 数据 推送到客户端
                    log.info("推送数据到客户端 {}", JSON.toJSONString(model));
                    radarMsgServiceAPI.sendRadarMsg2Client(model);
                }
            } catch (Exception e) {
                log.error("push失败 " + JSON.toJSONString(mapping), e);
            }
        }
    }


    public void addToPersistenceContainer(RadarObject radarObject) {
        List<String> ids = (List<String>) JSONPath.eval(radarObject, "$.sources.ids");
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        radarObjectList.add(radarObject);
    }

    @Scheduled(fixedDelay = 3_000)
    public void persistenceToMongoDBAsyncTask() {
        ListIterator<RadarObject> iterator = radarObjectList.listIterator();
        while (iterator.hasNext()) {
            RadarObject radarObject = iterator.next();
            try {
                analysisAndHandle(radarObject);
            } catch (Exception e) {
                log.info("数据持久化失败", e);
            } finally {
                radarObjectList.remove(radarObject);
            }
        }
    }

    /**
     * 是否数据已经过期
     *
     * @param lastTm 数据时间
     * @return
     */
    private static boolean isExpired(Long lastTm) {
        if (lastTm == null) {
            return true;
        }
        DateTime before3min = DateUtil.offset(DateUtil.date(), DateField.MINUTE, -3);
        return before3min.getTime() > lastTm;
    }

    private void analysisAndHandle(RadarObject radarObject) {
        JSONArray sources = radarObject.getSources();
        if (CollectionUtils.isEmpty(sources)) {
            return;
        }

        Map<String, List<JSONObject>> groupByType = sources.stream()
                .map(it -> ((JSONObject) it))
                .collect(Collectors.groupingBy(it -> (String) JSONPath.eval(it, "$.type")));
        for (String type : groupByType.keySet()) {
            List<JSONObject> idsList = groupByType.get(type);
            for (JSONObject jsonObject : idsList) {
                Long lastTm = radarObject.getLastTm();
                List<String> list = (List<String>) JSONPath.eval(jsonObject, "$.ids");
                for (String id : list) {
                    //超期或删除都直接移除
                    if (isExpired(lastTm) || radarObject.getStatus().equals("DELETED")) {
                        DateTime before3min = DateUtil.offset(DateUtil.date(), DateField.MINUTE, -3);
                        log.info("radarObject {} , calc {} ", JSON.toJSONString(radarObject), before3min.getTime());
                        removeFromMongo(radarObject, type, id);
                    } else {
                        saveOrUpdateToMongo(radarObject, type, id);
                    }
                }
            }
        }
    }

    private void removeFromMongo(RadarObject radarObject, String type, String id) {
        MONGO_UPDATED_THREAD_POOL.execute(() -> {
            try {
                Query query = new Query(Criteria
                        .where(TARGET_ID).is(radarObject.getId())
                        .and(SOURCE).is(id)
                        .and(SOURCE_TYPE).is(type)
                );
                mongoUtilDAO.remove(query, MongoDBType.PRIMARY, RadarMsgMongoModel.COLLECTION);
            } catch (Exception e) {
                log.error("移除数据失败", e);
            }
        });
    }

    private void saveOrUpdateToMongo(RadarObject radarObject, String type, String id) {
        MONGO_UPDATED_THREAD_POOL.execute(() -> {
            try {
                Query query = new Query(Criteria
                        .where(TARGET_ID).is(radarObject.getId())
                        .and(SOURCE).is(id)
                        .and(SOURCE_TYPE).is(type));
                Update update = new Update();
                update.set(TARGET_ID, radarObject.getId());
                update.set("displayId", radarObject.getDisplayId());
                update.set("vesselName", radarObject.getVesselName());
                update.set("mmsi", radarObject.getMmsi());
                update.set("callSign", radarObject.getCallSign());
                update.set("imo", radarObject.getImo());
                update.set("latitude", radarObject.getLatitude());
                update.set("longitude", radarObject.getLongitude());
                update.set("course", radarObject.getCourse());
                update.set("heading", radarObject.getHeading());
                update.set("speed", radarObject.getSpeed());
                update.set("len", radarObject.getLen());
                update.set("wid", radarObject.getWid());
                update.set("maxLen", radarObject.getMaxLen());
                update.set("minLen", radarObject.getMinLen());
                update.set("shiptype", radarObject.getShiptype());
                update.set("sClass", radarObject.getSClass());
                update.set("state", radarObject.getState());
                update.set("lastTm", radarObject.getLastTm());
                update.set("receiveTime", radarObject.getReceiveTime());
                update.set("status", radarObject.getStatus());
                update.set("province", radarObject.getProvince());
                update.set("quality", radarObject.getQuality());
                update.set("period", radarObject.getPeriod());
                update.set("vendorId", radarObject.getVendorId());
                update.set("nationality", radarObject.getNationality());
                update.set("duration", radarObject.getDuration());
                update.set("displacement", radarObject.getDisplacement());
                update.set("idLink", radarObject.getIdLink());
                update.set("accessNode", radarObject.getAccessNode());
                update.set("fixed", radarObject.getFixed());
                update.set("aisBaseInfo", radarObject.getAisBaseInfo());
                update.set("rangeMaxDistance", radarObject.getRangeMaxDistance());
                update.set(SOURCE, id);
                update.set(SOURCE_TYPE, type);
                update.set(UPDATE_TIMESTAMP, DateUtil.date().getTime());

                RadarMsgMongoModel model = new RadarMsgMongoModel();
                BeanUtil.copyProperties(radarObject, model);
                model.setUpdateTimestamp(DateUtil.date().getTime());
                model.setSource(id);
                model.setSourceType(type);
                model.setTargetId(radarObject.getId());
                model.setAisBaseInfo(radarObject.getAisBaseInfo());
                mongoUtilDAO.saveOrUpdate(query, update, RadarMsgMongoModel.COLLECTION, model, MongoDBType.PRIMARY);
            } catch (Exception e) {
                log.error("mongo 数据更新错误", e);
            }
        });
    }

    /**
     * 过滤出来需要推送的数据映射
     * - 最终的结果是一条消息可能会给多个用户推送消息，1 vs 1+的过程
     *
     * @param radarObject kafka中的雷达推送数据
     * @return 推送的消息映射信息，key 是 租户id_行业编码
     */
    public Map<Object, Object> needPushMapping(RadarObject radarObject) {
        if (MapUtils.isEmpty(userInfoMap)) {
            return Collections.EMPTY_MAP;
        }

        //超期移除，不推送
        Long lastTm = radarObject.getLastTm();
        if (isExpired(lastTm)) {
            return Collections.EMPTY_MAP;
        }

        //处理数据映射
        Set<Object> keySet = userInfoMap.keySet();
        Set<String> tenantKeySet = new HashSet<>();
        for (Object o : keySet) {
            String key = (String) o;
            tenantKeySet.add(tenantKey(key));
        }


        List<String> ids = (List<String>) JSONPath.eval(radarObject, "$.sources.ids");

        if (CollectionUtils.isEmpty(ids)) {
            return Collections.EMPTY_MAP;
        }
        return tenantKeySet.stream()
                .filter(it -> {
                    List<String> list = CACHE.getUnchecked(it);
                    if (CollectionUtils.isNotEmpty(list)
                            && Sets.intersection(Sets.newHashSet(list), Sets.newHashSet(ids)).size() > 0) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toMap(Function.identity(), it -> radarObject));
    }

    private static String tenantKey(String it) {
        String[] split = it.split(":");
        String tenantId = split[0];
        String industryCode = split[2];
        return tenantId + "_" + industryCode;
    }

    /**
     * 自动刷新redis中的数据到jvm中，定时更新，同时将已经过期的数据排除在外
     */
    @Scheduled(fixedRate = 3_000, initialDelay = 10_000)
    public void fetchFromRedis() {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(PREFIX_LIST);
        log.info("fetchFromRedis read data map = {}", map);

        Iterator<Map.Entry<Object, Object>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            Object key = entry.getKey();
            Object value = entry.getValue();

            removeExpiredData(iterator, key, value);

        }
        userInfoMap.clear();
        userInfoMap.putAll(map);
        Map<String, List<String>> newMap = new HashMap<>();
        for (Object key : userInfoMap.keySet()) {
            String tenantKey = tenantKey((String) key);
            if (newMap.containsKey(tenantKey)) {
                newMap.get(tenantKey).add((String) key);
            } else {
                List<String> list = new ArrayList<>();
                list.add((String) key);
                newMap.put(tenantKey, list);
            }
        }
        keyMapping.clear();
        keyMapping.putAll(newMap);
    }

    /**
     * 如果发现redis中的数据已经过期了，那就在遍历过程中删除它 <br>
     * redis中的数据结构如下：
     * <li>hash key：租户id:用户id:行业编码</li>
     * <li>hash value：过期时刻的时间戳</li>
     *
     * @param iterator 迭代器
     * @param key      hash key
     * @param value    hash value
     */
    private void removeExpiredData(Iterator<Map.Entry<Object, Object>> iterator, Object key, Object value) {
        try {
            if (value == null || ((long) value) <= DateUtil.date().getTime()) {
                iterator.remove();
                redisTemplate.opsForHash().delete(PREFIX_LIST, key);
            }
        } catch (Exception e) {
            log.error("过期数据淘汰失败", e);
        }
    }

    /**
     * 自动刷新缓存中的用户的订购信息映射
     */
    @Scheduled(fixedRate = 60_0_000, initialDelay = 10_0_000)
    public void freshOrderInfo() {
        Iterator<Map.Entry<Object, Object>> iterator = userInfoMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            // 租户id:用户名:行业编码
            Object key = entry.getKey();
            //过期的时刻的时间戳
            Object value = entry.getValue();
            if (((long) value) <= DateUtil.date().getTime()) {
                //已过期
                iterator.remove();
                CACHE.invalidate(key);
                continue;
            }
            CACHE.refresh(((String) key));
        }
    }


    /**
     * 用户订购雷达设备的 cache 配置
     * <li>最大缓存数量 1000</li>
     * <li>最长10分钟未访问 自动过期</li>
     * <li>写入后10分钟失效</li>
     * <li>记录统计数据</li>
     * 如果数据失效了，再访问就会触发 <code> com.google.common.cache.CacheLoader#load(java.lang.Object) </code>方法的执行
     */
    private final LoadingCache<String, List<String>> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) throws Exception {
                    log.info("cache load key = {}", key);
                    String[] split = key.split("_");
                    if (split.length == 1) {
                        return Collections.emptyList();
                    }
                    String tenantId = split[0];
                    String industryCode = split[1];
                    List<String> list = vmOrderSensorDevicesDAO.seleceDeviceCodeListByTenantId(tenantId, industryCode);
                    if (CollectionUtils.isEmpty(list)) {
                        return Collections.emptyList();
                    }
                    return list;
                }
            });

    public static class RadarMsgMongoModel extends RadarObject {
        public static final String COLLECTION = "radar_msg_latest";
        public static final String UPDATE_TIMESTAMP = "updateTimestamp";
        @Getter
        @Setter
        private Long updateTimestamp;
        @Getter
        @Setter
        private String source;
        @Getter
        @Setter
        private String sourceType;
        @Getter
        @Setter
        private String targetId;
    }
}