package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 摄像机管理导出类
 */
@Data
@ToString
public class CameraDeviceVoExport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel( name =  "摄像机编号")
    private String deviceCode;

    @Excel( name =  "摄像机名称")
    private String devName;

    @Excel( name =  "摄像机状态")
    private String deviceStatusName;

    @Excel( name =  "离线时长")
    private String offLineTime;

    @Excel( name =  "摄像机厂家")
    private String modelName;

    @Excel( name =  "摄像机地址")
    private String location;

    @Excel(name = "订购状态")
    private String orderStatusName;

    @Excel(name = "所属组织")
    private String orgName;

    @Excel(name = "省份")
    private String provinceName;

    @Excel(name = "地市")
    private String cityName;

    @Excel(name = "区县")
    private String countryName;

    @Excel(name = "乡镇")
    private String streetName;

    //标签名称列表
    private List<String> labelNameList;

    @Excel(name = "摄像机标签")
    private String labelName;
}
