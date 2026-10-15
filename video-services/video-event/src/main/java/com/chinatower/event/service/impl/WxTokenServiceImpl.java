package com.chinatower.event.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.api.feign.client.WeChatFeignClient;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.service.WxTokenService;
import com.chinatower.event.vo.GetWeChatSignatureReq;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description 告警加密分享控制器
 * @Author yhb
 * @Date 2023/1/03
 * @Version release_v1.60
 **/
@Slf4j
@Service
public class WxTokenServiceImpl implements WxTokenService {

    /** * 调用微信公众号的接口 获取Access token 必输的一个请求参数 该值是固定的 */
    private final static String GRANT_TYPE = "client_credential";
    /** * 调用微信公众号的接口 获取jsapi_ticket 必输的一个请求参数 该值是固定的 */
    private final static String GET_JSAPI_TICKET_TYPE = "jsapi";

    /** * 字符串，时间戳，签名 */
    private final static String GET_NONCESTR_TYPE = "MSMSAMDASSDSADAA";
    private final static String GET_NONCESTR = "nonceStr";
    private final static String GET_TIME_TYPE = "time";
    private final static String GET_TICKET_TYPE = "jsapiTicket";
    /** * appid 密钥 */
    private final static String GET_GRA_TYPE = "grant_type";
    private final static String GET_APP_TYPE = "appid";
    private final static String GET_SCR_TYPE = "secret";

    private final static String GET_TSTP_TYPE = "timestamp";

    @Resource
    private WeChatFeignClient weChatFeignClient;

    @Value("${elink.corApp}")
    private String appId;
    @Value("${elink.appSecret}")
    private String appSecret;
    @Value("${elink.username}")
    private String username;


    public Map xcxToken(GetWeChatSignatureReq req){
        //通过第一步获得的code获取微信授权信息
        Map<String,String> resultMap = new HashMap<>();
        req.setAppId(appId);
        req.setAppSecret(appSecret);
        try {
            // 生成随机字符串
            String nonceStr = GET_NONCESTR_TYPE;
            // 时间戳
            long timestamp = System.currentTimeMillis() / 1000;
            if (RedisUtil.hasKey(Constants.ALARM_SIGNATURE, RedisDBType.PRIMARY)){
                String ticket = RedisUtil.get(Constants.ALARM_SIGNATURE, RedisDBType.PRIMARY);
                log.info("调用缓存获取jsapiTicket，返回数据为:\n{}", ticket);
                JSONObject outcome=JSON.parseObject(ticket);
                String time = outcome.getString(GET_TIME_TYPE);
                String jsapiTicket = outcome.getString(GET_TICKET_TYPE);
                if(!StringUtils.isEmpty(jsapiTicket)){
                    resultMap = getSignature(jsapiTicket,req.getUrl(),GET_NONCESTR_TYPE,Long.valueOf(time));
                    return resultMap;
                }
            }
            // 调用微信公众号的接口 获取Access token
            String accessToken = getAccessToken(req);
            // 调用微信公众号的接口 获取jsapi_ticket
            String jsapiTicket = getJsapiTicket(accessToken);
            JSONObject json=new JSONObject();
            json.put(GET_TIME_TYPE,  timestamp+ "");
            json.put(GET_TICKET_TYPE, jsapiTicket);
            json.put(GET_NONCESTR,nonceStr);

            // 更新redis，同时新增本次新增的
            RedisUtil.set(Constants.ALARM_SIGNATURE, json.toJSONString(), RedisDBType.PRIMARY);
            RedisUtil.expire(Constants.ALARM_SIGNATURE, 7100, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            // 签名加密算法
            resultMap = getSignature(jsapiTicket, req.getUrl(),nonceStr,timestamp);
        }catch (Exception e){
            log.error("微信授权："+e.getMessage());
        }
        return resultMap;
    }

    public String getAccessToken(GetWeChatSignatureReq req) throws ApplicationException {

        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put(GET_GRA_TYPE, GRANT_TYPE);
        reqMap.put(GET_APP_TYPE, req.getAppId());
        reqMap.put(GET_SCR_TYPE, req.getAppSecret());
        log.info("调用微信的接口 获取AccessToken，请求数据为:\n{}", JSON.toJSONString(reqMap));
        String accessTokenResp = weChatFeignClient.getAccessToken(reqMap);
        log.info("调用微信的接口 获取AccessToken，响应数据为:\n{}", accessTokenResp);
        return JSON.parseObject(accessTokenResp).getString("access_token");
    }

    public String getJsapiTicket(String accessToken) throws ApplicationException {

        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("type", GET_JSAPI_TICKET_TYPE);
        reqMap.put("access_token", accessToken);
        log.info("调用微信的接口 获取jsapi_ticket，请求数据为:\n{}", JSON.toJSONString(reqMap));
        String jsapiTicketResp = weChatFeignClient.getJsapiTicket(reqMap);
        log.info("调用微信的接口 获取jsapi_ticket，响应数据为:\n{}", jsapiTicketResp);
        String ticket = JSON.parseObject(jsapiTicketResp).getString("ticket");
        return ticket;
    }
    public Map getSignature(String jsapiTicket, String url,String nonceStr, Long timestamp) throws ApplicationException {

        Map resp = new HashMap();
        // 通过jsapi_ticket、随机字符串、时间戳
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jsapi_ticket=").append(jsapiTicket).append("&")
                .append("noncestr=").append(nonceStr).append("&")
                .append("timestamp=").append(timestamp).append("&")
                .append("url=").append(url);
        String signatureStr = stringBuilder.toString();
        log.info("微信签名 she1加密前的数据：{}", signatureStr);
        String signature = DigestUtils.sha1Hex(signatureStr);
        log.info("微信签名，she1加密后的数据：{}", signature);
        resp.put(GET_NONCESTR,nonceStr);
        resp.put(GET_TSTP_TYPE,String.valueOf(timestamp));
        resp.put(Constants.ALARM_SIGNATURE,signature);
        resp.put(Constants.ALARM_USER,username);
        return resp;
    }


}
