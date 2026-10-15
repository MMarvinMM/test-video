package com.chinatower.platform.entity.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class WeatherWarningVO implements Serializable {


    //本条预警的唯一标识，可判断本条预警是否已经存在
    private String id;

    // 预警发布时间
    private String pubTime;

    // 预警信息标题
    private String title;

    //预警严重等级
    private String severity;

    //预警严重等级名称
    private String severityName;

    //预警严重等级颜色，可能为空
    private String severityColor;

    //预警严重等级颜色名称
    private String severityColorName;

    //预警类型id
    private String type;

    //预警类型名称
    private String typeName;

    //预警详细信息
    private String text;

    //说明
    private String remark;

    //防御指南
    private String defenseOrder;



}
