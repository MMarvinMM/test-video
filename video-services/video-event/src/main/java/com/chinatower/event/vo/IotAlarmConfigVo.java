package com.chinatower.event.vo;


import com.chinatower.event.dataobject.IotAlarmConfigGroupsDo;
import com.chinatower.event.dataobject.IotAlarmDeviceDo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IotAlarmConfigVo implements Serializable {

    /**
     * 主键
     */
    private String id;

    /**
     * 物联告警配置名称
     */
    private String iotAlarmConfigName;

    /**
     * 配置方式
     */
    private String configMethod;

    /**
     * 启用状态
     */
    private String configStatus;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 删除标记
     */
    private Integer deleteFlag;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 应用版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 物联详情配置
     */
    private List<IotAlarmConfigGroupsDo> iotAlarmConfigGroupsList;

    /**物联告警类型*/
    private String iotAlarmType;


    /**告警开始时间*/
    private String receiveStartTime;

    /**告警结束时间*/
    private String receiveEndTime;

    /**告警时间段*/
    private String receivePeriodTime;

    /**物联因子关系 0:且  1:或*/
    private String relation;


    /**物联因子 0:检测值  1:累加值*/
    private String valueType;

    /**累加值*/
    private String sumValue;

    /**累加值单位 天时分*/
    private String sumValueUnit;

    /**修改标识 0：覆盖*/
    private String updateRemark;

    //告警类型名称
    private String iotAlarmTypeName;

    /**告警等级 字典配置*/
    private String alarmLevel;

    /**告警压制时间*/
    private String interceptTime;

    /**告警压制时间单位  时分秒*/
    private String interceptTimeUnit;

    /**告警生效设备组合*/
    private List<String> iotDeviceGroups;
}
