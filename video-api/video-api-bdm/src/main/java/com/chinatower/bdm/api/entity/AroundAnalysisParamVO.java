package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 周边分析入参vo
 * @author liuxt
 */
@Data
@ToString
public class AroundAnalysisParamVO extends BaseDataScopeEntity implements Serializable {
    /**
     * 经度
     * @required true
     */
    @NotBlank(message = "经度不能为空")
    private String longitude;
    /**
     * 纬度
     * @required true
     */
    @NotBlank(message = "纬度不能为空")
    private String latitude;
    /**
     * 检索半径，单位：米
     * @required true
     */
    @NotBlank(message = "检索半径不能为空")
    private String radius;

    /**
     * 告警结束时间（格式：yyyy-MM-dd HH:mm:ss，若type中包含5.告警，则必填）
     * @required false
     */
    private String alarmTimeEnd;


    /**
     * 告警开始时间（格式：yyyy-MM-dd HH:mm:ss，若type中包含5.告警，则必填）
     * @required false
     */
    private String alarmTimeStart;

    /**
     * 类型(1.摄像机 2.无人机 3.大喇叭 4.网格员 5.告警,6.资源,7.雷达,8.物联设备)
     * @required true
     */
    private String type;

    /**
     * 是否过滤同一个经纬度的数据
     */
    private Boolean notFilterSameLngLat = false;

    /**
     *  唯一标识
     */
    private String id;

    /**
     * 是否查询无人机飞行状态
     */
    private Boolean withFlightStatus;

}
