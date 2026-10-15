package com.chinatower.platform.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.jpush.api.push.model.BatchPushResult;
import com.chinatower.platform.common.constant.MsgConstants;
import com.chinatower.platform.common.utils.CommonUtil;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.project.entity.request.JpushResDto;
import com.chinatower.platform.project.mongodb.domain.BsJpushRecordMongo;
import com.chinatower.platform.project.service.CommonSmsDbService;
import com.chinatower.platform.project.service.PushClientEquipmentService;
import com.chinatower.platform.project.utils.JpushClientUtil;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author
 */
@Service
@Slf4j
public class PushClientEquipmentServiceImpl implements PushClientEquipmentService {

    @Autowired
    private JpushClientUtil jpushClientUtil;


    private final String PUBLISH_MSG_RESULT_LOG_PRE = "推送消息结果{}";

    private final String JPUSH_LOG_PRE_1 = "jpush client 1:";
    private final String JPUSH_LOG_PRE_2 = "jpush client 2:";

    private final String FAIL_MESSAGE_BATCH_API = "极光批量单推API请求失败-";

    private final String FAIL_MESSAGE_CID_API = "极光获取cid请求失败";

    /**
     * 注入redis服务
     */
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private CommonSmsDbService commonSmsDbService;


    @Override
    public int pushPersonByUserId(JgPushRequest pushNews) {
        log.info("极光指定用户入参{}", pushNews.toString());
        //新版本
        BsJpushRecordMongo bsJpushRecordNew = new BsJpushRecordMongo();
        BeanUtil.copyProperties(pushNews,bsJpushRecordNew);
        bsJpushRecordNew.setType(StringUtils.isEmpty(pushNews.getType()) ? "1" : pushNews.getType());
        bsJpushRecordNew.setAlias(pushNews.getAlias() + "_" + pushNews.getIndustryCode());

        int result = pushNewByUserId(bsJpushRecordNew, pushNews);

        // 鸿蒙推送
        JpushResDto hmosResult = jpushClientUtil.sendToAliasHmos(
                pushNews.getAlias() + "_" + pushNews.getIndustryCode(),
                pushNews.getNotificationTitle(),
                pushNews.getMsgTitle(),
                pushNews.getMsgContent(),
                pushNews.getExtrasparam(),
                pushNews.getType(),
                pushNews.getSound(),
                pushNews.getMsgType());
        log.info("鸿蒙推送结果:{}", hmosResult.getCode() == 1 ? "成功" : "失败");

        return result;
    }

    private int pushNewByUserId(BsJpushRecordMongo bsJpushRecord, JgPushRequest pushNews) {
        //查询剩余未读消息
        log.info("查询别名:{}", bsJpushRecord.getAlias());
        JpushResDto jpushResDto = jpushClientUtil.sendToAlias(bsJpushRecord.getAlias(), pushNews.getNotificationTitle(),
                pushNews.getMsgTitle(), pushNews.getMsgContent(), pushNews.getExtrasparam(), pushNews.getType());
        if (1 == jpushResDto.getCode()) {
            commonSmsDbService.saveJpushRecordMongo(bsJpushRecord,MsgConstants.SMS_SUCCESS);
        } else {
//            bsJpushRecord.setFailReason(jpushResDto.getMsg());
//            commonSmsDbService.saveJpushRecordMongo(bsJpushRecord,MsgConstants.SMS_ERROR);
            log.error("极光推送消息异常:{}", jpushResDto.getMsg());
        }
        log.info(PUBLISH_MSG_RESULT_LOG_PRE, jpushResDto.getCode() == 1 ? "成功" : "失败");
        return jpushResDto.getCode();
    }

    @Override
    public int pushToAllAndroidOrIos(JgPushRequest pushNews, String type) {
        String defaultType = "Android";
        log.info("极光{}用户入参{}", type, pushNews.toString());
        int androidIosResult = 0;
        try {
            if (StringUtils.equals(defaultType, type)) {
                androidIosResult = jpushClientUtil.sendToAllAndroid(pushNews.getNotificationTitle(),
                        pushNews.getMsgTitle(), pushNews.getMsgContent(), pushNews.getExtrasparam());
                log.info(PUBLISH_MSG_RESULT_LOG_PRE, androidIosResult);
                return androidIosResult;
            }
            androidIosResult = jpushClientUtil.sendToAllIos(pushNews.getNotificationTitle(),
                    pushNews.getMsgTitle(), pushNews.getMsgContent(), pushNews.getExtrasparam());
            log.info(PUBLISH_MSG_RESULT_LOG_PRE, androidIosResult);
            return androidIosResult;
        } catch (Exception e) {
            log.error("极光推送消息异常");
            log.error(e.getMessage(), e);
        }
        return androidIosResult;
    }

    @Override
    @Async("msgTaskExecutor")
    public void batchPushNews(List<JgPushRequest> jgPushRequestList) {
        log.info("极光推送批量用户入参{}", jgPushRequestList.toString());
        List<BsJpushRecordMongo> list = new ArrayList<>();

        if (jgPushRequestList.isEmpty()) {
            return ;
        }
        //提前设置msg id 和别名
        List<JgPushRequest> pushNews = new ArrayList<>();
        jgPushRequestList.forEach(item -> {
            JgPushRequest jgPushRequestNew = new JgPushRequest();
            BeanUtil.copyProperties(item, jgPushRequestNew);
            jgPushRequestNew.setAlias(item.getAlias() + "_" + item.getIndustryCode());
            //提前生成id
            jgPushRequestNew.setMsgId(CommonUtil.getUuid());
            pushNews.add(jgPushRequestNew);
        });
        //获取不同用户的 未读消息数 map  如果不存在该map种 则为0
//        Map<String, Integer> noReadByUserList = commonSmsDbService.getNoReadByUserList(pushNews);
        //获得cid
        List<String> cidListByClient = jpushClientUtil.getCidList(pushNews, jpushClientUtil.jPushClient1);
        List<String> cidListByClient2 = jpushClientUtil.getCidList(pushNews, jpushClientUtil.jPushClient2);

        if (cidListByClient.isEmpty() && cidListByClient2.isEmpty()) {
            //全部失败
            pushNews.forEach(item -> {
                BsJpushRecordMongo bsJpushRecord = getJpushRecordMongo(item, MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_0, "无可用cid");
                list.add(bsJpushRecord);
            });
            //保存
            commonSmsDbService.saveJpushRecordMongoBatch(list);
            return ;
        }
        //client 1 {alias_industryCode:cid}
        Map<String, String> aliasAndCidMapClient1 = new HashMap<>();
        //client 2 {alias_industryCode:cid}
        Map<String, String> aliasAndCidMapClient2 = new HashMap<>();

        for (int i = 0; i < pushNews.size(); i++) {
            if (!cidListByClient.isEmpty()) {
                aliasAndCidMapClient1.put(pushNews.get(i).getAlias(), cidListByClient.get(i));
            }
            if (!cidListByClient2.isEmpty()) {
                aliasAndCidMapClient2.put(pushNews.get(i).getAlias(), cidListByClient2.get(i));
            }
        }


        //client1
        if (!cidListByClient.isEmpty()) {

            BatchPushResult result1 = jpushClientUtil.batchSendNews(pushNews, cidListByClient, jpushClientUtil.jPushClient1);
            if (ObjectUtil.isNotEmpty(result1) && result1.isResultOK()){
                //返回成功 挨个判断结果
                Map<String, BatchPushResult.PushResult> batchPushResult = result1.getBatchPushResult();
                pushNews.forEach(item -> {
                    BatchPushResult.PushResult pushResult = batchPushResult.get(aliasAndCidMapClient1.get(item.getAlias()));
                    if (null == pushResult.error) {
                        //success
                        BsJpushRecordMongo bsJpushRecord = getJpushRecordMongo(item, MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_1, null);
                        list.add(bsJpushRecord);
                    } else {
                        //error
                        String msg = JPUSH_LOG_PRE_1 + pushResult.error.getCode() + " " + pushResult.error.getMessage();
                        BsJpushRecordMongo bsJpushRecord = getJpushRecordMongo(item, MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_0, msg);
                        list.add(bsJpushRecord);
                    }
                });
            } else {
                //全部失败
                pushNews.forEach(item -> {
                    BsJpushRecordMongo bsJpushRecord = getJpushRecordMongo(item, MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_0, JPUSH_LOG_PRE_1 + FAIL_MESSAGE_BATCH_API + result1);
                    list.add(bsJpushRecord);
                });
                log.info(JPUSH_LOG_PRE_1 + FAIL_MESSAGE_BATCH_API + result1);
            }
        } else {
            //全部失败
            pushNews.forEach(item -> {
                BsJpushRecordMongo bsJpushRecord = getJpushRecordMongo(item, MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_0, JPUSH_LOG_PRE_1 + FAIL_MESSAGE_CID_API);
                list.add(bsJpushRecord);
            });
        }

        Iterator<BsJpushRecordMongo> it = list.iterator();
        //client2
        if (!cidListByClient2.isEmpty()) {

            BatchPushResult result2 = jpushClientUtil.batchSendNews(pushNews, cidListByClient2, jpushClientUtil.jPushClient2);
            if (ObjectUtil.isNotEmpty(result2) && result2.isResultOK()) {
                //返回成功 挨个判断结果
                Map<String, BatchPushResult.PushResult> batchPushResult = result2.getBatchPushResult();
                //移除失败的数据
                while (it.hasNext()) {
                    BsJpushRecordMongo item = it.next();
                    BatchPushResult.PushResult pushResult = batchPushResult.get(aliasAndCidMapClient2.get(item.getAlias()));
                    if (null == pushResult.error) {
                        //success
                        item.setSendResult(MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_1);
                    } else {
                        //error  两个客户端 有一个成功就算成功
                        if (MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_0.equals(item.getSendResult())) {
                            String msg = JPUSH_LOG_PRE_2 + pushResult.error.getCode() + " " + pushResult.error.getMessage();
                            log.info("极光消息发送失败-消息体:{} 错误信息:{}",item, item.getFailReason() + " " + msg);
                            it.remove();
                        }
                    }
                }
            } else {
                removeFailMsg(it, JPUSH_LOG_PRE_2 + FAIL_MESSAGE_BATCH_API + result2);
            }
        } else {
            removeFailMsg(it, JPUSH_LOG_PRE_2 + FAIL_MESSAGE_CID_API);
        }

        // 鸿蒙批量推送
        batchPushHmosIfNeeded(pushNews);

        //保存
        commonSmsDbService.saveJpushRecordMongoBatch(list);
    }

    /**
     * 鸿蒙批量推送：所有推送项都走鸿蒙推送
     */
    private void batchPushHmosIfNeeded(List<JgPushRequest> pushNews) {
        log.info("鸿蒙批量推送，共{}条", pushNews.size());

        // 推送鸿蒙 - pushApi1
        List<String> cidListHmos1 = jpushClientUtil.getCidList(pushNews, jpushClientUtil.jPushClient1);
        if (!cidListHmos1.isEmpty()) {
            jpushClientUtil.batchSendHmos(pushNews, cidListHmos1, jpushClientUtil.pushApi1);
        }

        // 推送鸿蒙 - pushApi2
        List<String> cidListHmos2 = jpushClientUtil.getCidList(pushNews, jpushClientUtil.jPushClient2);
        if (!cidListHmos2.isEmpty()) {
            jpushClientUtil.batchSendHmos(pushNews, cidListHmos2, jpushClientUtil.pushApi2);
        }
    }

    /**
     * 移除发送失败信息
     */
    private void removeFailMsg(Iterator<BsJpushRecordMongo> it, String msg) {
        while (it.hasNext()) {
            BsJpushRecordMongo item = it.next();
            if (MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_0.equals(item.getSendResult())) {
                it.remove();
                log.info("极光消息发送失败-消息体:{} 错误信息:{}",item, item.getFailReason() + " " + msg);
            }
        }
    }

    private BsJpushRecordMongo getJpushRecordMongo (JgPushRequest pushNews, String sendResult, String failReason) {
        BsJpushRecordMongo bsJpushRecord = new BsJpushRecordMongo();
        BeanUtil.copyProperties(pushNews,bsJpushRecord);
        Date todayDate = new Date();
        bsJpushRecord.setId(pushNews.getMsgId());
        bsJpushRecord.setCreateTime(todayDate);
        bsJpushRecord.setSendTime(todayDate);
        if (StringUtils.isEmpty(pushNews.getType()) || "4".equals(pushNews.getType())) {
            bsJpushRecord.setType("1");
        } else {
            bsJpushRecord.setType(pushNews.getType());
        }
        bsJpushRecord.setDelFlag(MsgConstants.JPUSH_RECORD_FIELD_DEL_FLAG_0);
        bsJpushRecord.setIsRead(MsgConstants.JPUSH_RECORD_FIELD_SEND_IS_READ_0);
        bsJpushRecord.setSendResult(sendResult);
        if (StringUtils.isNotEmpty(failReason)) {
            bsJpushRecord.setFailReason(failReason);
        }
        return bsJpushRecord;
    }

}
