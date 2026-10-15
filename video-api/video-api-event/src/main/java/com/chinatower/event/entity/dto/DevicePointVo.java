package com.chinatower.event.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DevicePointVo {


    /**
     * 百分比点位横坐标
     * @required true
     */
    @NotBlank(message = "百分比点位横坐标不可以为空")
    private String percentX;

    /**
     * 百分比点位纵坐标
     * @required true
     */
    @NotBlank(message = "百分比点位纵坐标不可以为空")
    private String percentY;


    /**
     * 经度
     * @required true
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;

    /**
     * 标记
     */
    private String flag;

}
