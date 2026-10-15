package com.chinatower.event.dto;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName StatisticsDTO
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/16
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsDTO extends BaseDataScopeEntity implements Serializable {

    //维度标识:1当年；2当月；3前一天;4当天
    private String dimensionFlag;

    //趋势标识:year年；month月；day天
    private String trendFlag;

    //开始时间(格式：yyyy-MM-dd HH:mm:ss）
    private String queryStartTime;
    //结束时间(格式：yyyy-MM-dd HH:mm:ss）
    private String queryEndTime;

    //告警事件状态：0、虚假告警完结 1、待研判    2、派单中    3、抢单中    4、待处置  5、待核实 6、工单完成  7、重复告警完成
    private String orderStatus;

    //客户编码
    private String tenantId;

    private String alarmTypeId;

    private String algorithmCode;

    private String algorithmName;

    private String productName;

    //省id
    private String provinceId;

    //市id
    private String cityId;

    //区县id
    private String countyId;

    /**
     * 行业应用编码
     */
    private String appVerCode;
}
