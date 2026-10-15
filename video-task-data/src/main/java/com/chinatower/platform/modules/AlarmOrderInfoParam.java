package com.chinatower.platform.modules;


import com.chinatower.event.api.model.DeviceInfoEntity;
import com.chinatower.event.entity.dto.OrderFileDO;
import com.chinatower.event.entity.vo.UavNestDeviceVo;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class AlarmOrderInfoParam {

    //用户信息
    private Map<String, String> userInfoMap;

    //用户信息
    private Map<String, String> userInfoMap2;

    //用户信息
    private Map<String, String> userMap;

    //行业信息
    private Map<String, String> industryConfMap;

    //预置位信息
    private Map<String, String> presetInfoMap;

    //告警来源信息
    private Map<String, String> warningSourceMap;

    //第三方告警来源信息
    private Map<String, String> thirdWarningSourceMap;

    //告警类型信息
    private Map<Object, Object> alarmTypeMap;

    //告警状态信息
    private Map<String, String> orderStatusMap;

    //重要等级信息
    private Map<String, String> importanceLevelMap;

    //事件等级信息
    private Map<String, String> emergencyLevelMap;

    //消息类型信息
    private Map<String, String> msgTypeMap;

    //通知类型信息
    private Map<String, String> noticeResourceMap;

    //告警压制原因
    private Map<String, String> pressiongReasonMap;

    //网格信息
    private Map<String, Map<String, Object>> gridInfoMap;

    //设备信息
    private Map<String, DeviceInfoEntity> deviceInfoMap;

    //物联设备信息
    private Map<String, DeviceInfoEntity> iotDeviceInfoMap;

    //物联设备关联摄像机信息
    private Map<String, List<DeviceInfoEntity>> iotDeviceConfMap;

    //雷达设备信息
    private Map<String, DeviceInfoEntity> radarDeviceInfoMap;

    //图层图斑信息
    private Map<String, List<Map>> layerInfoMap;

    //节点信息
    private Map<String, String> platformDataMap;

    //告警流程配置信息
    private Map<String, String> alarmFlowConfigMap;

    //研判、调度、核实过程的附件信息
    private Map<String, List<OrderFileDO>> orderFileAllMap;

    //处置过程的附件信息
    private Map<String, List<OrderFileDO>> processFileAllMap;

    //获取研判、调度、核实环节的水印附件
    private Map<String, List<OrderFileDO>> orderFileMap;

    //处置环节的水印附件
    private Map<String, List<Map>> handleFileMap;

    //联动算法信息
    private Map<String, String> algorithmNameAllMap;

    //无人机设备信息
    private Map<String, List<DeviceInfoEntity>> uavDeviceMap;

    //无人机关联机场信息
    private Map<String, List<UavNestDeviceVo>> uavDeviceNestMap;

    //摄像机标签信息
    private Map<String, List<String>> deviceLabelMap;

    //执行定时任务时传的时间入参：2023-11-30
    private String dateParam;
}
