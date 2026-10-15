package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BasePageDTO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningInterceptDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/27 09:43
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class WarningInterceptDTO extends BasePageDTO implements Serializable {

    /**
     * 告警开始时间
     */
    private String alarmTimeStart;
    /**
     * 告警结束时间
     */
    private String alarmTimeEnd;
    /**
     * 告警ID
     */
    private String orderId;
    /**
     * 告警标题
     */
    private String alarmBody;
    /**
     * 告警类型
     */
    private String warningTypeId;

    /**
     * 告警类型
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务编码列表（实际触发，与 warningTypeIdList 为 AND）
     */
    private List<String> subSceneIdList;

    /**
     * 重要等级
     */
    private String importanceLevel;
    /**
     * 设备编码
     */
    private List<String> deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 市编码
     */
    private String cityId;
    /**
     * 县编码
     */
    private String countyId;
    /**
     * 省编码
     */
    private String provinceId;
    /**
     * 租户ID，铁塔视角用
     */
    private String tenantId;
    /**
     * APP关键字
     */
    private String keyword;
    /**
     * 告警等级
     */
    private String emergencyLevel;
    /**
     * 扩展SQL
     */
    private String extraSql;
    /**
     * 是否是导出excel
     */
    private boolean isExport = false;

    /**
     * 来源：0铁塔 1租户
     */
    private String source;

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
     * 摄像机厂商ID(多个入参)
     */
    private List<String> modelIds;

    /**
     * 置信度
     */
    private String confidenceLevel;

    private List<String> deviceCodes;

    /**
     * 设备标签
     */
    private String deviceLabelId;

    /**
     * 摄像机标签标签
     */
    private List<String> deviceLabelIdList;

    /**
     * 组织ID
     */
    private String orgId;
    /**
     * 组织ID
     */
    private List<String> orgIdList;

    private List<AlarmOrderByDeviceDTO> devList;

    //告警类型标签id列表
    private List<String> tagIdList;

    /** 告警来源 */
    private String warningSource;
    /**
     * 告警来源批量查询
     */
    private List<String> warningSourceList;

    /**
     * 前端告警类型
     */
    private String cameraAlarmType;

    /**
     * 是否二次研判 1-是；0-否
     */
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     */
    private String secondJudgeResult;

    /**
     * 是否二次识别 1-是；0-否
     */
    private String isSecondIdentify;

    /**
     * 二次识别结果 1-真，0-假，2-算法未分析
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
     * 是否3D放大 1 是
     */
    private String enlargeFlag;

    /**
     *  压制原因 1.置信度压制 2.重复告警压制 3.告警时间压制  4.研判压制
     */
    private String intercept;

    /**
     * 行业ID
     */
    private String tradeId;

    /**
     * 是否行业调用 1：是
     */
    private String isTrade;

    /** userId */
    private String userId;

    /**
     *  卫星区域集合
     */
    private List<SysDeptDeviceVO> satelliteList;

    /**
     *  雷达区域集合
     */
    private List<SysDeptDeviceVO> radarList;

    /**
     *  摄像机、雷达、物联设备编码集合
     */
    private List<String> deviceCodeList;

    /**
     *  用户id集合
     */
    private List<String> userIdList;

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
     *  告警标签id列表
     */
    private List<String> lableIdList;

    /**
     * 告警原因列表
     */
    private List<String> alarmReasonList;

    /**
     * 告警工单ID集合
     */
    private List<String> warningOrderIds;

    private List<AreaCodeReq> areaCodeList;

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

    private List<String> interceptList;

    private List<String> algorithmCodeList;

    private String sort;

    private String sortName;

}
