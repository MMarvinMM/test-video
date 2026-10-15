package com.chinatower.event.dto;

import java.util.List;
import java.util.Map;

import com.chinatower.common.core.dto.BaseOrderListDTO;
import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import lombok.Data;

/**
 * 查询告警列表参数
 */
@Data
public class OrderListDTO extends BaseOrderListDTO {
    /**
     * 查询半径（公里）
     */
    private String radius;

    /**
     * 告警来源批量查询
     */
    private List<String> warningSourceList;

    /**
     * 告警状态批量查询
     */
    private List<String> orderStatusList;

    /**
     * 告警类型批量查询
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务编码列表（实际触发，与 warningTypeIdList 为 AND）
     */
    private List<String> subSceneIdList;

    /**
     * 工单月表名列表（领导批示待办计数 EXISTS 关联过滤时由服务端填充）
     */
    private List<String> warningOrderTableList;

    //告警类型标签id列表
    private List<String> tagIdList;
    /**
     * 前端告警类型
     */
    private String cameraAlarmType;

    private List<AlarmOrderByDeviceDTO> devList;

    /**
     * 组织ID
     */
    private List<String> orgIdList;

    /** 需过滤的order_status */
    private List<String> notInOrderStatus;

    /**
     * 研判结果0：误判 1：真实 2：重复报警
     */
    private String judgeResult;

    /**
     * 行业ID
     */
    private String tradeId;

    /**
     * 行业应用版本编码
     */
    private String appVerCode;

    /**
     * 是否收藏 1:收藏 0：未收藏
     */
    private String isCollection;

    /**
     * 事件编码（集合，需要包含的事件）
     */
    private List<Map<String, List<String>>> warningOrderIdList;

    private List<String> notInWarningOrderIds;
    private List<String> warningOrderIds;
    /**
     * 是否二次研判 1-是；0-否
     */
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     */
    private String secondJudgeResult;

    /**
     * 网格员ID
     * 行业使用
     */
    private String gridUserId;

    /**
     * 处理状态：0-待审批；1-同意；2-不同意
     */
    private String status;

    /**
     * 领导批示已办状态 1,2：已办  0待办
     */
    private List<String> leaderStatus;

    /**
     *  卫星区域集合
     */
    private List<SysDeptDeviceVO> satelliteList;

    /**
     *  摄像机、雷达、物联设备编码集合
     */
    private List<String> deviceCodeList;

    /**
     *  用户id集合
     */
    private List<String> userIdList;

    /**
     * 排序字段
     */
    private String sortName;
    /**
     * 排序规则
     */
    private String sort;

    /**
     * 领导ID
     */
    private String leaderId;

    /**
     *  临时权限切面
     */
    private String dataScopeTemp;

    private List<String> roleIdList;

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
     * 详细地址
     */
    private String address;

    /**
     *  告警标签id列表
     */
    private List<String> lableIdList;

    /**
     * 预置位id
     */
    private String presetId;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 是否3D放大 1 是
     */
    private String enlargeFlag;

    private List<String> presetIdList;

    /**
     * 告警原因列表
     */
    private List<String> alarmReasonList;

    /**
     * 研判结果多选   0误报  1真警  2重复告警
     * */
    private List<String> judgeResultList;

    /**
     * 网格id多选
     * */
    private List<String> gridIdList;

    private String flightRouteId;

    private String taskRecordId;

    /**
     * 关键字检索（事件编码/告警标题/设备名称）
     */
    private String keywords;
}
