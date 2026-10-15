package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 雷达管理导出类
 */
@Data
@ToString
public class RadarDeviceVoExport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "雷达编号")
    private String deviceCode;

    @Excel(name = "雷达名称")
    private String deviceName;

    @Excel(name = "雷达状态")
    private String deviceStatusName;

    @Excel(name = "雷达厂家")
    private String modelName;

    @Excel(name = "所属组织")
    private String orgName;

    @Excel(name = "订购状态")
    private String orderStatus;

    @Excel(name = "省份")
    private String provinceName;

    @Excel(name = "地市")
    private String cityName;

    @Excel(name = "区县")
    private String countyName;

    @Excel(name = "乡镇")
    private String townName;

}
