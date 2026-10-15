package com.chinatower.event.dto.zhongshi;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 大模型测试验证信息表
 */
@Data
@TableName("large_model_validation")
public class LargeModelValidationDTO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("model_name")
    private String modelName;

    @TableField("downstream_algorithm")
    private String downstreamAlgorithm;

    @TableField("test_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date testDate;

    @TableField("test_times")
    private Long testTimes;

    @TableField("is_passed")
    private Integer passed;

    @TableField("first_pass_rate")
    private BigDecimal firstPassRate;

    @TableField("final_pass_rate")
    private BigDecimal finalPassRate;

    @TableField("creator")
    private String creator;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField("modifier")
    private String modifier;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableField("delete_flag")
    private Integer deleteFlag;

    @TableField("tenant_id")
    private String tenantId;

    @TableField("industry_code")
    private String industryCode;

    /**
     * 分页页码（查询参数）
     */
    @TableField(exist = false)
    private Integer pageNum;

    /**
     * 分页大小（查询参数）
     */
    @TableField(exist = false)
    private Integer pageSize;

    /**
     * 查询开始日期（查询参数）
     */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    /**
     * 查询结束日期（查询参数）
     */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
}
