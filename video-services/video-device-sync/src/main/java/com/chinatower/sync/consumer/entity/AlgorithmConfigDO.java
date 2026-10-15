package com.chinatower.sync.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 算法配置
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-19
 */
@Getter
@Setter
@TableName("vm_algorithm_config")
public class AlgorithmConfigDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置编号
     */
    @TableId(value = "config_id",type = IdType.INPUT)
    private String configId;

    /**
     *
     */
    @TableField(value = "device_code")
    private String deviceCode;

    @TableField(value = "device_name")
    private String deviceName;

    /**
     *
     */
    @TableField(value = "channel_code")
    private String channelCode;

    @TableField(value = "channel_name")
    private String channelName;

    /**
     * 位置类型：1通道，2预置位
     */
    @TableField(value = "pos_type")
    private Integer posType;

    /**
     * 预置位编号
     */
    @TableField(value = "preset_id")
    private Integer presetId;

    /**
     * 预置位名称
     */
    @TableField(value = "preset_name")
    private String presetName;

    /**
     * 客户编号
     */
    @TableField(value = "tenant_id")
    private String tenantId;

    @TableField(value = "tenant_name")
    private String tenantName;

    /**
     * 算法编号
     */
    @TableField(value = "algorithm_code")
    private String algorithmCode;

    /**
     * 点位json
     */
    @TableField(value = "points")
    private String points;

    /**
     * 截图
     */
    @TableField(value = "img")
    private String img;

    /**
     *
     */
    @TableField(value = "algorithm_name")
    private String algorithmName;

    /**
     * 监控任务唯一标识
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 算法任务状态：0:停用 1:启用
     */
    @TableField(value = "status")
    private String status;



    /**
     * 订单编码
     */
    @TableField(value = "order_code")
    private String orderCode;

    //铁塔视角算法状态：1:启用成功 2:启用失败 3:处理中
    @TableField(value = "algorithm_status")
    private String algorithmStatus;

    /**
     * 置信度阈值
     */
    @TableField(value = "thresh")
    private Float thresh;

    /**
     * 重复告警间隔
     */
    @TableField(value = "alarmInterval")
    private Integer alarmInterval;

    /**
     * 重复告警间隔单位:秒s,分M,时h,天d
     */
    @TableField(value = "alarmInterval_type")
    private String alarmintervalType;

    /**
     * 算法运行时间
     */
    @TableField(value = "algorithm_time")
    private String algorithmTime;
    /**
     * 行业编码
     */

    /**
     * 行业编码
     */
    @TableField(value = "industry_code_his")
    private String industryCodeHis;

    @TableField(value = "industry_code")
    private String industryCode;

    @TableField(value = "app_ver_code")
    private String appVerCode;

    @TableField(value = "creator")
    private String creator;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;
    @TableField(value = "modifier")
    private String modifier;
    @TableField(value = "update_time")
    private String updateTime;
    /**
     * 删除标识 正常0  删除1
     */
    @TableField(value = "delete_flag")
    private int deleteFlag;

    // 配置参数json
    @TableField(value = "model_config")
    private String modelConfig;
    // 绊线json
    @TableField(value = "trip_thread")
    private String tripThread;

    //算法分析时段
    @TableField(value = "analysis_period")
    private String analysisPeriod;

    /** 油气管线json */
    @TableField(value = "oil_gas_pipelines")
    private String oilGasPipelines;

    /** 设备受理节点 */
    @TableField(value = "accept_node")
    private String acceptNode;

    /** 设备接入节点 */
    @TableField(value = "access_node")
    private String accessNode;

    /** 任务抽帧频率*/
    @TableField(value = "time_interval")
    private Integer timeInterval;

    /** 抽帧频率单位,1:小时 2:分钟 3:秒 */
    @TableField(value = "time_interval_unit")
    private Integer timeIntervalUnit;
}
