package com.chinatower.manage.api.dto;

import com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: 大屏数据配置数据
 * @Author: wangyt
 * @CreateTime: 2025-01-06  10:03
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreenDataConfigVO {
    private Long id;
    // 大屏配置id
    private Long screenDataConfigId;
    // 大屏配置id数据类型0-摄像机1-物联设备2-雷达3-喇叭4-无人机5-执法记录仪6-网格7-网格员8-资源9-告警类型
    private String dataType;
    // 数据编码
    private String dataCode;
    // 告警来源
    private String dataSource;
    private String createBy;
    private String createTime;
    private String updateBy;
    private String updateTime;
    private String deleteFlag;
    private String tenantId;
    private String industryCode;
    private String appVerCode;
    // 大屏id
    private String screenId;
    // 大屏类型 1-旧大屏 2-新大屏
    private String screenType;
    // 是否自定义
    private String isCustomize;
    // 摄像机编码集合
    private List<String> cameraCodeList;
    // 物联设备编码集合
    private List<String> iotCodeList;
    // 雷达编码集合
    private List<String> radarCodeList;
    // 喇叭编码集合
    private List<String> speakerCodeList;
    // 无人机编码集合
    private List<String> uavCodeList;
    // 执法记录仪编码集合
    private List<String> recorderCodeList;
    // 网格编码集合
    private List<String> gridCodeList;
    // 网格员编码集合
    private List<String> gridKeeperCodeList;
    // 资源编码集合
    private List<String> resourcesCodeList;
    // 告警类型集合
    private List<ScreenDataConfigParticipationVO> alarmTypeList;
    // 摄像机集合
    private List<ScreenDataConfigParticipationVO> cameraList;
    // 物联设备集合
    private List<ScreenDataConfigParticipationVO> iotList;
    // 雷达集合
    private List<ScreenDataConfigParticipationVO> radarList;
    // 喇叭集合
    private List<ScreenDataConfigParticipationVO> speakerList;
    // 无人机集合
    private List<ScreenDataConfigParticipationVO> uavList;
    // 执法记录仪集合
    private List<ScreenDataConfigParticipationVO> recorderList;
    // 网格集合
    private List<ScreenDataConfigParticipationVO> gridList;
    // 网格员集合
    private List<ScreenDataConfigParticipationVO> gridKeeperList;
    // 资源集合
    private List<ScreenDataConfigParticipationVO> resourcesList;

}
