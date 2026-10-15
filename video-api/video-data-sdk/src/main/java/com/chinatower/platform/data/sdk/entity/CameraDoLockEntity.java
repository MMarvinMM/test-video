package com.chinatower.platform.data.sdk.entity;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 1.1.1 获取云台控制权
 *
 * @author dencycheng
 */
@Data
@Builder
@SuppressWarnings("all")
public class CameraDoLockEntity extends AbstactEntity implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 有效时长
     */
    private String duration;
    /**
     * 待释放控制权对应的令牌
     */
    private String token;

    public CameraDoLockEntity(String deviceCode, String channelCode, String duration, String token) {
        super(URL.CAMERA_DO_LOCK, JSON.toJSONString(new HashMap<String, Object>() {
            {
                this.put("deviceCode", deviceCode);
                this.put("channelCode", channelCode);
                this.put("duration", duration);
                this.put("token", token);
            }
        }));
        this.deviceCode = deviceCode;
        this.channelCode = channelCode;
        this.duration = duration;
        this.token = token;
    }
}
