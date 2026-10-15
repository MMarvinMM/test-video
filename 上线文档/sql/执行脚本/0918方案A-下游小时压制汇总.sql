-- 0918 方案A 扩展：下游小时/压制汇总表（库 video_platform_statistic）
-- 与类型小时/压制表并行，互不改写

CREATE TABLE IF NOT EXISTS `vm_statistics_event_hour_sub_scene` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户id',
  `province_id` VARCHAR(16) DEFAULT NULL COMMENT '省份编码',
  `city_id` VARCHAR(16) DEFAULT NULL COMMENT '市编码',
  `county_id` VARCHAR(32) DEFAULT NULL COMMENT '区县编码',
  `warning_source` VARCHAR(10) DEFAULT NULL COMMENT '告警来源',
  `device_code` VARCHAR(64) DEFAULT NULL COMMENT '设备编码',
  `sub_scene_code` VARCHAR(64) NOT NULL COMMENT '下游任务编码',
  `sub_scene_name` VARCHAR(50) DEFAULT NULL COMMENT '下游任务名称',
  `warning_hour` INT DEFAULT NULL COMMENT '告警小时 0-23',
  `warning_num` INT DEFAULT NULL COMMENT '告警数量',
  `real_num` INT DEFAULT NULL COMMENT '真警数量',
  `false_num` INT DEFAULT NULL COMMENT '假警数量',
  `repeat_complete_num` INT DEFAULT NULL COMMENT '重复告警',
  `year` VARCHAR(5) DEFAULT NULL COMMENT '年',
  `month` VARCHAR(5) DEFAULT NULL COMMENT '月',
  `day` VARCHAR(5) DEFAULT NULL COMMENT '日',
  `update_date` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_date` DATETIME DEFAULT NULL COMMENT '创建时间',
  `industry_code` VARCHAR(50) DEFAULT NULL COMMENT '行业编码',
  `app_ver_code` VARCHAR(50) DEFAULT NULL COMMENT '行业版本',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_vsehs_create_date` (`create_date`),
  KEY `idx_vsehs_tenant_id` (`tenant_id`),
  KEY `idx_vsehs_sub_hour` (`tenant_id`, `sub_scene_code`, `warning_hour`, `year`, `month`, `day`),
  KEY `idx_vsehs_ymd` (`year`, `month`, `day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC
  COMMENT='客户下游任务×小时告警统计汇总';

CREATE TABLE IF NOT EXISTS `vm_statistics_intercept_event_sub_scene` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户id',
  `province_id` VARCHAR(16) DEFAULT NULL COMMENT '省份编码',
  `city_id` VARCHAR(16) DEFAULT NULL COMMENT '市编码',
  `county_id` VARCHAR(32) DEFAULT NULL COMMENT '区县编码',
  `province_name` VARCHAR(64) DEFAULT NULL,
  `city_name` VARCHAR(64) DEFAULT NULL,
  `county_name` VARCHAR(64) DEFAULT NULL,
  `warning_source` VARCHAR(10) DEFAULT NULL COMMENT '告警来源',
  `device_code` VARCHAR(64) DEFAULT NULL COMMENT '设备编码',
  `sub_scene_code` VARCHAR(64) NOT NULL COMMENT '下游任务编码',
  `sub_scene_name` VARCHAR(50) DEFAULT NULL COMMENT '下游任务名称',
  `today_num` INT DEFAULT NULL COMMENT '压制数量',
  `year` VARCHAR(5) DEFAULT NULL COMMENT '年',
  `month` VARCHAR(5) DEFAULT NULL COMMENT '月',
  `day` VARCHAR(5) DEFAULT NULL COMMENT '日',
  `update_date` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_date` DATETIME DEFAULT NULL COMMENT '创建时间',
  `industry_code` VARCHAR(50) DEFAULT NULL COMMENT '行业编码',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_vsies_create_date` (`create_date`),
  KEY `idx_vsies_tenant_id` (`tenant_id`),
  KEY `idx_vsies_sub_ymd` (`tenant_id`, `sub_scene_code`, `year`, `month`, `day`),
  KEY `idx_vsies_ymd` (`year`, `month`, `day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC
  COMMENT='客户下游任务×日压制统计汇总';

CREATE TABLE IF NOT EXISTS `vm_statistics_event_hour_intercept_sub_scene` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户id',
  `province_id` VARCHAR(16) DEFAULT NULL COMMENT '省份编码',
  `city_id` VARCHAR(16) DEFAULT NULL COMMENT '市编码',
  `county_id` VARCHAR(32) DEFAULT NULL COMMENT '区县编码',
  `warning_source` VARCHAR(10) DEFAULT NULL COMMENT '告警来源',
  `device_code` VARCHAR(64) DEFAULT NULL COMMENT '设备编码',
  `sub_scene_code` VARCHAR(64) NOT NULL COMMENT '下游任务编码',
  `sub_scene_name` VARCHAR(50) DEFAULT NULL COMMENT '下游任务名称',
  `warning_hour` INT DEFAULT NULL COMMENT '告警小时 0-23',
  `warning_num` INT DEFAULT NULL COMMENT '压制数量',
  `year` VARCHAR(5) DEFAULT NULL COMMENT '年',
  `month` VARCHAR(5) DEFAULT NULL COMMENT '月',
  `day` VARCHAR(5) DEFAULT NULL COMMENT '日',
  `update_date` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_date` DATETIME DEFAULT NULL COMMENT '创建时间',
  `industry_code` VARCHAR(50) DEFAULT NULL COMMENT '行业编码',
  `app_ver_code` VARCHAR(50) DEFAULT NULL COMMENT '行业版本',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_vsehis_create_date` (`create_date`),
  KEY `idx_vsehis_tenant_id` (`tenant_id`),
  KEY `idx_vsehis_sub_hour` (`tenant_id`, `sub_scene_code`, `warning_hour`, `year`, `month`, `day`),
  KEY `idx_vsehis_ymd` (`year`, `month`, `day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC
  COMMENT='客户下游任务×小时压制统计汇总';
