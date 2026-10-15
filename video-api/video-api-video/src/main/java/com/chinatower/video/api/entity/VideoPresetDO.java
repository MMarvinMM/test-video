package com.chinatower.video.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

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
@Alias("VideoPresetDOVideo")
public class VideoPresetDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预置点id
     */
    @TableId(value = "preset_id", type = IdType.INPUT)
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
     * 是否为看守位 0否 1是
     */
    private String isGuard = "0";

    private String industryCode;

    private String appVerCode;

}
