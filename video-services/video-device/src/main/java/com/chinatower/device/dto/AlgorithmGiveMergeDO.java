package com.chinatower.device.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.platform.entity.device.DeviceChannelVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("vm_algorithm_give_merge_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmGiveMergeDO {

    private Integer id;
    private String deviceCode;
    private String channelCode;
    // 告警类型编码(未开启二次研判)
    private String alarmTypeNoalg;
    //告警类型编码(开启二次研判)
    private String alarmType;
    private String algorithmCode;
    private String grabUrl;
    private String videoUrl;
    private Integer mergeId;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String deleteFlag;
    private String syncStatus;
    private String failReason;
    private String pictureInfo;
    private String videoSetInfo;


    @TableField(exist = false)
    private List<DeviceChannelVO> deviceChannelList;

    private String triggerRule;

    @TableField(exist = false)
    private List<String> triggerRuleList;
}
