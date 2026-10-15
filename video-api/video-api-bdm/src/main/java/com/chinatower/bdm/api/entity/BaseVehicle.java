package com.chinatower.bdm.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;


/**
 *  车辆号牌库基类
 */
@Data
public class BaseVehicle implements Serializable {

    /**
     * 主键id
     */
    @TableId("id")
    private String id;

    /**
     * 租户Id
     */
    @TableField("tenant_id")
    private String tenantId;

    /**
     * 车辆类型
     */
    @TableField("vehicle_type")
    private String vehicleType;

    /**
     *  创建人
     */
    @TableField("creator")
    private String creator;


    /**
     *  创建时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     * 最后修改人
     */
    @TableField("modifier")
    private String modifier;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private String updateTime;

    /**
     * 逻辑删除标识
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 行业编码
     */
    @TableField("industry_code")
    private String industryCode;

    /**
     * 行业应用版本编码
     */
    @TableField("app_ver_code")
    private String appVerCode;

    @TableField(exist = false)
    private String createTimeStart;

    @TableField(exist = false)
    private String createTimeEnd;

    @TableField(exist = false)
    private String updateTimeStart;

    @TableField(exist = false)
    private String updateTimeEnd;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;
}
