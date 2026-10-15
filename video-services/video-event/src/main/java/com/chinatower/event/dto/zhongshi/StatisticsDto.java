package com.chinatower.event.dto.zhongshi;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2025/5/12 17:53
 */

@Data
@TableName("vm_zhongshi_statistics_t")
public class StatisticsDto {

    /**
     * 表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 功能统计
     */
    private String functionCount;

    /**
     * 功能用例总数
     */
    private String functionCaseCount;

    /**
     * 功能已完成数量
     */
    private String functionEndCount;

    /**
     * 功能通过率
     */
    private String functionPassRate;

    /**
     * 算法统计
     */
    private String algorithmCount;

    /**
     * 算法用例总数
     */
    private String algorithmCaseCount;

    /**
     * 算法已完成数量
     */
    private String algorithmEndCount;

    /**
     * 算法通过率
     */
    private String algorithmPassRate;

    private String createUser;

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
