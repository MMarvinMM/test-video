package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/4 14:37
 */
@Data
public class AirEditDto extends PageDTO {
    /**
     * 航线表自增id
     */
    private String id;
    /**
     * 航线业务id
     */
    private String flightRouteId;
    /**
     * 航线名称
     */
    private String flightRouteName;
    /**
     * 航线类型 航点航线 正射影像 倾斜摄影 环绕飞行
     */
    private Integer type;
    /**
     * 设备编码 优先机巢
     */
    private String deviceCode;
    /**
     * 无人机编码
     */
    private String uavCode;
    /**
     * 修改日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String modifiedTime;
    /**
     * 下发算法编码集合
     */
    private List<String> warningTypeId;
    /**
     * 下发算法编码集合string
     */
    private String warningTypeIds;
    /**
     * 航线文件大json
     */
    private String kmzJson;
    /**
     * kmz文件路径
     */
    private String kmzUrl;
    /**
     * 无人机名称
     */
    private String uavName;
    /**
     * 航线文件名称
     */
    private String fileName;
    /**
     * 航线文件相对路径
     */
    private String storagePath;
    /**
     * 航线文件可访问路径
     */
    private String fileAccessPath;
    /**
     * 航线区域信息(废弃)
     */
    private String areaInfos;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建用户
     */
    private String createUser;
    private Date updateTime;
    private String updateUser;
    @NotBlank(message = "设备接入节点不能为空")
    private String accessNode;
    private String acceptNode;
    /**
     * 算法下发特殊字段
     */
    private String extraField;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createBeginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createEndTime;
    /**
     * 设备编码集合string  筛选
     */
    private String deviceCodes;
    /**
     * 航线类型集合string  筛选
     */
    private String types;
    /**
     * 机巢编码
     */
    private String uavNestCode;
    /**
     * 是否禁飞
     */
    private String uavAllowed;
    /**
     * 是否编辑航线
     */
    private String isEdit;
    /**
     * 航线备份版本id
     */
    private String snapshotId;
    // 操作类型
    private String optType;
    // 操作类型
    private String algorithmType;
    /**
     * 开启状态（0：开启  1：关闭）
     */
    private String status;
    /**
     * 航线大类 0正常航线 1指点飞行 2一键起飞
     */
    private String flightMainType;
    /**
     * 航线文件uos信息 解析需要使用
     */
    private String uosInfo;
    /**
     * 喇叭配置
     */
    private String hornInfo;
    /**
     * 无人机编码集合
     */
    private List<String> uavCodes;
    private String base64;
    /**
     * 任务开始时间
     */
    private String taskStartTime;
    /**
     * 复飞任务 源id
     */
    private String originalTaskRecordIds;
    /**
     *
     */
    private String taskRecordId;
    /**
     * 复飞任务 图片/视频源id
     */
    private String originalId;
    /**
     * 复飞任务 图片/视频源id
     */
    private String photoId;
    /**
     * 复飞任务的计划id
     */
    private String taskRecordName;
    /**
     * 算法下发电子围栏
     */
    private String algorithmDto;
    /**
     * 算法下发电子围栏，高级算法配置信息集合
     */
    private List<AlgorithmDto> algorithmDtos;
    /**
     * 无人机照相机编码
     */
    private String carmeCode;
    /**
     * 是否
     */
    private String isMerge;

    /**
     * 是否开启扩展水印（0-否、1-是）
     */
    private String isExpandWatermark;

    /**
     * 飞行里程
     */
    private String airMileage;

    /**
     * 航点维度自动二次识别任务
     */
    private String pointAlarmCodes;

    /**
     * 算法下发电子围栏，高级算法配置信息集合
     */
    private List<PointAlarmCodes> pointAlarmCodeLists;

    /**
     * 起飞机巢
     */
    private String startNestCode;

    /**
     * 降落机巢
     */
    private String endNestCode;

    /**
     * 是否蛙跳
     */
    private Integer frogLeap;

    /**
     * 无人机型号  1.8.10 改造
     */
    private String uavModelCode;

    /**
     * 起飞机巢 底层字段
     */
    private String takeOffDeviceCode;

    /**
     * 降落机巢 底层字段
     */
    private String landDeviceCode;

    /**
     * 是否蛙跳 底层字段
     */
    private Integer multiDock;

    @Data
    public static class PointAlarmCodes {
        /**
         * 算法编码集合
         */
        private List<String> algorithm;
        /**
         * 经度
         */
        private String longitude;
        /**
         * 纬度
         */
        private String latitude;
        /**
         * 算法编码,分割
         */
        private String algorithmCodeStr;
        /**
         * create
         */
        private String createUser;
    }
}
