package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

/**
 * 地图展示渠道信息vo迁移
 * @return:
 * @author: zheng.yan
 * @date: 2022/3/11
 * @version:
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RadarChannelInfoVO extends BaseIndustryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 可视范围
     */
    private String visualRange;
    /**
     * 水平辐射角度
     */
    private String horizRange;
    /**
     * 俯仰辐射角度
     */
    private String vertiRange;

}
