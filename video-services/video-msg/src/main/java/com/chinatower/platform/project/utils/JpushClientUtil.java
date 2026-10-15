package com.chinatower.platform.project.utils;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.sdk.api.PushApi;
import cn.jiguang.sdk.bean.push.PushSendParam;
import cn.jiguang.sdk.bean.push.PushSendResult;
import cn.jiguang.sdk.bean.push.batch.BatchPushParam;
import cn.jiguang.sdk.bean.push.batch.BatchPushSendParam;
import cn.jiguang.sdk.bean.push.batch.BatchPushSendResult;
import cn.jiguang.sdk.bean.push.message.custom.CustomMessage;
import cn.jiguang.sdk.bean.push.message.notification.NotificationMessage;
import cn.jiguang.sdk.exception.ApiErrorException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.CIDResult;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.*;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.common.constant.MsgConstants;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.project.entity.request.JpushResDto;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
public class JpushClientUtil {


    @Resource
    private JPushClient jPushClient;

    @Resource
    public JPushClient jPushClient1;

    @Resource
    public JPushClient jPushClient2;

    /**
     * 鸿蒙推送（新SDK）- 上架app
     */
    @Resource
    public PushApi pushApi1;

    /**
     * 鸿蒙推送（新SDK）- 蒲公英
     */
    @Resource
    public PushApi pushApi2;


    /**
     * 上架app
     */
    public Map<String, String> client1ChannelSoundMap = new HashMap<>();

    /**
     * 蒲公英
     */
    public Map<String, String> client2ChannelSoundMap = new HashMap<>();

    public static final String UNDER_LINE = "_";
    public static final String MSG_ID_STR = "msgId";

    public static final String NOTICE_TYPE = "noticeType";

    public static final String MSG_SOUND_STR = "sound";

    public static final String MSG_SOUND_CAF_STR = "sound.caf";

    public static final String INTENT_SL = "intent:#Intent;action=cn.jiguang.uniplugin_jpush.OpenClickActivity;component=cn.chinatower.sl/cn.jiguang.uniplugin_jpush.OpenClickActivity;end";
    public static final String INTENT_NSL = "intent:#Intent;action=cn.jiguang.uniplugin_jpush.OpenClickActivity;component=cn.chinatower.nsl/cn.jiguang.uniplugin_jpush.OpenClickActivity;end";

    /*
      初始化 极光channel id  对应声音
     */

    {
        //上架app
        client1ChannelSoundMap.put("sound0000", "103693");
        client1ChannelSoundMap.put("sound0001", "103694");
        client1ChannelSoundMap.put("sound0002", "103462");
        client1ChannelSoundMap.put("sound0003", "103463");
        client1ChannelSoundMap.put("sound0004", "103464");
        client1ChannelSoundMap.put("sound0005", "103465");
        client1ChannelSoundMap.put("sound0006", "103466");
        client1ChannelSoundMap.put("sound0007", "103467");

        //蒲公英
        client2ChannelSoundMap.put("sound0000", "103710");
        client2ChannelSoundMap.put("sound0001", "103711");
        client2ChannelSoundMap.put("sound0002", "103468");
        client2ChannelSoundMap.put("sound0003", "103469");
        client2ChannelSoundMap.put("sound0004", "103470");
        client2ChannelSoundMap.put("sound0005", "103471");
        client2ChannelSoundMap.put("sound0006", "103472");
        client2ChannelSoundMap.put("sound0007", "103473");
    }


    /**
     * 推送给设备登录后定义别名参数的用户
     *
     * @param alias              极光别名  //可以为单个  也可以为集合  集合每次最多5000
     * @param notification_title 通知内容标题
     * @param msg_title          消息内容标题
     * @param msg_content        消息内容
     * @param extrasparam        扩展字段
     * @return 0推送失败，1推送成功
     */
    public JpushResDto sendToAlias(String alias, String notification_title, String msg_title, String msg_content, String extrasparam, String type) {
        log.info("推送给设备登录后定义别名参数的用户{}", alias);
        int result = 0;
        String msg = "";
        JpushResDto jpushResDto = new JpushResDto();


        //10-31修改为同时发3个应用
        PushResult pushResult1;
        PushResult pushResult2;
        boolean jPushFlag1;
        boolean jPushFlag2;
        try {
            PushPayload pushPayload1 = JpushClientUtil.buildPushObject_android_and_iosByAlias_WithTitle(alias, notification_title,
                    msg_title, msg_content, extrasparam, null, type, INTENT_SL);
            log.info("pushPayload1为{}", pushPayload1.toString());
            pushResult1 = jPushClient1.sendPush(pushPayload1);
            log.info("1 极光请求返回结果{}", pushResult1);
            jPushFlag1 = true;
        } catch (Exception e) {
            log.error("jpush client  推送异常:{}", e);
            msg = "jpush 1: " + e.getMessage();
            jPushFlag1 = false;
        }
        try {
            PushPayload pushPayload2 = JpushClientUtil.buildPushObject_android_and_iosByAlias_WithTitle(alias, notification_title,
                    msg_title, msg_content, extrasparam, null, type, INTENT_NSL);
            log.info("pushPayload2为{}", pushPayload2.toString());

            pushResult2 = jPushClient2.sendPush(pushPayload2);
            log.info("1 极光请求返回结果{}", pushResult2);
            jPushFlag2 = true;
        } catch (Exception e) {
            log.error("jpush client  推送异常:{}", e);
            msg = msg + "jpush 2: " + e.getMessage();
            jPushFlag2 = false;
        }
        if (jPushFlag1 || jPushFlag2) {
            result = 1;
        } else {
            log.info("推送失败");
        }
        jpushResDto.setCode(result);
        jpushResDto.setMsg(msg);
        return jpushResDto;
    }


    /**
     *
     * 极光 批量单推 Api
     *
     * @param pushNews message
     * @return 存mongo 极光推送记录
     */
    public BatchPushResult batchSendNews(List<JgPushRequest> pushNews, List<String> cidListByClient, JPushClient jPushClient) {

       /* {"pushlist":{
            "cid 值 1":{...},
            "cid 值 2":{...},
    ...
        }}
        */
        BatchPushResult batchPushResult = null;
        try {
            //构建推送payload
            List<PushPayload> pushList = buildBatchPushList(pushNews, cidListByClient, jPushClient);
            log.info("pushList:{}", pushList);
            batchPushResult = jPushClient.batchSendPushByAlias(pushList);
        } catch (Exception e) {
            log.error("极光推送 异常{}", e);
            e.printStackTrace();
        }
        return batchPushResult;
    }


    public List<PushPayload> buildBatchPushList(List<JgPushRequest> pushNews, List<String> cidListByClient, JPushClient jPushClient) {

        Map<String, String> soundMap;
        String intentUrl;
        if (jPushClient1.equals(jPushClient)) {
            soundMap = client1ChannelSoundMap;
            intentUrl = INTENT_SL;
        } else {
            soundMap = client2ChannelSoundMap;
            intentUrl = INTENT_NSL;
        }
        log.info("获取channel sound map:{}", soundMap);
        //构建推送payload
        List<PushPayload> pushList = new ArrayList<>();

        for (int i = 0; i < pushNews.size(); i++) {
            JgPushRequest jgPushRequest = pushNews.get(i);
            PushPayload pushPayload;

            if (MsgConstants.JPUSH_MSG_TYPE_NOTIFICATION.equals(jgPushRequest.getMsgType())) {
                //通知类推送
                String channelId = soundMap.getOrDefault(jgPushRequest.getSound(), "");
                pushPayload = buildBatchPush_android_and_iosByAlias_WithTitle(jgPushRequest.getAlias(), jgPushRequest.getNotificationTitle(), jgPushRequest.getMsgTitle(), jgPushRequest.getMsgContent(), jgPushRequest.getExtrasparam(), jgPushRequest.getSound(), channelId, jgPushRequest.getMsgId(), jgPushRequest.getType(), intentUrl);
            } else {
                //自定义消息类推送
                pushPayload = buildPushObject_android_and_ios_alias_message(jgPushRequest.getAlias(), jgPushRequest.getMsgTitle(), jgPushRequest.getMsgContent(), jgPushRequest.getExtrasparam(), jgPushRequest.getMsgId());
            }
            pushPayload.setCid(cidListByClient.get(i));
            pushList.add(pushPayload);
       }
       return pushList;
    }

    public List<String> getCidList(List<JgPushRequest> pushNews, JPushClient jPushClient) {
        //获取cid
        List<String> cidList = new ArrayList<>();
        try {
            if (null == jPushClient) {
                jPushClient = jPushClient1;
            }

            if (1000 < pushNews.size()) {
                double ceil = Math.ceil((double) pushNews.size() / 1000);
                for (int i = 0; i < ceil; i++) {
                    int count;
                    if (pushNews.size() - 1000 * i >= 1000 * i) {
                        count = 1000;
                    } else {
                        count = pushNews.size() - 1000 * i ;
                    }

                    CIDResult push = jPushClient.getCidList(count, "push");
                    cidList.addAll(push.cidlist);
                }
            } else {
                CIDResult push = jPushClient.getCidList(pushNews.size(), "push");
                cidList.addAll(push.cidlist);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return cidList;
    }

    private static PushPayload buildPushObject_android_and_ios_alias_message(String alias, String msg_title, String msg_content, String extrasparam, String msgId) {
        log.info("----------buildPushObject_android_and_ios_alias_message");
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setTarget(alias)
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder()
                        .setMsgContent(StringUtils.isEmpty(msg_content) ? "": msg_content)
                        .setTitle(msg_title)
                        .addExtra("type", extrasparam)
                        .addExtra(MSG_ID_STR, msgId)
                        .build())
                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(true)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        //.setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build()
                )
                .build();
    }

    /**
     * 根据alias推送ios或者安卓
     *
     * @param alias
     * @param notification_title
     * @param msg_title
     * @param msg_content
     * @param extrasparam
     * @return
     */
    private static PushPayload buildPushObject_android_and_iosByAlias_WithTitle(String alias, String notification_title, String msg_title, String msg_content, String extrasparam, String sound, String type, String intentUrl) {
        // 安卓只有 通知标题和消息内容 ios有三个通知标题，消息内容标题，消息内容 考虑到ios 低版本问题现在与安卓统一，传通知标题和消息内容两个
        log.info("----------buildPushObject_android_and_iosByAlias_WithTitle");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段(注意苹果有点坑)
        IosAlert iosAlert = IosAlert.newBuilder().setTitleAndBody(notification_title, null, msg_content).build();

        //增加vivo 单独配置
        Map<String, JsonObject> thirdPartyChannelMap = new HashMap<>();
        JsonObject vivo = new JsonObject();
        vivo.addProperty("classification", 1);
        thirdPartyChannelMap.put("vivo", vivo);

        JsonObject intentJson = new JsonObject();
        intentJson.addProperty("url", intentUrl);

        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(StringUtils.isEmpty(msg_content) ? "": msg_content)
                                .setTitle(notification_title)
                                .addExtra("type", extrasparam)
                                .addExtra(NOTICE_TYPE, StringUtils.isEmpty(type) ? "1": type )
                                .setSound(StringUtils.isEmpty(sound) ? MSG_SOUND_STR : sound)
                                .setIntent(intentJson)
                                .build()
                        )
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(iosAlert)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
//                                .incrBadge(1)
                                //修改为查询现有的未读消息 badge
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound((StringUtils.isEmpty(sound) ? MSG_SOUND_STR : sound) + ".caf")
                                .addExtra("type", extrasparam)
                                .addExtra(NOTICE_TYPE, StringUtils.isEmpty(type) ? "1": type )
                                .setMutableContent(true).build()
                        ).build()
                )
                .setMessage(Message.newBuilder()
                        .setMsgContent(StringUtils.isEmpty(msg_content) ? "": msg_content)
                        .setTitle(notification_title)
                        .addExtra("type", extrasparam)
                        .build())
                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(true)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        //.setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .setThirdPartyChannelV2(thirdPartyChannelMap)
                        .build()
                )
                .build();

    }

    private static PushPayload buildBatchPush_android_and_iosByAlias_WithTitle(String alias, String notification_title, String msg_title, String msg_content, String extrasparam, String sound, String channelId, String msgId, String type, String intentUrl) {
        // 安卓只有 通知标题和消息内容 ios有三个通知标题，消息内容标题，消息内容 考虑到ios 低版本问题现在与安卓统一，传通知标题和消息内容两个
        log.info("----------buildPushObject_android_and_iosByAlias_WithTitle");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段(注意苹果有点坑)
        IosAlert iosAlert = IosAlert.newBuilder().setTitleAndBody(notification_title, null, msg_content).build();

        //增加vivo 单独配置
        Map<String, JsonObject> thirdPartyChannelMap = new HashMap<>();
        JsonObject vivo = new JsonObject();
        JsonObject honor = new JsonObject();
        JsonObject oppo = new JsonObject();
        JsonObject xiaomi = new JsonObject();
        JsonObject huawei = new JsonObject();
        vivo.addProperty("classification", 1);
        if(!StringUtils.isEmpty(channelId)){
            xiaomi.addProperty("channel_id", channelId);
            oppo.addProperty("channel_id", channelId);
        }
        oppo.addProperty("category", "TODO");
        honor.addProperty("importance", "NORMAL");
        huawei.addProperty("importance", "NORMAL");
        huawei.addProperty("category", "WORK");
        thirdPartyChannelMap.put("vivo", vivo);
        thirdPartyChannelMap.put("honor", honor);
        thirdPartyChannelMap.put("oppo", oppo);
        thirdPartyChannelMap.put("xiaomi", xiaomi);
        thirdPartyChannelMap.put("huawei", huawei);

        PushPayload.Builder builder = PushPayload.newBuilder();
        builder.setPlatform(Platform.all())
                .setTarget(alias)
                .setAudience(Audience.alias(alias));
        Notification.Builder notificationBuilder = Notification.newBuilder();
        AndroidNotification android;

        JsonObject intentJson = new JsonObject();
        intentJson.addProperty("url", intentUrl);

        if (StringUtils.isEmpty(channelId) || !alias.contains(UNDER_LINE)) {
            android = AndroidNotification.newBuilder()
                    .setAlert(StringUtils.isEmpty(msg_content) ? "": msg_content)
                    .setTitle(notification_title)
                    .setSound(StringUtils.isEmpty(sound) ? MSG_SOUND_STR : sound)
                    .setIntent(intentJson)
                    .addExtra("type", extrasparam).addExtra(MSG_ID_STR, msgId)
                    .addExtra(NOTICE_TYPE, StringUtils.isEmpty(type) ? "1" : type)
                    .addExtra("third_url_encode", true)
                    .build();
        } else {
            android = AndroidNotification.newBuilder()
                    .setAlert(StringUtils.isEmpty(msg_content) ? "": msg_content)
                    .setTitle(notification_title)
                    .setSound(StringUtils.isEmpty(sound) ? MSG_SOUND_STR : sound)
                    .setChannelId(StringUtils.isEmpty(channelId) ? sound: channelId)
                    .setIntent(intentJson)
                    .addExtra("type", extrasparam)
                    .addExtra(MSG_ID_STR, msgId)
                    .addExtra(NOTICE_TYPE, StringUtils.isEmpty(type) ? "1" : type)
                    .addExtra("third_url_encode", true)
                    .build();
        }
        notificationBuilder.addPlatformNotification(android).addPlatformNotification(IosNotification.newBuilder()
                        //传一个IosAlert对象，指定apns title、title、subtitle等
                        .setAlert(iosAlert)
                        //直接传alert
                        //此项是指定此推送的badge自动加1
//                                .incrBadge(1)
                        //修改为查询现有的未读消息 badge
                        //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                        // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                        .setSound((StringUtils.isEmpty(sound) ? MSG_SOUND_STR : sound) + ".caf")
                        .addExtra("type", extrasparam)
                        .addExtra(MSG_ID_STR, msgId)
                        .addExtra(NOTICE_TYPE, StringUtils.isEmpty(type) ? "1" : type)
                        .setMutableContent(true).build()
        ).build();
        builder.setNotification(notificationBuilder.build());
        builder.setMessage(Message.newBuilder()
                .setMsgContent(StringUtils.isEmpty(msg_content) ? "": msg_content)
                .setTitle(notification_title)
                .addExtra("type", extrasparam)
                .addExtra(MSG_ID_STR, msgId)
                        .addExtra(NOTICE_TYPE, StringUtils.isEmpty(type) ? "1" : type)
                .build())
                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(true)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        //.setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .setThirdPartyChannelV2(thirdPartyChannelMap)
                        .build()
                ).build();
        return builder.build();

    }

    /**
     * 推送给设备标识参数的用户
     *
     * @param registrationId     设备标识
     * @param notification_title 通知内容标题
     * @param msg_title          消息内容标题
     * @param msg_content        消息内容
     * @param extrasparam        扩展字段
     * @return 0推送失败，1推送成功
     */
    public int sendToRegistrationId(String registrationId, String notification_title, String msg_title, String msg_content, String extrasparam) {
        int result = 0;
        try {
            PushPayload pushPayload = JpushClientUtil.buildPushObject_all_registrationId_alertWithTitle(registrationId, notification_title, msg_title, msg_content, extrasparam);
            log.info(pushPayload.toString());
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            log.info(pushResult.toString());
            if (pushResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            log.error(e.getMessage(), e);
        } catch (APIRequestException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 发送给所有安卓用户
     *
     * @param notification_title 通知内容标题
     * @param msg_title          消息内容标题
     * @param msg_content        消息内容
     * @param extrasparam        扩展字段
     * @return 0推送失败，1推送成功
     */
    public int sendToAllAndroid(String notification_title, String msg_title, String msg_content, String extrasparam) {
        int result = 0;
        try {
            PushPayload pushPayload = JpushClientUtil.buildPushObject_android_all_alertWithTitle(notification_title, msg_title, msg_content, extrasparam);
            log.info(pushPayload.toString());
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            log.info(pushResult.toString());
            if (pushResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 发送给所有IOS用户
     *
     * @param notification_title 通知内容标题
     * @param msg_title          消息内容标题
     * @param msg_content        消息内容
     * @param extrasparam        扩展字段
     * @return 0推送失败，1推送成功
     */
    public int sendToAllIos(String notification_title, String msg_title, String msg_content, String extrasparam) {
        int result = 0;
        try {
            PushPayload pushPayload = JpushClientUtil.buildPushObject_ios_all_alertWithTitle(notification_title, msg_title, msg_content, extrasparam);
            log.info(pushPayload.toString());
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            log.info(pushResult.toString());
            if (pushResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * 发送给所有用户
     *
     * @param notification_title 通知内容标题
     * @param msg_title          消息内容标题
     * @param msg_content        消息内容
     * @param extrasparam        扩展字段
     * @return 0推送失败，1推送成功
     */
    public int sendToAll(String notification_title, String msg_title, String msg_content, String extrasparam) {
        int result = 0;
        try {
            PushPayload pushPayload = JpushClientUtil.buildPushObject_android_and_ios(notification_title, msg_title, msg_content, extrasparam);
            log.info(pushPayload.toString());
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            log.info(pushResult.toString());
            if (pushResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }


    /**
     * 推送所有平台
     *
     * @param notification_title
     * @param msg_title
     * @param msg_content
     * @param extrasparam
     * @return
     */
    public static PushPayload buildPushObject_android_and_ios(String notification_title, String msg_title, String msg_content, String extrasparam) {
        IosAlert iosAlert = IosAlert.newBuilder().setTitleAndBody(notification_title, null, msg_content).build();
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(msg_content)
                                .setTitle(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("type", extrasparam)
                                .build()
                        )
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(iosAlert)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(0)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound(MSG_SOUND_CAF_STR)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("type", extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                // .setContentAvailable(true)
                                .build()
                        )
                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(notification_title)
                        .addExtra("type", extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(true)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        //.setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build()
                )
                .build();
    }

    private static PushPayload buildPushObject_all_registrationId_alertWithTitle(String registrationId, String notification_title, String msg_title, String msg_content, String extrasparam) {

        log.info("----------buildPushObject_all_all_alert");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段
        //IosAlert iosAlert =  IosAlert.newBuilder().setTitleAndBody("title", "alert body").build();
        IosAlert iosAlert = IosAlert.newBuilder().setTitleAndBody(notification_title, null, msg_content).build();
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.all())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.registrationId(registrationId))
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(msg_content)
                                .setTitle(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("androidNotification extras key", extrasparam)

                                .build())
                        //指定当前推送的iOS通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(iosAlert)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(0)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound(MSG_SOUND_CAF_STR)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("iosNotification extras key", extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                //取消此注释，消息推送时ios将无法在锁屏情况接收
                                // .setContentAvailable(true)
                                .build())
                        .build())
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(notification_title)
                        .addExtra("type", extrasparam)
                        .build())
                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(true)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        //.setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天；
                        .setTimeToLive(86400)
                        .build())
                .build();

    }

    /**
     * 推送全部安卓
     *
     * @param notification_title
     * @param msg_title
     * @param msg_content
     * @param extrasparam
     * @return
     */
    private static PushPayload buildPushObject_android_all_alertWithTitle(String notification_title, String msg_title, String msg_content, String extrasparam) {
        log.info("----------buildPushObject_android_registrationId_alertWithTitle");
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.android())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(msg_content)
                                .setTitle(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("type", extrasparam)
                                .build())
                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(notification_title)
                        .addExtra("type", extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(true)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        // .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    /**
     * 推送全部ios
     *
     * @param notification_title
     * @param msg_title
     * @param msg_content
     * @param extrasparam
     * @return
     */
    private static PushPayload buildPushObject_ios_all_alertWithTitle(String notification_title, String msg_title, String msg_content, String extrasparam) {
        log.info("----------buildPushObject_ios_registrationId_alertWithTitle");
        IosAlert iosAlert = IosAlert.newBuilder().setTitleAndBody(notification_title, null, msg_content).build();
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.ios())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.all())
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(iosAlert)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(0)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound(MSG_SOUND_CAF_STR)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("iosNotification extras key", extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                // .setContentAvailable(true)

                                .build())
                        .build()
                )
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(notification_title)
                        .addExtra("type", extrasparam)
                        .build())

                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(true)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        //.setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    // ==================== 鸿蒙推送（新SDK jiguang-sdk） ====================

    /**
     * 鸿蒙单推：根据别名推送到鸿蒙设备
     */
    public JpushResDto sendToAliasHmos(String alias, String notificationTitle, String msgTitle,
                                        String msgContent, String extrasparam, String type, String sound, String msgType) {
        log.info("鸿蒙单推-别名:{}", alias);
        JpushResDto jpushResDto = new JpushResDto();
        int result = 0;
        String msg = "";

        boolean pushFlag1 = false;
        boolean pushFlag2 = false;

        try {
            PushSendParam param1 = buildHmosPushPayload(alias, notificationTitle, msgTitle,
                    msgContent, extrasparam, type, INTENT_SL, sound, msgType);
            log.info("鸿蒙pushPayload1为{}", param1);
            PushSendResult pushResult1 = pushApi1.send(param1);
            log.info("鸿蒙 pushApi1 返回结果{}", pushResult1);
            pushFlag1 = true;
        } catch (ApiErrorException e) {
            log.error("鸿蒙 pushApi1 推送异常, code:{}, message:{}", e.getApiError().getError().getCode(),
                    e.getApiError().getError().getMessage());
            msg = "hmos pushApi1: " + e.getApiError().getError().getMessage();
        } catch (Exception e) {
            log.error("鸿蒙 pushApi1 推送异常:{}", e.getMessage());
            msg = "hmos pushApi1: " + e.getMessage();
        }

        try {
            PushSendParam param2 = buildHmosPushPayload(alias, notificationTitle, msgTitle,
                    msgContent, extrasparam, type, INTENT_NSL, sound, msgType);
            log.info("鸿蒙pushPayload2为{}", param2);
            PushSendResult pushResult2 = pushApi2.send(param2);
            log.info("鸿蒙 pushApi2 返回结果{}", pushResult2);
            pushFlag2 = true;
        } catch (ApiErrorException e) {
            log.error("鸿蒙 pushApi2 推送异常, code:{}, message:{}", e.getApiError().getError().getCode(),
                    e.getApiError().getError().getMessage());
            msg = msg + " hmos pushApi2: " + e.getApiError().getError().getMessage();
        } catch (Exception e) {
            log.error("鸿蒙 pushApi2 推送异常:{}", e.getMessage());
            msg = msg + " hmos pushApi2: " + e.getMessage();
        }

        if (pushFlag1 || pushFlag2) {
            result = 1;
        }
        jpushResDto.setCode(result);
        jpushResDto.setMsg(msg);
        return jpushResDto;
    }

    /**
     * 鸿蒙批量单推：根据别名批量推送到鸿蒙设备
     */
    public void batchSendHmos(List<JgPushRequest> pushNews, List<String> cidList, PushApi pushApi) {
        try {
            String intentUrl = pushApi.equals(pushApi1) ? INTENT_SL : INTENT_NSL;
            Map<String, BatchPushParam> pushSendParam = new HashMap<>();
            for (int i = 0; i < pushNews.size(); i++) {
                JgPushRequest item = pushNews.get(i);
                BatchPushParam batchPushParam = new BatchPushParam();
                batchPushParam.setTarget(item.getAlias());
                batchPushParam.setPlatform(java.util.Arrays.asList(
                        cn.jiguang.sdk.enums.platform.Platform.hmos));

                if (MsgConstants.JPUSH_MSG_TYPE_NOTIFICATION.equals(item.getMsgType())) {
                    // 通知类推送
                    NotificationMessage notificationMessage = buildHmosNotification(item.getNotificationTitle(),
                            item.getMsgTitle(), item.getMsgContent(), item.getExtrasparam(), item.getType(), intentUrl, item.getSound());
                    batchPushParam.setNotification(notificationMessage);
                } else {
                    // 自定义消息类推送
                    CustomMessage customMessage = new CustomMessage();
                    customMessage.setContent(StringUtils.isEmpty(item.getMsgContent()) ? "" : item.getMsgContent());
                    customMessage.setTitle(item.getNotificationTitle());
                    Map<String, Object> customExtras = new HashMap<>();
                    customExtras.put("type", item.getExtrasparam());
                    customExtras.put(MSG_ID_STR, item.getMsgId());
                    customExtras.put(NOTICE_TYPE, StringUtils.isEmpty(item.getType()) ? "1" : item.getType());
                    customMessage.setExtras(customExtras);
                    batchPushParam.setCustom(customMessage);
                }

                // options
                cn.jiguang.sdk.bean.push.options.Options options =
                        new cn.jiguang.sdk.bean.push.options.Options();
                options.setApnsProduction(true);
                options.setTimeToLive(86400L);
                batchPushParam.setOptions(options);

                pushSendParam.put(cidList.get(i), batchPushParam);
            }

            BatchPushSendParam param = new BatchPushSendParam();
            param.setSendParam(pushSendParam);
            BatchPushSendResult result = pushApi.batchSendByAlias(param);
            log.info("鸿蒙批量单推结果:{}", result);
        } catch (ApiErrorException e) {
            log.error("鸿蒙批量单推异常, code:{}, message:{}", e.getApiError().getError().getCode(),
                    e.getApiError().getError().getMessage());
        } catch (Exception e) {
            log.error("鸿蒙批量单推异常:{}", e.getMessage());
        }
    }

    /**
     * 构建鸿蒙单推 PushSendParam
     */
    private PushSendParam buildHmosPushPayload(String alias, String notificationTitle, String msgTitle,
                                                String msgContent, String extrasparam, String type,
                                                String intentUrl, String sound, String msgType) {
        PushSendParam param = new PushSendParam();

        // 目标平台：仅鸿蒙
        param.setPlatform(java.util.Arrays.asList(cn.jiguang.sdk.enums.platform.Platform.hmos));

        // 目标人群：按别名
        cn.jiguang.sdk.bean.push.audience.Audience audience =
                new cn.jiguang.sdk.bean.push.audience.Audience();
        audience.setAliasList(java.util.Collections.singletonList(alias));
        param.setAudience(audience);

        if (MsgConstants.JPUSH_MSG_TYPE_NOTIFICATION.equals(msgType)) {
            // 通知类推送
            NotificationMessage notificationMessage = buildHmosNotification(notificationTitle, msgTitle,
                    msgContent, extrasparam, type, intentUrl, sound);
            param.setNotification(notificationMessage);
        } else {
            // 自定义消息类推送
            CustomMessage customMessage = new CustomMessage();
            customMessage.setContent(StringUtils.isEmpty(msgContent) ? "" : msgContent);
            customMessage.setTitle(notificationTitle);
            Map<String, Object> customExtras = new HashMap<>();
            customExtras.put("type", extrasparam);
            customExtras.put(NOTICE_TYPE, StringUtils.isEmpty(type) ? "1" : type);
            customMessage.setExtras(customExtras);
            param.setCustom(customMessage);
        }

        // options
        cn.jiguang.sdk.bean.push.options.Options options =
                new cn.jiguang.sdk.bean.push.options.Options();
        options.setApnsProduction(true);
        options.setTimeToLive(86400L);
        param.setOptions(options);

        return param;
    }

    /**
     * 构建鸿蒙通知内容
     */
    private NotificationMessage buildHmosNotification(String notificationTitle, String msgTitle,
                                                       String msgContent, String extrasparam, String type,
                                                       String intentUrl, String sound) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert(StringUtils.isEmpty(msgContent) ? "" : msgContent);

        // 鸿蒙平台通知
        NotificationMessage.HMOS hmos = new NotificationMessage.HMOS();
        hmos.setAlert(StringUtils.isEmpty(msgContent) ? "" : msgContent);
        hmos.setTitle(notificationTitle);
        // 声音
        hmos.setSound(StringUtils.isEmpty(sound) ? MSG_SOUND_STR : sound + ".mp3");

        NotificationMessage.HMOS.Intent intent = new NotificationMessage.HMOS.Intent();
        intent.setUrl(intentUrl);
        hmos.setIntent(intent);
        // 扩展字段
        Map<String, Object> hmosExtras = new HashMap<>();
        hmosExtras.put("type", extrasparam);
        hmosExtras.put(NOTICE_TYPE, StringUtils.isEmpty(type) ? "1" : type);
        hmos.setExtras(hmosExtras);
        // 通知分类
        hmos.setCategory("WORK");
        notificationMessage.setHmos(hmos);

        return notificationMessage;
    }

}
