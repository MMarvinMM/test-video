package com.chinatower.platform.modules;


import com.chinatower.event.entity.vo.HandleRecordVO;
import com.chinatower.event.entity.vo.IotDeviceInfoVO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: lyy
 * @Date: 2024/03/18 17:27
 * @version 1.6.40
 */

@Data
@ToString
public class ExtendFieldVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String defaultField;

    //转义PTZ
    private String ptzTrans;

    /**
     * 网格边框颜色
     */
    private String borderColor;

    /**
     * 网格边框颜色透明度 单位% 取值0-1 默认0.5
     */
    private String borderColorTransparency;

    /**
     * 网格边框线条粗细 取值1 2 3 4 5 默认1
     */
    private String borderColorLineThickness;

    /**
     * 网格填充颜色
     */
    private String color;

    /**
     * 网格填充颜色透明度 单位% 取值0-1 默认0.5
     */
    private String colorTransparency;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 督办详情
     */
    private List<HandleRecordVO> handleRecordVOS;

    /**
     * 多组物联设备信息以及关联的摄像机信息
     */
    private List<IotDeviceInfoVO> iotDevInfoList;

    /**
     * 无人机设备信息
     */
    private transient List<Map<String, Object>> uavDevInfoList;

    /**
     * 研判人账号
     * */
    private String checkManUserName;

    /**
     * 摄像机标签
     * */
    private List<String> deviceLabel;

    /**
     * 摄像机类型
     */
    private String deviceType;

    /**
     * 摄像机类型名称
     */
    private String deviceTypeName;

    /**
     * 摄像机子类型
     */
    private String deviceSubType;

    /**
     * 摄像机子类型名称
     */
    private String deviceSubTypeName;

    /**
     * 通用新增储存备用的透传字段
     */
    private String transparentFields;
}
