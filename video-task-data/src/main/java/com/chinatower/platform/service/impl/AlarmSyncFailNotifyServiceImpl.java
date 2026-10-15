package com.chinatower.platform.service.impl;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.dao.LocalizationAlarmDetailedInfoDAO;
import com.chinatower.platform.entity.request.CommonMailRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.modules.AlarmSyncFailItem;
import com.chinatower.platform.modules.MailReceiverInfoDto;
import com.chinatower.platform.modules.PlatformDataConfigDto;
import com.chinatower.platform.service.AlarmSyncFailNotifyService;
import com.chinatower.system.api.resp.SysDictDataResponse;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 属地告警同步失败邮件提醒
 */
@Slf4j
@Service
public class AlarmSyncFailNotifyServiceImpl implements AlarmSyncFailNotifyService {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Autowired
    private MsgFeignClient msgFeignClient;

    @Autowired
    private LocalizationAlarmDetailedInfoDAO localizationAlarmDetailedInfoDAO;

    @Async
    @Override
    public void sendFailNotifyBatch(String pullDate, List<AlarmSyncFailItem> failList) {
        if (CollectionUtils.isEmpty(failList)) {
            return;
        }
        try {
            MailReceiverInfoDto receiverInfo = loadMailReceiverInfo();
            if (StringUtils.isEmpty(receiverInfo.getReceiveEmails())) {
                log.warn("属地告警同步失败但未配置邮件接收人, dictType:{}", Constants.ALARM_SYNC_FAIL_RECEIVE_EMAIL);
                return;
            }

            String taskCreateDate = DateUtil.format(new Date(), DATE_PATTERN);
            String pullDateDisplay = formatPullDate(pullDate);

            CommonMailRequest mailRequest = new CommonMailRequest();
            mailRequest.setReceiveMail(receiverInfo.getReceiveEmails());
            if (!StringUtils.isEmpty(receiverInfo.getCcEmails())) {
                mailRequest.setCcMail(receiverInfo.getCcEmails());
            }
            mailRequest.setMailTitle(String.format("属地告警同步中心异常提醒(%s)", taskCreateDate));
            mailRequest.setMailTxt(buildMailContent(pullDateDisplay, taskCreateDate, failList));
            log.info("属地告警同步失败聚合邮件发送, title:{}, receiveMail:{}, ccMail:{}",
                    mailRequest.getMailTitle(), receiverInfo.getReceiveEmails(), receiverInfo.getCcEmails());
            Result result = msgFeignClient.sendMail(mailRequest);
            log.info("属地告警同步失败聚合邮件发送结果:{}", result);
        } catch (Exception e) {
            log.error("属地告警同步失败邮件提醒处理异常, pullDate:{}, failCount:{}", pullDate, failList.size(), e);
        }
    }

    /**
     * 组装邮件正文
     */
    private String buildMailContent(String pullDateDisplay, String taskCreateDate, List<AlarmSyncFailItem> failList) {
        StringBuilder content = new StringBuilder();
        content.append("属地").append(pullDateDisplay).append("告警信息同步中心异常，敬请关注。<br/>");
        content.append(buildPlatformFailLines(failList));
        content.append("属地告警拉取任务创建日期：").append(taskCreateDate).append("。");
        return content.toString();
    }

    /**
     * 按属地聚合失败同步类型，生成邮件正文行
     * 示例：属地名称（属地编码）："同步类型1、同步类型2"数据同步异常;
     */
    private String buildPlatformFailLines(List<AlarmSyncFailItem> failList) {
        Map<String, String> platformNameMap = loadPlatformNameMap(failList);
        Map<String, Set<Integer>> platformFailTypeMap = groupFailTypeByPlatform(failList);

        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, Set<Integer>> entry : platformFailTypeMap.entrySet()) {
            String platformCode = entry.getKey();
            String platformName = platformNameMap.getOrDefault(platformCode, platformCode);
            String syncTypeNames = entry.getValue().stream()
                    .map(this::resolveSyncTypeName)
                    .collect(Collectors.joining("、"));
            content.append(platformName)
                    .append("（").append(platformCode).append("）：")
                    .append("\"").append(syncTypeNames).append("\"数据同步异常;<br/>");
        }
        return content.toString();
    }

    /**
     * 按属地编码分组失败同步类型，保持出现顺序
     */
    private Map<String, Set<Integer>> groupFailTypeByPlatform(List<AlarmSyncFailItem> failList) {
        Map<String, Set<Integer>> platformFailTypeMap = new LinkedHashMap<>();
        for (AlarmSyncFailItem item : failList) {
            if (StringUtils.isEmpty(item.getPlatformCode()) || item.getSyncType() == null) {
                continue;
            }
            platformFailTypeMap.computeIfAbsent(item.getPlatformCode(), key -> new LinkedHashSet<>())
                    .add(item.getSyncType());
        }
        return platformFailTypeMap;
    }

    /**
     * 从 vm_platform_data_config 查询属地名称
     */
    private Map<String, String> loadPlatformNameMap(List<AlarmSyncFailItem> failList) {
        List<String> platformCodeList = failList.stream()
                .map(AlarmSyncFailItem::getPlatformCode)
                .filter(code -> !StringUtils.isEmpty(code))
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(platformCodeList)) {
            return new HashMap<>();
        }
        List<PlatformDataConfigDto> platformInfoList = localizationAlarmDetailedInfoDAO.getPlatformInfoByCodes(platformCodeList);
        if (CollectionUtils.isEmpty(platformInfoList)) {
            return new HashMap<>();
        }
        Map<String, String> platformNameMap = new HashMap<>(platformInfoList.size());
        for (PlatformDataConfigDto platformInfo : platformInfoList) {
            if (!StringUtils.isEmpty(platformInfo.getPlatformCode())) {
                platformNameMap.put(platformInfo.getPlatformCode(), platformInfo.getPlatformName());
            }
        }
        return platformNameMap;
    }

    /**
     * 拉取日期 yyyyMMdd 转 yyyy-MM-dd
     */
    private String formatPullDate(String pullDate) {
        if (StringUtils.isEmpty(pullDate) || pullDate.length() != 8) {
            return pullDate;
        }
        return pullDate.substring(0, 4) + "-" + pullDate.substring(4, 6) + "-" + pullDate.substring(6, 8);
    }

    /**
     * 一次查询字典，按备注拆分收件人与抄送人
     */
    private MailReceiverInfoDto loadMailReceiverInfo() {
        List<SysDictDataResponse> dictDataList = localizationAlarmDetailedInfoDAO
                .getDictDataByType(Constants.ALARM_SYNC_FAIL_RECEIVE_EMAIL);
        Set<String> receiveEmails = new LinkedHashSet<>();
        Set<String> ccEmails = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(dictDataList)) {
            for (SysDictDataResponse dictData : dictDataList) {
                String email = dictData.getDictValue() == null ? null : dictData.getDictValue().trim();
                if (StringUtils.isEmpty(email)) {
                    continue;
                }
                String remark = dictData.getRemark();
                if (matchReceiverType(remark, Constants.ALARM_SYNC_MAIL_CC)) {
                    ccEmails.add(email);
                } else if (matchReceiverType(remark, Constants.ALARM_SYNC_MAIL_RECEIVER)) {
                    receiveEmails.add(email);
                }
            }
        }
        return new MailReceiverInfoDto(joinEmails(receiveEmails), joinEmails(ccEmails));
    }

    /**
     * 邮箱去重后以英文逗号拼接
     */
    private String joinEmails(Set<String> emails) {
        return emails.isEmpty() ? null : String.join(",", emails);
    }

    /**
     * 备注信息包含“收件人”或“抄送人”即视为对应类型
     */
    private boolean matchReceiverType(String remark, String receiverType) {
        return !StringUtils.isEmpty(remark) && remark.contains(receiverType);
    }

    private String resolveSyncTypeName(Integer syncType) {
        if (syncType == null) {
            return "未知类型";
        }
        switch (syncType) {
            case Constants.SYNC_TYPE_WARNING_ORDER:
                return "告警工单";
            case Constants.SYNC_TYPE_OPERATE:
                return "运营研判";
            case Constants.SYNC_TYPE_INTERCEPT:
                return "回收站";
            default:
                return "未知类型";
        }
    }
}
