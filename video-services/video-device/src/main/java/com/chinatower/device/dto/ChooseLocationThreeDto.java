package com.chinatower.device.dto;

import com.chinatower.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChooseLocationThreeDto implements Serializable {

    @Excel(name = "经度（必填，保留6位小数）")
    private Double lon;

    @Excel(name = "纬度（必填，保留6位小数）")
    private Double lat;

    @Excel(name = "可视半径（必填，正整数）")
    private Integer radius;

    @Excel(name = "设备挂高（必填，保留2位小数）")
    private Double height;
}
