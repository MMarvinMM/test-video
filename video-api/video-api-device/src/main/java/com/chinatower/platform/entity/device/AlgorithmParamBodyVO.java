package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述
 * 接口报文-报文主体
 * @return
 * @author chenmf
 * @date 2022/2/9
 * @version
 */
@Data
public class AlgorithmParamBodyVO implements Serializable {
    
    //1-按通道；2-按预置位
    public static final int POSTYPE_CHANNEL = 1;
    public static final int POSTYPE_PRESET = 2;
    
    public static final int ZERO = 0;
    public static final int NEW_OPTTYPE = 1;
    public static final int UPDATE_OPTTYPE = 2;
    public static final int DELETE_OPTTYPE = 3;
    public static final int TIME = 180;
    public static final int MTIME = 60;
    public static final int HTIME = 3600;
    public static final int DTIME = 86400;
    public static final String SUCCESS = "0000";
    public static final String DATA = "data";
    public static final String RETURNCODE = "returnCode";
    public static final String ERRORMSG = "errorMsg";

    /** 设备名称 */
    public String deviceName;
    /** 通道名称 */
    public String channelName;
    /** 1-新增；2-修改；3-停用； */
    public int optType;
    /** 监控区域 */
    public List<AlgorithmMonitorAreaVO> monitorArea;
    /** 设备编码 */
    public String deviceCode;
    /** 通道编码 */
    public String channelCode;
    /** 算法编码 */
    public String algorithmCode;
    /** 预置位 */
    public transient Object preset;
    /** 任务编码 */
    public String taskId;
    /** 1-按通道；2-按预置位 */
    public int extractionType;
    /** 置信度阈值 */
    public Float thresh;
    /** 重复告警间隔 */
    public Integer alarmInterval;
    /** 重复告警间隔单位 */
    public String alarmIntervalType;
    /** 算法运行时间 */
    public transient List<String> algorithmTime;
    /** 个性化参数 */
    public transient Object modelConfig;
    /** 铁塔视角算法状态：1:启用成功 2:启用失败 3:处理中 */
    public String algorithmStatus;
    /** 算法操作 1.新增2.启用3.停用 */
    public Integer operate;
    /** 租户名称 */
    public String tenantName;
    /** 租客信息 */
    public String tenantCode;
    /** 租户编码 */
    public String tenantId;
}