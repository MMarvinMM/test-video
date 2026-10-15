package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThreadLineVO {

    //警戒点序号
    private String pointId;

    //X坐标百分比
    private Float percentX;

    //Y坐标百分比
    private Float percentY;

    private String lon;

    private String lat;
}
