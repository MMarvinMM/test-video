package com.chinatower.sync.consumer.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UavMountModelReport extends CommonYgReport {
    /**
     *   `id` bigint NOT NULL AUTO_INCREMENT,
     *   `dict_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '挂载型号',
     *   `dict_label` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '无人机挂载名称',
     *   `manufacturer` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '厂商名称',
     *   `manufacturer_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '厂商编码',
     *   `mount_type` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '挂载类型，字典：uavDictMgr_mount_model_type\n',
     *   `support_function` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支持功能，字典：uavDictMgr_mount_model_support_function',
     *   `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `create_time` datetime DEFAULT NULL,
     *   `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `update_time` datetime DEFAULT NULL,
     *   `del_flag` tinyint DEFAULT '0' COMMENT '删除状态，0：未删除，1：已删除',
     */

    private Long id;

    private String dictCode;

    private String dictLabel;

    private String manufacturer;

    private String manufacturerCode;

    private String mountType;

    private String supportFunction;
}
