package com.chinatower.platform.modules.alarm.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.modules.alarm.dao.AlarmStudyAndJudgeSmsNoticeDAO;
import com.chinatower.platform.modules.alarm.entity.JudgeAlarmSmsNoticeVO;
import com.chinatower.platform.modules.alarm.service.AlarmStudyAndJudgeSmsNoticeService;
import com.chinatower.platform.modules.alarm.service.AlarmTypeService;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @className: AlarmStudyAndJudgeSmsNoticeServiceImpl
 * @description: 研判环节告警短信统计通知服务
 * @author: peng.haibo
 * @date: 2022/6/24
 */
@Service
@Slf4j
public class AlarmStudyAndJudgeSmsNoticeServiceImpl implements AlarmStudyAndJudgeSmsNoticeService {

    /**
     * 短信模板编码
     */
    // @Value("${sms.templateCode:rwib_zdoh}")
    private String templateCode = "rwib_zdoh";

    private static ConcurrentMap<String, Date> currentLastEexecutionTimeMap = new ConcurrentHashMap<>();

    @Resource
    private AlarmStudyAndJudgeSmsNoticeDAO alarmStudyAndJudgeSmsNoticeDAO;

    @Autowired
    private AlarmTypeService alarmTypeService;

    @Autowired
    private MsgFeignClient msgFeignClient;

    /**
     * @param warningTypeId 告警类型ID
     * @description: 处理研判告警统计短信通知
     * @author: peng.haibo
     * @date: 2022/6/24
     */
    @Override
    public void dealEventJudgeAlarmSmsNotice(String warningTypeId) {
        JudgeAlarmSmsNoticeVO judgeAlarmSmsNoticeVO = new JudgeAlarmSmsNoticeVO();
        judgeAlarmSmsNoticeVO.setWarningTypeId(warningTypeId);
        judgeAlarmSmsNoticeVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));

        List<JudgeAlarmSmsNoticeVO>  judgeAlarmSmsNoticeVOList = alarmStudyAndJudgeSmsNoticeDAO.getJudgeAlarmNumList(judgeAlarmSmsNoticeVO);
        if (CollectionUtils.isEmpty(judgeAlarmSmsNoticeVOList)) {
            return;
        }
        // 根据flowId分组
        Map<String, List<JudgeAlarmSmsNoticeVO>> judgeAlarmSmsNoticeVOListMap = judgeAlarmSmsNoticeVOList.stream().collect(Collectors.groupingBy(JudgeAlarmSmsNoticeVO::getFlowId));

        String cacheKey;
        log.info("根据flowId分组judgeAlarmSmsNoticeVOListMap值为：{}", judgeAlarmSmsNoticeVOListMap);
        for (Map.Entry<String, List<JudgeAlarmSmsNoticeVO>> entry: judgeAlarmSmsNoticeVOListMap.entrySet()) {
            String flowId = entry.getKey();
            List<JudgeAlarmSmsNoticeVO> judgeAlarmSmsNoticeVOs = entry.getValue();
            if (CollectionUtils.isEmpty(judgeAlarmSmsNoticeVOs)) {
                // 按理应该不会为空
                continue;
            }

            cacheKey = getCacheKey(warningTypeId, flowId);
            log.info("获取缓存key为：{}", cacheKey);
            Date lastEexecutionTime = null;
            // 冲缓存里面获取上次执行发送短信的时间
            try {
                lastEexecutionTime = RedisUtil.get(cacheKey, Date.class, RedisDBType.PRIMARY);
            } catch (Exception e) {
                log.error("RedisUtil.get Redis connect exception:{}", e);
                // Redis连不上的情况下暂时用下本机时间来跑
                lastEexecutionTime = currentLastEexecutionTimeMap.get(cacheKey);
            }
            log.info("从Redis获取上传发送短信时间为{}", lastEexecutionTime);
            if (lastEexecutionTime == null) {
                // 没取到值， 从每天00:00:00开始计时
                lastEexecutionTime = getZeroPointDate();
            }
            if (!getTimeDifference(new Date(), subtractTime(lastEexecutionTime, judgeAlarmSmsNoticeVOs.get(0)))) {
                // 未到发短信时间
                continue;
            }

            log.info("执行发短信操作executeJudgeAlarmSmsDeal-{}", flowId);
            // 执行发短信操作
            executeJudgeAlarmSmsDeal(warningTypeId, judgeAlarmSmsNoticeVOs);

            try {
                // 设置当前时间为上次执行时间
                RedisUtil.set(getCacheKey(warningTypeId, flowId), new Date(), 24 * 60 * 60, TimeUnit.SECONDS, RedisDBType.PRIMARY);
            } catch (Exception e) {
                log.error("RedisUtil.set Redis connect exception:{}", e);
                // Redis连不上的情况下暂时用下本机时间来跑
                currentLastEexecutionTimeMap.put(cacheKey, new Date());
            }
        }

    }

    private void executeJudgeAlarmSmsDeal(String warningTypeId, List<JudgeAlarmSmsNoticeVO> judgeAlarmSmsNoticeVOs) {
        if (CollectionUtils.isEmpty(judgeAlarmSmsNoticeVOs)) {
            return;
        }
        Map<String, Long> sendNumMap = new HashMap<>();
        Long num;
        for (JudgeAlarmSmsNoticeVO judgeAlarmSmsNoticeVO : judgeAlarmSmsNoticeVOs) {
            if (StringUtils.isEmpty(judgeAlarmSmsNoticeVO.getProcessId())) {
                continue;
            }
            String[] processIds = judgeAlarmSmsNoticeVO.getProcessId().split(com.baomidou.mybatisplus.core.toolkit.Constants.COMMA);
            for (String processId : processIds) {
                if (StringUtils.isEmpty(processId)) {
                    continue;
                }
                num = sendNumMap.get(processId);
                if (num == null) {
                    num = 0L;
                }
                num++;
                sendNumMap.put(processId, num);
            }
        }

        // 查询告警类型名称
        String warningTypeName = alarmTypeService.queryWarningTypeNameByWarningTypeId(warningTypeId);
        if (StringUtils.isEmpty(warningTypeName)) {
            return;
        }
        // 发送短信
        smsMessagePush(warningTypeName, sendNumMap);
    }

    /**
     * 短信发送
     * @param warningTypeName
     * @param sendNumMap
     */
    private void smsMessagePush(String warningTypeName, Map<String, Long> sendNumMap) {
        if (MapUtils.isEmpty(sendNumMap)) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        for (Map.Entry<String, Long> entry: sendNumMap.entrySet()) {
            String userId = entry.getKey();
            Long judgeAlarmNum = entry.getValue();
            if (judgeAlarmNum <= 0) {
                continue;
            }

            try {
                //根据用户ID查询用户信息
                Map<String, String> userInfo = alarmTypeService.getUserInfoById(userId);
                if (MapUtils.isEmpty(userInfo) || StringUtils.isEmpty(MapUtils.getString(userInfo, "mobile_phone"))) {
                    continue;
                }

                String msg = MessageFormat.format(Constants.MSG, currentTime, warningTypeName, judgeAlarmNum);
                HashMap map = new HashMap<>();
                map.put("smsTxt", msg);
                // 内网推送
                CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
                commonSmsRequest.setMsgType(Constants.MSG_TYPE_SMS);
                commonSmsRequest.setPhone(MapUtils.getString(userInfo, "mobile_phone"));
                commonSmsRequest.setRequestMap(JSON.toJSONString(map));
                //templateCode:rwib_zdoh
                commonSmsRequest.setTemplateCode(templateCode);
                if (StringUtils.isEmpty(templateCode)) {
                    commonSmsRequest.setTemplateCode("rwib_zdoh");
                }
                commonSmsRequest.setAppVerCode(null);
                commonSmsRequest.setSmsTxt(msg);
                msgFeignClient.sendMessage(commonSmsRequest);
            } catch (Exception e) {
                log.error("msgFeignClient.sendMessage发送短信异常：{}" + e.getMessage());
            }
        }

    }

    /**
     * @Description 获取缓存key
     * @author peng.haibo
     * @date 2022/6/24
     * @param warningTypeId
     * @param flowId 参数描述
     * @return java.lang.String
     */
    private String getCacheKey(String warningTypeId, String flowId) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("yyyyMMdd");
        String yearMonthDay = format.format(new Date());
        return new StringBuilder("LAST_EXECUTION_TIME_").append(yearMonthDay).append("_").append(warningTypeId).append("_").append(flowId).toString();
    }

    /**
     * 获取0点时间
     * @author peng.haibo
     * @date 2022/6/24
     * @return java.util.Date
     */
    private Date getZeroPointDate() {
        Calendar cal = Calendar.getInstance();
        Date time = cal.getTime();
        String timeStr = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(time);
        return DateUtils.parseDate(timeStr);
    }

    /**
     * @Method : getTimeDifference
     * @Description : 返回true 表示时间到， false表示未到时间
     * @param startTime : 被减数
     * @param targetTime : 减数
     * @return : java.util.Date
     */
    public static boolean getTimeDifference(Date startTime, Date targetTime) {
        if (startTime == null || targetTime == null) {
            return false;
        }
        long difference = startTime.getTime() - targetTime.getTime();
        if (difference >= 0) {
            return true;
        }
        return false;
    }

    /**
     * 加减对应时间后的日期
     *
     * @param date 需要加减时间的日期
     * @param judgeAlarmSmsNoticeVO 加减的时间(毫秒)
     * @return 加减对应时间后的日期
     */
    public static Date subtractTime(Date date, JudgeAlarmSmsNoticeVO judgeAlarmSmsNoticeVO) {
        try {
            String msgNotifyCompany = judgeAlarmSmsNoticeVO.getMsgNotifyCompany();
            String msgNotifyLimit = judgeAlarmSmsNoticeVO.getMsgNotifyLimit();
            if (StringUtils.isEmpty(msgNotifyLimit)) {
                msgNotifyLimit = "1440";
            }
            if (StringUtils.isEmpty(msgNotifyCompany) || !StringUtils.equals("1", msgNotifyCompany)) {
                msgNotifyCompany = "2";
            }
            int amount = Integer.parseInt(msgNotifyLimit);
            if (StringUtils.equals("2", msgNotifyCompany)) {
                // 小时转分钟
                amount = amount * 60;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strTime = sdf.format(date.getTime() + amount * 60 * 1000);
            Date time = sdf.parse(strTime);
            return time;
        }
        catch (Exception e) {
            log.error("计算时间异常subtractTime：{}", e);
        }
        return null;
    }
}
