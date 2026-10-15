package com.chinatower.device.entity.vo;

import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.DeviceCameraModelVO;
import com.chinatower.device.entity.vo.UavModelVO;
import com.chinatower.device.entity.vo.UavNestModelVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UavDetailVO implements Serializable {

    /**
     * 无人机信息
     */
    private UavDeviceDTO baseInfo;

    /**
     * 无人机相机型号信息
     */
    private List<DeviceCameraModelVO> cameraModelVOList;

    /**
     * 无人机型号信息列表
     */
    private List<UavModelVO> uavModelVOList;

    /**
     * 无人机机巢信息列表
     */
    private List<UavNestModelVO> uavNestModelVOList;
}
