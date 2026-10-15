package com.chinatower.video.modules.query.dto;

import com.chinatower.platform.model.dto.PercentPointDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yy
 */
@Data
public class PointsDto implements Serializable {

    private String monitorAreaId;

    private List<PercentPointDto> moniPointList;

    //1：屏蔽区域，0：监控区域，默认为否
    private int monitorType;
}
