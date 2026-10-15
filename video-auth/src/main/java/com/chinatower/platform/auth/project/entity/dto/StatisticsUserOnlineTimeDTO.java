package com.chinatower.platform.auth.project.entity.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chinatower.common.core.enums.ClientType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;

import java.util.Map;

/**
 * 用户登录在线时长统计沉淀表
 *
 * @author cuisj
 */
@Data
@Slf4j
public class StatisticsUserOnlineTimeDTO {
    private Long id;
    //用户ID
    private String userId;
    //昵称
    private String nickName;
    //部门ID
    private String deptId;
    //租户ID
    private String tenantId;
    //省编码
    private String provinceId;
    //市编码
    private String cityId;
    /**
     * yyyyy-MM-dd
     */
    private String loginTime;
    /**
     * 单位 秒
     */
    private String onlineTime;
    private String lastRecordJson;
    private String webOnlineTime;
    private String appOnlineTime;
    private String wxOnlineTime;

    @JsonIgnore
    @Transient
    private TypeReference<Map<ClientType, Long>> typeReference = new TypeReference<Map<ClientType, Long>>() {
    };
    private Map<ClientType, Long> clientDuration;

    /**
     * 是否新增
     */
    public boolean addOrNot() {
        return this.id == null;
    }

    /**
     * 从历史记录更新
     */
    public void updateFromHistory(StatisticsUserOnlineTimeDTO history) {
        this.id = history.getId();
        this.onlineTime = history.getOnlineTime();
        this.lastRecordJson = history.getLastRecordJson();
        this.webOnlineTime = history.getWebOnlineTime();
        this.appOnlineTime = history.getAppOnlineTime();
        this.wxOnlineTime = history.getWxOnlineTime();
        try {
            this.clientDuration = JSON.parseObject(this.lastRecordJson, typeReference);
        } catch (Exception e) {
            log.error("统计用户登录时长任务->json转换异常,json str:{}", this.lastRecordJson, e);
        }
    }

    /**
     * 增加在线时长
     */
    public void addDuration(Map<ClientType, Long> clientDuration) {
        long increment = 0;
        for (Map.Entry<ClientType, Long> entry : clientDuration.entrySet()) {
            ClientType clientType = entry.getKey();
            Long duration = entry.getValue();
            long time = this.calculateIncrement(clientType, duration);
            increment += time;
            addTime(time,clientType.getClientType());
        }
        if (this.onlineTime == null) {
            this.onlineTime = increment + "";
        } else {
            this.onlineTime = (Long.parseLong(this.onlineTime) + increment) + "";
        }
        this.lastRecordJson = JSON.toJSONString(clientDuration);
    }

    /**
     * 增加在线时长
     */
    public void addTime(long time,String clientType) {
        if (clientType.equals("web_manage")) {
            if (this.webOnlineTime == null) {
                this.webOnlineTime = time +"";
            } else {
                this.webOnlineTime = (Long.parseLong(this.webOnlineTime) + time) + "";
            }
        }else if (clientType.equals("app_manage")){
            if (this.appOnlineTime == null) {
                this.appOnlineTime = time +"";
            } else {
                this.appOnlineTime = (Long.parseLong(this.appOnlineTime) + time) + "";
            }
        }else if (clientType.equals("wx_manage")){
            if (this.wxOnlineTime == null) {
                this.wxOnlineTime = time +"";
            } else {
                this.wxOnlineTime = (Long.parseLong(this.wxOnlineTime) + time) + "";
            }
        }
    }

    /**
     * 计算在线时长增量
     */
    private long calculateIncrement(ClientType clientType, Long duration) {
        if (this.clientDuration == null) {
            return duration;
        }
        Long lastDuration = this.clientDuration.get(clientType);
        //上次未统计，为本次
        if (lastDuration == null) {
            return duration;
        }
        //上次统计小于本次，为本次-上次
        if (lastDuration < duration) {
            return duration - lastDuration;
        }
        //上次大于本次，则累加本次
        if (lastDuration > duration) {
            return duration;
        }
        return 0;
    }
}
