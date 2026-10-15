package com.chinatower.receive.vo.entity;

import com.chinatower.common.core.dto.BaseModel;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @className: VmAlarmSuppressStrategyVo
 * @description: 告警压制策略 bean
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@Data
public class VmAlarmSuppressStrategyVO extends BaseModel {

    /**
     * id
     */
    private Integer id;
    /**
     * 告警压制策略名称
     */
    private String alarmSuppressName;
    /**
     * 设备压制类型：1、摄像机 2、告警类型
     */
    private String alarmSuppressType;
    /**
     * AI告警来源：22、设备告警 21、设备告警二次研判为真 20、设备告警二次研判为假 10、算法告警(noticeResource+msgType)
     */
    private String alarmSource;
    /**
     * 是否进行二次研判1、是  0、否
     */
    private String alarmIfJudge;
    /**
     * 二次研判结果：1、真  0、假
     */
    private String alarmJudgeResult;
    /**
     * 天气状况
     */
    private String weatherConditions;
    /**
     * 基础信息时间段
     */
    private String periodTime;
    /**
     * 告警压制时间段
     */
    private String suppressPeriodTime;
    /**
     * 告警压制开始时间
     */
    private Date suppressStartTime;
    /**
     * 告警压制结束时间
     */
    private Date suppressEndTime;
    /**
     * 置信度阈值
     */
    private Float suppressThresh;
    /**
     * 重复告警间隔
     */
    private Integer alarmInterval;
    /**
     * 重复告警间隔单位:秒s,分M,时h,天d
     */
    private String alarmIntervalType;
    /**
     * 重复告警范围P
     */
    private String alarmRangeP;
    /**
     * 重复告警范围T
     */
    private String alarmRangeT;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 客户名称
     */
    private String tenantName;
    /**
     * 车流量（辆）
     */
    private Integer trafficFlow;
    /**
     * 人流量（人）
     */
    private Integer peopleFlow;
    /**
     * 人员密度（人）
     */
    private Integer peopleDensity;
    /**
     * 状态 0禁用 1启用
     */
    private String status;
    /**
     * 创建人
     */
    private String createrBy;

    /**
     * 删除标识 0删除 1正常
     */
    private Integer deleteFlag;
    /**
     * 设备压制列表
     */
    private List<VmAlarmDeviceSuppressVO> deviceSuppressVoList;
    /**
     * 告警类型压制列表
     */
    private List<VmAlarmTypeSuppressVO> typeSuppressVoList;
    /**
     * 区域压制列表
     */
    private List<VmAlarmAreaSuppressVO> areaSuppressVoList;

    /**
     * 无人机告警区域压制列表
     */
    private List<VmAlarmUavAreaSuppressVO> uavAreaSuppressVoList;

    /**
     * 雨天压制配置
     */
    private Map<String, AlarmSuppressConfigDTO> rainSuppressVoList;

    private String source; //操作类型：0-铁塔，1-租户，2-铁塔视角下查看租户
    private List<Integer> strategyIdList;

    private String startDate;

    private String endDate;

    /**
     * 修改时间 查询字段
     */
    private String updateTimeStartDate;

    private String updateTimeEndDate;

    private String orderByString;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业应用版本
     */
    private String appVerCode;

    /**
     * 行业应用版本名称
     */
    private String industryName;

    /** 创建时间排序 */
    private String createDateOrder;
    /** 修改时间排序 */
    private String updateDateOrder;
    /** sql排序 */
    private String orderSql;

    /**
     * 告警空间范围
     */
    private String alarmSpaceRange;

    /**
     * 告警范围关系（0：或者  1：并且）
     */
    private String alarmRangeStatus;

    private String localNode;

    /**
     * 设备编码
     * */
    private String deviceCode;

    /**
     * 设备编码集合
     * */
    private List<String> deviceCodeList;

    /**
     * 告警类型集合
     * */
    private List<String> typeList;

    /**
     * 配置类型
     */
    private String configType;

    /**
     * 开始识别时间
     */
    private String beginRecognizeTime;

    /**
     * 开始识别单位
     */
    private String beginRecognizeTimeType;

    /**
     * 结束识别时间
     */
    private String endRecognizeTime;

    /**
     * 结束识别单位
     */
    private String endRecognizeTimeType;
}
