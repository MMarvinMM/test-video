package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 物联设备表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RadarDeviceVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  雷达id
     */
    private String radarId;

    /**
     * 设备信息
     */
    private List<RadarDeviceInfoVO> devices;

}
