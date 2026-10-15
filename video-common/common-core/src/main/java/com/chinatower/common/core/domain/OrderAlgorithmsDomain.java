package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName OrderAlgorithmsDO
 * @Deacription 订单算法信息表
 * @Author chenmf
 * @Date 2022/1/6 11:14
 * @Version 1.0
 **/
@Data
public class OrderAlgorithmsDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    //订购关系编码
    @TableField("order_code")
    private String orderCode;

    //客户编码/租户id
    @TableField("tenant_id")
    private String tenantId;

    //套餐编码
    @TableField("package_code")
    private String packageCode;

    //算法编码
    @TableField("algorithm_code")
    private String algorithmCode;

    //算法名称
    @TableField("algorithm_name")
    private String algorithmName;

    //订单状态
    @TableField("ord_status")
    private Integer ordStatus;

    //删除标志位
    @TableField("delete_flag")
    private Integer deleteFlag;

    //行业编码
    @TableField("industry_code")
    private String industryCode;

    //行业版本编码
    @TableField("app_ver_code")
    private String appVerCode;

    //创建时间
    @TableField("create_time")
    private Date createTime;

    //更新时间
    @TableField("update_time")
    private Date updateTime;

    //迁移标志累加
    @TableField("move_sign")
    private Integer moveSign;

    /**
     * 受理节点
     */
    @TableField("accept_node")
    private String acceptNode;
}
