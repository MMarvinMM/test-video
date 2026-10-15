package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 雷达数据对象
 * Created by luo.zhenghong on 2022/8/8.
 */
@Data
@ApiModel(value = "雷达数据对象",description = "雷达数据对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RadarReportDTO extends BaseMongoDTO {
    // 目标ID
    //private String id;
    // 短编号
    private Integer displayId;
    // 雷达id
    private Integer idR;
    // 船名
    private String vesselName ;
    // 船舶MMSI
    private Integer mmsi;
    // 呼号
    private String callSign;
    // IMO编号
    private Integer imo;
    // 纬度
    private Double latitude;
    // 经度
    private Double longitude ;
    // 目标航向
    private Float course;
    // 目标艏向
    private Float heading;
    // 目标航速
    private Float speed;
    // 目标长度
    private Integer len;
    // 目标宽度
    private Integer wid;
    // 最大长度
    private Integer maxLen;
    // 最小长度
    private Integer minLen;
    // 船舶类型
    private Integer shiptype;
    // 目标类型
    private String sClass;
    // 目标状态
    private String state;
    // 时间戳
    private Long lastTm;
    // 接收时间
    private Long receiveTime;
    // 数据状态
    private String status;
    // 省份
    private String province;
    // 数据质量
    private Integer quality;
    // 更新周期
    private Integer period;
    // AIS厂商
    private String vendorId;
    // 国籍
    private String nationality;
    // 雷达站新编号
    private String uid;
    // 数据批号
    private Long snapshotId;
    //持续时长
    private Long duration;
    private String displacement;

    private String fixed;

    private boolean hidden;

    private String idLink;

    private String registry;

    private String sost;

    private String targetId;

    private String uidR;

    private String mMmsi;

    private String flags;

    private String lastDt;

    private String processTime;

    private String rangeMaxDistance;

    private String shortLived;

    private String sources;

    private String staticState;

}
