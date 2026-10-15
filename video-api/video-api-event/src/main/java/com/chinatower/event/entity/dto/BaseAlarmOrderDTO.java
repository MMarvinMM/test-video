package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BaseModel;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: BaseAlarmOrderDTO.java
 *
 * @author lyy
 * @version v1.5.75
 * @date 2023/03/29 11:22
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class BaseAlarmOrderDTO extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 事件标题
     * @required false
     */
    private String alarmBody;

    /**
     * 设备编码
     * @required false
     */
    private List<String> deviceCode;

    /**
     * 事件等级
     * @required false
     */
    private String emergencyLevel;

    /**
     * 工单编码
     * @required true
     */
    private String orderId;

    /**
     * 工单状态
     * @required false
     */
    private String orderStatus;

    /**
     * 告警结束时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeEnd;

    /**
     * 告警开始时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeStart;

    /**
     * 市编码
     * @required false
     */
    private String cityId;

    /**
     * 县编码
     * @required false
     */
    private String countyId;

    /**
     * 省编码
     * @required false
     */
    private String provinceId;

    /**
     * 告警来源
     * @required false
     */
    private String warningSource;

    /**
     * 告警类型
     * @required false
     */
    private String warningTypeId;

    /**
     * 待办已办
     * @required false
     */
    private String searchType;

    /**
     * 超期  1已超期 2未超期
     * @required false
     */
    private String ifTimeOut;

    /**
     * 客户端类型 pc app
     * @required false
     */
    private String clienttype;

    /**
     * 用户ID
     * @required false
     */
    private String userId;

    /**
     * 需过滤的order_status
     * @required false
     */
    private List<String> notInOrderStatus;

    /**
     * 是否是管理员
     * @required false
     */
    private boolean isAdmin;

    /**
     * 是否是领导
     * @required false
     */
    private Boolean isLeader;

    /**
     * APP关键字检索
     * @required false
     */
    private String keyword;

    /**
     * 关键字检索（事件编码/告警标题/设备名称）
     */
    private String keywords;

    /**
     * 告警重要等级 1:1级,2:2级
     * @required false
     */
    private String importanceLevel;

    /**
     * 租户ID，铁塔视角用
     * @required true
     */
    private String tenantId;

    /**
     * 当前用户组织ID与上级所有组织ID
     * @required false
     */
    private List<String> orgIds;

    /**
     * 研判结果0：误判 1：真实 2：重复报警
     * @required false
     */
    private String judgeResult;

    /**
     * 组织ID
     * @required false
     */
    private String orgId;

    /**
     * 组织ID集合
     * @required false
     */
    private List<String> orgIdList;

    /**
     * 是否收藏 1:收藏 0：未收藏
     * @required false
     */
    private String isCollection;

    /**
     * 设备标签
     * @required false
     */
    private String deviceLabelId;

    /**
     * 摄像机标签
     * @required false
     */
    private List<String> deviceLabelIdList;

    /**
     * 收藏创建人
     * @required false
     */
    private String collectionCreator;

    /**
     * 来源：0铁塔 1租户
     * @required false
     */
    private String source;

    /**
     * 是否是导出excel
     * @required false
     */
    private boolean isExport = false;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 环节表名
     * @required false
     */
    private String flowTableName;

    /**
     * 角色ID集合
     * @required false
     */
    private Long[] roleIds;

    /**
     * 角色集合
     * @required false
     */
    private List<String> roleIdList;

    /**
     * 订单状态
     * @required false
     */
    private List<String> warningOrderStatus;

    /**
     * 处理环节ID
     * @required false
     */
    private String linkId;

    /**
     * 1：删除告警 2：转移告警
     * @required false
     */
    private Integer deleteFlag;

    /**
     * 拼接sql
     * @required false
     */
    private String sqlParam;

    /**
     * 设备列表
     * @required false
     */
    private List<AlarmOrderByDeviceDTO> devList;

    /**
     * 查询半径（公里）
     * @required false
     */
    private String radius;

    /**
     * 经度
     * @required false
     */
    private String longitude;

    /**
     * 纬度
     * @required false
     */
    private String latitude;

    /**
     * 行业编码
     * @required false
     */
    private String tradeId;

    /**
     * 行业应用版本编码
     * @required false
     */
    private String appVerCode;

    /**
     * 是否行业调用 1：是
     * @required false
     */
    private String isTrade;

    /**
     * 封装用户待办SQL
     * @required false
     */
    private String orderStatusSql;

    /**
     * 是否查询列表 空查询一条  1：查询多条
     * @required false
     */
    private String queryList;

    /**
     * 设备厂家
     * @required false
     */
    private String modelCode;

    /**
     * 网格员ID（行业使用）
     * @required false
     */
    private String gridUserId;

    /**
     * AI前端告警 1后端 2前端
     * @required false
     */
    private String noticeResource;

    /**
     * 摄像机厂家
     * @required false
     */
    private String modelId;

    /**
     * 摄像机厂家集合
     * @required false
     */
    private List<String> modelIds;

    /**
     * 置信度
     * @required false
     */
    private String confidenceLevel;

    /**
     * 消息类型
     * @required false
     */

    private String msgType;

    /**
     * 网格id
     */
    private String gridId;



    /**
     * 网格id批量
     *
     */
    private List<String> gridIdList;

    /**
     * 事件编号(集合,需要排除掉的事件)
     * @required false
     */
    private List<Map<String, List<String>>> notInWarningOrderIdList;

    /**
     * 事件编码（集合，需要包含的事件）
     * @required false
     */
    private List<Map<String, List<String>>> warningOrderIdList;

    /**
     * 事件编码（集合，不需要包含的事件）
     * @required false
     */
    private List<String> notInWarningOrderIds;

    /**
     * 告警事件编码集合
     * @required false
     */
    private List<String> warningOrderIds;

    /**
     * 告警来源批量查询
     * @required false
     */
    private List<String> warningSourceList;

    /**
     * 告警状态批量查询
     * @required false
     */
    private List<String> orderStatusList;

    /**
     * 告警类型批量查询
     * @required false
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务编码批量查询（实际触发，与 warningTypeIdList 为 AND）
     * @required false
     */
    private List<String> subSceneIdList;

    /**
     * 告警场景编码批量查询（多选）
     * @required false
     */
    private List<String> sceneCodeList;

    /**
     * AI风险等级批量查询（多选，如 1/2/3 或 高/中/低）
     * @required false
     */
    private List<String> riskLevelList;

    /**
     * 告警类型标签id列表
     * @required false
     */
    private List<String> tagIdList;

    /**
     * 告警标签集合
     * @required false
     */
    private List<AlarmLableRelDTO> alarmLableRelDTOList;

    /**
     * 前端告警类型
     * @required false
     */
    private String cameraAlarmType;

    /**
     * 是否二次研判 1-是；0-否
     * @required false
     */
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     * @required false
     */
    private String secondJudgeResult;

    /**
     * 是否二次识别 1-是；0-否
     * @required false
     */
    private String isSecondIdentify;

    /**
     * 二次识别结果 1-真，0-假，2-算法未分析
     * @required false
     */
    private String secondIdentifyResult;

    /**
     * 物联设备编码/名称
     */
    private List<String> iotDeviceCode;

    /**
     * 设备名称
     * @required false
     */
    private String deviceName;

    /**
     * 关键字检索：APP物联设备编号/名称
     */
    private String iotDeviceCodeOrName;

    /**
     * 是否3D放大 1：是
     * @required false
     */
    private String enlargeFlag;

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
     * 环节配置和环节id
     * @required false
     */
    private String flowLinkIds;

    /**
     *  卫星区域集合
     *  @required false
     */
    private List<SysDeptDeviceVO> satelliteList;

    /**
     *  雷达区域集合
     *  @required false
     */
    private List<SysDeptDeviceVO> radarList;

    /**
     *  摄像机、雷达、物联设备编码集合
     *  @required false
     */
    private List<String> deviceCodeList;

    /**
     * 组织下的用户集合
     */
    private String orgUserListString;

    /**
     *  用户id集合
     *  @required false
     */
    private List<String> userIdList;

    /**
     *  地址
     *  @required false
     */
    private String address;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     * @required false
     */
    private String realLinkType;

    /**
     *  置信度最小值
     *  @required false
     */
    private String confidenceLevelMin;
    /**
     *  置信度最大值
     *  @required false
     */
    private String confidenceLevelMax;

    /**
     *  置信度最小值符号
     *  @required false
     */
    private String minChar;

    /**
     *  置信度最大值符号
     *  @required false
     */
    private String maxChar;

    /**
     *  告警标签id列表
     *  @required false
     */
    private List<String> lableIdList;

    /**
     * 告警原因列表
     */
    private List<String> alarmReasonList;

    /**
     * 是否受理（1：待受理）
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 研判结果多选   0误报  1真警  2重复告警
     * */
    private List<String> judgeResultList;

    /**
     * 航线ID
     */
    private String flightRouteId;

    /**
     * 飞行任务ID
     */
    private String taskRecordId;


    public String getQueryValue() {
        StringBuilder sb = new StringBuilder();
        String dev = "";
        if (!CollectionUtils.isEmpty(deviceCode)) {
            dev = StringUtils.join(deviceCode, ",");
        }
        String end = "";
        if (null != alarmTimeEnd) {
            end = DateUtils.dateToString(alarmTimeEnd);
        }
        String start = "";
        if (null != alarmTimeStart) {
            start = DateUtils.dateToString(alarmTimeStart);
        }
        sb.append(alarmBody).append(dev).append(iotDeviceCode).append(iotDeviceCodeOrName).append(emergencyLevel).append(orderId).append(tenantId).append(end)
                .append(start).append(cityId).append(countyId).append(provinceId).append(warningSource).append(keyword).append(keywords)
                .append(warningTypeId).append(searchType).append(orderStatus).append(ifTimeOut).append(clienttype)
                .append(importanceLevel).append(judgeResult).append(orgId).append(isCollection).append(deviceLabelId)
                .append(tradeId).append(appVerCode).append(warningSourceList).append(orderStatusList).append(warningTypeIdList)
                .append(subSceneIdList).append(sceneCodeList).append(riskLevelList)
                .append(warningOrderStatus).append(radius).append(longitude).append(latitude).append(gridUserId).append(modelId)
                .append(confidenceLevel).append(msgType).append(notInWarningOrderIds).append(warningOrderIds).append(orgIdList).append(devList).append(orgUserListString)
                .append(tagIdList).append(deviceLabelIdList).append(cameraAlarmType).append(isSecondJudge).append(secondJudgeResult).append(isSecondIdentify)
                .append(secondIdentifyResult).append(enlargeFlag).append(sortName).append(sort).append(flowLinkIds).append(satelliteList).append(userIdList)
                .append(address).append(confidenceLevelMin).append(confidenceLevelMax).append(minChar).append(maxChar).append(lableIdList).append(alarmReasonList)
                .append(deleteFlag).append(judgeResultList).append(gridId).append(gridIdList).append(flightRouteId).append(taskRecordId);
        return sb.toString();
    }
}
