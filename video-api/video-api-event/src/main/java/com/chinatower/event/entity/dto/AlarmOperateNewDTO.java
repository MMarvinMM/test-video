package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BasePageDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * <p>
 * 类名称: AlarmOperateDTO.java
 *
 * @date 2022/3/12 16:28
 */
@Data
public class AlarmOperateNewDTO extends BasePageDTO implements Serializable {

    private String tenantId;
    private boolean isQuery;
    private String orderStatus;
    private List<String> orderStatusList;
    private String provinceId;
    private String cityId;
    private String countyId;
    private String alarmTimeStart;
    private String alarmTimeEnd;
    private String alarmBody;
    private String orderId;
    private String warningTypeId;
    private String emergencyLevel;
    private String judgeResult;
    private List<String> tenantList;
    private String customer;
    private String keyTexts;
    private String operateType;
    private String myOperateType;
    private List<String> deviceCode;

    private String warningOrderId;
    private String resultTab;

    private String operationNode;

    private String localNode;

    private String localDept;

    private String operationNodeName;

    /**
     * 乡镇
     */
    private String townName;
    /**
     *所属网格
     */
    private String gridName;
    /**
     * 预置位ID
     */
    private String presetId;

    /**
     * 告警工单ID集合
     */
    private List<String> warningOrderIds;

    /**
     * AI告警来源
     * 通知类型 1-调度通知；2-设备端通知；（热成像告警、雷达平台按2方式通知）；算法需要将此字段透传回算法仓.
     */
    private String noticeResource;

    /**
     * 摄像机厂商ID
     */
    private String modelId;

    /**
     * 摄像机厂商ID
     */
    private List<String> modelIds;

    /**
     * 摄像机名称
     */
    private String deviceName;

    /**
     * 设备编码
     */
    private List<String> deviceCodes;

    /**
     * 置信度
     */
    private String confidenceLevel;

    /**
     * 告警类型
     */
    private List<String> warningTypeIdList;

    /**
     * 告警来源
     */
    private String warningSource;

    /**
     * 是否二次研判
     */
    private String isSecondJudge;

    /**
     * 二次研判结果
     */
    private String secondJudgeResult;

    /**
     * 是否二次识别 1-是；0-否
     */
    private String isSecondIdentify;

    /**
     * 二次识别结果 1-真，0-假
     */
    private String secondIdentifyResult;

    /**
     * 关键字检索：APP物联设备编号/名称
     */
    private String iotDeviceCodeOrName;

    /**
     * 物联设备编码/名称
     */
    private List<String> iotDeviceCode;

    /**
     * 处理人
     */
    private List<String> judgeUserList;

    /**
     * 来源：0铁塔 1租户
     */
    private String source;

    private String custUser;

    private String userId;

    /**
     * 组织ID
     */
    private List<String> orgIdList;

    /**
     * 组织ID拼接字符串
     */
    private String orgIdStr;

    /**
     * 组织ID
     */
    private List<String> orgIdStrList;
    /**
     * 告警等级：一级、二级、三级、四级
     *
     */
    private String importanceLevel;
    /**
     *  置信度最小值
     */
    private String confidenceLevelMin;
    /**
     *  置信度最大值
     */
    private String confidenceLevelMax;
    /**
     *  置信度最小值符号
     */
    private String minChar;
    /**
     *  置信度最大值符号
     */
    private String maxChar;

    /**
     * 算法编码集合
     */
    private List<String> algorithmCodeList;

    /**
     * 用户经度
     */
    private String userLongitude;
    /**
     * 用户维度
     */
    private String userLatitude;

    /**
     * 排序方式（1：事发点距离 2：告警时间）
     */
    private String sortFlag;

    /**
     * 是否3D放大（0-否，1-是）
     */
    private String enlargeFlag;

    /**
     * 行业应用名称
     */
    private String industryName;

    /**
     * 行业编码集合
     */
    private List<String> industryCodeList;

    /**
     * 属地标记位（1：表示属地运营研判列表）
     */
    private String localFlag;

    /**
     * 事件描述
     */
    private String description;

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
