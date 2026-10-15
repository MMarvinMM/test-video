package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
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

    private String orderCode;

    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;

    /**
     * 是否为看守位 0否 1是
     */
    private String isGuard = "0";

    private int staySeconds;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;


    //行业编码
    private String industryCode;

    //行业版本编码
    private String appVerCode;

}
