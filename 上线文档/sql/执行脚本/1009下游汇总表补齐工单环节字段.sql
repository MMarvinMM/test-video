-- 库：video_platform_statistic（以实际统计库为准）
-- 目的：vm_statistics_event_sub_scene 补齐与 num_day 一致的工单环节字段，
--       使 SUB_TASK 维度 orderStatusList 可区分 2/4/5/9 等状态。
-- 执行后需重跑下游日汇总 Job（eventSubSceneForTenantHandler）回刷数据。

ALTER TABLE `vm_statistics_event_sub_scene`
  ADD COLUMN `dispatch_num` INT DEFAULT NULL COMMENT '派单数量(order_status=2)' AFTER `judge_num`,
  ADD COLUMN `grab_num` INT DEFAULT NULL COMMENT '待领取数量(order_status=3)' AFTER `dispatch_num`,
  ADD COLUMN `handle_num` INT DEFAULT NULL COMMENT '处置数量(order_status=4)' AFTER `grab_num`,
  ADD COLUMN `approve_num` INT DEFAULT NULL COMMENT '审核数量(order_status=5)' AFTER `handle_num`,
  ADD COLUMN `order_complete_num` INT DEFAULT NULL COMMENT '办结数量(order_status=6)' AFTER `approve_num`,
  ADD COLUMN `acceptance_num` INT DEFAULT NULL COMMENT '验收数量(enable_acceptance_mode=1)' AFTER `order_complete_num`,
  ADD COLUMN `judge_real_num` INT DEFAULT NULL COMMENT '研判真警数量' AFTER `acceptance_num`,
  ADD COLUMN `dispatch_real_num` INT DEFAULT NULL COMMENT '派单真警数量' AFTER `judge_real_num`,
  ADD COLUMN `handle_real_num` INT DEFAULT NULL COMMENT '处置真警数量' AFTER `dispatch_real_num`,
  ADD COLUMN `approve_real_num` INT DEFAULT NULL COMMENT '审核真警数量' AFTER `handle_real_num`,
  ADD COLUMN `order_complete_real_num` INT DEFAULT NULL COMMENT '办结真警数量' AFTER `approve_real_num`,
  ADD COLUMN `acceptance_real_num` INT DEFAULT NULL COMMENT '验收真警数量' AFTER `order_complete_real_num`;
