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
 * 巡航路径信息表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Getter
@Setter
@TableName("vm_patrol_info_t")
public class PatrolInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 巡航路径id
     */
    @TableId(value = "patrol_id", type = IdType.INPUT)
    private Long patrolId;

    /**
     * 巡航路径名称
     */
    private String patrolName;

    private String deviceCode;

    private String channelCode;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 创建人
     */
    private String creator;



    /**
     * 修改人
     */
    private String modifier;

    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 巡航启动状态：1-停用；2-启用；
     */
    private String ifCruise;

    /**
     * 巡航计划时间段
     */
    private String startTimePeriod;

    /**
     * 预约启动状态：1-停用；2-启用；
     */
    private String startStop;

    /**
     * 预约启动时间
     */
    private Date startStopDate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    //行业编码
    private String industryCode;

    //行业版本编码
    private String appVerCode;


}
