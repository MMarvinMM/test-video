package com.chinatower.platform.common.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.encrypt.utils.Util;
import com.chinatower.platform.common.config.SmsTemplateConf;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用铁塔统一通知处理组件短信接口
 * <p>
 * 2020年9月
 * 赵坤
 */
@Component
@Slf4j
public class ItowerSmsUtil {

    public static final String MESSAGE_STR = "message";

    @Autowired
    private SmsTemplateConf smsTemplateConf;
    //例如告警会有告警的模板，其他短信会有其他的模板，所有templateCode作为参数传入
    public Map<String, String> send(String msg, String phone, String templateCode ) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("msgContent", msg+"--"+templateCode);
        log.info("sms发送信息send===map+"+JSON.toJSONString(map));
        try{
            String sysCode=smsTemplateConf.getSysCode();
            String urlCall=smsTemplateConf.getUrlCall();
            //返回值 application/json
            //mess 消息 例如：同步到消息队列
            //data 短信唯一标识，请保存到数据库。 "data":"ae94b0fa56"，回调时会携带该标识，并携带短信发送的状态。
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String token = Util.creterSM2Signature(timeStamp, timeStamp, sysCode, smsTemplateConf.getPrivateKey());
            CloseableHttpClient client = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(30000).setConnectTimeout(30000).setSocketTimeout(30000).build();
            HttpPost post = new HttpPost(urlCall);
            post.setConfig(requestConfig);
            //post.addHeader("Content-Type", "multipart/form-data");//这里不要添加Content-Type，坑死我了
            post.setHeader("Accept", "*/*");
            post.setHeader("Connection", "keep-alive");
            post.addHeader("token", token);
            post.addHeader("timestamp", timeStamp);
            post.addHeader("nonce", timeStamp);
            post.addHeader("sysCode", sysCode);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            ContentType contentType = ContentType.create("multipart/form-data", Consts.UTF_8);
            ContentType contentType2 = ContentType.create("text/plain", Consts.UTF_8);
            builder.setContentType(contentType);
            builder.setMode(HttpMultipartMode.RFC6532);
            builder.setCharset(Consts.UTF_8);
//            builder.addTextBody("topic", "mass ", contentType2);
            builder.addTextBody("phones", phone, contentType2);
            builder.addTextBody("msg", msg, contentType2);
            builder.addTextBody("url", "urlBack", contentType2);//回调地址
            builder.addTextBody("signCode", smsTemplateConf.getSignCode(), contentType2);
            builder.addTextBody("templateCode", templateCode, contentType2);
            post.setEntity(builder.build());
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String res= EntityUtils.toString(entity, "UTF-8");
            log.info("sms返回信息+"+res);
            JSONObject jsonObject= JSON.parseObject(res);
            //resultStat SUCCESS/ERROR
            String resultStat=jsonObject.getString("resultStat");
            if("SUCCESS".equals(resultStat)){
                map.put("msgId",jsonObject.getString("data"));
                map.put("code", "1");
                map.put(MESSAGE_STR, "send msg success");
            }else{
                map.put("code", "0");
                map.put(MESSAGE_STR, "send msg error");
            }

        }catch (Exception e){
            map.put("code", "0");
            map.put(MESSAGE_STR, "send msg error");
            e.printStackTrace();
            log.error(e.getMessage(),e);
            log.error("短信发送异常{}",e.getMessage());
        }
        return map;
    }
}
