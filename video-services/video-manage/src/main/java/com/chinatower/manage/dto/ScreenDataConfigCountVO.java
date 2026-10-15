package com.chinatower.manage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: 大屏数据配置数量
 * @Author: wangyt
 * @CreateTime: 2025-01-06  09:54
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ScreenDataConfigCountVO {

    private Long id;
    // 大屏id
    private String screenId;
    // 大屏类型 1-旧大屏 2-新大屏
    private String screenType;
    // 是否自定义
    private String isCustomize;
    // 摄像机数量
    private int cameraNum;
    // 物联设备数量
    private int iotNum;
    // 雷达数量
    private int radarNum;
    // 喇叭数量
    private int speakerNum;
    // 无人机数量
    private int uavNum;
    // 执法记录仪数量
    private int recorderNum;
    // 网格数量
    private int gridNum;
    // 网格员数量
    private int gridKeeperNum;
    // 资源数量
    private int resourcesNum;
    // 告警类型数量
    private int alarmTypeNum;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone="GMT+8")
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone="GMT+8")
    private Date updateTime;
    private String deleteFlag;
    private String tenantId;
    private String industryCode;
    private String appVerCode;
    private String screenName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTimeStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTimeEnd;
    private String sortName;
    private String sort;
    private Integer pageNum;
    private Integer pageSize;

}
