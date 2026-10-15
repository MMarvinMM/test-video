package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AlgorithmListVO
 * @Description
 * @Author Chensw
 * @Date 2022/1/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmListVO {

    /**
     * 设备编码
     * @required true
     */
    String deviceCode;
    /**
     * 通道编码
     * @required true
     */
    String channelCode;
    /**
     * 租户编码
     */
    String tenantId;

    /**
     * 设备名称
     * @required true
     */
    String deviceName;
    /**
     * 通道名称
     * @required true
     */
    String channelName;

    /** 订购编码 */
    private String orderCode;

    /** 算法数量 */
    Integer algorithmCount;

    /**
     * 点位列表
     * @required true
     */
    List<AlgorithmConfigVO> positions;
    /** 行业编码 */
    String industryCode;

    /** 已订购算法数量 */
    Integer orderAlgoCount = 0;
    /** 已配置算法数量 */
    Integer algoConfigCount = 0;
    /** 待配置算法数量 */
    Integer toBeConfigCount = 0;


    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /** 可视半径，单位：米 */
    private String visualRadius;

    /** 标识 */
    private String platFlag;
}
