package com.chinatower.sync.consumer.item;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class UavNestChannelReport implements Serializable {

    /**
     *   `device_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联设备ID',
     *   `device_code` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设备编码',
     *   `channel_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '通道编号',
     *   `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '通道名称',
     *   `channel_type` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '通道子类型',
     *   `status` varchar(10) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态 0在线，1离线，2 从未连接',
     *   `channel_type_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '朝内朝外name',
     *   `visual_range` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '可视范围（单位：公里）',
     *   `horizontal_radiation_angle` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '水平辐射角度',
     *   `vertical_radiation_angle` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '俯仰辐射角度',
     *   `pixel` int DEFAULT NULL COMMENT '摄像机像素',
     *   `access_proctrol` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '接入协议',
     *   `national_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '国标编号',
     *   `del_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志, 0：正常，2：删除',
     *   `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
     *   `create_time` datetime NOT NULL COMMENT '创建时间',
     *   `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
     *   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
     *   `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
     *   `max_guard` int DEFAULT NULL COMMENT '可设置最大看守位数量',
     *   `max_preset` int DEFAULT NULL COMMENT '可设置最大预置点数量',
     *   `max_patrol` int DEFAULT NULL COMMENT '可设置最大巡航路径数量',
     *   `stream_type` int DEFAULT NULL COMMENT '码流类型',
     *   `height` decimal(10,0) DEFAULT NULL COMMENT '海拔高度',
     *   `facade` int DEFAULT NULL COMMENT '外形（1-球机；2-半球；3-枪机）',
     *   `busin_status` int DEFAULT NULL COMMENT '业务状态   0-启用；1-停用',
     *   `device_code_nvr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联nvr设备的code',
     *   `monitor_discription` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '监测因子中文描述',
     *   `monitor_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '监测因子名称',
     *   `monitor_status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '有效状态：0-无效；1-有效',
     *   `monitor_time` datetime DEFAULT NULL COMMENT '监测时间',
     */

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "通道编码")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "通道状态")
    private String status;

    @ApiModelProperty(value = "删除标志, 0：正常，2：删除")
    private String delFlag;

    private String channelType;

    private String channelTypeName;

    private String visualRange;

    private String horizontalRadiationAngle;

    private String verticalRadiationAngle;

    private Integer pixel;

    private String accessProctrol;

    private String nationalCode;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String remark;

    private Integer maxGuard;

    private Integer maxPreset;

    private Integer maxPatrol;

    private Integer streamType;

    private String height;

    private Integer facade;

    private Integer businStatus;

    private String deviceCodeNvr;

    private String monitorDiscription;

    private String monitorName;

    private String monitorStatus;

    private Date monitorTime;

    //canal类型
    private String canalType;
    // 执行时间
    private long ts;
}
