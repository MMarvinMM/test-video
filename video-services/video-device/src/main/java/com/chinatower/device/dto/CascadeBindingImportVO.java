package com.chinatower.device.dto;

import com.chinatower.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class CascadeBindingImportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "云台控制权限")
    private String ptzPermission;

    @Excel(name = "录像回放权限")
    private String playPermission;

    @Excel(name = "实时视频权限")
    private String realtimeVideoPermission;

//    @Excel(name = "级联平台")
//    private String platformName;

    @Excel(name = "省份")
    private String provinceName;

    @Excel(name = "地市")
    private String cityName;

    @Excel(name = "区县")
    private String countyName;

    @Excel(name = "摄像机编号")
    private String deviceCode;

    @Excel(name = "摄像机名称")
    private String deviceName;

    @Excel(name = "摄像机通道编号")
    private String channelCodes;

    @Excel(name = "自定义通道编号")
    private String customChannelCodes;

    @Excel(name = "业务组织名称")
    private String businessOrgName;

    @Excel(name = "业务组织编码")
    private String businessOrgCode;

    @Excel(name = "一级虚拟组织名称")
    private String virtualOrgLevel1Name;

    @Excel(name = "一级虚拟组织编码")
    private String virtualOrgLevel1Code;

    @Excel(name = "二级虚拟组织名称")
    private String virtualOrgLevel2Name;

    @Excel(name = "二级虚拟组织编码")
    private String virtualOrgLevel2Code;

    @Excel(name = "三级虚拟组织名称")
    private String virtualOrgLevel3Name;

    @Excel(name = "三级虚拟组织编码")
    private String virtualOrgLevel3Code;

    @Excel(name = "四级虚拟组织名称")
    private String virtualOrgLevel4Name;

    @Excel(name = "四级虚拟组织编码")
    private String virtualOrgLevel4Code;

    @Excel(name = "五级虚拟组织名称")
    private String virtualOrgLevel5Name;

    @Excel(name = "五级虚拟组织编码")
    private String virtualOrgLevel5Code;

    @Excel(name = "六级虚拟组织名称")
    private String virtualOrgLevel6Name;

    @Excel(name = "六级虚拟组织编码")
    private String virtualOrgLevel6Code;

    private String provinceCode;

    private String cityCode;

    private String countyCode;
}
