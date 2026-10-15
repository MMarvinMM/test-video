package com.chinatower.platform.modules.alarm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 算法权重表-大数据平台获取
 * 
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vm_algorithm_weight_t")
public class VmAlgorithmWeightDO extends BaseDataScopeEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 算法唯一编码
     */
    private String algorithmCode;

    /**
     * 算法名称
     */
    private String algorithmName;

    /**
     * 算法权重
     */
    private String algorithmWeight;



}