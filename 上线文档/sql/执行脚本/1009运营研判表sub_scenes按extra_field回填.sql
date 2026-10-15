-- ============================================================
-- 业务库 video_platform
-- 运营研判表 sub_scenes 历史回填（补齐月表回填覆盖不到的单）
--
-- 背景：
--   1009 月表→operate 回填依赖月表非空 sub_scenes；
--   串行假警等可能无月表行，导致 operate.sub_scenes 一直为空，
--   算法统计回退场景 UUID 后被剔纯场景。
--
-- 本脚本：
--   1）再跑一遍「月表 → operate」（可重复执行）
--   2）对仍为空的 operate，从 extra_field(CVLM-ALARM-INFO) 按
--      SubScenesExtractUtil 口径提取实际触发下游编码写入
--
-- 前置：vm_alarm_operate_t.sub_scenes 列已存在（先执行
--       1009运营研判表增加sub_scenes.sql 的 ALTER）
-- 执行后：重跑 XXL Job eventJudgeAlarmHandler，入参如 2026-10-08
-- ============================================================

USE video_platform;

-- ------------------------------------------------------------
-- 0）预览：将从 extra_field 回填的样本（执行 UPDATE 前先看）
-- ------------------------------------------------------------
SELECT o.warning_order_id,
       o.operate_type,
       o.judge_result,
       o.algorithm_code,
       DATE(o.alarm_time) AS alarm_day,
       CAST(o.sub_scenes AS CHAR) AS op_ss_before,
       x.extracted AS op_ss_after
FROM vm_alarm_operate_t o
INNER JOIN (
    SELECT o2.warning_order_id,
           CAST(CONCAT(
               '[',
               GROUP_CONCAT(DISTINCT CONCAT('"', jt.alarm_type, '"')
                            ORDER BY jt.alarm_type SEPARATOR ','),
               ']'
           ) AS JSON) AS extracted
    FROM vm_alarm_operate_t o2
    INNER JOIN JSON_TABLE(
        o2.extra_field,
        '$[*].attrList[*]' COLUMNS (
            attr_type VARCHAR(64) PATH '$.attrType',
            attr_value LONGTEXT PATH '$.attrValue'
        )
    ) a ON a.attr_type = 'CVLM-ALARM-INFO'
    INNER JOIN JSON_TABLE(
        CAST(a.attr_value AS JSON),
        '$[*]' COLUMNS (
            alarm_type VARCHAR(64) PATH '$.alarmType',
            NESTED PATH '$.alarmAreaList[*].alarmArea[*].alarmPointList[*]' COLUMNS (
                point_id INT PATH '$.alarmPointId'
            )
        )
    ) jt ON TRUE
    WHERE o2.delete_flag = '0'
      AND o2.extra_field IS NOT NULL
      AND JSON_TYPE(o2.extra_field) = 'ARRAY'
      AND (o2.sub_scenes IS NULL
           OR JSON_TYPE(o2.sub_scenes) = 'NULL'
           OR JSON_LENGTH(o2.sub_scenes) = 0)
      AND jt.alarm_type IS NOT NULL
      AND jt.alarm_type <> ''
      AND jt.point_id IS NOT NULL
    GROUP BY o2.warning_order_id
) x ON x.warning_order_id = o.warning_order_id
ORDER BY o.alarm_time DESC
LIMIT 200;

-- ------------------------------------------------------------
-- 1）月表 → operate（与 1009 脚本同口径，可重复执行）
-- ------------------------------------------------------------
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

    SELECT CONCAT('backfilled_from_months=', v_total) AS sp_result;
END$$

DELIMITER ;

CALL `sp_backfill_operate_sub_scenes_from_months`();
DROP PROCEDURE IF EXISTS `sp_backfill_operate_sub_scenes_from_months`;

-- ------------------------------------------------------------
-- 2）extra_field → operate（覆盖假警/无月表行等）
--    口径：CVLM-ALARM-INFO 子告警存在非空 alarmPointList → 取 alarmType
-- ------------------------------------------------------------
UPDATE vm_alarm_operate_t o
INNER JOIN (
    SELECT o2.warning_order_id,
           CAST(CONCAT(
               '[',
               GROUP_CONCAT(DISTINCT CONCAT('"', jt.alarm_type, '"')
                            ORDER BY jt.alarm_type SEPARATOR ','),
               ']'
           ) AS JSON) AS extracted
    FROM vm_alarm_operate_t o2
    INNER JOIN JSON_TABLE(
        o2.extra_field,
        '$[*].attrList[*]' COLUMNS (
            attr_type VARCHAR(64) PATH '$.attrType',
            attr_value LONGTEXT PATH '$.attrValue'
        )
    ) a ON a.attr_type = 'CVLM-ALARM-INFO'
    INNER JOIN JSON_TABLE(
        CAST(a.attr_value AS JSON),
        '$[*]' COLUMNS (
            alarm_type VARCHAR(64) PATH '$.alarmType',
            NESTED PATH '$.alarmAreaList[*].alarmArea[*].alarmPointList[*]' COLUMNS (
                point_id INT PATH '$.alarmPointId'
            )
        )
    ) jt ON TRUE
    WHERE o2.delete_flag = '0'
      AND o2.extra_field IS NOT NULL
      AND JSON_TYPE(o2.extra_field) = 'ARRAY'
      AND (o2.sub_scenes IS NULL
           OR JSON_TYPE(o2.sub_scenes) = 'NULL'
           OR JSON_LENGTH(o2.sub_scenes) = 0)
      AND jt.alarm_type IS NOT NULL
      AND jt.alarm_type <> ''
      AND jt.point_id IS NOT NULL
    GROUP BY o2.warning_order_id
) x ON x.warning_order_id = o.warning_order_id
SET o.sub_scenes = x.extracted
WHERE o.sub_scenes IS NULL
   OR JSON_TYPE(o.sub_scenes) = 'NULL'
   OR JSON_LENGTH(o.sub_scenes) = 0;

SELECT ROW_COUNT() AS backfilled_from_extra_field;

-- ------------------------------------------------------------
-- 3）抽检（含此前问题单）
-- ------------------------------------------------------------
SELECT warning_order_id,
       operate_type,
       judge_result,
       CAST(sub_scenes AS CHAR) AS sub_scenes,
       algorithm_code,
       alarm_time
FROM vm_alarm_operate_t
WHERE warning_order_id IN (
    '202610081200469903837223',
    '202610081200469903837224',
    '202610081200469903837225',
    '202610081200469903837308',
    '202610081200469903836357'
)
ORDER BY warning_order_id;

-- 预期：
--   …837223 → ["20212128"]
--   …837225 / …837308 → ["20212102","20313100"]（若 extra 可解析）
--   …837224 未研判且无触发点时可能仍为空（属正常）
--
-- 执行后重跑：
--   XXL Job: eventJudgeAlarmHandler
--   入参:    2026-10-08
--   （多日逗号分隔，如 2026-10-08,2026-10-09）
