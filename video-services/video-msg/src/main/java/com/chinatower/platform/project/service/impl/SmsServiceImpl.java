package com.chinatower.platform.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.config.SmsTemplateConf;
import com.chinatower.platform.common.constant.MsgConstants;
import com.chinatower.platform.common.utils.CommonUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.constants.CommonConstants;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.CustomSmsTemplateConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.entity.request.MsgRequestVO;
import com.chinatower.platform.enums.SmsResultCode;
import com.chinatower.platform.project.mapper.BsSmsRecordMapper;
import com.chinatower.platform.project.service.CommonSmsDbService;
import com.chinatower.platform.project.service.CommonSmsService;
import com.chinatower.platform.project.service.SmsService;
import com.chinatower.platform.project.vo.SmsRecordQuery;
import com.chinatower.platform.util.OkhttpUtil;
import com.chinatower.platform.utils.SmsUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 短信service实现类
 * @Author: 张帅
 * @Date: 2021/11/12 16:05
 **/
@Service
@Slf4j
@RefreshScope
public class SmsServiceImpl implements SmsService {


    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //private static  String pri = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALL34DxOIwXY82VRyrTWVyFMRzpuvor6msZYudUZI2lWtGcXgBeL5aGc+tRXpcLqwngEMK5LbdD1a6Z0YbPs132Zg3D8lS5rI+ffJeVziWgXENVxIpEnGxxcfwhnrsDg+IMX9PiF4rd2kYenV9Imwvcvw2CZmGDTRrathMMDYaHtAgMBAAECgYAajTHkYIdLp8s8fez/9UjJ9OIgZiu4vMNXp/QyDF+3khiDrvXfCG9SEdMYy8QY7fFaEu0R/rjVJoQJFv0jz4CMr0AGCbHzjkGzV/+sTd5GMgiu5hnNQ68r0Wg4cG1YIUs+lbzaIKd5Uzjmr+R4T2rktK/RaCbqojq5xSIx0+AplQJBANvB24uToYFcneBZ0lrfWGlgqLcWAA8n/VKzH23b6it/C+8Dj4FIr+R/EWuPhCOiINYz32O0bPG5PdUctPvhOmsCQQDQe+imRUNKerUjIZWR+X7A9uWkrXSlJHL8vHVzu+ri/O5TsLj+PY/A/CnOEGOa8BJ4gpMWwrsO/NGa7a1Yn1sHAkAwqwQmKzHPZmWIdBwECAaaf8+djH/C24ZctFK3j8aSj3ZjHpS/ewuJPSLsgUIwag1/oslMWX7vIakx80OZrM21AkEAjLJSe4NaujS9cYwEqJK7m/YDXHNCuQA0KaP1j7lN9wnCvPASqGFbVl3WyJ3+2l0R2gokwcOBAchUnD2yMpC4mQJBAIxmWI+Tjn6nxUwxgEaP5TIfLxo03Wnxd/R+t88JZBx5EtB8OiFEEpKFHN+THO/aQeB8KnAA26qmbmeMn9D2OUY=";
    /**
     * 注入短信发送模块dao
     */
    @Resource
    private BsSmsRecordMapper bsSmsRecordMapper;

    /**
     * 短信登录错误次数记录
     */
    public static final String SMS_FAIL_NUM_KEY = "sms_login_fail_num:";

    /**
     * 超时时间设置（秒）
     */
    @Value("${sms.timeout:60}")
    private String smsTimeout;

    /**
     * 短信调用地址
     */
    @Value("${sms.url}")
    private String url;

    /**
     * 调用签名
     */
    @Value("${sms.pri}")
    private String pri;

    /**
     * 短信平台账号id
     */
    @Value("${sms.tenantId}")
    private String tenantId;

    /**
     * 间隔时间
     */
//    @Value("${sms.timeInterval:60}")
//    private String timeInterval;

    /**
     * 10分钟发送次数
     */
//    @Value("${sms.tenMinutesCount:3}")
//    private int tenMinutesCount;

    /**
     * 一天发送次数
     */
//    @Value("${sms.dailyCount:20}")
//    private int dailyCount;

    @Autowired
    @Lazy
    private CommonSmsService commonSmsService;

    @Resource
    private CommonSmsDbService commonSmsDbService;

    @Resource
    private SmsTemplateConf smsTemplateConf;

    @Autowired
    private SystemFeignClient systemFeignClient;

    private static final String ERROR_MSG = "非法请求!";

    private static final String MSG_ID = "msgId";

    private static final String SEND_SMS_SUCCESS = "发送短信成功";

    private static final String COMMA = ",";

    private static final String ERROR_PHONENUMBER = "手机号为空!";

    private static final String ERROR_TEMPLATE = "模板号为空!";

    private static final String ERROR_TENANTID = "租户ID为空!";


    /**
     * 公网发送短信验证码
     */
    @Override
    public Result sendShortMessage(String phone, String header, String aesPhone, String templateKey) {
        try {
            //1.timeIntervalKey ttl默认是60，如果此key存在则不发送 60s内一个手机只能发一次
            String uniKey = templateKey + ":" + phone;
            String timeIntervalKey = MsgConstants.SMS_INTERVAL_TIME_KEY + uniKey;
            String timeIntervalValue = uniKey + " : " + simpleDateFormat.format(new Date());
            // 短信验证码间隔时间（秒）
            String timeInterval = this.getConfigValue(MsgConstants.SMS_TIME_INTERVAL);
            log.info(MsgConstants.SMS_TIME_INTERVAL_INFO + timeInterval);
            if (!RedisUtil.setIfAbsent(timeIntervalKey, timeIntervalValue, Long.parseLong(timeInterval), TimeUnit.SECONDS, RedisDBType.PRIMARY)) {
                log.info("{} 发送短信频繁", phone);
                return Result.fail(SmsResultCode.SEND_TOO_FAST, "发送短信太频繁，请" + timeInterval + "秒以后再次尝试！");
            }
            //2.校验加密是否正确
            if (!"".equals(header) && null != header) {
                boolean flag = SmsUtils.checkDynamicKey(header, phone, aesPhone);
                if (!flag) {
                    log.info("{} 非法请求!", phone);
                    return Result.fail(ERROR_MSG);
                }
            }
            //3.发送次数限制
            boolean checkCount = this.checkCount(phone);
            if (!checkCount) {
                log.info("{} 发送短信频繁，请稍后再试！", phone);
                return Result.fail(SmsResultCode.SEND_TIMES_LIMIT, "发送短信频繁，请稍后再试！");
            }
            //重新获取的时候重置一下错误次数
            RedisUtil.delete(Lists.newArrayList(CommonConstants.SMS_FAIL_NUM_KEY + phone, CommonConstants.SMS_FAIL_NUM_KEY + uniKey), RedisDBType.PRIMARY);
            BsSmsRecord bsSmsRecord = new BsSmsRecord();
            bsSmsRecord.setPhone(phone);
            CommonSmsRequest request = new CommonSmsRequest();
            request.setPhone(phone);
            request.setSmsCode(CommonUtil.getRandom6());
            if (StringUtils.isNotBlank(templateKey)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("code", request.getSmsCode());
                request.setRequestMap(JSON.toJSONString(map));
                request.setTemplateCode(CustomSmsTemplateConstants.PUBLIC_TEMPLATE_KEY_MAP.get(templateKey));
                bsSmsRecord.setSmsTxt(JSON.toJSONString(map)+"--"+request.getTemplateCode());
            }
            long s = System.currentTimeMillis();
            //Map<String, String> map = commonSmsService.sendMessageToPhone(request, true);
            Map<String,Object> map = sendMessageToPhoneNew(request);
            long e = System.currentTimeMillis();
            log.info("短信网关发送短信({})耗时：{}ms", phone, (e - s));
            /*bsSmsRecord.setMsgId(map.get(MSG_ID) == null ? "0" : map.get(MSG_ID));
            bsSmsRecord.setGateWay(map.get("gateWay"));
            bsSmsRecord.setSmsTxt(map.get("msgContent"));*/
            if (ResultCode.SUCCESS.getCode() == Integer.valueOf(String.valueOf(map.get("code")))) {
                bsSmsRecord.setKeyId(header);
                commonSmsDbService.saveSmsRecord(bsSmsRecord, MsgConstants.SMS_SUCCESS);
                // 发送成功（此处是用来校验验证码的地方）
                String smsKey = CommonConstants.SMS_SUCCESS_KEY + phone;
                // 增加验证码类型区分
                String smsKeyPublic = CommonConstants.SMS_SUCCESS_KEY + uniKey;
                // 短信验证码超时时间设置（秒）
                String smsTimeoutParam = this.getConfigValue(MsgConstants.SMS_TIMEOUT);
                log.info(MsgConstants.SMS_TIMEOUT_INFO + smsTimeoutParam);
                long time = Long.parseLong(smsTimeoutParam);
                RedisUtil.set(smsKey, request.getSmsCode(), time, RedisDBType.PRIMARY);
                RedisUtil.set(smsKeyPublic, request.getSmsCode(), time, RedisDBType.PRIMARY);
                Boolean rt = RedisUtil.hasKey(smsKey, RedisDBType.PRIMARY);
                //redis加次数
                this.increase(phone);
                return Result.success(SEND_SMS_SUCCESS + rt);
            }
            //String errorMsg = map.get("message");
            bsSmsRecord.setFailReason(JSON.toJSONString(map));
            commonSmsDbService.saveSmsRecord(bsSmsRecord, MsgConstants.SMS_ERROR);
            return Result.fail("发送失败，" + JSON.toJSONString(map));
        } catch (Exception e) {
            log.error("发送短信验证码失败{}", e.getMessage(), e);
            return Result.fail("发送短信验证码失败");
        }
    }

    /**
     * 内网发送短信
     *
     * @param request
     * @return
     */
    @Override
    public Result inSideSendMessage(CommonSmsRequest request) {
        try {
            log.info("短信发送入参：{}",JSON.toJSONString(request));
            //String phone = request.getPhone();
            List<BsSmsRecord> recordList = new ArrayList<>();
            String[] phonesList = request.getPhone().split(",");
            for (String phone: phonesList) {
                BsSmsRecord bsSmsRecord = new BsSmsRecord();
                bsSmsRecord.setPhone(phone);
                request.setPhone(phone);
                Map<String, Object> map = new HashMap<>();
                if ("2".equals(request.getMsgType())) {//短信
                /*map = commonSmsService.sendMessageToPhone(request, false);
                bsSmsRecord.setMsgId(map.get(MSG_ID) == null ? "0" : map.get(MSG_ID));
                bsSmsRecord.setGateWay(map.get("gateWay"));
                bsSmsRecord.setSmsTxt(request.getSmsTxt());*/
                    String phones = request.getPhone();
                    List<String> phoneList = new ArrayList<>();
                    if (phones.contains(COMMA)) {
                        String[] phoneSplit = phones.split(COMMA);
                        phoneList.addAll(Arrays.asList(phoneSplit));
                    } else {
                        phoneList.add(phones);
                    }
                    map = sendMessageToPhoneNew(request);
                    if (ResultCode.SUCCESS.getCode() == Integer.valueOf(String.valueOf(map.get("code")))) {
                        commonSmsDbService.saveBatchSmsRecord(bsSmsRecord, phoneList, recordList, MsgConstants.SMS_SUCCESS);
                    } else {
                        bsSmsRecord.setFailReason(JSON.toJSONString(map));
                        commonSmsDbService.saveBatchSmsRecord(bsSmsRecord, phoneList, recordList, MsgConstants.SMS_ERROR);
                    }
                } else {//短信验证码
                    //map = commonSmsService.sendMessageToPhone(request, true);
                    map = sendMessageToPhoneNew(request);
                    if (ResultCode.SUCCESS.getCode() == Integer.valueOf(String.valueOf(map.get("code")))) {
                        //重新获取的时候重置一下错误次数
                        RedisUtil.delete(CommonConstants.SMS_FAIL_NUM_KEY + phone, RedisDBType.PRIMARY);
                        commonSmsDbService.saveSmsRecord(bsSmsRecord, MsgConstants.SMS_SUCCESS);
                        // 发送成功（此处是用来校验验证码的地方）
                        String smsKey = CommonConstants.SMS_SUCCESS_KEY + phone;
                        log.info("发送验证码缓存key:{}",smsKey);
                        log.info("发送验证码缓存value:{}",request.getSmsCode());
                        RedisUtil.set(smsKey, request.getSmsCode(), Long.parseLong(smsTimeout), RedisDBType.PRIMARY);
                        Boolean rt = RedisUtil.hasKey(smsKey, RedisDBType.PRIMARY);
                        //redis加次数 内部接口无需加次数
//                    this.increase(phone);
                    } else {
                        //String errorMsg = map.get("message");
                        bsSmsRecord.setFailReason(JSON.toJSONString(map));
                        commonSmsDbService.saveSmsRecord(bsSmsRecord, MsgConstants.SMS_ERROR);
                        return Result.fail("发送失败，" + JSON.toJSONString(map));
                    }
                }
            }
            log.info("批量保存:{}", recordList);
            if (!CollectionUtils.isEmpty(recordList)) {
                bsSmsRecordMapper.batchInsert(recordList);
            }
            return Result.success(SEND_SMS_SUCCESS);
        } catch (Exception e) {
            log.error("发送短信验证码失败{}", e.getMessage());
            log.error(e.getMessage(), e);
            return Result.fail(ERROR_MSG);
        }
    }


    public Map<String,Object> sendMessageToPhoneNew(CommonSmsRequest request){
        Map<String, Object> map = new HashMap<>();
        MsgRequestVO msgRequest = new MsgRequestVO();
        msgRequest.setSmsTemplateCode(request.getTemplateCode());
        msgRequest.setPhoneNumber(request.getPhone());
        msgRequest.setParaMap(request.getRequestMap());
        log.info("sendMessageToPhoneNew-入参:{}",JSON.toJSONString(msgRequest));
        Result result = this.innovationSendMessage(msgRequest);
        log.info("sendMessageToPhoneNew-出参:{}",JSON.toJSONString(result));
        try{
            if(ResultCode.SUCCESS.getCode() == result.getCode()){
                map = JSON.parseObject(JSON.toJSONString(result.getData()),Map.class);
            }else{
                map.put("code",400);
                map.put("msg", Constants.FAIL_MSG_SMS);
            }
        }catch(Exception e){
            map.put("code",400);
            map.put("msg",Constants.FAIL_MSG_SMS);
        }
        return map;
    }

    @Override
    public Result innovationSendMessage(MsgRequestVO request) {
        log.info("innovationSendMessage入参:{}",JSON.toJSONString(request));
        if(StringUtils.isEmpty(request.getPhoneNumber())){
            return Result.fail(ERROR_PHONENUMBER);
        }
        if(StringUtils.isEmpty(request.getSmsTemplateCode())){
            return Result.fail(ERROR_TEMPLATE);
        }
        try {
            request.setTenantId(tenantId);
            request.setTimestamp(System.currentTimeMillis()+"");
            //map2.put("paraMap",JSON.toJSONString(map));
            request.setParaMap(request.getParaMap());
            Map<String,Object> map2= JSONObject.parseObject(JSON.toJSONString(request),Map.class);
            String str= getSignContent(map2);
            String localTemp = str+ "&key=" + pri;
            request.setSign(RsaUtils.sign(localTemp,pri));
            JSONObject result= OkhttpUtil.doPost(JSON.toJSONString(request),url);
            log.info("innovationSendMessage-resp:{}",JSON.toJSONString(result));
            return Result.data(result);
        }catch (Exception e){
            log.error("innovationSendMessage-发送短信异常:"+e);
            return Result.fail(Constants.FAIL_MSG_SMS);
        }
    }

    /*public static void main(String[] args) throws Exception {
        MsgRequestVO request = new MsgRequestVO();
        request.setTenantId("video20230824");
        request.setSmsTemplateCode("smsvideo20230824197");
        request.setPhoneNumber("16696694000");
        request.setTimestamp(System.currentTimeMillis()+"");
        //map2.put("paraMap",JSON.toJSONString(map));
        Map<String,Object> map = new HashMap<>();
        map.put("password","B_&v9IwdF8TX#f~");
        map.put("userName","liusen501");
        map.put("url","http://comp.tower0788.cn:9770/msg/sendMessage");
        request.setParaMap(JSON.toJSONString(map));
        Map<String,Object> map2= JSONObject.parseObject(JSON.toJSONString(request),Map.class);
        String str= getSignContent(map2);
        System.out.println(str);
        String localTemp = str+ "&key=" + "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALL34DxOIwXY82VRyrTWVyFMRzpuvor6msZYudUZI2lWtGcXgBeL5aGc+tRXpcLqwngEMK5LbdD1a6Z0YbPs132Zg3D8lS5rI+ffJeVziWgXENVxIpEnGxxcfwhnrsDg+IMX9PiF4rd2kYenV9Imwvcvw2CZmGDTRrathMMDYaHtAgMBAAECgYAajTHkYIdLp8s8fez/9UjJ9OIgZiu4vMNXp/QyDF+3khiDrvXfCG9SEdMYy8QY7fFaEu0R/rjVJoQJFv0jz4CMr0AGCbHzjkGzV/+sTd5GMgiu5hnNQ68r0Wg4cG1YIUs+lbzaIKd5Uzjmr+R4T2rktK/RaCbqojq5xSIx0+AplQJBANvB24uToYFcneBZ0lrfWGlgqLcWAA8n/VKzH23b6it/C+8Dj4FIr+R/EWuPhCOiINYz32O0bPG5PdUctPvhOmsCQQDQe+imRUNKerUjIZWR+X7A9uWkrXSlJHL8vHVzu+ri/O5TsLj+PY/A/CnOEGOa8BJ4gpMWwrsO/NGa7a1Yn1sHAkAwqwQmKzHPZmWIdBwECAaaf8+djH/C24ZctFK3j8aSj3ZjHpS/ewuJPSLsgUIwag1/oslMWX7vIakx80OZrM21AkEAjLJSe4NaujS9cYwEqJK7m/YDXHNCuQA0KaP1j7lN9wnCvPASqGFbVl3WyJ3+2l0R2gokwcOBAchUnD2yMpC4mQJBAIxmWI+Tjn6nxUwxgEaP5TIfLxo03Wnxd/R+t88JZBx5EtB8OiFEEpKFHN+THO/aQeB8KnAA26qmbmeMn9D2OUY=";
        request.setSign(RsaUtils.sign(localTemp,"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALL34DxOIwXY82VRyrTWVyFMRzpuvor6msZYudUZI2lWtGcXgBeL5aGc+tRXpcLqwngEMK5LbdD1a6Z0YbPs132Zg3D8lS5rI+ffJeVziWgXENVxIpEnGxxcfwhnrsDg+IMX9PiF4rd2kYenV9Imwvcvw2CZmGDTRrathMMDYaHtAgMBAAECgYAajTHkYIdLp8s8fez/9UjJ9OIgZiu4vMNXp/QyDF+3khiDrvXfCG9SEdMYy8QY7fFaEu0R/rjVJoQJFv0jz4CMr0AGCbHzjkGzV/+sTd5GMgiu5hnNQ68r0Wg4cG1YIUs+lbzaIKd5Uzjmr+R4T2rktK/RaCbqojq5xSIx0+AplQJBANvB24uToYFcneBZ0lrfWGlgqLcWAA8n/VKzH23b6it/C+8Dj4FIr+R/EWuPhCOiINYz32O0bPG5PdUctPvhOmsCQQDQe+imRUNKerUjIZWR+X7A9uWkrXSlJHL8vHVzu+ri/O5TsLj+PY/A/CnOEGOa8BJ4gpMWwrsO/NGa7a1Yn1sHAkAwqwQmKzHPZmWIdBwECAaaf8+djH/C24ZctFK3j8aSj3ZjHpS/ewuJPSLsgUIwag1/oslMWX7vIakx80OZrM21AkEAjLJSe4NaujS9cYwEqJK7m/YDXHNCuQA0KaP1j7lN9wnCvPASqGFbVl3WyJ3+2l0R2gokwcOBAchUnD2yMpC4mQJBAIxmWI+Tjn6nxUwxgEaP5TIfLxo03Wnxd/R+t88JZBx5EtB8OiFEEpKFHN+THO/aQeB8KnAA26qmbmeMn9D2OUY="));
        JSONObject result= OkhttpUtil.doPost(JSON.toJSONString(request),"http://comp.tower0788.cn:9770/msg/sendMessage");
        System.out.println(JSON.toJSONString(result));
    }*/

    @Override
    public Result batchSendMessage(List<CommonSmsRequest> list) {
        list.forEach(item -> {
            try {
                inSideSendMessage(item);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        });
        return Result.success(SEND_SMS_SUCCESS);
    }


    @Override
    @Async("msgTaskExecutor")
    public void updateSmsRecordStatByMsgId(BsSmsRecord record) {
        bsSmsRecordMapper.updateByMsgId(record);
    }

    @Override
    public List<BsSmsRecord> list(SmsRecordQuery query) {
        return bsSmsRecordMapper.selectList(query);
    }

    @Override
    public Result checkPhoneCode(String phone, String code, String templateKey, boolean canRepeatCheck) {
        if (StrUtil.isBlank(phone)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
        }
        String uniKey = phone;
        if (StringUtils.isNotBlank(templateKey)) {
            uniKey = templateKey + ":" + phone;
        }
        return checkPhoneCode(uniKey, code, canRepeatCheck);
    }

    /**
     * 将map按照 ASCII 码从小到大排序（
     * @return
     */
    /**
     * map排序返回字符串
     * @param sortedParams
     * @return
     */
    public static String getSignContent(Map<String, Object> sortedParams) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(sortedParams.keySet());
        Collections.sort(keys);
        int index = 0;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = sortedParams.get(key);
            if(value!=null&&!com.chinatower.common.core.utils.StringUtils.isEmpty(value.toString())){
                content.append((index == 0 ? "" : "&") + key + "=" + value.toString());
                index++;
            }
        }
        return content.toString();
    }

    private Result checkPhoneCode(String uniKey, String code, boolean canRepeatCheck) {
        String failNumKey = CommonConstants.SMS_FAIL_NUM_KEY + uniKey;
        String smsKey = CommonConstants.SMS_SUCCESS_KEY + uniKey;
        String smsCheckPossWordKey = "smsCheckPossWordKey:" + uniKey;
        log.info("校验验证码缓存key:{}",smsKey);
        log.info("校验验证码缓存smsCheckPossWordKey:{}",smsCheckPossWordKey);
        String failNumStr = RedisUtil.get(failNumKey, RedisDBType.PRIMARY);
        int failNum = StrUtil.isNotBlank(failNumStr) ? Integer.parseInt(failNumStr) : 0;
        if (failNum >= CommonConstants.CAPTCHA_FAIL_MAX) {
            //删除验证码
            RedisUtil.delete(smsKey, RedisDBType.PRIMARY);
            return Result.fail(SmsResultCode.VERIFY_TIME_OUT);
        }
        String cacheCode = RedisUtil.get(smsKey, RedisDBType.PRIMARY);
        log.info("校验验证码缓存value:{}",cacheCode);
        boolean success = StrUtil.isNotBlank(cacheCode) && cacheCode.equals(code);
        if (success) {
            //校验成功就删除校验失败记录
            RedisUtil.delete(failNumKey, RedisDBType.PRIMARY);
            if (!canRepeatCheck) {
                //删除验证码
                RedisUtil.delete(smsKey, RedisDBType.PRIMARY);
            }
            try{
                RedisUtil.set(smsCheckPossWordKey, "1", 180, RedisDBType.PRIMARY);
            }catch (Exception e){
                log.error("校验验证码缓存set error, e:", e);
            }
            return Result.success("校验成功");
        } else {
            //增加错误次数
            RedisUtil.set(failNumKey, (failNum + 1) + "", 1800, RedisDBType.PRIMARY);
            return Result.fail(SmsResultCode.VERIFY_ERROR);
        }
    }

    /**
     * 功能说明：校验短信次数  每10分钟允许发3次，每天允许发二十次
     *
     * @author 张帅
     * @date 2021/12/3 16:03
     */
    private boolean checkCount(String phone) {
        boolean flag = true;
        String tempKey = MsgConstants.SMS_LIMIT_COUNT_KEY + phone;
        if (RedisUtil.hasKey(tempKey, RedisDBType.PRIMARY)) {
            int count = Integer.parseInt(RedisUtil.get(tempKey, RedisDBType.PRIMARY));
            // 短信验证码10分钟发送次数上限
            int tenMinutesCount = Integer.parseInt(this.getConfigValue(MsgConstants.SMS_TEN_MINUTES_COUNT));
            log.info(MsgConstants.SMS_TEN_MINUTES_COUNT_INFO + tenMinutesCount + ",手机号：" + phone + ",当前次数：" + count );
            if (count >= tenMinutesCount) {
                flag = false;
                return flag;
            }
        } else {
            RedisUtil.set(tempKey, 0, 10 * 60, RedisDBType.PRIMARY);
        }
        String dailyKey = MsgConstants.SMS_LIMIT_DAY_COUNT_KEY + phone;
        if (RedisUtil.hasKey(dailyKey, RedisDBType.PRIMARY)) {
            int count = Integer.parseInt(RedisUtil.get(dailyKey, RedisDBType.PRIMARY));
            int dailyCount = Integer.parseInt(this.getConfigValue(MsgConstants.SMS_DAILY_COUNT));
            log.info(MsgConstants.SMS_DAILY_COUNT_INFO + dailyCount + ",手机号：" + phone + ",当前次数：" + count );
            if (count >= dailyCount) {
                flag = false;
            }
        } else {
            RedisUtil.set(dailyKey, 0, 60 * 60 * 24, RedisDBType.PRIMARY);
        }
        return flag;
    }

    /**
     * 发送短信之后在redis中对对应的key进行增量
     * 修复：incrBy创建的key没有过期时间的问题
     */
    private void increase(String phone) {
        String tempKey = MsgConstants.SMS_LIMIT_COUNT_KEY + phone;
        RedisUtil.incrBy(tempKey, 1, RedisDBType.PRIMARY);
        long ttl = RedisUtil.getExpire(tempKey, RedisDBType.PRIMARY);
        if (ttl <= 0) {
            RedisUtil.expire(tempKey, 10 * 60, TimeUnit.SECONDS, RedisDBType.PRIMARY);
        }

        String dailyKey = MsgConstants.SMS_LIMIT_DAY_COUNT_KEY + phone;
        RedisUtil.incrBy(dailyKey, 1, RedisDBType.PRIMARY);
        long dailyTtl = RedisUtil.getExpire(dailyKey, RedisDBType.PRIMARY);
        if (dailyTtl <= 0) {
            RedisUtil.expire(dailyKey, 60 * 60 * 24, TimeUnit.SECONDS, RedisDBType.PRIMARY);
        }
    }

    public String getConfigValue(String key){
        String configValue = MsgConstants.STRING_BLANK;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(key);
        if (MsgConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            configValue = StringUtils.defaultIfBlank(sysConfig.getConfigValue(), MsgConstants.STRING_BLANK);
        }
        return configValue;
    }
}
