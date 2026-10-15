package com.chinatower.video.modules.query.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.List;

@Data
public class ArLayerTagReq extends BaseDataScopeEntity {
    /**
     * 主视频画面，摄像机编码
     */
    private String deviceCode;

    /**
     * 主视频画面，通道编码
     */
    private String channelCode;

    /**
     * 主视频画面，图层编码
     */
    private String layerId;

    /**
     * 标签id列表
     */
    private List<String> tagIdList;

    private String geoPointStr;
}
