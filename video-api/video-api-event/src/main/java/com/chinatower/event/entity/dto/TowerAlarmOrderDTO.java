package com.chinatower.event.entity.dto;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.BasePageDTO;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TowerAlarmOrderDTO.java
 *
 * @author lyy
 * @version v1.0.0
 * @date 2022/11/29 14:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class TowerAlarmOrderDTO extends BasePageDTO implements Serializable {

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
     * @required false
     */
    private String orderId;

    /**
     * 工单状态
     * @required false
     */
    private String orderStatus;

    /**
     * 告警结束时间
     * @required true
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeEnd;

    /**
     * 告警开始事件时间
     * @required true
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
     * 查询类型:0-全部；1待办；2已办
     * @required true
     */
    private String searchType;

    /**
     *  是否超期  1：已超期 2：未超期
     *  @required false
     */
    private String ifTimeOut;

    /**
     * 客户端类型 pc app
     * @required false
     */
    private String clienttype;

    /**
     * 用户ID
     * @required true
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
    private boolean isLeader;

    /**
     * APP关键字检索
     * @required false
     */
    private String keyword;

    /**
     * R-ZBZB-202112080822436 zzh 告警重要等级 1:1级,2:2级
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
     * 组织ID
     * @required false
     */
    private List<String> orgIdList;

    /**
     * 组织ID拼接字符串
     */
    private String orgIdStr;

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
     * 摄像机标签标签
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
     */
    private boolean isExport = false;

    private String tableName;

    /**
     * 环节表名
     */
    private String flowTableName;
    private Long[] roleIds;

    /**
     * 角色ID集合
     * @required false
     */
    private List<String> roleIdList;

    /**
     * 订单状态集合
     * @required false
     */
    private List<String> warningOrderStatus;

    /**
     * 环节ID
     * @required true
     */
    private String linkId;

//    private String deleteFlag;

    private String sqlParam;

    /**
     * 设备信息列表
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
     * AI前端告警 1后端 2前端
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
     * 0-是；1-否；
     * 2-否；
     */
    private String msgType;

    /**
     * 告警工单ID集合
     * @required false
     */
    private List<String> warningOrderIds;

    /**
     * 告警类型批量查询
     * @required false
     */
    private List<String> warningTypeIdList;

    /**
     * 告警场景编码批量查询（多选）
     * @required false
     */
    private List<String> sceneCodeList;

    /**
     * AI风险等级批量查询（多选）
     * @required false
     */
    private List<String> riskLevelList;

    /**
     * 告警类型标签id列表
     * @required false
     */
    private List<String> tagIdList;

    /**
     * 前端告警类型
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
     * 告警状态批量查询
     * @required false
     */
    private List<String> orderStatusList;

    /**
     * 告警来源集合
     * @required false
     */
    private List<String> warningSourceList;

    /**
     * 设备名称
     * @required false
     */
    private String deviceName;

    /**
     * 是否3D放大 1 是
     * @required false
     */
    private String enlargeFlag;

    /**
     * 铁塔管理区域查询数据
     * @required false
     */
    private List<AreaCodeReq> areaCodeList;

    /**
     * 排序方式
     * @required false
     */
    private String sort;

    private String extraField;

    /**
     * 算法编码
     * @required false
     */
    private String algorithmCode;

    /**
     * 综合置信度
     * @required false
     */
    private String compConfidenceLevel;

    /**
     * 排序字段
     * @required false
     */
    private String sortName;

    /**
     * 环节配置和环节id
     * @required false
     */
    private String flowLinkIds;

    /**
     * 真实环节ID
     * @required false
     */
    private String realLinkType;


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
        String areaCodeListStr = "";
        if (!CollectionUtils.isEmpty(areaCodeList)) {
            areaCodeListStr = JSON.toJSONString(areaCodeList);
        }
        sb.append(alarmBody).append(dev).append(emergencyLevel).append(orderId).append(tenantId).append(end)
                .append(start).append(cityId).append(countyId).append(provinceId).append(warningSource)
                .append(warningTypeId).append(searchType).append(orderStatus).append(ifTimeOut).append(clienttype)
                .append(importanceLevel).append(judgeResult).append(orgId).append(isCollection).append(deviceLabelId)
                .append(warningOrderStatus).append(radius).append(longitude).append(latitude).append(modelId).append(modelIds)
                .append(confidenceLevel).append(msgType).append(warningTypeIdList).append(sceneCodeList).append(riskLevelList).append(orgIdList)
                .append(tagIdList).append(deviceLabelIdList).append(cameraAlarmType).append(isSecondJudge).append(secondJudgeResult)
                .append(orderStatusList).append(warningSourceList).append(enlargeFlag).append(areaCodeListStr).append(sortName).append(sort)
                .append(flowLinkIds).append(orgIdStr);
        return sb.toString();
    }
}
