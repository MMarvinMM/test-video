package com.chinatower.platform.project.custom.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author liuxt
 * @date 2024/01/06 15:30
 * @description 第三方告警类型同步实体
 */
@Data
@ToString
public class ThirdPartyAlarmTypeSyncDataVO {
        @JsonProperty("id")
        private String id;
        @JsonProperty("tenant_id")
        private String tenantId;
        @JsonProperty("warning_source_name")
        private String warningSourceName;
        @JsonProperty("alarm_type")
        private String alarmType;
        @JsonProperty("modifier")
        private String modifier;
        @JsonProperty("create_time")
        private String createTime;
        @JsonProperty("update_time")
        private String updateTime;
        @JsonProperty("delete_flag")
        private String deleteFlag;
        @JsonProperty("industry_code")
        private String industryCode;
        @JsonProperty("app_ver_code")
        private String appVerCode;
        @JsonProperty("creator")
        private Object creator;

}
