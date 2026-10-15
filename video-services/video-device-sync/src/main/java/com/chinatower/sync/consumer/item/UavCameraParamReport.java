package com.chinatower.sync.consumer.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UavCameraParamReport extends CommonYgReport {
    /**
     *   `id` bigint NOT NULL AUTO_INCREMENT,
     *   `device_uav_camera_dict_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相机型号code，device_uav_camera_dict_data表dict_code',
     *   `lens_name` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '镜头名称',
     *   `spectral_type` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '光谱类型',
     *   `sensor_size` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '传感器尺寸(mm)',
     *   `pixel_size` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '像元大小(mm)',
     *   `focal_length_type` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '焦距类型',
     *   `zoom_conversion_coefficient` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '变焦转化系数',
     *   `actual_focal_length_range` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '实际焦距范围(mm)',
     *   `equivalent_focal_length_range` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '等效焦距范围(mm)',
     *   `optical_zoom_range` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '光学变焦范围',
     *   `digital_zoom_range` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '数码变焦范围',
     *   `color_palette_style` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '调色盘样式',
     *   `supporting_feature` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支持功能',
     */

    private Long id;

    private String deviceUavCameraDictCode;

    private String deviceUavCameraDictDataCode;

    private String lensName;

    private String spectralType;

    private String sensorSize;

    private String pixelSize;

    private String focalLengthType;

    private String zoomConversionCoefficient;

    private String actualFocalLengthRange;

    private String equivalentFocalLengthRange;

    private String opticalZoomRange;

    private String digitalZoomRange;

    private String colorPaletteStyle;

    private String supportingFeature;
}
