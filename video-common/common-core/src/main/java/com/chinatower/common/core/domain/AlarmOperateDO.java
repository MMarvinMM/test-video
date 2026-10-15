package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 运营研判表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
@Getter
@Setter
public class AlarmOperateDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String warningOrderId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 第三方任务ID
     */
    private String taskId;

    /**
     * 告警内容
     */
    private String alarmBody;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 告警类型编码
     */
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    private String warningTypeName;

    /**
     * 实际触发的下游任务编码数组（JSON，与月表 sub_scenes 语义一致）
     */
    private String subScenes;

    /**
     * 形成告警时间
     */
    private Date alarmTime;

    /**
     * 所在省份编码
     */
    private String provinceId;

    /**
     * 所在省份名称
     */
    private String provinceName;

    /**
     * 所在市编码
     */
    private String cityId;

    /**
     * 所在市名称
     */
    private String cityName;

    /**
     * 所在区编码
     */
    private String countyId;

    /**
     * 所在区名称
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
     * 告警图片url
     */
    private String imgUrl;

    /**
     * 告警创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date modifierTime;

    /**
     * 0正常 1删除
     */
    private String deleteFlag;

    /**
     * P位置
     */
    private Integer ptzP;

    /**
     * T位置
     */
    private Integer ptzT;

    /**
     * Z位置
     */
    private Integer ptzZ;

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
     * 事件等级名称
     */
    private String emergencyLevelName;

    /**
     * 重复报警事件ID
     */
    private String duplicateOrderId;

    /**
     * 虚假告警说明
     */
    private String falseAlarmRemark;

    /**
     * 研判人ID
     */
    private String createUser;

    /**
     * 研判人名称
     */
    private String createUserName;

    /**
     * 研判结果 0：误报 1：真实告警 2：重复报警 3：待研判
     */
    private String judgeResult;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 视频路径
     */
    private String videoUrl;

    /**
     * 框图
     */
    private String fileImgUrlIcon;

    /**
     * 告警重要等级 1:1级,2:2级
     */
    private String importanceLevel;


}
