package com.chinatower.event.vo;

import com.chinatower.event.entity.dto.AlarmLableRelAPIDTO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: BigScreenOrderListVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/6/8 16:14
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class BigScreenOrderListVO extends OrderInfoVO implements Serializable {

    /**
     * 原图
     */
    private String imgUrl;

    /**
     * 创建时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;

    /**
     * 时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String reciveTime;

    /**
     * 处理人名称
     */
    private String processName;

    /**
     * 环节时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String flowNodeTime;

    /**
     * 时限
     */
    private String timeLimit;

    /**
     * 处理人ID
     */
    private String processId;


    /**
     *
     */
    private String groupId;

    private String icon;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     */
    private String realLinkType;

    private String duration;

    private String timeLimitAcceptance;

    /**
     * 告警标签集合
     */
    private List<AlarmLableRelAPIDTO> alarmLableRelDTOList;

    /**
     * 告警框图
     */
    private String imgUrlIcon;
}
