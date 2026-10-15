package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName OrderAlgorithmConfigDomain
 * @Deacription 订购算法配置表（设备-算法订购关系，运管同步）
 * @Author chenshaowu
 * @Date 2026/07/28
 * @Version 1.0
 **/
@Data
public class OrderAlgorithmConfigDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_code")
    private String orderCode;

    @TableField("industry_code")
    private String industryCode;

    @TableField("device_code")
    private String deviceCode;

    @TableField("algorithm_code")
    private String algorithmCode;

    @TableField("algorithm_type")
    private Integer algorithmType;

    @TableField("creator")
    private String creator;

    @TableField("modifier")
    private String modifier;

    @TableField("delete_flag")
    private Integer deleteFlag;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("tenant_id")
    private String tenantId;

    @TableField("accept_node")
    private String acceptNode;
}
