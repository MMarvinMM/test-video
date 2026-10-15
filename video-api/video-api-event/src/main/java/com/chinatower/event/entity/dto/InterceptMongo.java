package com.chinatower.event.entity.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class InterceptMongo extends BaseMongoDTO {

    @Field("emergency_level")
    private String emergencyLevel;
    @Field("file_img_url_src")
    private String imgUrl;
    @Field("confidence_level_min")
    private String confidenceLevelMin;
    @Field("alarm_time")
    private String alarmTime;
    @Field("ptz_p")
    private String ptzP;
    @Field("industry_code")
    private String industryCode;
    @Field("channel_code")
    private String channelCode;
    @Field("ptz_t")
    private String ptzT;
    @Field("province_code")
    private String provinceCode;
    @Field("grid_code")
    private String gridCode;
    @Field("algorithm_code")
    private String algorithmCode;
    @Field("ptz_z")
    private String ptzZ;
    @Field("app_ver_code")
    private String appVerCode;
    @Field("confidence_level_max")
    private String confidenceLevelMax;
    @Field("grid_id")
    private String gridId;
    @Field("flow_id")
    private String flowId;
    @Field("id")
    private String id;
    @Field("delete_flag")
    private String deleteFlag;
    @Field("extra_field")
    private String extraField;

    /**
     * 实际触发的下游子任务编码数组（与月表 sub_scenes 对应）
     */
    @Field("sub_scenes")
    private List<String> subScenes;

    @Field("msg_id")
    private String msgId;
    @Field("task_id")
    private String taskId;
    @Field("customer_code")
    private String customerCode;
    @Field("longitude")
    private String longitude;
    @Field("warning_source")
    private String warningSource;
    @Field("file_img_url_icon")
    private String fileImgUrlIcon;
    @Field("tenant_name")
    private String tenantName;
    @Field("alarm_body")
    private String alarmBody;
    @Field("create_time")
    private String createTime;
    @Field("algorithm_time")
    private String algorithmTime;
    @Field("enlarge_flag")
    private String enlargeFlag;
    @Field("grid_name")
    private String gridName;
    @Field("province_name")
    private String provinceName;
    @Field("town_code")
    private String townCode;
    @Field("hori_azimuth_angle")
    private String horiAzimuthAngle;
    @Field("intercept")
    private String intercept;
    @Field("device_code")
    private String deviceCode;
    @Field("msg_type")
    private String msgType;
    @Field("modifier_date")
    private String modifierDate;
    @Field("notice_resource")
    private String noticeResource;
    @Field("comp_confidence_level")
    private String compConfidenceLevel;
    @Field("tenant_id")
    private String tenantId;
    @Field("importance_level")
    private String importanceLevel;
    @Field("county_code")
    private String countyCode;
    @Field("latitude")
    private String latitude;
    @Field("confidence_level")
    private String confidenceLevel;
    @Field("warning_order_id")
    private String warningOrderId;
    @Field("city_code")
    private String cityCode;
    @Field("warning_type_id")
    private String warningTypeId;
    @Field("city_name")
    private String cityName;
    @Field("device_name")
    private String deviceName;
    @Field("video_url")
    private String videoUrl;
    @Field("town_name")
    private String townName;
    @Field("channel_name")
    private String channelName;
    @Field("address")
    private String address;
    @Field("county_name")
    private String countyName;
    @Field("alarm_code")
    private String alarmCode;
    @Field("preset_id")
    private String presetId;
    @Field("prevAlarmTime")
    private String prevAlarmTime;
    @Field("thresh")
    private String thresh;
    @Field("this_thresh")
    private String thisThresh;
    @Field("judge_result")
    private String judgeResult;
    @Field("is_second_judge")
    private String isSecondJudge;
    @Field("second_judge_result")
    private String secondJudgeResult;
    @Field("is_second_identify")
    private String isSecondIdentify;
    @Field("second_identify_result")
    private String secondIdentifyResult;
    @Field("linkage_algorithm")
    private String linkageAlgorithm;
    @Field("front_alarm_pic_url")
    private String frontAlarmPicUrl;
    @Field("front_alarm_vedio_url")
    private String frontAlarmVedioUrl;

    /**
     * 摄像机俯仰角
     */
    @Field("device_pitch")
    private String devicePitch;
    /**
     * 告警原因
     */
    @Field("alarm_reason")
    private String alarmReason;
    @Field("device_horizontal")
    private String deviceHorizontal;

    @Field("transparent_fields")
    private TransparentFieldsDto transparentFieldsDto;

    @Field("pitch_angle")
    private String pitchAngle;
    @Field("hori_field_angle")
    private String horiFieldAngle;
    @Field("vertical_field_angle")
    private String verticalFieldAngle;
    @Field("device_address")
    private String deviceAddress;
}
