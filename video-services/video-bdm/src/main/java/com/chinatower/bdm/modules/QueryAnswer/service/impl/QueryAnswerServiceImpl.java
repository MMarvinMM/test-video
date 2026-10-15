package com.chinatower.bdm.modules.QueryAnswer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.QueryAnswer.entity.ChatVO;
import com.chinatower.bdm.modules.QueryAnswer.entity.QueryAnswerDTO;
import com.chinatower.bdm.modules.QueryAnswer.entity.QueryAnswerLoginVO;
import com.chinatower.bdm.modules.QueryAnswer.entity.QueryAnswerVO;
import com.chinatower.bdm.modules.QueryAnswer.service.IQueryAnswerService;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class QueryAnswerServiceImpl implements IQueryAnswerService {

    @Value("${queryAnswer.api.compUrl}")
    private String apiUrl;

    @Value("${queryAnswer.api.tenantId}")
    private String tenantId;

    @Value("${queryAnswer.api.appCode}")
    private String appCode;

    @Value("${queryAnswer.api.password}")
    private String password;

    @Autowired
    private SystemFeignClient systemFeignClient;

    private static final String FORMAT_JSON = "application/json";

    private static final String QUERY_STR = "query";

    private static final String ANSWER_STR = "answer";

    /**
     * 获取token，并缓存
     * @return
     */
    private String getToken(){
        try {
            String token = null;
            Map<String,String> extParamMap = new HashMap<>();
            extParamMap.put("user_id",tenantId);
            String jsonString = JSON.toJSONString(extParamMap);
            String code = tenantId + ":" + password;
            log.info("code:{}",code);
            String base64Code = Base64.getEncoder().encodeToString(code.getBytes());
            log.info("base64Code:{}",base64Code);
            String Authorization = "Basic " + base64Code;
            log.info("Authorization:{}",Authorization);
            HashMap<String,Object> params = new HashMap<>();
            params.put("tenant_id",tenantId);
            params.put("app_id",appCode);
            params.put("grant_type", Constants.GRANT_TYPE);
            params.put("ext_params",jsonString);
            log.info("获取token接口入参:{},开始时间：{}",params,System.currentTimeMillis());
            HttpResponse tokenResponse = HttpRequest.post(apiUrl+Constants.TOKEN_URL)
                    .header("Authorization", Authorization)
                    .header("content-type", "multipart/form-data")
                    .form("tenant_id",tenantId)
                    .form("app_id",appCode)
                    .form("grant_type", Constants.GRANT_TYPE)
                    .form("ext_params",jsonString)
                    .timeout(50000)
                    .execute();
            log.info("HttpResponse tokenResponse:{},结束时间：{}",tokenResponse,System.currentTimeMillis());
            if (tokenResponse.isOk()){
                QueryAnswerLoginVO queryAnswerLoginVO = JSON.parseObject(tokenResponse.body(), QueryAnswerLoginVO.class);
                log.info("获取token接口返回信息:{}",queryAnswerLoginVO);
                if (queryAnswerLoginVO == null){
                    //不报空指针
                    return null;
                }
                token = queryAnswerLoginVO.getAccessToken();
                String expireIn = queryAnswerLoginVO.getExpiresIn();
                if (StrUtil.isBlank(token) || StrUtil.isBlank(expireIn)) {
                    log.info("获取token失败");
                    return null;
                }
                RedisUtil.setEx(Constants.TOKEN_CACHE_KEY,token,Long.parseLong(expireIn)-60, TimeUnit.SECONDS,RedisDBType.PRIMARY);
            }
            return token;
        }catch (Exception e){
            log.error("获取token异常:",e);
            return null;
        }
    }

    /**
     * 获取缓存中的token,如果缓存中没有则调用接口获取
     * @return
     */
    private String getCacheToken(){
        String cacheToken = RedisUtil.get(Constants.TOKEN_CACHE_KEY, RedisDBType.PRIMARY);
        if (StrUtil.isNotBlank(cacheToken)) {
            log.info("获取缓存token:{}",cacheToken);
            return cacheToken;
        }else {
            String token = getToken();
            log.info("获取缓存token失败,重新获取token:{}",token);
            return token;
        }
    }

    /**
     * 获取ticket
     * @param token
     * @return
     */
    private String getTicket(String token){
        try {
            String ticket = null;
            String grantType = Constants.GRANT_TYPE;
            String compCode = Constants.COMP_CODE;
            String accessType = Constants.ACCESS_TYPE;
            StringBuilder ticketResultUrl = new StringBuilder();
            ticketResultUrl.append(apiUrl)
                    .append(Constants.TICKET_URL)
                    .append(Constants.GRANT_TYPE_PARAM).append(grantType)
                    .append(Constants.COMP_CODE_PARAM).append(compCode)
                    .append(Constants.AUTHORIZATION_PARAM).append(token)
                    .append(Constants.ACCESS_TYPE_PARAM).append(accessType);
            log.info("获取ticket接口url:{}，开始时间：{}",ticketResultUrl,System.currentTimeMillis());
            HttpResponse ticketResponse = HttpRequest.get(ticketResultUrl.toString()).timeout(5000).execute();
            log.info("HttpResponse ticketResponse:{},结束时间：{}",ticketResponse,System.currentTimeMillis());
            if (ticketResponse.isOk()){
                JSONObject ticketObject = JSONObject.parseObject(ticketResponse.body());
                log.info("获取ticket接口返回信息:{}",JSON.toJSONString(ticketObject));
                if (ticketObject == null){
                    //不报空指针
                    return null;
                }
                Object o = ticketObject.get("ticket");
                ticket = ObjectUtil.isEmpty(o)?null:o.toString();
            }
            return ticket;
        }catch (Exception e){
            log.error("获取ticket异常:",e);
            return null;
        }
    }

    @Override
    public Flux<String> streamData(QueryAnswerDTO queryAnswerDTO){
        if (StrUtil.isBlank(queryAnswerDTO.getQueryType())) {
            return Flux.just("查询模式不能为空！");
        }

        //获取系统配置中的接口超时时长
        AjaxResult ajaxResult = systemFeignClient.getConfValue("time_out");
        //设置超时时间--默认先20S--具体从参数配置中获取
        int timeout = 120;
        if (ObjectUtil.isNotEmpty(ajaxResult)) {
            Object code = ajaxResult.get(AjaxResult.CODE_TAG);
            if (ObjectUtil.isNotEmpty(code)&&"200".equals(String.valueOf(code))) {
                Object data = ajaxResult.get(AjaxResult.DATA_TAG);
                if (ObjectUtil.isNotEmpty(data)){
                    SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(data), SysConfig.class);
                    if (ObjectUtil.isNotEmpty(sysConfig)&& StrUtil.isNotBlank(sysConfig.getConfigValue())) {
                        timeout = Integer.parseInt(sysConfig.getConfigValue());
                    }
                }
            }
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout,TimeUnit.SECONDS)
                .readTimeout(timeout,TimeUnit.SECONDS)
                .writeTimeout(timeout,TimeUnit.SECONDS)
                .build();

        //1,获取token
        String cacheToken = getCacheToken();
        if (StrUtil.isBlank(cacheToken)) {
            return Flux.just("获取token失败！");
        }
        //2,获取ticket
        String ticket = getTicket(cacheToken);
        if (StrUtil.isBlank(ticket)) {
            return Flux.just("获取ticket失败！");
        }
        //3.拼装默认响应情况
        String queryType = queryAnswerDTO.getQueryType();
        String apiName = "";
        switch (queryType){
            case "1":
                apiName = Constants.ORDINARY_URL;
                break;
            case "2":
                apiName = Constants.DIALOGUE_URL;
                break;
            default:
                break;
        }
        //4.调用接口
        queryAnswerDTO.setTenantId(tenantId);
        queryAnswerDTO.setAppCode(appCode);
        queryAnswerDTO.setStream(true);
        queryAnswerDTO.setModelType("baidu");
        RequestBody body = RequestBody.create(MediaType.parse(FORMAT_JSON),JSON.toJSONString(queryAnswerDTO));
        Request request = new Request.Builder()
                .url(apiUrl + apiName)
                .header("Ticket",ticket)
                .post(body)
                .build();
        log.info("调用接口入参：{}",JSON.toJSONString(queryAnswerDTO));
        JSONObject errObject = new JSONObject();
        errObject.put(QUERY_STR, queryAnswerDTO.getMsg());
        errObject.put(ANSWER_STR,Constants.ERROR_MSG);
        String joinErrMsg = "data:"+JSON.toJSONString(errObject);
        return Flux.create(fluxSink -> {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //处理请求失败情况
                    log.error("*********流式请求人工机器人接口请求失败", e);
                    //fluxSink.error(e);
                    fluxSink.next(joinErrMsg);
                    fluxSink.complete();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if (!response.isSuccessful()) {
                        //fluxSink.error(new RuntimeException("响应失败:"+response));
                        log.error("*********流式请求人工机器人接口响应失败:{}",response);
                        fluxSink.next(joinErrMsg);
                        return;
                    }
                    try(ResponseBody responseBody = response.body()) {
                        if (ObjectUtil.isNotEmpty(responseBody)) {
                            BufferedSource source = responseBody.source();
                            while (!source.exhausted()) {
                                String line = source.readUtf8Line();
                                if (StrUtil.isNotBlank(line)) {
                                    log.info("==============获取到的流信息结果为：{}",line);
                                    fluxSink.next(line);
                                }
                            }
                        } else {
                            log.info("~~~~~~~~流式请求人工机器人接口响应失败:ResponseBody为空");
                            //fluxSink.error(new RuntimeException("请求失败:ResponseBody为空"));
                            fluxSink.next(joinErrMsg);
                        }
                    } catch (IOException e){
                        log.error("~~~~~~~~流式请求人工机器人异常：",e);
                        //fluxSink.error(e);
                        fluxSink.next(joinErrMsg);
                    } finally {
                        fluxSink.complete();
                    }
                }
            });
        }, FluxSink.OverflowStrategy.BUFFER);
    }

    @Override
    public Result<Object> queryProblemAnswer(QueryAnswerDTO queryAnswerDTO) {
        if (StringUtils.isEmpty(queryAnswerDTO.getQueryType())){
            return Result.fail("查询模式不能为空！");
        }
        String apiName = "";
        String queryKey = "";
        JSONObject jsonObject;
        String queryReceiveTime = DateUtils.dateTimeNow();
        Stopwatch stopwatch = Stopwatch.createStarted();
        if (queryAnswerDTO.getQueryType().equals("1")){
            apiName = "llm_answer";
        }else if (queryAnswerDTO.getQueryType().equals("2")){
            apiName = "dialogue";
            if (queryAnswerDTO.getMsg().equals(Constants.DEFAULT_MSG_ONE) || queryAnswerDTO.getMsg().equals(Constants.DEFAULT_MSG_TWO)
                    || queryAnswerDTO.getMsg().equals(Constants.DEFAULT_MSG_THREE) || queryAnswerDTO.getMsg().equals(Constants.DEFAULT_MSG_FOUR)){
                //专业模式--默认四个问题查询缓存
                queryKey = apiName +":" + queryAnswerDTO.getMsg();
                QueryAnswerVO redisJson = RedisUtil.get(queryKey, QueryAnswerVO.class, RedisDBType.PRIMARY);
                if (null != redisJson) {
                    log.info("key:" + queryKey + "在缓存中存在");
                    return Result.data(redisJson);
                }
            }
        }
        AjaxResult ajaxResult = systemFeignClient.getConfValue("time_out");
        //设置超时时间--默认先20S--具体从参数配置中获取
        int timeout = 20000;
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            timeout = Integer.parseInt(sysConfig.getConfigValue());
        }
        StringBuilder url = new StringBuilder();
        url.append(apiUrl).append(apiName);
        log.info("问答查询url:"+url);
        queryAnswerDTO.setTenantId(tenantId);
        queryAnswerDTO.setAppCode(appCode);
        String jsonString = JSON.toJSONString(queryAnswerDTO);
        log.info("外部接口调用前耗时：{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        try {
            //超过5S未回复就返回错误语句
            HttpResponse httpResponse = HttpRequest.post(url.toString()).body(jsonString,FORMAT_JSON).timeout(timeout).execute();
            log.info("外部接口调用时长：{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            log.info("HttpResponse {}", httpResponse);
            if (httpResponse.isOk()){
                String body = httpResponse.body();
                jsonObject = JSONObject.parseObject(body);
                log.info("调用问答接口返回结果: {}", JSON.toJSONString(jsonObject));
                if (jsonObject == null) {
                    return null;
                }
                if (!queryKey.equals("")){
                    RedisUtil.set(queryKey, jsonObject, RedisDBType.PRIMARY);
                    //失效时间为10分钟
                    RedisUtil.expire(queryKey, 10, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                }
            }else {
                jsonObject = new JSONObject();
                jsonObject.put("queryReceiveTime",queryReceiveTime);
                jsonObject.put(QUERY_STR,queryAnswerDTO.getMsg());
                jsonObject.put(ANSWER_STR,Constants.ERROR_MSG);
                jsonObject.put("answerReturnTime",DateUtils.dateTimeNow());
            }
        }catch (Exception e){
            log.error("接口调用异常：",e);
            jsonObject = new JSONObject();
            jsonObject.put("queryReceiveTime",queryReceiveTime);
            jsonObject.put(QUERY_STR,queryAnswerDTO.getMsg());
            jsonObject.put(ANSWER_STR,Constants.ERROR_MSG);
            jsonObject.put("answerReturnTime",DateUtils.dateTimeNow());
        }
        return Result.data(jsonObject);
    }

    /**
     * 多模态接口
     * @param chatVO
     * @return
     */
    public Result<ChatVO> chat(ChatVO chatVO) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        //获取系统配置中的接口超时时长
        AjaxResult ajaxResult = systemFeignClient.getConfValue("chat_time_out");
        //设置超时时间--默认先20S--具体从参数配置中获取
        int timeout = 60000;
        if (ObjectUtil.isNotEmpty(ajaxResult)) {
            Object code = ajaxResult.get(AjaxResult.CODE_TAG);
            if (ObjectUtil.isNotEmpty(code)&&"200".equals(String.valueOf(code))) {
                Object data = ajaxResult.get(AjaxResult.DATA_TAG);
                if (ObjectUtil.isNotEmpty(data)){
                    SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(data), SysConfig.class);
                    if (ObjectUtil.isNotEmpty(sysConfig)&& StrUtil.isNotBlank(sysConfig.getConfigValue())) {
                        timeout = Integer.parseInt(sysConfig.getConfigValue());
                    }
                }
            }
        }
        //1,获取token
        String cacheToken = getCacheToken();
        if (StrUtil.isBlank(cacheToken)) {
            return Result.fail("获取token失败！");
        }
        //2,获取ticket
        String ticket = getTicket(cacheToken);
        if (StrUtil.isBlank(ticket)) {
            return Result.fail("获取ticket失败！");
        }
        String url = apiUrl + Constants.CHAT_COMPLETIONS;
        String imgReqParam = this.buildParam(chatVO.getType(), chatVO.getPicId(), chatVO.getContent());
        log.info("请求图片 req: {}", imgReqParam);
        //先调用上传图片接口
        log.info("外部接口调用前耗时：{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        try {
            HttpResponse httpResponse = HttpRequest.post(url).header("Ticket",ticket).body(imgReqParam,FORMAT_JSON).timeout(timeout).execute();
            log.info("外部接口调用时长：{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            log.info("HttpResponse1 {}", httpResponse);
            if (httpResponse.isOk()) {
                String body = httpResponse.body();
                JSONObject jsonObject = JSONObject.parseObject(body);
                log.info("调用多模态接口返回结果: {}", JSON.toJSONString(jsonObject));
                JSONArray array = jsonObject.getJSONObject("data").getJSONArray("choices");
                JSONObject messageObj = array.getJSONObject(Constants.ZERO_NUM).getJSONObject(Constants.PARAM_MESSAGE);
                String content = messageObj.getString(Constants.PARAM_CONTENT);
                String imgUrl = messageObj.getJSONObject(Constants.PARAM_IMG_URL).getString(Constants.PARAM_URL);
                if (StringUtils.isNotBlank(content) || StringUtils.isNotBlank(imgUrl)) {
                    ChatVO resultVO = new ChatVO();
                    resultVO.setContent(content);
                    resultVO.setImgUrl(imgUrl);
                    return Result.data(resultVO);
                }
                return Result.success("调用成功");
            }
        }
        catch (Exception e){
            log.error("接口调用异常：",e);
        }
        return Result.fail("不好意思，您的问题我还没有理解，或者换一种方式和我再讲一下呢？");
    }

    /**
     * 组装多模态接口参数
     * @param type
     * @param uuid
     * @return
     */
    private String buildParam(String type, String uuid, String content) {
        JSONObject reqParam = new JSONObject();
        JSONObject messageObj = new JSONObject();
        JSONObject contentObj = new JSONObject();
        List messageList = Lists.newArrayList();
        List contentList = Lists.newArrayList();
        reqParam.put(Constants.PARAM_MODEL, "ZGTT-MLLM");
        reqParam.put(Constants.PARAM_MESSAGES, messageList);
        messageObj.put(Constants.PARAM_ROLE, "user");
        messageObj.put(Constants.PARAM_CONTENT, contentList);
        contentObj.put(Constants.PARAM_PIC_ID, uuid);
        contentObj.put(Constants.PARAM_TYPE, type);
        if (Objects.equals(Constants.PARAM_TEXT, type)) {
            contentObj.put(Constants.PARAM_TEXT, content);
        }
        else {
            JSONObject imgObj = new JSONObject();
            imgObj.put(Constants.PARAM_URL, content);
            contentObj.put(Constants.PARAM_IMAGE_URL, imgObj);
        }
        contentList.add(contentObj);
        messageList.add(messageObj);
        return JSONObject.toJSONString(reqParam);
    }
}
