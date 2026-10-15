package com.chinatower.event.entity.dto;

import com.chinatower.event.entity.vo.OrderInfoVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmOperateDTO.java
 *
 * @author lyy
 * @version v1.0.0
 * @date 2022/12/09 3:26
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmOperateDTO extends OrderInfoVO implements Serializable {

    private boolean isQuery;

    /**
     * 告警状态集合
     * @required false
     */
    private List<String> orderStatusList;
    /**
     * 开始时间
     * @required true
     */
    private String alarmTimeStart;
    /**
     * 结束时间
     * @required true
     */
    private String alarmTimeEnd;

    /**
     * 租户ID集合
     * @required false
     */
    private List<String> tenantList;
    private String customer;
    private String keyTexts;

    /**
     * 告警工单ID集合
     * @required false
     */
    private List<String> warningOrderIds;

    /**
     * 设备编码
     * @required false
     */
    private List<String> deviceCodes;

    /**
     * 告警类型集合
     * @required false
     */
    private List<String> warningTypeIdList;

    /**
     * 更新时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String modifierTime;

    /**
     * 完成时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String finishTime;

    /**
     * 研判人ID
     * @required false
     */
    private String createUser;

    /**
     * 研判人名称
     * @required false
     */
    private String createUserName;

    /**
     * 运营研判标识：1:串行 2:并行
     * @required true
     */
    private String operateType;

    private List<String> judgeUserList;

    /**
     * 原图
     */
    private String imgUrl;

    /**
     * 来源：0铁塔 1租户
     * 行业版本编码
     */
    private String source;

    private String custUser;

    /**
     * 用户ID
     * @required true
     */
    private String userId;

    /**
     * 组织ID
     * @required false
     */
    private List<String> orgIdList;

    /**
     * 组织ID拼接字符串
     * @required false
     */
    private String orgIdStr;

    /**
     * 行业版本编码
     * @required false
     */
    private String tradeVerCode;

    /**
     * 虚拟研判状态 (1:虚拟研判 0:取消虚拟研判)
     */
    private String virtualJudgeStatus;

    /**
     * 虚拟研判处理人
     */
    private String virtualJudgeProcess;

    /**
     * 虚拟研判开始时间
     */
    private String virtualJudgeTimeStart;

    /**
     * 虚拟研判结束时间
     */
    private String virtualJudgeTimeEnd;

    /**
     * 虚拟研判告警id
     */
    private List<String> virtualJudgeAlarmIdList;
}
