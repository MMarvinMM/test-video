package com.chinatower.event.entity.vo;

import com.chinatower.event.entity.dto.AlarmLableRelAPIDTO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.model.dto.BaseWarningOrderDTO;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：告警工单列表VO对象实体
 * <p>
 * 类名称: WarningOrderListVo.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/24 11:26
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class WarningOrderListVO extends BaseWarningOrderDTO {
    /**
     * 下个环节是否多个标记为（0：不是  1：是）
     * @required false
     */
    private String moreNextFlag;

    /**
     * 研判说明
     * @required false
     */
    private String checkRemark;

    /**
     * 告警标签集合
     * @required false
     */
    private List<AlarmLableRelAPIDTO> alarmLableRelDTOList;

    /**
     * 受理状态
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 受理时限
     * @required false
     */
    private String timeLimitAcceptance;

    /**
     * P位置
     * @required false
     */
    private String ptzP;

    /**
     * T位置
     * @required false
     */
    private String ptzT;

    /**
     * Z位置
     * @required false
     */
    private String ptzZ;

    /**
     * 预置位名称ID
     * @required false
     */
    private String presetId;

    /**
     * 摄像机厂家
     * @required false
     */
    private String modelId;

    /**
     * 焦距范围
     * @required false
     */
    private String focusRange;

    /**
     * 水平向下为负 0否,1是
     * @required false
     */
    private String vertiDownNeg;

    /**
     * 专题图
     * @required false
     */
    private String relativePathThem;

    /**
     * 红外图
     * @required false
     */
    private String relativePathMir;

    /**
     * 用户距事发点距离
     * @required false
     */
    private String distance;

    /**
     * 用户距离事发地距离
     * @required false
     */
    private String userDistance;

    /**
     * 当前环节停留时长
     * @required false
     */
    private String waitTime;

    /**
     * 处理人
     * @required false
     */
    private List<UserInfoDto> processUserList;

    /**
     * 用户id流转记录，用','拼接
     * @required false
     */
    private String userRecord;

    /**
     * 删除用户
     * @required false
     */
    private String deleteUser;

    /**
     * 删除用户信息
     * @required false
     */
    private SysUser deleteUserInfo;

    /** 监控任务唯一标识 */
    private String taskId;

    /** 算法编码 */
    private String algorithmCode;

}
