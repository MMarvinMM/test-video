package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@ApiModel(value = "地图接口-对象", description = "地图接口-对象")
public class GisKeyWordQueryReq extends GisBaseReq{
    /**
    * 地点关键字
    * @required false
    */
    @ApiModelProperty(value = "关键字")
    private String keyWord;
    /**
     * 增加指定区域内数据召回权重
     * 百度必传/高德天地图不必传
     * @required false
     * */
    @ApiModelProperty(value = "指定区域(百度必传-示例：北京、131（北京的code）、海淀区、全国，等)")
    private String region;
    /**
     * 仅召回region对应区域内数据
     * 高德百度不必传/天地图无此字段
     * @required false
     * */
    private String cityLimit;
    /**
     * 纬度
     * @required false
     * */
    @ApiModelProperty(value = "纬度")
    private String lat;
    /**
     * 经度
     * @required false
     * */
    @ApiModelProperty(value = "经度")
    private String lon;
    /**
     * 规则遵循：国家、省份、城市、区县、城镇、乡村、街道、门牌号码、屋邨、大厦
     * 如：北京市朝阳区阜通东大街6号
     * @required false
     * */
    @ApiModelProperty(value = "地址")
    private String address;
    /**
     * 地区编码
     * @required false
     * */
    @ApiModelProperty(value = "地区编码")
    private String adCode;
    /**
     * 需转换的源坐标
     * @required false
     * */
    private String locations;
    /**
     * 行政区级数
     * @required false
     * */
    private String subdistrict;

    /**
     * 查询的地图范围
     * @required false
     */
    private String mapBound;

    /**
     * 行政区名称
     * @required false
     */
    private String areaName;

}
