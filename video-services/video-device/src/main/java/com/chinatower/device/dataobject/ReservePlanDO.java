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
@TableName("vm_reserve_plan_t")
public class ReservePlanDO implements Serializable {
    
    /**
     * 预案id
     */
    @TableId(value = "plan_id", type = IdType.INPUT)
    private String planId;
    /**
     * 预案名称
     */
    private String planName;
    /**
     * 预案类型：1公有预案；2私有预案
     */
    private String planType;
    
    /**
     * 轮巡间隔:单位秒（s）
     */
    private String patrolInterval;
    /**
     * 是否自动执行预案
     */
    private String ifAutoExecute;
    /**
     * 执行开始时间
     */
    private String planStartTime;
    /**
     * 执行结束时间
     */
    private String planEndTime;
    /**
     * 用户编码
     */
    private String userCode;
    /**
     * 租户id
     */
    private String tenantId;
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

    /**
     * 行业编码
     */
    private String industryCode;
    
    /**
     * 行业版本编码
     */
    private String appVerCode;
    
    /**
     * 修改人
     */
    private String modifier;
}
