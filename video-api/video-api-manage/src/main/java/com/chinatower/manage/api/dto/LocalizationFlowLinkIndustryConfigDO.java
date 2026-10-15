package com.chinatower.manage.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 流程环节属性表
 * </p>
 *
 */
@Data
public class LocalizationFlowLinkIndustryConfigDO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 当前环节Id
     * @required true
     */
    private Long linkId;

    /**
     * 流程环节名称
     * @required true
     */
    private String linkName;

    /**
     * 默认关联角色ID，多个角色中间用逗号隔开
     * @required true
     */
    private String roleId;

    /**
     * 消息通知类型，1：APP,2：短信,3：PC系统消息,4：PC告警声音，多种方式中间用逗号隔开
     * @required true
     */
    private String msgNotifyType;

    /**
     * 短信通知间隔 单位分钟
     * @required false
     */
    private Integer msgNotifyLimit;

    /**
     * 短信通知间隔 单位 1：分钟，2：小时
     * @required false
     */
    private String msgNotifyCompany;

    /**
     * 转领导批示，1：是，0：否 、
     * @required false
     */
    private String leaderFlag;

    /**
     * 会签标识，0，不会签，1会签
     * @required false
     */
    private String signFlag;

    /**
     * 状态：1生效 0失效
     * @required false
     */
    private String status;
    /** 大屏环节URL */
    private String screenUrl;
    /** PC环节URL */
    private String pcUrl;
    /** app环节URL */
    private String appUrl;

    /**
     * 属性：0为默认属性，1为算法配置属性
     * @required false
     */
    private String attrFlag;

    /**
     * 回退标识 0-不回退，1-可回退
     * @required false
     */
    private String rollbackFlag;

    /**
     * 时限，单位分钟
     * @required false
     */
    private Integer timeLimit;

    /**
     * 是否通知管理员，0，不通知，1通知
     * @required false
     */
    private String notifyAdminFlag;

    /**
     * 网格员-用户类型，1用户,2角色
     * @required false
     */
    private String	gridUserType;

    /**
     * 是否过滤数据权限,0否,1是
     * @required false
     */
    private String	dataFilterPermiss;

    /**
     * 是否显示客户管理员,0否,1是
     * @required false
     */
    private String	customAdmin;

    /**
     * 环节处理时限单位, 0: 秒，1：分钟，2：小时，3：天
     * @required false
     */
    private String	timeLimitCompany;

    /**
     * 告警声音提醒开始时段
     * @required false
     */
    private String	timeStartAlram;

    /**
     * 告警声音提醒结束时段
     * @required false
     */
    private String	timeEndAlram;

    /**
     * 告警提示音
     * @required false
     */
    private String	voiceAlram;

    /**
     * 提醒时长类型，0： 手动关闭、1：自动停止
     * @required false
     */
    private String	timeRemind;

    /**
     * 提醒时长自动停止单位 0: 秒，1：分钟
     * @required false
     */
    private String	timeRemindAuto;

    /**
     * 提醒时长自动停止
     * @required false
     *
     */
    private String	timeRemindNumber;

    /**
     * 稍后提醒0否,1是
     * @required false
     */
    private String	ifLaterRemind;

    /**
     * 稍后提醒
     * @required false
     */
    private String	laterRemind;

    /**
     * 稍后提醒单位 0: 秒，1：分钟，2：小时
     * @required false
     */
    private String	laterRemindCompany;

    /**
     * 短信推送时效 实时推送0 定时推送1
     * @required true
     */
    private String	timePushType;

    /**
     * 短信通知上限
     * @required false
     */
    private String	timeLimitPush;

    /**
     * 网格员角色
     * @required false
     */
    private String	gridSignRole;

    /**
     * 用户类型,2角色,3 网格员
     * @required false
     */
    private String	userType;

    /**
     * 短信通知时间区间
     * @required false
     */
    private String msgSendTimeInterval;

    /**
     * 属地版本
     */
    private String localVersion;
}
