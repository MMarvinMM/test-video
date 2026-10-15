package com.chinatower.video.api.entity;

import com.chinatower.platform.model.vo.DeviceModelVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data

public class VideoPtzRes {
    //顶点百分比坐标列表
    private List<PercentVO> vertexPercentList;

    //顶点ptz列表
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PresetData> vertexPtzList;

    //中心点ptz
    private PresetData centerPointPtz;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DeviceModelVO deviceModel;
}
