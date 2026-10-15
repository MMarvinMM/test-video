package com.chinatower.sync.consumer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class YgDeviceChannelDO {
    @JsonProperty("data")
    private List<DataDTO> data;
    @JsonProperty("database")
    private String database;
    @JsonProperty("es")
    private Long es;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("isDdl")
    private Boolean isDdl;
    @JsonProperty("pkNames")
    private List<String> pkNames;
    @JsonProperty("sql")
    private String sql;
    @JsonProperty("table")
    private String table;
    @JsonProperty("ts")
    private Long ts;
    @JsonProperty("type")
    private String type;

    @NoArgsConstructor
    @Data
    public class DataDTO {
        @JsonProperty("device_code")
        private String deviceCode;
        @JsonProperty("channel_code")
        private String channelCode;
    }
}
