package com.chinatower.message.util;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.util.OkhttpUtil;
import com.chinatower.message.api.model.MsgBody;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @title
 * @description http請求工具類
 * @author LiZe
 * @updateTime 2022/1/19 14:24
 * @throws
 */
@Slf4j
@AllArgsConstructor
public class HttpClientUtils
{

    /**
     * http請求前綴
     * */
    public static final String PRE_HTTP = "http://";
    /**
     * 消息接口映射路徑
     * */
    public static final String MAPPING_PATH_OF_MSG_INTERFACE = "/InternalForwardingMsg/messagePush";
    public static final String RADAR_MESSAGE_PUSH = "/InternalForwardingMsg/radarMessagePush";
    public static final String PARAM_ORDER_STATUS= "orderStatus=";
    public static final String PARAM_MSG_ID= "msgId=";
    public static final String APP_VER_CODE= "appVerCode=";

    private static final String INDUSTRY_CODE = "industryCode=";
    public static final String ALARM_FLAG= "alarmFlag=";
    public static final String SHOW_TYPE= "showType=";
    public static final String INDUSTRY_FLAG= "industryFlag=";
    public static final String INDUSTRY_EXTEND= "industryExtend=";
    public static final String PARAM_UID = "userId=";
    public static final String PARAM_NOTIFY_TYPE = "notifyType=";
    public static final String PARAM_MSG_INFO = "notifyTitle=";
    public static final String PARAM_ADDRESS = "address=";
    public static final String PARAM_SOUND_CLOSE_TYPE = "soundCloseType=";
    public static final String PARAM_SOUND_DURATION = "soundDuration=";
    public static final String PARAM_SOUND_URL = "soundUrl=";
    public static final String PARAM_ORDER_STATUS_NAME = "orderStatusName=";
    public static final String UTF8="utf-8";
    public static final String AND = "&";
    public static final String QUESTION = "?";
    public static final String CONTENT_TYPE ="Content-Type";
    public static final String APPLICATION_JSON ="application/json;charset=utf8";
    /**
     * @title
     * @description http post 消息转发
     * @author LiZe
     * @updateTime 2022/1/19 15:10
     * @throws
     */
    public static void httpPostWithParams(String channelCode, String targetUrl, MsgBody req){
        String orderStatus = req.getOrderStatus(); // 告警事件状态
        String msgId = req.getMsgId(); // 告警ID
        String notifyType = req.getNotifyType(); // 通知类型
        String notifyTitle = req.getNotifyTitle(); // 通知标题
        String address = req.getAddress(); // 地址
        String appVerCode = req.getAppVerCode(); // 行业版本编码
        String alarmFlag = req.getAlarmFlag(); // 告警状态
        String showType = req.getShowType(); // 显示位置
        String industryFlag = req.getIndustryFlag(); // 行业FLAG
        String industryExtend = req.getIndustryExtend(); // 扩展信息
        String industryCode = null == req.getIndustryCode() ? null : req.getIndustryCode(); //行业编码
        String soundCloseType = req.getSoundCloseType();//声音关闭方式 1：自动关闭，2：手动关闭
        String soundDuration = req.getSoundDuration();//播放时长（单位：秒）
        String soundUrl = req.getSoundUrl();//声音加密url
        String orderStatusName = req.getOrderStatusName(); // 告警事件状态名称

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 参数
        StringBuilder params = new StringBuilder();
        try {
            // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
            params.append(PARAM_UID + channelCode);
            params.append(AND);
            params.append(PARAM_ORDER_STATUS + orderStatus);
            params.append(AND);
            params.append(PARAM_MSG_ID + msgId);
            params.append(AND);
            params.append(PARAM_NOTIFY_TYPE + notifyType);
            params.append(AND);
            if(StringUtils.isEmpty(notifyTitle)){
                params.append(PARAM_MSG_INFO + URLEncoder.encode("", UTF8));
            }
            else{
                params.append(PARAM_MSG_INFO + URLEncoder.encode(notifyTitle, UTF8));
            }
            params.append(AND);
            if(StringUtils.isEmpty(address)){
                params.append(PARAM_ADDRESS + URLEncoder.encode("", UTF8));
            }
            else{
                params.append(PARAM_ADDRESS + URLEncoder.encode(address, UTF8));
            }

            params.append(AND);
            params.append(APP_VER_CODE + appVerCode);
            params.append(AND);
            params.append(INDUSTRY_CODE + industryCode);
            params.append(AND);
            params.append(ALARM_FLAG + alarmFlag);
            params.append(AND);
            params.append(SHOW_TYPE + showType);
            params.append(AND);
            params.append(INDUSTRY_FLAG + industryFlag);
            params.append(AND);
            params.append(INDUSTRY_EXTEND + industryExtend);

            params.append(AND);
            if(StringUtils.isEmpty(soundCloseType)){
                params.append(PARAM_SOUND_CLOSE_TYPE + URLEncoder.encode("", UTF8));
            }
            else{
                params.append(PARAM_SOUND_CLOSE_TYPE + URLEncoder.encode(soundCloseType, UTF8));
            }

            params.append(AND);
            if(StringUtils.isEmpty(soundDuration)){
                params.append(PARAM_SOUND_DURATION + URLEncoder.encode("", UTF8));
            }
            else{
                params.append(PARAM_SOUND_DURATION + URLEncoder.encode(soundDuration, UTF8));
            }

            params.append(AND);
            if(StringUtils.isEmpty(soundUrl)){
                params.append(PARAM_SOUND_URL + URLEncoder.encode("", UTF8));
            }
            else{
                params.append(PARAM_SOUND_URL + URLEncoder.encode(soundUrl, UTF8));
            }

            params.append(AND);
            if(StringUtils.isEmpty(orderStatusName)){
                params.append(PARAM_ORDER_STATUS_NAME + URLEncoder.encode("", UTF8));
            }
            else{
                params.append(PARAM_ORDER_STATUS_NAME + URLEncoder.encode(orderStatusName, UTF8));
            }


        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        log.info("请求参数:" + params.toString());
        // 创建Post请求
        HttpPost httpPost = new HttpPost(PRE_HTTP + targetUrl + MAPPING_PATH_OF_MSG_INTERFACE + QUESTION + params);
        // 设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            log.info("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                log.info("响应内容长度为:" + responseEntity.getContentLength());
                log.info("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ClientProtocolException|ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void httpPostWithParams(String channelCode, String targetUrl, Object content) {

        try {
            String s = JSON.toJSONString(content);
            log.info("请求参数:" + s);
            OkhttpUtil.doPost(s, PRE_HTTP + targetUrl + RADAR_MESSAGE_PUSH);
        } catch (Exception e) {
            log.error("处理异常", e);
        }
    }

}