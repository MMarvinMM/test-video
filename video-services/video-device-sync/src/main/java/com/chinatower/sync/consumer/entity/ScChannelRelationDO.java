package com.chinatower.sync.consumer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ScChannelRelationDO {

    @JsonProperty("data")
    private List<DataDTO> data;
    @JsonProperty("database")
    private String database;
    @JsonProperty("es")
    private Long es;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("isDdl")
    private Boolean isDdl;
    @JsonProperty("mysqlType")
    private MysqlTypeDTO mysqlType;
    @JsonProperty("pkNames")
    private List<String> pkNames;
    @JsonProperty("sql")
    private String sql;
    @JsonProperty("sqlType")
    private SqlTypeDTO sqlType;
    @JsonProperty("table")
    private String table;
    @JsonProperty("ts")
    private Long ts;
    @JsonProperty("type")
    private String type;

    @NoArgsConstructor
    @Data
    public static class MysqlTypeDTO {
        @JsonProperty("id")
        private String id;
        @JsonProperty("algorithm_code")
        private String algorithmCode;
        @JsonProperty("algorithm_name")
        private String algorithmName;
        @JsonProperty("channel_code")
        private String channelCode;
        @JsonProperty("channel_name")
        private String channelName;
        @JsonProperty("device_code")
        private String deviceCode;
        @JsonProperty("device_name")
        private String deviceName;
        @JsonProperty("moni_point_list")
        private String moniPointList;
        @JsonProperty("monitor_area_id")
        private String monitorAreaId;
        @JsonProperty("opt_type")
        private String optType;
        @JsonProperty("preset_id")
        private String presetId;
        @JsonProperty("preset_name")
        private String presetName;
        @JsonProperty("push_status")
        private String pushStatus;
        @JsonProperty("update_time")
        private String updateTime;
        @JsonProperty("advanced_parameters")
        private String advancedParameters;
        @JsonProperty("push_time")
        private String pushTime;
        @JsonProperty("subside_code")
        private String subsideCode;
        @JsonProperty("subside_name")
        private String subsideName;
        @JsonProperty("task_id")
        private String taskId;
        @JsonProperty("preset")
        private String preset;
        @JsonProperty("monitor_area")
        private String monitorArea;
        @JsonProperty("error_message")
        private String errorMessage;
        @JsonProperty("service_batch_id")
        private String serviceBatchId;
        @JsonProperty("service_name")
        private String serviceName;
        @JsonProperty("extraction_type")
        private String extractionType;
        @JsonProperty("status")
        private String status;
        @JsonProperty("thresh")
        private String thresh;
        @JsonProperty("alarm_interval")
        private String alarmInterval;
        @JsonProperty("model_config")
        private String modelConfig;
        @JsonProperty("basic_status")
        private String basicStatus;
        @JsonProperty("basic_time")
        private String basicTime;
        @JsonProperty("basic_error_msg")
        private String basicErrorMsg;
        @JsonProperty("device_status")
        private String deviceStatus;
        @JsonProperty("task_status")
        private String taskStatus;
        @JsonProperty("is_cruise")
        private String isCruise;
        @JsonProperty("sync_status")
        private String syncStatus;
        @JsonProperty("sync_time")
        private String syncTime;
        @JsonProperty("analy_type")
        private String analyType;
        @JsonProperty("basc_time")
        private String bascTime;
        @JsonProperty("tenant_code")
        private String tenantCode;
        @JsonProperty("tenant_name")
        private String tenantName;
    }

    @NoArgsConstructor
    @Data
    public static class SqlTypeDTO {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("algorithm_code")
        private Integer algorithmCode;
        @JsonProperty("algorithm_name")
        private Integer algorithmName;
        @JsonProperty("channel_code")
        private Integer channelCode;
        @JsonProperty("channel_name")
        private Integer channelName;
        @JsonProperty("device_code")
        private Integer deviceCode;
        @JsonProperty("device_name")
        private Integer deviceName;
        @JsonProperty("moni_point_list")
        private Integer moniPointList;
        @JsonProperty("monitor_area_id")
        private Integer monitorAreaId;
        @JsonProperty("opt_type")
        private Integer optType;
        @JsonProperty("preset_id")
        private Integer presetId;
        @JsonProperty("preset_name")
        private Integer presetName;
        @JsonProperty("push_status")
        private Integer pushStatus;
        @JsonProperty("update_time")
        private Integer updateTime;
        @JsonProperty("advanced_parameters")
        private Integer advancedParameters;
        @JsonProperty("push_time")
        private Integer pushTime;
        @JsonProperty("subside_code")
        private Integer subsideCode;
        @JsonProperty("subside_name")
        private Integer subsideName;
        @JsonProperty("task_id")
        private Integer taskId;
        @JsonProperty("preset")
        private Integer preset;
        @JsonProperty("monitor_area")
        private Integer monitorArea;
        @JsonProperty("error_message")
        private Integer errorMessage;
        @JsonProperty("service_batch_id")
        private Integer serviceBatchId;
        @JsonProperty("service_name")
        private Integer serviceName;
        @JsonProperty("extraction_type")
        private Integer extractionType;
        @JsonProperty("status")
        private Integer status;
        @JsonProperty("thresh")
        private Integer thresh;
        @JsonProperty("alarm_interval")
        private Integer alarmInterval;
        @JsonProperty("model_config")
        private Integer modelConfig;
        @JsonProperty("basic_status")
        private Integer basicStatus;
        @JsonProperty("basic_time")
        private Integer basicTime;
        @JsonProperty("basic_error_msg")
        private Integer basicErrorMsg;
        @JsonProperty("device_status")
        private Integer deviceStatus;
        @JsonProperty("task_status")
        private Integer taskStatus;
        @JsonProperty("is_cruise")
        private Integer isCruise;
        @JsonProperty("sync_status")
        private Integer syncStatus;
        @JsonProperty("sync_time")
        private Integer syncTime;
        @JsonProperty("analy_type")
        private Integer analyType;
        @JsonProperty("basc_time")
        private Integer bascTime;
        @JsonProperty("tenant_code")
        private Integer tenantCode;
        @JsonProperty("tenant_name")
        private Integer tenantName;
    }

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("id")
        private String id;
        @JsonProperty("algorithm_code")
        private String algorithmCode;
        @JsonProperty("algorithm_name")
        private String algorithmName;
        @JsonProperty("channel_code")
        private String channelCode;
        @JsonProperty("channel_name")
        private String channelName;
        @JsonProperty("device_code")
        private String deviceCode;
        @JsonProperty("device_name")
        private String deviceName;
        @JsonProperty("moni_point_list")
        private String moniPointList;
        @JsonProperty("monitor_area_id")
        private String monitorAreaId;
        @JsonProperty("opt_type")
        private String optType;
        @JsonProperty("preset_id")
        private String presetId;
        @JsonProperty("preset_name")
        private Object presetName;
        @JsonProperty("push_status")
        private String pushStatus;
        @JsonProperty("update_time")
        private String updateTime;
        @JsonProperty("advanced_parameters")
        private Object advancedParameters;
        @JsonProperty("push_time")
        private String pushTime;
        @JsonProperty("subside_code")
        private String subsideCode;
        @JsonProperty("subside_name")
        private String subsideName;
        @JsonProperty("task_id")
        private String taskId;
        @JsonProperty("preset")
        private String preset;
        @JsonProperty("monitor_area")
        private Object monitorArea;
        @JsonProperty("error_message")
        private String errorMessage;
        @JsonProperty("service_batch_id")
        private Object serviceBatchId;
        @JsonProperty("service_name")
        private Object serviceName;
        @JsonProperty("extraction_type")
        private String extractionType;
        @JsonProperty("status")
        private String status;
        @JsonProperty("thresh")
        private Object thresh;
        @JsonProperty("alarm_interval")
        private String alarmInterval;
        @JsonProperty("model_config")
        private Object modelConfig;
        @JsonProperty("basic_status")
        private String basicStatus;
        @JsonProperty("basic_time")
        private String basicTime;
        @JsonProperty("basic_error_msg")
        private String basicErrorMsg;
        @JsonProperty("device_status")
        private Object deviceStatus;
        @JsonProperty("task_status")
        private Object taskStatus;
        @JsonProperty("is_cruise")
        private Object isCruise;
        @JsonProperty("sync_status")
        private String syncStatus;
        @JsonProperty("sync_time")
        private String syncTime;
        @JsonProperty("analy_type")
        private String analyType;
        @JsonProperty("basc_time")
        private Object bascTime;
        @JsonProperty("tenant_code")
        private String tenantCode;
        @JsonProperty("tenant_name")
        private String tenantName;
    }

}
