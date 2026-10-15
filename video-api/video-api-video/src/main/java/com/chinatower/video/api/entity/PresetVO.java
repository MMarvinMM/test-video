package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName PresetVo
 * @Description
 * @Author chenshaowu
 * @Date 2022/3/13
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("PresetVOVideo")
public class PresetVO {

    /**
     * 预置点id
     */
    private Long presetId;

    /**
     * 预置点名称
     */
    private String presetName;

    /**
     * 预置点编码
     */
    private String presetCode;

    /**
     * 转动速度(范围0~254)
     */
    private Integer moveSpeed;

    /**
     * ⽔平参数[0, 3600]
     */
    private Float pan;

    /**
     * 垂直参数[-1800, 1800]
     */
    private Float tilt;

    /**
     * 变倍参数[0, 128]
     */
    private Float zoom;

    /**
     * 预置位图片Url
     */
    private String imgUrl;

    private String deviceCode;

    private String channelCode;

    /**
     * 客户编码
     */
    private String tenantId;

    private String userCode;

    private Date createTime;

    private Date modifyTime;

    private String orderCode;

    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;

    /**
     * 0需要标绘
     */
    Integer isPlot = 0;

}
