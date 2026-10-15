package com.chinatower.platform.entity.order;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


/**
 * @ClassName weatherDailyDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/31 11:03
 * @Version 1.0
 **/
@Data
@ApiModel(value = "未来天气对象",description = "未来天气对象")
@Document(collection="weather_future_day_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherFutureDailyDTO extends BaseMongoDTO {
    
    @ApiModelProperty(value = "地市区域编码")
    private String adcode;
    
    @ApiModelProperty(value = "地市名称")
    private String locationNameZh;
    
    @ApiModelProperty(value = "预报日期")
    private String fxDate;
    
    @ApiModelProperty(value = "预报日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date fxTimeDate;
    
    @ApiModelProperty(value = "日出时间")
    private String sunrise;
    
    @ApiModelProperty(value = "日落时间")
    private String sunset;
    
    @ApiModelProperty(value = "月升时间")
    private String moonrise;
    
    @ApiModelProperty(value = "月落时间")
    private String moonset;
    
    @ApiModelProperty(value = "月相名称")
    private String moonPhase;
    
    @ApiModelProperty(value = "月相图标代码，图标可通过天气状况和图标下载")
    private String moonPhaseIcon;
    
    @ApiModelProperty(value = "预报当天最高温度 ")
    private Double tempMax;
    
    @ApiModelProperty(value = "预报当天最低温度")
    private Double tempMin;
    
    @ApiModelProperty(value = "预报白天天气状况的图标代码，图标可通过天气状况和图标下载")
    private String iconDay;
    
    @ApiModelProperty(value = "预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述")
    private String textDay;
    
    @ApiModelProperty(value = "预报夜间天气状况的图标代码，图标可通过天气状况和图标下载")
    private String iconNight;
   
    @ApiModelProperty(value = "预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述")
    private String textNight;
    
    @ApiModelProperty(value = "预报白天风向360角度")
    private Double wind360Day;
    
    @ApiModelProperty(value = "预报白天风向")
    private String windDirDay;
    
    @ApiModelProperty(value = "预报白天风力等级")
    private String windScaleDay;
    
    @ApiModelProperty(value = "预报白天风速，公里/小时")
    private Double windSpeedDay;
    
    @ApiModelProperty(value = "预报夜间风向360角度")
    private Double wind360Night;
    
    @ApiModelProperty(value = "预报夜间当天风向")
    private String windDirNight;
    
    @ApiModelProperty(value = "预报夜间风力等级")
    private String windScaleNight;
    
    @ApiModelProperty(value = "预报夜间风速，公里/小时")
    private Double windSpeedNight;
    
    @ApiModelProperty(value = "预报当天总降水量，默认单位：毫米")
    private Double precip;
    
    @ApiModelProperty(value = "紫外线强度指数")
    private Double uvIndex;
    
    @ApiModelProperty(value = "相对湿度，百分比数值")
    private Double humidity;
    
    @ApiModelProperty(value = "大气压强，默认单位：百帕")
    private Double pressure;
    
    @ApiModelProperty(value = "能见度，默认单位：公里")
    private Double vis;
    
    @ApiModelProperty(value = "云量，百分比数值。可能为空")
    private Double cloud;
    
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
    
    public WeatherFutureDailyDTO() {
        this.collectionName = "weather_future_day_info";
    }
}
