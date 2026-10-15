package com.chinatower.bdm.api.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 签到签退入参
 */
@Data
public class AttendanceSignParam {

    @NotBlank(message = "经度不能为空")
    private String longitude;
    @NotBlank(message = "维度不能为空")
    private String latitude;
    @NotBlank(message = "地址不能为空")
    private String address;
    @NotBlank(message = "网格编码不能为空")
    private String gridId;
}
