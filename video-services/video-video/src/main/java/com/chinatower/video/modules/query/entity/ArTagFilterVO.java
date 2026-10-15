package com.chinatower.video.modules.query.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ArTagFilterVO extends BaseDataScopeEntity implements Serializable {
    /**
     * 标签id列表
     */
    private List<String> tagIdList;

    /**
     * 打点经度
     */
    private double longitude;

    /**
     * 打点纬度
     */
    private double latitude;

    private String geoPointStr;

    /**
     * 主视频画面摄像机编码
     */
    private String arViewId;

    /**
     * 主视频画面通道编码
     */
    private String channelCode;
}
