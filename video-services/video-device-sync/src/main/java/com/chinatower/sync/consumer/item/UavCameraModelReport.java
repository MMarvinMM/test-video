package com.chinatower.sync.consumer.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UavCameraModelReport extends CommonYgReport {
    /**
     *   `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
     *   `dict_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相机型号id',
     *   `dict_label` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相机型号',
     *   `manufacturer` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生产厂家',
     *   `manufacturer_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '厂商code',
     *   `domain` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `type` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `sub_type` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `range_of_pan` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '云台俯仰角范围',
     *   `range_of_yaw` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '云台偏航角范围',
     *   `support_function` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支持功能',
     *   `create_time` datetime DEFAULT NULL,
     *   `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `update_time` datetime DEFAULT NULL,
     *   `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `del_flag` int DEFAULT '0' COMMENT '删除状态，0：未删除，1：已删除',
     */

    private Long id;

    private String dictCode;

    private String dictLabel;

    private String manufacturer;

    private String manufacturerCode;

    private String domain;

    private String type;

    private String subType;

    private String rangeOfPan;

    private String rangeOfYaw;

    private String supportFunction;

    private List<UavCameraParamReport> deviceUavCameraLensDictDataList;
}
