package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @ClassName PresetVO
 * @Description 预置位参数
 * @Author Chensw
 * @Date 2022/1/24
 **/
@Data
@ToString
public class PresetDTO extends BaseDataScopeEntity {

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
    /** 设备编号 */
    private String deviceCode;
    /** 通道编号 */
    private String channelCode;

    /**
     * 客户编码
     */
    private String tenantId;
    /** 用户编码 */
    private String userCode;
    /** 修改时间 */
    private Date modifyTime;
    /** 订单编码 */
    private String orderCode;

    /** 转动速度 */
    private Integer moveSpreed;
    /** 预置位编码数组 */
    private long[] presets;

    /** 停留时间 单位：秒 */
    private Long staySeconds;

    /** 云台控制令牌，解锁时必填。 */
    private String lockToken;

    /** 巡航路径id */
    private Long patrolId;

    /** 巡航速度 转置到目标预置点的速度：0-全速； 1-1/2倍速； 2-1/4倍速； 3-1/8倍速； 4-1/16倍速； */
    private Integer speed;

    /** 预置位排序 */
    private Integer sort;
    /** 设备编码集合 */
    private List<String> devIdList;

    /** 码流类型 */
    private int streamType;
    /** 图片回传服务地址 */
    private String imageUpUrl;
    /** 图片名称 */
    private String imageName;

    /** 0需要标绘 */
    int isplot = 0;

}
