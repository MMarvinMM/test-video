package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName WarningSensorMonitorVO
 * @Deacription
 * @Author luo.zhenghong
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Data
public class WarningSensorMonitorVO implements Serializable {

    /**
     * 监测指标编号（因子编号）
     */
    private String monitorCode;

    /**
     * 检测指标名称（监测因子名称）
     */
    private String monitorName;

    /**
     * 监测值
     */
    private String monitorValue;

    /**
     * 告警阈值
     */
    private String thresholdValue;

    /**
     * 告警工单id
     */
    private String warningOrderId;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 备注
     */
    private String remark;

    //前端回显物联因子集合
    private List<OneFactoryVO> newMonitors;

}
