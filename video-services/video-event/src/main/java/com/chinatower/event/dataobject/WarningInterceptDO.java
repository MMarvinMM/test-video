package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 告警拦截信息表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
@Getter
@Setter
@TableName("vm_warning_intercept_t")
public class WarningInterceptDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 主键
     */
    private String warningOrderId;

    /**
     * 第三方任务ID
     */
    private String taskId;

    /**
     * 告警类型编码
     */
    private String warningTypeId;

    /**
     * 形成告警时间
     */
    private Date alarmTime;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 所在省名称
     */
    private String provinceName;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 所在市名称
     */
    private String cityName;

    /**
     * 县编码
     */
    private String countyCode;

    /**
     * 县名称
     */
    private String countyName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 告警内容
     */
    private String alarmBody;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * P位置
     */
    private String ptzP;

    /**
     * T位置
     */
    private String ptzT;

    /**
     * Z位置
     */
    private String ptzZ;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 事件等级，关联枚举值表
     */
    private String emergencyLevel;

    /**
     * 客户编号
     */
    private String customerCode;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 网格编码
     */
    private String gridCode;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 视频路径
     */
    private String videoUrl;

    /**
     * 原图
     */
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    private String fileImgUrlIcon;

    /**
     * 研判结果 1：真实
     */
    private String judgeResult;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date modifierDate;

    /**
     * 0正常 1删除
     */
    private String deleteFlag;

    /**
     * 告警重要等级 1:1级,2:2级
     */
    private String importanceLevel;

    /**
     * 拦截原因：1.置信度阈值，2.告警间隔，3.告警运行时间，4研判压制
     */
    private String intercept;

    /**
     * 上一次告警时间
     */
    @TableField(value = "prevAlarmTime")
    private Date prevAlarmTime;

    /**
     * 本次告警置信度阈值
     */
    private String thisThresh;

    /**
     * 算法配置置信度阈值
     */
    private String thresh;

    /**
     * 算法配置重复告警间隔
     */
    @TableField(value = "alarmInterval")
    private Integer alarmInterval;

    /**
     * 算法配置重复告警间隔单位:秒s,分M,时h,天d
     */
    private String alarmintervalType;

    /**
     * 算法配置算法运行时间
     */
    private String algorithmTime;


}
