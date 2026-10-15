package com.chinatower.device.mongodb.dto;

import com.chinatower.device.dto.AlgorithmHornContentVO;
import com.chinatower.device.dto.AlgorithmHornInfoVO;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "algorithm_horn_task_info")
public class AlgorithmHornTaskDTO extends BaseMongoDTO {

    private Long taskId;

    /**
     * 内容类型  0-音频 1-文本
     */
    private Integer taskType;

    private List<AlgorithmHornContentVO> contentInfos;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业名称
     */
    private String industryName;

    /**
     * 任务关联喇叭数据
     */
    private List<AlgorithmHornInfoVO> deviceInfos;

    /**
     * 受理节点
     */
    private String acceptNode;

    /*
     * 启动状态 0-未启动 1-启动中.
     */
    private Integer startupStatus;

    /*
     * 执行状态 0-未执行 1-执行中. 2-部分执行中
     */
    private Integer executStatus;

    /*
     * 操作时间.
     */
    private Date operateTime;

    /*
     * 操作时间.
     */
    private Date createTime;


    private Date updateTime;
}
