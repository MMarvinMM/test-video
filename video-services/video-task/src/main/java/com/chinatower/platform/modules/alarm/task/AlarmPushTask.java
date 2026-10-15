package com.chinatower.platform.modules.alarm.task;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.vo.ExtraInfoVO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.dao.AlarmPushDAO;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AlarmPushTask {

    @Resource
    private IReceiveAlarmNewService receiveAlarmNewService;

    @Resource
    private AlarmPushDAO alarmPushDAO;

    /**
     * 自动告警推送
     *
     * @param param 事件ID，多个以逗号分隔；为空走 Redis 模版
     */
    @XxlJob("alarmPushHandler")
    public ReturnT<String> alarmPushHandler(String param) {
        List<ReceiveAlarmVOReq> receiveAlarmVOReqs;
        if (StringUtils.isEmpty(param)) {
            // 默认事件ID列表
            param = "20260902770014994270267768,20260713770014994268865218,20260716123016303268944542,20260625123016303268487090,20260625123016303268486975,20260313770015304261812682,20260313770015304261812746,20260313770015304261814166,20260618770014994268303004,20260630770014994268592236,20260611700015367268157009,20260611700015367268156877,20260611700015367268157055,20260611700015367268156975,20260616770014994268257609,20260514770014994267304627,20260820770014994269860103,20260820770014994269870578,20260820770014994269870870,20260820770014994269865673,20260814770014994269688021,20260820770014994269867660,20260820770014994269867992,20260820770014994269868518,20260820770014994269868939,20260820700015421269869170";
        }
        receiveAlarmVOReqs = buildAlarmReqByEventIds(param);
        if (CollectionUtils.isEmpty(receiveAlarmVOReqs)) {
            log.warn("====== alarmPushHandler 按事件ID未查到可推送数据，param：{} ======", param);
            return ReturnT.SUCCESS;
        }
        receiveAlarmVOReqs.forEach(receiveAlarmVOReq -> {
            if (receiveAlarmVOReq == null || receiveAlarmVOReq.getData() == null) {
                log.warn("====== alarmPushHandler 推送报文data为空，跳过：{} ======", JSON.toJSONString(receiveAlarmVOReq));
                return;
            }
            receiveAlarmVOReq.getData().setAlarmTime(DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS));
            receiveAlarmNewService.receiveAlarm(receiveAlarmVOReq);
        });
        return ReturnT.SUCCESS;
    }

    /**
     * 按事件ID查询详情并组装告警推送报文
     */
    private List<ReceiveAlarmVOReq> buildAlarmReqByEventIds(String param) {
        List<String> eventIds = Arrays.stream(param.split(","))
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(eventIds)) {
            return new ArrayList<>();
        }
        // 按事件ID前6位（yyyyMM）分表查询
        Map<String, List<String>> tableOrderMap = eventIds.stream()
                .filter(id -> id.length() >= 6)
                .collect(Collectors.groupingBy(id -> Constants.WARN_ORDER_TEBLE + id.substring(0, 6)));
        List<OrderInfoVO> orderInfoList = new ArrayList<>();
        tableOrderMap.forEach((tableName, orderIds) -> {
            try {
                List<OrderInfoVO> list = alarmPushDAO.getOrderInfoByIds(orderIds, tableName);
                if (!CollectionUtils.isEmpty(list)) {
                    orderInfoList.addAll(list);
                }
            } catch (Exception e) {
                log.error("====== alarmPushHandler 查询事件详情失败，tableName：{}，orderIds：{} ======",
                        tableName, orderIds, e);
            }
        });
        if (CollectionUtils.isEmpty(orderInfoList)) {
            return new ArrayList<>();
        }
        log.info("====== alarmPushHandler 按事件ID查询到详情条数：{}，入参事件数：{} ======",
                orderInfoList.size(), eventIds.size());
        return orderInfoList.stream()
                .map(this::buildReceiveAlarmVOReq)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 以事件详情为基础模版组装告警推送报文
     */
    private ReceiveAlarmVOReq buildReceiveAlarmVOReq(OrderInfoVO orderInfo) {
        if (orderInfo == null || StringUtils.isBlank(orderInfo.getWarningTypeId())) {
            log.warn("====== alarmPushHandler 事件详情缺少告警类型，跳过：{} ======", JSON.toJSONString(orderInfo));
            return null;
        }
        ReceiveAlarmVO data = new ReceiveAlarmVO();
        BeanUtils.copyProperties(orderInfo, data);
        // 接收侧关键字段与事件详情字段映射
        data.setAlarmType(orderInfo.getWarningTypeId());
        data.setAlarmSource(orderInfo.getWarningSource());
        data.setWarningSource(orderInfo.getWarningSource());
        data.setOriginalPictureUrl(orderInfo.getFileImgUrlSrc());
        data.setAlarmPictureUrl(orderInfo.getFileImgUrlIcon());
        data.setAlarmVideoUrl(orderInfo.getVideoUrl());
        data.setIndustryCode(orderInfo.getTradeId());
        data.setProvinceCode(orderInfo.getProvinceId());
        data.setCityCode(orderInfo.getCityId());
        data.setCountyCode(orderInfo.getCountyId());
        // 预置位：data 与算法透传 extraInfo 均需带上，接收/研判链路从 extraInfo 读取
        data.setPresetId(orderInfo.getPresetId());
        fillPresetExtraInfo(data, orderInfo.getPresetId());
        data.setAlarmTime(DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS));
        data.setWarningOrderId(null);
        if (StringUtils.isNotBlank(orderInfo.getExtraField())) {
            data.setExtraField(orderInfo.getExtraField());
        }
        ReceiveAlarmVOReq req = new ReceiveAlarmVOReq();
        req.setData(data);
        return req;
    }

    /**
     * 将预置位写入算法透传字段 extraInfo
     */
    private void fillPresetExtraInfo(ReceiveAlarmVO data, String presetId) {
        if (data == null || StringUtils.isBlank(presetId)) {
            return;
        }
        ExtraInfoVO extraInfo = data.getExtraInfo();
        if (extraInfo == null) {
            extraInfo = new ExtraInfoVO();
        }
        extraInfo.setPresetId(presetId);
        data.setExtraInfo(extraInfo);
    }
}
