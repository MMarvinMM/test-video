package com.chinatower.platform.ecmwf.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SysEcmwf  {

    //主键ID
    private String ecmwfId;

    //类型tp,sp,2t,r,10u,10v
    private String ecmwfType;

    //入库文件路径
    private String ecmwfPath;

    //开始时间
    private Date beginTime;

    //终止时间
    private Date endTime;

    //入库时间
    private Date inTime;

    //数据时间
    private String dataTime;

    //气象数据
    private String ecmwfStr;

    //起始纬度
    private String beginLat;

    //终止纬度
    private String endLat;

    //起始经度
    private String beginLon;

    //终止经度
    private String endLon;

    //网格
    private String grib;

    //横向网格量
    private String longthNum;

    //纵向网格量
    private String heightNum;

    //最大值
    private String maxData;

    //最小值
    private String minData;

    //数据集的单位
    private String gribUnit;
}
