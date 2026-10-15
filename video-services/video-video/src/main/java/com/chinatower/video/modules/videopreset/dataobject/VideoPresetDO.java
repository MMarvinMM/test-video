package com.chinatower.video.modules.videopreset.dataobject;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 预置点信息表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Getter
@Setter
@TableName("vm_video_preset")
public class VideoPresetDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    private String orderCode;

    /**
     * 删除标识 正常0  删除1
     */
    private Integer deleteFlag;

    /**
     * 是否为看守位 0否 1是
     */
    private String isGuard;

    private String industryCode;

    private String appVerCode;

    private String creator;

    private String modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date modifyTime;

    private Integer staySeconds;
    
    /**
     * 抓图周期 0:无、1:每天、2:每周、3:每月
     */
    private Integer snapshotCycle;
    
    /**
     * 哪天抓图
     */
    private String snapshotDay;

    /**
     * 抓图时间
     */
    private String snapshotTime;

    /**
     * 是否开启算法识别任务 0：否 1：是
     */
    private String isAlgorithmIdentify;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 是否开启双通道并行分析 0：否，1：是
     */
    private String doubleImgAnalysis;
}
