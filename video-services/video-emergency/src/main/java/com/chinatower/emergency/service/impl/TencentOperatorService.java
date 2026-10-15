package com.chinatower.emergency.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.emergency.service.ITencentOperatorService;
import com.chinatower.platform.entity.admin.SysConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import okio.ByteString;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

@Component
@Slf4j
public class TencentOperatorService implements ITencentOperatorService {

    private final static String DATA = "data";
    private final static String DATA_MAO = "data:";

    private final static String EVENT_MAO = "event:";

    private final static String CONTENT = "content";

    private final static String PROMPT = "提示词";

    private final static String OP = "op";

    private final static String HANDLE_PRE = "--开始处理";

    private final static String BOT_APP_KEY = "bot_app_key";

    private final static String VISITOR_BIZ_ID = "visitor_biz_id";

    private final static String JSON_UTF_8 = "application/json;charset=UTF-8";

    private final static String INPUTS = "inputs";

    public JSONObject reply2VLB(JSONObject content) throws Exception {
        log.info(content + HANDLE_PRE + "===" + System.currentTimeMillis());

        String url = "http://10.37.1.27/gateway/v1/chat/completions";

        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).callTimeout(15, TimeUnit.SECONDS).build();

        RequestBody requestBody = RequestBody
                .create(MediaType.parse(JSON_UTF_8), content.toJSONString());

        Request request = new Request.Builder()
                .post(requestBody)
                .header("X-TC-Action","/v1/chat/completions")
                .header("X-TC-Version","2020-10-01")
                .header("X-TC-Service","modelservice-7355")
                .header("X-TC-Project","1")
                .url(url)
                .build();

        log.info(PROMPT + content + "请求开始时间" + System.currentTimeMillis());

        Call call = httpClient.newCall(request);

        Response response = call.execute();

        log.info(PROMPT + content + "请求结束时间" + System.currentTimeMillis());

        String body = response.body().string();

        log.info("多模态结果:" + body);

        return JSON.parseObject(body);
    }

    public JSONObject reply2Tencent(String content,int type) throws Exception{


        log.info(PROMPT + content + HANDLE_PRE + "===" + type + "===" + System.currentTimeMillis());

        String url = "https://wss.lke.cloud.tencent.com/v1/qbot/chat/sse";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(CONTENT, content);

        long timeout = 60L;

        if(type == 1){
            jsonObject.put(BOT_APP_KEY, "JOSpufti");
            jsonObject.put(VISITOR_BIZ_ID, "视联通用指令场景");
            timeout = 5L;
        }else if(type == 2){
            jsonObject.put(BOT_APP_KEY, "iovhhgLC");
            jsonObject.put(VISITOR_BIZ_ID, "视联业务");
        }else{
            jsonObject.put(BOT_APP_KEY, "PxxlUPTl");
            jsonObject.put(VISITOR_BIZ_ID, "HR应用");
        }

        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).callTimeout(timeout, TimeUnit.SECONDS).build();

        jsonObject.put("session_id", UUID.randomUUID().toString());

        RequestBody requestBody = RequestBody
                .create(MediaType.parse(JSON_UTF_8), jsonObject.toJSONString());
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();

        log.info(PROMPT + content + "请求开始时间" + System.currentTimeMillis());

        Call call = httpClient.newCall(request);

        Response response = call.execute();

        log.info(PROMPT + content + "请求结束时间" + System.currentTimeMillis());

        assert response.body() != null;

        String body = response.body().string();

        String[] lines = body.split("\n");

        Map<String, List<JSONObject>> responseJson = new HashMap<>();

        String lastKey = "";
        for(String line:lines){

            if(line.startsWith(EVENT_MAO)){
                lastKey = line.trim().substring(EVENT_MAO.length());
            }

            if(!responseJson.containsKey(lastKey)){
                responseJson.put(lastKey,new ArrayList<>());
            }

            if(line.startsWith(DATA_MAO)){
                responseJson.get(lastKey).add(JSONObject.parseObject(line.trim().substring(DATA_MAO.length())));
            }
        }

        List<JSONObject> jsonList = responseJson.get("reply");

        log.info(content + "返回结果:" + jsonList.get(jsonList.size() - 1));

        String replyStr = jsonList.get(jsonList.size() - 1).getJSONObject("payload").getString(CONTENT);

        System.out.println(replyStr);

        log.info(replyStr);



        JSONObject reply = null;

        if(type == 2 || type == 3){
            reply = new JSONObject();

            reply.put(CONTENT,replyStr);

            log.info(PROMPT + content + "--结束处理" + System.currentTimeMillis());

            return reply;
        }

        if(type == 1){
            if(!JSONObject.isValidObject(replyStr)){
                throw new RuntimeException("大模型返回格式错误");
            }
            reply = JSONObject.parseObject(replyStr);
        }

        log.info(PROMPT + content + "--结束处理" + System.currentTimeMillis());

        return reply;
    }

}
