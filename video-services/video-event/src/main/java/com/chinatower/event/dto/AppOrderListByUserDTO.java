package com.chinatower.event.dto;

import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.BaseAppOrderListByUserDTO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * APP获取当前登录人上报的事件列表入参
 */
@Data
public class AppOrderListByUserDTO extends BaseAppOrderListByUserDTO {

    private List<String> roleIdList;

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
    private List<String> orgIdList;

    /**
     * 设备厂家
     */
    private String modelId;

    /**
     * 设备厂家(多个)
     */
    private List<String> modelIds;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 告警类型批量查询
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务编码列表（实际触发，与 warningTypeIdList 为 AND）
     */
    private List<String> subSceneIdList;

    //告警类型标签id列表
    private List<String> tagIdList;

    /** 告警源 */
    private String warningSource;

    /** 客户端类型 pc app */
    private String clienttype;
    /** 需过滤的order_status */
    private List<String> notInOrderStatus;
    /** 是否是领导 */
    private boolean isLeader;
    /** APP关键字检索 */
    private String keyword;

    /**
     * 研判结果0：误判 1：真实 2：重复报警
     */
    private String judgeResult;

    /**
     * 是否收藏 1:收藏 0：未收藏
     */
    private String isCollection;
    /**
     * 收藏创建人
     */
    private String collectionCreator;

    /**
     * 来源：0铁塔 1租户
     */
    private String source;
    /**
     * 是否是导出excel
     */
    private boolean isExport = false;

    private String tableName;

    private Long[] roleIds;
    /**订单状态 */
    private List<String> warningOrderStatus;

    private String linkId;

    private String sqlParam;


    /**
     * 查询半径（公里）
     */
    private String radius;

    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String latitude;

    /**
     * 用户经度
     */
    private String userLongitude;
    /**
     * 用户维度
     */
    private String userLatitude;

    private String orderStatusSql;

    /**
     * 是否查询列表 空查询一条  1：查询多条
     */
    private String queryList;

    /**
     * AI前端告警 1后端 2前端
     */
    private String noticeResource;

    /**
     * 置信度
     */
    private String confidenceLevel;

    /**
     * 消息类型
     * 0-是；1-否；
     * 2-否；
     */
    private String msgType;

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
     * 告警状态批量查询
     */
    private List<String> orderStatusList;

    private List<String> warningSourceList;

    /**
     * 是否3D放大 1 是
     */
    private String enlargeFlag;

    /**
     * 环节配置和环节id
     */
    private String flowLinkIds;

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
     * 告警标签集合
     * @required false
     */
    private List<AlarmLableRelDTO> alarmLableRelDTOList;

    /**
     * 告警事件编码集合
     * @required false
     */
    private List<String> warningOrderIds;

    /**
     * 受理状态
     */
    private String enableAcceptanceMode;

    /**
     * 排序方式（1：事发点距离 2：告警时间）
     */
    private String sortFlag;

    /**
     * 告警原因批量查询
     */
    private List<String> alarmReasonList;

    /**
     * 网格id多选
     * */
    private List<String> gridIdList;
}
