package com.chinatower.platform.project.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.common.utils.CommonUtil;
import com.chinatower.platform.entity.request.WxMsgData;
import com.chinatower.platform.entity.request.WxPushRequest;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.config.WxMpProperties;
import com.chinatower.platform.project.dto.WxPushRequestDTO;
import com.chinatower.platform.project.entity.request.WxPushRequestVo;
import com.chinatower.platform.project.mapper.WxMsgMapper;
import com.chinatower.platform.project.mongodb.WxPushRequestMongoDAO;
import com.chinatower.platform.project.service.WxMsgService;
import com.chinatower.platform.project.utils.UUIDUtils;
import com.chinatower.platform.utils.HttpUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.enums.WxType;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.json.GsonParser;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.enums.WxMpApiUrl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 微信公众号推送 service
 * @Author: 朱君
 * @Date: 2022/11/15
 **/
@Service
@Slf4j
@RefreshScope
@AllArgsConstructor
public class WxServiceImpl implements WxMsgService {

    @Resource
    private WxMpService wxMpService;

    /**
     * 微信公众号推送消息模块dao
     */
    @Resource
    private WxMsgMapper wxMsgMapper;

    // 配置参数
    private final WxMpProperties properties;

    @Resource
    private WxPushRequestMongoDAO wxPushRequestMongoDAO;

    private static final String MINI_APP_URL = "pages/event/detail?eventId=";

    private static final String MINI_APP_URL_PARAM_INDUSTRY_CODE = "&industryCode=";

    /**
     * 公网微信公众号信息
     */
    @Override
    public Result sendEventMessage(WxPushRequest wxPushRequest) {
        log.info("公网微信公众号信息入参：{}", wxPushRequest);
        if (StringUtils.isBlank(wxPushRequest.getUserId()) && null == wxPushRequest.getUserIds()) {
            return Result.fail("userId与userIds不能同时为空");
        }
        WxPushRequestVo wxPushRequestVo = new WxPushRequestVo();
        WxPushRequestDTO wxPushRequestDTO;
        BeanUtils.copyProperties(wxPushRequest, wxPushRequestVo);

        LoginUser loginUser = this.getLoginUser();
        if (null != loginUser && null != loginUser.getUser()) {
            wxPushRequestVo.setCreateBy(loginUser.getUser().getUserId());
        }
        String eventId = wxPushRequest.getId();
        //12.30添加逻辑 如果userIds不为空，那么不适用userId参数1
        List<Map<String, String>> userInfos = wxMsgMapper.getUserInfo(wxPushRequest);
        //记录已经发送过的openId
        Set<String> sendOpenIds = new HashSet<>();
        boolean allSuccess = true;

        //2023-01-09 mongodb添加批量插入
        List<WxPushRequestDTO> insertMongoList = new ArrayList();

        for (Map<String, String> userInfo : userInfos) {
            try {
                String openId = userInfo.get("openId");
                if (sendOpenIds.contains(openId)) {
                    continue;
                } else {
                    sendOpenIds.add(openId);
                }

                wxPushRequestDTO = new WxPushRequestDTO();
                BeanUtils.copyProperties(wxPushRequest, wxPushRequestDTO);

                wxPushRequestVo.setOpenId(userInfo.get("openId"));
                wxPushRequestVo.setUserId(userInfo.get("userId"));
                wxPushRequestVo.setUnionId(userInfo.get("unionId"));

                String industryCode = wxPushRequest.getIndustryCode();
                //发送消息到公众号
                JSONObject jsonObject = sendMsgUseTemplate(wxPushRequestVo, eventId, industryCode);
                wxPushRequestVo.setMessageId(jsonObject.getString("msgId"));
                wxPushRequestVo.setSendResult("1");
                wxPushRequestVo.setId(CommonUtil.getUuid());
                wxPushRequestVo.setCreateTime(new Date());
                //保存到mongo
                BeanUtils.copyProperties(wxPushRequestVo, wxPushRequestDTO);

                //2023-01-06 添加请求json url id
                wxPushRequestDTO.setId(UUIDUtils.getUUID().toString());
                wxPushRequestDTO.setRequestJson(jsonObject.getString("requestJson"));
                wxPushRequestDTO.setRequestUrl(jsonObject.getString("requestUrl"));
                wxPushRequestDTO.setEventId(eventId);
                insertMongoList.add(wxPushRequestDTO);

            } catch (WxErrorException e) {
                wxPushRequestDTO = new WxPushRequestDTO();
                BeanUtils.copyProperties(wxPushRequest, wxPushRequestDTO);
                log.error("推送公众号模板消息报错", e);
                wxPushRequestVo.setSendResult("0");
                wxPushRequestVo.setFailReason(e.getMessage());
                BeanUtils.copyProperties(wxPushRequestVo, wxPushRequestDTO);
                wxPushRequestDTO.setEventId(eventId);
                wxPushRequestDTO.setId(UUIDUtils.getUUID().toString());
                allSuccess = false;
                insertMongoList.add(wxPushRequestDTO);
            }
        }

        try {
            //批量插入
            if (insertMongoList.size() > 0) {
                wxPushRequestMongoDAO.insertAll(insertMongoList, "bs_wx_record", MongoDBType.PRIMARY);
            }
        } catch (Exception e) {
            log.error("推送公众号模板消息批量插入报错:{}", e);
        }

        if (allSuccess) {
            return Result.success("推送公众号通知成功");
        } else {
            return Result.success("推送公众号通知存在部分失败");
        }
    }

    /**
     * 公网微信公众号信息
     */
    @Override
    public Result sendMultipleWxMessage(List<WxPushRequest> wxPushRequests) {
        int successNum = 0;
        int errorNum = 0;
        List<String> errorMsgId = new ArrayList<>();
        Result resultTemp;
        for (WxPushRequest wxPushRequest : wxPushRequests) {
            resultTemp = sendEventMessage(wxPushRequest);
            if (resultTemp.getMsg().indexOf("失败") < 0) {
                successNum++;
            } else {
                errorNum++;
                errorMsgId.add(wxPushRequest.getId());
            }
        }
        StringBuilder returnMsg = new StringBuilder("推送公众号通知成功").append(successNum).append("条，失败").append(errorNum).append("条。");
        if(!errorMsgId.isEmpty()){
            returnMsg.append("推送失败的消息ID：").append(StringUtils.join(errorMsgId,","));
        }
        return Result.success(returnMsg.toString());
    }

    private LoginUser getLoginUser() {
        LoginUser loginUser = null;
        try {
            loginUser = SecurityUtils.getLoginUser();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return loginUser;
    }

    /**
     * 发送微信模板消息
     *
     * @param wxPushRequest
     * @return messageId
     * @throws WxErrorException
     */
    public JSONObject sendMsgUseTemplate(WxPushRequestVo wxPushRequest, String eventId, String industryCode) throws WxErrorException {
        List<WxMpTemplateData> wxMpTemplateData = new ArrayList<>();
        WxMsgData extrasparam = wxPushRequest.getExtrasparam();
        if (!isDateValid(extrasparam.getTime3())) {
            throw new WxErrorException("告警时间定义有误");
        }
        if (StringUtils.isBlank(extrasparam.getFirst())) {
            extrasparam.setFirst("您有一条新的告警事件");
        } else if (extrasparam.getFirst().length() > 10) {
            extrasparam.setFirst(extrasparam.getFirst().substring(0, 7) + "...");
        }
        if (extrasparam.getThing4().length() > 20) {
            extrasparam.setThing4(extrasparam.getThing4().substring(0, 17) + "...");
        }
        if (extrasparam.getThing8().length() > 20) {
            extrasparam.setThing8(extrasparam.getThing8().substring(0, 17) + "...");
        }
        if (extrasparam.getPhrase9().length() > 5) {
            extrasparam.setPhrase9(extrasparam.getPhrase9().substring(0, 5));
        }
        extrasparam.setThing15(extrasparam.getThing8());
        extrasparam.setThing14(extrasparam.getThing4());
        Field[] field = extrasparam.getClass().getDeclaredFields();        //获取实体类的所有属性，返回Field数组
        //遍历所有属性
        for (int j = 0; j < field.length; j++) {
            //获取属性的名字
            String name = field[j].getName();
            String getName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1); //将属性的首字符大写，方便构造get，set方法
            try {
                Method m = extrasparam.getClass().getMethod(getName);
                String value = (String) m.invoke(extrasparam);    //调用getter方法获取属性值
                if (value != null) {
                    WxMpTemplateData templateData = new WxMpTemplateData();
                    templateData.setName(name);
                    templateData.setValue(value);
                    wxMpTemplateData.add(templateData);
                }
            } catch (Exception e) {
                log.error("设置微信模板消息参数异常：{}", e);
                throw new WxErrorException("设置微信模板消息参数异常");
            }
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(wxPushRequest.getOpenId())
                .templateId(properties.getTemplateId())
                .data(wxMpTemplateData)
                .url(properties.getServerHost())
                .build();
        if (StringUtils.isNotEmpty(properties.getMiniAppId())) {
            WxMpTemplateMessage.MiniProgram miniProgram = new WxMpTemplateMessage.MiniProgram(properties.getMiniAppId(),
                    MINI_APP_URL + eventId + MINI_APP_URL_PARAM_INDUSTRY_CODE + industryCode,
                    true);
            templateMessage.setMiniProgram(miniProgram);
        }

        // 发送模板消息，返回消息id
//        return wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        return this.sendTemplateMsg(templateMessage, true);
    }

    /**
     * token
     */
    private static final String ACCESS_TOKEN_KEY = "wx:accessToken:";
    /**
     * token缓存时间 2小时
     */
    private static final int ACCESS_TOKEN_KEY_CACHE_TIME = 7200;

    /**
     * 自定义发送
     *
     * @param templateMessage
     * @return
     * @throws WxErrorException
     */
    public JSONObject sendTemplateMsg(WxMpTemplateMessage templateMessage, boolean firstSend) throws WxErrorException {
        String cacheKey = ACCESS_TOKEN_KEY + properties.getAppId();
        //获取token
        String accessToken = RedisUtil.get(cacheKey, RedisDBType.PRIMARY);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = this.wxMpService.getAccessToken(true);
            RedisUtil.set(cacheKey, accessToken, ACCESS_TOKEN_KEY_CACHE_TIME, TimeUnit.SECONDS, RedisDBType.PRIMARY);
        }

        String s = templateMessage.toJson();
        JSONObject sendMsg = JSONObject.parseObject(s);
        JSONObject miniprogram = sendMsg.getJSONObject("miniprogram");
        miniprogram.put("pagepath", miniprogram.getString("path"));
        miniprogram.remove("path");
        sendMsg.put("miniprogram", miniprogram);

        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
        log.info("发送公众号模板消息url：{}", requestUrl);
        log.info("发送公众号模板消息参数：{}", sendMsg.toJSONString());
        String responseContent = HttpUtil.post(requestUrl, sendMsg.toJSONString());
        log.info("发送公众号模板消息返回：{}", responseContent);
        JsonObject jsonObject = GsonParser.parse(responseContent);
        //添加token失效重试逻辑
        if (jsonObject.get("errcode").getAsInt() == 40001 && firstSend) {
            log.info("发送公众号模板消息token失效，进入重试逻辑");
            //删除现有缓存token
            RedisUtil.delete(cacheKey, RedisDBType.PRIMARY);
            //重试一次
            log.info("发送公众号模板消息token失效，递归调用重试一次");
            return this.sendTemplateMsg(templateMessage, false);
        } else if (jsonObject.get("errcode").getAsInt() == 0) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("msgId", jsonObject.get("msgid").getAsString());
            returnJson.put("requestJson", sendMsg.toJSONString());
            returnJson.put("requestUrl", requestUrl);
            return returnJson;
        } else {
            throw new WxErrorException(WxError.fromJson(responseContent, WxType.MP));
        }
    }

    /**
     * 时间字符串校验
     *
     * @param dateStr
     * @return
     */
    public boolean isDateValid(String dateStr) {
        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
