package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName ReservePlanDO
 * @Deacription
 * @Author chenmf
 * @Date 2022/4/18 15:31
 * @Version 1.0
 **/
@Data
@TableName("vm_reserve_plan_channel_t")
public class ReservePlanChannelDO implements Serializable {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 预案id
     */
    private String planId;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 码流类型
     */
    private int streamType;
    /**
     * 是否调用预置位：1是0否
     */
    private String ifPreset;
    /**
     * 顺序
     */
    private String chanOrder;
    /**
     * 创建时间
     */
    private Date creatorDate;
    /**
     * 修改时间
     */
    private Date modifierDate;
    /**
     * 删除状态：0正常 1删除
     */
    private String deleteFlag;
    
    
}
