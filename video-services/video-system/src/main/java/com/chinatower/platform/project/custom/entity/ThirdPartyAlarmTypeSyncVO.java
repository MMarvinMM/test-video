package com.chinatower.platform.project.custom.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author liuxt
 * @date 2024/01/06 15:30
 * @description 第三方告警类型同步实体
 */
@Data
@ToString
public class ThirdPartyAlarmTypeSyncVO {

    @JsonProperty("data")
    private List<ThirdPartyAlarmTypeSyncDataVO> data;
    @JsonProperty("database")
    private String database;
    @JsonProperty("es")
    private Long es;
    @JsonProperty("id")
    private Long id;
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






}
