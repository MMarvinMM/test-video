package com.chinatower.common.connect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@SuppressWarnings("all")
public class NetConnectUtils {

    @Value("${video-video.url:http://10.43.82.110:9994}")
    private String ipPort;
    @Resource
    RedisTemplate<String, String> redisTemplate;
    private static final String Prefix = "connectUrl:";
    public static final String CASCADE_FROM_CODE = "X-Cascade-From-Code";
    public static final String CASCADE_TO_CODE = "X-Cascade-To-Code";

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(100, 100, TimeUnit.SECONDS))
            .build();

    static final Pattern pattern = Pattern.compile("^\\{(\\w)+\\}$");

    @SafeVarargs
    public final <T extends RequestParamter<U>, U, O> O post(T body, Class<O> outputType, HeaderPair<String, String>... headerPair) throws Exception {
        String urlTemplate = getUrlTemplate(body.getPath().url);

        String realUrl = realUrl(urlTemplate, body.getPath());
        try {
            String data = dataResponse(JSON.toJSONString(body.getBody()), realUrl, headerPair);
            //通过级联网关调用转发返回报文格式处理
            data = cascadeForward(data, headerPair);
            log.info("解析后的data:{}", JSON.toJSONString(data));
            O instance = ReflectUtil.newInstance(outputType);
            BeanUtil.fillBeanWithMap(JSON.parseObject(data), instance, false);
            return instance;
        }
        catch (Exception e) {
            log.error("http 请求发生异常", e);
            throw e;
        }
    }

    @SafeVarargs
    public final <T extends RequestParamter<U>, U, O> O postTjh(T body, Class<O> outputType, HeaderPair<String, String>... headerPair) throws Exception {
        String urlTemplate = this.getUrlTemplate(body.getPath().url);
        String realUrl = this.realUrl(urlTemplate, body.getPath());

        try {
            String data = dataResponse(JSON.toJSONString(body.getBody()), realUrl, headerPair);
            data = this.cascadeForwardTjh(data, headerPair);
            log.info("解析后的data:{}", JSON.toJSONString(data));
            O instance = ReflectUtil.newInstance(outputType, new Object[0]);
            BeanUtil.fillBeanWithMap(JSON.parseObject(data), instance, false);
            return instance;
        } catch (Exception var8) {
            log.error("http 请求发生异常", var8);
            throw var8;
        }
    }

    @SafeVarargs
    public final <T extends RequestParamter<U>, U, O> O getTjh(T body, Class<O> outputType, HeaderPair<String, String>... headerPair) throws Exception {
        String urlTemplate = this.getUrlTemplate(body.getPath().url);
        String realUrl = this.realUrl(urlTemplate, body.getPath());

        try {
            String data = dataResponseGet(realUrl, headerPair);
            data = this.cascadeForwardTjh(data, headerPair);
            log.info("解析后的data:{}", JSON.toJSONString(data));
            O instance = ReflectUtil.newInstance(outputType, new Object[0]);
            BeanUtil.fillBeanWithMap(JSON.parseObject(data), instance, false);
            return instance;
        } catch (Exception var8) {
            log.error("http 请求发生异常", var8);
            throw var8;
        }
    }

    @SafeVarargs
    private static <T extends RequestParamter<U>, U> String recordDataResponse(String body, String realUrl, Pair<String, String>... headerPair) throws IOException {
        Map<String, String> header = rcordHeader();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it ->
                            header.put(it.getKey(), it.getValue())
                    );
        }
        Request request = new Request.Builder()
                .headers(Headers.of(header))
                .post(RequestBody.create(MediaType.parse("application/json"), body))
                .url(realUrl)
                .build();
        log.info("发送网络请求 : realUrl = {} , header = {} , body = {}", realUrl, header, body);
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.body() != null) {
                String string = response.body().string();
                log.error("response body = {}", string);
            }
            throw new ConnectException("http 请求错误 code = " + response.code());
        }

        if (response.body() == null) {
            throw new ConnectException("http 请求错误 body 为空");
        }
        String string = response.body().string();
        log.info("发送网络请求 : realUrl = {} , header = {} , body = {} , str = {}", realUrl, header, body, string);
        return string;
    }

    public enum HeaderKeyEnum {
        X_Token() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Token", value);
            }
        },
        X_CustomerCode() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-CustomerCode", value);
            }
        },
        X_AppCode() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-AppCode", value);
            }
        },
        X_UserCode() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-UserCode", value);
            }
        },
        X_TowerCode() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-TowerCode", value);
            }
        },
        X_OrderCode() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-OrderCode", value);
            }
        },
        X_LOCK_TOKEN() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-LockToken", value);
            }
        },
        X_DURATION() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Duration", value);
            }
        },
        X_PlugId() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-PlugId", value);
            }
        },
        X_AppKey() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-AppKey", value);
            }
        },
        X_ServerCode() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-ServerCode", value);
            }
        },
        X_Cascade_From_Code() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from(CASCADE_FROM_CODE, value);
            }
        },
        X_Cascade_To_Code() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from(CASCADE_TO_CODE, value);
            }
        },
        X_Cascade_To_App() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Cascade-To-App", value);
            }
        },

        X_Cascade_Appkey() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Cascade-Appkey", value);
            }
        },

        X_Cascade_RequestTime() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Cascade-RequestTime", value);
            }
        },

        X_Cascade_RequestID() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Cascade-RequestID", value);
            }
        },

        X_Cascade_Signature() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Cascade-Signature", value);
            }
        },

        X_Cascade_Method() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Cascade-Method", value);
            }
        },

        X_Cascade_To_Path() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Cascade-To-Path", value);
            }
        },
        X_level() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-level", value);
            }
        },

        X_StreamUuid() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-StreamUuid", value);
            }
        },

        X_FrontStreamUuid() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-FrontStreamUuid", value);
            }
        },
        X_MsgId() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-MsgId", value);
            }
        },
        X_RequsetTime() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-RequestTime", value);
            }
        },
        X_Signature() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Signature", value);
            }
        },

        X_NetSrcAddr() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-NetSrcAddr", value);
            }
        },

        Authorization() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("Authorization", value);
            }
        },

        SerialNo() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("serialNo", value);
            }
        },

        TimeStamp() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("timeStamp", value);
            }
        },

        ReqType() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("reqType", value);
            }
        },
        EncryptedData() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Encrypted-Data", value);
            }
        },
        EncryptionIv() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Encryption-Iv", value);
            }
        },
        X_Gateway_UserName() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Gateway-UserName", value);
            }
        },
        X_Gateway_RequestTime() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Gateway-RequestTime", value);
            }
        },
        X_Gateway_Signature() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("X-Gateway-Signature", value);
            }
        },
        api_key() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("api-key", value);
            }
        },

        PlateformCode() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("plateformCode", value);
            }
        },

        Semp_AppKey() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("semp-appKey", value);
            }
        },

        Semp_MsgId() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("semp-msgId", value);
            }
        },

        Semp_RequestTime() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("semp-requestTime", value);
            }
        },

        Semp_Signature() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("semp-signature", value);
            }
        },


        Semp_AppCode() {
            @Override
            public HeaderPair<String, String> of(String value) {
                return HeaderPair.from("semp-appCode", value);
            }
        },
        ;

        public abstract HeaderPair<String, String> of(String value);
    }

    public static class HeaderPair<K, V> extends Pair<K, V> {
        private HeaderPair(K key, V value) {
            super(key, value);
        }

        private static <K, V> HeaderPair<K, V> from(K key, V value) {
            return new HeaderPair<>(key, value);
        }
    }

    private <T extends RequestParamter<U>, U> String getUrlTemplate(RequestParamter.URLMappingEnum body) {
        String urlTemplate = redisTemplate.opsForValue().get(Prefix + body.mapping);
        if (urlTemplate == null) {
            throw new ConnectException("没有找到url映射");
        }
        return urlTemplate;
    }

    @SafeVarargs
    private static <T extends RequestParamter<U>, U> String dataResponse(String body, String realUrl, Pair<String, String>... headerPair) throws IOException {
        Map<String, String> header = header();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it ->
                            header.put(it.getKey(), it.getValue())
                    );
        }
        Request request = new Request.Builder()
                .headers(Headers.of(header))
                .post(RequestBody.create(MediaType.parse("application/json"), body))
                .url(realUrl)
                .build();
        log.info("发送网络请求 : realUrl = {} , header = {} , body = {}", realUrl, header, body);
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.body() != null) {
                String string = response.body().string();
                log.error("response body = {}", string);
            }
            throw new ConnectException("http 请求错误 code = " + response.code());
        }

        if (response.body() == null) {
            throw new ConnectException("http 请求错误 body 为空");
        }
        String string = response.body().string();
        log.info("发送网络请求 : realUrl = {} , header = {} , body = {} , str = {}", realUrl, header, body, string);
        return string;
    }

    @SafeVarargs
    private static <T extends RequestParamter<U>, U> String dataResponseGet(String realUrl, Pair<String, String>... headerPair) throws IOException {
        Map<String, String> header = header();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it ->
                            header.put(it.getKey(), it.getValue())
                    );
        }
        Request request = new Request.Builder()
                .headers(Headers.of(header))
                .get()
                .url(realUrl)
                .build();
        log.info("发送网络请求 : realUrl = {} , header = {} , body = {}", realUrl, header);
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.body() != null) {
                String string = response.body().string();
                log.error("response body = {}", string);
            }
            throw new ConnectException("http 请求错误 code = " + response.code());
        }

        if (response.body() == null) {
            throw new ConnectException("http 请求错误 body 为空");
        }
        String string = response.body().string();
        log.info("发送网络请求 : realUrl = {} , header = {} , str = {}", realUrl, header, string);
        return string;
    }

    private static Map<String, String> rcordHeader() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("Accept", "application/json;charset=UTF-8");
        return headerMap;
    }


    private String realUrl(String urlTemplate, RequestParamter.Path path) {
        String[] split = urlTemplate.split("/");
        List<String> parts = new ArrayList<>();
        for (String s : split) {
            Matcher matcher = pattern.matcher(s);
            boolean match = matcher.matches();
            if (match) {
                String fname = s.replace("{", "").replace("}", "");
                Object fieldValue = null;
                try {
                    fieldValue = ReflectUtil.getFieldValue(path, fname);
                }
                catch (Exception e) {
                    throw new ConnectException("路径中 " + fname + " 没有对应的值可以填充");
                }
                if (fieldValue == null) {
                    throw new ConnectException("路径中 " + fname + " 没有对应的值可以填充");
                }
                parts.add(fieldValue.toString());
                continue;
            }
            parts.add(s);
        }
        StringBuilder stringBuilder = new StringBuilder(ipPort);
        for (String part : parts) {
            stringBuilder.append(part).append("/");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    private static Map<String, String> header() {
        String msgId = UUID.randomUUID().toString(true);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json;charset=UTF-8");
        headerMap.put("Accept", "application/json;charset=UTF-8");
        headerMap.put("X-MsgId", msgId);
        headerMap.put("X-RequestTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        return headerMap;
    }

    @SafeVarargs
    public final <T extends RequestParamter<U>, U, O> O recordPost(T body, Class<O> outputType, HeaderPair<String, String>... headerPair) throws Exception {
        String urlTemplate = getUrlTemplate(body.getPath().url);

        String realUrl = realUrl(urlTemplate, body.getPath());
        try {
            String data = recordDataResponse(JSON.toJSONString(body.getBody()), realUrl, headerPair);
            //通过级联网关调用转发返回报文格式处理
            data = cascadeForward(data, headerPair);
            log.info("解析后的data:{}", JSON.toJSONString(data));
            O instance = ReflectUtil.newInstance(outputType);
            BeanUtil.fillBeanWithMap(JSON.parseObject(data), instance, false);
            return instance;
        }
        catch (Exception e) {
            log.error("http 请求发生异常", e);
            throw e;
        }
    }


    static class UUID implements java.io.Serializable, Comparable<UUID> {
        private final long mostSigBits;

        /**
         * 此UUID的最低64有效位
         */
        private final long leastSigBits;

        public UUID(long mostSigBits, long leastSigBits) {
            this.mostSigBits = mostSigBits;
            this.leastSigBits = leastSigBits;
        }

        private UUID(byte[] data) {
            long msb = 0;
            long lsb = 0;
            assert data.length == 16 : "data must be 16 bytes in length";
            for (int i = 0; i < 8; i++) {
                msb = (msb << 8) | (data[i] & 0xff);
            }
            for (int i = 8; i < 16; i++) {
                lsb = (lsb << 8) | (data[i] & 0xff);
            }
            this.mostSigBits = msb;
            this.leastSigBits = lsb;
        }

        public static UUID randomUUID() {
            return randomUUID(true);
        }

        private static class Holder {
            static final SecureRandom numberGenerator = getSecureRandom();
        }

        public static SecureRandom getSecureRandom() {
            try {
                return SecureRandom.getInstance("SHA1PRNG");
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public static ThreadLocalRandom getRandom() {
            return ThreadLocalRandom.current();
        }

        public static UUID randomUUID(boolean isSecure) {
            final Random ng = isSecure ? Holder.numberGenerator : getRandom();

            byte[] randomBytes = new byte[16];
            ng.nextBytes(randomBytes);
            randomBytes[6] &= 0x0f; /* clear version */
            randomBytes[6] |= 0x40; /* set to version 4 */
            randomBytes[8] &= 0x3f; /* clear variant */
            randomBytes[8] |= 0x80; /* set to IETF variant */
            return new UUID(randomBytes);
        }


        @Override
        public int compareTo(UUID val) {
            return (this.mostSigBits < val.mostSigBits ? -1 :
                    (this.mostSigBits > val.mostSigBits ? 1 :
                            (this.leastSigBits < val.leastSigBits ? -1 :
                                    (this.leastSigBits > val.leastSigBits ? 1 :
                                            0))));
        }

        @Override
        public String toString() {
            return toString(false);
        }

        public String toString(boolean isSimple) {
            final StringBuilder builder = new StringBuilder(isSimple ? 32 : 36);
            // time_low
            builder.append(digits(mostSigBits >> 32, 8));
            if (false == isSimple) {
                builder.append('-');
            }
            // time_mid
            builder.append(digits(mostSigBits >> 16, 4));
            if (false == isSimple) {
                builder.append('-');
            }
            // time_high_and_version
            builder.append(digits(mostSigBits, 4));
            if (false == isSimple) {
                builder.append('-');
            }
            // variant_and_sequence
            builder.append(digits(leastSigBits >> 48, 4));
            if (false == isSimple) {
                builder.append('-');
            }
            // node
            builder.append(digits(leastSigBits, 12));

            return builder.toString();
        }

        private static String digits(long val, int digits) {
            long hi = 1L << (digits * 4);
            return Long.toHexString(hi | (val & (hi - 1))).substring(1);
        }
    }

    @Data
    public static class RequestParamter<T> {
        /**
         * 路径参数 ：该path类型用于处理 /abc/{deviceCode}/{channelCode}/{controlType}类型的路径
         * 如果path 不能够满足这个条件，请自行继承 SingleRequestBody，重写path类 即可
         */
        private Path path;
        private T body;

        public enum URLMappingEnum {
            TEXT_DETAIL("textGet"),
            AUDIO_DETAIL("audioGet"),
            DELETE_AUDIO("deleteAudio"),
            UPLOAD_AUDIO("/smartbc/audio/upload/one"),
            TXT_AUDITION("audition"),
            DO_LOCK_OR_RELEASE_PTZ("doLockOrReleasePtz"),
            DO_PTZ_CMDS("doPtzCmds"),
            QUERY_PTZ_POSITION("queryPtzPosition"),
            DO_PTZ_LOCATION_CMD("doPtzLocationCmd"),
            DO_CAMERA_FOCUS_OR_ZOOM("doCameraFocusOrZoom"),
            DO_CONTROL_PRESET("doControlPreset"),
            ADD_CAMERA_PRESET("addCameraPreset"),
            DEL_CAMERA_PRESET("delCameraPreset"),
            BATCH_DEL_CAMERA_PRESET("batchDelCameraPreset"),
            UPDATE_PRESET_INFO("updatePresetInfo"),
            QUERY_PRESET_LIST("queryPresetList"),
            DO_SET_GUARD("doSetGuard"),
            ADD_CRUISE_PATROL("addCruisePatrol"),
            DEL_PATROL("delPatrol"),
            BATCH_DEL_PATROL("batchDelPatrol"),
            UPDATE_CRUISE_PATROL("updateCruisePatrol"),
            QUERY_PATROL("queryPatrol"),
            DO_PATROL("doPatrol"),
            BATCH_START_OR_END_PATROL("batchStartOrEndPatrol"),
            QUERY_GUARD_LIST("queryGuardList"),
            QUERY_VIDEO_LIST("queryVideoList"),
            GET_VIDEO_PLAYBACK_URL("getVideoPlaybackUrl"),
            THIRD_LOCATION("thirdLocation"),
            GET_VIDEO_REALTIME_URL("getVideoRealtimeUrl"),
            GET_AUDIO_CHANNEL("getAudioChannel"),
            DO_SWITCH_LIGHT("doSwitchLight"),
            PLAY_BACK_CONTROL("playBackControl"),
            SNAPSHOT("snapshot"),
            SNAPSHOT_BY_NODE("snapshotByNode"),
            SYNC_SNAPSHOT("syncSnapshot"),
            GET_VIDEO_REALTIME_URLS("getVideoRealtimeUrls"),
            ADD_RECORD_PLAN("addRecordPlan"),
            UPDATE_RECORD_PLAN("updateRecordPlan"),
            BATCH_START_OR_END_RECORD_PLAN("batchStartOrEndRecordPlan"),
            DELETE_RECORD_PLAN("deleteRecordPlan"),
            BATCH_DELETE_RECORD_PLAN("batchDeleteRecordPlan"),
            QUERY_RECORD_PLAN("queryRecordPlan"),
            DO_SET_CHANNEL_PARAM("doSetChannelParam"),
            GET_ALARM_INFO("getAlarmInfo"),
            DEL_DEVICES("delDevices"),
            QUERY_DEVICES("queryDevices"),
            CVR_ALLOCATE_ADD("cvrAllocateAdd"),
            CVR_ALLOCATE_UPDATE("cvrAllocateUpdate"),
            CVR_ALLOCATE_DELETE("cvrAllocateDelete"),
            SYNC_SENSOR_INFO("syncSensorInfo"),
            ADD_ALARM_LINKAGE_LIST("addAlarmLinkageList"),
            UPDATE_ALARM_LINKAGE_LIST("updateAlarmLinkageList"),
            DEL_ALARM_LINKAGE_LIST("delAlarmLinkageList"),

            TARGET_FOLLOW("targetFollow"),

            TARGET_FOLLOW_RELAY("targetFollowRelay"),

            TARGET_FOLLOW_LOOK_TO("targetFollowLookTo"),
            SYNCH_ALGORITHM_CONFIG_STATUS("synchAlgorithmConfigStatus"),
            SYNCH_DEVICE_ALGORITHMS("synchDeviceAlgorithms"),
            QUERY_DEVICE_BY_CHANNEL_CODE("queryDeviceByChannelCode"),

            ALLOCATION("allocation"),

            DELETE_CASCADE("deleteCascade"),
            RADAR_ALARM_SYNC("radarAlarmSync"),
            SERVICE_NAME_GET_ALARM_INFOL("getAlarmInfo"),
            DOLINKAGE("doLinkage"),
            ADD_LOCK("addHornLock"),
            START_PLAY("startHornPlay"),
            RELEASE_LOCK("releaseHornLock"),
            STOP_PLAY("stopPlay"),

            START_PLAY_V2("startPlayV2"),
            STOP_PLAY_V2("stopPlayV2"),
            HEARTBEAT_V2("heartbeatV2"),

            START_PLAY_APP_V2("startPlayAPPV2"),
            STOP_PLAY_APP_V2("stopPlayAPPV2"),

            ADD_HORN_DEVICE("addHornDevice"),
            UPDATE_HORN_DEVICE("updateHornDevice"),
            DELETE_HORN_DEVICE("deleteHornDevice"),

            ADD_TIMING("addTiming"),
            UPDATE_TIMING("updateTiming"),
            DELETE_TIMING("batchDelete"),
            ADD_TEXT("addText"),
            DELETE_TEXT("deleteText"),
            STOP_TASKT("stopTask"),
            ADD_NODE_TIMING("addNodeTiming"),

            //运管全量同步
            ALL_UAV_MODEL("allUavModel"),
            ALL_UAV_NEST_MODEL("allUavNestModel"),
            ALL_UAV_CAMERA_MODEL("allUavCameraModel"),
            ALL_UAV_MOUNT_MODEL("allUavMountModel"),
            GET_POSITION_INFO("getPositionInfo"),

            AUDIO_UPLOAD("audioUpload"),
            AUDITION("auditionV2"),
            SYN_DEVICE_CONTROL("synDeviceControl"),
            SYN_CASCADE_PLATFORM("synCascadePlatform"),
            QUERY_RELAY_DEVICE("queryRelayDevice"),
            QUERY_RADAR_REVIEW_INFO("queryRadarReviewInfo"),
            QUERY_RADAR_TRACK_INFO("queryRadarTrackInfo"),

            QUERY_AIS_SEARCH("queryAisSearch"),
            QUERY_AIS_DETAILS("queryAisDetails"),
            TRAJECTORY_TRACK("queryTrajectoryTrack"),
            TRAJECTORY_TRACKPOINT("queryTrajectoryTrackPoint"),
            TRAJECTORY_TRACKPOINTCHARTQUERY("trackPointChartQuery"),
            TRAJECTORY_THERMALMAP("queryTrajectoryThermalMap"),

            QUERY_FIXED_TARGET("queryFixedTarget"),
            UPDATE_FIXED_TARGET("updateFixedTarget"),
            TRAJECTORY_GLOBAL_TRACK("trajectoryGlobalTrack"),
            SHIPPORTRAIT_AIS("shipPortraitAis"),
            SHIPPORTRAIT_RADAR("shipPortraitRadar"),

            GET_TILE_RADAR("getTileRadar"),
            GET_EXTEND_TILE_RADAR("getExtendTileRadar"),

            SYN_CASCADE_SUP_PLATFORM("synCascadeSupPlatform"),

            DOWNLOAD_VIDEO("downloadVideo"),

            STOP_DOWNLOAD_VIDEO("stopDownloadVideo"),
            SYN_ANALYSIS_TASK("synAnalysisTask"),
            SYN_INTELLIGENT_JUDGE_TASK("synIntelligentJudgeTask"),
            ALARM_BY_TASK_BATCH_ID("alarmByTaskBatchId"),
            GET_ALGORITHM_JUDGE_ALARM("getAlgorithmJudgeAlarm"),

            DOWNLOAD_PROCESS("downloadProcess"),

            GET_ENDURANCE_TIME("getEnduranceTime"),
            TERMINATE_TASK("terminateTask"),
            SYNC_ALGORITHMS_REALTIME_TASK("algorithmsRealTimeTask"),
            GET_AIR_DEVICE_INFO("getAirDeviceInfo"),
            PHOTO_VIDEO_DELETE("photoVideoDelete"),
            PHOTO_VIDEO_RENAME("photoVideoRename"),
            SYNC_UAV_AIR_EDIT("syncUavAirEdit"),
            UAV_GET_VIDEO_REALTIMEURL("uavGetVideoRealtimeUrl"),
            UAV_CALIBRATION("uavCalibration"),
            NEST_CHANNELS_GET_VIDEO_REALTIMEURL("nestChannelsGetVideoRealtimeUrl"),
            SET_VIDEO_QUALITY("setVideoQuality"),
            SYNC_UAV_AIR_UPLOAD("syncUavAirUpload"),
            SYNC_UAV_ALGORITHMS_TASK("syncUavAlgorithmsTask"),
            SYNC_UAV_TASK_LIST("syncUavTaskList"),
            SYNC_UAV_TASK_PHOTO("syncUavTaskPhoto"),

            QUERY_PHOTO_VIDEOA_TTR("queryPhotoVideoAttr"),

            QUERY_PHOTO_VIDEO_APP("queryPhotoVideoApp"),
            SYNC_UAV_TASK_VIDEO("syncUavTaskVideo"),

            QUERY_UAV_POINT_INFO("queryUavPointInfo"),
            NEST_LOCATION("nestLocation"),

            UAV_RETURN("uavReturn"),
            UAV_IMMEDIATELY_FLY("uavImmediatelyFly"),
            POINTT_TO_FLY_ON("pointToFlyon"),
            POINTT_TO_FLY_NEW("pointToFlyNew"),
            SYNC_UAV_AIR_DETAIL("syncUavAirDetail"),
            SYNC_UAV_DATA_SOURCE("syncUavDataSource"),
            SYNC_UAV_AIR_HISTORY("syncUavAirHistory"),
            SYNC_UAV_AIR_DELETE("syncUavAirDelete"),
            PLAN_ADD_AND_EDIT("planAddAndEdit"),
            PLAN_DELETE_PLANID("planDeletePlanId"),
            PLAN_TASK_DELETE("planTaskDelete"),
            UAV_CONTINUE_TASK("continueTask"),

            GET_UAV_NEST_STATUS("getUavNestStatus"),
            PLAN_TASK_ON("planTaskOn"),
            PLAN_TASK_OFF("planTaskOff"),

            SAVE_DEVICE("saveDevice"),

            DELETE_DEVICE("deleteDevice"),

            SYNC_UAV_MODEL("syncUavModel"),

            SYNC_NEST_MODEL("syncNestModel"),

            SYNC_UAV_CAMERA_MODEL("syncUavCameraModel"),

            SYNC_UAV_MOUNT_MODEL("syncUavMountModel"),


            SYNC_ORDER_SYSTEM("syncOrderSystem"),
            DO_VIDEO_EXTRACTION("doVideoExtraction"),
            QUERY_PHOTOS("queryPhotos"),
            DEL_EXTRACT_TASK_PHOTOS("delExtractTaskPhotos"),

            TJH_GET_TOKEN("GetToken"),

            TJH_ADD_DEVICE_INFO("AddDeviceInfo"),

            TJH_ADD_DEVICE_INFO_PTZ("AddDeviceInfoWithPtz"),

            QUERY_VIEW_PITCHANGLE("queryViewPitchAngle"),

            TJH_ADD_BY_DEVICECODE("AddByDeviceCode"),

            TJH_GET_STATUS_BY_TASKID("GetStatusByTaskId"),

            DEVICE_INFO_DETAIL("deviceInfoDetail"),

            AIOT_PLATFORM_COMMAND("AiotPlatformCommand"),

            AIOT_DEVICE_COMMAND("AiotDeviceCommand"),

            DEVICE_CENTER_QRY_DEVICE("DeviceCenterQryDevice"),
            DEVICE_CENTER_QRY_DEVICE_LIST("DeviceCenterQryDeviceList"),
            DEVICE_CENTER_GET_DEVICE_STATUS("DeviceCenterGetDeviceStatus"),

            NUM_TWINS_ADD_CONFIG("numTwinsAddConfig"),
            LOGIN_INFO("numTwinsLoginInfo"),
            OFFLINE_USER("numTwinsOfflineUser"),

            RECEIVE_SATELLITE_ALARM("receiveSatelliteAlarm"),
            // 智能任务接口
            AGENT_OPERATION_TASK_LIST("agentOperationTaskList"),
            AGENT_OPERATION_TASK_MODIFY("agentOperationTaskModify"),

            QUERY_LOCALIZED_INFO("queryLocalizedInfo"),

            QUERY_LOCALIZED_VERSION_LIST("queryLocalizedVersionList"),
            QUERY_INDUSTRY_CONFIG_INFO("queryIndustryConfigInfo"),
            QUERY_INDUSTRY_MENU_CONFIG_INFO("queryIndustryMenuConfigInfo"),
            QUERY_INDUSTRY_SELECT("queryIndustrySelect"),

            NETWORK_MANAGEMENT_SYNC_DEVICE("networkManagementSyncDevice"),
            NETWORK_MANAGEMENT_SYNC_DEVICE_CHANNEL("networkManagementSyncDeviceChannel"),

            QUERY_INDUSTRY_MENU_AUTH_INFO("queryIndustryMenuAuthInfo"),

            MOUNT_SPEAKER_SET("mountSpeakerSet"),

            MOUNT_SPEAKER_PLAY("mountSpeakerPlay"),

            MOUNT_SPEAKER_STOP("mountSpeakerStop"),

            MOUNT_SEARCHLIGHT_SET("mountSearchlightSet"),

            MOUNT_SEARCHLIGHT_SWITCH("mountSearchlightSwitch"),

            MOUNT_PLAY_TTS("mountPlayTts"),
            
            /**云台控制*/
            DRC_MODE_ENTER("drcModeEnter"),
            DRC_MODE_EXIT("drcModeExit"),
            SET_RATIO("setRatio"),
            TAKE_PHOTO("takePhoto"),
            GIMBAL_RESET("gimbalReset"),
            PAUSE_MISSION("pauseMission"),
            OPS_GIMBAL("opsGimbal"),
            CAMERA_PHOTO_TAKE("cameraPhotoTake"),
            CAMERA_PHOTO_STOP("cameraPhotoStop"),
            RETURN_HOME("returnHome"),
            RETURN_HOME_CANCEL("returnHomeCancel"),
            RECORDING("recording"),
            CONTINUE_MISSION("continueMission"),
            DRONE_CONTROL("droneControl"),
            CAMERA_SCREEN_DRAG("cameraScreenDrag"),
            CONTROL_GIMBAL("controlGimbal"),
            STOP_PROCESS("stopProcess"),
            CAMERA_ZOOM("cameraZoom"),
            ACQUIRE("acquire")
            ;
            public final String mapping;

            URLMappingEnum(String mapping) {
                this.mapping = mapping;
            }
        }

        @Data
        public static class Path {
            /**
             * 路径 ： redis key 映射
             */
            private URLMappingEnum url;
            /**
             * 航线计划id
             */
            private Integer planId;
            /**
             * 飞行任务ID
             */
            private String taskRecordId;
            /**
             * 设备编码
             */
            private String deviceCode;
            /**
             * 通道编码
             */
            private String channelCode;
            /**
             * 控制类型
             */
            private String controlType;
            /**
             * 任务id
             */
            private String taskId;
            /**
             * 告警事件id
             */
            private String alarmId;
            /**
             * 预置点Id
             */
            private Long presetId;
            /**
             * 广播消息编码
             */
            private String messageId;
            /**
             * 接入节点
             */
            private String accessNode;

            /**
             * photo-图片  video-视频
             */
            private String dataType;

            /**
             * 航点ID
             */
            private String flightRouteId;

            private Integer currentPage;

            private Integer pageSize;

            /**
             * 地图模式 day_b、day_s、day_a、night_b、night_s、night_a、dusk_b、dusk_s dusk_a
             */
            private String node;
        }
    }


    static class ConnectException extends RuntimeException {
        public ConnectException(String message) {
            super(message);
        }
    }


    //对外提供获取真实路径方法
    public <T extends RequestParamter<U>, U, O> String getRealUrl(T body) {
        String urlTemplate = getUrlTemplate(body.getPath().url);
        return realUrlNoIpPort(urlTemplate, body.getPath());
    }

    private String realUrlNoIpPort(String urlTemplate, RequestParamter.Path path) {
        String[] split = urlTemplate.split("/");
        List<String> parts = new ArrayList<>();
        for (String s : split) {
            Matcher matcher = pattern.matcher(s);
            boolean match = matcher.matches();
            if (match) {
                String fname = s.replace("{", "").replace("}", "");
                Object fieldValue = null;
                try {
                    fieldValue = ReflectUtil.getFieldValue(path, fname);
                }
                catch (Exception e) {
                    throw new ConnectException("路径中 " + fname + " 没有对应的值可以填充");
                }
                if (fieldValue == null) {
                    throw new ConnectException("路径中 " + fname + " 没有对应的值可以填充");
                }
                parts.add(fieldValue.toString());
                continue;
            }
            parts.add(s);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String part : parts) {
            stringBuilder.append(part).append("/");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


    /**
     * 通过级联网关调用转发返回报文格式处理
     *
     * @param headerPair
     * @return data
     */
    private String cascadeForward(String data, HeaderPair<String, String>... headerPair) {
        Map<String, String> headerMap = new HashMap<>();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it -> headerMap.put(it.getKey(), it.getValue()));
        }
        if (!StringUtils.isEmpty(headerMap.get(CASCADE_FROM_CODE))
                && !StringUtils.isEmpty(headerMap.get(CASCADE_TO_CODE))
                && !headerMap.get(CASCADE_FROM_CODE).equals(headerMap.get(CASCADE_TO_CODE))) {
            Object resultData = JSON.parseObject(data).get("data");
            if (resultData != null) {
                JSONArray colonyList = JSON.parseArray(String.valueOf(JSON.parseObject(String.valueOf(resultData)).get("colonyList")));
                if (colonyList != null) {
                    if (colonyList.size() == 1) {
                        data = String.valueOf(JSON.parseObject(String.valueOf(colonyList.get(0))).get("result"));
                    }
                    else {
                        JSONArray ja = new JSONArray();
                        for (Object o : colonyList) {
                            JSONObject jsonObject = (JSONObject) JSON.parseObject(String.valueOf(o)).get("result");
                            JSONArray array = (JSONArray) jsonObject.get("data");
                            array.forEach(item -> ja.add(item));
//                    ja.add(JSON.parseObject(String.valueOf(o)).get("result"));
                        }

                        Map<String, Object> res = new HashMap<>();
                        res.put("returnCode", "0000");
                        res.put("returnMsg", "ok");
                        res.put("data", ja);
                        data = JSON.toJSONString(res);
                    }
                }
            }
        }
        return data;
    }

    private String cascadeForwardTjh(String data, HeaderPair<String, String>... headerPair) {
        Map<String, String> headerMap = new HashMap<>();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it -> headerMap.put(it.getKey(), it.getValue()));
        }

        Map<String, Object> res = new HashMap<>();
        if(!StringUtils.isEmpty(data)){
            JSONObject object = JSON.parseObject(data);
            int state = (int)object.get("State");
            String returnCode = String.valueOf(state);
            if(0 == state){
                returnCode = "0000";
            }
            res.put("returnCode", returnCode);
            res.put("returnMsg", String.valueOf(object.get("Message")));
            res.put("errorCode", String.valueOf(object.get("ErrorCode")));
            res.put("data", object.get("Data"));
        }else{
            res.put("returnCode", "8888");
            res.put("returnMsg", "");
            res.put("errorCode", "");
            res.put("data", "");
        }
        data = JSON.toJSONString(res);
        return data;
    }

    @SafeVarargs
    public final <T extends RequestParamter<U>, U, O> O postForm(T body, List<MultipartFile> multipartFiles, Class<O> outputType, HeaderPair<String, String>... headerPair) throws Exception {
        String urlTemplate = getUrlTemplate(body.getPath().url);

        String realUrl = realUrl(urlTemplate, body.getPath());
        try {
            String data = dataResponseForm((Map<String, Object>) JSON.parseObject(JSON.toJSONString(body.getBody()), Map.class), multipartFiles, realUrl, headerPair);
            //通过级联网关调用转发返回报文格式处理
            data = cascadeForward(data, headerPair);
            log.info("解析后的data:{}", JSON.toJSONString(data));
            O instance = ReflectUtil.newInstance(outputType);
            BeanUtil.fillBeanWithMap(JSON.parseObject(data), instance, false);
            return instance;
        }
        catch (Exception e) {
            log.error("http 请求发生异常", e);
            throw e;
        }
    }

    @SafeVarargs
    public final <T extends RequestParamter<U>, U, O> O postFormUpload(T body, MultipartFile kmzFile, Class<O> outputType, HeaderPair<String, String>... headerPair) throws Exception {
        String urlTemplate = getUrlTemplate(body.getPath().url);

        String realUrl = realUrl(urlTemplate, body.getPath());
        try {
            String data = dataResponseFormUpload((Map<String, Object>) JSON.parseObject(JSON.toJSONString(body.getBody()), Map.class), kmzFile, realUrl, headerPair);
            //通过级联网关调用转发返回报文格式处理
            data = cascadeForward(data, headerPair);
            log.info("解析后的data:{}", JSON.toJSONString(data));
            O instance = ReflectUtil.newInstance(outputType);
            BeanUtil.fillBeanWithMap(JSON.parseObject(data), instance, false);
            return instance;
        }
        catch (Exception e) {
            log.error("http 请求发生异常", e);
            throw e;
        }
    }

    @SafeVarargs
    private static <T extends RequestParamter<U>, U> String dataResponseFormUpload(Map<String, Object> params, MultipartFile kmzFile, String realUrl, Pair<String, String>... headerPair) throws IOException {
        Map<String, String> header = header();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it ->
                            header.put(it.getKey(), it.getValue())
                    );
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (!params.isEmpty()) {
            params.forEach((key, value) -> {
                builder.addFormDataPart(key, "" + value);
            });
        }

        if (!Objects.isNull(kmzFile)) {
            builder.addFormDataPart("file", kmzFile.getOriginalFilename(), RequestBody.create(MediaType.parse(kmzFile.getContentType()), kmzFile.getBytes()));
        }

        // 构建请求体
        RequestBody requestBody = builder.build();

        // 构建请求
        Request request = new Request.Builder()
                .headers(Headers.of(header))
                .post(requestBody)
                .url(realUrl)
                .build();

        log.info("发送multipart/form-data网络请求 : realUrl = {} , header = {} , body = {}", realUrl, header, params);
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.body() != null) {
                String string = response.body().string();
                log.error("response body = {}", string);
            }
            throw new ConnectException("http multipart/form-data 请求错误 code = " + response.code());
        }

        if (response.body() == null) {
            throw new ConnectException("http multipart/form-data 请求错误 body 为空");
        }
        String string = response.body().string();
        log.info("multipart/form-data网络请求返回 : str = {}", string);
        return string;
    }

    @SafeVarargs
    private static <T extends RequestParamter<U>, U> String dataResponseForm(Map<String, Object> params, List<MultipartFile> multipartFiles, String realUrl, Pair<String, String>... headerPair) throws IOException {
        Map<String, String> header = header();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it ->
                            header.put(it.getKey(), it.getValue())
                    );
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (!params.isEmpty()) {
            params.forEach((key, value) -> {
                builder.addFormDataPart(key, "" + value);
            });
        }

        if (!Objects.isNull(multipartFiles)) {
            for (MultipartFile multipartFile : multipartFiles) {
                builder.addFormDataPart("multipartFile", multipartFile.getOriginalFilename(), RequestBody.create(MediaType.parse(multipartFile.getContentType()), multipartFile.getBytes()));
            }
        }

        // 构建请求体
        RequestBody requestBody = builder.build();

        // 构建请求
        Request request = new Request.Builder()
                .headers(Headers.of(header))
                .post(requestBody)
                .url(realUrl)
                .build();

        log.info("发送multipart/form-data网络请求 : realUrl = {} , header = {} , body = {}", realUrl, header, params);
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.body() != null) {
                String string = response.body().string();
                log.error("response body = {}", string);
            }
            throw new ConnectException("http multipart/form-data 请求错误 code = " + response.code());
        }

        if (response.body() == null) {
            throw new ConnectException("http multipart/form-data 请求错误 body 为空");
        }
        String string = response.body().string();
        log.info("multipart/form-data网络请求返回 : str = {}", string);
        return string;
    }

    @SafeVarargs
    public final <T extends RequestParamter<U>, U, O> O postQueryString(T body, Class<O> outputType, HeaderPair<String, String>... headerPair) throws Exception {
        String urlTemplate = getUrlTemplate(body.getPath().url);

        String realUrl = realUrl(urlTemplate, body.getPath());
        try {
            String data = dataResponseQueryString((Map<String, Object>) JSON.parseObject(JSON.toJSONString(body.getBody()), Map.class), realUrl, headerPair);
            //通过级联网关调用转发返回报文格式处理
            data = cascadeForward(data, headerPair);
            log.info("解析后的data:{}", JSON.toJSONString(data));
            O instance = ReflectUtil.newInstance(outputType);
            BeanUtil.fillBeanWithMap(JSON.parseObject(data), instance, false);
            return instance;
        }
        catch (Exception e) {
            log.error("http 请求发生异常", e);
            throw e;
        }
    }

    @SafeVarargs
    private static <T extends RequestParamter<U>, U> String dataResponseQueryString(Map<String, Object> params, String realUrl, Pair<String, String>... headerPair) throws IOException {
        Map<String, String> header = header();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it ->
                            header.put(it.getKey(), it.getValue())
                    );
        }
        String content = params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), content);
        // 构建请求
        Request request = new Request.Builder()
                .headers(Headers.of(header))
                .post(requestBody)
                .url(realUrl)
                .build();

        log.info("发送QueryString网络请求 : realUrl = {} , header = {} , body = {}", realUrl, header, params);
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.body() != null) {
                String string = response.body().string();
                log.error("response body = {}", string);
            }
            throw new ConnectException("http QueryString 请求错误 code = " + response.code());
        }

        if (response.body() == null) {
            throw new ConnectException("http QueryString 请求错误 body 为空");
        }
        String string = response.body().string();
        log.info("QueryString网络请求返回 : str = {}", string);
        return string;
    }

    @SafeVarargs
    public final ResponseBody get4Response(NetConnectUtils.RequestParamter body, HeaderPair<String, String>... headerPair) throws Exception {
        String urlTemplate = getUrlTemplate(body.getPath().url);
        return get4Response(body, urlTemplate, headerPair);
    }


    @SafeVarargs
    public final ResponseBody get4Response(NetConnectUtils.RequestParamter body, String urlTemplate, HeaderPair<String, String>... headerPair) throws Exception {
        String realUrl = realUrl(urlTemplate, body.getPath());
        try {
            ResponseBody responseBody = dataResponseGetBody((Map<String, Object>) JSON.parseObject(JSON.toJSONString(body.getBody()), Map.class), realUrl, headerPair);
            log.info("get4Response 解析后的responseBody:{}", JSON.toJSONString(responseBody));
            return responseBody;
        }
        catch (Exception e) {
            log.error("http 请求发生异常", e);
            throw e;
        }
    }

    @SafeVarargs
    private static <T extends RequestParamter<U>, U> ResponseBody dataResponseGetBody(Map<String, Object> params, String realUrl, Pair<String, String>... headerPair) throws IOException {
        Map<String, String> header = header();
        if (ArrayUtil.isNotEmpty(headerPair)) {
            Stream.of(headerPair).filter(it -> !StringUtils.isEmpty(it.getValue()))
                    .forEach(it ->
                            header.put(it.getKey(), it.getValue())
                    );
        }

        String content = params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        realUrl = realUrl + "?" + content;

        Request request = new Request.Builder()
                .headers(Headers.of(header))
                .get()
                .url(realUrl)
                .build();
        log.info("发送网络请求 : realUrl = {} , header = {} , body = {}", realUrl, header);
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.body() != null) {
                String string = response.body().string();
                log.error("response body = {}", string);
            }
            throw new ConnectException("http 请求错误 code = " + response.code());
        }

        if (response.body() == null) {
            throw new ConnectException("http 请求错误 body 为空");
        }
        log.info("发送网络请求 : realUrl = {} , header = {} , str = {}", realUrl, header, response.body().toString());
        return response.body();
    }
}
