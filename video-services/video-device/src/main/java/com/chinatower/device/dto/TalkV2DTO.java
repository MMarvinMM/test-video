package com.chinatower.device.dto;

import com.chinatower.device.entity.dto.HornRangeDevInfoDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 喊话相关接口入参实体类
 */
@Data
public class TalkV2DTO implements Serializable {

    /**
     * 设备编码列表
     */
    private List<HornRangeDevInfoDTO> deviceInfoList;

    /**
     * 音量
     */
    private Integer volume;

    /**
     * 喊话流id
     */
    private Integer streamCodeId;

    /**
     * 终端忽略音量参数
     */
    private Boolean emergency;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 设备名称列表
     */
    private List<String> deviceNameList;

}
