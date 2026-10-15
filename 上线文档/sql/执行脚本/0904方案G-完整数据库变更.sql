USE video_platform;

DROP PROCEDURE IF EXISTS `sp_add_sub_scenes_to_warning_order_months`;

DELIMITER $$

CREATE PROCEDURE `sp_add_sub_scenes_to_warning_order_months`()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE v_table VARCHAR(128);
    DECLARE v_sql TEXT;
    DECLARE v_cnt INT DEFAULT 0;
    DECLARE v_skip INT DEFAULT 0;

    DECLARE cur CURSOR FOR
        SELECT TABLE_NAME
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME REGEXP '^vm_warning_order_t_[0-9]{6}$'
        ORDER BY TABLE_NAME;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_table;
        IF done = 1 THEN
            LEAVE read_loop;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = v_table
              AND COLUMN_NAME = 'sub_scenes'
        ) THEN
            SET v_skip = v_skip + 1;
        ELSE
            SET v_sql = CONCAT(
                'ALTER TABLE `', v_table, '` ',
                'ADD COLUMN `sub_scenes` JSON NULL ',
                'COMMENT ''实际触发的下游任务编码数组(JSON)'' ',
                'AFTER `extra_field`'
            );
            SET @dyn_sql = v_sql;
            PREPARE stmt FROM @dyn_sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
            SET v_cnt = v_cnt + 1;
        END IF;
    END LOOP;
    CLOSE cur;

    SELECT CONCAT('added=', v_cnt, ', skipped=', v_skip) AS sp_add_sub_scenes_result;
END$$

DELIMITER ;

CALL `sp_add_sub_scenes_to_warning_order_months`();
DROP PROCEDURE IF EXISTS `sp_add_sub_scenes_to_warning_order_months`;

ALTER TABLE `vm_warning_order_collection_t`
    ADD COLUMN `sub_scenes` JSON NULL
    COMMENT '实际触发的下游任务编码数组(JSON)'
    AFTER `warning_type_id`;

USE video_platform_statistic;

CREATE TABLE IF NOT EXISTS `vm_statistics_event_sub_scene` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` VARCHAR(32) DEFAULT NULL COMMENT '租户id',
  `province_id` VARCHAR(16) DEFAULT NULL COMMENT '省份编码',
  `city_id` VARCHAR(16) DEFAULT NULL COMMENT '市编码',
  `county_id` VARCHAR(32) DEFAULT NULL COMMENT '区县编码',
  `town_id` VARCHAR(100) DEFAULT NULL COMMENT '乡镇编码',
  `warning_source` VARCHAR(10) DEFAULT NULL COMMENT '告警来源',
  `device_code` VARCHAR(64) DEFAULT NULL COMMENT '设备编码',
  `sub_scene_code` VARCHAR(64) NOT NULL COMMENT '下游任务/兜底类型编码',
  `sub_scene_name` VARCHAR(50) DEFAULT NULL COMMENT '下游任务名称',
  `warning_type_id` VARCHAR(64) DEFAULT NULL COMMENT '告警类型/场景编码（与下游组合过滤）',
  `warning_num` INT DEFAULT NULL COMMENT '告警数量',
  `judge_num` INT DEFAULT NULL COMMENT '研判数量',
  `dispatch_num` INT DEFAULT NULL COMMENT '派单数量',
  `grab_num` INT DEFAULT NULL COMMENT '待领取数量',
  `handle_num` INT DEFAULT NULL COMMENT '处置数量',
  `approve_num` INT DEFAULT NULL COMMENT '审核数量',
  `order_complete_num` INT DEFAULT NULL COMMENT '办结数量',
  `acceptance_num` INT DEFAULT NULL COMMENT '验收数量',
  `judge_real_num` INT DEFAULT NULL COMMENT '研判真警数量',
  `dispatch_real_num` INT DEFAULT NULL COMMENT '派单真警数量',
  `handle_real_num` INT DEFAULT NULL COMMENT '处置真警数量',
  `approve_real_num` INT DEFAULT NULL COMMENT '审核真警数量',
  `order_complete_real_num` INT DEFAULT NULL COMMENT '办结真警数量',
  `acceptance_real_num` INT DEFAULT NULL COMMENT '验收真警数量',
  `real_num` INT DEFAULT NULL COMMENT '真警数量',
  `false_num` INT DEFAULT NULL COMMENT '假警数量',
  `ai_num` INT DEFAULT NULL COMMENT 'ai数量',
  `year` VARCHAR(5) DEFAULT NULL COMMENT '年',
  `month` VARCHAR(5) DEFAULT NULL COMMENT '月',
  `day` VARCHAR(5) DEFAULT NULL COMMENT '日',
  `update_date` DATETIME DEFAULT NULL COMMENT '更新时间',
  `create_date` DATETIME DEFAULT NULL COMMENT '创建时间',
  `industry_code` VARCHAR(50) DEFAULT NULL COMMENT '行业编码',
  `app_ver_code` VARCHAR(50) DEFAULT NULL COMMENT '行业版本',
  `repeat_complete_num` INT DEFAULT NULL COMMENT '重复告警',
  `today_complete` INT DEFAULT NULL COMMENT '今日已办结数量',
  `grid_id` VARCHAR(64) DEFAULT NULL COMMENT '网格ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_vses_device_code` (`device_code`) USING BTREE,
  KEY `idx_vses_province_id` (`province_id`),
  KEY `idx_vses_city_id` (`city_id`),
  KEY `idx_vses_county_id` (`county_id`),
  KEY `idx_vses_town_id` (`town_id`),
  KEY `idx_vses_create_date` (`create_date`),
  KEY `idx_vses_tenant_id` (`tenant_id`),
  KEY `idx_vses_grid_id` (`grid_id`),
  KEY `idx_vses_sub_scene_ymd` (`tenant_id`, `sub_scene_code`, `year`, `month`, `day`),
  KEY `idx_vses_type_sub_ymd` (`tenant_id`, `warning_type_id`, `sub_scene_code`, `year`, `month`, `day`),
  KEY `idx_vses_ymd` (`year`, `month`, `day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC
  COMMENT='客户下游任务维度告警统计汇总（方案A，按sub_scenes拆行，含warning_type_id）';
