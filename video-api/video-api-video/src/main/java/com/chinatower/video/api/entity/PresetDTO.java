package com.chinatower.video.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

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
@Alias("PresetDTOVideo")
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

    private String deviceCode;

    private String channelCode;

    /**
     * 客户编码
     */
    private String tenantId;

    private String userCode;

    private Date modifyTime;

    private String orderCode;


    /** 转动速度 */
    private Integer moveSpreed;

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

    private List<String> devIdList;

    /** 码流类型 */
    private int streamType;
    /** 图片回传服务地址 */
    private String imageUpUrl;
    /** 图片名称 */
    private String imageName;

    /** 0需要标绘 */
    int isplot = 0;

    private String industryCode;

    private String appVerCode;
    
    // 用来区分是否是定时任务抓图，如果值是1则是定时抓图，否则不是
    private String presetSnapshotJob;

    /**
     * 1-双通道抓图；0-单通道抓图;默认单通道抓图
     */
    private Integer doubleChannelSnapshotType;

    /**
     * 是否开启双通道并行分析 1是，0否
     */
    private String doubleImgAnalysis;
}
