package com.chinatower.platform.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.platform.common.balance.LoadBalanceContext;
import com.chinatower.platform.common.cmpp.AiPaiSmsCompont;
import com.chinatower.platform.common.cmpp.ZhongShangSmsCompont;
import com.chinatower.platform.common.config.SmsTemplateConf;
import com.chinatower.platform.common.constant.MsgConstants;
import com.chinatower.platform.common.sms.ItowerSmsUtil;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.project.mapper.BsSmsRecordMapper;
import com.chinatower.platform.project.service.CommonSmsService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: chinatowerH
 * @create: 2020/10/29
 **/
@Service
@Slf4j
@RefreshScope
public class CommonSmsServiceImpl implements CommonSmsService {

    /**
     * 注入短信发送模块dao
     */
    @Resource
    private BsSmsRecordMapper bsSmsRecordMapper;
    @Resource
    private LoadBalanceContext loadBalanceContext;
    @Resource
    private ZhongShangSmsCompont zhongShangSmsCompont;
    @Resource
    private AiPaiSmsCompont aiPaiSmsCompont;
    @Resource
    private SmsTemplateConf smsTemplateConf;


    @Value("${sms.channelRatio}")
    private String channelRatio;
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private ItowerSmsUtil itowerSmsUtil;


    /**
     * 功能说明：
     *
     * @param commonSmsRequest
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @throws
     * @author 张帅
     * @requestID
     * @date 2021/12/3 16:36
     */
    @Override
    public Map<String, String> sendMessageToPhone(CommonSmsRequest commonSmsRequest, Boolean isSendVerificationCode) {
        String smsType = "2";
//        AjaxResult ajaxResult = systemFeignClient.getConfValue(MsgConstants.SYS_SMS_TYPE);
//        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
//            SysConfig sysConfig = JSONObject.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)),SysConfig.class);
//            smsType = sysConfig.getConfigValue();
//        }
        String phone = commonSmsRequest.getPhone();
        String msgCode = commonSmsRequest.getSmsCode();
        String msgText = commonSmsRequest.getSmsTxt();
        String smsTemplate = commonSmsRequest.getSmsTemplate();
        Map<String, String> resultMap = new HashMap<>();
        String selectKey="sms";
       if("1".equals(smsType)){
           Map<String, Integer> map = JSON.parseObject(channelRatio, Map.class);
           selectKey = loadBalanceContext.select(map);
           log.info(String.format("获取的短信网关====== %s", selectKey));
           if (isSendVerificationCode) {
               switch (selectKey) {
                   case MsgConstants.ZHONG_SHANG:
                       resultMap = zhongShangSmsCompont.sendVerificationCode(phone, msgCode, smsTemplate);
                       if (!StringUtils.equals(MsgConstants.SMS_SUCCESS, resultMap.get("code"))) {
                           resultMap = aiPaiSmsCompont.sendVerificationCode(phone, msgCode, smsTemplate);
                           selectKey = MsgConstants.AI_PAI;
                       }
                       break;
                   case MsgConstants.AI_PAI:
                       resultMap = aiPaiSmsCompont.sendVerificationCode(phone, msgCode, smsTemplate
                       );
                       if (!StringUtils.equals(MsgConstants.SMS_SUCCESS, resultMap.get("code"))) {
                           resultMap = zhongShangSmsCompont.sendVerificationCode(phone, msgCode, smsTemplate);
                           selectKey = MsgConstants.ZHONG_SHANG;
                       }
                       break;
                   default:
                       resultMap = aiPaiSmsCompont.sendVerificationCode(phone, msgCode, smsTemplate);
                       selectKey = MsgConstants.AI_PAI;
                       if (!StringUtils.equals(MsgConstants.SMS_SUCCESS, resultMap.get("code"))) {
                           resultMap = zhongShangSmsCompont.sendVerificationCode(phone, msgCode, smsTemplate);
                           selectKey = MsgConstants.ZHONG_SHANG;
                       }
               }
           } else {
               switch (selectKey) {
                   case MsgConstants.ZHONG_SHANG:
                       resultMap = zhongShangSmsCompont.sendSmsMessage(phone, msgText);
                       if (!StringUtils.equals(MsgConstants.SMS_SUCCESS, resultMap.get("code"))) {
                           resultMap = aiPaiSmsCompont.sendSmsMessage(phone, msgText);
                           selectKey = MsgConstants.AI_PAI;
                       }
                       break;
                   case MsgConstants.AI_PAI:
                       resultMap = aiPaiSmsCompont.sendSmsMessage(phone, msgText);
                       if (!StringUtils.equals(MsgConstants.SMS_SUCCESS, resultMap.get("code"))) {
                           resultMap = zhongShangSmsCompont.sendSmsMessage(phone, msgText);
                           selectKey = MsgConstants.ZHONG_SHANG;
                       }
                       break;
                   default:
                       resultMap = aiPaiSmsCompont.sendSmsMessage(phone, msgText);
                       selectKey = MsgConstants.AI_PAI;
                       if (!StringUtils.equals(MsgConstants.SMS_SUCCESS, resultMap.get("code"))) {
                           resultMap = zhongShangSmsCompont.sendSmsMessage(phone, msgText);
                           selectKey = MsgConstants.ZHONG_SHANG;
                       }
               }
           }
       }else{
           String msg="";
           String templateCode="";
           if(StrUtil.isEmpty(commonSmsRequest.getTemplateCode())){//如果传过来的模板code是空的则走默认的否则走个性化的
               Map<String,String> map=new HashMap<String,String>();
               map.put("code",commonSmsRequest.getSmsCode());
               msg=JSON.toJSONString(map);
               templateCode=smsTemplateConf.getTemplateCode();
           }else{
               msg=commonSmsRequest.getRequestMap();
               templateCode=commonSmsRequest.getTemplateCode();
           }
           resultMap = itowerSmsUtil.send(msg, phone,templateCode );

           selectKey="sms";


       }

        resultMap.put("gateWay", selectKey);
        return resultMap;
    }

}
