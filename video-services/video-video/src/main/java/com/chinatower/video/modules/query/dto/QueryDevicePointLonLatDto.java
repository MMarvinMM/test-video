package com.chinatower.video.modules.query.dto;

import com.chinatower.event.entity.dto.DevicePointVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryDevicePointLonLatDto implements Serializable {

    private String deviceCode;

    private String channelCode;

    private String pan;

    private String tilt;

    private String zoom;

    private List<DevicePointVo> points;
}
