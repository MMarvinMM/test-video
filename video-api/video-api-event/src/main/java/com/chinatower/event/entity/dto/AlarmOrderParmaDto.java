package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BaseModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AlarmOrderParmaDto extends BaseModel implements Serializable {

    /**
     * 摄像机编码集合
     * @required false
     */
    private List<String> deviceCode;

    /**
     * 物联编码集合
     * @required false
     */
    private List<String> deviceCodes;

    /**
     * 告警结束时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeEnd;

    /**
     * 告警开始时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeStart;

    /**
     * 通道编码
     * @required false
     */
    private String channelCode;

    /**
     * 预置位ID
     * @required false
     */
    private String presetId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * （0：全部  1：处理中）
     * @required false
     */
    private String flag;

    /**
     * 告警来源集合
     * @required false
     */
    private List<String> warningSourceList;

    /**
     * 告警状态集合
     * @required false
     */
    private List<String> orderStatusList;

    /**
     * 告警类型集合
     * @required false
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务编码列表（实际触发，与 warningTypeIdList 为 AND）
     * @required false
     */
    private List<String> subSceneIdList;

    /**
     * 告警类型查询
     * @required false
     */
    private String alarmType;

    /**
     * 告警状态查询
     * @required false
     */
    private String orderStatus;

    /**
     * 是否受理（1：待受理）
     * @required false
     */
    private String enableAcceptanceMode;
}
