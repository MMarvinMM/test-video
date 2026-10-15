package com.chinatower.video.modules.videorecordplan.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 通道录像计划时间段表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Data
@TableName("vm_plan_time_section_t")
public class PlanTimeSectionDO  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "time_section_id", type = IdType.AUTO)
    private Long timeSectionId;

    /**
     * 录像计划id
     */
    private Long recordPlanId;

    /**
     * 日期
     */
    private Integer dayOfWeek;

    /**
     * 录像开始时间
     */
    private String startTime;

    /**
     * 录像结束时间
     */
    private String endTime;

    /**
     * 创建人
     */
    private String creator;


    /**
     * 修改人
     */
    private String modifier;



    /**
     * 删除标识0正常 1删除
     */
    private int deleteFlag;
    private String industryCode;

    private String appVerCode;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private String deviceCode;
}
