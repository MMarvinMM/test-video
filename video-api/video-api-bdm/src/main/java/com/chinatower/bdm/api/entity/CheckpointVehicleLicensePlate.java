package com.chinatower.bdm.api.entity;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.annotation.Excel;
import lombok.Data;

import java.util.ArrayList;

@Data
@TableName("vm_checkpoint_vehicle_license_plate_t")
public class CheckpointVehicleLicensePlate extends BaseVehicle {



    /**
     * 1黑名单 2白名单
     */
    @TableField("black_or_white")
    private Integer blackOrWhite;

    /**
     * 车牌号
     */
    @Excel(name = "车牌号",type= Excel.Type.ALL)
    @TableField("vehicle_number")
    private String vehicleNumber;


    /**
     * 生效时段(返显)
     */
    @TableField(exist = false)
    private JSONArray sxTime;

    /**
     * 生效时段(返显)
     */
    @TableField("valid_period_vo")
    private String validPeriodVO;

    /**
     * 生效时段
     */
    @Excel(name = "生效时间段",type= Excel.Type.ALL)
    @TableField("valid_period")
    private String validPeriod;

    /**
     * 车辆类型
     */
    @TableField("vehicle_type")
    private String vehicleType;

    /**
     * 车辆类型名称
     */
    @Excel(name = "车辆类型",type= Excel.Type.EXPORT)
    @TableField(exist = false)
    private String vehicleName;



    /**
     * 负载重量，单位：吨
     */
    @Excel(name = "负载重量（吨）",type= Excel.Type.EXPORT)
    @TableField("load_weight")
    private String loadWeight;

    /**
     * 省份编码
     */
    @TableField("province_code")
    private String provinceCode;

    /**
     * 地市编码
     */
    @TableField("city_code")
    private String cityCode;


    /**
     * 区县编码
     */
    @TableField("county_code")
    private String countyCode;

    /**
     * 行政区域
     */
    @Excel(name = "行政区域",type= Excel.Type.EXPORT)
    @TableField(exist = false)
    private String administrativeArea;

    /**
     * 图片
     */
    @TableField("img_url")
    private String imgUrl;

    @TableField(exist = false)
    private String sortName;

    @TableField(exist = false)
    private String sort;

    @TableField(exist = false)
    private String acceptNode;

    @TableField(exist = false)
    private String platformCode;


}
