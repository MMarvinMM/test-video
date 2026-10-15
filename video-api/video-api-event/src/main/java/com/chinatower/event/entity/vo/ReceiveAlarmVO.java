package com.chinatower.event.entity.vo;

import com.chinatower.common.core.vo.BaseReceiveAlarmVO;
import com.chinatower.event.entity.dto.OrderFileDO;
import com.chinatower.event.entity.dto.ReceiveIotAlarmConfigDto;
import com.chinatower.event.entity.dto.TransparentFieldsDto;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: hued
 * @Date: 2021年11月3日11:24:33
 * @Description: 告警接收请求实体
 */
@Data
public class ReceiveAlarmVO extends BaseReceiveAlarmVO {
    /**
     * 任务id
     */
//    private String taskId;
    /**
     * 报警区域
     */
    private List<AlarmOriginalAreaVO> alarmArea;
    /**
     * 告警区域列表
     */
    private List<AlarmOriginalAreaListVO> alarmAreaList;

    /**
     * 图片集合-视频图片必传一个
     *
     * @required true
     */
    private List<AlarmImgVO> imgs = new ArrayList<>();

    /**
     * 视频集合-视频图片必传一个
     *
     * @required true
     */
    private List<AlarmVideoVO> videos;

    /**
     * 带水印附件
     */
    private List<OrderFileDO> orderFileList;

    /**
     * 扩展字段属性
     */
    private Object extendField;
    /**
     * 前端告警识别结果扩展字段
     */
    private Object frontExtendField;
    /**
     * 算法仓特征点 p值
     */
    private String featurePan;
    /**
     * 算法仓特征点 t值
     */
    private String featureTilt;

    /**
     * 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     */
    private String notifyAdminFlag;
    /**
     * 行业信息id
     */
    private String industryCode;

    /**
     * 行业应用版本编码
     */
    private String appVerCode;

    /**
     * 物联设备告警上报时，物联因子挂载节点
     */
    private List<WarningSensorMonitorVO> sensorMonitorVOList;
    /**
     * 透传字段物联设备告警上报所有因子值
     */
    private List<WarningAllSensorMonitorVO> allSensorMonitorVOList;

    /**
     * 透传字段中的物联设备信息集合
     */
    private List<IotDeviceExtraFieldVO> iotDeviceExtraFieldVOList;
    /**
     * 并行研判告警类型字符串
     */
    private String parallelAlarmTypeStr;
    /**
     * 串行研判告警类型字符串
     */
    private String serialAlarmTypeStr;
    /**
     * 运营研判模式
     */
    private String operationType;
    /**
     * 已配置运营研判的所有告警来源  -1代表全部
     */
    private String operationAlarmSource;
    /**
     * 所匹配告警压制策略的重复告警压制时间
     */
    private Integer orderTimeLimit;
    // 操作之前的linkId
    private String beforLink;
    /**
     * 定位点信息json格式
     */
    private String points;
    /**
     * 流程环节id
     */
    private String flowAttrId;


    /**
     * 研判节点
     */
    private String operationNode;

    /**
     * 告警节点
     */
    private String alarmNode;

    /**
     * 研判组织
     */
    private String operationDept;

    /**
     * 研判节点名称
     */
    private String operationNodeName;

    /**
     * 并行研判节点
     */
    private String parallelOperationNode;

    /**
     * 并行研判组织
     */
    private String parallelOperationDept;

    //是否二次识别 1-是；0-否
    private String isSecondIdentify;
    /**
     * 二次识别结果 1-真，0-假，2-算法未分析
     */
    private String secondIdentifyResult;

    private String linkageAlgorithm;
    //接入节点
    private String accessNode;

    //受理节点
    private String acceptNode;

    //当前节点
    private String localNode;

    /**
     * 卫片标记位（1：来自卫片）
     */
    private String regionAlarmFlag;

    /**
     * 流程环节配置属性
     */
    private VmFlowLinkAttrDto vmFlowLinkAttrDto;

    /**
     * 告警匹配的物联配置
     */
    private ReceiveIotAlarmConfigDto iotAlarmConfigDto;


    /**
     * 联动摄像机
     */
    private String linkageCamera;

    /**
     * 联动动作 0:抓图 1:抓视频
     */
    private String linkageAction;

    /**
     * 选择算法
     */
    private String selectAlgorithm;

    /**
     * 算法仓联动算法
     */
    private String linkageAlgorithmCode;

    /**
     * 设备厂家
     */
    private String modelCode;

    /**
     * 通道属性
     */
    private String attribute;

    /**
     * 通用新增储存备用的透传字段
     */
    private String transparentFields;

    /**
     * 通用新增储存备用的透传字段对象
     */
    private TransparentFieldsDto transparentFieldsDto;

    /**
     * 0:是 1：否
     */
    private String isPresetMakeAlarm;

    /**
     * 航线ID
     */
    private String flightRouteId;

    /**
     * 飞行任务ID
     */
    private String taskRecordId;

    /**
     * 双通道抓图
     */
    private String assistPicUrl;

    /**
     * 任务记录id
     */
    private Long taskExecutionId;

    /**
     * 记录是否处理过大模型报文标识, true:处理过; 其他情况未处理
     */
    private Boolean dutyLargeModelFlag;

}
