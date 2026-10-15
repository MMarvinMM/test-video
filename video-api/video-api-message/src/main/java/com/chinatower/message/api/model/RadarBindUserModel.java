package com.chinatower.message.api.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
public class RadarBindUserModel implements Serializable {
    @NotBlank(message = "用户信息不可以为空")
    private String userKey;
    @NotBlank(message = "雷达信息不可以为空")
    private RadarObject radarObject;

    private List<RadarObject> radarObjectList;

    public RadarBindUserModel(String userKey, List<RadarObject> radarObjectList) {
        this.userKey = userKey;
        this.radarObjectList = radarObjectList;
    }

    public RadarBindUserModel() {

    }
}
