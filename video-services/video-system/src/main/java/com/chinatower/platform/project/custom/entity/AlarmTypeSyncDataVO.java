package com.chinatower.platform.project.custom.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author liuxt
 * @date 2024/01/06 15:30
 * @description 告警类型同步实体
 */
@Data
@ToString
public class AlarmTypeSyncDataVO {
        @JsonProperty("alarm_type_id")
        private String alarmTypeId;
        @JsonProperty("alarm_type_class_id")
        private String alarmTypeClassId;
        @JsonProperty("type_name")
        private String typeName;
        @JsonProperty("type_value")
        private String typeValue;
        @JsonProperty("alarm_level")
        private String alarmLevel;
        @JsonProperty("type_code")
        private String typeCode;
        @JsonProperty("alarm_source")
        private String alarmSource;
        @JsonProperty("tenantor")
        private String tenantor;
        @JsonProperty("source")
        private String source;
        @JsonProperty("if_free")
        private String ifFree;
        @JsonProperty("creator")
        private Object creator;
        @JsonProperty("create_time")
        private String createTime;
        @JsonProperty("modifier")
        private String modifier;
        @JsonProperty("update_time")
        private String updateTime;
        @JsonProperty("delete_flag")
        private String deleteFlag;
        @JsonProperty("icon")
        private String icon;
        @JsonProperty("app_ver_code")
        private String appVerCode;
        @JsonProperty("tenant_id")
        private String tenantId;
        @JsonProperty("industry_code")
        private String industryCode;

}
