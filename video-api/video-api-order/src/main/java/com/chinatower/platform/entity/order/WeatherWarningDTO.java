package com.chinatower.platform.entity.order;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@ApiModel(value = "天气灾害预警对象",description = "天气灾害预警对象")
@Document(collection="weather_warning_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherWarningDTO extends BaseMongoDTO {

    @ApiModelProperty(value = "地市区域编码")
    private String adcode;

    @ApiModelProperty(value = "地市名称")
    private String locationNameZh;

    @ApiModelProperty(value = "天气灾害预警id")
    private String id;

    @ApiModelProperty(value = "天气灾害预警发布时间")
    private String pubTime;

    @ApiModelProperty(value = "天气灾害预警发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date pubTimeDate;

    @ApiModelProperty(value = "天气灾害预警标题")
    private String title;

    @ApiModelProperty(value = "天气灾害预警等级")
    private String severity;

    @ApiModelProperty(value = "天气灾害预警等级名称")
    private String severityName;

    @ApiModelProperty(value = "天气灾害预警等级颜色")
    private String severityColor;

    @ApiModelProperty(value = "天气灾害预警等级颜色名称")
    private String severityColorName;

    @ApiModelProperty(value = "天气灾害预警类型")
    private String type;

    @ApiModelProperty(value = "天气灾害预警类型名称")
    private String typeName;

    @ApiModelProperty(value = "天气灾害预警详情")
    private String text;

    /**
     * 省份编码
     */
    @ApiModelProperty(value = "省份编码")
    private String provinceId;

    /**
     * 地市编码
     */
    @ApiModelProperty(value = "地市编码")
    private String cityId;

    /**
     * 区县编码
     */
    @ApiModelProperty(value = "区县编码")
    private String countyId;

    /**
     * 省份名称
     */
    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    /**
     * 地市名称
     */
    @ApiModelProperty(value = "地市名称")
    private String cityName;

    /**
     * 区县名称
     */
    @ApiModelProperty(value = "区县名称")
    private String countyName;

    public WeatherWarningDTO() {
        this.collectionName = "weather_warning_info";
    }
}
