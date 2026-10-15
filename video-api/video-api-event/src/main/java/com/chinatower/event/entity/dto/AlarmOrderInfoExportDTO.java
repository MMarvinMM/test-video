package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.event.entity.vo.IotDeviceInfoVO;
import com.chinatower.event.entity.vo.RelDeviceVo;
import com.chinatower.event.entity.vo.UavAlarmSourceVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**

 * 类名称: AlarmOrderInfoExportDTO.java
 *
 * @author cuisj
 * @date 2024/06/04 10:42
 */
@Data
public class AlarmOrderInfoExportDTO{

    private static final long serialVersionUID = 1L;

    /**
     * 事件标题
     */
    private String alarmBody;

    /**
     * 事件编号
     */
    private String warningOrderId;



    /**
     * 重要等级名称
     *
     */
    private String importanceLevelName;

    /**
     * 形成告警时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String alarmTime;

    /**
     * 告警来源名称
     *
     */
    private String warningSourceName;

    /**
     * 告警来源
     *
     */
    private String warningSource;

    private String realAlarmSource;

    /**
     * 告警类型名称
     */
    private String warningTypeName;

    /**
     * 告警原因
     */
    private String alarmReason;

    /**
     * 事发地点
     */
    private String address;

    /**
     * 经纬度
     */
    private String lotAndLat;


    /**
     * 事发地点到摄像机距离 单位: 米
     */
    private String incidentDistance;

    /**
     * 摄像机俯仰角
     */
    private String devicePitch;

    /**
     * 摄像机水平角
     *
     */
    private String deviceHorizontal;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     *网格员名称
     */
    private String keeperName;

    /**
     * polygonName:电子围栏  layerName:图层名称   tbbh:图层编码
     * @required true
     */
    private List<Map> polygonOperate;


    /**
     * 事件描述
     *
     */
    private String description;

    /**
     * AI算法配置参数 事件描述
     *
     */
    private String algorithmDescription;

    /**
     * 备注
     *
     */
    private String remark;

    /**
     * 联动摄像机集合
     *
     */
    private List<RelDeviceVo> relDeviceList;

    /**
     * 设备型号
     */
    private String deviceModelCode;
    /**
     * 摄像机编号
     */
    private String deviceCode;
    /**
     * 摄像机名称
     */
    private String deviceName;

    /**
     * 摄像机标签
     * */
    private List<String> deviceLabel;

    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 摄像机厂家名称
     */
    private String modelFactoryName;

    /**
     * 摄像机挂高，单位：米
     */
    private String height;

    /**
     * 摄像机经纬度
     */
    private String deviceLotAndLat;

    /**
     * 摄像机地址
     */
    private String deviceAddress;

    /**
     * 置信度
     */
    private String confidenceLevel;

    /** 水平方位角 */
    private String horiAzimuthAngle;

    /**
     * 预置位Id
     */
    private String presetId;

    /**
     * 预置位名称
     */
    private String presetName;

    /**
     * 是否二次研判(转换后值) 1-是；0-否
     */
    private String isSecondJudge;

    /**
     * 二次研判结果(转换后值) 1-真，0-假
     */
    private String secondJudgeResult;

    /**
     * 是否二次识别(转换后值) 1-是；0-否
     * @required false
     */
    private String isSecondIdentify;

    /**
     * 二次识别结果(转换后值) 1-真，0-假，2-算法未分析
     * @required false
     */
    private String secondIdentifyResult;

    /**
     * 联动算法名称
     */
    private String linkageAlgorithm;

    /**
     * 下垫面类型
     */
    private String underlyingSurfaceType;

    /**
     * 火区面积
     */
    private String fireArea;
    /**
     * 前类型
     */
    private String preType;
    /**
     * 后类型
     */
    private String postType;

    /**
     * 图斑编号
     */
    private String spotNo;

    private String provinceName;
    /**
     * 城市名
     */
    private String cityName;
    /**
     * 区县名
     */
    private String countyName;
    /**
     * 乡镇名
     */
    private String townName;
    /**
     * 村名
     */
    private String villageName;
    /**
     * 卫片来源
     */
    private String regionSource;
    /**
     * 前时相
     */
    private String preChronology;
    /**
     * 后时相
     */
    private String postChronology;
    /**
     * 图版面积
     */
    private String spotArea;
    /**
     * 图版周长
     */
    private String spotPerimeter;
    /**
     * 变化类型
     */
    private String changeType;

    /**
     * 面积
     */
    private String area;
    /**
     * 土地利用类型
     */
    private String landUseType;
    /**
     * 影像时间
     */
    private String imageTime;
    /**
     * 是否质检
     */
    private String detectionFlag;

    /**
     * 图层id
     */
    private String layerId;

    /**
     * 图斑唯一标识
     */
    private String bsm;

    /**
     * 告警人员
     */
    private String findName;

    /**
     * 联系方式
     */
    private String findMobile;

    /**
     * 告警图片
     * */
    private List<Map> alarmPicList;

    /**
     * 客户id(铁塔视角/属地)
     */
    private String tenantId;

    /**
     * 客户名称(铁塔视角/属地)
     */
    private String tenantName;

    /**
     * 行业应用名称(铁塔视角/属地)
     */
    private String industryName;

    /**
     * 行业应用版本(铁塔视角/属地)
     */
    private String appVerCode;

    /** 受理节点(属地) */
    private String acceptNode;
    /** 受理节点名称(属地) */
    private String acceptNodeName;

    private String warningTypeId;

    private String industryCode;

    private String extraField;

    /** 告警压制原因(压制) */
    private String interceptName;

    /**
     * 删除人
     * */
    private String deleteUser;

    /**
     * 删除说明
     * */
    private String deleteRemark;

    /**
     * 修改时间
     * */
    private String modifierDate;

    /**
     * 属地流转信息
     * */
    private List<Map> flowLinkList;

    /**
     * 多组物联设备信息以及关联的摄像机信息
     */
    private List<IotDeviceInfoVO> iotDevInfoList;

    /**
     * 无人机告警来源
     */
    private UavAlarmSourceVo uavDevInfoList;
}
