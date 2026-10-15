package com.chinatower.airline.dto.response;

import com.chinatower.device.entity.vo.DeviceCameraModelVO;
import lombok.Data;

import java.util.List;

@Data
public class UavCameraInfoRep {
    /**
     * 镜头信息集合
     */
    List<DeviceCameraModelVO>  cameraModelVOList;
}
