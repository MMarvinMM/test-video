package com.chinatower.event.entity.dto;

import java.io.Serializable;
import java.util.List;

import com.chinatower.common.core.dto.BasePageDTO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: LeaderAuidtListDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/26 15:45
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class LeaderAuidtListDTO extends BasePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 领导ID
     */
    private String leaderId;
    /**
     * 告警ID
     */
    private String orderId;
    /**
     * 告警标题
     */
    private String alarmBody;
    /**
     * 处理状态：0-待审批；1-同意；2-不同意
     */
    private String status;
    /**
     * 告警开始时间
     */
    private String alarmTimeStart;
    /**
     * 告警结束时间
     */
    private String alarmTimeEnd;
    /**
     * 告警类型
     */
    private String warningTypeId;
    /**
     * 省
     */
    private String provinceId;
    /**
     * 市
     */
    private String cityId;
    /**
     * 区县
     */
    private String countyId;
    /**
     * 是否是管理员
     */
    private boolean isAdmin;
    /**
     * 是否是导出excel
     */
    private boolean isExport = false;
    /**
     * 告警等级ID
     */
    private String emergencyLevel;
    /**
     * APP关键字检索
     */
    private String keyword;

    /**
     * 摄像机编号
     */
    private List<String> deviceCode;

    /**
     * 来源：0铁塔 1租户
     */
    private String source;

    /**
     * 领导批示已办状态 1,2：已办  0待办
     */
    private List<String> leaderStatus;

    /**
     * 告警类型
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务编码列表（实际触发，与 warningTypeIdList 为 AND）
     */
    private List<String> subSceneIdList;

    /**
     * 工单月表名列表（领导批示表无 sub_scenes，EXISTS 关联过滤时由服务端按告警时间填充）
     */
    private List<String> warningOrderTableList;

    /**
     * 摄像机厂家
     */
    private String modelId;
    /**
     * 摄像机厂家(多个)
     */
    private List<String> modelIds;
    /**
     * AI前端告警 1后端 2前端
     */
    private String noticeResource;

    /**
     * 置信度
     */
    private String confidenceLevel;

    /** 超期  1已超期 2未超期*/
    private String ifTimeOut;

    /** 告警源 */
    private String warningSource;
    /**
     * 研判结果0：误判 1：真实 2：重复报警
     */
    private String judgeResult;

    /**
     * 是否收藏 1:收藏 0：未收藏
     */
    private String isCollection;
    /**
     * 设备标签
     */
    private String deviceLabelId;
    /**
     * 摄像机标签标签
     */
    private List<String> deviceLabelIdList;

    //告警类型标签id列表
    private List<String> tagIdList;
    /**
     * 组织ID
     */
    private String orgId;
    /**
     * 组织ID
     */
    private List<String> orgIdList;

    private List<AlarmOrderByDeviceDTO> devList;

    private String importanceLevel;

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
     * @required false
     */
    private String isSecondIdentify;

    /**
     * 二次识别结果 1-真，0-假，2-算法未分析
     * @required false
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
     * 事件状态
     */
    private String orderStatus;

    /**
     * 是否3D放大 1 是
     */
    private String enlargeFlag;

    /**
     * 排序字段
     */
    private String sortName;
    /**
     * 排序规则
     */
    private String sort;


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
     * 组织下的用户集合
     */
    private String orgUserListString;

    /**
     *  临时权限切面
     */
    private String dataScopeTemp;

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
     *  告警id列表
     */
    private List<String> warningOrderIds;

    /**
     * 告警原因列表
     */
    private List<String> alarmReasonList;

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

    private String deviceName;

}
