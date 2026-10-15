package com.chinatower.platform.project.numtwin.entity.numtwinsvo;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/15 11:44
 */

@Data
public class NumTwinsSaveDto {

    /**
     * 大屏唯一标识
     */
    private String uniqueIdentifier;

    /**
     * 数字孪生地址
     */
    private String digitalTwinAddress;

    /**
     * 并发量
     */
    private String concurrencyVolume;

    /**
     * 渲染引擎
     */
    private String renderingEngine;

    /**
     * 信令地址
     */
    private String signalingAddress;

    /**
     * 省份
     */
    private String provinceName;

    /**
     * 城市
     */
    private String cityName;

    /**
     * 区名称
     */
    private String districtName;

    /**
     * 大屏名称
     */
    private String digitalTwinName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 优先级设置
     */
    private List<NumTwinsUserDto> digitalUserSettingsBOList;


}
