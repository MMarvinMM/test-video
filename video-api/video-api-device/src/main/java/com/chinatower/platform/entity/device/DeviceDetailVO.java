package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DeviceDetailVO
 * @Description 摄像头属性
 */

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDetailVO implements Serializable {

    private String deviceCode;

    private String deviceName;

    private String channelCode;

    private String channelName;

    private String categoryCode;

    private String categoryName;

    /**
     * 标签
     *
     */
    private transient List lablelList;

    private String modelCode;

    private String modelName;

    private String deviceModelCode;

    private String longitude;

    private String latitude;



}
