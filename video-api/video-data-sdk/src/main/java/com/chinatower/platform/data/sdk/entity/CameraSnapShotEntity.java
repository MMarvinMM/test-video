package com.chinatower.platform.data.sdk.entity;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 1.1.13 抓图
 *
 * @author dencycheng
 */
@Data
@Builder
@SuppressWarnings("all")
public class CameraSnapShotEntity extends AbstactEntity implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 码流类型
     */
    private Integer streamType;
    /**
     * 图片回传服务地址
     */
    private String imageUpUrl;
    /**
     * 图片名称
     */
    private String imageName;

    /**
     * 租户id
     */
    private String tenantId;


    public CameraSnapShotEntity(String deviceCode, String channelCode, Integer streamType, String imageUpUrl, String imageName,String tenantId) {
        super(URL.CAMERA_SNAP_SHOT_PRESET, JSON.toJSONString(new HashMap<String, Object>() {
            {
                this.put("deviceCode", deviceCode);
                this.put("channelCode", channelCode);
                this.put("streamType", streamType);
                this.put("imageUpUrl", imageUpUrl);
                this.put("imageName", imageName);
                this.put("tenantId", tenantId);
            }
        }));
        this.deviceCode = deviceCode;
        this.channelCode = channelCode;
        this.streamType = streamType;
        this.imageUpUrl = imageUpUrl;
        this.imageName = imageName;
        this.tenantId = tenantId;
    }
}
