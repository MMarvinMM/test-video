package com.chinatower.video.modules.artags.dataobject;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagLocationVO extends BaseDataScopeEntity {
    private String deviceCode;
    private String deviceName;
    private String channelCode;
    private String channelName;
    private String longitude;
    private String latitude;
    private String location;
    private String visualRadius;
    private String distance;
}
