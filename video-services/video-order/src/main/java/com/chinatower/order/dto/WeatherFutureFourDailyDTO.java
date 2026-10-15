package com.chinatower.order.dto;

import com.chinatower.platform.entity.order.WeatherFutureDailyDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @ClassName weatherDailyDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/31 11:03
 * @Version 1.0
 **/
@Data
@ApiModel(value = "未来四天预报天气",description = "未来四天预报天气")
@Document(collection="weather_future_day_info_4")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherFutureFourDailyDTO extends WeatherFutureDailyDTO {

    public WeatherFutureFourDailyDTO() {
        this.collectionName = "weather_future_day_info_4";
    }
}
