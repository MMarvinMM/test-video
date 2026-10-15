package com.chinatower.analysis.api.entity;


import com.chinatower.analysis.api.entity.dto.PointRankDto;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class StatisticsExportParam {
    //租户告警导出
    private Map eventSheet1;
    private List<Map> eventSheet2;
    private List<Map> eventSheet22;
    private List<Map> eventSheet3;
    private List<Map> eventSheet4;
    private List<Map> eventSheet44;
    private List<Map> eventSheet5;
    private List<Map> eventSheet55;
    private List<Map> eventSheet6;
    private List<Map> eventSheet66;
    private List<Map> eventSheet7;
    private Map eventSheet8;
    private List<Map> eventSheet9;
    private List<Map> eventSheet10;
    private List<Map> eventSheet11;
    private List<Map> eventSheet12;
    private List<Map> eventSheet13;
    private List<Map> eventSheet14;
    private List<Map> eventSheet15;
    private List<Map> eventSheet16;

    /**
     * 方案 G V2.0：下游任务排行（statDimension=SUB_TASK）
     */
    private List<Map> eventSheetSubScene;

    /**
     * 方案 G V2.0：下游任务准确率排行（statDimension=SUB_TASK）
     */
    private List<Map> eventSheetSubSceneAccuracy;

    //铁塔告警导出
    private Map eventTTSheet1;
    private List<Map> eventTTSheet2;
    private List<Map> eventTTSheet22;
    private List<Map> eventTTSheet3;
    private List<Map> eventTTSheet4;
    private List<Map> eventTTSheet44;
    private List<Map> eventTTSheet5;
    private List<Map> eventTTSheet55;
    private List<Map> eventTTSheet6;
    private List<Map> eventTTSheet66;
    private List<Map> eventTTSheet7;
    private List<Map> eventTTSheet8;
    private List<Map> eventTTSheet88;
    private Map eventTTSheet9;
    private List<Map> eventTTSheet10;
    private List<Map> eventTTSheet11;
    private List<Map> eventTTSheet12;
    private List<Map> eventTTSheet13;
    private List<Map> eventTTSheet14;
    private List<Map> eventTTSheet15;
    private List<Map> eventTTSheet16;

    //租户设备导出
    private DeviceStatisticsVO deviceSheet1;
    private List<Map> deviceSheet2;
    private List<Map> deviceSheet3;
    private List<Map<String, String>> deviceSheet4;
    private IotDeviceStatisticsVo deviceSheet5;
    private List<Map> deviceSheet6;
    private UavDeviceStatisticsVo deviceSheet7;
    private List<Map> deviceSheet8;
    private RadarDeviceStatisticsVo deviceSheet9;
    private List<Map> deviceSheet10;
    private HornDeviceStatisticsVo deviceSheet11;
    private List<Map> deviceSheet12;
    private DeviceStatisticsVO deviceSheet13;
    private List<Map> deviceSheet14;

    //铁塔设备导出
    private DeviceStatisticsVO deviceTTSheet1;
    private List<Map> deviceTTSheet2;
    private List<PointRankDto> deviceTTSheet3;
    private List<Map> deviceTTSheet4;
    private List<Map<String, String>> deviceTTSheet5;
    private List<Map<String, String>> deviceTTSheet6;
    private List<Map> deviceTTSheet7;
    private List<Map> deviceTTSheet8;
    private IotDeviceStatisticsVo deviceTTSheet9;
    private List<Map> deviceTTSheet10;
    private List<Map> deviceTTSheet11;
    private List<Map> deviceTTSheet12;
    private UavDeviceStatisticsVo deviceTTSheet13;
    private List<Map> deviceTTSheet14;
    private List<Map> deviceTTSheet15;
    private List<Map> deviceTTSheet16;
    private RadarDeviceStatisticsVo deviceTTSheet17;
    private List<Map> deviceTTSheet18;
    private List<Map> deviceTTSheet19;
    private List<Map> deviceTTSheet20;
    private HornDeviceStatisticsVo deviceTTSheet21;
    private List<Map> deviceTTSheet22;
    private List<Map> deviceTTSheet23;
    private List<Map> deviceTTSheet24;
    private DeviceStatisticsVO deviceTTSheet25;
    private List<Map> deviceTTSheet26;
    private List<Map> deviceTTSheet27;
    private List<Map> deviceTTSheet28;


    //租户用户导出
    private Map userSheet1;
    private List<Map> userSheet2;
    private List<Map> userSheet3;
    private List<Map> userSheet33;
    private List<Map> userSheet333;
    private List<Map> userSheet4;
    private List<Map> userSheet5;
    private List<Map> userSheet6;
    private List<Map> userSheet7;
    private List<Map> userSheet8;
    private List<Map> userSheet9;
    private List<Map> userSheet10;
    private List<Map> userSheet11;
    private List<Map> userSheet12;
    private List<Map> userSheet13;
    private List<Map> userSheet14;
    private List<Map> userSheet15;

    //铁塔用户导出
    private Map userTTSheet1;
    private List<Map> userTTSheet2;
    private List<Map> userTTSheet3;
    private List<Map> userTTSheet4;
    private List<Map> userTTSheet44;
    private List<Map> userTTSheet444;
    private List<Map> userTTSheet5;
    private List<Map> userTTSheet6;
    private List<Map> userTTSheet7;
    private List<Map> userTTSheet8;
    private List<Map> userTTSheet9;
    private List<Map> userTTSheet10;
    private List<Map> userTTSheet11;
    private List<Map> userTTSheet12;
    private List<Map> userTTSheet13;
    private List<Map> userTTSheet14;
    private List<Map> userTTSheet15;
    private List<Map> userTTSheet16;
    private List<Map> userTTSheet17;

    //租户巡查导出
    private Map inspectSheet1;
    private List<Map> inspectSheet2;
    private Map inspectSheet3;
    private Map inspectSheet4;
    private List<Map> inspectSheet5;
    private List<Map> inspectSheet6;
    private List<Map> inspectSheet7;
    private List<Map> inspectSheet8;
    private List<Map> inspectSheet9;
    private List<Map> inspectSheet10;
    private List<Map> inspectSheet11;
    private List<Map> inspectSheet12;
    private List<Map> inspectSheet13;
    private List<Map> inspectSheet14;
    private List<Map> inspectSheet15;
    private List<Map> inspectSheet16;
}
