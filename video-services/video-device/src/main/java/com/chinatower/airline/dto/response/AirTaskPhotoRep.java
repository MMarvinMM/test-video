package com.chinatower.airline.dto.response;

import com.chinatower.device.dto.OrderTypeDto;
import com.chinatower.event.constants.Constants;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author WLF
 * @description
 * @date 2024/11/5 17:31
 */
@Data
public class AirTaskPhotoRep {
    // 飞行任务id
    private String taskRecordId;

     //航线ID
    private String flightRouteId;

    //航线名称
    private String flightRouteName;

    // 照片id
    private String id;

    // 照片id
    private String photoId;

    // 照片名称
    private String name;

    // 图片别名
    private String originalFilename;

    // 原图路径
    private String photoUrl;

    // 原图访问地址
    private String photoAccessUrl;

    // 缩略图名称
    private String thumbnailName;

    // 缩略图路径
    private String thumbnailUrl;

    // 缩略图大图
    private String thumbnailBigUrl;

    // 架次id
    private String missionId;

    // 任务架次记录id
    private String missionRecordsId;

    // 纬度
    private Double latitude;

    // 经度
    private Double longitude;

    // 海拔
    private Double altitude;

    // 照片来源
    private Integer source;

    // 图片拍摄时间
    private String timeCreated;

    // 创建时间
    private String createTime;

    // 修改时间
    private String modifyTime;

    // 设备编码
    private String deviceCode;

    // 镜头类型
    private Integer lenType;

    /**
     *
     */
    private Integer eventStatus;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 任务名称
     */
    private String taskRecordName;

    /**
     * 无人机编码
     */
    private String uavCode;

    /**
     * 复飞的原始任务id
     */
    private String originalTaskRecordId;

    /**
     * 查询名称
     */
    private String queryName;

    /**
     * 收藏类型
     */
    private String colType;

    /**
     * 源id
     */
    private String originalId;

    /**
     * 无人机名称
     */
    private String uavName;

    /**
     * 拍摄时间
     */
    private String originalDate;

    /**
     * 重复飞行状态：0-未飞行，1-已飞行
     */
    private String reFlyStatus = Constants.ZERO;
    /**
     * 采集状态：0-未收藏，1-已收藏
     */
    private String collectStatus = Constants.ZERO;
    /**
     * 是否主任务：0-主任务，1-复飞任务
     */
    private String isMainTaskStatus = Constants.ZERO;

    /**
     * 海拔
     */
    private String height;

    /**
     * 告警信息
     */
    private List<OrderTypeDto> alarmCodes;

    /**
     * 已生成告警的编码集合
     */
    private Set<String> algorithmCodes;

    /**
     * 扩展水印备注
     */
    private String remark;

    /**
     * 扩展水印url
     */
    private String watermarkUrl;

    /**
     * 是否开启扩展水印标识 0开启 1关闭
     */
    private String isWatermark;

    /**
     * 飞机sn
     */
    private String snCode;

    /**
     * 机型
     */
    private String uavModelName;

    /**
     * 道路信息
     */
    private String roadInfo;

    /**
     * 起始机巢编号
     */
    private String startNestCode	;
    /**
     * 降落机巢编号
     */
    private String endNestCode	;
}
