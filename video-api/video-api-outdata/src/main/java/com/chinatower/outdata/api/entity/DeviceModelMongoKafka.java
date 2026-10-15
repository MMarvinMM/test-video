package com.chinatower.outdata.api.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceModelMongoKafka {
    private List<DeviceModelMongoDO> data;
    @JsonProperty("type")
    private String type;
}
