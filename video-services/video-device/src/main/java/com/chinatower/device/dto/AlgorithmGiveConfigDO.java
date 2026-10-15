package com.chinatower.device.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.chinatower.platform.entity.device.TriggerRuleJson;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("vm_algorithm_give_config")
public class AlgorithmGiveConfigDO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String deviceCode;
    private String deviceName;
    private String channelCode;
    private String channelName;
    // 是否二次研判 0：是  1：否
    private String secondSwitch;
    private String alarmType;
    private String alarmTypeName;
    private String algorithmCode;
    private String algorithmName;
    // 算法任务状态：0:启用 1:停用
    private String status;
    // 基础平台同步状态，1操作成功；2操作失败；
    private String syncStatus;
    private String orderCode;
    private String grabUrl;
    private String videoUrl;
    private String tenantId;
    private String tenantName;
    private String industryCode;
    private String appVerCode;
    private String creator;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String modifier;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String deleteFlag;
    @TableField(exist = false)
    List<SysDictDataResponse> dictValues;

    private String triggerRule;

    @TableField(exist = false)
    private List<Integer> ids;
}
