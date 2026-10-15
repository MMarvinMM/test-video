package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.vo.BaseAlarmVO;
import lombok.Data;


/**
 * @Auther: lc
 * @Date: 2022年4月26日
 * @Description:
 * 车辆卡口摄像机告警实体
 */
@Data
public class CheckpointAlarmVO extends BaseAlarmVO {

    private static final long serialVersionUID = -5682469723548070577L;

    /** 告警ID */
    private String alarmId;
    /** 市编码 */
    private String cityCode;
    /** 县编码 */
    private String countyCode;
    /** 省编码 */
    private String provinceCode;
    /** 市名称 */
    private String cityName;
    /** 县名称*/
    private String countyName;
    /** 省名称 */
    private String provinceName;
    /** 1-1级，需应用在告警列表展示，默认值为1；2-2级，需在工作台内静默展示；*/
    private String importanceLevel;
    /** 告警来源 1-算法AI告警；2-雷达告警；3-无人机告警；*/
    private String alarmSource;
    /** 删除标识：0正常1删除 */
    private String deleteFlag;
    /** 车牌号 */
    private String vehicleNumber;
    /** 车牌类型 */
    private String vehicleType;
    /** 车牌颜⾊ */
    private String vehicleNumColor;
    /** 车辆颜⾊ */
    private String vehicleColor;
}
