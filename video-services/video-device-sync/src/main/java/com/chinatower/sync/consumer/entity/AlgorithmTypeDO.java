package com.chinatower.sync.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 算法类型信息表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
@Getter
@Setter
@TableName("vm_algorithm_type_t")
public class AlgorithmTypeDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "algorithm_type_id", type = IdType.AUTO)
    private Long algorithmTypeId;

    /**
     * 算法小类编码
     */
    private String algoMinTypeCode;

    /**
     * 算法小类名称
     */
    private String algoMinTypeName;

    /**
     * 算法大类编码
     */
    private String algoTypeCode;

    /**
     * 算法大类名称
     */
    private String algoTypeName;

    /**
     * 算法中类编码
     */
    private String algoSubTypeCode;

    /**
     * 算法中类名称
     */
    private String algoSubTypeName;

    /**
     * 1-有效；2-失效 
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0正常1删除
     */
    private String deleteFlag;


}
