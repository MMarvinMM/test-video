-- 方案 A：下游日汇总增加 warning_type_id，支持「告警类型 ∧ 下游任务」组合查询
-- 库：video_platform_statistic（以实际环境为准）
-- 执行后须重跑 eventSubSceneForTenantHandler 回刷历史日数据

ALTER TABLE `vm_statistics_event_sub_scene`
  ADD COLUMN `warning_type_id` VARCHAR(64) DEFAULT NULL COMMENT '告警类型/场景编码（与下游组合过滤）'
  AFTER `sub_scene_name`;

ALTER TABLE `vm_statistics_event_sub_scene`
  ADD KEY `idx_vses_type_sub_ymd` (`tenant_id`, `warning_type_id`, `sub_scene_code`, `year`, `month`, `day`);
