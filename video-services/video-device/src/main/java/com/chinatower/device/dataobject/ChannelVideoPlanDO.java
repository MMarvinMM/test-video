package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 通道录像计划表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Data
@TableName("vm_channel_video_plan_t")
public class ChannelVideoPlanDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 录像计划id
     */
    private Long recordPlanId;

    /**
     * 录像计划名称
     */
    private String recordPlanName;

    /**
     * 录像计划状态 1-启用；0-禁用
     */
    private Integer recordPlanStatus;

    /**
     * 生效开始日期
     */
    private String startDate;

    /**
     * 生效结束日期
     */
    private String endDate;

    /**
     * 是否全程录像 默认为0；1-是；0-否；如果为1，timeSections省略；
     */
    private Integer isAllDay;

    /**
     * 码流类型 1-主码流；0-子码流
     */
    private Integer streamType;

    /**
     * 存储类型 0-前端存储；1-中心存储；
     */
    private Integer storageType;

    /**
     * 存储时长 录像存储的最长周期 单位:天
     */
    private Integer storageDay;

    /**
     * 是否开启音频 1-是；0-否；
     */
    private Integer isAudio;

    private String deviceCode;

    private String channelCode;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 套餐编码
     */
    private String packageCode;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;

    //行业编码
    private String industryCode;

    //行业版本编码
    private String appVerCode;
}
