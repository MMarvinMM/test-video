package com.chinatower.event.dto.zhongshi;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2025/4/29 14:41
 */

@Data
@TableName("vm_zhongshi_algorithm_t")
public class AlgorithmDto {

    /**
     * 表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户id
     */
    private String tenantId;

    private String industryCode;

    private String passRate;

    private String passNum;

    private String unit;

    private String createUser;

    private String sum;

    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    private String delete_flag;
}
