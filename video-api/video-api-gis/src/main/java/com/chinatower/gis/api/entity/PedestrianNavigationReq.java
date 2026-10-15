package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @desription: 步行导航
 * @data 创建时间：2023/8/9 10:39
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PedestrianNavigationReq extends GisBaseReq{
    /**
     * 起点经纬度 经度在前，纬度在后，经度和纬度用","分割，经纬度小数点后不得超过6位。
     * @required true
     */
    @NotBlank(message = "起点不能为空")
    private String origin;
    /**
     * 目的地 经度在前，纬度在后，经度和纬度用","分割，经纬度小数点后不得超过6位。
     * @required true
     */
    @NotBlank(message = "目的地不能为空")
    private String destination;

    /**
     * 1：多备选路线中第一条路线
     *
     * 2：多备选路线中前两条路线
     *
     * 3：多备选路线中三条路线
     *
     * 不传则默认返回一条路线方案
     * @required false
     */
    @ApiModelProperty(value = "返回路线条数")
    private String alternativeRoute;

    /**
     * show_fields用来筛选response结果中可选字段。show_fields的使用需要遵循如下规则：
     *
     * 1、具体可指定返回的字段类请见下方返回结果说明中的“show_fields”内字段类型；
     *
     * 2、多个字段间采用“,”进行分割；
     *
     * 3、show_fields未设置时，只返回基础信息类内字段。
     * @required false
     */
    @ApiModelProperty(value = "返回结果控制")
    private String showFields;
}
