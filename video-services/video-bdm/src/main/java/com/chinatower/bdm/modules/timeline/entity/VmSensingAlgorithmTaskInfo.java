package com.chinatower.bdm.modules.timeline.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 遥感算法任务信息表(VmSensingAlgorithmTaskInfo)实体类
 *
 * @author makejava
 * @since 2024-05-27
 */
@Data
@Accessors(chain = true)
@TableName("vm_sensing_algorithm_task_info")
@ApiModel(value = "VmSensingAlgorithmTaskInfo", description = "遥感算法任务信息表实体类")
public class VmSensingAlgorithmTaskInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务状态，1:执行中，2:成功，3:失败
     */
    private Integer taskStatus;

    /**
     * 算法code
     */
    private String algorithmCode;

    /**
     * 算法名称
     */
    private String algorithmName;

    /**
     * 图层1信息
     */
    private String layerOneInfo;

    /**
     * 图层2信息
     */
    private String layerTwoInfo;

    /**
     * 默认标识，1:默认，0或者null非默认,3待默认
     */
    private Integer defaultFlag;

    /**
     * 删除标识，0:未删除，1:删除
     */
    private Integer deleteFlag;

    /**
     * 行业信息
     */
    private String industryCode;

    /**
     * 行业应用版本编码
     */
    private String appVerCode;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
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

    /**
     * 任务执行失败时的错误信息
     */
    private String errorMsg;

    /**
     * 算法仓返回的任务批次id
     */
    private String taskBatchId;
    /**
     * 二次识别结果
     */
    private String secondRecogResult;

}
