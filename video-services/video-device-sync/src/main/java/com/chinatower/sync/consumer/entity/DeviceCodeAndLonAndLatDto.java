package com.chinatower.sync.consumer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCodeAndLonAndLatDto implements Serializable {

    private String deviceCode;

    private String longitude;

    private String latitude;

    private String provinceId;

    private String cityId;

    private String countyId;
}
