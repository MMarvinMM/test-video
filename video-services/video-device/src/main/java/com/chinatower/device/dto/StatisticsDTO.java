package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName StatisticsDTO
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/16
 **/
@Data
public class StatisticsDTO extends BaseDataScopeEntity implements Serializable {

    //维度标识:1当年；2当月；3前一天;4当天
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dimensionFlag;

    //趋势标识:year年；month月；day天
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trendFlag;

    //开始时间(格式：yyyy-MM-dd HH:mm:ss）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String queryStartTime;
    //结束时间(格式：yyyy-MM-dd HH:mm:ss）
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String queryEndTime;

    //告警事件状态：0虚假告警完结 1 待研判    2 派单中    3抢单中    4 待处置  5待核实 6工单完成  7重复告警完成
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orderStatus;

    //权限类型：//1全部摄像头权限;2组织下摄像头权限;3仅本人可见
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String anthType;

    //客户编码
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tenantId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List countyIdList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List ttProvinceIdList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List ttCityIdList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> typeList;//告警类型ID列表

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sourceFlag; //1租户表；2铁塔表

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String otherFlag; //1包含其他

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tenantListAuthority; //1省份地市无下属租户或租户下无设备

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provinceId;//省id

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cityId;//市id

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countyId;//区县id

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> checkList;//选择导出的sheet页

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alarmTypeId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String algorithmCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String algorithmName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String productName;
}