package com.chinatower.event.entity.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TradeAlarmOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/21 10:42
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class TradeAlarmOrderDTO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 事件标题
     * @required true
     */
    private String alarmBody;

    /**
     * 告警事件编码
     */
    private String warningOrderId;

    /**
     * 告警开始时间
     * @required true
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeStart;

    /**
     * 告警结束时间
     * @required true
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeEnd;

    /**
     * 告警来源
     *
     */
    private String warningSource;

    /**
     * 告警类型
     *
     */
    private String warningTypeId;

    /**
     * 告警状态
     *
     */
    private String orderStatus;

    /**
     * 关键字检索 告警编码/事件标题关键字
     *
     */
    private String keyword;

    /**
     * 经度
     *
     */
    private String longitude;

    /**
     * 维度
     *
     */
    private String latitude;

    /**
     * 是否收藏 1:收藏 0：未收藏
     *
     */
    private String isCollection;

    /**
     * 设备编码、通道编码集合
     *
     */
    private List<AlarmOrderByDeviceDTO> devList;

    /**
     * 设备编码集合
     *
     */
    private List<String> deviceCode;

    /**
     * 事件编号（集合,需要排除掉的事件）
     *
     */
    private List<Map<String, List<String>>> notInWarningOrderIdList;

    /**
     * 事件编码（集合，需要包含的事件）
     *
     */
    private List<Map<String, List<String>>> warningOrderIdList;

    /**
     * 页码
     * @required true
     */
    private Integer pageNum;

    /**
     * 每页条数
     * @required true
     */
    private Integer pageSize;

    /**
     * 省编码
     *
     */
    private String provinceId;

    /**
     * 市编码
     *
     */
    private String cityId;

    /**
     * 区县编码
     *
     */
    private String countyId;

    /**
     * 查询半径（公里）
     *
     */
    private String radius;

    /**
     * 查询类型:0-全部（默认）；1待办；2已办
     *
     */
    private String searchType;

    /**
     * 事件编码集合
     *
     */
    private List<String> warningOrderIds;

    /**
     * 告警来源集合
     *
     */
    private List<String> warningSourceList;

    /**
     * 告警状态集合
     * @required true
     */
    private List<String> orderStatusList;

    /**
     * 告警类型集合
     *
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务编码列表（实际触发，与 warningTypeIdList 为 AND）
     */
    private List<String> subSceneIdList;

    /**
     * 告警场景编码批量查询（多选）
     */
    private List<String> sceneCodeList;

    /**
     * AI风险等级批量查询（多选）
     */
    private List<String> riskLevelList;

    /**
     * 网格员ID（提供给行业使用）
     *
     */
    private String gridUserId;

    /**
     * 需过滤的事件状态
     *
     */
    private List<String> notInOrderStatus;

    /**
     * 前端告警类型
     *
     */
    private String cameraAlarmType;

    /**
     * 是否二次研判 1-是；0-否
     *
     */
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     *
     */
    private String secondJudgeResult;

    /**
     * 告警重要等级 1:1级,2:2级
     *
     */
    private String importanceLevel;

    /**
     * 地址
     *
     */
    private String address;

    /**
     * 是否过滤数据权限（1：不过滤(提供给君南调用)）
     *
     */
    private String scopeFlag;

    /**
     *  告警标签id列表
     *
     */
    private List<String> lableIdList;

    /**
     * 预置位id
     *
     */
    private String presetId;

    /**
     * 网格id
     *
     */
    private String gridId;

    /**
     * 网格id批量
     *
     */
    private List<String> gridIdList;

    private String judgeResult;

    /**
     * 排序方式、排序字段
     * @required true
     */
    private String sortName;

    /**
     * 排序规则
     * @required true
     */
    private String sort;

    /**
     * 研判结果多选 0误报  1真警  2重复告警
     * */
    private List<String> judgeResultList;

}
