package com.chinatower.platform.data.sdk.entity;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 1.1.8 转到预置点
 *
 * @author dencycheng
 */
@Data
@Builder
@SuppressWarnings("all")
public class CameraDoControlPresetEntity extends AbstactEntity implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 预置点Id
     */
    private Integer presetId;

    /**
     * 租户id
     */
    private String tenantId;


    public CameraDoControlPresetEntity(String deviceCode, String channelCode,Integer presetId,String tenantId) {
        super(URL.CAMERA_DO_CONTROL_PRESET, JSON.toJSONString(new HashMap<String, Object>() {
            {
                this.put("deviceCode", deviceCode);
                this.put("channelCode", channelCode);
                this.put("presetId", presetId);
                this.put("tenantId", tenantId);
            }
        }));
        this.deviceCode = deviceCode;
        this.channelCode = channelCode;
        this.presetId = presetId;
        this.tenantId = tenantId;
    }
}
