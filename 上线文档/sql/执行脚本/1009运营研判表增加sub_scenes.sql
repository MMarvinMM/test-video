-- ============================================================
-- 业务库 video_platform
-- 运营研判表增加 sub_scenes（与月表语义一致）
--
-- 用途：算法研判统计按下游子场景拆行时，优先读运营表，不依赖月表 JOIN
-- 说明：重复执行 ADD COLUMN 会报错，按环境是否已有列决定是否执行
-- ============================================================

USE video_platform;

ALTER TABLE `vm_alarm_operate_t`
    ADD COLUMN `sub_scenes` JSON NULL
    COMMENT '实际触发的下游任务编码数组(JSON)'
    AFTER `extra_field`;

-- 历史回填：从全部月表同步非空 sub_scenes 到运营表
DROP PROCEDURE IF EXISTS `sp_backfill_operate_sub_scenes_from_months`;

DELIMITER $$

CREATE PROCEDURE `sp_backfill_operate_sub_scenes_from_months`()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE v_table VARCHAR(128);
    DECLARE v_sql TEXT;
    DECLARE v_rows INT DEFAULT 0;
    DECLARE v_total INT DEFAULT 0;

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
            SET v_sql = CONCAT(
                'UPDATE vm_alarm_operate_t o ',
                'INNER JOIN `', v_table, '` w ON o.warning_order_id = w.warning_order_id ',
                'SET o.sub_scenes = w.sub_scenes ',
                'WHERE (o.sub_scenes IS NULL OR JSON_TYPE(o.sub_scenes)=''NULL'' OR JSON_LENGTH(o.sub_scenes)=0) ',
                'AND w.sub_scenes IS NOT NULL ',
                'AND JSON_TYPE(w.sub_scenes)!=''NULL'' ',
                'AND JSON_LENGTH(w.sub_scenes)>0'
            );
            SET @dyn_sql = v_sql;
            PREPARE stmt FROM @dyn_sql;
            EXECUTE stmt;
            SET v_rows = ROW_COUNT();
            DEALLOCATE PREPARE stmt;
            SET v_total = v_total + IFNULL(v_rows, 0);
        END IF;
    END LOOP;
    CLOSE cur;

    SELECT CONCAT('backfilled_rows=', v_total) AS sp_backfill_operate_sub_scenes_result;
END$$

DELIMITER ;

CALL `sp_backfill_operate_sub_scenes_from_months`();
DROP PROCEDURE IF EXISTS `sp_backfill_operate_sub_scenes_from_months`;
