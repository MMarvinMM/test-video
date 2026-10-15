package com.chinatower.video.modules.query.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VmAlarmAreaSuppressVO {


    /**
     * area_id
     */
    private Integer areaId;
    /**
     * 告警压制策略ID
     */
    private Integer alarmSuppressId;
    /**
     * 摄像机code
     */
    private String deviceCode;
    /**
     * 摄像机名称
     */
    private String devName;
    /**
     * 通道code
     */
    private String channelCode;
    /**
     * 屏蔽区域名称
     */
    private String areaName;
    /**
     * 重叠范围比0-100
     */
    private String overlapRate;
    /**
     * 区域屏蔽类型：1、实绘区域  2、PT值范围
     */
    private String areaType;
    /**
     * 点位json
     */
    private String points;

    /**
     * PT标绘json
     */
    private String pointsPt;
    /**
     * p值 设备
     */
    private String pointsP;
    /**
     * t值 设备
     */
    private String pointsT;
    /**
     * z值 设备
     */
    private String pointsZ;
    /**
     * p最小值
     */
    private String pMin;
    /**
     * p最大值
     */
    private String pMax;
    /**
     * t最小值
     */
    private String tMin;
    /**
     * t最大值
     */
    private String tMax;
    /**
     * 屏蔽区域图路径
     */
    private String screenshot;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     * */
    private String tenantName;

    /**
     * 创建人
     */
    private String createrBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 最后更新时间
     */
    private Date updateTime;
    /**
     * 删除标识 0删除 1正常
     */
    private String deleteFlag;


    private List<Integer> strategyIdList;

    private String startDate;

    private String endDate;

    /** 创建时间排序 */
    private String createDateOrder;
    /** 修改时间排序 */
    private String updateDateOrder;

    private String orderByString;

    private String industryCode;

    /**
     * 添加 修改标识 add/update
     * */
    private String remark;

    /**
     * 根据点位及摄像机ptz所计算出的经纬度
     */
    private String positionInformation;
}
