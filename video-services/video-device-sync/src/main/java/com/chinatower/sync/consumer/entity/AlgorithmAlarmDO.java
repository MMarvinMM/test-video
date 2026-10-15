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
 * 算法与告警类型关系表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
@Getter
@Setter
@TableName("vm_algorithm_alarm")
public class AlgorithmAlarmDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 算法小类编码
     */
    private String algoMinType;

    /**
     * 告警类型编码
     */
    private String alarmType;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 变更时间
     */
    private Date updateDate;

    /**
     * 0:失效 1:有效
     */
    private String deleteFlag;


}
